package net.rhocraft.rhotek.plugins.blockanimationplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XMLAccesser {
	private final String XMLDOCUMENT = "BlockAnimationPlugin-Animations.xml";
	private World world;
	private File file;

	private Logger logger = null;

	private Document document = null;

	public XMLAccesser(World world, Logger logger) {
		this.world = world;
		this.file = new File(this.world.getName(), XMLDOCUMENT);
		this.logger = logger;
	}

	public boolean createAnimation(String name) {
		if (getAnimation(name) == null) {
			Document document = openXMLDocument();
			if (document != null) {
				Element root = document.getRootElement();
				Element animationelement = new Element("animation");
				animationelement.setAttribute(new Attribute("name", name));
				animationelement
						.setAttribute(new Attribute("running", "false"));
				root.addContent(animationelement);
				saveXMLDocument(document);
				logger.info("Die Animation " + name + " wurde angelegt.");
				return true;
			} else {
				logger.severe("Das XML-Dokument zur Speicherung der Animationen konnte nicht ge�ffnet werden!");
			}
		}
		return false;
	}

	public boolean removeAnimation(String name) {
		Document document = openXMLDocument();
		if (document != null) {
			Element root = document.getRootElement();
			int id = -1;
			@SuppressWarnings("rawtypes")
			List content = root.getContent();
			for (int i = 0; i < content.size(); i++) {
				if (content.get(i) instanceof Element) {
					Element element = ((Element) content.get(i));
					if (element.getName().equals("animation")) {
						if (element.getAttributeValue("name").equals(name)) {
							id = i;
							break;
						}
					}
				}
			}
			if (id != -1) {
				if (root.removeContent(id) != null) {
					saveXMLDocument(document);
					logger.info("Die Animation " + name + " wurde gel�scht.");
					return true;
				} else {
					logger.severe("Die Animation " + name
							+ " konnte nicht entfernt werden!");
				}
			} else {
				logger.info("Die Animation "
						+ name
						+ " konnte nicht gel�scht werden, da sie nicht gefunden wurde.");
			}
		} else {
			logger.severe("Das XML-Dokument zur Speicherung der Animationen konnte nicht ge�ffnet werden!");
		}
		return false;
	}

	public boolean updateAnimation(Animation animation) {
		Document document = openXMLDocument();
		Element ae = getAnimationElement(document, animation.getName());
		ae.setAttribute("running", String.valueOf(animation.isRunning()));
		Animation.TimeMode timemode = animation.getTimeMode();
		String time;
		switch (timemode) {
		case GAMETIME:
			time = "gametime";
			break;
		case REALTIME:
			time = "realtime";
			break;
		case DURATIONS:
		default:
			time = "durations";
			break;
		}
		ae.setAttribute("time", time);
		return saveXMLDocument(document);
	}

	@SuppressWarnings("unchecked")
	public Animation getAnimation(String name) {
		Document document = openXMLDocument();
		Element animationelement = getAnimationElement(document, name);
		if (animationelement != null) {
			Animation animation = new Animation(this.world,
					animationelement.getAttributeValue("name"));
			if (animationelement.getAttributeValue("running") != null
					&& animationelement.getAttributeValue("running")
							.equalsIgnoreCase("true")) {
				animation.start();
			}
			if (animationelement.getAttributeValue("time") != null) {
				String time = animationelement.getAttributeValue("time");
				if (time.equals("gametime")) {
					animation.setTimeMode(Animation.TimeMode.GAMETIME);
				} else if (time.equals("realtime")) {
					animation.setTimeMode(Animation.TimeMode.REALTIME);
				} else {
					animation.setTimeMode(Animation.TimeMode.DURATIONS);
				}
			}
			List<Block> blockelements = animationelement.getChildren("block");
			if (blockelements != null) {
				for (Element blockelement : blockelements
						.toArray(new Element[blockelements.size()])) {
					Block block = null;
					try {
						int x = Integer.parseInt(blockelement
								.getAttributeValue("x"));
						int y = Integer.parseInt(blockelement
								.getAttributeValue("y"));
						int z = Integer.parseInt(blockelement
								.getAttributeValue("z"));
						block = new Block(x, y, z);
					} catch (Exception exc) {
						continue;
					}
					List<BlockID> blockidelements = blockelement
							.getChildren("blockid");
					if (blockidelements != null) {
						for (Element blockidelement : blockidelements
								.toArray(new Element[blockidelements.size()])) {
							int times = 1;
							try {
								times = Animation.parseDuration(blockidelement
												.getAttributeValue("time"));
							} catch (Exception exc) {
								// exc.printStackTrace();
							}
							if (times < 1) {
								times = 1;
							}
							for (int i = 0; i < times; i++) {
								try {
									int id = Integer.parseInt(blockidelement
											.getAttributeValue("id"));
									byte data = Byte.parseByte(blockidelement
											.getAttributeValue("data"));
									block.addBlockID(id, data);
								} catch (Exception exc) {

								}
							}
						}
					}
					animation.addBlock(block);
				}
			}
			return animation;
		}
		return null;
	}

	public Animation[] getAnimations() {
		String[] names = getAnimationNames();
		Animation[] animationen = new Animation[names.length];
		for (int i = 0; i < names.length; i++) {
			animationen[i] = getAnimation(names[i]);
		}
		return animationen;
	}

	public String[] getAnimationNames() {
		ArrayList<String> animationen = new ArrayList<String>();
		Document document = openXMLDocument();
		Element root = document.getRootElement();
		@SuppressWarnings("rawtypes")
		List animationelements = root.getChildren("animation");
		for (Object animationelement : animationelements) {
			animationen.add(((Element) animationelement)
					.getAttributeValue("name"));
		}
		return animationen.toArray(new String[animationen.size()]);
	}

	private Element getAnimationElement(Document document, String name) {
		Element root = document.getRootElement();
		@SuppressWarnings("rawtypes")
		List animationelements = root.getChildren("animation");
		for (Object animationelement : animationelements) {
			Element ae = (Element) animationelement;
			if (ae.getAttributeValue("name").equals(name)) {
				return ae;
			}
		}
		return null;
	}

	private Document openXMLDocument() {
		if (document != null) {
			return document;
		} else {
			if (!file.exists()) {
				document = createXMLDocument();
				return document;
			}
			try {
				return new SAXBuilder(false).build(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Document createXMLDocument() {
		Element root = new Element("animations");
		Document document = new Document(root);
		if (saveXMLDocument(document)) {
			return document;
		} else {
			logger.severe("Die Datei konnte nicht angelegt werden!");
		}
		return null;
	}

	private boolean saveXMLDocument(Document document) {
		try {
			new org.jdom.output.XMLOutputter().output(document,
					new java.io.FileOutputStream(file));
			return true;
		} catch (Exception exc) {
			logger.logp(
					Level.SEVERE,
					"com.bukkit.sacaldur.blockanimationplugin.XMLAccesser",
					"saveXMLDocument",
					"Es ist ein Fehler beim speichern der Datei "
							+ file.getAbsolutePath()
							+ " zum Speichern der Animationen aufgetreten!",
					exc);
		}
		return false;
	}

	public boolean saveAnimation(Animation animation) {
		if (animation != null) {
			Document document = openXMLDocument();
			Element animations = document.getRootElement();
			Element animationelement = this.getAnimationElement(document,
					animation.getName());
			if (animationelement != null) {
				animations.removeContent(animationelement);
			}
			animationelement = new Element("animation");
			animationelement.setAttribute("name", animation.getName());
			animationelement
					.setAttribute("running", "" + animation.isRunning());
			switch (animation.getTimeMode()) {
			case GAMETIME:
				animationelement.setAttribute("time", "gametime");
				break;
			case REALTIME:
				animationelement.setAttribute("time", "realtime");
				break;
			default:
				animationelement.setAttribute("time", "durations");
				break;
			}
			for (Block block : animation.getBlocks()) {
				if (block != null) {
					Element belement = new Element("block");
					BlockLocation location = block.getLocation();
					belement.setAttribute("x", "" + location.getX());
					belement.setAttribute("y", "" + location.getY());
					belement.setAttribute("z", "" + location.getZ());
					BlockID[] ids = block.getBlockIDs();
					for (int i = 0, n = 1; i < ids.length; i++) {
						if (i < ids.length - 1
								&& ids[i].getId() == ids[i + 1].getId()
								&& ids[i].getData() == ids[i + 1].getData()) {
							n++;
						} else {
							Element idelement = new Element("blockid");
							idelement.setAttribute("id", "" + ids[i].getId());
							idelement.setAttribute("data",
									"" + ids[i].getData());
							if(n > 1) {
								idelement.setAttribute("time", Animation.durationToString(n));
							}
							belement.addContent(idelement);
							n = 1;
						}
					}
					animationelement.addContent(belement);
				}
			}
			animations.addContent(animationelement);
			this.saveXMLDocument(document);
		}
		return false;
	}
}
