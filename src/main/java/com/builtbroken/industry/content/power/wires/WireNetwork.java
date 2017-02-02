package com.builtbroken.industry.content.power.wires;

import com.builtbroken.mc.api.grid.IGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/1/2017.
 */
public class WireNetwork implements IGrid<TileWire>
{
    private List<TileWire> wires = new ArrayList();

    @Override
    public List<TileWire> getNodes()
    {
        return wires;
    }

    @Override
    public void add(TileWire node)
    {
        if (!wires.contains(node))
        {
            wires.add(node);
            node.setGrid(this);
        }
    }

    @Override
    public void remove(TileWire node)
    {
        wires.remove(node);
    }

    @Override
    public void reconstruct()
    {
        //If invalid destroy the network
        if (wires.stream().anyMatch(t -> t.isInvalid()))
        {
            deconstruct();
        }
        else
        {
            wires.forEach(t -> t.refreshConnections());
            //TODO clear machine connections
            //TODO rebuild machine connection
        }
    }

    @Override
    public void deconstruct()
    {
        //Clear grid
        wires.stream().filter(t -> t.getGrid() == this).forEach(t -> t.setGrid(null));
        //Update connections
        wires.forEach(t -> t.refreshConnections());
        //Wires will rebuild new networks
    }

    public static WireNetwork newGrid(TileWire... wires)
    {
        WireNetwork network = new WireNetwork();
        if (wires != null)
        {
            for (TileWire wire : wires)
            {
                network.add(wire);
            }
        }
        WireNetworkManager.INSTANCE.add(network);
        return network;
    }
}
