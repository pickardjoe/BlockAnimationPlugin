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

import net.rhocraft.rhotek.plugins.blockanimationplugin.World;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * 
 * @author Sacaldur
 * 
 */

public class AnimationScheduler extends Thread {
	private World world;

	public AnimationScheduler(World world) {
		this.world = world;

		for (Animation animation : this.world.getAnimations()) {
			drawAnimation(animation);
		}
	}

	@Override
	public void run() {
		for (Animation animation : this.world.getAnimations()) {
			if (animation.isRunning() && !animation.isInConstruction()) {
				animation.nextAnimationStep();
			}
			int time = animation.getAnimationStep();
			EditingInformations[] eis = null;
			if (animation.isInConstruction()) {
				eis = animation.getWorld().getEditingInformations(animation);
			}
			for (net.rhocraft.rhotek.plugins.blockanimationplugin.Block block : animation
					.getBlocks()) {
				BlockID id = block.getBlockID(time);
				BlockLocation location = block.getLocation();
				Block mcblock = world.getWorld().getBlockAt(location.getX(),
						location.getY(), location.getZ());
				try {
					if (id != null) {
						mcblock.setTypeId(id.getId());
						mcblock.setData(id.getData());
						if (eis != null) {
							for (EditingInformations ei : eis) {
								if (ei.getMappedDisplayedBlock(id) != null) {
									ei.getPlayer()
											.sendBlockChange(
													new Location(animation
															.getWorld()
															.getWorld(), block
															.getLocation()
															.getX(), block
															.getLocation()
															.getY(), block
															.getLocation()
															.getZ()),
													id.getId(), id.getData());
									System.out.println("TEST");
								}
							}
						}
					} else {
						mcblock.setTypeId(0);
						mcblock.setData((byte) 0);
					}
				} catch (Exception exc) {

				}
			}
		}
	}

	private void drawAnimation(Animation animation) {
		for (net.rhocraft.rhotek.plugins.blockanimationplugin.Block block : animation
				.getBlocks()) {
			BlockID id = block.getBlockID(animation.getAnimationStep());
			BlockLocation location = block.getLocation();
			Block mcblock = world.getWorld().getBlockAt(location.getX(),
					location.getY(), location.getZ());
			// if (!(block.getTypeId() == data && block.getData() == damage)) {
			try {
				mcblock.setTypeId(id.getId());
				mcblock.setData(id.getData());
			} catch (Exception exc) {
			}
			// }
		}
	}
}
