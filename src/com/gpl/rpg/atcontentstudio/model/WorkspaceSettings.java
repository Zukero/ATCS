package com.gpl.rpg.atcontentstudio.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.io.JsonPrettyWriter;

public class WorkspaceSettings {
	
	public static final String VERSION_KEY = "ATCS_Version";
	public static final String FILENAME = "workspace_settings.json";
	
	public static final int SETTINGS_VERSION = 1;

	public Workspace parent;
	public File file;
	
	public static Boolean DEFAULT_USE_SYS_MAP_EDITOR = true;
	public Setting<Boolean> useSystemDefaultMapEditor = new PrimitiveSetting<Boolean>("useSystemDefaultMapEditor", DEFAULT_USE_SYS_MAP_EDITOR);
	public static String DEFAULT_MAP_EDITOR_COMMAND = "tiled";
	public Setting<String> mapEditorCommand = new PrimitiveSetting<String>("mapEditorCommand", DEFAULT_MAP_EDITOR_COMMAND);
	
	public static Boolean DEFAULT_USE_SYS_IMG_VIEWER = true;
	public Setting<Boolean> useSystemDefaultImageViewer = new PrimitiveSetting<Boolean>("useSystemDefaultImageViewer", DEFAULT_USE_SYS_IMG_VIEWER);
	public static Boolean DEFAULT_USE_SYS_IMG_EDITOR = false;
	public Setting<Boolean> useSystemDefaultImageEditor = new PrimitiveSetting<Boolean>("useSystemDefaultImageEditor", DEFAULT_USE_SYS_IMG_EDITOR);
	public static String DEFAULT_IMG_EDITOR_COMMAND = "gimp";
	public Setting<String> imageEditorCommand = new PrimitiveSetting<String>("imageEditorCommand", DEFAULT_IMG_EDITOR_COMMAND);

	public List<Setting<? extends Object>> settings = new ArrayList<Setting<? extends Object>>();
	
	public WorkspaceSettings(Workspace parent) {
		this.parent = parent;
		settings.add(useSystemDefaultMapEditor);
		settings.add(mapEditorCommand);
		settings.add(useSystemDefaultImageViewer);
		settings.add(useSystemDefaultImageEditor);
		settings.add(imageEditorCommand);
		file = new File(parent.baseFolder, FILENAME);
		if (file.exists()) {
			load(file);
		}
	}
	
	public void load(File f) {
		JSONParser parser = new JSONParser();
		FileReader reader = null;
		try {
			reader = new FileReader(f);
			@SuppressWarnings("rawtypes")
			Map jsonSettings = (Map) parser.parse(reader);
			Integer version = (Integer) jsonSettings.get(VERSION_KEY);
			if (version != null) {
				if (version >= 1) {
					loadv1(jsonSettings);
				}
			}
			
		} catch (FileNotFoundException e) {
			Notification.addError("Error while parsing workspace settings: "+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Notification.addError("Error while parsing workspace settings: "+e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			Notification.addError("Error while parsing workspace settings: "+e.getMessage());
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
	private void loadv1(Map jsonSettings) {
		for (Setting s : settings) {
			s.readFromJson(jsonSettings);
		}
	}

	@SuppressWarnings("unchecked")
	public void save() {
		@SuppressWarnings("rawtypes")
		Map json = new LinkedHashMap();
		for (Setting<? extends Object> s : settings) {
			s.saveToJson(json);
		}
		
		if (json.isEmpty()) {
			//Everything is default.
			file.delete();
			return;
		}

		json.put(VERSION_KEY, ATContentStudio.APP_VERSION);
		StringWriter writer = new JsonPrettyWriter();
		try {
			JSONObject.writeJSONString(json, writer);
		} catch (IOException e) {
			//Impossible with a StringWriter
		}
		String toWrite = writer.toString();
		try {
			FileWriter w = new FileWriter(file);
			w.write(toWrite);
			w.close();
			Notification.addSuccess("Workspace settings saved.");
		} catch (IOException e) {
			Notification.addError("Error while saving workspace settings : "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void resetDefault() {
		for (Setting<? extends Object> s : settings) {
			s.resetDefault();
		}
	}
	
	public abstract class Setting<X extends Object> {

		public String id;
		public X value, defaultValue;
		
		public void setCurrentValue(X value) {
			this.value = value;
		}
		
		public X getCurrentValue() {
			return value;
		}
		
		public X getDefaultValue() {
			return defaultValue;
		}
		
		public void resetDefault() {
			value = defaultValue;
		}
		
		public abstract void readFromJson(Map json);
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void saveToJson(Map json) {
			if (!defaultValue.equals(value)) json.put(id, value);
		}
	}
	
	public class PrimitiveSetting<X extends Object> extends Setting<X> {
		
		
		public PrimitiveSetting(String id, X defaultValue) {
			this.id = id;
			this.value = this.defaultValue = defaultValue;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void readFromJson(Map json) {
			if (json.get(id) != null) value = (X)json.get(id);
		}
		
		
	}
	
	public class ListSetting<X extends Object> extends Setting<List<X>> {

		public ListSetting(String id, List<X> defaultValue) {
			this.id = id;
			this.value = this.defaultValue = defaultValue;
		}
		
		@Override
		public void readFromJson(Map json) {
			value = new ArrayList<X>();
			if (json.get(id) != null) {
				for (Object o : ((List)json.get(id))) {
					value.add((X)o);
				}
			}
		}
		
	}
	
	

}