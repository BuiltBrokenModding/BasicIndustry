package com.builtbroken.industry.content.machines.modular.cores;

import com.builtbroken.industry.content.machines.modular.MachineModuleBuilder;
import com.builtbroken.industry.content.machines.modular.cores.gui.ContainerDynamicMachine;
import com.builtbroken.industry.content.machines.modular.cores.gui.GuiDynamicMachine;
import com.builtbroken.industry.content.machines.modular.modules.MachineModule;
import com.builtbroken.industry.content.machines.modular.modules.controllers.ControlModule;
import com.builtbroken.industry.content.machines.modular.modules.inv.InventoryModule;
import com.builtbroken.industry.content.machines.modular.modules.power.PowerModule;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.api.tile.IInventoryProvider;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.List;

/**
 * Core of a machine handling all logic and functionality of the machine. This allows the machine to function without understanding itself or surroundings.
 * In other words the machine can exist as an entity, minecraft, or tile.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/10/2016.
 */
public abstract class MachineCore extends MachineModule implements IGuiTile, IInventoryProvider
{
    public final int INVENTORY_SIZE = 5;
    public final int GRINDER_SLOT = 0;
    public final int INPUT_INV_SLOT = 1;
    public final int OUTPUT_INV_SLOT = 2;
    public final int POWER_MOD_SLOT = 3;
    public final int CONTROL_MOD_SLOT = 4;

    //TODO create inventory module prefab that doesn't use slots, allows for easy AE integration if slots are not used.
    /** Module that handles or is the inventory for inputting items. */
    protected InventoryModule inputInventory;

    /** Module that handles or is the inventory for outputting items. */
    protected InventoryModule outputInventory;

    /** Module that handles power for the machine */
    protected PowerModule powerModule; //TODO implement power module that returns speed & power

    /** Module that handles automation style controls */
    protected ControlModule controllerModule; //TODO implement basic controller with on/off, input controls, redstone, etc

    protected ExternalInventory inventory;

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

    protected int processingCompleteTime = 20;

    /** Does the machine have an active recipe */
    protected boolean hasRecipe = false;
    /** Does the machine have items to process */
    protected boolean hasItems = false;
    /** Is the machine power on and functional */
    protected boolean isMachineOn = false;

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
        if (hasItems && isMachineOn && hasPower())
        {
            //Only check recipes every so often to avoid lag
            if (machineTicks++ >= 20)
            {
                machineTicks = 0;
                hasRecipe = hasRecipe();
            }

            if (hasRecipe && processingTime++ >= processingCompleteTime)
            {
                processingTime = 0;
                IMachineRecipeHandler recipeHandler = getRecipeHandler();
                if (recipeHandler != null)
                {
                    Pair<ItemStack, Integer> slot = InventoryUtility.findFirstItemInInventory(inputInventory, 6, 1);
                    Object object = recipeHandler.getRecipe(new Object[]{slot.left()}, 0, 0);
                    if (object instanceof ItemStack)
                    {

                    }
                }
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
                    stack = inventory.getStackInSlot(INPUT_INV_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof InventoryModule)
                    {
                        inputInventory = (InventoryModule) module;
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
                    stack = inventory.getStackInSlot(OUTPUT_INV_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof InventoryModule)
                    {
                        outputInventory = (InventoryModule) module;
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
                    stack = inventory.getStackInSlot(POWER_MOD_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof PowerModule)
                    {
                        powerModule = (PowerModule) module;
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
                    stack = inventory.getStackInSlot(CONTROL_MOD_SLOT);
                    module = MachineModuleBuilder.INSTANCE.build(stack);
                    if (module instanceof ControlModule)
                    {
                        controllerModule = (ControlModule) module;
                    }
                }
            }
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
        if (recipeHandler != null)
        {
            Pair<ItemStack, Integer> slot = InventoryUtility.findFirstItemInInventory(inputInventory, 6, 1);
            Object object = recipeHandler.getRecipe(new Object[]{slot.left()}, 0, 0);
            if (object instanceof ItemStack)
            {
                return true;
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
     * Does the input inventory contain the stack
     *
     * @param stack - item wer are searching for
     *              with stacksize.
     * @return true if the item is contained
     */
    protected boolean hasInputStack(ItemStack stack)
    {
        if (inputInventory != null)
        {
            Collection<ItemStack> stacks = inputInventory.getContainedItems();
            for (ItemStack slotStack : stacks)
            {
                if (InventoryUtility.stacksMatch(stack, slotStack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes items from the input inventory
     *
     * @param stack
     * @return true if all items were removed
     */
    protected boolean consumeInputStack(ItemStack stack)
    {
        if (inputInventory != null)
        {
            Collection<ItemStack> stacks = inputInventory.getContainedItems();
            for (ItemStack slotStack : stacks)
            {
                if (InventoryUtility.stacksMatch(stack, slotStack))
                {
                    return true;
                }
            }
        }
        return false;
    }


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
            inventory.load(nbt.getCompoundTag("inventory"));
        }
        ItemStack stack = inventory.getStackInSlot(INPUT_INV_SLOT);
        IModule module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof InventoryModule)
        {
            inputInventory = (InventoryModule) module;
        }

        stack = inventory.getStackInSlot(OUTPUT_INV_SLOT);
        module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof InventoryModule)
        {
            outputInventory = (InventoryModule) module;
        }

        stack = inventory.getStackInSlot(POWER_MOD_SLOT);
        module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof PowerModule)
        {
            powerModule = (PowerModule) module;
        }

        stack = inventory.getStackInSlot(CONTROL_MOD_SLOT);
        module = MachineModuleBuilder.INSTANCE.build(stack);
        if (module instanceof ControlModule)
        {
            controllerModule = (ControlModule) module;
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
                inventory.setInventorySlotContents(i, module.toStack());
            }
        }
        nbt.setTag("inventory", inventory.save(new NBTTagCompound()));
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
        //TODO only open module GUI if machine is powered down
        if (ID == 1)
        {
            //TODO open module GUI
        }
        return new ContainerDynamicMachine(getHost(), player);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        //TODO only open module GUI if machine is powered down
        if (ID == 1)
        {
            //TODO open module GUI
        }
        return new GuiDynamicMachine(getHost(), player);
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
        if (inventory == null)
        {
            inventory = new ExternalInventory(this, INVENTORY_SIZE);
        }
        return inventory;
    }

    public int getProcessingTicks()
    {
        return processingTime;
    }

    public int getMaxProcessingTicks()
    {
        return processingCompleteTime;
    }
}
