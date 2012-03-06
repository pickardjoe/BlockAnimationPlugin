package net.rhocraft.rhotek.plugins.translationplugin;

/**
 * An interface for translation plugins. The behaviour of implementations may be
 * different. The translations may are stored in a seperate file and not
 * dynamically generated. The translations may are stored temporarly in a cache.
 */
public interface ITranslationPlugin {

	/**
	 * Returns the translation for the given text name or <code>null</code>.
	 * 
	 * @param plugin
	 *            the name of plugin which calls this method (without package
	 *            name)
	 * @param language
	 *            the target languages language code
	 * @param name
	 *            the text name
	 * @return the translation or <code>null</code>.
	 */
	public abstract String getTextTranslation(String plugin, String name);
}
