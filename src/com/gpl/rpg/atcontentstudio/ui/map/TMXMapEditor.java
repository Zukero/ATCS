package com.gpl.rpg.atcontentstudio.ui.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import tiled.view.MapRenderer;
import tiled.view.OrthogonalRenderer;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.GameSource;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.ActorCondition;
import com.gpl.rpg.atcontentstudio.model.gamedata.Dialogue;
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist;
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.model.gamedata.JSONElement;
import com.gpl.rpg.atcontentstudio.model.gamedata.NPC;
import com.gpl.rpg.atcontentstudio.model.gamedata.Quest;
import com.gpl.rpg.atcontentstudio.model.gamedata.Requirement;
import com.gpl.rpg.atcontentstudio.model.maps.ContainerArea;
import com.gpl.rpg.atcontentstudio.model.maps.KeyArea;
import com.gpl.rpg.atcontentstudio.model.maps.MapChange;
import com.gpl.rpg.atcontentstudio.model.maps.MapObject;
import com.gpl.rpg.atcontentstudio.model.maps.MapObjectGroup;
import com.gpl.rpg.atcontentstudio.model.maps.ReplaceArea;
import com.gpl.rpg.atcontentstudio.model.maps.RestArea;
import com.gpl.rpg.atcontentstudio.model.maps.ScriptArea;
import com.gpl.rpg.atcontentstudio.model.maps.SignArea;
import com.gpl.rpg.atcontentstudio.model.maps.SpawnArea;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMap;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.BooleanBasedCheckBox;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.Editor;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.IntegerBasedCheckBox;
import com.gpl.rpg.atcontentstudio.ui.ScrollablePanel;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideTabbedPane;

public class TMXMapEditor extends Editor {

	private static final long serialVersionUID = -3079451876618342442L;


	Map<String, JPanel> editorTabs = new HashMap<String, JPanel>();
	JideTabbedPane editorTabsHolder;
	
	private RSyntaxTextArea editorPane;
	
	private IntegerBasedCheckBox outsideBox;
	private LayerListModel layerListModel;
	private JList layerList;
	private tiled.core.MapLayer selectedLayer;
	private	JButton addTileLayer;
	private	JButton addObjectGroup;
	private	JButton deleteLayer;
	
	private JPanel layerDetailsPane;
	private BooleanBasedCheckBox layerVisibleBox;
	private JTextField layerNameField;
	private MapObjectsListModel groupObjectsListModel;
	private JList groupObjectsList;
	private MapObject selectedMapObject;
	private	JButton addMapchange;
	private	JButton addSpawn;
	private	JButton addRest;
	private	JButton addKey;
	private	JButton addReplace;
	private	JButton addScript;
	private	JButton addContainer;
	private	JButton addSign;
	private	JButton deleteObject;
	
	private JPanel mapObjectSettingsPane;
	private JComboBox droplistBox;
	private JComboBox dialogueBox;
	private JComboBox mapBox;
	private JTextField areaField;
	private JComboBox targetAreaCombo;
	private JComboBox evaluateTriggerBox;
	private JSpinner quantityField;
	private JList npcList;
	private SpawnGroupNpcListModel npcListModel;

	private JComboBox requirementTypeCombo;
	private JPanel requirementParamsPane;
	private JComboBox requirementObj;
	private JTextField requirementObjId;
	private JSpinner requirementValue;
	private BooleanBasedCheckBox requirementNegated;
	
	private TMXViewer tmxViewer;
	
