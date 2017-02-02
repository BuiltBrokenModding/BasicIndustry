package com.builtbroken.industry.content.machines.dynamic.modules.furnace;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.recipe.IMachineRecipeHandler;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/31/2016.
 */
public class TileFurnace extends TileModuleMachine implements IRecipeContainer, IGuiTile
{
    private static final int[] slotsTop = new int[]{0, 1};
    private static final int[] slotsBottom = new int[]{0, 1, 4};
    private static final int[] slotsSides = new int[]{2, 3};

    protected boolean hasRecipe = false;
    protected int processingTicks = 0;
    protected int burnTime = 0;
    protected int burnTimeItem = 0;

    @SideOnly(Side.CLIENT)
    public static IIcon frontIcon;

    @SideOnly(Side.CLIENT)
    public static IIcon frontIconOn;

    @SideOnly(Side.CLIENT)
    public static IIcon sideIcon;

    @SideOnly(Side.CLIENT)
    public static IIcon topIcon;

    @SideOnly(Side.CLIENT)
    public static IIcon bottomIcon;

    public TileFurnace()
    {
        super("furnace", Material.rock);
        this.resistance = 1;
        this.hardness = 5;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 5);
    }

    @Override
    public TileFurnace newTile()
    {
        return new TileFurnace();
    }

    @Override
    public void update()
    {
        super.update();

        if (isServer())
        {
            boolean isModified = false;
            //Reduce fuel
            if (burnTime > 0)
            {
                burnTime--;
            }

            //Check recipe one a second
            if (ticks % 20 == 0)
            {
                hasRecipe = getRecipeOutput(0) != null || getRecipeOutput(1) != null;
            }

            //Consume fuel as needed
            if (hasRecipe && burnTime <= 0)
            {
                this.burnTimeItem = this.burnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(4));
                isModified = true;
                if (burnTime > 0)
                {
                    //Bucket handling
                    if (getStackInSlot(4).stackSize == 1)
                    {
                        setInventorySlotContents(4, getStackInSlot(4).getItem().getContainerItem(getStackInSlot(4)));
                    }
                    else
                    {
                        decrStackSize(4, 1);
                    }
                }
            }

            if (this.burnTime > 0 && hasRecipe)
            {
                if (processingTicks >= 200)
                {
                    for (int slot = 0; slot <= 1; slot++)
                    {
                        Pair<ItemStack, Integer> recipeOutput = getRecipeOutput(slot);
                        if (recipeOutput != null)
                        {
                            if (addToOutput(recipeOutput.left(), true))
                            {
                                processingTicks = 0;
                                decrStackSize(recipeOutput.right(), 1);
                                isModified = true;
                            }
                        }
                    }
                }
                else
                {
                    processingTicks++;
                }
            }
            else
            {
                processingTicks = 0;
            }

            if (isModified)
            {
                this.markDirty();
            }
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, BasicIndustry.INSTANCE);
        }
        return true;
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        //TODO rotate
        return false;
    }

    /**
     * Gets the recipe for the furnace
     *
     * @return
     */
    public Pair<ItemStack, Integer> getRecipeOutput(int slot)
    {
        IMachineRecipeHandler recipeHandler = MachineRecipeType.ITEM_SMELTER.getHandler();
        if (recipeHandler != null)
        {
            if (getStackInSlot(slot) != null)
            {
                Object out = recipeHandler.getRecipe(new Object[]{getStackInSlot(slot)}, 0, 0);
                if (out instanceof ItemStack)
                {
                    return new Pair(out, slot);
                }
            }
        }
        return null;
    }

    /**
     * Called to output an item
     *
     * @param stack    - stack to output
     * @param doAction - true will output stack, false will check for space only
     * @return
     */
    public boolean addToOutput(ItemStack stack, boolean doAction)
    {
        for (int i = 2; i <= 3; i++)
        {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack == null)
            {
                if (doAction)
                {
                    setInventorySlotContents(i, stack);
                }
                return true;
            }
            else if (InventoryUtility.stacksMatch(stack, slotStack) && InventoryUtility.roomLeftInSlot(this, i) >= stack.stackSize)
            {
                if (doAction)
                {
                    slotStack.stackSize += stack.stackSize;
                    setInventorySlotContents(i, slotStack);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slot == 0 || slot == 1)
        {
            if (BasicIndustry.doInputSlotValidationChecks)
            {
                return MachineRecipeType.ITEM_SMELTER.getHandler().getRecipe(new Object[]{stack}, 0, 0) instanceof ItemStack;
            }
            return true;
        }
        else if (slot == 4)
        {
            return TileEntityFurnace.isItemFuel(stack);
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == 2 || slot == 3;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side == 0 ? slotsBottom : (side == 1 ? slotsTop : slotsSides);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.burnTime = nbt.getInteger("cookTime");
        this.burnTimeItem = nbt.getInteger("cookTimeItem");
        this.processingTicks = nbt.getInteger("processingTicks");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("cookTime", burnTime);
        nbt.setInteger("cookTimeItem", burnTimeItem);
        nbt.setInteger("processingTicks", processingTicks);
    }

    @Override
    protected boolean useMetaForFacing()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (side == 0)
        {
            return bottomIcon;
        }
        else if (side == 1)
        {
            return topIcon;
        }
        else if (side == meta)
        {
            return burnTime > 0 ? frontIconOn : frontIcon;
        }
        return sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        sideIcon = iconRegister.registerIcon(BasicIndustry.PREFIX + "brickFurnace.side");
        topIcon = iconRegister.registerIcon(BasicIndustry.PREFIX + "brickFurnace.top");
        bottomIcon = iconRegister.registerIcon(BasicIndustry.PREFIX + "brickFurnace.bottom");
        frontIcon = iconRegister.registerIcon(BasicIndustry.PREFIX + "brickFurnace.front");
        frontIconOn = iconRegister.registerIcon(BasicIndustry.PREFIX + "brickFurnace.front.on");
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(BasicIndustry.blockBrickFurnace, "SSS", "BFB", "BBB", 'S', Blocks.stone_slab, 'B', Blocks.brick_block, 'F', Blocks.furnace));
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerFurnace(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiFurnace(player, this);
    }
}
