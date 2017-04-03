package com.builtbroken.industry.content.machines.dynamic.modules.cores;

import com.builtbroken.industry.content.machines.dynamic.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.dynamic.gui.ContainerDynamicMachine;
import com.builtbroken.industry.content.machines.dynamic.gui.GuiDynamicMachine;
import com.builtbroken.industry.content.machines.dynamic.modules.MachineModule;
import com.builtbroken.industry.content.machines.dynamic.modules.controllers.ControlModule;
import com.builtbroken.industry.content.machines.dynamic.modules.inv.InventoryModule;
import com.builtbroken.industry.content.machines.dynamic.modules.items.ItemMachineTools;
import com.builtbroken.industry.content.machines.dynamic.modules.power.PowerModule;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Core of a machine handling all logic and functionality of the machine. This allows the machine to function without understanding itself or surroundings.
 * In other words the machine can exist as an entity, minecraft, or tile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/10/2016.
 */
public abstract class MachineCore extends MachineModule implements IGuiTile, IInventoryProvider, IPacketIDReceiver
{
    public static final int INVENTORY_SIZE = 5;
    public static final int MACHINE_TOOL_SLOT = 0;
    public static final int INPUT_INV_SLOT = 1;
    public static final int OUTPUT_INV_SLOT = 2;
    public static final int POWER_MOD_SLOT = 3;
    public static final int CONTROL_MOD_SLOT = 4;

    //TODO create inventory module prefab that doesn't use slots, allows for easy AE integration if slots are not used.
    /** Module that handles or is the inventory for inputting items. */
    protected InventoryModule inputInventory;

    /** Module that handles or is the inventory for outputting items. */
    protected InventoryModule outputInventory;

    /** Module that handles power for the machine */
    protected PowerModule powerModule; //TODO implement power module that returns speed & power

    /** Module that handles automation style controls */
    protected ControlModule controllerModule; //TODO implement basic controller with on/off, input controls, redstone, etc

    protected ExternalInventory _inventory;

    /** Hardcore setting, amount of dust in a machine, decreases effectiveness, builds over time when machine is used with minor build up when left alone */
    protected int dustBuildUpLevel;
    /** Hardcore setting, amount of slug/grease on a machine, decreases effectiveness, builds over time when machine is used */
    protected int greaseBuildUpLevel;
    /** Hardcore setting, amount of rust on a machine, decreases effectiveness, builds over time due to no use */
    protected int rustBuildUpLevel;

    /** Slow logic update cycle */
    protected int machineTicks = 0;
    /** Recipe processing timer */
    protected int processingTime = 0;

    /** Does the machine have an active recipe */
    protected boolean hasRecipe = false;
    /** Does the machine have items to process */
    protected boolean hasItems = false;
    /** Is the machine power on and functional */
    protected boolean isMachineOn = true;

    /**
     * Default constructor
     *
     * @param name
     */
    public MachineCore(ItemStack stack, String name)
    {
        super(stack, "core." + name);
    }

    @Override
    public void update()
    {
        if (getHost() != null && getHost().isServer())
        {
            if (hasPower() && inputInventory != null && outputInventory != null)
            {
                //Only check recipes every so often to avoid lag
                if (machineTicks++ >= 20)
                {
                    machineTicks = 0;
                    hasRecipe = hasRecipe();
                }

                if (hasRecipe && processingTime++ >= getRecipeProcessingTime())
                {
                    processingTime = 0;
                    IMachineRecipeHandler recipeHandler = getRecipeHandler();
                    if (recipeHandler != null)
                    {
                        Pair<ItemStack, Integer> slot = InventoryUtility.findFirstItemInInventory(inputInventory, 6, 1);
                        Object object = recipeHandler.getRecipe(new Object[]{slot.left()}, 0, 0);
                        if (object instanceof ItemStack && canOutput((ItemStack) object))
                        {
                            addToOutput((ItemStack) object);
                            inputInventory.decrStackSize(slot.right(), 1);
                        }
                    }
                }
                sendPacketToGui(0, processingTime);
            }
        }
    }

