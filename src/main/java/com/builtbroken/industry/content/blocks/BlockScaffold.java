package com.builtbroken.industry.content.blocks;

import com.builtbroken.industry.BasicIndustry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Full block sized latter used for easy navigation around multi-block structures.
 * Created by Green on 1/19/2016.
 */
public class BlockScaffold extends Block
{
    public BlockScaffold()
    {
        super(Material.circuits);
        this.setBlockName(BasicIndustry.PREFIX + "scaffold");
        this.setHardness(2f);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        //TODO have a version that is redstone controlled to not be a ladder when redstone is on
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        if (canBlockStay(world, x, y, z))
        {
            //TODO allow stronger scaffolds at bottom to extend
            //low tier build height. In other words weak scaffolds at top
            //strong scaffolds at the bottom.

            final int meta = world.getBlockMetadata(x, y, z);
            final int max = ScaffoldBlocks.getMaxScaffolds(meta);

            int blocks = 0;
            int yDelta = -1;

            while (blocks < max)
            {
                final Block block = world.getBlock(x, y + yDelta, z);
                final int blockMeta = world.getBlockMetadata(x, y + yDelta, z);
                if (block != this)
                {
                    break;
                }
                if (blockMeta != meta)
                {
                    //TODO add error output for not supporting stacking different materials
                    return false;
                }
                y--;
                blocks++;
            }
            //TODO add sound effect of breaking wood if too tall
            //TODO add chance to break lowest block if too tall
            return blocks < max;
        }
        return false;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return world.isSideSolid(x, y - 1, z, ForgeDirection.UP);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (!canBlockStay(world, x, y, z))
        {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
        super.onNeighborBlockChange(world, x, y, z, block);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        //return true as see threw blocks are not considered solid by default
        return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        //Wooden scaffold
        if (world.getBlockMetadata(x, y, z) == ScaffoldBlocks.SCAFFOLD_WOOD.ordinal())
        {
            return 10;
        }
        return super.getFireSpreadSpeed(world, x, y, z, face);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        //Wooden scaffold
        if (world.getBlockMetadata(x, y, z) == ScaffoldBlocks.SCAFFOLD_WOOD.ordinal())
        {
            return 20;
        }
        return super.getFlammability(world, x, y, z, face);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        for (ScaffoldBlocks block : ScaffoldBlocks.values())
        {
            block.icon = reg.registerIcon(BasicIndustry.PREFIX + block.textureName);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta >= 0 && meta < ScaffoldBlocks.values().length)
        {
            return ScaffoldBlocks.values()[meta].icon;
        }
        return Blocks.fence.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List items)
    {
        for (int i = 0; i < ScaffoldBlocks.values().length; i++)
        {
            items.add(new ItemStack(item, 1, i));
        }
    }

    /**
     * Enum of meta values
     */
    public enum ScaffoldBlocks
    {
        /** Lowest too highest tier versions */
        SCAFFOLD_WOOD("casing_wood", 10),
        SCAFFOLD_WOOD_2("casing_wood", 15),
        SCAFFOLD_STONE("casing_stone", 10),
        SCAFFOLD_COPPER("casing_copper", 20),
        SCAFFOLD_BRONZE("casing_bronze", 25),
        SCAFFOLD_IRON("casing_iron", 30),
        SCAFFOLD_STEEL("casing_steel", 35),
        SCAFFOLD_ALUM("casing_alum", 40);

        /** Tile icon. */
        protected IIcon icon;
        /** Texture name. */
        protected final String textureName;
        /** Max stacked scaffold blocks. */
        protected final int maxScaffolds;

        ScaffoldBlocks(String name, int max)
        {
            this.textureName = name;
            this.maxScaffolds = max;
        }

        /**
         * Max number of scaffolds that can be stacked without breaking.
         *
         * @param meta - meta value, between 0 - 15, checked
         * @return max number of stacked blocks, or 10 for default value
         */
        public static int getMaxScaffolds(int meta)
        {
            if (meta >= 0 && meta < values().length)
            {
                return values()[meta].maxScaffolds;
            }
            return 10;
        }
    }
}
