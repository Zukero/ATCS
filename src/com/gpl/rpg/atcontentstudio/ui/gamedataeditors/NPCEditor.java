package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.ActorCondition;
import com.gpl.rpg.atcontentstudio.model.gamedata.Dialogue;
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist;
import com.gpl.rpg.atcontentstudio.model.gamedata.NPC;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.IntegerBasedCheckBox;
import com.gpl.rpg.atcontentstudio.ui.OverlayIcon;
import com.gpl.rpg.atcontentstudio.ui.gamedataeditors.dialoguetree.DialogueGraphView;
import com.jidesoft.swing.JideBoxLayout;

public class NPCEditor extends JSONElementEditor {

	private static final long serialVersionUID = 4001483665523721800L;
	
	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	private static final String dialogue_tree_id = "Dialogue Tree";

	private NPC.TimedConditionEffect selectedHitEffectSourceCondition;
	private NPC.TimedConditionEffect selectedHitEffectTargetCondition;
	private NPC.TimedConditionEffect selectedHitReceivedEffectSourceCondition;
	private NPC.TimedConditionEffect selectedHitReceivedEffectTargetCondition;
	private NPC.TimedConditionEffect selectedDeathEffectSourceCondition;
	
	private JButton npcIcon;
	private JTextField idField;
	private JTextField nameField;
	private JTextField spawnGroupField;
	private JTextField factionField;
	private JSpinner experienceField;
	private MyComboBox dialogueBox;
	private MyComboBox droplistBox;
	@SuppressWarnings("rawtypes")
	private JComboBox monsterClassBox;
	private IntegerBasedCheckBox uniqueBox;
	@SuppressWarnings("rawtypes")
	private JComboBox moveTypeBox;
	
	private CollapsiblePanel combatTraitPane;
	private JSpinner maxHP;
	private JSpinner maxAP;
	private JSpinner moveCost;
	private JSpinner atkDmgMin;
	private JSpinner atkDmgMax;
	private JSpinner atkCost;
	private JSpinner atkChance;
	private JSpinner critSkill;
	private JSpinner critMult;
	private JSpinner blockChance;
	private JSpinner dmgRes;

	private NPC.HitEffect hitEffect;
	private CollapsiblePanel hitEffectPane;
	private JSpinner hitEffectHPMin;
	private JSpinner hitEffectHPMax;
	private JSpinner hitEffectAPMin;
	private JSpinner hitEffectAPMax;
	
	private SourceTimedConditionsListModel hitSourceConditionsListModel;
	@SuppressWarnings("rawtypes")
	private JList hitSourceConditionsList;
	private MyComboBox hitSourceConditionBox;
	private JSpinner hitSourceConditionChance;
	private JRadioButton hitSourceConditionClear;
	private JRadioButton hitSourceConditionApply;
	private JRadioButton hitSourceConditionImmunity;
	private JSpinner hitSourceConditionMagnitude;
	private JRadioButton hitSourceConditionTimed;
	private JRadioButton hitSourceConditionForever;
	private JSpinner hitSourceConditionDuration;
	
	private TargetTimedConditionsListModel hitTargetConditionsListModel;
	@SuppressWarnings("rawtypes")
	private JList hitTargetConditionsList;
	private MyComboBox hitTargetConditionBox;
	private JSpinner hitTargetConditionChance;
	private JRadioButton hitTargetConditionClear;
	private JRadioButton hitTargetConditionApply;
	private JRadioButton hitTargetConditionImmunity;
	private JSpinner hitTargetConditionMagnitude;
	private JRadioButton hitTargetConditionTimed;
	private JRadioButton hitTargetConditionForever;
	private JSpinner hitTargetConditionDuration;

	private NPC.HitReceivedEffect hitReceivedEffect;
	private CollapsiblePanel hitReceivedEffectPane;
	private JSpinner hitReceivedEffectHPMin;
	private JSpinner hitReceivedEffectHPMax;
	private JSpinner hitReceivedEffectAPMin;
	private JSpinner hitReceivedEffectAPMax;
	private JSpinner hitReceivedEffectHPMinTarget;
	private JSpinner hitReceivedEffectHPMaxTarget;
	private JSpinner hitReceivedEffectAPMinTarget;
	private JSpinner hitReceivedEffectAPMaxTarget;
	
	private SourceTimedConditionsListModel hitReceivedSourceConditionsListModel;
	@SuppressWarnings("rawtypes")
	private JList hitReceivedSourceConditionsList;
	private MyComboBox hitReceivedSourceConditionBox;
	private JSpinner hitReceivedSourceConditionChance;
	private JRadioButton hitReceivedSourceConditionClear;
	private JRadioButton hitReceivedSourceConditionApply;
	private JRadioButton hitReceivedSourceConditionImmunity;
	private JSpinner hitReceivedSourceConditionMagnitude;
	private JRadioButton hitReceivedSourceConditionTimed;
	private JRadioButton hitReceivedSourceConditionForever;
	private JSpinner hitReceivedSourceConditionDuration;
	
	private TargetTimedConditionsListModel hitReceivedTargetConditionsListModel;
	@SuppressWarnings("rawtypes")
	private JList hitReceivedTargetConditionsList;
	private MyComboBox hitReceivedTargetConditionBox;
	private JSpinner hitReceivedTargetConditionChance;
	private JRadioButton hitReceivedTargetConditionClear;
	private JRadioButton hitReceivedTargetConditionApply;
	private JRadioButton hitReceivedTargetConditionImmunity;
	private JSpinner hitReceivedTargetConditionMagnitude;
	private JRadioButton hitReceivedTargetConditionTimed;
	private JRadioButton hitReceivedTargetConditionForever;
	private JSpinner hitReceivedTargetConditionDuration;
	
	private NPC.DeathEffect deathEffect;
	private CollapsiblePanel deathEffectPane;
	private JSpinner deathEffectHPMin;
	private JSpinner deathEffectHPMax;
	private JSpinner deathEffectAPMin;
	private JSpinner deathEffectAPMax;
	
	private SourceTimedConditionsListModel deathSourceConditionsListModel;
	@SuppressWarnings("rawtypes")
	private JList deathSourceConditionsList;
	private MyComboBox deathSourceConditionBox;
	private JSpinner deathSourceConditionChance;
	private JRadioButton deathSourceConditionClear;
	private JRadioButton deathSourceConditionApply;
	private JRadioButton deathSourceConditionImmunity;
	private JSpinner deathSourceConditionMagnitude;
	private JRadioButton deathSourceConditionTimed;
	private JRadioButton deathSourceConditionForever;
	private JSpinner deathSourceConditionDuration;
	
	private JPanel dialogueGraphPane;
	private DialogueGraphView dialogueGraphView;
	
