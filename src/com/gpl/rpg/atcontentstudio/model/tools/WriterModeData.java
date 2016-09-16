package com.gpl.rpg.atcontentstudio.model.tools;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import com.gpl.rpg.atcontentstudio.model.GameDataElement.State;
import com.gpl.rpg.atcontentstudio.model.GameSource.Type;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.SaveEvent;
import com.gpl.rpg.atcontentstudio.model.gamedata.GameDataSet;
import com.gpl.rpg.atcontentstudio.model.tools.WriterModeData.WriterDialogue;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class WriterModeData extends GameDataElement {

	public WriterModeDataSet parent;
	
	public Image npcIcon;
	public WriterDialogue begin;
	public Map<String, WriterDialogue> dialogueThreads = new LinkedHashMap<String, WriterDialogue>();
	public Map<String, Integer> threadsNextIndex = new LinkedHashMap<String, Integer>();
	

	public WriterModeData(WriterModeDataSet parent, String id_prefix){
		this.parent = parent;
		this.begin = new WriterDialogue();
		begin.id_prefix = id_prefix;
		begin.index = getNextIndex(id_prefix);
		begin.text = "";
	}
	
	public  WriterModeData(WriterModeDataSet parent, Map jsonObj) {
		this.parent = parent;
		this.begin = new WriterDialogue(jsonObj);
		this.state = State.parsed;
	}
	
	public int getNextIndex(String id_prefix) {
		Integer index = threadsNextIndex.get(id_prefix);
		if (index == null) index = 0;
		while (getProject().getDialogue(id_prefix+index) != null) {
			index++;
		}
		threadsNextIndex.put(id_prefix, index + 1);
		return index;
	}
	
	
	public abstract class WriterNode {
		public String text;
		
		public abstract String getTitle();
		public abstract Map toJson();
		
	}
	
	public class WriterDialogue extends WriterNode {
		public String id_prefix;
		public int index;
		public List<WriterReply> replies = new LinkedList<WriterReply>();
		
		
		public WriterDialogue() {}
		
		public WriterDialogue(String id_prefix) {
			text = "";
			this.id_prefix = id_prefix;
			index = getNextIndex(id_prefix);
		}
		
		@Override
		public String getTitle() {
			return "Dialogue "+id_prefix+index;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Map toJson() {
			Map dialogueJson = new HashMap();
			dialogueJson.put("id_prefix", id_prefix);
			dialogueJson.put("index", index);
			dialogueJson.put("text", text);
			if (!replies.isEmpty()) {
				List repliesJson = new ArrayList();
				for (WriterReply reply : replies) {
					repliesJson.add(reply.toJson());
				}
				dialogueJson.put("replies", repliesJson);
			}
			return dialogueJson;
		}
		
		public WriterDialogue(Map json) {
			this.index = Integer.parseInt((String) json.get("index"));
			this.id_prefix = (String) json.get("id_prefix");
			this.text = (String) json.get("text");
			List repliesJson = (List) json.get("replies");
			for (Object rJson : repliesJson) {
				this.replies.add(new WriterReply(this, (Map)rJson));
			}
		}
		
	}
	
	public class SpecialDialogue extends WriterDialogue {}
	public class SelectorDialogue extends SpecialDialogue {}
	public class ShopDialogue extends SpecialDialogue {}
	public class FightDialogue extends SpecialDialogue {}
	public class EndDialogue extends SpecialDialogue {}
	
	public class WriterReply extends WriterNode {
		public WriterDialogue parent;
		public WriterDialogue next_dialogue;
		
		public WriterReply() {}
		
		public WriterReply(WriterDialogue parent) {
			this.parent = parent;
			this.text = "";
			parent.replies.add(this);
		}

		public WriterReply(WriterDialogue parent, Map json) {
			this.parent = parent;
			this.text = (String) json.get("text");
			if (json.containsKey("next_dialogue")) {
				next_dialogue = new WriterDialogue((Map) json.get("next_dialogue"));
			}
		}
		
		@Override
		public String getTitle() {
			return "Reply in "+parent.id_prefix+parent.index;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Map toJson() {
			Map replyJson = new HashMap();
			replyJson.put("text", text);
			if (next_dialogue != null) {
				replyJson.put("next_dialogue", next_dialogue.toJson());
			}
			return replyJson;
		}


	}
	
	public class SpecialReply extends WriterReply {

		public SpecialReply(WriterDialogue parent) {
			super(parent);
		}
	}
	public class EmptyReply extends SpecialReply {

		public EmptyReply(WriterDialogue parent) {
			super(parent);
		}
	}
	
	
	
	@Override
	public String getDesc() {
		return (this.state == State.modified ? "*" : "")+begin.id_prefix;
	}
	@Override
	public Project getProject() {
		return parent.getProject();
	}
	
	@Override
	public Image getIcon() {
		return DefaultIcons.getDialogueIcon();
	}
	@Override
	public Image getOpenIcon() {
		return null;
	}
	@Override
	public Image getClosedIcon() {
		return null;
	}
	@Override
	public Image getLeafIcon() {
		return getIcon();
	}
	@Override
	public GameDataSet getDataSet() {
		return null;
	}
	@Override
	public void parse() {
		// TODO
		
	}
	@Override
	public void link() {
		//Useless here.
	}
	@Override
	public GameDataElement clone() {
		//TODO
		return null;
	}
	@Override
	public void elementChanged(GameDataElement oldOne, GameDataElement newOne) {
		// Useless here.
		
	}
	@Override
	public String getProjectFilename() {
		return WriterModeDataSet.DEFAULT_REL_PATH_IN_PROJECT;
	}
	
	@Override
	public void save() {
		parent.save();
	}
	@Override
	public List<SaveEvent> attemptSave() {
		List<SaveEvent> events = parent.attemptSave();
		if (events == null || events.isEmpty()) {
			return null;
		}
		if (events.size() == 1 && events.get(0).type == SaveEvent.Type.alsoSave && events.get(0).target == this) {
			save();
			return null;
		}
		return events;
	}
	
	@SuppressWarnings("rawtypes")
	public Map toJson() {
		return begin.toJson();
	}
	

}
