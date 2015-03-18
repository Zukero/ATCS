package com.gpl.rpg.atcontentstudio.model.gamedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;

public class Requirement extends JSONElement {

	private static final long serialVersionUID = 7295593297142310955L;
	
	private static Map<RequirementType, List<RequirementType>> COMPATIBLE_TYPES = new HashMap<RequirementType, List<RequirementType>>();
	
	static {
		List<RequirementType> questTypes = new ArrayList<RequirementType>();
		questTypes.add(RequirementType.questProgress);
		questTypes.add(RequirementType.questLatestProgress);
		COMPATIBLE_TYPES.put(RequirementType.questProgress, questTypes);
		COMPATIBLE_TYPES.put(RequirementType.questLatestProgress, questTypes);
		
		List<RequirementType> countedItemTypes = new ArrayList<RequirementType>();
		countedItemTypes.add(RequirementType.inventoryRemove);
		countedItemTypes.add(RequirementType.inventoryKeep);
		countedItemTypes.add(RequirementType.usedItem);
		COMPATIBLE_TYPES.put(RequirementType.inventoryRemove, countedItemTypes);
		COMPATIBLE_TYPES.put(RequirementType.inventoryKeep, countedItemTypes);
		COMPATIBLE_TYPES.put(RequirementType.usedItem, countedItemTypes);
		
	}
	
	//Available from parsed state
	public RequirementType type = null;
	public String required_obj_id = null;
	public Integer required_value = null;
	public Boolean negated = null;
	
	//Available from linked state
	public GameDataElement required_obj = null;
	
	public enum RequirementType {
		questProgress,
		questLatestProgress,
		inventoryRemove,
		inventoryKeep,
		wear,
		skillLevel,
		killedMonster,
		timerElapsed,
		usedItem,
		spentGold,
		consumedBonemeals,
		hasActorCondition,
		removeQuestProgress
	}
	
	@Override
	public String getDesc() {
		return ((negated != null && negated) ? "NOT " : "")+required_obj_id+(required_value == null ? "" : ":"+required_value.toString());
	}

	@Override
	public void parse() {
		throw new Error("Thou shalt not reach this method.");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map toJson() {
		throw new Error("Thou shalt not reach this method.");
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void parse(Map jsonObj) {
		throw new Error("Thou shalt not reach this method.");	
	}

	@Override
	public void link() {
		if (this.state == State.created || this.state == State.modified || this.state == State.saved) {
			//This type of state is unrelated to parsing/linking.
			return;
		}
		if (this.state == State.init) {
			//Not parsed yet.
			this.parse();
		} else if (this.state == State.linked) {
			//Already linked.
			return;
		}
		Project proj = getProject();
		if (proj == null) {
			Notification.addError("Error linking requirement "+getDesc()+". No parent project found.");
			return;
		}
		switch (type) {
		case hasActorCondition:
			this.required_obj = proj.getActorCondition(required_obj_id);
			break;
		case inventoryKeep:
		case inventoryRemove:
		case usedItem:
		case wear:
			this.required_obj = proj.getItem(required_obj_id);
			break;
		case killedMonster:
			this.required_obj = proj.getNPC(required_obj_id);
			break;
		case questLatestProgress:
		case questProgress:
			this.required_obj = proj.getQuest(required_obj_id);
			break;
		case consumedBonemeals:
		case skillLevel:
		case spentGold:
		case timerElapsed:
			break;
		}
		if (this.required_obj != null) this.required_obj.addBacklink((GameDataElement) this.parent);
	}
	
	@Override
	public GameDataElement clone() {
		return clone(null);
	}
	
	public GameDataElement clone(GameDataElement parent) {
		Requirement clone = new Requirement();
		clone.parent = parent;
		clone.jsonFile = this.jsonFile;
		clone.state = this.state;
		clone.required_obj_id = this.required_obj_id;
		clone.required_value = this.required_value;
		clone.required_obj = this.required_obj;
		clone.type = this.type;
		if (clone.required_obj != null && parent != null) {
			clone.required_obj.addBacklink(parent);
		}
		return clone;
	}
	
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		if (this.required_obj == oldOne) {
			this.required_obj = newOne;
			if (newOne != null) newOne.addBacklink(this);
		}
	}
	@Override
	public String getProjectFilename() {
		throw new Error("Thou shalt not reach this method.");
	}
	
	public void changeType(RequirementType destType) {
		if (COMPATIBLE_TYPES.get(type) == null || !COMPATIBLE_TYPES.get(type).contains(destType)) {
			required_obj = null;
			required_obj_id = null;
			required_value = null;
		}
		type = destType;
	}

}
