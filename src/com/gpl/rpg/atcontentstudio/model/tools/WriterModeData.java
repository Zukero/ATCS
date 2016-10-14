package com.gpl.rpg.atcontentstudio.model.tools;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.SaveEvent;
import com.gpl.rpg.atcontentstudio.model.gamedata.GameDataSet;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;

public class WriterModeData extends GameDataElement {
	private static final long serialVersionUID = -7062544089063979696L;
	
	//Available from state init.
	public File jsonFile;

	public Image npcIcon;
	public String sketchName;
	

	public List<String> rootsId = new LinkedList<String>();
	public List<WriterDialogue> roots = new LinkedList<WriterDialogue>();
	public WriterDialogue begin;
	public Map<String, WriterDialogue> nodesById = new LinkedHashMap<String, WriterDialogue>();

	public Map<String, WriterDialogue> dialogueThreads = new LinkedHashMap<String, WriterDialogue>();
	public Map<String, Integer> threadsNextIndex = new LinkedHashMap<String, Integer>();
	

	public WriterModeData(String id_prefix){
		this.id = id_prefix;
	}
	
	public  WriterModeData(WriterModeDataSet parent, @SuppressWarnings("rawtypes") Map jsonObj) {
		this.parent = parent;
		this.begin = new WriterDialogue(jsonObj);
		this.id = begin.id_prefix;
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
		@SuppressWarnings("rawtypes")
		public abstract Map toJson();
		
	}
	
	public class WriterDialogue extends WriterNode {
		public String id;
		public String id_prefix;
		public int index;
		public List<WriterReply> replies = new LinkedList<WriterReply>();
		public List<WriterReply> parents = new LinkedList<WriterReply>();
		
		
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
			dialogueJson.put("id", id);
			dialogueJson.put("id_prefix", id_prefix);
			dialogueJson.put("index", index);
			dialogueJson.put("text", text);
			dialogueJson.put("special", isSpecial());
			if (!replies.isEmpty()) {
				List repliesJson = new ArrayList();
				for (WriterReply reply : replies) {
					repliesJson.add(reply.toJson());
				}
				dialogueJson.put("replies", repliesJson);
			}
			return dialogueJson;
		}
		
		@SuppressWarnings("rawtypes")
		public WriterDialogue(Map json) {
			this.id = (String) json.get("id");
			this.index = Integer.parseInt((String) json.get("index"));
			this.id_prefix = (String) json.get("id_prefix");
			this.text = (String) json.get("text");
			List repliesJson = (List) json.get("replies");
			for (Object rJson : repliesJson) {
				if (Boolean.parseBoolean((String)((Map)rJson).get("special"))) {
					//TODO Check different cases. But there are none currently.
					this.replies.add(new EmptyReply(this, ((Map)rJson)));
				}
				this.replies.add(new WriterReply(this, (Map)rJson));
			}
		}
		
