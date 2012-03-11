package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class BlockAnimationPlayerListener implements Listener {
	private final BlockAnimationPlugin plugin;

	public BlockAnimationPlayerListener(BlockAnimationPlugin instance) {
		plugin = instance;
		plugin.getClass();
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		int handitemid = player.getItemInHand().getTypeId();
		if (handitemid == Material.MINECART.getId()) { // copy&paste
																// frames
			plugin.sendMessage("unimplemented_feature", player);
		} else if (handitemid == Material.WATCH.getId()) { // set time
			EditingInformations info = plugin.getWorld(player.getWorld())
					.getEditingInformations(player);
			if (info != null) {
				Animation animation = info.getAnimation();
				if (animation != null) {
					if (event.getAction() == Action.RIGHT_CLICK_AIR
							|| event.getAction() == Action.RIGHT_CLICK_BLOCK) { // backward
																				// time
						int timespan = info.getTimeSpan();
						animation.setAnimationStep(animation.getAnimationStep()
								- timespan);
						// animation.previousAnimationStep();
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("step", Animation.durationToString(animation
								.getAnimationStep()));
						for (EditingInformations i : plugin.getWorld(
								player.getWorld()).getEditingInformations(
								animation)) {
							plugin.sendMessage("edit.info.current_step", map,
									i.getPlayer());
						}
					} else if (event.getAction() == Action.LEFT_CLICK_AIR
							|| event.getAction() == Action.LEFT_CLICK_BLOCK) { // forward
																				// time
						int timespan = info.getTimeSpan();
						animation.setAnimationStep(animation.getAnimationStep()
								+ timespan);
						// animation.nextAnimationStep();
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("step", Animation.durationToString(animation
								.getAnimationStep()));
						for (EditingInformations i : plugin.getWorld(
								player.getWorld()).getEditingInformations(
								animation)) {
							plugin.sendMessage("edit.info.current_step", map,
									i.getPlayer());
						}
					}
				}
			}
		} else if (handitemid == Material.SULPHUR.getId()) { // remove block
																// from
																// animation
			Block block = player.getTargetBlock(null, 300);
			EditingInformations info = plugin.getWorld(player.getWorld())
					.getEditingInformations(player);
			if (info != null) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR
						|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					World world = plugin.getWorld(player.getWorld());
					Animation animation = world.getAnimation(block.getX(),
							block.getY(), block.getZ());
					if (animation != null) {
						animation.removeBlock(block.getX(), block.getY(),
								block.getZ());
						plugin.sendMessage("edit.block.deleted", player);
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage();
		String[] arguments = command.split(" ");
		if (arguments[0].equalsIgnoreCase("/blan")) {
			Player player = event.getPlayer();
			boolean info = plugin.hasPermission(player, "blan.info");
			boolean control = plugin.hasPermission(player, "blan.control");
			boolean edit = plugin.hasPermission(player, "blan.edit");
			if (arguments.length >= 2) {
				String[] tmparguments = new String[arguments.length - 2];
				System.arraycopy(arguments, 2, tmparguments, 0,
						tmparguments.length);
				if (arguments[1].equalsIgnoreCase("create")
						|| arguments[1].equalsIgnoreCase("c")) {
					if (edit) {
						blan_create(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("edit")
						|| arguments[1].equalsIgnoreCase("e")) {
					if (edit) {
						blan_edit(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("remove")
						|| arguments[1].equalsIgnoreCase("r")) {
					if (edit) {
						blan_remove(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("detail")
						|| arguments[1].equalsIgnoreCase("d")) {
					if (info || control || edit) {
						blan_detail(player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("help")
						|| arguments[1].equalsIgnoreCase("h")) {
					if (info || control || edit) {
						blan_help(player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("list")
						|| arguments[1].equalsIgnoreCase("l")) {
					if (info || control || edit) {
						blan_list(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("start")) {
					if (control || edit) {
						blan_start(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("stop")) {
					if (control || edit) {
						blan_stop(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else if (arguments[1].equalsIgnoreCase("pause")) {
					if (control || edit) {
						blan_pause(tmparguments, player);
					} else {
						plugin.sendMessage("no_permission", player);
					}
				} else {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("command", arguments[1]);
					plugin.sendMessage("unknown_command", map, player);
				}
			} else {
				plugin.sendMessage("error.unknown_command", player);
			}
			event.setCancelled(true);
		} else if (arguments[0].equalsIgnoreCase("/blaned")) {
			Player player = event.getPlayer();
			if (plugin.hasPermission(player, "blan.edit")) {
				if (arguments.length >= 2) {
					String[] tmparguments = new String[arguments.length - 2];
					System.arraycopy(arguments, 2, tmparguments, 0,
							tmparguments.length);
					if (arguments[1].equalsIgnoreCase("start")) {
						blaned_start(tmparguments, player);
					} else if (arguments[1].equalsIgnoreCase("stop")) {
						blaned_stop(player);
					} else if (arguments[1].equalsIgnoreCase("save")) {
						blaned_save(player);
					} else if (arguments[1].equalsIgnoreCase("settime")
							|| arguments[1].equalsIgnoreCase("t")) {
						blaned_settime(tmparguments, player);
					} else if (arguments[1].equalsIgnoreCase("settimespan")
							|| arguments[1].equalsIgnoreCase("sts")) {
						blaned_settimespan(tmparguments, player);
					} else if (arguments[1].equalsIgnoreCase("map")
							|| arguments[1].equalsIgnoreCase("m")) {
						// blaned_map(tmparguments, player);
						plugin.sendMessage("unimplemented_feature", player);
					} else if (arguments[1].equalsIgnoreCase("unmap")
							|| arguments[1].equalsIgnoreCase("um")) {
						// blaned_unmap(tmparguments, player);
						plugin.sendMessage("unimplemented_feature", player);
					} else if (arguments[1].equalsIgnoreCase("timespan")
							|| arguments[1].equalsIgnoreCase("ts")) {
						blaned_timespan(tmparguments, player);
					} else {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("command", arguments[1]);
						plugin.sendMessage("error.edit.unknown_command", map,
								player);
					}
				} else {
					plugin.sendMessage("error.edit.no_command", player);
				}
			} else {
				plugin.sendMessage("no_permission", player);
			}
			event.setCancelled(true);
		}
	}

	private void blaned_settimespan(String[] args, Player player) {
		if (args.length > 0) {
			try {
				int timespan = Integer.parseInt(args[0]);
				if (timespan > 0) {
					EditingInformations info = plugin.getWorld(
							player.getWorld()).getEditingInformations(player);
					if (info != null) {
						info.setTimeSpan(timespan);
						// player.sendMessage("§2Erfolg§1!");
					} else {
						// TODO
					}
				} else {
					// TODO
				}
			} catch (NumberFormatException exc) {
				// TODO
			}
		} else {
			// TODO
		}
	}

	private void blaned_timespan(String[] args, Player player) {
		if (args.length > 0) {
			try {
				int timespan = Integer.parseInt(args[0]);
				EditingInformations info = plugin.getWorld(player.getWorld())
						.getEditingInformations(player);
				info.setTimeSpan(timespan);
			} catch (NumberFormatException exc) {
				plugin.sendMessage("error.edit.bad_time_span", player);
			}
		} else {
			plugin.sendMessage("error.edit.no_time_span", player);
		}
	}

	private void blaned_settime(String[] args, Player player) {
		if (args.length >= 1) {
			EditingInformations info = plugin.getWorld(player.getWorld())
					.getEditingInformations(player);
			if (info != null) {
				Animation animation = info.getAnimation();
				if (animation != null) {
					String strtime = args[0];
					Boolean relative_add = null;
					if (strtime.startsWith("-")) {
						strtime = strtime.substring(1);
						relative_add = Boolean.FALSE;
					} else if (strtime.startsWith("+")) {
						strtime = strtime.substring(1);
						relative_add = Boolean.TRUE;
					}
					try {
						int time = Animation.parseDuration(strtime);
						if (relative_add == null) {
							animation.setAnimationStep(time);
						} else {
							animation.setAnimationStep(animation
									.getAnimationStep()
									+ (relative_add.booleanValue() ? time
											: -time));
						}
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("step", "" + animation.getAnimationStep());
						for (EditingInformations i : plugin.getWorld(
								player.getWorld()).getEditingInformations(
								animation)) {
							plugin.sendMessage("edit.info.current_step", map,
									i.getPlayer());
						}
					} catch (Exception exc) {
						plugin.sendMessage("error.edit.bad_step", player);
					}
				}
			}
			plugin.sendMessage("error.edit.no_step", player);
		}
	}

	private void blaned_start(String[] args, Player player) {
		World world = plugin.getWorld(player.getWorld());
		if (args.length >= 1) {
			Animation animation = world.getAnimation(args[0]);
			if (animation != null) {
				if (world.getEditingInformations(player) != null) {
					blaned_editingstopped(player);
				}
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("animation", animation.getName());
				map.put("player", player.getName());
				for (EditingInformations info : plugin.getWorld(
						player.getWorld()).getEditingInformations(animation)) {
					plugin.sendMessage("edit.other_started", map,
							info.getPlayer());
				}
				world.addEditingInformations(player, new EditingInformations(
						player, animation));
				animation.setInConstruction(true);
				plugin.sendMessage("edit.started", map, player);
			} else {
				plugin.sendMessage("error.animation.not_found", player);
			}
		} else {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("animation", args[0]);
			plugin.sendMessage("error.no_animation", player);
		}
	}

	private void blaned_stop(Player player) {
		World world = plugin.getWorld(player.getWorld());
		EditingInformations edin = world.getEditingInformations(player);
		if (edin != null) {
			Animation animation = edin.getAnimation();
			blaned_editingstopped(player);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("animation", animation.getName());
			plugin.sendMessage("edit.stopped", map, player);
		} else {
			plugin.sendMessage("error.edit.no_editing", player);
		}
	}

	private void blaned_save(Player player) {
		EditingInformations info = plugin.getWorld(player.getWorld())
				.getEditingInformations(player);
		if (info != null) {
			Animation animation = info.getAnimation();
			if (animation != null) {
				if (animation.save()) {
					plugin.sendMessage("edit.saved", player);
				} else {
					plugin.sendMessage("error.edit.save_failed", player);
				}
			} else {
				plugin.sendMessage("error.edit.no_editing", player);
			}
		} else {
			plugin.sendMessage("error.edit.no_editing", player);
		}
	}

	private void blaned_editingstopped(Player player) {
		World world = plugin.getWorld(player.getWorld());
		EditingInformations info = world.getEditingInformations(player);
		if (info != null) {
			world.removeEditingInformations(player);
			EditingInformations[] infos = world.getEditingInformations(info
					.getAnimation());
			if (infos.length > 0) {
				for (EditingInformations i : infos) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("player", player.getName());
					map.put("animation", i.getAnimation().getName());
					plugin.sendMessage("edit.other_stopped", player);
				}
			} else {
				info.getAnimation().setInConstruction(false);
			}
		}
	}

	private void blan_stop(String[] arguments, Player player) {
		if (arguments.length >= 1) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("animation", arguments[0]);
			Animation animation = plugin.getWorld(player.getWorld())
					.getAnimation(arguments[0]);
			if (animation != null) {
				if (animation.isRunning()) {
					animation.stop();
					plugin.sendMessage("control.stopped", map, player);
				} else {
					plugin.sendMessage("error.control.not_running", map, player);
				}
			} else {
				plugin.sendMessage("error.animation.not_found", map, player);
			}
		} else {
			plugin.sendMessage("error.no_animation", player);
		}
	}

	private void blan_start(String[] arguments, Player player) {
		if (arguments.length >= 1) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("animation", arguments[0]);
			Animation animation = plugin.getWorld(player.getWorld())
					.getAnimation(arguments[0]);
			if (animation != null) {
				if (!animation.isRunning()) {
					animation.start();
					plugin.sendMessage("control.started", map, player);
				} else {
					plugin.sendMessage("error.control.still_running", map,
							player);
				}
			} else {
				plugin.sendMessage("error.animation.not_found", map, player);
			}
		} else {
			plugin.sendMessage("error.no_animation", player);
		}
	}

	private void blan_pause(String[] arguments, Player player) {
		if (arguments.length >= 1) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("animation", arguments[0]);
			Animation animation = plugin.getWorld(player.getWorld())
					.getAnimation(arguments[0]);
			if (animation != null) {
				if (animation.isRunning()) {
					animation.pause();
					plugin.sendMessage("control.paused", map, player);
				} else {
					plugin.sendMessage("error.animation.not_running", map,
							player);
				}
			} else {
				plugin.sendMessage("error.animation.not_found", map, player);
			}
		} else {
			plugin.sendMessage("error.no_animation", player);
		}
	}

	private void blan_help(Player player) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", plugin.getName());
		map.put("version", plugin.getVersion());
		plugin.sendMessage("help", map, player);
	}

	private void blan_list(String[] arguments, Player player) {
		if (arguments.length >= 1) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (arguments[0].equalsIgnoreCase("commands")
					|| arguments[0].equalsIgnoreCase("c")) {
				map.put("commands", "help, list, create, edit, remove, detail");
				plugin.sendMessage("info.list.commands", map, player);
			} else if (arguments[0].equalsIgnoreCase("animations")
					|| arguments[0].equalsIgnoreCase("a")) {
				String[] animations = plugin.getWorld(player.getWorld())
						.getAnimationNames();
				if (animations.length > 0) {
					String animationsstring = animations[0];
					for (int i = 1; i < animations.length; i++) {
						animationsstring += ", " + animations[i];
					}
					map.put("count", "" + animations.length);
					map.put("animations", animationsstring);
					plugin.sendMessage("info.list.animations", map, player);
				} else {
					plugin.sendMessage("info.list.no_animations", player);
				}
			} else {
				map.put("command", arguments[0]);
				plugin.sendMessage("error.list.bad_option", player);
			}
		} else {
			plugin.sendMessage("error.list.no_option", player);
		}
	}

	private void blan_detail(Player player) {
		// TODO detail
		plugin.sendMessage("unimplemented_feature", player);
	}

	private void blan_create(String[] arguments, Player player) {
		HashMap<String, String> map = new HashMap<String, String>();
		String name = "";
		if (arguments.length >= 1) {
			name = arguments[0];
		}
		map.put("animation", name);
		if (Animation.isName(name)) {
			if (plugin.getWorld(player.getWorld()).getAnimation(name) == null) {
				plugin.getWorld(player.getWorld()).createAnimation(name);
				plugin.sendMessage("manage.created", map, player);
			} else {
				plugin.sendMessage("error.animation.exists", map, player);
			}
		} else if (!name.equals("")) {
			plugin.sendMessage("error.animation.bad_name", player);
		} else {
			plugin.sendMessage("error.animation.no_name", player);
		}
	}

	private void blan_edit(String[] arguments, Player player) {
		if (arguments.length >= 1) {
			if (arguments[0].equalsIgnoreCase("animation")
					|| arguments[0].equalsIgnoreCase("a")) {
				// TODO edit Animation
				plugin.sendMessage("unimplemented_feature", player);
			} else {
				plugin.sendMessage("error.unknown_type", player);
			}
		} else {
			plugin.sendMessage("error.no_type", player);
		}
	}

	private void blan_remove(String[] arguments, Player player) {
		HashMap<String, String> map = new HashMap<String, String>();
		String name = "";
		if (arguments.length >= 1) {
			name = arguments[0];
		}
		if (Animation.isName(name)) {
			map.put("animation", name);
			Animation animation = plugin.getWorld(player.getWorld())
					.getAnimation(name);
			if (animation != null) {
				plugin.getWorld(player.getWorld()).removeAnimation(animation);
				plugin.sendMessage("manage.deleted", map, player);
			} else {
				plugin.sendMessage("error.animation.not_found", map, player);
			}
		} else if (!name.equals("")) {
			plugin.sendMessage("error.animation.bad_name", player);
		} else {
			plugin.sendMessage("error.animation.no_name", player);
		}
	}

/*	private void blaned_map(String[] arguments, Player player) {
		// TODO unused
		EditingInformations ei = this.plugin.getWorld(player.getWorld())
				.getEditingInformations(player);
		if (ei != null) {
			Animation animation = ei.getAnimation();
			if (animation != null) {
				if (arguments.length >= 1) {
					if (arguments.length >= 2) {
						try {
							BlockID original = new BlockID(arguments[0]);
							BlockID mapping = new BlockID(arguments[1]);
							ei.mapDisplayedBlock(original, mapping);
						} catch (NumberFormatException exc) {
							this.plugin.sendMessage("edit.mapping.mapped",
									player);
						}
					} else {
						this.plugin.sendMessage("error.mapping.no_map_blockid",
								player);
					}
				} else {
					this.plugin.sendMessage("error.mapping.no_orig_blockid",
							player);
				}
			} else {
				this.plugin.sendMessage("error.edit.no_editing", player);
			}
		} else {
			this.plugin.sendMessage("error.edit.no_editing", player);
		}
	}

	private void blaned_unmap(String[] arguments, Player player) {
		// TODO implement
	}*/
}