    /**
     * Called by inventory modules to tell the core something has changed
     *
     * @param module
     * @param slot
     * @param prev
     * @param item
     */
    protected void markInventoryUpdate(InventoryModule module, int slot, ItemStack prev, ItemStack item)
    {
        if (slot > 0 && module == inputInventory)
        {
            hasItems = true;
            //TODO correctly check if the inventory has items
        }
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        ItemStack stack;
        IModule module;

        if (!InventoryUtility.stacksMatch(prev, item) || item == null)
        {
            if (slot == INPUT_INV_SLOT)
            {
                if (item == null)
                {
                    inputInventory = null;
                }
                else
                {
                    stack = getInventory().getStackInSlot(INPUT_INV_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof InventoryModule)
                    {
                        inputInventory = (InventoryModule) module;
                        inputInventory.setHost(this);
                    }
                }
            }
            else if (slot == OUTPUT_INV_SLOT)
            {
                if (item == null)
                {
                    outputInventory = null;
                }
                else
                {
                    stack = getInventory().getStackInSlot(OUTPUT_INV_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof InventoryModule)
                    {
                        outputInventory = (InventoryModule) module;
                        outputInventory.setHost(this);
                    }
                }
            }
            else if (slot == POWER_MOD_SLOT)
            {
                if (item == null)
                {
                    powerModule = null;
                }
                else
                {
                    stack = getInventory().getStackInSlot(POWER_MOD_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof PowerModule)
                    {
                        powerModule = (PowerModule) module;
                        powerModule.setHost(this);
                    }
                }
            }
            else if (slot == CONTROL_MOD_SLOT)
            {
                if (item == null)
                {
                    controllerModule = null;
                }
                else
                {
                    stack = getInventory().getStackInSlot(CONTROL_MOD_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof ControlModule)
                    {
                        controllerModule = (ControlModule) module;
                        controllerModule.setHost(this);
                    }
                }
            }
        }
        if (getHost() != null)
        {
            getHost().onMachineChanged(true);
        }
    }

