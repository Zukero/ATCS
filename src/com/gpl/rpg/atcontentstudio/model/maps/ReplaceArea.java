package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;


public class ReplaceArea extends MapObject {

	public ReplaceArea(tiled.core.MapObject obj) {
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		
	}
	
}
