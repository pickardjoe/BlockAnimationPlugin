package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Animation {
	private World world;
	private String name = null;
	private boolean running = false;
	private boolean redraw = false;
	private boolean construction = false;
	private TimeMode gametime = TimeMode.DURATIONS;
	private int step;
	private final ArrayList<Block> blocks = new ArrayList<Block>();
	private final Lock blocklistlock = new ReentrantLock();

	public Animation(World world, String name) {
		this.world = world;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public TimeMode getTimeMode() {
		return this.gametime;
	}

	public void setTimeMode(TimeMode value) {
		this.gametime = value;
		if (!this.construction)
			this.world.getXMLAccesser().updateAnimation(this);
	}

	public void start() {
		this.running = true;
		if (!this.construction)
			this.world.getXMLAccesser().updateAnimation(this);
	}

	public void pause() {
		this.running = false;
		if (!this.construction)
			this.world.getXMLAccesser().updateAnimation(this);
	}

	public void stop() {
		this.running = false;
		this.step = 0;
		this.redraw = true;
		if (!this.construction)
			this.world.getXMLAccesser().updateAnimation(this);
	}

	public boolean getRedraw() {
		return this.redraw;
	}

	public void drawn() {
		this.redraw = false;
	}

	public boolean isRunning() {
		return this.running;
	}

	public boolean isInConstruction() {
		return this.construction;
	}

	public void setInConstruction(boolean value) {
		this.construction = value;
		if (!value) {
			this.save();
		}
	}

	public boolean save() {
		this.world.getXMLAccesser().saveAnimation(this);
		return false;
	}

	public boolean nextAnimationStep() {
		boolean changes = false;
		long duration = getDuration();
		this.step++;
		if (this.step >= duration + (this.construction ? 1 : 0)) {
			this.step = 0;
		}
		return changes;
	}

	public int getDuration() {
		int duration = 0;
		this.blocklistlock.lock();
		for (Block bg : this.blocks) {
			if (duration < bg.getDuration()) {
				duration = bg.getDuration();
			}
		}
		this.blocklistlock.unlock();
		return duration;
	}

	public boolean previousAnimationStep() {
		boolean changes = false;
		int duration = getDuration();
		this.step--;
		if (this.step < 0) {
			this.step = duration - (this.construction ? 0 : 1);
			if (this.step < 0) {
				this.step = 0;
			}
		}
		return changes;
	}

	public static boolean isName(String name) {
		return name != null && !name.equals("") && name.matches("\\w+");
	}

	public void addBlock(Block block) {
		block.setAnimation(this);
		this.blocklistlock.lock();
		this.blocks.add(block);
		this.blocklistlock.unlock();
	}

	public void removeBlock(Block block) {
		this.blocklistlock.lock();
		this.blocks.remove(block);
		block.setAnimation(null);
		this.blocklistlock.unlock();
	}

	public Block[] getBlocks() {
		return this.blocks.toArray(new Block[blocks.size()]);
	}

	public enum TimeMode {
		GAMETIME, REALTIME, DURATIONS
	}

	public int getAnimationStep() {
		return step;
	}

	public void setBlockIdAt(int x, int y, int z, int id, byte data, int time) {
		boolean added = false;
		for (Block block : this.blocks) {
			if (block.equals(x, y, z)) {
				if (block.getDuration() > time) {
					block.setBlockIdAt(id, data, time);
					added = true;
				} else {
					for (int i = 0; i < block.getDuration() - time - 1; i++) {
						block.addBlockID(0, (byte) 0);
					}
					block.addBlockID(id, data);
					added = true;
				}
			} else {
				for (int i = 0; i < time + 1 - block.getDuration(); i++) {
					block.addBlockID(0, (byte) 0);
				}
			}
		}
		if (!added) {
			Block block = new Block(new BlockLocation(x, y, z));
			for (int i = 0; i < time; i++) {
				block.addBlockID(0, (byte) 0);
			}
			block.addBlockID(id, (byte) data);
			for (int i = 0; i < getDuration() - 1 - time; i++) {
				block.addBlockID(0, (byte) 0);
			}
			this.addBlock(block);
		}
	}

	private Block getBlock(int x, int y, int z) {
		this.blocklistlock.lock();
		for (Block block : blocks) {
			if (block.getLocation().getX() == x
					&& block.getLocation().getY() == y
					&& block.getLocation().getZ() == z) {
				this.blocklistlock.unlock();
				return block;
			}
		}
		this.blocklistlock.unlock();
		return null;
	}

	public World getWorld() {
		return this.world;
	}

	public void setAnimationStep(int time) {
		if (time < this.getDuration() + (this.construction ? 1 : 0)
				&& time >= 0) {
			this.step = time;
		} else if (time < 0) {
			this.step = this.getDuration() + (this.construction ? 1 : 0) - 1;
		} else {
			this.step = 0;
		}
	}

	public void addBlock(int x, int y, int z, int id, byte data, long time) {
		Block block = this.getBlock(x, y, z);
		if (block == null) {
			block = new Block(x, y, z);
			for (int i = 1; i < time; i++) {
				block.addBlockID(0, (byte) 0);
			}
			block.addBlockID(id, data);
			for (int i = 0; i < this.getDuration() - time; i++) {
				block.addBlockID(0, (byte) 0);
			}
		}
	}

	public boolean containsBlock(int x, int y, int z) {
		this.blocklistlock.lock();
		for (Block block : this.blocks) {
			if (block.getLocation().getX() == x
					&& block.getLocation().getY() == y
					&& block.getLocation().getZ() == z) {
				this.blocklistlock.unlock();
				return true;
			}
		}
		this.blocklistlock.unlock();
		return false;
	}

	public void removeBlock(int x, int y, int z) {
		Block block = this.getBlock(x, y, z);
		this.blocklistlock.lock();
		this.blocks.remove(block);
		this.blocklistlock.unlock();
	}

	public HashMap<BlockLocation, BlockID> getCurrentFrame() {
		HashMap<BlockLocation, BlockID> map = new HashMap<BlockLocation, BlockID>();
		this.blocklistlock.lock();
		for (Block block : this.blocks) {
			map.put(block.getLocation(), block.getBlockID(this.step));
		}
		this.blocklistlock.unlock();
		return map;
	}

	public void addEmptyFrameAt(int time) {
		for (Block block : this.blocks) {
			block.addBlockID(0, (byte) 0, time);
		}
	}

	public static int parseDuration(String duration) {
		return (int) (Double.parseDouble(duration) * 20);
	}

	public static String durationToString(int duration) {
		return "" + (duration / 20.0);
	}
}
