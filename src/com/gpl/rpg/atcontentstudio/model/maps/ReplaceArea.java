package com.gpl.rpg.atcontentstudio.model.maps;

import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.gamedata.Requirement;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;


public class ReplaceArea extends MapObject {

	public Requirement requirement = null;
	public boolean oldSchoolRequirement = true;
	
	public List<ReplaceArea.Replacement> replacements = null;

	public ReplaceArea(tiled.core.MapObject obj) {
//		String requireType = obj.getProperties().getProperty("requireType");
		String requireId = obj.getProperties().getProperty("requireId");
		String requireValue = obj.getProperties().getProperty("requireValue");
		if (requireId == null) {
			String[] fields = obj.getName().split(":");
			if (fields.length == 2) {
//				requireType = Requirement.RequirementType.questProgress.toString();
				requireValue = fields[1];
				requireId = fields[0];
			} /*else if (fields.length == 3) {
				requireValue = fields[2];
				requireType = fields[0];
				requireId = fields[1];
			}*/
			oldSchoolRequirement = true;
		} else {
			oldSchoolRequirement = false;
		}
		requirement = new Requirement();
		//Replace areas only support questProgress requirements ATM
		//requirement.type = Requirement.RequirementType.valueOf(requireType);
		requirement.type = Requirement.RequirementType.questProgress;
		requirement.required_obj_id = requireId;
		if (requireValue != null) requirement.required_value = Integer.parseInt(requireValue);
		requirement.state = GameDataElement.State.parsed;
		
		
		for (Object s : obj.getProperties().keySet()) {
			if (replacements == null) replacements = new LinkedList<ReplaceArea.Replacement>();
			replacements.add(new Replacement(s.toString(), obj.getProperties().getProperty(s.toString())));
		}
		
	}

	@Override
	public void link() {
		requirement.parent = parentMap;
		requirement.link();
	}
	
	@Override
	public Image getIcon() {
		return DefaultIcons.getReplaceIcon();
	}
	
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		requirement.elementChanged(oldOne, newOne);
	}
	
	public ReplaceArea.Replacement addReplacement(String source, String target) {
		Replacement repl = new Replacement(source, target); 
		addReplacement(repl);
		return repl;
	}
	
	public void addReplacement(ReplaceArea.Replacement repl) {
		if (replacements == null) replacements = new LinkedList<ReplaceArea.Replacement>();
		replacements.add(repl);
	}
	
//	public void removeReplacement(String source, String target) {
//		replacedLayers.remove(source);
//	}
	
	public void removeReplacement(Replacement repl) {
		replacements.remove(repl);
	}
	
	@Override
	public void savePropertiesInTmxObject(tiled.core.MapObject tmxObject) {
		if (replacements != null) {
			for(Replacement r : replacements)
			tmxObject.getProperties().setProperty(r.sourceLayer, r.targetLayer);
		}
		if (requirement != null) {
			if (oldSchoolRequirement && Requirement.RequirementType.questProgress.equals(requirement.type) && (requirement.negated == null || !requirement.negated)) {
				tmxObject.setName(requirement.required_obj_id+":"+Integer.toString(requirement.required_value));
			} else {
				tmxObject.getProperties().setProperty("requireType", requirement.type.toString());
				if (requirement.required_obj != null) {
					tmxObject.getProperties().setProperty("requireId", requirement.required_obj.id);
				} else if (requirement.required_obj_id != null) {
					tmxObject.getProperties().setProperty("requireId", requirement.required_obj_id);
				}
				if (requirement.required_value != null) {
					tmxObject.getProperties().setProperty("requireValue", requirement.required_value.toString());
				}
			}
		}
	}

	//Don't use yet !
	public void updateNameFromRequirementChange() {
		if (oldSchoolRequirement && Requirement.RequirementType.questProgress.equals(requirement.type) && (requirement.negated == null || !requirement.negated)) {
			name = requirement.required_obj_id+":"+Integer.toString(requirement.required_value);
		} else if (oldSchoolRequirement) {
			int i = 0;
			String futureName = requirement.type.toString() + "#" + Integer.toString(i);
			while (parentMap.getMapObject(futureName) != null) {
				i++;
				futureName = requirement.type.toString() + "#" + Integer.toString(i);
			}
			this.name = futureName;
		}
	}
	
	public class Replacement {
		public String sourceLayer, targetLayer;
		public Replacement(String source, String target) {
			this.sourceLayer = source;
			this.targetLayer = target;
		}
	}

	public boolean hasReplacementFor(String name) {
		if (name == null) return false;
		for (Replacement repl : replacements) {
			if (name.equalsIgnoreCase(repl.sourceLayer)) return true;
		}
		return false;
	}
}