	public NPCEditor(NPC npc) {
		super(npc, npc.getDesc(), npc.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
		if (npc.dialogue != null) {
			createDialogueGraphView(npc);
			addEditorTab(dialogue_tree_id, dialogueGraphPane);
		}
	}
	
	public JPanel createDialogueGraphView(final NPC npc) {
		dialogueGraphPane = new JPanel();
		dialogueGraphPane.setLayout(new BorderLayout());
		
		dialogueGraphView = new DialogueGraphView(npc.dialogue, npc);
		dialogueGraphPane.add(dialogueGraphView, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new JideBoxLayout(buttonPane, JideBoxLayout.LINE_AXIS));
		JButton reloadButton = new JButton("Refresh graph");
		buttonPane.add(reloadButton, JideBoxLayout.FIX);
		buttonPane.add(new JPanel(), JideBoxLayout.VARY);
		dialogueGraphPane.add(buttonPane, BorderLayout.NORTH);
		
		
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadGraphView(npc);
			}
		});
		
		return dialogueGraphPane;
	}
	
	public void reloadGraphView(NPC npc) {
		if (npc.dialogue != null) {
			if (dialogueGraphPane != null) {
				dialogueGraphPane.remove(dialogueGraphView);
				dialogueGraphView = new DialogueGraphView(npc.dialogue, npc);
				dialogueGraphPane.add(dialogueGraphView, BorderLayout.CENTER);
				dialogueGraphPane.revalidate();
				dialogueGraphPane.repaint();
			} else {
				createDialogueGraphView(npc);
				addEditorTab(dialogue_tree_id, dialogueGraphPane);
			}
		} else {
			if (dialogueGraphPane != null) {
				removeEditorTab(dialogue_tree_id);
				dialogueGraphPane = null;
				dialogueGraphView = null;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void insertFormViewDataField(JPanel pane) {
		final NPC npc = (NPC) target;
		
		final FieldUpdateListener listener = new NPCFieldUpdater();
		
		npcIcon = createButtonPane(pane, npc.getProject(), npc, NPC.class, npc.getImage(), Spritesheet.Category.monster, listener);
		
		idField = addTextField(pane, "Internal ID: ", npc.id, npc.writable, listener);
		nameField = addTranslatableTextField(pane, "Display name: ", npc.name, npc.writable, listener);
		spawnGroupField = addTextField(pane, "Spawn group ID: ", npc.spawngroup_id, npc.writable, listener);
		factionField = addTextField(pane, "Faction ID: ", npc.faction_id, npc.writable, listener);
		experienceField = addIntegerField(pane, "Experience reward: ", npc.getMonsterExperience(), false, false, listener);
		dialogueBox = addDialogueBox(pane, npc.getProject(), "Initial phrase: ", npc.dialogue, npc.writable, listener);
		droplistBox = addDroplistBox(pane, npc.getProject(), "Droplist / Shop inventory: ", npc.droplist, npc.writable, listener);
		monsterClassBox = addEnumValueBox(pane, "Monster class: ", NPC.MonsterClass.values(), npc.monster_class, npc.writable, listener);
		uniqueBox = addIntegerBasedCheckBox(pane, "Unique", npc.unique, npc.writable, listener);
		moveTypeBox = addEnumValueBox(pane, "Movement type: ", NPC.MovementType.values(), npc.movement_type, npc.writable, listener);
		combatTraitPane = new CollapsiblePanel("Combat traits: ");
		combatTraitPane.setLayout(new JideBoxLayout(combatTraitPane, JideBoxLayout.PAGE_AXIS, 6));
		maxHP = addIntegerField(combatTraitPane, "Max HP: ", npc.max_hp, 1, false, npc.writable, listener);
		maxAP = addIntegerField(combatTraitPane, "Max AP: ", npc.max_ap, 10, false, npc.writable, listener);
		moveCost = addIntegerField(combatTraitPane, "Move cost: ", npc.move_cost, 10, false, npc.writable, listener);
		atkDmgMin = addIntegerField(combatTraitPane, "Attack Damage min: ", npc.attack_damage_min, false, npc.writable, listener);
		atkDmgMax = addIntegerField(combatTraitPane, "Attack Damage max: ", npc.attack_damage_max, false, npc.writable, listener);
		atkCost = addIntegerField(combatTraitPane, "Attack cost: ", npc.attack_cost, 10, false, npc.writable, listener);
		atkChance = addIntegerField(combatTraitPane, "Attack chance: ", npc.attack_chance, false, npc.writable, listener);
		critSkill = addIntegerField(combatTraitPane, "Critical skill: ", npc.critical_skill, false, npc.writable, listener);
		critMult = addDoubleField(combatTraitPane, "Critical multiplier: ", npc.critical_multiplier, npc.writable, listener);
		blockChance = addIntegerField(combatTraitPane, "Block chance: ", npc.block_chance, false, npc.writable, listener);
		dmgRes = addIntegerField(combatTraitPane, "Damage resistance: ", npc.damage_resistance, false, npc.writable, listener);
		hitEffectPane = new CollapsiblePanel("Effect on every hit: ");
		hitEffectPane.setLayout(new JideBoxLayout(hitEffectPane, JideBoxLayout.PAGE_AXIS));
		if (npc.hit_effect == null) {
			hitEffect = new NPC.HitEffect();
		} else {
			hitEffect = npc.hit_effect;
		}
		hitEffectHPMin = addIntegerField(hitEffectPane, "HP bonus min: ", hitEffect.hp_boost_min, true, npc.writable, listener);
		hitEffectHPMax = addIntegerField(hitEffectPane, "HP bonus max: ", hitEffect.hp_boost_max, true, npc.writable, listener);
		hitEffectAPMin = addIntegerField(hitEffectPane, "AP bonus min: ", hitEffect.ap_boost_min, true, npc.writable, listener);
		hitEffectAPMax = addIntegerField(hitEffectPane, "AP bonus max: ", hitEffect.ap_boost_max, true, npc.writable, listener);
		
		CollapsiblePanel hitSourceConditionsPane = new CollapsiblePanel("Actor Conditions applied to the source: ");
		hitSourceConditionsPane.setLayout(new JideBoxLayout(hitSourceConditionsPane, JideBoxLayout.PAGE_AXIS));
		hitSourceConditionsListModel = new SourceTimedConditionsListModel(hitEffect);
		hitSourceConditionsList = new JList(hitSourceConditionsListModel);
		hitSourceConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		hitSourceConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hitSourceConditionsPane.add(new JScrollPane(hitSourceConditionsList), JideBoxLayout.FIX);
		final JPanel hitSourceTimedConditionsEditorPane = new JPanel();
		final JButton createHitSourceCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitSourceCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitSourceConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitEffectSourceCondition = (NPC.TimedConditionEffect) hitSourceConditionsList.getSelectedValue();
				updateHitSourceTimedConditionEditorPane(hitSourceTimedConditionsEditorPane, selectedHitEffectSourceCondition, listener);
			}
		});
		if (npc.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createHitSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					NPC.TimedConditionEffect condition = new NPC.TimedConditionEffect();
					hitSourceConditionsListModel.addItem(condition);
					hitSourceConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(hitSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteHitSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedHitEffectSourceCondition != null) {
						hitSourceConditionsListModel.removeItem(selectedHitEffectSourceCondition);
						selectedHitEffectSourceCondition = null;
						hitSourceConditionsList.clearSelection();
						listener.valueChanged(hitSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createHitSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteHitSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			hitSourceConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		hitSourceTimedConditionsEditorPane.setLayout(new JideBoxLayout(hitSourceTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		hitSourceConditionsPane.add(hitSourceTimedConditionsEditorPane, JideBoxLayout.FIX);
		if (npc.hit_effect == null || npc.hit_effect.conditions_source == null || npc.hit_effect.conditions_source.isEmpty()) {
			hitSourceConditionsPane.collapse();
		}
		hitEffectPane.add(hitSourceConditionsPane, JideBoxLayout.FIX);
		final CollapsiblePanel hitTargetConditionsPane = new CollapsiblePanel("Actor Conditions applied to the target: ");
		hitTargetConditionsPane.setLayout(new JideBoxLayout(hitTargetConditionsPane, JideBoxLayout.PAGE_AXIS));
		hitTargetConditionsListModel = new TargetTimedConditionsListModel(hitEffect);
		hitTargetConditionsList = new JList(hitTargetConditionsListModel);
		hitTargetConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		hitTargetConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hitTargetConditionsPane.add(new JScrollPane(hitTargetConditionsList), JideBoxLayout.FIX);
		final JPanel hitTargetTimedConditionsEditorPane = new JPanel();
		final JButton createHitTargetCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitTargetCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitTargetConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitEffectTargetCondition = (NPC.TimedConditionEffect) hitTargetConditionsList.getSelectedValue();
				updateHitTargetTimedConditionEditorPane(hitTargetTimedConditionsEditorPane, selectedHitEffectTargetCondition, listener);
			}
		});
		if (npc.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createHitTargetCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					NPC.TimedConditionEffect condition = new NPC.TimedConditionEffect();
					hitTargetConditionsListModel.addItem(condition);
					hitTargetConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(hitTargetConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteHitTargetCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedHitEffectTargetCondition != null) {
						hitTargetConditionsListModel.removeItem(selectedHitEffectTargetCondition);
						selectedHitEffectTargetCondition = null;
						hitTargetConditionsList.clearSelection();
						listener.valueChanged(hitTargetConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createHitTargetCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteHitTargetCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			hitTargetConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		hitTargetTimedConditionsEditorPane.setLayout(new JideBoxLayout(hitTargetTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		hitTargetConditionsPane.add(hitTargetTimedConditionsEditorPane, JideBoxLayout.FIX);
		hitEffectPane.add(hitTargetConditionsPane, JideBoxLayout.FIX);
		if (npc.hit_effect == null || npc.hit_effect.conditions_target == null || npc.hit_effect.conditions_target.isEmpty()) {
			hitTargetConditionsPane.collapse();
		}
		combatTraitPane.add(hitEffectPane, JideBoxLayout.FIX);
		
		hitReceivedEffectPane = new CollapsiblePanel("Effect on every hit received: ");
		hitReceivedEffectPane.setLayout(new JideBoxLayout(hitReceivedEffectPane, JideBoxLayout.PAGE_AXIS));
		if (npc.hit_received_effect == null) {
			hitReceivedEffect = new NPC.HitReceivedEffect();
		} else {
			hitReceivedEffect = npc.hit_received_effect;
		}
		hitReceivedEffectHPMin = addIntegerField(hitReceivedEffectPane, "NPC HP bonus min: ", hitReceivedEffect.hp_boost_min, true, npc.writable, listener);
		hitReceivedEffectHPMax = addIntegerField(hitReceivedEffectPane, "NPC HP bonus max: ", hitReceivedEffect.hp_boost_max, true, npc.writable, listener);
		hitReceivedEffectAPMin = addIntegerField(hitReceivedEffectPane, "NPC AP bonus min: ", hitReceivedEffect.ap_boost_min, true, npc.writable, listener);
		hitReceivedEffectAPMax = addIntegerField(hitReceivedEffectPane, "NPC AP bonus max: ", hitReceivedEffect.ap_boost_max, true, npc.writable, listener);
		hitReceivedEffectHPMinTarget = addIntegerField(hitReceivedEffectPane, "Attacker HP bonus min: ", hitReceivedEffect.hp_boost_min_target, true, npc.writable, listener);
		hitReceivedEffectHPMaxTarget = addIntegerField(hitReceivedEffectPane, "Attacker HP bonus max: ", hitReceivedEffect.hp_boost_max_target, true, npc.writable, listener);
		hitReceivedEffectAPMinTarget = addIntegerField(hitReceivedEffectPane, "Attacker AP bonus min: ", hitReceivedEffect.ap_boost_min_target, true, npc.writable, listener);
		hitReceivedEffectAPMaxTarget = addIntegerField(hitReceivedEffectPane, "Attacker AP bonus max: ", hitReceivedEffect.ap_boost_max_target, true, npc.writable, listener);
		
		CollapsiblePanel hitReceivedSourceConditionsPane = new CollapsiblePanel("Actor Conditions applied to this NPC: ");
		hitReceivedSourceConditionsPane.setLayout(new JideBoxLayout(hitReceivedSourceConditionsPane, JideBoxLayout.PAGE_AXIS));
		hitReceivedSourceConditionsListModel = new SourceTimedConditionsListModel(hitReceivedEffect);
		hitReceivedSourceConditionsList = new JList(hitReceivedSourceConditionsListModel);
		hitReceivedSourceConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		hitReceivedSourceConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hitReceivedSourceConditionsPane.add(new JScrollPane(hitReceivedSourceConditionsList), JideBoxLayout.FIX);
		final JPanel hitReceivedSourceTimedConditionsEditorPane = new JPanel();
		final JButton createHitReceivedSourceCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitReceivedSourceCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitReceivedSourceConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitReceivedEffectSourceCondition = (NPC.TimedConditionEffect) hitReceivedSourceConditionsList.getSelectedValue();
				updateHitReceivedSourceTimedConditionEditorPane(hitReceivedSourceTimedConditionsEditorPane, selectedHitReceivedEffectSourceCondition, listener);
			}
		});
		if (npc.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createHitReceivedSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					NPC.TimedConditionEffect condition = new NPC.TimedConditionEffect();
					hitReceivedSourceConditionsListModel.addItem(condition);
					hitReceivedSourceConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(hitReceivedSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteHitReceivedSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedHitReceivedEffectSourceCondition != null) {
						hitReceivedSourceConditionsListModel.removeItem(selectedHitReceivedEffectSourceCondition);
						selectedHitReceivedEffectSourceCondition = null;
						hitReceivedSourceConditionsList.clearSelection();
						listener.valueChanged(hitReceivedSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createHitReceivedSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteHitReceivedSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			hitReceivedSourceConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		hitReceivedSourceTimedConditionsEditorPane.setLayout(new JideBoxLayout(hitReceivedSourceTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		hitReceivedSourceConditionsPane.add(hitReceivedSourceTimedConditionsEditorPane, JideBoxLayout.FIX);
		if (npc.hit_received_effect == null || npc.hit_received_effect.conditions_source == null || npc.hit_received_effect.conditions_source.isEmpty()) {
			hitReceivedSourceConditionsPane.collapse();
		}
		hitReceivedEffectPane.add(hitReceivedSourceConditionsPane, JideBoxLayout.FIX);
		final CollapsiblePanel hitReceivedTargetConditionsPane = new CollapsiblePanel("Actor Conditions applied to the attacker: ");
		hitReceivedTargetConditionsPane.setLayout(new JideBoxLayout(hitReceivedTargetConditionsPane, JideBoxLayout.PAGE_AXIS));
		hitReceivedTargetConditionsListModel = new TargetTimedConditionsListModel(hitReceivedEffect);
		hitReceivedTargetConditionsList = new JList(hitReceivedTargetConditionsListModel);
		hitReceivedTargetConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		hitReceivedTargetConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hitReceivedTargetConditionsPane.add(new JScrollPane(hitReceivedTargetConditionsList), JideBoxLayout.FIX);
		final JPanel hitReceivedTargetTimedConditionsEditorPane = new JPanel();
		final JButton createHitReceivedTargetCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitReceivedTargetCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitReceivedTargetConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitReceivedEffectTargetCondition = (NPC.TimedConditionEffect) hitReceivedTargetConditionsList.getSelectedValue();
				updateHitReceivedTargetTimedConditionEditorPane(hitReceivedTargetTimedConditionsEditorPane, selectedHitReceivedEffectTargetCondition, listener);
			}
		});
		if (npc.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createHitReceivedTargetCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					NPC.TimedConditionEffect condition = new NPC.TimedConditionEffect();
					hitReceivedTargetConditionsListModel.addItem(condition);
					hitReceivedTargetConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(hitReceivedTargetConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteHitReceivedTargetCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedHitReceivedEffectTargetCondition != null) {
						hitReceivedTargetConditionsListModel.removeItem(selectedHitReceivedEffectTargetCondition);
						selectedHitReceivedEffectTargetCondition = null;
						hitReceivedTargetConditionsList.clearSelection();
						listener.valueChanged(hitReceivedTargetConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createHitReceivedTargetCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteHitReceivedTargetCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			hitReceivedTargetConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		hitReceivedTargetTimedConditionsEditorPane.setLayout(new JideBoxLayout(hitReceivedTargetTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		hitReceivedTargetConditionsPane.add(hitReceivedTargetTimedConditionsEditorPane, JideBoxLayout.FIX);
		hitReceivedEffectPane.add(hitReceivedTargetConditionsPane, JideBoxLayout.FIX);
		if (npc.hit_received_effect == null || npc.hit_received_effect.conditions_target == null || npc.hit_received_effect.conditions_target.isEmpty()) {
			hitReceivedTargetConditionsPane.collapse();
		}
		combatTraitPane.add(hitReceivedEffectPane, JideBoxLayout.FIX);

		deathEffectPane = new CollapsiblePanel("Effect when killed: ");
		deathEffectPane.setLayout(new JideBoxLayout(deathEffectPane, JideBoxLayout.PAGE_AXIS));
		if (npc.death_effect == null) {
			deathEffect = new NPC.DeathEffect();
		} else {
			deathEffect = npc.death_effect;
		}
		deathEffectHPMin = addIntegerField(deathEffectPane, "Killer HP bonus min: ", deathEffect.hp_boost_min, true, npc.writable, listener);
		deathEffectHPMax = addIntegerField(deathEffectPane, "Killer HP bonus max: ", deathEffect.hp_boost_max, true, npc.writable, listener);
		deathEffectAPMin = addIntegerField(deathEffectPane, "Killer AP bonus min: ", deathEffect.ap_boost_min, true, npc.writable, listener);
		deathEffectAPMax = addIntegerField(deathEffectPane, "Killer AP bonus max: ", deathEffect.ap_boost_max, true, npc.writable, listener);
		
		CollapsiblePanel deathSourceConditionsPane = new CollapsiblePanel("Actor Conditions applied to the killer: ");
		deathSourceConditionsPane.setLayout(new JideBoxLayout(deathSourceConditionsPane, JideBoxLayout.PAGE_AXIS));
		deathSourceConditionsListModel = new SourceTimedConditionsListModel(deathEffect);
		deathSourceConditionsList = new JList(deathSourceConditionsListModel);
		deathSourceConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		deathSourceConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		deathSourceConditionsPane.add(new JScrollPane(deathSourceConditionsList), JideBoxLayout.FIX);
		final JPanel deathSourceTimedConditionsEditorPane = new JPanel();
		final JButton createDeathSourceCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteDeathSourceCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		deathSourceConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedDeathEffectSourceCondition = (NPC.TimedConditionEffect) deathSourceConditionsList.getSelectedValue();
				updateDeathSourceTimedConditionEditorPane(deathSourceTimedConditionsEditorPane, selectedDeathEffectSourceCondition, listener);
			}
		});
		if (npc.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createDeathSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					NPC.TimedConditionEffect condition = new NPC.TimedConditionEffect();
					deathSourceConditionsListModel.addItem(condition);
					deathSourceConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(deathSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteDeathSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedDeathEffectSourceCondition != null) {
						deathSourceConditionsListModel.removeItem(selectedDeathEffectSourceCondition);
						selectedDeathEffectSourceCondition = null;
						deathSourceConditionsList.clearSelection();
						listener.valueChanged(deathSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createDeathSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteDeathSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			deathSourceConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		deathSourceTimedConditionsEditorPane.setLayout(new JideBoxLayout(deathSourceTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		deathSourceConditionsPane.add(deathSourceTimedConditionsEditorPane, JideBoxLayout.FIX);
		if (npc.death_effect == null || npc.death_effect.conditions_source == null || npc.death_effect.conditions_source.isEmpty()) {
			deathSourceConditionsPane.collapse();
		}
		deathEffectPane.add(deathSourceConditionsPane, JideBoxLayout.FIX);
		combatTraitPane.add(deathEffectPane, JideBoxLayout.FIX);
		
		
		pane.add(combatTraitPane, JideBoxLayout.FIX);
	}
	
	public void updateHitSourceTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (hitSourceConditionBox != null) {
			removeElementListener(hitSourceConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		hitSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		hitSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
		
		hitSourceConditionClear = new JRadioButton("Clear active condition");
		pane.add(hitSourceConditionClear, JideBoxLayout.FIX);
		hitSourceConditionApply = new JRadioButton("Apply condition with magnitude");
		pane.add(hitSourceConditionApply, JideBoxLayout.FIX);
		hitSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
		hitSourceConditionImmunity = new JRadioButton("Give immunity to condition");
		pane.add(hitSourceConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(hitSourceConditionApply);
		radioEffectGroup.add(hitSourceConditionClear);
		radioEffectGroup.add(hitSourceConditionImmunity);
		
		hitSourceConditionTimed = new JRadioButton("For a number of rounds");
		pane.add(hitSourceConditionTimed, JideBoxLayout.FIX);
		hitSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
		hitSourceConditionForever = new JRadioButton("Forever");
		pane.add(hitSourceConditionForever, JideBoxLayout.FIX);
		
		ButtonGroup radioDurationGroup = new ButtonGroup();
		radioDurationGroup.add(hitSourceConditionTimed);
		radioDurationGroup.add(hitSourceConditionForever);
		
		updateHitSourceTimedConditionWidgets(condition);
		
		hitSourceConditionClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitSourceConditionClear, new Boolean(hitSourceConditionClear.isSelected()));
			}
		});
		hitSourceConditionApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitSourceConditionApply, new Boolean(hitSourceConditionApply.isSelected()));
			}
		});
		hitSourceConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitSourceConditionImmunity, new Boolean(hitSourceConditionImmunity.isSelected()));
			}
		});
		
		hitSourceConditionTimed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitSourceConditionTimed, new Boolean(hitSourceConditionTimed.isSelected()));
			}
		});
		hitSourceConditionForever.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitSourceConditionForever, new Boolean(hitSourceConditionForever.isSelected()));
			}
		});
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateHitSourceTimedConditionWidgets(NPC.TimedConditionEffect condition) {

		boolean immunity = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration != null && condition.duration > ActorCondition.DURATION_NONE);
		boolean clear = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration == null || condition.duration == ActorCondition.DURATION_NONE);
		boolean forever = condition.duration != null && condition.duration == ActorCondition.DURATION_FOREVER;
		
		hitSourceConditionClear.setSelected(clear);
		hitSourceConditionApply.setSelected(!clear && !immunity);
		hitSourceConditionMagnitude.setEnabled(!clear && !immunity);
		hitSourceConditionImmunity.setSelected(immunity);
		
		hitSourceConditionTimed.setSelected(!forever);
		hitSourceConditionTimed.setEnabled(!clear);
		hitSourceConditionDuration.setEnabled(!clear && !forever);
		hitSourceConditionForever.setSelected(forever);
		hitSourceConditionForever.setEnabled(!clear);
	}

	
	public void updateHitTargetTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (hitTargetConditionBox != null) {
			removeElementListener(hitTargetConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		hitTargetConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		hitTargetConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
		hitTargetConditionClear = new JRadioButton("Clear active condition");
		pane.add(hitTargetConditionClear, JideBoxLayout.FIX);
		hitTargetConditionApply = new JRadioButton("Apply condition with magnitude");
		pane.add(hitTargetConditionApply, JideBoxLayout.FIX);
		hitTargetConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
		hitTargetConditionImmunity = new JRadioButton("Give immunity to condition");
		pane.add(hitTargetConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(hitTargetConditionApply);
		radioEffectGroup.add(hitTargetConditionClear);
		radioEffectGroup.add(hitTargetConditionImmunity);
		
		hitTargetConditionTimed = new JRadioButton("For a number of rounds");
		pane.add(hitTargetConditionTimed, JideBoxLayout.FIX);
		hitTargetConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
		hitTargetConditionForever = new JRadioButton("Forever");
		pane.add(hitTargetConditionForever, JideBoxLayout.FIX);
		
		ButtonGroup radioDurationGroup = new ButtonGroup();
		radioDurationGroup.add(hitTargetConditionTimed);
		radioDurationGroup.add(hitTargetConditionForever);
		
		updateHitTargetTimedConditionWidgets(condition);
		
		hitTargetConditionClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitTargetConditionClear, new Boolean(hitTargetConditionClear.isSelected()));
			}
		});
		hitTargetConditionApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitTargetConditionApply, new Boolean(hitTargetConditionApply.isSelected()));
			}
		});
		hitTargetConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitTargetConditionImmunity, new Boolean(hitTargetConditionImmunity.isSelected()));
			}
		});
		
		hitTargetConditionTimed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitTargetConditionTimed, new Boolean(hitTargetConditionTimed.isSelected()));
			}
		});
		hitTargetConditionForever.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitTargetConditionForever, new Boolean(hitTargetConditionForever.isSelected()));
			}
		});
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateHitTargetTimedConditionWidgets(NPC.TimedConditionEffect condition) {

		boolean immunity = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration != null && condition.duration > ActorCondition.DURATION_NONE);
		boolean clear = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration == null || condition.duration == ActorCondition.DURATION_NONE);
		boolean forever = condition.duration != null && condition.duration == ActorCondition.DURATION_FOREVER;
		
		hitTargetConditionClear.setSelected(clear);
		hitTargetConditionApply.setSelected(!clear && !immunity);
		hitTargetConditionMagnitude.setEnabled(!clear && !immunity);
		hitTargetConditionImmunity.setSelected(immunity);
		
		hitTargetConditionTimed.setSelected(!forever);
		hitTargetConditionTimed.setEnabled(!clear);
		hitTargetConditionDuration.setEnabled(!clear && !forever);
		hitTargetConditionForever.setSelected(forever);
		hitTargetConditionForever.setEnabled(!clear);
	}
	
	
	public void updateHitReceivedSourceTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (hitReceivedSourceConditionBox != null) {
			removeElementListener(hitReceivedSourceConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		hitReceivedSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		hitReceivedSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
		
		hitReceivedSourceConditionClear = new JRadioButton("Clear active condition");
		pane.add(hitReceivedSourceConditionClear, JideBoxLayout.FIX);
		hitReceivedSourceConditionApply = new JRadioButton("Apply condition with magnitude");
		pane.add(hitReceivedSourceConditionApply, JideBoxLayout.FIX);
		hitReceivedSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
		hitReceivedSourceConditionImmunity = new JRadioButton("Give immunity to condition");
		pane.add(hitReceivedSourceConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(hitReceivedSourceConditionApply);
		radioEffectGroup.add(hitReceivedSourceConditionClear);
		radioEffectGroup.add(hitReceivedSourceConditionImmunity);
		
		hitReceivedSourceConditionTimed = new JRadioButton("For a number of rounds");
		pane.add(hitReceivedSourceConditionTimed, JideBoxLayout.FIX);
		hitReceivedSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
		hitReceivedSourceConditionForever = new JRadioButton("Forever");
		pane.add(hitReceivedSourceConditionForever, JideBoxLayout.FIX);
		
		ButtonGroup radioDurationGroup = new ButtonGroup();
		radioDurationGroup.add(hitReceivedSourceConditionTimed);
		radioDurationGroup.add(hitReceivedSourceConditionForever);
		
		updateHitReceivedSourceTimedConditionWidgets(condition);
		
		hitReceivedSourceConditionClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedSourceConditionClear, new Boolean(hitReceivedSourceConditionClear.isSelected()));
			}
		});
		hitReceivedSourceConditionApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedSourceConditionApply, new Boolean(hitReceivedSourceConditionApply.isSelected()));
			}
		});
		hitReceivedSourceConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedSourceConditionImmunity, new Boolean(hitReceivedSourceConditionImmunity.isSelected()));
			}
		});
		
		hitReceivedSourceConditionTimed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedSourceConditionTimed, new Boolean(hitReceivedSourceConditionTimed.isSelected()));
			}
		});
		hitReceivedSourceConditionForever.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedSourceConditionForever, new Boolean(hitReceivedSourceConditionForever.isSelected()));
			}
		});
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateHitReceivedSourceTimedConditionWidgets(NPC.TimedConditionEffect condition) {

		boolean immunity = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration != null && condition.duration > ActorCondition.DURATION_NONE);
		boolean clear = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration == null || condition.duration == ActorCondition.DURATION_NONE);
		boolean forever = condition.duration != null && condition.duration == ActorCondition.DURATION_FOREVER;
		
		hitReceivedSourceConditionClear.setSelected(clear);
		hitReceivedSourceConditionApply.setSelected(!clear && !immunity);
		hitReceivedSourceConditionMagnitude.setEnabled(!clear && !immunity);
		hitReceivedSourceConditionImmunity.setSelected(immunity);
		
		hitReceivedSourceConditionTimed.setSelected(!forever);
		hitReceivedSourceConditionTimed.setEnabled(!clear);
		hitReceivedSourceConditionDuration.setEnabled(!clear && !forever);
		hitReceivedSourceConditionForever.setSelected(forever);
		hitReceivedSourceConditionForever.setEnabled(!clear);
	}

	
	public void updateHitReceivedTargetTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (hitReceivedTargetConditionBox != null) {
			removeElementListener(hitReceivedTargetConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		hitReceivedTargetConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		hitReceivedTargetConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
		hitReceivedTargetConditionClear = new JRadioButton("Clear active condition");
		pane.add(hitReceivedTargetConditionClear, JideBoxLayout.FIX);
		hitReceivedTargetConditionApply = new JRadioButton("Apply condition with magnitude");
		pane.add(hitReceivedTargetConditionApply, JideBoxLayout.FIX);
		hitReceivedTargetConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
		hitReceivedTargetConditionImmunity = new JRadioButton("Give immunity to condition");
		pane.add(hitReceivedTargetConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(hitReceivedTargetConditionApply);
		radioEffectGroup.add(hitReceivedTargetConditionClear);
		radioEffectGroup.add(hitReceivedTargetConditionImmunity);
		
		hitReceivedTargetConditionTimed = new JRadioButton("For a number of rounds");
		pane.add(hitReceivedTargetConditionTimed, JideBoxLayout.FIX);
		hitReceivedTargetConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
		hitReceivedTargetConditionForever = new JRadioButton("Forever");
		pane.add(hitReceivedTargetConditionForever, JideBoxLayout.FIX);
		
		ButtonGroup radioDurationGroup = new ButtonGroup();
		radioDurationGroup.add(hitReceivedTargetConditionTimed);
		radioDurationGroup.add(hitReceivedTargetConditionForever);
		
		updateHitReceivedTargetTimedConditionWidgets(condition);
		
		hitReceivedTargetConditionClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedTargetConditionClear, new Boolean(hitReceivedTargetConditionClear.isSelected()));
			}
		});
		hitReceivedTargetConditionApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedTargetConditionApply, new Boolean(hitReceivedTargetConditionApply.isSelected()));
			}
		});
		hitReceivedTargetConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedTargetConditionImmunity, new Boolean(hitReceivedTargetConditionImmunity.isSelected()));
			}
		});
		
		hitReceivedTargetConditionTimed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedTargetConditionTimed, new Boolean(hitReceivedTargetConditionTimed.isSelected()));
			}
		});
		hitReceivedTargetConditionForever.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(hitReceivedTargetConditionForever, new Boolean(hitReceivedTargetConditionForever.isSelected()));
			}
		});
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateHitReceivedTargetTimedConditionWidgets(NPC.TimedConditionEffect condition) {

		boolean immunity = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration != null && condition.duration > ActorCondition.DURATION_NONE);
		boolean clear = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration == null || condition.duration == ActorCondition.DURATION_NONE);
		boolean forever = condition.duration != null && condition.duration == ActorCondition.DURATION_FOREVER;
		
		hitReceivedTargetConditionClear.setSelected(clear);
		hitReceivedTargetConditionApply.setSelected(!clear && !immunity);
		hitReceivedTargetConditionMagnitude.setEnabled(!clear && !immunity);
		hitReceivedTargetConditionImmunity.setSelected(immunity);
		
		hitReceivedTargetConditionTimed.setSelected(!forever);
		hitReceivedTargetConditionTimed.setEnabled(!clear);
		hitReceivedTargetConditionDuration.setEnabled(!clear && !forever);
		hitReceivedTargetConditionForever.setSelected(forever);
		hitReceivedTargetConditionForever.setEnabled(!clear);
	}
	
	public void updateDeathSourceTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (deathSourceConditionBox != null) {
			removeElementListener(deathSourceConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		deathSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		deathSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);
		
		deathSourceConditionClear = new JRadioButton("Clear active condition");
		pane.add(deathSourceConditionClear, JideBoxLayout.FIX);
		deathSourceConditionApply = new JRadioButton("Apply condition with magnitude");
		pane.add(deathSourceConditionApply, JideBoxLayout.FIX);
		deathSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
		deathSourceConditionImmunity = new JRadioButton("Give immunity to condition");
		pane.add(deathSourceConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(deathSourceConditionApply);
		radioEffectGroup.add(deathSourceConditionClear);
		radioEffectGroup.add(deathSourceConditionImmunity);
		
		deathSourceConditionTimed = new JRadioButton("For a number of rounds");
		pane.add(deathSourceConditionTimed, JideBoxLayout.FIX);
		deathSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, 1, false, writable, listener);
		deathSourceConditionForever = new JRadioButton("Forever");
		pane.add(deathSourceConditionForever, JideBoxLayout.FIX);
		
		ButtonGroup radioDurationGroup = new ButtonGroup();
		radioDurationGroup.add(deathSourceConditionTimed);
		radioDurationGroup.add(deathSourceConditionForever);
		
		updateDeathSourceTimedConditionWidgets(condition);
		
		deathSourceConditionClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(deathSourceConditionClear, new Boolean(deathSourceConditionClear.isSelected()));
			}
		});
		deathSourceConditionApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(deathSourceConditionApply, new Boolean(deathSourceConditionApply.isSelected()));
			}
		});
		deathSourceConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(deathSourceConditionImmunity, new Boolean(deathSourceConditionImmunity.isSelected()));
			}
		});
		
		deathSourceConditionTimed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(deathSourceConditionTimed, new Boolean(deathSourceConditionTimed.isSelected()));
			}
		});
		deathSourceConditionForever.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(deathSourceConditionForever, new Boolean(deathSourceConditionForever.isSelected()));
			}
		});
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateDeathSourceTimedConditionWidgets(NPC.TimedConditionEffect condition) {

		boolean immunity = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration != null && condition.duration > ActorCondition.DURATION_NONE);
		boolean clear = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration == null || condition.duration == ActorCondition.DURATION_NONE);
		boolean forever = condition.duration != null && condition.duration == ActorCondition.DURATION_FOREVER;
		
		deathSourceConditionClear.setSelected(clear);
		deathSourceConditionApply.setSelected(!clear && !immunity);
		deathSourceConditionMagnitude.setEnabled(!clear && !immunity);
		deathSourceConditionImmunity.setSelected(immunity);
		
		deathSourceConditionTimed.setSelected(!forever);
		deathSourceConditionTimed.setEnabled(!clear);
		deathSourceConditionDuration.setEnabled(!clear && !forever);
		deathSourceConditionForever.setSelected(forever);
		deathSourceConditionForever.setEnabled(!clear);
	}

	public static class TargetTimedConditionsListModel implements ListModel<NPC.TimedConditionEffect> {
		
		NPC.HitEffect source;
		
		public TargetTimedConditionsListModel(NPC.HitEffect effect) {
			this.source = effect;
		}

		@Override
		public int getSize() {
			if (source.conditions_target == null) return 0;
			return source.conditions_target.size();
		}
		
		@Override
		public NPC.TimedConditionEffect getElementAt(int index) {
			if (source.conditions_target == null) return null;
			return source.conditions_target.get(index);
		}

		public void addItem(NPC.TimedConditionEffect item) {
			if (source.conditions_target == null) {
				source.conditions_target = new ArrayList<NPC.TimedConditionEffect>();
			}
			source.conditions_target.add(item);
			int index = source.conditions_target.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(NPC.TimedConditionEffect item) {
			int index = source.conditions_target.indexOf(item);
			source.conditions_target.remove(item);
			if (source.conditions_target.isEmpty()) {
				source.conditions_target = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(NPC.TimedConditionEffect item) {
			int index = source.conditions_target.indexOf(item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
			}
		}
		
		List<ListDataListener> listeners = new CopyOnWriteArrayList<ListDataListener>();
		
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}
		
		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}
	}
	
	public static class SourceTimedConditionsListModel implements ListModel<NPC.TimedConditionEffect> {
		
		NPC.DeathEffect source;
		
		public SourceTimedConditionsListModel(NPC.DeathEffect effect) {
			this.source = effect;
		}

		@Override
		public int getSize() {
			if (source.conditions_source == null) return 0;
			return source.conditions_source.size();
		}
		
		@Override
		public NPC.TimedConditionEffect getElementAt(int index) {
			if (source.conditions_source == null) return null;
			return source.conditions_source.get(index);
		}
		
		public void addItem(NPC.TimedConditionEffect item) {
			if (source.conditions_source == null) {
				source.conditions_source = new ArrayList<NPC.TimedConditionEffect>();
			}
			source.conditions_source.add(item);
			int index = source.conditions_source.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(NPC.TimedConditionEffect item) {
			int index = source.conditions_source.indexOf(item);
			source.conditions_source.remove(item);
			if (source.conditions_source.isEmpty()) {
				source.conditions_source = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(NPC.TimedConditionEffect item) {
			int index = source.conditions_source.indexOf(item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
			}
		}
		
		List<ListDataListener> listeners = new CopyOnWriteArrayList<ListDataListener>();
		
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}
		
		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}
	}
	
	public static class TimedConditionsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				NPC.TimedConditionEffect effect = (NPC.TimedConditionEffect) value;
				
				if (effect.condition != null) {

					boolean immunity = (effect.magnitude == null || effect.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (effect.duration != null && effect.duration > ActorCondition.DURATION_NONE);
					boolean clear = (effect.magnitude == null || effect.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (effect.duration == null || effect.duration == ActorCondition.DURATION_NONE);
					boolean forever = effect.duration != null && effect.duration == ActorCondition.DURATION_FOREVER;
					
					if (clear) {
						label.setIcon(new ImageIcon(effect.condition.getIcon()));
						label.setText(effect.chance+"% chances to clear actor condition "+effect.condition.getDesc());
					} else if (immunity) {
						label.setIcon(new OverlayIcon(effect.condition.getIcon(), DefaultIcons.getImmunityIcon()));
						label.setText(effect.chance+"% chances to give immunity to "+effect.condition.getDesc()+(forever ? " forever" : " for "+effect.duration+" rounds"));
					} else {
						label.setIcon(new ImageIcon(effect.condition.getIcon()));
						label.setText(effect.chance+"% chances to give actor condition "+effect.condition.getDesc()+" x"+effect.magnitude+(forever ? " forever" : " for "+effect.duration+" rounds"));
					}
				} else {
					label.setText("New, undefined actor condition effect.");
				}
			}
			return c;
		}
	}
	
	public static boolean isNull(NPC.HitEffect effect) {
		if (effect.ap_boost_min != null) return false;
		if (effect.ap_boost_max != null) return false;
		if (effect.hp_boost_min != null) return false;
		if (effect.hp_boost_max != null) return false;
		if (effect.conditions_source != null) return false;
		if (effect.conditions_target != null) return false;
		return true;
	}
	
	public static boolean isNull(NPC.HitReceivedEffect effect) {
		if (effect.ap_boost_min != null) return false;
		if (effect.ap_boost_max != null) return false;
		if (effect.hp_boost_min != null) return false;
		if (effect.hp_boost_max != null) return false;
		if (effect.ap_boost_min_target != null) return false;
		if (effect.ap_boost_max_target != null) return false;
		if (effect.hp_boost_min_target != null) return false;
		if (effect.hp_boost_max_target != null) return false;
		if (effect.conditions_source != null) return false;
		if (effect.conditions_target != null) return false;
		return true;
	}
	
	public static boolean isNull(NPC.DeathEffect effect) {
		if (effect.ap_boost_min != null) return false;
		if (effect.ap_boost_max != null) return false;
		if (effect.hp_boost_min != null) return false;
		if (effect.hp_boost_max != null) return false;
		if (effect.conditions_source != null) return false;
		return true;
	}
	
	public class NPCFieldUpdater implements FieldUpdateListener {

		@Override
		public void valueChanged(JComponent source, Object value) {
			NPC npc = (NPC)target;
			boolean updateHit, updateHitReceived, updateDeath;
			updateHit = updateHitReceived = updateDeath = false;
			if (source == idField) {
				//Events caused by cancel an ID edition. Dismiss.
				if (skipNext) {
					skipNext = false;
					return;
				}
				if (target.id.equals((String) value)) return;
				
				if (idChanging()) {
					npc.id = (String) value;
					NPCEditor.this.name = npc.getDesc();
					npc.childrenChanged(new ArrayList<ProjectTreeNode>());
					ATContentStudio.frame.editorChanged(NPCEditor.this);
				} else {
					cancelIdEdit(idField);
					return;
				}
			} else if (source == nameField) {
				npc.name = (String) value;
				NPCEditor.this.name = npc.getDesc();
				npc.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(NPCEditor.this);
			} else if (source == npcIcon) {
				npc.icon_id = (String) value;
				npc.childrenChanged(new ArrayList<ProjectTreeNode>());
				NPCEditor.this.icon = new ImageIcon(npc.getProject().getIcon((String) value));
				ATContentStudio.frame.editorChanged(NPCEditor.this);
				npcIcon.setIcon(new ImageIcon(npc.getProject().getImage((String) value)));
				npcIcon.revalidate();
				npcIcon.repaint();
			} else if (source == spawnGroupField) {
				npc.spawngroup_id = (String) value;
			}  else if (source == factionField) {
				npc.faction_id = (String) value;
			} else if (source == dialogueBox) {
				if (npc.dialogue != null) {
					npc.dialogue.removeBacklink(npc);
				}
				npc.dialogue = (Dialogue) value;
				if (npc.dialogue != null) {
					npc.dialogue_id =npc.dialogue.id;
					npc.dialogue.addBacklink(npc);
				} else {
					npc.dialogue_id = null;
				}
				reloadGraphView(npc);
			} else if (source == droplistBox) {
				if (npc.droplist != null) {
					npc.droplist.removeBacklink(npc);
				}
				npc.droplist = (Droplist) value;
				if (npc.droplist != null) {
					npc.droplist_id = npc.droplist.id;
					npc.droplist.addBacklink(npc);
				} else {
					npc.droplist_id = null;
				}
			} else if (source == monsterClassBox) {
				npc.monster_class = (NPC.MonsterClass) value;
			} else if (source == uniqueBox) {
				npc.unique = (Integer) value;
			} else if (source == moveTypeBox) {
				npc.movement_type = (NPC.MovementType) value;
			} else if (source == maxHP) {
				npc.max_hp = (Integer) value;
			} else if (source == maxAP) {
				npc.max_ap = (Integer) value;
			} else if (source == moveCost) {
				npc.move_cost = (Integer) value;
			} else if (source == atkDmgMin) {
				npc.attack_damage_min = (Integer) value;
			} else if (source == atkDmgMax) {
				npc.attack_damage_max = (Integer) value;
			} else if (source == atkCost) {
				npc.attack_cost = (Integer) value;
			} else if (source == atkChance) {
				npc.attack_chance = (Integer) value;
			} else if (source == critSkill) {
				npc.critical_skill = (Integer) value;
			} else if (source == critMult) {
				npc.critical_multiplier = (Double) value;
			} else if (source == blockChance) {
				npc.block_chance = (Integer) value;
			} else if (source == dmgRes) {
				npc.damage_resistance = (Integer) value;
			} else if (source == hitEffectHPMin) {
				hitEffect.hp_boost_min = (Integer) value;
				updateHit = true;
			} else if (source == hitEffectHPMax) {
				hitEffect.hp_boost_max = (Integer) value;
				updateHit = true;
			} else if (source == hitEffectAPMin) {
				hitEffect.ap_boost_min = (Integer) value;
				updateHit = true;
			} else if (source == hitEffectAPMax) {
				hitEffect.ap_boost_max = (Integer) value;
				updateHit = true;
			} else if (source == hitSourceConditionsList) {
				updateHit = true;
			} else if (source == hitSourceConditionBox) {
				if (selectedHitEffectSourceCondition.condition != null) {
					selectedHitEffectSourceCondition.condition.removeBacklink(npc);
				}
				selectedHitEffectSourceCondition.condition = (ActorCondition) value;
				if (selectedHitEffectSourceCondition.condition != null) {
					selectedHitEffectSourceCondition.condition.addBacklink(npc);
					selectedHitEffectSourceCondition.condition_id = selectedHitEffectSourceCondition.condition.id;
				} else {
					selectedHitEffectSourceCondition.condition_id = null;
				}
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
			} else if (source == hitSourceConditionClear && (Boolean) value) {
				selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectSourceCondition.duration = null;
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			}  else if (source == hitSourceConditionApply && (Boolean) value) {
				selectedHitEffectSourceCondition.magnitude = (Integer) hitSourceConditionMagnitude.getValue();
				selectedHitEffectSourceCondition.duration = hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitSourceConditionDuration.getValue();
				if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectSourceCondition.duration = 1;
				}
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionImmunity && (Boolean) value) {
				selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectSourceCondition.duration = hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitSourceConditionDuration.getValue();
				if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectSourceCondition.duration = 1;
				}
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionMagnitude) {
				selectedHitEffectSourceCondition.magnitude = (Integer) value;
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionTimed && (Boolean) value) {
				selectedHitEffectSourceCondition.duration = (Integer) hitSourceConditionDuration.getValue();
				if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectSourceCondition.duration = 1;
				}
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionForever && (Boolean) value) {
				selectedHitEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionDuration) {
				selectedHitEffectSourceCondition.duration = (Integer) value;
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionChance) {
				selectedHitEffectSourceCondition.chance = (Double) value;
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
			} else if (source == hitTargetConditionsList) {
				updateHit = true;
			} else if (source == hitTargetConditionBox) {
				if (selectedHitEffectTargetCondition.condition != null) {
					selectedHitEffectTargetCondition.condition.removeBacklink(npc);
				}
				selectedHitEffectTargetCondition.condition = (ActorCondition) value;
				if (selectedHitEffectTargetCondition.condition != null) {
					selectedHitEffectTargetCondition.condition_id = selectedHitEffectTargetCondition.condition.id;
					selectedHitEffectTargetCondition.condition.addBacklink(npc);
				} else {
					selectedHitEffectTargetCondition.condition_id = null;
				}
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
			} else if (source == hitTargetConditionClear && (Boolean) value) {
				selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectTargetCondition.duration = null;
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			}  else if (source == hitTargetConditionApply && (Boolean) value) {
				selectedHitEffectTargetCondition.magnitude = (Integer) hitTargetConditionMagnitude.getValue();
				selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
				if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectTargetCondition.duration = 1;
				}
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionImmunity && (Boolean) value) {
				selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
				if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectTargetCondition.duration = 1;
				}
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionMagnitude) {
				selectedHitEffectTargetCondition.magnitude = (Integer) value;
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionTimed && (Boolean) value) {
				selectedHitEffectTargetCondition.duration = (Integer) hitTargetConditionDuration.getValue();
				if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectTargetCondition.duration = 1;
				}
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionForever && (Boolean) value) {
				selectedHitEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionDuration) {
				selectedHitEffectTargetCondition.duration = (Integer) value;
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionChance) {
				selectedHitEffectTargetCondition.chance = (Double) value;
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
			} else if (source == hitReceivedEffectHPMin) {
				hitReceivedEffect.hp_boost_min = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectHPMax) {
				hitReceivedEffect.hp_boost_max = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectAPMin) {
				hitReceivedEffect.ap_boost_min = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectAPMax) {
				hitReceivedEffect.ap_boost_max = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectHPMinTarget) {
				hitReceivedEffect.hp_boost_min_target = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectHPMaxTarget) {
				hitReceivedEffect.hp_boost_max_target = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectAPMinTarget) {
				hitReceivedEffect.ap_boost_min_target = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedEffectAPMaxTarget) {
				hitReceivedEffect.ap_boost_max_target = (Integer) value;
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionsList) {
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionBox) {
				if (selectedHitReceivedEffectSourceCondition.condition != null) {
					selectedHitReceivedEffectSourceCondition.condition.removeBacklink(npc);
				}
				selectedHitReceivedEffectSourceCondition.condition = (ActorCondition) value;
				if (selectedHitReceivedEffectSourceCondition.condition != null) {
					selectedHitReceivedEffectSourceCondition.condition.addBacklink(npc);
					selectedHitReceivedEffectSourceCondition.condition_id = selectedHitReceivedEffectSourceCondition.condition.id;
				} else {
					selectedHitReceivedEffectSourceCondition.condition_id = null;
				}
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
			} else if (source == hitReceivedSourceConditionClear && (Boolean) value) {
				selectedHitReceivedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitReceivedEffectSourceCondition.duration = null;
				updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			}  else if (source == hitReceivedSourceConditionApply && (Boolean) value) {
				selectedHitReceivedEffectSourceCondition.magnitude = (Integer) hitReceivedSourceConditionMagnitude.getValue();
				selectedHitReceivedEffectSourceCondition.duration = hitReceivedSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedSourceConditionDuration.getValue();
				if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitReceivedEffectSourceCondition.duration = 1;
				}
				updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionImmunity && (Boolean) value) {
				selectedHitReceivedEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitReceivedEffectSourceCondition.duration = hitReceivedSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedSourceConditionDuration.getValue();
				if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitReceivedEffectSourceCondition.duration = 1;
				}
				updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionMagnitude) {
				selectedHitReceivedEffectSourceCondition.magnitude = (Integer) value;
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionTimed && (Boolean) value) {
				selectedHitReceivedEffectSourceCondition.duration = (Integer) hitReceivedSourceConditionDuration.getValue();
				if (selectedHitReceivedEffectSourceCondition.duration == null || selectedHitReceivedEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitReceivedEffectSourceCondition.duration = 1;
				}
				updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionForever && (Boolean) value) {
				selectedHitReceivedEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
				updateHitReceivedSourceTimedConditionWidgets(selectedHitReceivedEffectSourceCondition);
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionDuration) {
				selectedHitReceivedEffectSourceCondition.duration = (Integer) value;
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedSourceConditionChance) {
				selectedHitReceivedEffectSourceCondition.chance = (Double) value;
				hitReceivedSourceConditionsListModel.itemChanged(selectedHitReceivedEffectSourceCondition);
			} else if (source == hitReceivedTargetConditionsList) {
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionBox) {
				if (selectedHitReceivedEffectTargetCondition.condition != null) {
					selectedHitReceivedEffectTargetCondition.condition.removeBacklink(npc);
				}
				selectedHitReceivedEffectTargetCondition.condition = (ActorCondition) value;
				if (selectedHitReceivedEffectTargetCondition.condition != null) {
					selectedHitReceivedEffectTargetCondition.condition_id = selectedHitReceivedEffectTargetCondition.condition.id;
					selectedHitReceivedEffectTargetCondition.condition.addBacklink(npc);
				} else {
					selectedHitReceivedEffectTargetCondition.condition_id = null;
				}
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
			} else if (source == hitReceivedTargetConditionClear && (Boolean) value) {
				selectedHitReceivedEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitReceivedEffectTargetCondition.duration = null;
				updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			}  else if (source == hitReceivedTargetConditionApply && (Boolean) value) {
				selectedHitReceivedEffectTargetCondition.magnitude = (Integer) hitReceivedTargetConditionMagnitude.getValue();
				selectedHitReceivedEffectTargetCondition.duration = hitReceivedTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedTargetConditionDuration.getValue();
				if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitReceivedEffectTargetCondition.duration = 1;
				}
				updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionImmunity && (Boolean) value) {
				selectedHitReceivedEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitReceivedEffectTargetCondition.duration = hitReceivedTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitReceivedTargetConditionDuration.getValue();
				if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitReceivedEffectTargetCondition.duration = 1;
				}
				updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionMagnitude) {
				selectedHitReceivedEffectTargetCondition.magnitude = (Integer) value;
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionTimed && (Boolean) value) {
				selectedHitReceivedEffectTargetCondition.duration = (Integer) hitReceivedTargetConditionDuration.getValue();
				if (selectedHitReceivedEffectTargetCondition.duration == null || selectedHitReceivedEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitReceivedEffectTargetCondition.duration = 1;
				}
				updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionForever && (Boolean) value) {
				selectedHitReceivedEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
				updateHitReceivedTargetTimedConditionWidgets(selectedHitReceivedEffectTargetCondition);
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionDuration) {
				selectedHitReceivedEffectTargetCondition.duration = (Integer) value;
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
				updateHitReceived = true;
			} else if (source == hitReceivedTargetConditionChance) {
				selectedHitReceivedEffectTargetCondition.chance = (Double) value;
				hitReceivedTargetConditionsListModel.itemChanged(selectedHitReceivedEffectTargetCondition);
			} else if (source == deathEffectHPMin) {
				deathEffect.hp_boost_min = (Integer) value;
				updateDeath = true;
			} else if (source == deathEffectHPMax) {
				deathEffect.hp_boost_max = (Integer) value;
				updateDeath = true;
			} else if (source == deathEffectAPMin) {
				deathEffect.ap_boost_min = (Integer) value;
				updateDeath = true;
			} else if (source == deathEffectAPMax) {
				deathEffect.ap_boost_max = (Integer) value;
				updateDeath = true;
			} else if (source == deathSourceConditionsList) {
				updateDeath = true;
			} else if (source == deathSourceConditionBox) {
				if (selectedDeathEffectSourceCondition.condition != null) {
					selectedDeathEffectSourceCondition.condition.removeBacklink(npc);
				}
				selectedDeathEffectSourceCondition.condition = (ActorCondition) value;
				if (selectedDeathEffectSourceCondition.condition != null) {
					selectedDeathEffectSourceCondition.condition.addBacklink(npc);
					selectedDeathEffectSourceCondition.condition_id = selectedDeathEffectSourceCondition.condition.id;
				} else {
					selectedDeathEffectSourceCondition.condition_id = null;
				}
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
			} else if (source == deathSourceConditionClear && (Boolean) value) {
				selectedDeathEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedDeathEffectSourceCondition.duration = null;
				updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			}  else if (source == deathSourceConditionApply && (Boolean) value) {
				selectedDeathEffectSourceCondition.magnitude = (Integer) deathSourceConditionMagnitude.getValue();
				selectedDeathEffectSourceCondition.duration = deathSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) deathSourceConditionDuration.getValue();
				if (selectedDeathEffectSourceCondition.duration == null || selectedDeathEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedDeathEffectSourceCondition.duration = 1;
				}
				updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			} else if (source == deathSourceConditionImmunity && (Boolean) value) {
				selectedDeathEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedDeathEffectSourceCondition.duration = deathSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) deathSourceConditionDuration.getValue();
				if (selectedDeathEffectSourceCondition.duration == null || selectedDeathEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedDeathEffectSourceCondition.duration = 1;
				}
				updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			} else if (source == deathSourceConditionMagnitude) {
				selectedDeathEffectSourceCondition.magnitude = (Integer) value;
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			} else if (source == deathSourceConditionTimed && (Boolean) value) {
				selectedDeathEffectSourceCondition.duration = (Integer) deathSourceConditionDuration.getValue();
				if (selectedDeathEffectSourceCondition.duration == null || selectedDeathEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedDeathEffectSourceCondition.duration = 1;
				}
				updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			} else if (source == deathSourceConditionForever && (Boolean) value) {
				selectedDeathEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
				updateDeathSourceTimedConditionWidgets(selectedDeathEffectSourceCondition);
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			} else if (source == deathSourceConditionDuration) {
				selectedDeathEffectSourceCondition.duration = (Integer) value;
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
				updateDeath = true;
			} else if (source == deathSourceConditionChance) {
				selectedDeathEffectSourceCondition.chance = (Double) value;
				deathSourceConditionsListModel.itemChanged(selectedDeathEffectSourceCondition);
			}

			if (updateHit) {
				if (isNull(hitEffect)) {
					npc.hit_effect = null;
				} else {
					npc.hit_effect = hitEffect;
				}
			}
			if (updateHitReceived) {
				if (isNull(hitReceivedEffect)) {
					npc.hit_received_effect = null;
				} else {
					npc.hit_received_effect = hitReceivedEffect;
				}
			}
			if (updateDeath) {
				if (isNull(deathEffect)) {
					npc.death_effect = null;
				} else {
					npc.death_effect = deathEffect;
				}
			}
			
			experienceField.setValue(npc.getMonsterExperience());
			
			if (npc.state != GameDataElement.State.modified) {
				npc.state = GameDataElement.State.modified;
				NPCEditor.this.name = npc.getDesc();
				npc.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(NPCEditor.this);
			}
			updateJsonViewText(npc.toJsonString());
			
		}
		
	}


}
