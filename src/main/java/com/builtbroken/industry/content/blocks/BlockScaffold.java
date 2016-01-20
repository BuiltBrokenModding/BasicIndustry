package com.builtbroken.industry.content.blocks;

import com.builtbroken.industry.BasicIndustry;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by Green on 1/19/2016.
 */
public class BlockScaffold extends Block {

    public BlockScaffold()
    {
        super(Material.circuits);
        this.setBlockName(BasicIndustry.PREFIX + "scaffold");
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
        if(meta >= 0 && meta < ScaffoldBlocks.values().length)
        {
            return ScaffoldBlocks.values()[meta].icon;
        }
        return Blocks.fence.getIcon(side, meta);
    }

    @Override
    public boolean isOpaqueCube() { return false; }
    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        return true;
    }

    public enum ScaffoldBlocks
    {
        SCAFFOLD_BLOCK("casing_wood");
        protected IIcon icon;
        protected final String textureName;

        ScaffoldBlocks(String name)
        {
            this.textureName = name;
       }
    }

}
