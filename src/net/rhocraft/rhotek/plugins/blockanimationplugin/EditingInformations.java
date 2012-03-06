package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class EditingInformations {
	private Player player;
	private Animation animation;
	private int id;
	private byte data;
	private int timespan;
	private HashMap<BlockLocation, BlockID> copiedframe = new HashMap<BlockLocation, BlockID>();
	private HashMap<BlockID, BlockID> displayedblockmapping = new HashMap<BlockID, BlockID>();

	public EditingInformations(Player player, Animation animation) {
		this.player = player;
		this.animation = animation;
		this.timespan = 20;
	}

	/**
	 * Gets the payler.
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the animation the player edits.
	 * 
	 * @return the animation
	 */
	public Animation getAnimation() {
		return animation;
	}

	/**
	 * Gets the players selected block id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the players selected block id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the players selected block data value.
	 * 
	 * @return the data value
	 */
	public byte getData() {
		return data;
	}

	/**
	 * Sets the players selected block data value.
	 * 
	 * @param data
	 *            the data value to set
	 */
	public void setData(byte data) {
		this.data = data;
	}
	
	public int getTimeSpan() {
		return this.timespan;
	}
	
	public void setTimeSpan(int timespan) {
		if(timespan < 1) {
			timespan = 1;
		}
		this.timespan = timespan;
	}
	
	public HashMap<BlockLocation, BlockID> getCopiedFrame() {
		return this.copiedframe;
	}
	
	public void setCopiedFrame(HashMap<BlockLocation, BlockID> frame) {
		if(frame != null) {
			this.copiedframe = frame;
		} else {
			this.copiedframe = new HashMap<BlockLocation, BlockID>();
		}
	}
	
	public void mapDisplayedBlock(BlockID original, BlockID replacedby) {
		if(original != null && replacedby != null) {
			this.displayedblockmapping.put(original, replacedby);
		}
	}

	public void unmapDisplayedBlock(BlockID original) {
		if(original != null) {
			this.displayedblockmapping.remove(original);
		}
	}
	
	public void unmapAllDisplayedBlocks() {
		this.displayedblockmapping.clear();
	}
	
	public BlockID getMappedDisplayedBlock(BlockID original) {
		if(this.displayedblockmapping.containsKey(original)) {
			return this.displayedblockmapping.get(original);
		}
		return null;
	}
}
