package com.gpl.rpg.atcontentstudio.ui.tools.i18n;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.gpl.rpg.atcontentstudio.model.Project;

import net.launchpad.tobal.poparser.POEntry;
import net.launchpad.tobal.poparser.POFile;
import net.launchpad.tobal.poparser.POParser;

public class PotComparator {

	Map<String, List<String>> stringsResourcesNew = new LinkedHashMap<String, List<String>>();
	Map<String, String> resourcesStringsNew = new LinkedHashMap<String, String>();
	
	Map<String, List<String>> stringsResourcesOld = new LinkedHashMap<String, List<String>>();
	Map<String, String> resourcesStringsOld = new LinkedHashMap<String, String>();
	
	
	public PotComparator(Project proj) {
		POParser parser = new POParser();

		POFile newPot = parser.parseFile(new File(proj.alteredContent.baseFolder.getAbsolutePath()+File.separator+"english.pot"));
		if (newPot == null) {
			System.err.println("Cannot locate new english.pot file at "+proj.alteredContent.baseFolder.getAbsolutePath()+File.separator);
		}
		extractFromPoFile(newPot, stringsResourcesNew, resourcesStringsNew);
		
		POFile oldPot = parser.parseFile(new File(proj.baseContent.baseFolder.getAbsolutePath()+File.separator+"assets"+File.separator+"translation"+File.separator+"english.pot"));
		if (oldPot == null) {
			System.err.println("Cannot locate old english.pot file at "+proj.baseContent.baseFolder.getAbsolutePath()+File.separator+"assets"+File.separator+"translations"+File.separator);
		}
		extractFromPoFile(oldPot, stringsResourcesOld, resourcesStringsOld);
	}
	
	
	private void extractFromPoFile(POFile po, Map<String, List<String>> stringsResources, Map<String, String> resourcesStrings) {
		for (POEntry entry : po.getEntryArray()) {
			Vector<String> resources = entry.getStringsByType(POEntry.StringType.REFERENCE);
			Vector<String> msgids = entry.getStringsByType(POEntry.StringType.MSGID);
			if (resources == null || resources.size() == 0 || msgids == null || msgids.size() == 0) continue;
			String msgid = msgids.get(0);
			if (msgids.size() > 1) {
				for (int i = 1; i < msgids.size(); i++) {
					msgid += "\n";
					msgid += msgids.get(i); 
				}
			}
			for (String resLine : resources) {
				String[] resArray = resLine.split(" ");
				for (String res : resArray) {
					resourcesStrings.put(res, msgid);
					if (stringsResources.get(msgid) == null) {
						stringsResources.put(msgid, new LinkedList<String>());
					}
					stringsResources.get(msgid).add(res);
				}
			}
		}
	}
	
	public void compare() {
		for (String oldRes : resourcesStringsOld.keySet()) {
			String newString = resourcesStringsNew.get(oldRes);
			String oldString = resourcesStringsOld.get(oldRes);
			if (newString != null) {
				if (!newString.equals(oldString)) {
					List<String> allOldResources = stringsResourcesOld.get(oldString);
					List<String> allNewResources = stringsResourcesNew.get(oldString);
					System.out.println("---------------------------------------------");
					System.out.println("--- TYPO CHECK ------------------------------");
					System.out.println("---------------------------------------------");
					System.out.println("String at: "+oldRes);
					if (allOldResources.size() > 1) {
						System.out.println("Also present at:");
						for (String res : allOldResources) {
							if (!res.equals(oldRes)) {
								System.out.println("- "+res);
							}
						}
					}
					if (allNewResources != null) {
						System.out.println("Still present at: ");
						for (String res : allNewResources) {
							System.out.println("- "+res);
						}
					}
					System.out.println("Was : \""+oldString+"\"");
					System.out.println("Now : \""+newString+"\"");
					
				}
			} else {
				List<String> allOldResources = stringsResourcesOld.get(oldString);
				List<String> allNewResources = stringsResourcesNew.get(oldString);
				if (allOldResources.size() >= 1) {
					System.out.println("---------------------------------------------");
					System.out.println("--- REMOVED RESOURCE ------------------------");
					System.out.println("---------------------------------------------");
					System.out.println("String at: "+oldRes);
					if (allOldResources.size() > 1) {
						System.out.println("And also at:");
						for (String res : allOldResources) {
							if (!res.equals(oldRes)) {
								System.out.println("- "+res);
							}
						}
					}
					System.out.println("Was: \""+oldString+"\"");
					if (allNewResources == null) {
						System.out.println("Absent from new.");
					} else {
						System.out.println("Still present at: ");
						for (String res : allNewResources) {
							System.out.println("- "+res);
						}

					}
				}
			}
		}
		removedStrings: for (String oldString : stringsResourcesOld.keySet())  {
			if (stringsResourcesNew.get(oldString) == null) {
				List<String> allOldResources = stringsResourcesOld.get(oldString);
				if (allOldResources.size() >= 1) {
					if (allOldResources.size() > 0) {
						for (String res : allOldResources) {
							String newString = resourcesStringsNew.get(res);
							if (newString != null) {
								continue removedStrings;
							}
						}
					}
					System.out.println("---------------------------------------------");
					System.out.println("--- REMOVED STRING --------------------------");
					System.out.println("---------------------------------------------");
					System.out.println("String: \""+oldString+"\"");
					if (allOldResources.size() > 0) {
						System.out.println("Was at:");
						for (String res : allOldResources) {
							System.out.println("- "+res);
						}
					}
					System.out.println("This string is absent from the new file, and its attached resources are missing too.");
				}
			}
		}
	}
	
}
