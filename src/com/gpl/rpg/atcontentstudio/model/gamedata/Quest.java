package com.gpl.rpg.atcontentstudio.model.gamedata;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.GameSource;
import com.gpl.rpg.atcontentstudio.model.GameDataElement.State;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class Quest extends JSONElement {

	private static final long serialVersionUID = 2004839647483250099L;
	
	//Available from init state
	//public String id = null; inherited.
	public String name = null;
	
	//Available in parsed state
	public Integer visible_in_log = null;
	public List<QuestStage> stages = null;
	
	public static class QuestStage implements Cloneable {
		public Integer progress = null;
		public String log_text = null;
		public Integer exp_reward = null;
		public Integer finishes_quest = null;
		
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	@Override
	public String getDesc() {
		return ((this.state == State.modified || this.state == State.created) ? "*" : "")+name+" ("+id+")";
	}

	public static String getStaticDesc() {
		return "Quests";
	}
	

	@SuppressWarnings("rawtypes")
	public static void fromJson(File jsonFile, GameDataCategory<Quest> category) {
		JSONParser parser = new JSONParser();
		FileReader reader = null;
		try {
			reader = new FileReader(jsonFile);
			List quests = (List) parser.parse(reader);
			for (Object obj : quests) {
				Map questJson = (Map)obj;
				Quest quest = fromJson(questJson);
				quest.jsonFile = jsonFile;
				quest.parent = category;
				if (quest.getDataType() == GameSource.Type.created || quest.getDataType() == GameSource.Type.altered) {
					quest.writable = true;
				}
				category.add(quest);
			}
		} catch (FileNotFoundException e) {
			Notification.addError("Error while parsing JSON file "+jsonFile.getAbsolutePath()+": "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Notification.addError("Error while parsing JSON file "+jsonFile.getAbsolutePath()+": "+e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			Notification.addError("Error while parsing JSON file "+jsonFile.getAbsolutePath()+": "+e.getMessage());
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Quest fromJson(String jsonString) throws ParseException {
		Map questJson = (Map) new JSONParser().parse(jsonString);
		Quest quest = fromJson(questJson);
		quest.parse(questJson);
		return quest;
	}
	
	@SuppressWarnings("rawtypes")
	public static Quest fromJson(Map questJson) {
		Quest quest = new Quest();
		quest.id = (String) questJson.get("id");
		quest.name = (String) questJson.get("name");
		return quest;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void parse(Map questJson) {
		this.visible_in_log = JSONElement.getInteger((Number) questJson.get("showInLog"));
		List questStagesJson = (List) questJson.get("stages");
		if (questStagesJson != null && !questStagesJson.isEmpty()) {
			this.stages = new ArrayList<QuestStage>();
			for (Object questStageJsonObj : questStagesJson) {
				Map questStageJson = (Map)questStageJsonObj;
				QuestStage questStage = new QuestStage();
				questStage.progress = JSONElement.getInteger((Number) questStageJson.get("progress"));
				questStage.log_text = (String) questStageJson.get("logText");
				questStage.exp_reward = JSONElement.getInteger((Number) questStageJson.get("rewardExperience"));
				questStage.finishes_quest = JSONElement.getInteger((Number) questStageJson.get("finishesQuest"));
				this.stages.add(questStage);
			}
		}
	
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
		
		//Nothing to link to :D
		this.state = State.linked;
	}

	@Override
	public Image getIcon() {
		return DefaultIcons.getQuestIcon();
	}
	
	public Image getImage() {
		return DefaultIcons.getQuestImage();
	}
	
	@Override
	public GameDataElement clone() {
		Quest clone = new Quest();
		clone.jsonFile = this.jsonFile;
		clone.state = this.state;
		clone.id = this.id;
		clone.name = this.name;
		clone.visible_in_log = this.visible_in_log;
		if (this.stages != null) {
			clone.stages = new ArrayList<Quest.QuestStage>();
			for (QuestStage stage : this.stages){
				clone.stages.add((QuestStage) stage.clone());
			}
		}
		return clone;
	}
	
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		//Nothing to link to.
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map toJson() {
		Map questJson = new LinkedHashMap();
		questJson.put("id", this.id);
		if (this.name != null) questJson.put("name", this.name);
		if (this.visible_in_log != null) questJson.put("showInLog", this.visible_in_log);
		if (this.stages != null) {
			List stagesJson = new ArrayList();
			questJson.put("stages", stagesJson);
			for (QuestStage stage : this.stages) {
				Map stageJson = new LinkedHashMap();
				stagesJson.add(stageJson);
				if (stage.progress != null) stageJson.put("progress", stage.progress);
				if (stage.log_text != null) stageJson.put("logText", stage.log_text);
				if (stage.exp_reward != null) stageJson.put("rewardExperience", stage.exp_reward);
				if (stage.finishes_quest != null) stageJson.put("finishesQuest", stage.finishes_quest);
			}
		}
		return questJson;
	}
	

	@Override
	public String getProjectFilename() {
		return "questlist_"+getProject().name+".json";
	}
	
}