    /**
     * Called to check if the machine has a recipe
     *
     * @return
     */
    protected boolean hasRecipe()
    {
        IMachineRecipeHandler recipeHandler = getRecipeHandler();
        if (recipeHandler != null && inputInventory != null)
        {
            Pair<ItemStack, Integer> slot = InventoryUtility.findFirstItemInInventory(inputInventory, 6, 1);
            if (slot != null && slot.left() != null)
            {
                Object object = recipeHandler.getRecipe(new Object[]{slot.left()}, 0, 0);
                if (object instanceof ItemStack)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the handler for recipes
     *
     * @return recipe handler
     */
    protected abstract IMachineRecipeHandler getRecipeHandler();


    /**
     * Checks if the machine has enough power to do an
     * operation.
     *
     * @return true if machine is powered
     */
    protected boolean hasPower()
    {
        return true;///powerModule != null;
    }

    /**
     * Its there enough space to output the item
     * into the output inventory.
     *
     * @param stack - ItemStack to output
     * @return true if there is enough space
     */
    protected boolean canOutput(ItemStack stack)
    {
        if (outputInventory != null)
        {
            List<Integer> slots = outputInventory.getSlotsWithSpace();
            for (int slot : slots)
            {
                ItemStack slotStack = outputInventory.getStackInSlot(slot);
                if (slotStack == null || InventoryUtility.stacksMatch(slotStack, stack) && InventoryUtility.roomLeftInSlot(outputInventory, slot) >= stack.stackSize)
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected ItemStack addToOutput(ItemStack stack)
    {
        if (outputInventory != null && stack != null)
        {
            ItemStack copyStack = stack.copy();
            List<Integer> slots = outputInventory.getSlotsWithSpace();
            for (int slot : slots)
            {
                ItemStack slotStack = outputInventory.getStackInSlot(slot);
                if (slotStack == null)
                {
                    outputInventory.setInventorySlotContents(slot, copyStack);
                    return null;
                }
                else if (InventoryUtility.stacksMatch(slotStack, stack))
                {
                    int space = InventoryUtility.roomLeftInSlot(outputInventory, slot);
                    if (space >= copyStack.stackSize)
                    {
                        slotStack.stackSize += copyStack.stackSize;
                        outputInventory.setInventorySlotContents(slot, slotStack);
                        return null;
                    }
                    else
                    {
                        slotStack.stackSize += space;
                        copyStack.stackSize -= space;
                        outputInventory.setInventorySlotContents(slot, slotStack);
                    }
                }
            }
        }
        return stack;
    }

    @Override
    public void getContainedItems(final List<ItemStack> items)
    {
        super.getContainedItems(items);
        if (inputInventory != null)
        {
            inputInventory.getContainedItems(items);
        }
        if (outputInventory != null)
        {
            outputInventory.getContainedItems(items);
        }
        if (powerModule != null)
        {
            powerModule.getContainedItems(items);
        }
        if (controllerModule != null)
        {
            controllerModule.getContainedItems(items);
        }
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("inventory"))
        {
            getInventory().load(nbt.getCompoundTag("inventory"));
        }
        ItemStack stack = getInventory().getStackInSlot(INPUT_INV_SLOT);
        IModule module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof InventoryModule)
        {
            inputInventory = (InventoryModule) module;
            inputInventory.setHost(this);
        }

        stack = getInventory().getStackInSlot(OUTPUT_INV_SLOT);
        module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof InventoryModule)
        {
            outputInventory = (InventoryModule) module;
            outputInventory.setHost(this);
        }

        stack = getInventory().getStackInSlot(POWER_MOD_SLOT);
        module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof PowerModule)
        {
            powerModule = (PowerModule) module;
            powerModule.setHost(this);
        }

        stack = getInventory().getStackInSlot(CONTROL_MOD_SLOT);
        module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof ControlModule)
        {
            controllerModule = (ControlModule) module;
            controllerModule.setHost(this);
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        for (int i = 0; i < INVENTORY_SIZE; i++)
        {
            MachineModule module = getModuleForSlot(i);
            if (module != null)
            {
                getInventory().setInventorySlotContents(i, module.toStack());
            }
        }
        nbt.setTag("inventory", getInventory().save(new NBTTagCompound()));
        return nbt;
    }

    public MachineModule getModuleForSlot(int slot)
    {
        if (slot == INPUT_INV_SLOT)
        {
            return inputInventory;
        }
        else if (slot == OUTPUT_INV_SLOT)
        {
            return outputInventory;
        }
        else if (slot == POWER_MOD_SLOT)
        {
            return powerModule;
        }
        else if (slot == CONTROL_MOD_SLOT)
        {
            return controllerModule;
        }
        return null;
    }

    public InventoryModule getInputInventory()
    {
        return inputInventory;
    }

    public InventoryModule getOutputInventory()
    {
        return outputInventory;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDynamicMachine(getHost(), player, ID);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiDynamicMachine(getHost(), player, ID);
    }

    @Override
    public void onLeaveWorld()
    {
        super.onLeaveWorld();
        //TODO clean up module's caches
    }

    @Override
    public ExternalInventory getInventory()
    {
        if (_inventory == null)
        {
            _inventory = new ExternalInventory(this, INVENTORY_SIZE);
        }
        return _inventory;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == MACHINE_TOOL_SLOT && stack.getItem() instanceof ItemMachineTools; //TODO replace with interface check
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        //TODO add ability to remove broken tools
        return false;
    }

    /**
     * Amount of time currently working towards recipe
     * completion
     *
     * @return ticks
     */
    public int getProcessingTicks()
    {
        return processingTime;
    }

    /**
     * The expected time to complete the current recipe
     *
     * @return ticks
     */
    public int getRecipeProcessingTime()
    {
        return 20;
    }

    /**
     * Is the machine currently enabled
     *
     * @return
     */
    public boolean isMachineOn()
    {
        return isMachineOn;
    }

    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (getHost() != null)
        {
            if (getHost().isClient())
            {
                if (id == 0)
                {
                    processingTime = buf.readInt();
                    return true;
                }
            }
            else
            {

            }
        }
        return false;
    }

    public void sendPacket(Object... data)
    {
        if (getHost() != null)
        {
            getHost().sendPacket(new PacketTile(getHost(), 3, data));
        }
    }

    public void sendPacketToGui(Object... data)
    {
        if (getHost() != null)
        {
            getHost().sendPacketToGuiUsers(new PacketTile(getHost(), 3, data));
        }
    }
}
