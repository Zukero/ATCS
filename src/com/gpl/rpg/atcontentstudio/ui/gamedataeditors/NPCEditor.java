package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
import com.gpl.rpg.atcontentstudio.ui.gamedataeditors.dialoguetree.DialogueGraphView;
import com.jidesoft.swing.JideBoxLayout;

public class NPCEditor extends JSONElementEditor {

	private static final long serialVersionUID = 4001483665523721800L;
	
	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	private static final String dialogue_tree_id = "Dialogue Tree";

	private NPC.TimedConditionEffect selectedHitEffectSourceCondition;
	private NPC.TimedConditionEffect selectedHitEffectTargetCondition;
	
	private JButton npcIcon;
	private JTextField idField;
	private JTextField nameField;
	private JTextField spawnGroupField;
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
	private MyComboBox sourceConditionBox;
	private JSpinner sourceConditionMagnitude;
	private JSpinner sourceConditionDuration;
	private JSpinner sourceConditionChance;
	
	private TargetTimedConditionsListModel hitTargetConditionsListModel;
	@SuppressWarnings("rawtypes")
	private JList hitTargetConditionsList;
	private MyComboBox targetConditionBox;
	private JSpinner targetConditionMagnitude;
	private JSpinner targetConditionDuration;
	private JSpinner targetConditionChance;

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
		experienceField = addIntegerField(pane, "Experience reward: ", npc.getMonsterExperience(), false, false, listener);
		dialogueBox = addDialogueBox(pane, npc.getProject(), "Initial phrase: ", npc.dialogue, npc.writable, listener);
		droplistBox = addDroplistBox(pane, npc.getProject(), "Droplist / Shop inventory: ", npc.droplist, npc.writable, listener);
		monsterClassBox = addEnumValueBox(pane, "Monster class: ", NPC.MonsterClass.values(), npc.monster_class, npc.writable, listener);
		uniqueBox = addIntegerBasedCheckBox(pane, "Unique", npc.unique, npc.writable, listener);
		moveTypeBox = addEnumValueBox(pane, "Movement type: ", NPC.MovementType.values(), npc.movement_type, npc.writable, listener);
		combatTraitPane = new CollapsiblePanel("Combat traits: ");
		combatTraitPane.setLayout(new JideBoxLayout(combatTraitPane, JideBoxLayout.PAGE_AXIS, 6));
		maxHP = addIntegerField(combatTraitPane, "Max HP: ", npc.max_hp, false, npc.writable, listener);
		maxAP = addIntegerField(combatTraitPane, "Max AP: ", npc.max_ap, false, npc.writable, listener);
		moveCost = addIntegerField(combatTraitPane, "Move cost: ", npc.move_cost, false, npc.writable, listener);
		atkDmgMin = addIntegerField(combatTraitPane, "Attack Damage min: ", npc.attack_damage_min, false, npc.writable, listener);
		atkDmgMax = addIntegerField(combatTraitPane, "Attack Damage max: ", npc.attack_damage_max, false, npc.writable, listener);
		atkCost = addIntegerField(combatTraitPane, "Attack cost: ", npc.attack_cost, false, npc.writable, listener);
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
		final JPanel sourceTimedConditionsEditorPane = new JPanel();
		final JButton createHitSourceCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitSourceCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitSourceConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitEffectSourceCondition = (NPC.TimedConditionEffect) hitSourceConditionsList.getSelectedValue();
				updateSourceTimedConditionEditorPane(sourceTimedConditionsEditorPane, selectedHitEffectSourceCondition, listener);
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
		sourceTimedConditionsEditorPane.setLayout(new JideBoxLayout(sourceTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		hitSourceConditionsPane.add(sourceTimedConditionsEditorPane, JideBoxLayout.FIX);
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
		final JPanel targetTimedConditionsEditorPane = new JPanel();
		final JButton createHitTargetCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitTargetCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitTargetConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitEffectTargetCondition = (NPC.TimedConditionEffect) hitTargetConditionsList.getSelectedValue();
				updateTargetTimedConditionEditorPane(targetTimedConditionsEditorPane, selectedHitEffectTargetCondition, listener);
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
		targetTimedConditionsEditorPane.setLayout(new JideBoxLayout(targetTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		hitTargetConditionsPane.add(targetTimedConditionsEditorPane, JideBoxLayout.FIX);
		hitEffectPane.add(hitTargetConditionsPane, JideBoxLayout.FIX);
		if (npc.hit_effect == null || npc.hit_effect.conditions_target == null || npc.hit_effect.conditions_target.isEmpty()) {
			hitTargetConditionsPane.collapse();
		}
		combatTraitPane.add(hitEffectPane, JideBoxLayout.FIX);
		
		pane.add(combatTraitPane, JideBoxLayout.FIX);
	}
	
	public void updateSourceTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, FieldUpdateListener listener) {
		pane.removeAll();
		if (sourceConditionBox != null) {
			removeElementListener(sourceConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		sourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		sourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude, false, writable, listener);
		sourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, false, writable, listener);
		sourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

		pane.revalidate();
		pane.repaint();
	}
	
	public void updateTargetTimedConditionEditorPane(JPanel pane, NPC.TimedConditionEffect condition, FieldUpdateListener listener) {
		pane.removeAll();
		if (targetConditionBox != null) {
			removeElementListener(targetConditionBox);
		}
		
		boolean writable = ((NPC)target).writable;
		Project proj = ((NPC)target).getProject();
		
		targetConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		targetConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude, false, writable, listener);
		targetConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, false, writable, listener);
		targetConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

		pane.revalidate();
		pane.repaint();
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
		
		NPC.HitEffect source;
		
		public SourceTimedConditionsListModel(NPC.HitEffect effect) {
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
					label.setIcon(new ImageIcon(effect.condition.getIcon()));
					label.setText(effect.chance+"% chances to give "+effect.duration+" rounds of "+effect.condition.getDesc()+" x"+effect.magnitude);
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
	
	public class NPCFieldUpdater implements FieldUpdateListener {

		@Override
		public void valueChanged(JComponent source, Object value) {
			NPC npc = (NPC)target;
			boolean updateHit = false;
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
				//TODO
			} else if (source == sourceConditionBox) {
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
			} else if (source == sourceConditionMagnitude) {
				selectedHitEffectSourceCondition.magnitude = (Integer) value;
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
			} else if (source == sourceConditionDuration) {
				selectedHitEffectSourceCondition.duration = (Integer) value;
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
			} else if (source == sourceConditionChance) {
				selectedHitEffectSourceCondition.chance = (Double) value;
				hitSourceConditionsListModel.itemChanged(selectedHitEffectSourceCondition);
			} else if (source == hitTargetConditionsList) {
				//TODO
			} else if (source == targetConditionBox) {
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
			} else if (source == targetConditionMagnitude) {
				selectedHitEffectTargetCondition.magnitude = (Integer) value;
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
			} else if (source == targetConditionDuration) {
				selectedHitEffectTargetCondition.duration = (Integer) value;
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
			} else if (source == targetConditionChance) {
				selectedHitEffectTargetCondition.chance = (Double) value;
				hitTargetConditionsListModel.itemChanged(selectedHitEffectTargetCondition);
			}

			if (updateHit) {
				if (isNull(hitEffect)) {
					npc.hit_effect = null;
				} else {
					npc.hit_effect = hitEffect;
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
