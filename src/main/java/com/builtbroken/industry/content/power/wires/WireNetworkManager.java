package com.builtbroken.industry.content.power.wires;

import com.builtbroken.mc.api.event.tile.TileEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles events and update logic for wire networks
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/1/2017.
 */
public class WireNetworkManager
{
    public static WireNetworkManager INSTANCE;

    public List<WireNetwork> allNetworks = new ArrayList();

    public static void init()
    {
        if (INSTANCE != null)
        {
            INSTANCE = new WireNetworkManager();
            MinecraftForge.EVENT_BUS.register(INSTANCE);
        }
    }

    /**
     * Called to merge two networks into a single network
     *
     * @param grid
     * @param grid2
     */
    public static void merge(WireNetwork grid, WireNetwork grid2)
    {
        //Create new network
        WireNetwork network = WireNetwork.newGrid();

        //Add wires from each network
        grid.getNodes().forEach(t -> network.add(t));
        grid2.getNodes().forEach(t -> network.add(t));

        //Destroy old networks
        grid.deconstruct();
        grid2.deconstruct();

        //Call update on new network
        network.reconstruct();
    }

    /**
     * Called to add the network to the manager
     *
     * @param network
     */
    public void add(WireNetwork network)
    {
        if (!allNetworks.contains(network))
        {
            allNetworks.add(network);
        }
    }

    @SubscribeEvent
    public void onTileLoad(TileEvent.TileLoadEvent event)
    {
        if (event.tile() instanceof TileWire)
        {
            TileWire wire = (TileWire) event.tile();
            if (wire.getGrid() == null)
            {
                wire.refreshConnections();
            }
        }
    }

    @SubscribeEvent
    public void onTileUnload(TileEvent.TileUnLoadEvent event)
    {
        if (event.tile() instanceof TileWire)
        {
            TileWire wire = (TileWire) event.tile();
            if (wire.getGrid() != null)
            {
                int connections = 0;
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
                {
                    if (wire.hasWireConnection(dir))
                    {
                        connections++;
                        if (connections == 2)
                        {
                            break;
                        }
                    }
                }
                wire.getGrid().remove(wire);
                //If there are more than two connections destroy the network
                if (connections >= 2)
                {
                    wire.getGrid().deconstruct();
                }
                wire.setGrid(null);
            }
        }
    }
}
