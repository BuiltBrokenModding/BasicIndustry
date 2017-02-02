package com.builtbroken.industry.content.power.wires;

import com.builtbroken.industry.BasicIndustry;
import com.builtbroken.mc.api.grid.IGridNode;
import com.builtbroken.mc.api.tile.ConnectionType;
import com.builtbroken.mc.api.tile.ITileConnection;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Really basic wires with no extra functionality beyond providing connections for networks
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/1/2017.
 */
public class TileWire extends Tile implements IPacketReceiver, IGridNode<WireNetwork>, ITileConnection
{
    //TODO replace with casing object, allows for none-block materials
    public Block casingMaterial;
    public int casingMaterialMeta = 0;

    public WireNetwork network;

    /** Bitmask **/
    private byte wireConnections = 0;

    @SideOnly(Side.CLIENT)
    private static IIcon wireTexture;

    public TileWire()
    {
        super("wire", Material.iron);
        onAdded();
    }

    @Override
    public Tile newTile()
    {
        return new TileWire();
    }

    @Override
    public void onNeighborChanged(Pos pos)
    {
        //IF we do not have a grid, find or make one
        if (getGrid() == null)
        {
            findOrMakeGrid();
        }

        //TODO unit test
        final TileEntity tile = pos.getTileEntity(world());
        final ForgeDirection dir = toPos().sub(pos).floor().toForgeDirection();
        if (tile instanceof ITileConnection)
        {
            if (((ITileConnection) tile).canConnect(this, ConnectionType.UE_POWER, dir))
            {
                checkForConnection(dir, tile);
            }
            else if (hasWireConnection(dir))
            {
                refreshConnections();
            }
        }
        else if (hasWireConnection(dir))
        {
            refreshConnections();
        }
    }

    @Override
    public boolean refreshConnections()
    {
        final Pos center = new Pos(this);

        //IF we do not have a grid, find or make one
        if (getGrid() == null)
        {
            findOrMakeGrid();
        }

        //Update connections
        final byte prevConnections = wireConnections;
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            final Pos pos = center.add(direction);
            final TileEntity tile = pos.getTileEntity(world());
            checkForConnection(direction, tile);
        }
        if (prevConnections != wireConnections)
        {
            sendDescPacket();
            return true;
        }
        return false;
    }

    private void findOrMakeGrid()
    {
        final Pos center = new Pos(this);
        //Look for an existing network
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            Pos pos = center.add(direction);
            TileEntity tile = pos.getTileEntity(world());
            if (tile instanceof TileWire && ((TileWire) tile).getGrid() != null)
            {
                setGrid(((TileWire) tile).getGrid());
                break;
            }
        }
        if (getGrid() == null)
        {
            setGrid(WireNetwork.newGrid());
        }
    }

    /**
     * Checks if a connection should be made
     *
     * @param direction - direction from this tile
     * @param tile      - tile to check for connection
     * @return true if a connection was made
     */
    private boolean checkForConnection(ForgeDirection direction, TileEntity tile)
    {
        if (tile instanceof ITileConnection && ((ITileConnection) tile).canConnect(this, ConnectionType.UE_POWER, direction))
        {
            //If wire check for network merges
            if (tile instanceof TileWire)
            {
                if (((TileWire) tile).getGrid() != getGrid())
                {
                    WireNetworkManager.merge(((TileWire) tile).getGrid(), getGrid());
                }
            }
            setWireConnection(direction, true);
            return true;
        }
        else
        {
            setWireConnection(direction, false);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return wireTexture;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        wireTexture = iconRegister.registerIcon(BasicIndustry.PREFIX + "copperWire");
    }

    public boolean hasWireConnection(ForgeDirection direction)
    {
        return (wireConnections & (1 << direction.ordinal())) != 0;
    }

    public void setWireConnection(ForgeDirection direction, boolean hadConnection)
    {
        if (hadConnection)
        {
            wireConnections = (byte) (wireConnections | (1 << direction.ordinal()));
        }
        else
        {
            wireConnections = (byte) (wireConnections & ~(1 << direction.ordinal()));

        }

        if (!this.worldObj.isRemote)
        {
            sendDescPacket();
        }

        markDirty();
        markRender();
    }

    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType packet)
    {
        if (isClient())
        {
            String blockName = ByteBufUtils.readUTF8String(data);
            this.casingMaterial = blockName.isEmpty() ? null : (Block) Block.blockRegistry.getObject(blockName);
            this.casingMaterialMeta = data.readInt();
            this.wireConnections = data.readByte();
            markRender();
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        String blockName = "";
        if (casingMaterial != null)
        {
            blockName = Block.blockRegistry.getNameForObject(casingMaterialMeta);
            if (blockName == null)
            {
                blockName = "";
            }
        }
        return new PacketTile(this, blockName, this.casingMaterialMeta, this.wireConnections);
    }

    @Override
    public void setGrid(WireNetwork grid)
    {
        if (network != null)
        {
            network.remove(this);
        }
        network = grid;
        if (network != null)
        {
            grid.add(this);
        }
    }

    @Override
    public WireNetwork getGrid()
    {
        return network;
    }

    @Override
    public boolean canConnect(TileEntity tile, ConnectionType type, ForgeDirection from)
    {
        if (tile instanceof TileWire)
        {
            return ((TileWire) tile).casingMaterial == casingMaterial && ((TileWire) tile).casingMaterialMeta == casingMaterialMeta;
        }
        return type == ConnectionType.UE_POWER;
    }

    @Override
    public boolean hasConnection(ConnectionType type, ForgeDirection side)
    {
        return (type == null || type == ConnectionType.UE_POWER) && hasWireConnection(side);
    }
}
