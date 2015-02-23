package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.gamedata.NPC;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class SpawnArea extends MapObject {

	public int quantity = 1;
	public int spawnchance = 10;
	public List<NPC> spawnGroup = new ArrayList<NPC>();
	
	public SpawnArea(tiled.core.MapObject obj) {
		if (obj.getProperties().getProperty("quantity") != null) {
			this.quantity = Integer.parseInt(obj.getProperties().getProperty("quantity"));
		}
		if (obj.getProperties().getProperty("spawnchance") != null) {
			this.spawnchance = Integer.parseInt(obj.getProperties().getProperty("spawnchance"));
		}
	}

	@Override
	public void link() {
		if (name != null) {
			spawnGroup = parentMap.getProject().getSpawnGroup(name);
		} else {
			spawnGroup = new ArrayList<NPC>();
		}
		if (spawnGroup != null) {
			for (NPC npc : spawnGroup) {
				npc.addBacklink(parentMap);
			}
		}
	}
	
	@Override
	public Image getIcon() {
		if (spawnGroup != null && !spawnGroup.isEmpty()) {
			return spawnGroup.get(0).getIcon();
		}
		return DefaultIcons.getNullifyIcon();
	}
	
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		int replacedIndex = -1;
		for (NPC npc : spawnGroup) {
			if (npc == oldOne) {
				replacedIndex = spawnGroup.indexOf(npc);
			}
		}
		if (replacedIndex >= 0) {
			spawnGroup.set(replacedIndex, (NPC) newOne);
			newOne.addBacklink(parentMap);
		}
	}
	
	@Override
	public void savePropertiesInTmxObject(tiled.core.MapObject tmxObject) {
		if (quantity != 1) {
			tmxObject.getProperties().setProperty("quantity", Integer.toString(quantity));
		}
		if (spawnchance != 10) {
			tmxObject.getProperties().setProperty("spawnchance", Integer.toString(spawnchance));
		}
	}
	
}
