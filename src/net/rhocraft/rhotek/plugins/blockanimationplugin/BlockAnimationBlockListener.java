/*
 * Copyright 2011 Sacaldur
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rhocraft.rhotek.plugins.blockanimationplugin;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

/**
 * 
 * @author Sacaldur
 * 
 */

public class BlockAnimationBlockListener implements Listener {
	private final BlockAnimationPlugin plugin;

	public BlockAnimationBlockListener(BlockAnimationPlugin instance) {
		plugin = instance;
		plugin.getClass();
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(this, plugin);
	}

	@EventHandler
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

	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		EditingInformations info = plugin.getWorld(player.getWorld())
				.getEditingInformations(player);
		Block b = event.getBlock();
		Animation animation = plugin.getWorld(player.getWorld()).getAnimation(
				b.getX(), b.getY(), b.getZ());
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

	// TODO Animationen
}
