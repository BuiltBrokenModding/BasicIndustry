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
        //TODO path down to see how high we are stacked
        //TODO add stack limits to each tier
        return canBlockStay(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        return world.isSideSolid(x - 1, y, z, ForgeDirection.DOWN);
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
        if (world.getBlockMetadata(x, y, z) == ScaffoldBlocks.SCAFFOLD_BLOCK.ordinal())
        {
            return 10;
        }
        return super.getFireSpreadSpeed(world, x, y, z, face);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        //Wooden scaffold
        if (world.getBlockMetadata(x, y, z) == ScaffoldBlocks.SCAFFOLD_BLOCK.ordinal())
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
            items.add(new ItemStack(item, i, 0));
        }
    }

    /**
     * Enum of meta values
     */
    public enum ScaffoldBlocks
    {
        /** Lowest tier version */
        SCAFFOLD_BLOCK("casing_wood");

        /** Tile icon. */
        protected IIcon icon;
        /** Texture name. */
        protected final String textureName;

        ScaffoldBlocks(String name)
        {
            this.textureName = name;
        }
    }
}