	public TMXMapEditor(TMXMap map) {
		this.target = map;
		this.name = map.getDesc();
		this.icon = new ImageIcon(DefaultIcons.getTiledIconIcon());
		
		
		setLayout(new BorderLayout());
		editorTabsHolder = new JideTabbedPane(JideTabbedPane.BOTTOM);
		editorTabsHolder.setTabShape(JideTabbedPane.SHAPE_FLAT);
		editorTabsHolder.setUseDefaultShowCloseButtonOnTab(false);
		editorTabsHolder.setShowCloseButtonOnTab(false);
		add(editorTabsHolder, BorderLayout.CENTER);
		
		JScrollPane tmxScroller = new JScrollPane(getTmxEditorPane(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane xmlScroller = new JScrollPane(getXmlEditorPane(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		xmlScroller.getVerticalScrollBar().setUnitIncrement(16);
		editorTabsHolder.add("TMX", tmxScroller);
		editorTabsHolder.add("XML", xmlScroller);
		
	}
	
	public JPanel getTmxEditorPane() {
		final TMXMap map = (TMXMap) target;
		final FieldUpdateListener listener = new MapFieldUpdater();
		
		ScrollablePanel pane = new ScrollablePanel();
		pane.setScrollableWidth( ScrollablePanel.ScrollableSizeHint.FIT );
		pane.setLayout(new JideBoxLayout(pane, JideBoxLayout.PAGE_AXIS, 6));
		
		addLabelField(pane, "TMX File: ", ((TMXMap)target).tmxFile.getAbsolutePath());
		createButtonPane(pane, map.getProject(), map, listener);
		outsideBox = addIntegerBasedCheckBox(pane, "Map is outdoors", map.outside, map.writable, listener);
		
		JSplitPane layersViewSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		layerListModel = new LayerListModel(map); 
		layerList = new JList(layerListModel);
		layerList.setCellRenderer(new LayerListRenderer());
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane layerListScroller = new JScrollPane(layerList);
		layerListScroller.getVerticalScrollBar().setUnitIncrement(16);
		layerList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedLayer = (tiled.core.MapLayer) layerList.getSelectedValue();
				selectedMapObject = null;
				if (selectedLayer != null && map.writable) {
					deleteLayer.setEnabled(true);
				} else {
					deleteLayer.setEnabled(false);
				}
				updateLayerDetailsPane(layerDetailsPane, selectedLayer, listener);
				listener.valueChanged(layerList, selectedLayer);
			}
		});
		JPanel layersListPane = new JPanel();
		layersListPane.setLayout(new JideBoxLayout(layersListPane, JideBoxLayout.PAGE_AXIS, 6));
		layersListPane.add(layerListScroller, JideBoxLayout.VARY);
		addTileLayer = new JButton(new ImageIcon(DefaultIcons.getCreateTileLayerIcon()));
		addTileLayer.setToolTipText("Create new tile layer (graphics layer).");
		addTileLayer.setEnabled(map.writable);
		addTileLayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layerListModel.addObject(new tiled.core.TileLayer());
			}
		});
		addObjectGroup = new JButton(new ImageIcon(DefaultIcons.getCreateObjectGroupIcon()));
		addObjectGroup.setToolTipText("Create new object group.");
		addObjectGroup.setEnabled(map.writable);
		addObjectGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layerListModel.addObject(new tiled.core.ObjectGroup());
			}
		});
		deleteLayer = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		deleteLayer.setToolTipText("Delete selected layer/group.");
		deleteLayer.setEnabled(false);
		deleteLayer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layerListModel.removeObject(selectedLayer);
			}
		});
		JPanel layersButtonsPane = new JPanel();
		layersButtonsPane.setLayout(new JideBoxLayout(layersButtonsPane, JideBoxLayout.LINE_AXIS, 6));
		layersButtonsPane.add(addTileLayer, JideBoxLayout.FIX);
		layersButtonsPane.add(addObjectGroup, JideBoxLayout.FIX);
		layersButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
		layersButtonsPane.add(deleteLayer, JideBoxLayout.FIX);
		layersListPane.add(layersButtonsPane, JideBoxLayout.FIX);
		layersViewSplitPane.setLeftComponent(layersListPane);
		layerDetailsPane = new JPanel();
		layerDetailsPane.setLayout(new JideBoxLayout(layerDetailsPane, JideBoxLayout.PAGE_AXIS, 6));
		layersViewSplitPane.setRightComponent(layerDetailsPane);
		pane.add(layersViewSplitPane, JideBoxLayout.FIX);
		
		tmxViewer = new TMXViewer(((TMXMap)target), listener);
		JScrollPane tmxScroller = new JScrollPane(tmxViewer);
		tmxScroller.getVerticalScrollBar().setUnitIncrement(16);
		tmxScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tmxScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane.add(tmxScroller, JideBoxLayout.FIX);
		
		addTMXMapSpritesheetsList(pane, ((TMXMap)target));
		
		addBacklinksList(pane, map);
		
		pane.add(new JPanel(), JideBoxLayout.VARY);
		return pane;
	}
	
	public void updateLayerDetailsPane(JPanel pane, tiled.core.MapLayer selected, final FieldUpdateListener listener) {
		final TMXMap map = (TMXMap)target;
		pane.removeAll();
		if (selected == null) {
			return;
		} else if (selected instanceof tiled.core.TileLayer) {
			layerNameField = addTextField(pane, "Layer name: ", selected.getName(), map.writable, listener);
			layerVisibleBox = addBooleanBasedCheckBox(pane, "Visible", selected.isVisible(), true, listener);
			pane.add(new JPanel(), JideBoxLayout.VARY);
		} else if (selected instanceof tiled.core.ObjectGroup) {
			JSplitPane objectGroupDetailsSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			JPanel groupDetailPane = new JPanel();
			groupDetailPane.setLayout(new JideBoxLayout(groupDetailPane, JideBoxLayout.PAGE_AXIS, 6));
			objectGroupDetailsSplitter.setLeftComponent(groupDetailPane);
			layerNameField = addTextField(groupDetailPane, "Group name: ", selected.getName(), map.writable, listener);
			layerVisibleBox = addBooleanBasedCheckBox(groupDetailPane, "Visible", selected.isVisible(), true, listener);
			MapObjectGroup objGroup = null;
			for (MapObjectGroup group : map.groups) {
				if (group.tmxGroup == selected) {
					objGroup = group;
					break;
				}
			}
			groupObjectsListModel = new MapObjectsListModel(objGroup);
			groupObjectsList = new JList(groupObjectsListModel);
			groupObjectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			groupObjectsList.setCellRenderer(new GroupObjectsRenderer());
			JScrollPane groupObjectsScroller = new JScrollPane(groupObjectsList);
			groupObjectsScroller.getVerticalScrollBar().setUnitIncrement(16);
			groupDetailPane.add(groupObjectsScroller, JideBoxLayout.VARY);
			groupObjectsList.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					selectedMapObject = (MapObject) groupObjectsList.getSelectedValue();
					updateMapObjectSettingsPane(mapObjectSettingsPane, selectedMapObject, listener);
					listener.valueChanged(groupObjectsList, selectedMapObject);
					if (selectedMapObject != null && map.writable) {
						deleteObject.setEnabled(true);
					} else {
						deleteObject.setEnabled(false);
					}
				}
			});
			
			addMapchange = new JButton(new ImageIcon(DefaultIcons.getCreateMapchangeIcon()));
			addMapchange.setToolTipText("Create new mapchange area.");
			addMapchange.setEnabled(map.writable);
			addMapchange.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newMapchange(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addSpawn = new JButton(new ImageIcon(DefaultIcons.getCreateSpawnareaIcon()));
			addSpawn.setToolTipText("Create new spawn area.");
			addSpawn.setEnabled(map.writable);
			addSpawn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newSpawnArea(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addRest = new JButton(new ImageIcon(DefaultIcons.getCreateRestIcon()));
			addRest.setToolTipText("Create new rest area.");
			addRest.setEnabled(map.writable);
			addRest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newRest(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addKey = new JButton(new ImageIcon(DefaultIcons.getCreateKeyIcon()));
			addKey.setToolTipText("Create new key area.");
			addKey.setEnabled(map.writable);
			addKey.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newKey(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addReplace = new JButton(new ImageIcon(DefaultIcons.getCreateReplaceIcon()));
			addReplace.setToolTipText("Create new replace area.");
			addReplace.setEnabled(map.writable);
			addReplace.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newReplace(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addScript = new JButton(new ImageIcon(DefaultIcons.getCreateScriptIcon()));
			addScript.setToolTipText("Create new script area.");
			addScript.setEnabled(map.writable);
			addScript.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newScript(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addContainer = new JButton(new ImageIcon(DefaultIcons.getCreateContainerIcon()));
			addContainer.setToolTipText("Create new container.");
			addContainer.setEnabled(map.writable);
			addContainer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newContainer(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			addSign = new JButton(new ImageIcon(DefaultIcons.getCreateSignIcon()));
			addSign.setToolTipText("Create new sign post.");
			addSign.setEnabled(map.writable);
			addSign.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.addObject(MapObject.newSign(new tiled.core.MapObject(0, 0, 32, 32), map));
				}
			});
			deleteObject = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
			deleteObject.setToolTipText("Delete selected map object.");
			deleteObject.setEnabled(false);
			deleteObject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					groupObjectsListModel.removeObject(selectedMapObject);
				}
			});

			JPanel groupButtonsPane1 = new JPanel();
			groupButtonsPane1.setLayout(new JideBoxLayout(groupButtonsPane1, JideBoxLayout.LINE_AXIS, 6));
			groupButtonsPane1.add(addMapchange, JideBoxLayout.FIX);
			groupButtonsPane1.add(addSpawn, JideBoxLayout.FIX);
			groupButtonsPane1.add(addRest, JideBoxLayout.FIX);
			groupButtonsPane1.add(addKey, JideBoxLayout.FIX);
			groupButtonsPane1.add(new JPanel(), JideBoxLayout.VARY);
			JPanel groupButtonsPane2 = new JPanel();
			groupButtonsPane2.setLayout(new JideBoxLayout(groupButtonsPane2, JideBoxLayout.LINE_AXIS, 6));
			groupButtonsPane2.add(addReplace, JideBoxLayout.FIX);
			groupButtonsPane2.add(addScript, JideBoxLayout.FIX);
			groupButtonsPane2.add(addContainer, JideBoxLayout.FIX);
			groupButtonsPane2.add(addSign, JideBoxLayout.FIX);
			groupButtonsPane2.add(new JPanel(), JideBoxLayout.VARY);
			groupButtonsPane2.add(deleteObject, JideBoxLayout.FIX);
			groupDetailPane.add(groupButtonsPane1, JideBoxLayout.FIX);
			groupDetailPane.add(groupButtonsPane2, JideBoxLayout.FIX);
			
			mapObjectSettingsPane = new JPanel();
			mapObjectSettingsPane.setLayout(new JideBoxLayout(mapObjectSettingsPane, JideBoxLayout.PAGE_AXIS, 6));
			JScrollPane mapObjectSettingsScroller = new JScrollPane(mapObjectSettingsPane);
			mapObjectSettingsScroller.getVerticalScrollBar().setUnitIncrement(16);
			objectGroupDetailsSplitter.setRightComponent(mapObjectSettingsScroller);
			pane.add(objectGroupDetailsSplitter, JideBoxLayout.VARY);
		}
		pane.revalidate();
		pane.repaint();
	}

	public void updateMapObjectSettingsPane(JPanel pane, final MapObject selected, final FieldUpdateListener listener) {
		pane.removeAll();
		boolean needVary = true;
		if (selected instanceof ContainerArea) {
			droplistBox = addDroplistBox(pane, ((TMXMap)target).getProject(), "Droplist: ", ((ContainerArea)selected).droplist, ((TMXMap)target).writable, listener);
		} else if (selected instanceof KeyArea) {
			dialogueBox = addDialogueBox(pane, ((TMXMap)target).getProject(), "Message when locked: ", ((KeyArea)selected).dialogue, ((TMXMap)target).writable, listener);
			requirementTypeCombo = addEnumValueBox(pane, "Requirement type: ", Requirement.RequirementType.values(), ((KeyArea)selected).requirement.type, ((TMXMap)target).writable, listener);
			requirementParamsPane = new JPanel();
			requirementParamsPane.setLayout(new JideBoxLayout(requirementParamsPane, JideBoxLayout.PAGE_AXIS, 6));
			pane.add(requirementParamsPane, JideBoxLayout.FIX);
			updateRequirementParamsPane(requirementParamsPane, ((KeyArea)selected).requirement, listener);
		} else if (selected instanceof MapChange) {
			areaField = addTextField(pane, "Area ID: ", ((MapChange)selected).name, ((TMXMap)target).writable, listener);
			mapBox = addMapBox(pane, ((TMXMap)target).getProject(), "Target map: ", ((MapChange)selected).map, ((TMXMap)target).writable, listener);
			targetAreaCombo = new JComboBox();
			if (((MapChange)selected).map != null) {
				((MapChange)selected).map.link();
				targetAreaCombo.setModel(new DefaultComboBoxModel((((MapChange)selected).map.getMapchangesNames().toArray())));
			}
			targetAreaCombo.setEditable(false);
			targetAreaCombo.setEnabled(((TMXMap)target).writable);
			targetAreaCombo.setSelectedItem(((MapChange)selected).place_id);
			JPanel tACPane = new JPanel();
			tACPane.setLayout(new JideBoxLayout(tACPane, JideBoxLayout.LINE_AXIS, 6));
			tACPane.add(new JLabel("Target mapchange area ID: "), JideBoxLayout.FIX);
			tACPane.add(targetAreaCombo, JideBoxLayout.VARY);
			JButton nullifyTargetArea = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
			tACPane.add(nullifyTargetArea, JideBoxLayout.FIX);
			nullifyTargetArea.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					targetAreaCombo.setSelectedItem(null);
					listener.valueChanged(targetAreaCombo, null);
				}
			});
			targetAreaCombo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					listener.valueChanged(targetAreaCombo, targetAreaCombo.getSelectedItem());
				}
			});
			pane.add(tACPane, JideBoxLayout.FIX);
		} else if (selected instanceof ReplaceArea) {
			//TODO replace area edition
		} else if (selected instanceof RestArea) {
			pane.add(new JLabel("Rest areas have no parameters"), JideBoxLayout.FIX);
		} else if (selected instanceof ScriptArea) {
			evaluateTriggerBox = addEnumValueBox(pane, "Evaluate on every: ", ScriptArea.EvaluationTrigger.values(), ((ScriptArea)selected).trigger_type, ((TMXMap)target).writable, listener);
			dialogueBox = addDialogueBox(pane, ((TMXMap)target).getProject(), "Script: ", ((ScriptArea)selected).dialogue, ((TMXMap)target).writable, listener);
		} else if (selected instanceof SignArea) {
			dialogueBox = addDialogueBox(pane, ((TMXMap)target).getProject(), "Message: ", ((SignArea)selected).dialogue, ((TMXMap)target).writable, listener);
		} else if (selected instanceof SpawnArea) {
			areaField = addTextField(pane, "Spawn group ID: ", ((SpawnArea)selected).name, ((TMXMap)target).writable, listener);
			quantityField = addIntegerField(pane, "Number of spawned NPCs: ", ((SpawnArea)selected).quantity, false, ((TMXMap)target).writable, listener);
			npcListModel = new SpawnGroupNpcListModel((SpawnArea) selected);
			npcList = new JList(npcListModel);
			npcList.setCellRenderer(new GDERenderer(true, ((TMXMap)target).writable));
			npcList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			npcList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						ATContentStudio.frame.openEditor((JSONElement)npcList.getSelectedValue());
						ATContentStudio.frame.selectInTree((JSONElement)npcList.getSelectedValue());
					}
				}
			});
			npcList.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						ATContentStudio.frame.openEditor((JSONElement)npcList.getSelectedValue());
						ATContentStudio.frame.selectInTree((JSONElement)npcList.getSelectedValue());
					}
				}
			});
			JScrollPane npcListScroller = new JScrollPane(npcList);
			npcListScroller.getVerticalScrollBar().setUnitIncrement(16);
			pane.add(npcListScroller, JideBoxLayout.VARY);
			needVary = false;
		}
		if (needVary) pane.add(new JPanel(), JideBoxLayout.VARY);
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateRequirementParamsPane(JPanel pane, Requirement requirement, FieldUpdateListener listener) {
		boolean writable = ((TMXMap)target).writable;
		Project project = ((TMXMap)target).getProject();
		pane.removeAll();
		if (requirement.type != null) {
			switch (requirement.type) {
			case consumedBonemeals:
			case spentGold:
				requirementObj = null;
				requirementObjId = null;
				requirementValue = addIntegerField(pane, "Quantity: ", requirement.required_value, false, writable, listener);
				break;
			case hasActorCondition:
				requirementObj = addActorConditionBox(pane, project, "Actor Condition: ", (ActorCondition) requirement.required_obj, writable, listener);
				requirementObjId = null;
				requirementValue = null;
				break;
			case inventoryKeep:
			case inventoryRemove:
			case usedItem:
				requirementObj = addItemBox(pane, project, "Item: ", (Item) requirement.required_obj, writable, listener);
				requirementObjId = null;
				requirementValue = addIntegerField(pane, "Quantity: ", requirement.required_value, false, writable, listener);
				break;
			case killedMonster:
				requirementObj = addNPCBox(pane, project, "Monster: ", (NPC) requirement.required_obj, writable, listener);
				requirementObjId = null;
				requirementValue = addIntegerField(pane, "Quantity: ", requirement.required_value, false, writable, listener);
				break;
			case questLatestProgress:
			case questProgress:
				requirementObj = addQuestBox(pane, project, "Quest: ", (Quest) requirement.required_obj, writable, listener);
				requirementObjId = null;
				requirementValue = addIntegerField(pane, "Quest stage: ", requirement.required_value, false, writable, listener);
				break;
			case skillLevel:
				requirementObj = null;
				requirementObjId = addTextField(pane, "Skill ID:", requirement.required_obj_id, writable, listener);
				requirementValue = addIntegerField(pane, "Level: ", requirement.required_value, false, writable, listener);
				break;
			case timerElapsed:
				requirementObj = null;
				requirementObjId = addTextField(pane, "Timer ID:", requirement.required_obj_id, writable, listener);
				requirementValue = addIntegerField(pane, "Timer value: ", requirement.required_value, false, writable, listener);
				break;
			case wear:
				requirementObj = addItemBox(pane, project, "Item: ", (Item) requirement.required_obj, writable, listener);
				requirementObjId = null;
				requirementValue = null;
				break;
			}
		}
		requirementNegated = addBooleanBasedCheckBox(pane, "Negate this requirement.", requirement.negated, writable, listener);
		pane.revalidate();
		pane.repaint();
	}

	public JPanel getXmlEditorPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new JideBoxLayout(pane, JideBoxLayout.PAGE_AXIS, 6));
		editorPane = new RSyntaxTextArea();
		editorPane.setText(((TMXMap)target).toXml());
		editorPane.setEditable(false);
		editorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		pane.add(editorPane, JideBoxLayout.VARY);

		return pane;
	}
	
	public void updateXmlViewText(String text) {
		editorPane.setText(text);
	}
	
	public static JList addTMXMapSpritesheetsList(JPanel pane, TMXMap tmxMap) {
		final JList list = new JList(new TMXMapSpritesheetsListModel(tmxMap));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ATContentStudio.frame.openEditor((Spritesheet)list.getSelectedValue());
					ATContentStudio.frame.selectInTree((Spritesheet)list.getSelectedValue());
				}
			}
		});
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ATContentStudio.frame.openEditor((Spritesheet)list.getSelectedValue());
					ATContentStudio.frame.selectInTree((Spritesheet)list.getSelectedValue());
				}
			}
		});
		list.setCellRenderer(new SpritesheetCellRenderer(true));
		JScrollPane scroller = new JScrollPane(list);
		scroller.setBorder(BorderFactory.createTitledBorder("Spritesheets used in this map."));
		pane.add(scroller, JideBoxLayout.FIX);
		return list;
	}
	
	public class LayerListModel implements ListModel {

		public TMXMap map;
		
		public LayerListModel(TMXMap map) {
			this.map = map;
		}
		
		@Override
		public int getSize() {
			return map.tmxMap.getLayerCount();
		}

		@Override
		public Object getElementAt(int index) {
			return map.tmxMap.getLayer(index);
		}


		public void objectChanged(tiled.core.MapLayer layer) {
			int index = map.tmxMap.getLayerIndex(layer);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
			}
		}
		
		public void addObject(tiled.core.MapLayer layer) {
			map.addLayer(layer);
			int index = map.tmxMap.getLayerIndex(layer);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeObject(tiled.core.MapLayer layer) {
			int index = map.tmxMap.getLayerIndex(layer);
			map.removeLayer(layer);
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}
		
		List<ListDataListener> listeners = new ArrayList<ListDataListener>();
		
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		
	}
	
	public class LayerListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -6182599528961565957L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = (JLabel)c;
				tiled.core.MapLayer layer = (tiled.core.MapLayer)value;
				label.setText(layer.getName());
				if (layer instanceof tiled.core.TileLayer) {
					label.setIcon(new ImageIcon(DefaultIcons.getTileLayerIcon()));
				} else if (layer instanceof tiled.core.ObjectGroup) {
					label.setIcon(new ImageIcon(DefaultIcons.getObjectLayerIcon()));
				}
			}
			return c;
		}
	}
	
	public class MapObjectsListModel implements ListModel {

		public MapObjectGroup group;
		
		public MapObjectsListModel(MapObjectGroup group) {
			this.group = group;
		}
		
		@Override
		public int getSize() {
			return group.mapObjects.size();
		}

		@Override
		public Object getElementAt(int index) {
			return group.mapObjects.get(index);
		}
		
		public void objectChanged(MapObject area) {
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(groupObjectsList, ListDataEvent.CONTENTS_CHANGED, group.mapObjects.indexOf(area), group.mapObjects.indexOf(area)));
			}
		}
		
		public void addObject(MapObject area) {
			group.mapObjects.add(area);
			int index = group.mapObjects.indexOf(area);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(groupObjectsList, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeObject(MapObject area) {
			int index = group.mapObjects.indexOf(area);
			group.mapObjects.remove(area);
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(groupObjectsList, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		List<ListDataListener> listeners = new ArrayList<ListDataListener>();
		
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}
	
	}

	public class GroupObjectsRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -6182599528961565957L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				((JLabel)c).setText(((MapObject)value).name);
				((JLabel)c).setIcon(new ImageIcon(((MapObject)value).getIcon()));
			}
			return c;
		}
	}
	
	public class SpawnGroupNpcListModel implements ListModel {
		
		public SpawnArea area;
		
		public SpawnGroupNpcListModel(SpawnArea area) {
			this.area = area;
		}
		
		@Override
		public int getSize() {
			return area.spawnGroup.size();
		}

		@Override
		public Object getElementAt(int index) {
			return area.spawnGroup.get(index);
		}

		List<ListDataListener> listeners = new ArrayList<ListDataListener>();
		
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}
	
	}
	
	public class TMXViewer extends JPanel implements Scrollable {
		
		private static final long serialVersionUID = 2845032142029325865L;
		public tiled.core.MapObject highlighted = null;
	    private MapRenderer renderer;
	    private FieldUpdateListener listener;
	    
	    public boolean resizing = false;
	    public boolean moving = false;

	    public Rectangle getResizeHitArea() {
	    	//16x16 px square in the lower right corner of area
	    	return new Rectangle(selectedMapObject.x + selectedMapObject.w - 16, selectedMapObject.y + selectedMapObject.h - 16, 16, 16);
	    }
	    
	    public Rectangle getMoveHitArea() {
	    	//16x16 px square in the upper left corner of area
	    	return new Rectangle(selectedMapObject.x, selectedMapObject.y, 16, 16);
	    }
	    
	    public Rectangle getSelectHitArea(MapObject obj) {
	    	//16x16 px square in the upper left corner of area
	    	return new Rectangle(obj.x, obj.y, 16, 16);
	    }
	    
	    public Point getClosestTileCorner(Point p) {
	    	return new Point(getClosestMultiple(p.x, 32), getClosestMultiple(p.y, 32));
	    }
	    
	    public int getClosestMultiple(int num, int ref) {
	    	int rest = num % ref;
	    	int result = num - rest;
	    	if (rest >= ref / 2) {
	    		result += ref;
	    	}
	    	return result;
	    }
	    
	    public TMXViewer(final TMXMap map, FieldUpdateListener listener) {
	    	this.listener = listener;
	        renderer = createRenderer(map.tmxMap);

	        setPreferredSize(renderer.getMapSize());
	        setOpaque(true);
	        
	        addMouseListener(new MouseAdapter() {

        		@Override
        		public void mouseClicked(MouseEvent e) {
        			if (e.getButton() == MouseEvent.BUTTON1) {
        				if (!moving && !resizing) {
        					select: for (MapObjectGroup group : map.groups) {
        						if (group.visible) {
        							for (MapObject obj : group.mapObjects) {
        								if (getSelectHitArea(obj).contains(e.getPoint())) {
        									TMXMapEditor.this.selectMapObject(obj);
        									break select;
        								}
        							}
        						}
        					}
        				}
        			}
        		}
	        });
	        
	        if (((TMXMap)target).writable) {
	        	addMouseListener(new MouseAdapter() {

	        		@Override
	        		public void mouseReleased(MouseEvent e) {
	        			if (e.getButton() == MouseEvent.BUTTON1) {
	        				resizing = false;
	        				moving = false;
	        			}
	        		}

	        		@Override
	        		public void mousePressed(MouseEvent e) {
	        			if (e.getButton() == MouseEvent.BUTTON1) {
	        				if (selectedMapObject != null && selectedLayer.isVisible()) {
	        					if (getResizeHitArea().contains(e.getPoint())) {
	        						resizing = true;
	        					} else if (getMoveHitArea().contains(e.getPoint())) {
	        						moving = true;
	        					}
	        				}
	        			}
	        		}
	        		
	        	});
	        	
	        	addMouseMotionListener(new MouseMotionListener() {
					@Override
					public void mouseMoved(MouseEvent e) {
						if (selectedMapObject == null) return;
						if (!resizing && !moving) {
							if (getResizeHitArea().contains(e.getPoint())) {
								setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
							} else if (getMoveHitArea().contains(e.getPoint())) {
								setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							} else {
								setCursor(Cursor.getDefaultCursor());
							}
						}
					}
					
					@Override
					public void mouseDragged(MouseEvent e) {
						if (selectedMapObject == null) return;
						boolean valueChanged = false;
						if (resizing) {
							Point p = getClosestTileCorner(e.getPoint());
							if (p.x > selectedMapObject.x) {
								int oldW = selectedMapObject.w;
								selectedMapObject.w = Math.min(p.x - selectedMapObject.x, map.tmxMap.getWidth() * map.tmxMap.getTileWidth());
								if (selectedMapObject.w != oldW) valueChanged = true;
							}
							if (p.y > selectedMapObject.y) {
								int oldH = selectedMapObject.h;
								selectedMapObject.h = Math.min(p.y - selectedMapObject.y, map.tmxMap.getHeight() * map.tmxMap.getTileHeight());
								if (selectedMapObject.h != oldH) valueChanged = true;
							}
						} else if (moving) {
							Point p = getClosestTileCorner(e.getPoint());
							if (p.x + selectedMapObject.w <= map.tmxMap.getWidth() * map.tmxMap.getTileWidth()) {
								int oldX = selectedMapObject.x;
								selectedMapObject.x = Math.max(p.x, 0);
								if (selectedMapObject.x != oldX) valueChanged = true;
							}
							if (p.y + selectedMapObject.h <= map.tmxMap.getHeight() * map.tmxMap.getTileHeight()) {
								int oldY = selectedMapObject.y;
								selectedMapObject.y = Math.max(p.y, 0);
								if (selectedMapObject.y != oldY) valueChanged = true;
							}
						}
						if (valueChanged) {
							TMXViewer.this.listener.valueChanged(TMXViewer.this, null);
							TMXViewer.this.revalidate();
							TMXViewer.this.repaint();
						}
					}
				});
	        }
	    }

	    public void setHighlight(tiled.core.MapObject selected) {
			highlighted = selected;
			invalidate();
		}

		public void paintComponent(Graphics g) {
	        final Graphics2D g2d = (Graphics2D) g.create();
	        final Rectangle clip = g2d.getClipBounds();

	        // Draw a gray background
	        g2d.setPaint(new Color(100, 100, 100));
	        g2d.fill(clip);

	        // Draw each tile map layer
	        boolean paintSelected = false;
	        for (tiled.core.MapLayer layer : ((TMXMap)target).tmxMap) {
	            if (layer instanceof tiled.core.TileLayer && layer.isVisible()) {
	                renderer.paintTileLayer(g2d, (tiled.core.TileLayer) layer);
	            } else if (layer instanceof tiled.core.ObjectGroup && layer.isVisible()) {
	                paintSelected |= paintObjectGroup(g2d, (tiled.core.ObjectGroup) layer);
	            }
	        }
	        if (paintSelected) {
	        	//TODO make this less ugly..... visually speaking.
	        	g2d.setColor(new Color(190, 20, 20));
	        	g2d.drawRect(selectedMapObject.x + selectedMapObject.w - 16, selectedMapObject.y + selectedMapObject.h - 16, 15, 15);
	        	g2d.drawRect(selectedMapObject.x + selectedMapObject.w - 12, selectedMapObject.y + selectedMapObject.h - 12, 11, 11);
				drawObject(selectedMapObject, g2d, new Color(190, 20, 20));
			}
	        
	        g2d.dispose();
	    }

	    private boolean paintObjectGroup(Graphics2D g2d, tiled.core.ObjectGroup layer) {
	    	boolean paintSelected = false;
	    	for (MapObjectGroup group : ((TMXMap)target).groups) {
				if (group.tmxGroup == layer) {
					for (MapObject object : group.mapObjects) {
						if (object == selectedMapObject) {
							paintSelected = true;
							continue;
						} else {
							drawObject(object, g2d, new Color(20, 20, 190));
						}
					}
					break;
				}
			}
	    	return paintSelected;
		}
	    
	    private void drawObject(MapObject object, Graphics2D g2d, Color color) {
	    	g2d.setPaint(color);
			g2d.drawRect(object.x+1, object.y+1, object.w-3, object.h-3);
			g2d.drawRect(object.x+2, object.y+2, object.w-5, object.h-5);
			g2d.setPaint(color.darker().darker());
			g2d.drawLine(object.x, object.y + object.h - 1, object.x + object.w - 1, object.y + object.h - 1);
			g2d.drawLine(object.x + object.w - 1, object.y, object.x + object.w - 1, object.y + object.h - 1);
			g2d.drawLine(object.x + 3, object.y + 3, object.x + object.w - 4, object.y + 3);
			g2d.drawLine(object.x + 3, object.y + 3, object.x + 3, object.y + object.h - 4);
			g2d.setPaint(color.brighter().brighter().brighter());
			g2d.drawLine(object.x, object.y, object.x + object.w - 1, object.y);
			g2d.drawLine(object.x, object.y, object.x, object.y + object.h - 1);
			g2d.drawLine(object.x + 3, object.y + object.h - 4, object.x + object.w - 4, object.y + object.h - 4);
			g2d.drawLine(object.x + object.w - 4, object.y + 3, object.x + object.w - 4, object.y + object.h - 4);
			Image img = object.getIcon();
			g2d.setColor(new Color(255, 255, 255, 120));
			g2d.fillRect(object.x + 2, object.y + 2, img.getWidth(null), img.getHeight(null));
			g2d.drawImage(object.getIcon(), object.x + 2, object.y + 2, null);
	    }

		private MapRenderer createRenderer(tiled.core.Map map) {
	        switch (map.getOrientation()) {
	            case tiled.core.Map.ORIENTATION_ORTHOGONAL:
	                return new OrthogonalRenderer(map);
	            default:
	                return null;
	        }
	    }

	    public Dimension getPreferredScrollableViewportSize() {
	        return getPreferredSize();
	    }

	    public int getScrollableUnitIncrement(Rectangle visibleRect,
	                                          int orientation, int direction) {
	        if (orientation == SwingConstants.HORIZONTAL)
	            return ((TMXMap)target).tmxMap.getTileWidth();
	        else
	            return ((TMXMap)target).tmxMap.getTileHeight();
	    }

	    public int getScrollableBlockIncrement(Rectangle visibleRect,
	                                           int orientation, int direction) {
	        if (orientation == SwingConstants.HORIZONTAL) {
	            final int tileWidth = ((TMXMap)target).tmxMap.getTileWidth();
	            return (visibleRect.width / tileWidth - 1) * tileWidth;
	        } else {
	            final int tileHeight = ((TMXMap)target).tmxMap.getTileHeight();
	            return (visibleRect.height / tileHeight - 1) * tileHeight;
	        }
	    }

	    public boolean getScrollableTracksViewportWidth() {
	        return false;
	    }

	    public boolean getScrollableTracksViewportHeight() {
	        return false;
	    }
		
	}
	
	public static class TMXMapSpritesheetsListModel implements ListModel {
		
		TMXMap map;
		
		public TMXMapSpritesheetsListModel(TMXMap map) {
			this.map = map;
		}
		
		@Override
		public int getSize() {
			return map.usedSpritesheets.size();
		}

		@Override
		public Object getElementAt(int index) {
			for (Spritesheet sheet : map.usedSpritesheets) {
				if (index == 0) return sheet;
				index --;
			}
			return null;
		}

		List<ListDataListener> listeners = new ArrayList<ListDataListener>();
		
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}
		
		public void fireListChanged() { 
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.getSize()));
			}
		}
	}
	
	public static class SpritesheetCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 6819681566800482793L;

		private boolean includeType = false;
		
		public SpritesheetCellRenderer(boolean includeType) {
			super();
			this.includeType = includeType;
			
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value == null) {
				label.setText("none");
			} else {
				if (includeType && ((Spritesheet)value).getDataType() != null) {
					label.setText(((Spritesheet)value).getDataType().toString()+"/"+((Spritesheet)value).getDesc());
				} else {
					label.setText(((Spritesheet)value).getDesc());
				}
				if (((Spritesheet)value).getIcon() == null) {
					Notification.addError("Unable to find icon for "+((Spritesheet)value).getDesc());
				} else {
					label.setIcon(new ImageIcon(((Spritesheet)value).getIcon()));
				}
			}
			return label;
		}
	}

	@Override
	public void targetUpdated() {
		this.name = ((TMXMap)target).getDesc();
		updateMessage();
	}
	
	
	

	protected void selectMapObject(MapObject obj) {
		for (MapObjectGroup group : ((TMXMap)target).groups) {
			if (group.mapObjects.contains(obj)) {
				layerList.setSelectedValue(group.tmxGroup, true);
				groupObjectsList.setSelectedValue(obj, true);
			}
		}
	}

	public JButton createButtonPane(JPanel pane, final Project proj, final TMXMap map, final FieldUpdateListener listener) {
		final JButton gdeIcon = new JButton(new ImageIcon(DefaultIcons.getTiledIconImage()));
		JPanel savePane = new JPanel();
		savePane.add(gdeIcon, JideBoxLayout.FIX);
		savePane.setLayout(new JideBoxLayout(savePane, JideBoxLayout.LINE_AXIS, 6));
		if (map.writable) {
			if (map.getDataType() == GameSource.Type.altered) {
				savePane.add(message = new JLabel(ALTERED_MESSAGE), JideBoxLayout.FIX);
			} else if (map.getDataType() == GameSource.Type.created) {
				savePane.add(message = new JLabel(CREATED_MESSAGE), JideBoxLayout.FIX);
			}
			JButton save = new JButton(SAVE);
			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (map.state != TMXMap.State.saved) { 
						map.save();
						ATContentStudio.frame.nodeChanged(map);
					}
				}
			});
			savePane.add(save, JideBoxLayout.FIX);
			JButton delete = new JButton(DELETE);
			if (map.getDataType() == GameSource.Type.altered) {
				delete.setText(REVERT);
			}
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ATContentStudio.frame.closeEditor(map);
					map.childrenRemoved(new ArrayList<ProjectTreeNode>());
					map.delete();
				}
			});
			savePane.add(delete, JideBoxLayout.FIX);
		} else {
			if (proj.getMap(map.id) != map) {
				savePane.add(message = new JLabel(ALTERED_EXISTS_MESSAGE), JideBoxLayout.FIX);
				JButton makeWritable = new JButton(GO_TO_ALTERED);
				makeWritable.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (map.getProject().getMap(map.id) != map) {
							ATContentStudio.frame.openEditor(map.getProject().getMap(map.id));
							ATContentStudio.frame.closeEditor(map);
							ATContentStudio.frame.selectInTree(map.getProject().getMap(map.id));
						}
					}
				});
				savePane.add(makeWritable, JideBoxLayout.FIX);

			} else {
				savePane.add(message = new JLabel(READ_ONLY_MESSAGE), JideBoxLayout.FIX);
				JButton makeWritable = new JButton(ALTER);
				makeWritable.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (map.getProject().getMap(map.id) == map) {
							map.getProject().makeWritable(map);
						}
						if (map.getProject().getMap(map.id) != map) {
							ATContentStudio.frame.openEditor(map.getProject().getMap(map.id));
							ATContentStudio.frame.closeEditor(map);
							ATContentStudio.frame.selectInTree(map.getProject().getMap(map.id));
						}
					}
				});
				savePane.add(makeWritable, JideBoxLayout.FIX);
			}
		}
		JButton prev = new JButton(new ImageIcon(DefaultIcons.getArrowLeftIcon()));
		JButton next = new JButton(new ImageIcon(DefaultIcons.getArrowRightIcon()));
		savePane.add(prev, JideBoxLayout.FIX);
		savePane.add(next, JideBoxLayout.FIX);
		if (map.getParent().getIndex(map) == 0) {
			prev.setEnabled(false);
		}
		if (map.getParent().getIndex(map) == map.getParent().getChildCount() - 1) {
			next.setEnabled(false);
		}
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectTreeNode prevNode = (ProjectTreeNode) map.getParent().getChildAt(map.getParent().getIndex(map) - 1);
				if (prevNode != null && prevNode instanceof GameDataElement) {
					ATContentStudio.frame.openEditor((GameDataElement) prevNode);
				}
			}
		});
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectTreeNode nextNode = (ProjectTreeNode) map.getParent().getChildAt(map.getParent().getIndex(map) + 1);
				if (nextNode != null && nextNode instanceof GameDataElement) {
					ATContentStudio.frame.openEditor((GameDataElement) nextNode);
				}
			}
		});
		//Placeholder. Fills the eventual remaining space.
		savePane.add(new JPanel(), JideBoxLayout.VARY);
		pane.add(savePane, JideBoxLayout.FIX);
		return gdeIcon;
	}
	
	
	
	public class MapFieldUpdater implements FieldUpdateListener {
		@Override
		public void valueChanged(JComponent source, Object value) {
			TMXMap map = (TMXMap) target;
			boolean modified = true;
			if (source == layerNameField) {
				selectedLayer.setName((String) value);
				if (selectedLayer instanceof tiled.core.ObjectGroup){
					map.getGroup((tiled.core.ObjectGroup) selectedLayer).name = (String) value;
				}
				layerListModel.objectChanged(selectedLayer);
			} else if (source == layerVisibleBox) {
				selectedLayer.setVisible(layerVisibleBox.isSelected());
				if (selectedLayer instanceof tiled.core.ObjectGroup) {
					map.getGroup((tiled.core.ObjectGroup) selectedLayer).visible = layerVisibleBox.isSelected();
				}
				modified = false;
				tmxViewer.revalidate();
				tmxViewer.repaint();
			} else if (source == layerList) {
				modified = false;
				tmxViewer.revalidate();
				tmxViewer.repaint();
			} else if (source == groupObjectsList) {
				modified = false;
				tmxViewer.revalidate();
				tmxViewer.repaint();
			} else if (source == areaField) {
				if (selectedMapObject instanceof SpawnArea) {
					SpawnArea area = (SpawnArea)selectedMapObject;
					if (area.spawnGroup != null && !area.spawnGroup.isEmpty()) {
						for (NPC npc : area.spawnGroup) {
							npc.removeBacklink(map);
						}
					}
					area.name = (String) value;
					selectedMapObject.link();
					npcList.setModel(new SpawnGroupNpcListModel(area));
					groupObjectsListModel.objectChanged(area);
					npcList.revalidate();
					npcList.repaint();
					tmxViewer.revalidate();
					tmxViewer.repaint();
				} else if (selectedMapObject instanceof MapChange) {
					MapChange area = (MapChange) selectedMapObject;
					area.name = (String) value;
					groupObjectsListModel.objectChanged(area);
				}
			} else if (source == targetAreaCombo) {
				if (selectedMapObject instanceof MapChange) {
					MapChange area = (MapChange) selectedMapObject;
					area.place_id = (String) value;
				}
			} else if (source == outsideBox) {
				map.outside = (Integer)value;
			} else if (source == droplistBox) {
				if (selectedMapObject instanceof ContainerArea) {
					ContainerArea area = (ContainerArea)selectedMapObject;
					if (area.droplist != null) {
						area.droplist.removeBacklink(map);
					}
					area.droplist = (Droplist) value;
					if (area.droplist != null) {
						area.name = area.droplist.id;
					} else {
						area.name = null;
					}
					groupObjectsListModel.objectChanged(area);
					tmxViewer.revalidate();
					tmxViewer.repaint();
				}
			} else if (source == dialogueBox) {
				if (selectedMapObject instanceof KeyArea) {
					KeyArea area = (KeyArea) selectedMapObject;
					if (area.dialogue != null) {
						area.dialogue.removeBacklink(map);
					}
					area.dialogue = (Dialogue) value;
					if (area.dialogue != null) {
						area.dialogue_id = area.dialogue.id;
					} else {
						area.dialogue_id = null;
					}
					tmxViewer.revalidate();
					tmxViewer.repaint();
				} else if (selectedMapObject instanceof ScriptArea) {
					ScriptArea area = (ScriptArea) selectedMapObject;
					if (area.dialogue != null) {
						area.dialogue.removeBacklink(map);
					}
					area.dialogue = (Dialogue) value;
					if (area.dialogue != null) {
						area.name = area.dialogue.id;
					} else {
						area.name = null;
					}
					groupObjectsListModel.objectChanged(area);
					tmxViewer.revalidate();
					tmxViewer.repaint();
				} else if (selectedMapObject instanceof SignArea) {
					SignArea area = (SignArea) selectedMapObject;
					if (area.dialogue != null) {
						area.dialogue.removeBacklink(map);
					}
					area.dialogue = (Dialogue) value;
					if (area.dialogue != null) {
						area.name = area.dialogue.id;
					} else {
						area.name = null;
					}
					groupObjectsListModel.objectChanged(area);
					tmxViewer.revalidate();
					tmxViewer.repaint();
				}
			} else if (source == mapBox) {
				if (selectedMapObject instanceof MapChange) {
					MapChange area = (MapChange) selectedMapObject;
					if (area.map != null) {
						area.map.removeBacklink(map);
					}
					area.map = (TMXMap) value;
					if (area.map != null) {
						area.map_id = area.map.id;
						targetAreaCombo.setModel(new DefaultComboBoxModel((area.map.getMapchangesNames().toArray())));
					} else {
						area.map_id = null;
					}
					tmxViewer.revalidate();
					tmxViewer.repaint();
				}
			} else if (source == evaluateTriggerBox) {
				if (selectedMapObject instanceof ScriptArea) {
					ScriptArea area = (ScriptArea) selectedMapObject;
					area.trigger_type = (ScriptArea.EvaluationTrigger) value;
				}
			} else if (source == quantityField) {
				if (selectedMapObject instanceof SpawnArea) {
					SpawnArea area = (SpawnArea) selectedMapObject;
					area.quantity = (Integer) value;
				}
			} else if (source == requirementTypeCombo) {
				if (selectedMapObject instanceof KeyArea) {
					KeyArea area = (KeyArea) selectedMapObject;
					area.requirement.changeType((Requirement.RequirementType)requirementTypeCombo.getSelectedItem());
					updateRequirementParamsPane(requirementParamsPane, area.requirement, this);
				}
			} else if (source == requirementObj) {
				if (selectedMapObject instanceof KeyArea) {
					KeyArea area = (KeyArea) selectedMapObject;
					area.requirement.required_obj = (GameDataElement) value;
					if (area.requirement.required_obj != null) {
						area.requirement.required_obj_id = area.requirement.required_obj.id; 
					} else {
						area.requirement.required_obj_id = null;
					}
				}
			} else if (source == requirementObjId) {
				if (selectedMapObject instanceof KeyArea) {
					KeyArea area = (KeyArea) selectedMapObject;
					area.requirement.required_obj_id = (String) value;
					area.requirement.required_obj = null;
				}
			} else if (source == requirementValue) {
				if (selectedMapObject instanceof KeyArea) {
					KeyArea area = (KeyArea) selectedMapObject;
					area.requirement.required_value = (Integer) value;
				}
			} else if (source == requirementNegated) {
				if (selectedMapObject instanceof KeyArea) {
					KeyArea area = (KeyArea) selectedMapObject;
					area.requirement.negated = (Boolean) value;
				}
			} 
			if (modified) {
				if (map.state != GameDataElement.State.modified) {
					map.state = GameDataElement.State.modified;
					TMXMapEditor.this.name = map.getDesc();
					map.childrenChanged(new ArrayList<ProjectTreeNode>());
					ATContentStudio.frame.editorChanged(TMXMapEditor.this);
				}
				updateXmlViewText(map.toXml());
			}
		}
	}
	

	public void updateMessage() {
		
		//TODO make this a full update of the button panel.
		TMXMap node = (TMXMap) target;
		if (node.writable) {
			if (node.getDataType() == GameSource.Type.altered) {
				message.setText(ALTERED_MESSAGE);
			} else if (node.getDataType() == GameSource.Type.created) {
				message.setText(CREATED_MESSAGE);
			}
		} else if (node.getProject().getMap(node.id) != node) {
			message.setText(ALTERED_EXISTS_MESSAGE);
		} else {
			message.setText(READ_ONLY_MESSAGE);
		}
		message.revalidate();
		message.repaint();
	}
	
	
}
