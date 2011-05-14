package net.minecraft.src.buildcraft.transport;

import java.util.LinkedList;

import net.minecraft.src.Container;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.core.EntityPassiveItem;
import net.minecraft.src.buildcraft.core.Orientations;
import net.minecraft.src.buildcraft.core.Position;
import net.minecraft.src.buildcraft.core.Utils;

public class TileWoodenPipe extends TilePipe {
	
	long lastMining = 0;
	boolean lastPower = false;
	
	public TileWoodenPipe () {
		
	}
	
	public void checkPower () {
		World w = CoreProxy.getWorld();
		boolean currentPower = w.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		
		if (lastPower != currentPower) {
			extract ();
		}
		
		lastPower = currentPower;
	}
	
	/** 
	 * Extracts a random piece of item outside of a nearby chest.
	 */
	public void extract () {		
		World w = CoreProxy.getWorld();
		
		if (w.getWorldTime() - lastMining < 50) {
			return;
		}
		
		lastMining = w.getWorldTime();
		
		LinkedList<Position> inventories = new LinkedList<Position>();
		
		for (int j = 0; j < 6; ++j) {
			Position pos = new Position(xCoord, yCoord, zCoord,
					Orientations.values()[j]);
			pos.moveForwards(1.0);
			
			TileEntity tile = w.getBlockTileEntity((int) pos.i, (int) pos.j,
					(int) pos.k);
			
			if (tile instanceof IInventory) {
				IInventory inventory = (IInventory) tile;
				
				if (checkExtract(inventory, false, pos.orientation.reverse()) != null) {
					inventories.add(pos);
				}
			}
		}
		
		if (inventories.size() == 0) {
			return;
		}
		
		Position chestPos = inventories.get(w.rand.nextInt(inventories.size()));
		IInventory inventory = (IInventory) w.getBlockTileEntity(
				(int) chestPos.i, (int) chestPos.j, (int) chestPos.k);
		
		ItemStack stack = checkExtract(inventory, true,
				chestPos.orientation.reverse());								
		
		Position entityPos = new Position(chestPos.i + 0.5, chestPos.j
				+ Utils.getPipeFloorOf(stack), chestPos.k + 0.5,
				chestPos.orientation.reverse());
				
		entityPos.moveForwards(0.5);
				
		EntityPassiveItem entity = new EntityPassiveItem(w, entityPos.i,
				entityPos.j, entityPos.k, stack);
		
		w.entityJoinedWorld(entity);
		entityEntering(entity, entityPos.orientation);		
	}
	
	/**
	 * Return the itemstack that can be if something can be extracted from this
	 * inventory, null if none. On certain cases, the extractable slot depends
	 * on the position of the pipe.
	 */
	public ItemStack checkExtract (IInventory inventory, boolean doRemove, Orientations from) {
		if (inventory instanceof TileDiamondPipe) {
			return null;
		}
		
		if (inventory.getSizeInventory() == 3) {
			//  This is a furnace-like inventory
			
			int slotIndex = 0;
			
			if (from == Orientations.YPos) {
				slotIndex = 0;
			} else if (from == Orientations.YNeg) {
				slotIndex = 1;
			} else {
				slotIndex = 2;
			}
			
			ItemStack slot = inventory.getStackInSlot(slotIndex);
			
			if (slot != null && slot.stackSize > 0) {			
				if (doRemove) {
					return inventory.decrStackSize(slotIndex, 1);
				} else {
					return slot;
				}			
			}	
		} else if (inventory.getSizeInventory() == 9) {
			// This is a workbench inventory
			
			// Do only craft if there's at least two items of each, to keep
			// the template.
			

			InventoryCrafting craftMatrix = new InventoryCrafting(new Container () {
				@SuppressWarnings("unused")
				public boolean isUsableByPlayer(EntityPlayer entityplayer) {
					return false;
				}

				@SuppressWarnings("unused")
				public boolean canInteractWith(EntityPlayer entityplayer) {
					// TODO Auto-generated method stub
					return false;
				}}, 3, 3);	
			
			for (int i = 0; i < inventory.getSizeInventory(); ++i) {
				ItemStack stack = inventory.getStackInSlot(i);
				
				if (stack != null && stack.stackSize == 1) {
					return null;
				}
				
				craftMatrix.setInventorySlotContents(i, stack);
			}
			
			ItemStack resultStack = CraftingManager.getInstance().findMatchingRecipe(
					craftMatrix);
			
			if (resultStack != null && doRemove) {
				for (int i = 0; i < inventory.getSizeInventory(); ++i) {
					ItemStack stack = inventory.getStackInSlot(i);
					
					if (stack != null) {
						inventory.decrStackSize(i, 1);
					}
				}
			}
			
			return resultStack;
		} else {
			// This is a generic inventory
			
			for (int k = 0; k < inventory.getSizeInventory(); ++k) {
				if (inventory.getStackInSlot(k) != null
						&& inventory.getStackInSlot(k).stackSize > 0) {
										
					ItemStack slot = inventory.getStackInSlot(k);				
					
					if (slot != null && slot.stackSize > 0) {
						if (doRemove) {
							return inventory.decrStackSize(k, 1);
						} else {
							return slot;
						}
					}				
				}
			}
		}
		
		return null;
	}

}
