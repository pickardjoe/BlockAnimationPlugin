package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.util.ArrayList;

public class Block {
	private BlockLocation location;
	private ArrayList<BlockID> blockids = new ArrayList<BlockID>();
	private Animation animation;
	
	public Block(int x, int y, int z) {
		this.location = new BlockLocation(x, y, z);
	}
	
	public Block(BlockLocation location) {
		this.location = location;
	}
	
	public BlockLocation getLocation() {
		return this.location;
	}
	
	public void addBlockID(int id, byte data, int time) {
		blockids.add(time, new BlockID(id, data));
	}
	
	public void addBlockID(int id, byte data) {
		blockids.add(new BlockID(id, data));
	}
	
	public void removeBlockID(int time) {
		if(this.blockids.size() > time && time >= 0) {
			this.blockids.remove(time);
		}
	}
	
	public int getDuration() {
		return blockids.size();
	}
	
	public Animation getAnimation() {
		return this.animation;
	}
	
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}
	
	public BlockID getBlockID(int time) {
		if(this.blockids.size() > time && time >= 0) {
			return this.blockids.get(time);
		}
		return null;
	}
	
	public BlockID[] getBlockIDs() {
		return this.blockids.toArray(new BlockID[blockids.size()]);
	}

	public boolean equals(int x, int y, int z) {
		return this.location.getX() == x && this.location.getY() == y && this.location.getZ() == z;
	}

	public void setBlockIdAt(int id, byte data, int time) {
		if(this.blockids.size() >= time && time >= 0) {
			this.blockids.set(time, new BlockID(id, data));
		}
	}
}
