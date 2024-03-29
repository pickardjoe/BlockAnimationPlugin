package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

public class World {
	private BlockAnimationPlugin plugin;
	private XMLAccesser xml;
	private org.bukkit.World world;
	private ArrayList<Animation> animations = new ArrayList<Animation>();
	private final Lock animationlistlock = new ReentrantLock();
	private AnimationScheduler scheduler;
	// private int schedulertask = -1;
	private HashMap<Player, EditingInformations> editinginformations = new HashMap<Player, EditingInformations>();

	public World(BlockAnimationPlugin plugin, org.bukkit.World world) {
		this.plugin = plugin;
		this.world = world;
		this.xml = new XMLAccesser(this, plugin.getServer().getLogger());
		if (this.world != null) {
			loadAnimations();
			this.scheduler = new AnimationScheduler(this);
			plugin.getServer().getScheduler()
					.scheduleSyncRepeatingTask(plugin, this.scheduler, 0, 1);
		}
	}

	public org.bukkit.World getWorld() {
		return this.world;
	}

	public XMLAccesser getXMLAccesser() {
		return this.xml;
	}

	public String[] getAnimationNames() {
		Animation[] animations = getAnimations();
		String[] names = new String[animations.length];
		for (int i = 0; i < animations.length; i++) {
			names[i] = animations[i].getName();
		}
		return names;
	}

	public Animation getAnimation(String name) {
		this.animationlistlock.lock();
		for (Animation animation : this.animations) {
			if (animation.getName().equalsIgnoreCase(name)) {
				this.animationlistlock.unlock();
				return animation;
			}
		}
		return null;
	}

	public Animation getAnimation(int x, int y, int z) {
		this.animationlistlock.lock();
		for (Animation animation : this.animations) {
			if (animation.containsBlock(x, y, z)) {
				this.animationlistlock.unlock();
				return animation;
			}
		}
		this.animationlistlock.unlock();
		return null;
	}

	public void createAnimation(String name) {
		this.animationlistlock.lock();
		this.animations.add(new Animation(this, name));
		xml.createAnimation(name);
		this.animationlistlock.unlock();
	}

	public void removeAnimation(Animation animation) {
		this.animationlistlock.lock();
		this.animations.remove(animation);
		xml.removeAnimation(animation.getName());
		this.animationlistlock.unlock();
	}

	public void loadAnimations() {
		this.animationlistlock.lock();
		Logger logger = plugin.getServer().getLogger();
		logger.log(Level.FINE, "BlockAnimationPlugin: Loading animations");
		Animation[] animations = xml.getAnimations();
		for (Animation animation : animations) {
			this.animations.add(animation);
//			Block[] blocks = xml.getBlocks(animation);
//			for (Block blockgroup : blocks) {
//				animation.addBlock(blockgroup);
//			}
			logger.log(Level.FINE, "BlockAnimationPlugin: " + animation.getName() + " loaded");
		}
		this.animationlistlock.unlock();
	}

	public void saveAnimations() {
		this.animationlistlock.lock();
		for (Animation animation : this.animations) {
			xml.updateAnimation(animation);
		}
		this.animationlistlock.unlock();
	}

	public String getName() {
		return this.world.getName();
	}

	public Animation[] getAnimations() {
		this.animationlistlock.lock();
		Animation[] animations = this.animations
				.toArray(new Animation[this.animations.size()]);
		this.animationlistlock.unlock();
		return animations;
	}

	public void addEditingInformations(Player player, EditingInformations info) {
		editinginformations.put(player, info);
	}

	public void removeEditingInformations(Player player) {
		editinginformations.remove(player);
	}

	public EditingInformations getEditingInformations(Player player) {
		return editinginformations.get(player);
	}

	public EditingInformations[] getEditingInformations(Animation animation) {
		ArrayList<EditingInformations> infos = new ArrayList<EditingInformations>();
		for (EditingInformations info : editinginformations.values()) {
			if (info.getAnimation() == animation) {
				infos.add(info);
			}
		}
		return infos.toArray(new EditingInformations[infos.size()]);
	}
}
