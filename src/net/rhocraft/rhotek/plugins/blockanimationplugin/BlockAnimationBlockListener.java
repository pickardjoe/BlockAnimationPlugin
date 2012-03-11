package net.rhocraft.rhotek.plugins.blockanimationplugin;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;


public class BlockAnimationBlockListener implements Listener {
	private final BlockAnimationPlugin plugin;

	public BlockAnimationBlockListener(BlockAnimationPlugin instance) {
		plugin = instance;
		plugin.getClass();
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		EditingInformations info = plugin.getWorld(player.getWorld()).getEditingInformations(player);
		if (info != null && info.getAnimation() != null) {
			Block b = event.getBlock();
			Animation animation = info.getAnimation();
			int timespan = info.getTimeSpan();
			for (int i = 0; i < timespan; i++) {
				animation.setBlockIdAt(b.getX(), b.getY(), b.getZ(),
						b.getTypeId(), b.getData(), info.getAnimation()
								.getAnimationStep() + i);
			}
			plugin.sendMessage("edit.block.added", player);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		EditingInformations info = plugin.getWorld(player.getWorld()).getEditingInformations(player);
		Block b = event.getBlock();
		Animation animation = plugin.getWorld(player.getWorld()).getAnimation(b.getX(), b.getY(), b.getZ());
		if (info != null && info.getAnimation() != null) {
			if (animation == info.getAnimation()) {
				int timespan = info.getTimeSpan();
				for (int i = 0; i < timespan; i++) {
					animation.setBlockIdAt(b.getX(), b.getY(), b.getZ(), 0,
							(byte) 0, animation.getAnimationStep() + i);
				}
				plugin.sendMessage("edit.block.removed", player);
			}
		} else {
			if (animation != null) {
				event.setCancelled(true);
				plugin.sendMessage("error.animationblock", player);
			}
		}
	}
}
