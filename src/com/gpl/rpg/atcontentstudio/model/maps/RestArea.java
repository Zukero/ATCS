package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;


public class RestArea extends MapObject {

	public RestArea(tiled.core.MapObject obj) {}

	@Override
	public void link() {}
	
	@Override
	public Image getIcon() {
		return DefaultIcons.getRestIcon();
	}
	
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		
	}
	
	@Override
	public void savePropertiesInTmxObject(tiled.core.MapObject tmxObject) {
		
	}
	
}