		public boolean isSpecial() {return false;}
		
		
	}
	
	public abstract class SpecialDialogue extends WriterDialogue {
		
		public boolean isSpecial() {return true;}
		public abstract SpecialDialogue duplicate();
	}
	public class SelectorDialogue extends SpecialDialogue {
		public SpecialDialogue duplicate() {return new SelectorDialogue();}
	}
	public class ShopDialogue extends SpecialDialogue {
		public static final String id = "S";
		public SpecialDialogue duplicate() {return new ShopDialogue();}
	}
	public class FightDialogue extends SpecialDialogue {
		public static final String id = "F";
		public SpecialDialogue duplicate() {return new FightDialogue();}
	}
	public class EndDialogue extends SpecialDialogue {
		public static final String id = "X";
		public SpecialDialogue duplicate() {return new EndDialogue();}
	}
	public class RemoveNPCDialogue extends SpecialDialogue {
		public static final String id = "R";
		public SpecialDialogue duplicate() {return new RemoveNPCDialogue();}
	}
	
	public class WriterReply extends WriterNode {
		public WriterDialogue parent;
		public String next_dialogue_id;
		public WriterDialogue next_dialogue;
		
		public WriterReply() {}
		
		public WriterReply(WriterDialogue parent) {
			this.parent = parent;
			this.text = "";
			parent.replies.add(this);
		}

		@SuppressWarnings("rawtypes")
		public WriterReply(WriterDialogue parent, Map json) {
			this.parent = parent;
			this.text = (String) json.get("text");
			if (json.containsKey("next_dialogue_id")) {
				next_dialogue_id = (String) json.get("next_dialogue_id");
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
			replyJson.put("special", isSpecial());
			if (next_dialogue != null) {
				replyJson.put("next_dialogue_id", next_dialogue.id);
			}
			return replyJson;
		}

		public boolean isSpecial() {return false;}

	}
	
	public class SpecialReply extends WriterReply {

		public boolean isSpecial() {return true;}
		
		public SpecialReply(WriterDialogue parent) {
			super(parent);
		}
		
		public SpecialReply(WriterDialogue parent, Map json) {
			super(parent, json);
		}
	}
	public class EmptyReply extends SpecialReply {

		public EmptyReply(WriterDialogue parent) {
			super(parent);
			text="N";
		}
		
		public EmptyReply(WriterDialogue parent, Map json) {
			super(parent, json);
			text="N";
		}
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
		((WriterModeDataSet)this.getParent()).save(this.jsonFile);
	}
	
	@Override
	public List<SaveEvent> attemptSave() {
		List<SaveEvent> events = ((WriterModeDataSet)parent).attemptSave();
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
	
	@SuppressWarnings("rawtypes")
	public void parse() {
		if (this.state == State.created || this.state == State.modified || this.state == State.saved) {
			//This type of state is unrelated to parsing/linking.
			return;
		}
		JSONParser parser = new JSONParser();
		FileReader reader = null;
		try {
			reader = new FileReader(jsonFile);
			List gameDataElements = (List) parser.parse(reader);
			for (Object obj : gameDataElements) {
				Map jsonObj = (Map)obj;
				String id  = (String) jsonObj.get("id");
				if (id != null && id.equals(this.id )) {
					this.parse(jsonObj);
					this.state = State.parsed;
					break;
				}
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
	public void parse(Map json) {
		this.id = (String) json.get("id");
		this.sketchName = (String) json.get("name");
		List jsonRootsId = (List) json.get("roots_id");
		if (jsonRootsId != null) {
			for (Object jsonRootId : jsonRootsId) {
				rootsId.add((String) jsonRootId);
			}
		}
		List jsonDialogues = (List) json.get("dialogues");
		if (jsonDialogues != null) {
			for (Object jsonDialogue : jsonDialogues) {
				WriterDialogue dialogue = new WriterDialogue((Map)jsonDialogue);
				nodesById.put(dialogue.id, dialogue);
			}
		}
		this.state = State.parsed;
	}
	
	@Override
	public void link() {
		if (this.state == State.created) {
			this.begin = new WriterDialogue();
			begin.id_prefix = id;
			begin.index = getNextIndex(id);
			begin.text = "";
		}
		if (this.state == State.init) {
			//Not parsed yet.
			this.parse();
		} else if (this.state == State.parsed) {
			for (WriterDialogue dialogue : nodesById.values()) {
				if (dialogue.replies == null) continue;
				for (WriterReply reply : dialogue.replies) {
					if (reply.next_dialogue_id != null) {
						if (isSpecial(reply.next_dialogue_id)) {
							reply.next_dialogue = getSpecial(reply.next_dialogue_id);
						} else {
							reply.next_dialogue = nodesById.get(reply.next_dialogue_id);
						}
					}
				}
			}
			for (String rootId : rootsId) {
				roots.add(nodesById.get(rootId));
			}

		} else if (this.state == State.linked) {
			//Already linked.
			return;
		}

		this.state = State.linked;
	}
	
	public boolean isSpecial(String id) {
		if (id == null) return false;
		if (ShopDialogue.id.equals(id)) return true;
		if (FightDialogue.id.equals(id)) return true;
		if (EndDialogue.id.equals(id)) return true;
		if (RemoveNPCDialogue.id.equals(id)) return true;
		return false;
	}
	
	public SpecialDialogue getSpecial(String id) {
		if (id == null) return null;
		if (ShopDialogue.id.equals(id)) return new ShopDialogue();
		if (FightDialogue.id.equals(id)) return new FightDialogue();
		if (EndDialogue.id.equals(id)) return new EndDialogue();
		if (RemoveNPCDialogue.id.equals(id)) return new RemoveNPCDialogue();
		return null;
	}

}
