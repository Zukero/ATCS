package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.Map;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;


public class ReplaceArea extends MapObject {
	
	public Map<String, String> replacedLayers = null;

	public ReplaceArea(tiled.core.MapObject obj) {
		for (Object s : obj.getProperties().keySet()) {
			if (replacedLayers == null) replacedLayers = new LinkedHashMap<String, String>();
			replacedLayers.put(s.toString(), obj.getProperties().getProperty(s.toString()));
		}
		
	}

	@Override
	public void link() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Image getIcon() {
		return DefaultIcons.getReplaceIcon();
	}
	
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void savePropertiesInTmxObject(tiled.core.MapObject tmxObject) {
		if (replacedLayers != null) {
			for(String s : replacedLayers.keySet())
			tmxObject.getProperties().setProperty(s, replacedLayers.get(s));
		}
	}
	
}
