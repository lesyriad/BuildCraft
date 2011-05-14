package net.minecraft.src.buildcraft.transport;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.core.Utils;

public class BlockDiamondPipe extends BlockPipe {
	
	
	public BlockDiamondPipe(int i) {
		super(i, Material.iron);

		blockIndexInTexture = ModLoader.addOverride("/terrain.png",
		"/net/minecraft/src/buildcraft/transport/gui/diamond_pipe.png");
	}
	
	@Override
	protected TileEntity getBlockEntity() {
		return new TileDiamondPipe ();
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {		
		TileDiamondPipe	tileRooter = (TileDiamondPipe) world.getBlockTileEntity(i, j, k);
		
		TransportProxy.displayGUIFilter(entityplayer, tileRooter);
		
		return true;
	}	
	
    public void onBlockRemoval(World world, int i, int j, int k) {    	
		Utils.dropItems(world,
				(IInventory) world.getBlockTileEntity(i, j, k), i, j, k);
    	
        super.onBlockRemoval(world, i, j, k);        
    }
}
