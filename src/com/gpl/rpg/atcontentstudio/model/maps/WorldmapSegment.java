package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.SaveEvent;
import com.gpl.rpg.atcontentstudio.model.gamedata.GameDataSet;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class WorldmapSegment extends GameDataElement {

	private static final long serialVersionUID = 2658610076889592723L;
	
	public int segmentX;
	public int segmentY;
	public Map<String, Point> mapLocations = new LinkedHashMap<String, Point>();
	public Map<String, List<String>> labelledMaps = new LinkedHashMap<String, List<String>>();
	public Map<String, NamedArea> labels = new LinkedHashMap<String, NamedArea>();
	public Element xmlNode;
	
	public WorldmapSegment(Worldmap parent, String name, Element xmlNode) {
		this.parent = parent;
		this.id = name;
		this.xmlNode = xmlNode;
	}
	
	@Override
	public GameDataSet getDataSet() {
		return parent.getDataSet();
	}

	@Override
	public String getDesc() {
		return id;
	}

	@Override
	public void parse() {
		segmentX = Integer.parseInt(xmlNode.getAttribute("x"));
		segmentY = Integer.parseInt(xmlNode.getAttribute("y"));
		NodeList mapsList = xmlNode.getElementsByTagName("map");
		for (int j = 0; j < mapsList.getLength(); j++) {
			Element mapNode = (Element) mapsList.item(j);
			mapLocations.put(mapNode.getAttribute("id"), new Point(Integer.parseInt(mapNode.getAttribute("x")) - segmentX, Integer.parseInt(mapNode.getAttribute("y")) - segmentY));
			String area;
			if ((area = mapNode.getAttribute("area")) != null && !"".equals(area)) {
				if (labelledMaps.get(area) == null) {
					labelledMaps.put(area, new LinkedList<String>());
				}
				labelledMaps.get(area).add(mapNode.getAttribute("id"));
			}
		}
		NodeList namedAreasNodeList = xmlNode.getElementsByTagName("namedarea");
		for (int j = 0; j < namedAreasNodeList.getLength(); j++) {
			Element namedAreaNode = (Element) namedAreasNodeList.item(j);
			labels.put(namedAreaNode.getAttribute("id"), new NamedArea(namedAreaNode.getAttribute("id"), namedAreaNode.getAttribute("name"), namedAreaNode.getAttribute("type")));
		}
		this.state = State.parsed;
	}

	@Override
	public void link() {
		if (this.state == State.init) {
			this.parse();
		} else if (this.state == State.linked) {
			return;
		}
		for (String mapName : mapLocations.keySet()) {
			getProject().getMap(mapName).addBacklink(this);
		}
	}

	@Override
	public WorldmapSegment clone() {
		WorldmapSegment clone = new WorldmapSegment((Worldmap)parent, id, (Element) xmlNode.cloneNode(true));
		
		return clone;
	}

	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		oldOne.removeBacklink(this);
		newOne.addBacklink(this);
	}

	@Override
	public String getProjectFilename() {
		return "worldmap.xml";
	}

	@Override
	public void save() {
		((Worldmap)parent).save();
	}
	
	public Element toXmlElement(Document doc) {
		Element element = doc.createElement("segment");
		element.setAttribute("id", id);
		element.setAttribute("x", Integer.toString(segmentX));
		element.setAttribute("y", Integer.toString(segmentY));
		
		for (String s : mapLocations.keySet()) {
			Element map = doc.createElement("map");
			map.setAttribute("id", s);
			map.setAttribute("x", Integer.toString(mapLocations.get(s).x + segmentX));
			map.setAttribute("y", Integer.toString(mapLocations.get(s).y + segmentY));
			for (String label : labelledMaps.keySet()) {
				if (labelledMaps.get(label).contains(s)) {
					map.setAttribute("area", label);
				}
			}
			element.appendChild(map);
		}
		
		for (NamedArea area : labels.values()) {
			Element namedArea = doc.createElement("namedarea");
			namedArea.setAttribute("id", area.id);
			namedArea.setAttribute("name", area.name);
			namedArea.setAttribute("type", area.type);
			element.appendChild(namedArea);
		}
		
		return element;
	}
	

	@Override
	public List<SaveEvent> attemptSave() {
		// TODO Auto-generated method stub
		save();
		return null;
	}

	public static class NamedArea {
		public String id;
		public String name;
		public String type;
		
		public NamedArea(String id, String name, String type) {
			this.id = id;
			this.name = name;
			this.type = type;
		}
	}
	
	@Override
	public Image getIcon() {
		return DefaultIcons.getUIMapIcon();
	}
	@Override
	public Image getLeafIcon() {
		return DefaultIcons.getUIMapIcon();
	}
	
}
