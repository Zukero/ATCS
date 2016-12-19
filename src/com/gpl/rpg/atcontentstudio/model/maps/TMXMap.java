package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreeNode;

import tiled.io.TMXMapReader;
import tiled.io.TMXMapWriter;

import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.GameSource;
import com.gpl.rpg.atcontentstudio.model.GameSource.Type;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.SaveEvent;
import com.gpl.rpg.atcontentstudio.model.gamedata.GameDataSet;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class TMXMap extends GameDataElement {

	private static final long serialVersionUID = 1609502879500898837L;
	
	public static final String GROUND_LAYER_NAME = "Ground";
	public static final String OBJECTS_LAYER_NAME = "Objects";
	public static final String ABOVE_LAYER_NAME = "Above";
	public static final String WALKABLE_LAYER_NAME = "Walkable";
	
	public enum ColorFilter {
		black20,
		black40,
		black60,
		black80,
		invert,
		bw,
		redtint,
		greentint,
		bluetint
	}
	
	public File tmxFile = null;
	public tiled.core.Map tmxMap = null;
	public Set<Spritesheet> usedSpritesheets = null;
	public List<MapObjectGroup> groups = null;
	
	public ProjectTreeNode parent;
	public Integer outside = null;
	public ColorFilter colorFilter = null;

	public boolean writable = false;

	public TMXMap(TMXMapSet parent, File f) {
		this.parent = parent;
		this.tmxFile = f;
		String name = f.getName();
		id = name.substring(0, name.length() - 4);
	}
	
	public void parse() {
		if (this.state == GameDataElement.State.init) {
			if (tmxMap != null) return;
			usedSpritesheets = new HashSet<Spritesheet>();
			try {
				tmxMap = new TMXMapReader().readMap(tmxFile.getAbsolutePath(), this);
				if (tmxMap.getProperties().get("outdoors") != null) {
					outside = new Integer(((String) tmxMap.getProperties().get("outdoors")));
				}
				if (tmxMap.getProperties().get("colorfilter") != null) {
					colorFilter = ColorFilter.valueOf(((String) tmxMap.getProperties().get("colorfilter")));
				}
			} catch (FileNotFoundException e) {
				Notification.addError("Impossible to load TMX map file "+tmxFile.getAbsolutePath());
			} catch (Exception e) {
				Notification.addError("Error while loading TMX map file "+tmxFile.getAbsolutePath()+": "+e.getMessage());
				e.printStackTrace();
			}
			for (tiled.core.MapLayer layer : tmxMap.getLayers()) {
				if (layer instanceof tiled.core.ObjectGroup) {
					if (groups == null) {
						groups = new ArrayList<MapObjectGroup>();
					}
					MapObjectGroup group = new MapObjectGroup((tiled.core.ObjectGroup) layer, this);
					groups.add(group);
				}
			}
			for (Spritesheet s : usedSpritesheets) {
				s.addBacklink(this);
			}
			state = State.parsed;
		}
	}
	
	public void create() {
		if (tmxMap != null) return;
		tmxMap = new tiled.core.Map(30, 30);
	}
	
	public TMXMap clone() {
		TMXMap clone = new TMXMap((TMXMapSet) this.parent, this.tmxFile);
		try {
			clone.usedSpritesheets = new HashSet<Spritesheet>();
			clone.tmxMap = new TMXMapReader().readMap(new StringReader(this.toXml()), clone);
			if (clone.tmxMap.getProperties().get("outdoors") != null) {
				clone.outside = new Integer(((String) clone.tmxMap.getProperties().get("outdoors")));
			}
			if (clone.tmxMap.getProperties().get("colorfilter") != null) {
				clone.colorFilter = ColorFilter.valueOf(((String) tmxMap.getProperties().get("colorfilter")));
			}
			for (tiled.core.MapLayer layer : clone.tmxMap.getLayers()) {
				if (layer instanceof tiled.core.ObjectGroup) {
					if (clone.groups == null) {
						clone.groups = new ArrayList<MapObjectGroup>();
					}
					MapObjectGroup group = new MapObjectGroup((tiled.core.ObjectGroup) layer, this);
					clone.groups.add(group);
				}
			}
			for (Spritesheet s : usedSpritesheets) {
				s.addBacklink(clone);
			}
		} catch (Exception e) {
			Notification.addError("Error while cloning map "+this.id+" : "+e.getMessage());
			e.printStackTrace();
		}
		
		return clone;
	}
	
	@Override
	public Enumeration<ProjectTreeNode> children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode arg0) {
		return 0;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public void childrenAdded(List<ProjectTreeNode> path) {
		path.add(0,this);
		parent.childrenAdded(path);
	}

	@Override
	public void childrenChanged(List<ProjectTreeNode> path) {
		path.add(0,this);
		parent.childrenChanged(path);
	}

	@Override
	public void childrenRemoved(List<ProjectTreeNode> path) {
		path.add(0,this);
		parent.childrenRemoved(path);
	}
	@Override
	public void notifyCreated() {
		childrenAdded(new ArrayList<ProjectTreeNode>());
	}
	@Override
	public String getDesc() {
		return (this.state == State.modified ? "*" : "")+id;
	}
	
	@Override
	public Project getProject() {
		return parent.getProject();
	}

	@Override
	public Image getIcon() {
		return DefaultIcons.getTiledIconIcon();
	}
	@Override
	public Image getLeafIcon() {
		return DefaultIcons.getTiledIconIcon();
	}
	@Override
	public Image getClosedIcon() {return null;}
	@Override
	public Image getOpenIcon() {return null;}
	
	@Override
	public GameDataSet getDataSet() {
		return null;
	}
	
	@Override
	public Type getDataType() {
		return parent.getDataType();
	}

	public String toXml() {
		if (outside != null && outside == 1) {
			tmxMap.getProperties().put("outdoors", Integer.toString(outside));
		} else {
			tmxMap.getProperties().remove("outdoors");
		}
		if (colorFilter != null) {
			tmxMap.getProperties().put("colorfilter", colorFilter.toString());
		} else {
			tmxMap.getProperties().remove("colorfilter");
		}
		
		for (MapObjectGroup group : groups) {
			group.pushBackToTiledProperties();
			if (!tmxMap.containsLayer(group.tmxGroup)) {
				tmxMap.addLayer(group.tmxGroup);
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			TMXMapWriter writer = new TMXMapWriter();
			writer.settings.layerCompressionMethod = TMXMapWriter.Settings.LAYER_COMPRESSION_METHOD_ZLIB;
			if (getDataType() == GameSource.Type.source) {
				writer.writeMap(tmxMap, baos, tmxFile.getAbsolutePath());
			} else {
				writer.writeMap(tmxMap, baos, getProject().baseContent.gameMaps.mapFolder.getAbsolutePath()+File.separator+"placeholder.tmx");
			}
		} catch (Exception e) {
			Notification.addError("Error while converting map "+getDesc()+" to XML: "+e.getMessage());
			e.printStackTrace();
		}
		return baos.toString();
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}

	public void save() {
		if (writable) {
			try {
				FileWriter w = new FileWriter(tmxFile);
				w.write(toXml());
				w.close();
				this.state = State.saved;
				Notification.addSuccess("TMX file "+tmxFile.getAbsolutePath()+" saved.");
			} catch (IOException e) {
				Notification.addError("Error while writing TMX file "+tmxFile.getAbsolutePath()+" : "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public List<SaveEvent> attemptSave() {
		//TODO check cases where map should be moved from altered/created to created/altered....
		save();
		return null;
	}

	public void delete() {
		if (writable) {
			if (tmxFile.exists()) {
				if (tmxFile.delete()) {
					Notification.addSuccess("TMX file "+tmxFile.getAbsolutePath()+" deleted.");
				} else {
					Notification.addError("Error while deleting TMX file "+tmxFile.getAbsolutePath());
				}
			}
			((TMXMapSet)parent).tmxMaps.remove(this);
			//TODO clear blacklinks ?
		}
	}

	@Override
	public void link() {
		if (this.state == GameDataElement.State.init) {
			parse();
		}
		if (this.state == GameDataElement.State.parsed || this.state == GameDataElement.State.created) {
			for (MapObjectGroup group : groups) {
				group.link();
			}
		}
	}

	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		for (MapObjectGroup group : groups) {
			group.elementChanged(oldOne, newOne);
		}
	}

	@Override
	public String getProjectFilename() {
		return tmxFile.getName();
	}

	public void addLayer(tiled.core.MapLayer layer) {
		tmxMap.addLayer(layer);
		if (layer instanceof tiled.core.ObjectGroup) {
			groups.add(new MapObjectGroup((tiled.core.ObjectGroup) layer, this));
		}
	}

	public void removeLayer(tiled.core.MapLayer layer) {
		tmxMap.removeLayer(tmxMap.getLayerIndex(layer));
		if (layer instanceof tiled.core.ObjectGroup) {
			MapObjectGroup toRemove = null;
			for (MapObjectGroup group : groups) {
				if (group.tmxGroup == layer) {
					toRemove = group;
				}
			}
			if (toRemove != null) {
				groups.remove(toRemove);
			}
		}
	}

	public MapObjectGroup getGroup(tiled.core.ObjectGroup selectedLayer) {
		for (MapObjectGroup group : groups) {
			if (group.tmxGroup == selectedLayer) {
				return group;
			}
		}
		return null;
	}

	public List<String> getMapchangesNames() {
		List<String> result = new ArrayList<String>();
		result.add(null);
		for (MapObjectGroup group : groups) {
			for (MapObject obj : group.mapObjects) {
				if (obj.type == MapObject.Types.mapchange) {
					result.add(obj.name);
				}
			}
		}
		return result;
	}
	
	public MapObject getMapObject(String name) {
		MapObject result = null;
		for (MapObjectGroup group : groups) {
			for (MapObject obj : group.mapObjects) {
				if (obj.name.equals(name)) {
					result = obj;
					break;
				}
			}
		}
		return result;
	}
	
	public static boolean isPaintedLayerName(String name) {
		return GROUND_LAYER_NAME.equalsIgnoreCase(name) ||
				OBJECTS_LAYER_NAME.equalsIgnoreCase(name) ||
				ABOVE_LAYER_NAME.equalsIgnoreCase(name) ||
				WALKABLE_LAYER_NAME.equalsIgnoreCase(name);
	}
	
}
