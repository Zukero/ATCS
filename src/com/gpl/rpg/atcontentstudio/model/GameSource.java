package com.gpl.rpg.atcontentstudio.model;

import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import com.gpl.rpg.atcontentstudio.model.gamedata.GameDataSet;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMapSet;
import com.gpl.rpg.atcontentstudio.model.maps.Worldmap;
import com.gpl.rpg.atcontentstudio.model.maps.WorldmapSegment;
import com.gpl.rpg.atcontentstudio.model.sprites.SpriteSheetSet;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class GameSource implements ProjectTreeNode, Serializable {

	private static final long serialVersionUID = -1512979360971918158L;
	
	public transient GameDataSet gameData;
	public transient TMXMapSet gameMaps;
	public transient SpriteSheetSet gameSprites;
	public transient Worldmap worldmap;
	private transient SavedSlotCollection v;
	
	public static enum Type {
		source,
		referenced,
		altered,
		created
	}
	
	public File baseFolder;
	public Type type;
	
	public transient Project parent = null;
	
	public GameSource(File folder, Project parent) {
		this.parent = parent;
		this.baseFolder = folder;
		this.type = Type.source;
		initData();
	}
	
	public GameSource(Project parent, Type type) {
		this.parent = parent;
		this.baseFolder = new File(parent.baseFolder, type.toString());
		this.type = type;
		initData();
	}
	
	public void refreshTransients(Project p) {
		parent = p;
		initData();
	}

	public void initData() {
		this.gameData = new GameDataSet(this);
		this.gameMaps = new TMXMapSet(this);
		this.gameSprites = new SpriteSheetSet(this);
		this.worldmap = new Worldmap(this);
		v = new SavedSlotCollection();
		v.add(gameData);
		v.add(gameMaps);
		v.add(gameSprites);
		v.add(worldmap);
	}
	
	@Override
	public Enumeration<ProjectTreeNode> children() {
		return v.getNonEmptyElements();
	}
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	@Override
	public TreeNode getChildAt(int arg0) {
		return v.getNonEmptyElementAt(arg0);
	}
	@Override
	public int getChildCount() {
		return v.getNonEmptySize();
	}
	@Override
	public int getIndex(TreeNode arg0) {
		return v.getNonEmptyIndexOf((ProjectTreeNode) arg0);
	}
	@Override
	public TreeNode getParent() {
		return parent;
	}
	@Override
	public boolean isLeaf() {
		return false;
	}
	@Override
	public void childrenAdded(List<ProjectTreeNode> path) {
		path.add(0, this);
		parent.childrenAdded(path);
	}
	@Override
	public void childrenChanged(List<ProjectTreeNode> path) {
		path.add(0, this);
		parent.childrenChanged(path);
	}
	@Override
	public void childrenRemoved(List<ProjectTreeNode> path) {
		if (path.size() == 1 && this.v.getNonEmptySize() == 1) {
			childrenRemoved(new ArrayList<ProjectTreeNode>());
		} else {
			path.add(0, this);
			parent.childrenRemoved(path);
		}
	}
	@Override
	public void notifyCreated() {
		childrenAdded(new ArrayList<ProjectTreeNode>());
		for (ProjectTreeNode node : v.getNonEmptyIterable()) {
			node.notifyCreated();
		}
	}
	@Override
	public String getDesc() {
		switch(type) {
		case altered: return "Altered data";
		case created: return "Created data";
		case referenced: return "Referenced data";
		case source: return "AT Source"; //The fact that it is from "source" is already mentionned by its parent.
		default: return "Game data";
		}
	}
	
	
	@Override
	public Project getProject() {
		return parent == null ? null : parent.getProject();
	}

	public Image getIcon(String iconId) {
		String[] data = iconId.split(":");
		for (Spritesheet sheet : gameSprites.spritesheets) {
			if (sheet.id.equals(data[0])) {
				return sheet.getIcon(Integer.parseInt(data[1]));
			}
		}
		return null;
	}
	
	public Image getImage(String iconId) {
		String[] data = iconId.split(":");
		for (Spritesheet sheet : gameSprites.spritesheets) {
			if (sheet.id.equals(data[0])) {
				return sheet.getImage(Integer.parseInt(data[1]));
			}
		}
		return null;
	}
		

	@Override
	public Image getIcon() {
		return getOpenIcon();
	}
	@Override
	public Image getClosedIcon() {
		return DefaultIcons.getATClosedIcon();
	}
	@Override
	public Image getLeafIcon() {
		return DefaultIcons.getATClosedIcon();
	}
	@Override
	public Image getOpenIcon() {
		return DefaultIcons.getATOpenIcon();
	}
	@Override
	public GameDataSet getDataSet() {
		return null;
	}
	

	@Override
	public Type getDataType() {
		return type;
	}
	
	@Override
	public boolean isEmpty() {
		return v.isEmpty();
	}

	public WorldmapSegment getWorldmapSegment(String id) {
		return worldmap.getWorldmapSegment(id);
	}
}
