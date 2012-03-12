package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.rhocraft.rhotek.plugins.translationplugin.ITranslationPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class BlockAnimationPlugin extends JavaPlugin {
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

	private String version;
	private String name;

	private HashMap<org.bukkit.World, World> worlds = new HashMap<org.bukkit.World, World>();

	private ITranslationPlugin translationplugin;
	private PermissionHandler permissionhandler;

	public BlockAnimationPlugin() {
		super();
	}

	public void onEnable() {
		setupPermissions();
		PluginDescriptionFile pdfFile = this.getDescription();
		new BlockAnimationBlockListener(this);
		new BlockAnimationPlayerListener(this);
		this.version = pdfFile.getVersion();
		this.name = pdfFile.getName();
		
		for (org.bukkit.World world : this.getServer().getWorlds()) {
			worlds.put(world, new World(this, world));
		}
		this.getServer().getLogger().info(this.name + " version " + this.version + " is enabled!");
	}

	private void setupPermissions() {
		if (permissionhandler == null) {
			Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
			if (permissionsPlugin != null) {
				permissionhandler = ((Permissions) permissionsPlugin).getHandler();
			}
		}
	}

	public void onDisable() {
		// Animation.save(); TODO
		PluginDescriptionFile pdfFile = this.getDescription();
		translationplugin = null;
		permissionhandler = null;
		this.getServer().getLogger()
				.info(pdfFile.getName() + " version " + pdfFile.getVersion()
						+ " is disabled!");
	}

	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return true;
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, Boolean.valueOf(value));
	}

	private ITranslationPlugin getTranslationPlugin(PluginManager pm) {
		for (Plugin plugin : pm.getPlugins()) {
			try {
				if (plugin.isEnabled()) {
					return (ITranslationPlugin) plugin;
				}
			} catch (Exception exc) {
			}
		}
		return null;
	}

	public ITranslationPlugin getTranslationPlugin() {
		if (this.translationplugin != null) {
			return this.translationplugin;
		} else {
			this.translationplugin = getTranslationPlugin(getServer().getPluginManager());
			return this.translationplugin;
		}
	}

	private String getTranslation(String name) {
		ITranslationPlugin translation = getTranslationPlugin();
		if (translation != null) {
			return translation.getTextTranslation("BlockAnimationPlugin", name);
		}
		return name;
	}

	private String getTranslation(String name, HashMap<String, String> values) {
		String text = getTranslation(name);
		if (text != null) {
			if (values != null) {
				for (String key : values.keySet()) {
					text = text
							.replaceAll("%%%" + key + "%%%", values.get(key));
				}
			}
		} else {
			text = name;
			if (values != null && values.size() > 0) {
				text += ":";
				for (String key : values.keySet()) {
					text += " " + key + "=" + values.get(key);
				}
			}
		}
		return text;
	}

	public String getVersion() {
		return this.version;
	}

	public String getNameR() {
		return this.name;
	}

	public World getWorld(org.bukkit.World world) {
		return worlds.get(world);
	}

	public void sendMessage(String messgename, Player... player) {
		sendMessage(messgename, null, player);
	}

	public void sendMessage(String messgename, HashMap<String, String> values,
			Player... players) {
		String translation;
		translation = getTranslation(messgename, values);
		String[] lines = translation.split("\n");
		for (Player player : players) {
			for (String line : lines) {
				if (!line.trim().isEmpty()) {
					player.sendMessage(line);
				}
			}
		}
	}

	public boolean hasPermission(Player player, String permission) {
		if (permissionhandler != null) {
			return permissionhandler.has(player, permission) || player.isOp();
		} else {
			return player.isOp();
		}
	}
}
