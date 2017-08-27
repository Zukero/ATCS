package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

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
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.model.gamedata.ItemCategory;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.IntegerBasedCheckBox;
import com.jidesoft.swing.JideBoxLayout;

public class ItemEditor extends JSONElementEditor {

	private static final long serialVersionUID = 7538154592029351986L;
	
	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	
	private static final String killLabel = "Effect on every kill: ";
	private static final String useLabel = "Effect on use: ";
	
	
	private Item.ConditionEffect selectedEquipEffectCondition;
	private Item.TimedConditionEffect selectedHitEffectSourceCondition;
	private Item.TimedConditionEffect selectedHitEffectTargetCondition;
	private Item.TimedConditionEffect selectedKillEffectCondition;

	
	private JButton itemIcon;
	private JTextField idField;
	private JTextField nameField;
	private JTextField descriptionField;
	@SuppressWarnings("rawtypes")
	private JComboBox typeBox;
	private IntegerBasedCheckBox manualPriceBox;
	private JSpinner baseCostField;
	private MyComboBox categoryBox;
	private Integer baseManualPrice = null;
	
	private CollapsiblePanel equipEffectPane;
	private Item.EquipEffect equipEffect;
	private JSpinner equipDmgMin;
	private JSpinner equipDmgMax;
	private JSpinner equipBoostHP;
	private JSpinner equipBoostAP;
	private JSpinner equipBoostAC;
	private JSpinner equipBoostBC;
	private JSpinner equipBoostCS;
	private JSpinner equipSetCM;
	private JSpinner equipBoostDR;
	private JSpinner equipIncMoveCost;
	private JSpinner equipIncUseCost;
	private JSpinner equipIncReequipCost;
	private JSpinner equipIncAttackCost;
	private ConditionsListModel equipConditionsModel;
	@SuppressWarnings("rawtypes")
	private JList equipConditionsList;
	private MyComboBox equipConditionBox;
	private JRadioButton equipConditionWithMagnitude;
	private JRadioButton equipConditionImmunity;
	private JSpinner equipConditionMagnitude;

	private CollapsiblePanel hitEffectPane;
	private Item.HitEffect hitEffect;
	private JSpinner hitHPMin;
	private JSpinner hitHPMax;
	private JSpinner hitAPMin;
	private JSpinner hitAPMax;
	private SourceTimedConditionsListModel hitSourceConditionsModel;
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
	private TargetTimedConditionsListModel hitTargetConditionsModel;
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
	
	private CollapsiblePanel killEffectPane;
	private Item.KillEffect killEffect;
	private JSpinner killHPMin;
	private JSpinner killHPMax;
	private JSpinner killAPMin;
	private JSpinner killAPMax;
	private SourceTimedConditionsListModel killSourceConditionsModel;
	@SuppressWarnings("rawtypes")
	private JList killSourceConditionsList;
	private MyComboBox killSourceConditionBox;
	private JSpinner killSourceConditionChance;
	private JRadioButton killSourceConditionClear;
	private JRadioButton killSourceConditionApply;
	private JRadioButton killSourceConditionImmunity;
	private JSpinner killSourceConditionMagnitude;
	private JRadioButton killSourceConditionTimed;
	private JRadioButton killSourceConditionForever;
	private JSpinner killSourceConditionDuration;
	
	
	public ItemEditor(Item item) {
		super(item, item.getDesc(), item.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void insertFormViewDataField(JPanel pane) {

		final Item item = (Item) target;
		
		final FieldUpdateListener listener = new ItemFieldUpdater();
		
		itemIcon = createButtonPane(pane, item.getProject(), item, Item.class, item.getImage(), Spritesheet.Category.item, listener);
		
		idField = addTextField(pane, "Internal ID: ", item.id, item.writable, listener);
		nameField = addTranslatableTextField(pane, "Display name: ", item.name, item.writable, listener);
		descriptionField = addTranslatableTextField(pane, "Description: ", item.description, item.writable, listener);
		typeBox = addEnumValueBox(pane, "Type: ", Item.DisplayType.values(), item.display_type, item.writable, listener);
		manualPriceBox = addIntegerBasedCheckBox(pane, "Has manual price", item.has_manual_price, item.writable, listener);
		baseManualPrice = item.base_market_cost;
		baseCostField = addIntegerField(pane, "Base market cost: ", (item.has_manual_price != null && item.has_manual_price == 1) ? item.base_market_cost : item.computePrice(), false, item.writable, listener);
		if (!manualPriceBox.isSelected()) {
			baseCostField.setEnabled(false);
		}
		categoryBox = addItemCategoryBox(pane, item.getProject(), "Category: ", item.category, item.writable, listener);
		
		equipEffectPane = new CollapsiblePanel("Effect when equipped: ");
		equipEffectPane.setLayout(new JideBoxLayout(equipEffectPane, JideBoxLayout.PAGE_AXIS));
		if (item.equip_effect == null) {
			equipEffect = new Item.EquipEffect();
		} else {
			equipEffect = item.equip_effect;
		}
		equipDmgMin = addIntegerField(equipEffectPane, "Attack Damage min: ", equipEffect.damage_boost_min, true, item.writable, listener);
		equipDmgMax = addIntegerField(equipEffectPane, "Attack Damage max: ", equipEffect.damage_boost_max, true, item.writable, listener);
		equipBoostHP = addIntegerField(equipEffectPane, "Boost max HP: ", equipEffect.max_hp_boost, true, item.writable, listener);
		equipBoostAP = addIntegerField(equipEffectPane, "Boost max AP: ", equipEffect.max_ap_boost, true, item.writable, listener);
		equipBoostAC = addIntegerField(equipEffectPane, "Boost attack chance: ", equipEffect.increase_attack_chance, true, item.writable, listener);
		equipBoostBC = addIntegerField(equipEffectPane, "Boost block chance: ", equipEffect.increase_block_chance, true, item.writable, listener);
		equipBoostCS = addIntegerField(equipEffectPane, "Boost critical skill: ", equipEffect.increase_critical_skill, true, item.writable, listener);
		equipSetCM = addDoubleField(equipEffectPane, "Critical multiplier: ", equipEffect.critical_multiplier, item.writable, listener);
		equipBoostDR = addIntegerField(equipEffectPane, "Boost damage resistance: ", equipEffect.increase_damage_resistance, true, item.writable, listener);
		equipIncMoveCost = addIntegerField(equipEffectPane, "Increase move cost: ", equipEffect.increase_move_cost, true, item.writable, listener);
		equipIncUseCost = addIntegerField(equipEffectPane, "Increase item use cost: ", equipEffect.increase_use_item_cost, true, item.writable, listener);
		equipIncReequipCost = addIntegerField(equipEffectPane, "Increase reequip cost: ", equipEffect.increase_reequip_cost, true, item.writable, listener);
		equipIncAttackCost = addIntegerField(equipEffectPane, "Increase attack cost: ", equipEffect.increase_attack_cost, true, item.writable, listener);
		CollapsiblePanel equipConditionsPane = new CollapsiblePanel("Actor Conditions applied when equipped: ");
		equipConditionsPane.setLayout(new JideBoxLayout(equipConditionsPane, JideBoxLayout.PAGE_AXIS));
		equipConditionsModel = new ConditionsListModel(equipEffect);
		equipConditionsList = new JList(equipConditionsModel);
		equipConditionsList.setCellRenderer(new ConditionsCellRenderer());
		equipConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		equipConditionsPane.add(new JScrollPane(equipConditionsList), JideBoxLayout.FIX);
		final JPanel equipConditionsEditorPane = new JPanel();
		final JButton createEquipCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteEquipCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		equipConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedEquipEffectCondition = (Item.ConditionEffect) equipConditionsList.getSelectedValue();
				if (selectedEquipEffectCondition == null) {
					deleteEquipCondition.setEnabled(false);
				} else {
					deleteEquipCondition.setEnabled(true);
				}
				updateEquipConditionEditorPane(equipConditionsEditorPane, selectedEquipEffectCondition, listener);
			}
		});
		if (item.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createEquipCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Item.ConditionEffect condition = new Item.ConditionEffect();
					equipConditionsModel.addItem(condition);
					equipConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(equipConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteEquipCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedEquipEffectCondition != null) {
						equipConditionsModel.removeItem(selectedEquipEffectCondition);
						selectedEquipEffectCondition = null;
						equipConditionsList.clearSelection();
						listener.valueChanged(equipConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createEquipCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteEquipCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			equipConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		equipConditionsEditorPane.setLayout(new JideBoxLayout(equipConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		equipConditionsPane.add(equipConditionsEditorPane, JideBoxLayout.FIX);
		if (item.equip_effect == null || item.equip_effect.conditions == null || item.equip_effect.conditions.isEmpty()) {
			equipConditionsPane.collapse();
		}
		equipEffectPane.add(equipConditionsPane, JideBoxLayout.FIX);
		pane.add(equipEffectPane, JideBoxLayout.FIX);
		if (item.equip_effect == null) {
			equipEffectPane.collapse();
		}
		
		hitEffectPane = new CollapsiblePanel("Effect on every hit: ");
		hitEffectPane.setLayout(new JideBoxLayout(hitEffectPane, JideBoxLayout.PAGE_AXIS));
		if (item.hit_effect == null) {
			hitEffect = new Item.HitEffect();
		} else {
			hitEffect = item.hit_effect;
		}
		hitHPMin = addIntegerField(hitEffectPane, "HP bonus min: ", hitEffect.hp_boost_min, true, item.writable, listener);
		hitHPMax = addIntegerField(hitEffectPane, "HP bonus max: ", hitEffect.hp_boost_max, true, item.writable, listener);
		hitAPMin = addIntegerField(hitEffectPane, "AP bonus min: ", hitEffect.ap_boost_min, true, item.writable, listener);
		hitAPMax = addIntegerField(hitEffectPane, "AP bonus max: ", hitEffect.ap_boost_max, true, item.writable, listener);
		final CollapsiblePanel hitSourceConditionsPane = new CollapsiblePanel("Actor Conditions applied to the source: ");
		hitSourceConditionsPane.setLayout(new JideBoxLayout(hitSourceConditionsPane, JideBoxLayout.PAGE_AXIS));
		hitSourceConditionsModel = new SourceTimedConditionsListModel(hitEffect);
		hitSourceConditionsList = new JList(hitSourceConditionsModel);
		hitSourceConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		hitSourceConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hitSourceConditionsPane.add(new JScrollPane(hitSourceConditionsList), JideBoxLayout.FIX);
		final JPanel sourceTimedConditionsEditorPane = new JPanel();
		final JButton createHitSourceCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitSourceCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitSourceConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitEffectSourceCondition = (Item.TimedConditionEffect) hitSourceConditionsList.getSelectedValue();
				updateHitSourceTimedConditionEditorPane(sourceTimedConditionsEditorPane, selectedHitEffectSourceCondition, listener);
				if (selectedHitEffectSourceCondition == null) {
					deleteHitSourceCondition.setEnabled(false);
				} else {
					deleteHitSourceCondition.setEnabled(true);
				}
			}
		});
		if (item.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createHitSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Item.TimedConditionEffect condition = new Item.TimedConditionEffect();
					hitSourceConditionsModel.addItem(condition);
					hitSourceConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(hitSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteHitSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedHitEffectSourceCondition != null) {
						hitSourceConditionsModel.removeItem(selectedHitEffectSourceCondition);
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
		if (item.hit_effect == null || item.hit_effect.conditions_source == null || item.hit_effect.conditions_source.isEmpty()) {
			hitSourceConditionsPane.collapse();
		}
		hitEffectPane.add(hitSourceConditionsPane, JideBoxLayout.FIX);
		final CollapsiblePanel hitTargetConditionsPane = new CollapsiblePanel("Actor Conditions applied to the target: ");
		hitTargetConditionsPane.setLayout(new JideBoxLayout(hitTargetConditionsPane, JideBoxLayout.PAGE_AXIS));
		hitTargetConditionsModel = new TargetTimedConditionsListModel(hitEffect);
		hitTargetConditionsList = new JList(hitTargetConditionsModel);
		hitTargetConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		hitTargetConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hitTargetConditionsPane.add(new JScrollPane(hitTargetConditionsList), JideBoxLayout.FIX);
		final JPanel targetTimedConditionsEditorPane = new JPanel();
		final JButton createHitTargetCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteHitTargetCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		hitTargetConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedHitEffectTargetCondition = (Item.TimedConditionEffect) hitTargetConditionsList.getSelectedValue();
				updateHitTargetTimedConditionEditorPane(targetTimedConditionsEditorPane, selectedHitEffectTargetCondition, listener);
				if (selectedHitEffectTargetCondition == null) {
					deleteHitTargetCondition.setEnabled(false);
				} else {
					deleteHitTargetCondition.setEnabled(true);
				}
			}
		});
		if (item.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createHitTargetCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Item.TimedConditionEffect condition = new Item.TimedConditionEffect();
					hitTargetConditionsModel.addItem(condition);
					hitTargetConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(hitTargetConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteHitTargetCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedHitEffectTargetCondition != null) {
						hitTargetConditionsModel.removeItem(selectedHitEffectTargetCondition);
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
		if (item.hit_effect == null || item.hit_effect.conditions_target == null || item.hit_effect.conditions_target.isEmpty()) {
			hitTargetConditionsPane.collapse();
		}
		hitEffectPane.add(hitTargetConditionsPane, JideBoxLayout.FIX);
		if (item.hit_effect == null) {
			hitEffectPane.collapse();
		}
		pane.add(hitEffectPane, JideBoxLayout.FIX);
		
		
		
		killEffectPane = new CollapsiblePanel(killLabel);
		killEffectPane.setLayout(new JideBoxLayout(killEffectPane, JideBoxLayout.PAGE_AXIS));
		if (item.kill_effect == null) {
			killEffect = new Item.KillEffect();
		} else {
			killEffect = item.kill_effect;
		}
		killHPMin = addIntegerField(killEffectPane, "HP bonus min: ", killEffect.hp_boost_min, true, item.writable, listener);
		killHPMax = addIntegerField(killEffectPane, "HP bonus max: ", killEffect.hp_boost_max, true, item.writable, listener);
		killAPMin = addIntegerField(killEffectPane, "AP bonus min: ", killEffect.ap_boost_min, true, item.writable, listener);
		killAPMax = addIntegerField(killEffectPane, "AP bonus max: ", killEffect.ap_boost_max, true, item.writable, listener);
		final CollapsiblePanel killSourceConditionsPane = new CollapsiblePanel("Actor Conditions applied to the source: ");
		killSourceConditionsPane.setLayout(new JideBoxLayout(killSourceConditionsPane, JideBoxLayout.PAGE_AXIS));
		killSourceConditionsModel = new SourceTimedConditionsListModel(killEffect);
		killSourceConditionsList = new JList(killSourceConditionsModel);
		killSourceConditionsList.setCellRenderer(new TimedConditionsCellRenderer());
		killSourceConditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		killSourceConditionsPane.add(new JScrollPane(killSourceConditionsList), JideBoxLayout.FIX);
		final JPanel killSourceTimedConditionsEditorPane = new JPanel();
		final JButton createKillSourceCondition = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteKillSourceCondition = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		killSourceConditionsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedKillEffectCondition = (Item.TimedConditionEffect) killSourceConditionsList.getSelectedValue();
				updateKillSourceTimedConditionEditorPane(killSourceTimedConditionsEditorPane, selectedKillEffectCondition, listener);
				if (selectedKillEffectCondition == null) {
					deleteKillSourceCondition.setEnabled(false);
				} else {
					deleteKillSourceCondition.setEnabled(true);
				}
			}
		});
		if (item.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createKillSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Item.TimedConditionEffect condition = new Item.TimedConditionEffect();
					killSourceConditionsModel.addItem(condition);
					killSourceConditionsList.setSelectedValue(condition, true);
					listener.valueChanged(killSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteKillSourceCondition.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedKillEffectCondition != null) {
						killSourceConditionsModel.removeItem(selectedKillEffectCondition);
						selectedKillEffectCondition = null;
						killSourceConditionsList.clearSelection();
						listener.valueChanged(killSourceConditionsList, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createKillSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(deleteKillSourceCondition, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			killSourceConditionsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		killSourceTimedConditionsEditorPane.setLayout(new JideBoxLayout(killSourceTimedConditionsEditorPane, JideBoxLayout.PAGE_AXIS));
		killSourceConditionsPane.add(killSourceTimedConditionsEditorPane, JideBoxLayout.FIX);
		if (item.kill_effect == null || item.kill_effect.conditions_source == null || item.kill_effect.conditions_source.isEmpty()) {
			killSourceConditionsPane.collapse();
		}
		killEffectPane.add(killSourceConditionsPane, JideBoxLayout.FIX);
		if (item.kill_effect == null) {
			killEffectPane.collapse();
		}
		pane.add(killEffectPane, JideBoxLayout.FIX);
		
		if (item.category == null || item.category.action_type == null || item.category.action_type == ItemCategory.ActionType.none) {
			equipEffectPane.setVisible(false);
			hitEffectPane.setVisible(false);
			killEffectPane.setVisible(false);
		} else if (item.category.action_type == ItemCategory.ActionType.use) {
			equipEffectPane.setVisible(false);
			hitEffectPane.setVisible(false);
			killEffectPane.setVisible(true);
			killEffectPane.setTitle(useLabel);
			killEffectPane.revalidate();
			killEffectPane.repaint();
		} else if (item.category.action_type == ItemCategory.ActionType.equip) {
			equipEffectPane.setVisible(true);
			hitEffectPane.setVisible(true);
			killEffectPane.setVisible(true);
			killEffectPane.setTitle(killLabel);
			killEffectPane.revalidate();
			killEffectPane.repaint();
		}
		
	}
	
	public void updateHitSourceTimedConditionEditorPane(JPanel pane, Item.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (hitSourceConditionBox != null) {
			removeElementListener(hitSourceConditionBox);
		}
		if (condition == null) {
			pane.revalidate();
			pane.repaint();
			return;
		}
		
		boolean writable = ((Item)target).writable;
		Project proj = ((Item)target).getProject();
		
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
		hitSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, false, writable, listener);
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
	
	public void updateHitSourceTimedConditionWidgets(Item.TimedConditionEffect condition) {

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

	public void updateHitTargetTimedConditionEditorPane(JPanel pane, Item.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (hitTargetConditionBox != null) {
			removeElementListener(hitTargetConditionBox);
		}
		if (condition == null) {
			pane.revalidate();
			pane.repaint();
			return;
		}
		
		boolean writable = ((Item)target).writable;
		Project proj = ((Item)target).getProject();

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
		hitTargetConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, false, writable, listener);
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

	public void updateHitTargetTimedConditionWidgets(Item.TimedConditionEffect condition) {

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
	
	public void updateKillSourceTimedConditionEditorPane(JPanel pane, Item.TimedConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (killSourceConditionBox != null) {
			removeElementListener(killSourceConditionBox);
		}
		if (condition == null) {
			pane.revalidate();
			pane.repaint();
			return;
		}
		
		boolean writable = ((Item)target).writable;
		Project proj = ((Item)target).getProject();

		killSourceConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		killSourceConditionChance = addDoubleField(pane, "Chance: ", condition.chance, writable, listener);

		killSourceConditionClear = new JRadioButton("Clear active condition");
		pane.add(killSourceConditionClear, JideBoxLayout.FIX);
		killSourceConditionApply = new JRadioButton("Apply condition with magnitude");
		pane.add(killSourceConditionApply, JideBoxLayout.FIX);
		killSourceConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude == null ? null : condition.magnitude >= 0 ? condition.magnitude : 0, 1, false, writable, listener);
		killSourceConditionImmunity = new JRadioButton("Give immunity to condition");
		pane.add(killSourceConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(killSourceConditionApply);
		radioEffectGroup.add(killSourceConditionClear);
		radioEffectGroup.add(killSourceConditionImmunity);
		
		killSourceConditionTimed = new JRadioButton("For a number of rounds");
		pane.add(killSourceConditionTimed, JideBoxLayout.FIX);
		killSourceConditionDuration = addIntegerField(pane, "Duration: ", condition.duration, false, writable, listener);
		killSourceConditionForever = new JRadioButton("Forever");
		pane.add(killSourceConditionForever, JideBoxLayout.FIX);
		
		ButtonGroup radioDurationGroup = new ButtonGroup();
		radioDurationGroup.add(killSourceConditionTimed);
		radioDurationGroup.add(killSourceConditionForever);
		
		updateKillSourceTimedConditionWidgets(condition);
		
		killSourceConditionClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(killSourceConditionClear, new Boolean(killSourceConditionClear.isSelected()));
			}
		});
		killSourceConditionApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(killSourceConditionApply, new Boolean(killSourceConditionApply.isSelected()));
			}
		});
		killSourceConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(killSourceConditionImmunity, new Boolean(killSourceConditionImmunity.isSelected()));
			}
		});
		
		killSourceConditionTimed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(killSourceConditionTimed, new Boolean(killSourceConditionTimed.isSelected()));
			}
		});
		killSourceConditionForever.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(killSourceConditionForever, new Boolean(killSourceConditionForever.isSelected()));
			}
		});
		
		pane.revalidate();
		pane.repaint();
	}

	public void updateKillSourceTimedConditionWidgets(Item.TimedConditionEffect condition) {

		boolean immunity = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration != null && condition.duration > ActorCondition.DURATION_NONE);
		boolean clear = (condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (condition.duration == null || condition.duration == ActorCondition.DURATION_NONE);
		boolean forever = condition.duration != null && condition.duration == ActorCondition.DURATION_FOREVER;
		
		killSourceConditionClear.setSelected(clear);
		killSourceConditionApply.setSelected(!clear && !immunity);
		killSourceConditionMagnitude.setEnabled(!clear && !immunity);
		killSourceConditionImmunity.setSelected(immunity);
		
		killSourceConditionTimed.setSelected(!forever);
		killSourceConditionTimed.setEnabled(!clear);
		killSourceConditionDuration.setEnabled(!clear && !forever);
		killSourceConditionForever.setSelected(forever);
		killSourceConditionForever.setEnabled(!clear);
	}
	
	public void updateEquipConditionEditorPane(JPanel pane, Item.ConditionEffect condition, final FieldUpdateListener listener) {
		pane.removeAll();
		if (equipConditionBox != null) {
			removeElementListener(equipConditionBox);
		}
		if (condition == null) {
			pane.revalidate();
			pane.repaint();
			return;
		}
		
		boolean writable = ((Item)target).writable;
		Project proj = ((Item)target).getProject();
		
		equipConditionBox = addActorConditionBox(pane, proj, "Actor Condition: ", condition.condition, writable, listener);
		equipConditionWithMagnitude = new JRadioButton("Apply condition with magnitude.");
		pane.add(equipConditionWithMagnitude, JideBoxLayout.FIX);
		equipConditionMagnitude = addIntegerField(pane, "Magnitude: ", condition.magnitude, 1, false, writable, listener);
		equipConditionImmunity = new JRadioButton("Give immunity to condition.");
		pane.add(equipConditionImmunity, JideBoxLayout.FIX);
		
		ButtonGroup radioEffectGroup = new ButtonGroup();
		radioEffectGroup.add(equipConditionWithMagnitude);
		radioEffectGroup.add(equipConditionImmunity);
		
		boolean immunity = condition.magnitude == null || condition.magnitude == ActorCondition.MAGNITUDE_CLEAR;
		equipConditionImmunity.setSelected(immunity);
		equipConditionWithMagnitude.setSelected(!immunity);
		equipConditionMagnitude.setEnabled(!immunity);
		
		equipConditionWithMagnitude.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(equipConditionWithMagnitude, new Boolean(equipConditionWithMagnitude.isSelected()));
			}
		});
		equipConditionImmunity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(equipConditionImmunity, new Boolean(equipConditionImmunity.isSelected()));
			}
		});
		

		pane.revalidate();
		pane.repaint();
	}
	
	public static class SourceTimedConditionsListModel implements ListModel<Item.TimedConditionEffect> {
		
		Item.KillEffect source;
		
		public SourceTimedConditionsListModel(Item.KillEffect effect) {
			this.source = effect;;
		}

		@Override
		public int getSize() {
			if (source.conditions_source == null) return 0;
			return source.conditions_source.size();
		}
		
		@Override
		public Item.TimedConditionEffect getElementAt(int index) {
			if (source.conditions_source == null) return null;
			return source.conditions_source.get(index);
		}
		
		public void addItem(Item.TimedConditionEffect item) {
			if (source.conditions_source == null) {
				source.conditions_source = new ArrayList<Item.TimedConditionEffect>();
			}
			source.conditions_source.add(item);
			int index = source.conditions_source.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Item.TimedConditionEffect item) {
			int index = source.conditions_source.indexOf(item);
			source.conditions_source.remove(item);
			if (source.conditions_source.isEmpty()) {
				source.conditions_source = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Item.TimedConditionEffect item) {
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
	
	public static class TargetTimedConditionsListModel implements ListModel<Item.TimedConditionEffect> {
		
		Item.HitEffect source;
		
		public TargetTimedConditionsListModel(Item.HitEffect effect) {
			this.source = effect;;
		}

		@Override
		public int getSize() {
			if (source.conditions_target == null) return 0;
			return source.conditions_target.size();
		}
		
		@Override
		public Item.TimedConditionEffect getElementAt(int index) {
			if (source.conditions_target == null) return null;
			return source.conditions_target.get(index);
		}
		
		public void addItem(Item.TimedConditionEffect item) {
			if (source.conditions_target == null) {
				source.conditions_target = new ArrayList<Item.TimedConditionEffect>();
			}
			source.conditions_target.add(item);
			int index = source.conditions_target.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Item.TimedConditionEffect item) {
			int index = source.conditions_target.indexOf(item);
			source.conditions_target.remove(item);
			if (source.conditions_target.isEmpty()) {
				source.conditions_target = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Item.TimedConditionEffect item) {
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
	
	public static class TimedConditionsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				Item.TimedConditionEffect effect = (Item.TimedConditionEffect) value;
				
				if (effect.condition != null) {
					label.setIcon(new ImageIcon(effect.condition.getIcon()));
					
					boolean immunity = (effect.magnitude == null || effect.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (effect.duration != null && effect.duration > ActorCondition.DURATION_NONE);
					boolean clear = (effect.magnitude == null || effect.magnitude == ActorCondition.MAGNITUDE_CLEAR) && (effect.duration == null || effect.duration == ActorCondition.DURATION_NONE);
					boolean forever = effect.duration != null && effect.duration == ActorCondition.DURATION_FOREVER;
					
					if (clear) {
						label.setText(effect.chance+"% chances to clear actor condition "+effect.condition.getDesc());
					} else if (immunity) {
						label.setText(effect.chance+"% chances to give immunity to "+effect.condition.getDesc()+(forever ? " forever" : " for "+effect.duration+" rounds"));
					} else {
						label.setText(effect.chance+"% chances to give actor condition "+effect.condition.getDesc()+" x"+effect.magnitude+(forever ? " forever" : " for "+effect.duration+" rounds"));
					}
				} else {
					label.setText("New, undefined actor condition effect.");
				}
			}
			return c;
		}
	}
	
	public static class ConditionsListModel implements ListModel<Item.ConditionEffect> {
		
		Item.EquipEffect source;
		
		public ConditionsListModel(Item.EquipEffect equipEffect) {
			this.source = equipEffect;
		}

		@Override
		public int getSize() {
			if (source.conditions == null) return 0;
			return source.conditions.size();
		}
		
		@Override
		public Item.ConditionEffect getElementAt(int index) {
			if (source.conditions == null) return null;
			return source.conditions.get(index);
		}
		
		public void addItem(Item.ConditionEffect item) {
			if (source.conditions == null) {
				source.conditions = new ArrayList<Item.ConditionEffect>();
			}
			source.conditions.add(item);
			int index = source.conditions.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Item.ConditionEffect item) {
			int index = source.conditions.indexOf(item);
			source.conditions.remove(item);
			if (source.conditions.isEmpty()) {
				source.conditions = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Item.ConditionEffect item) {
			int index = source.conditions.indexOf(item);
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
	
	public static class ConditionsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				Item.ConditionEffect effect = (Item.ConditionEffect) value;
				
				if (effect.condition != null) {
					label.setIcon(new ImageIcon(effect.condition.getIcon()));
					if (effect.magnitude == ActorCondition.MAGNITUDE_CLEAR) {
						label.setText("Immune to actor condition "+effect.condition.getDesc());
					} else {
						label.setText("Give actor condition "+effect.condition.getDesc()+" x"+effect.magnitude);
					}
				} else {
					label.setText("New, undefined actor condition effect.");
				}
			}
			return c;
		}
	}
	
	public static boolean isNull(Item.EquipEffect effect) {
		if (effect.conditions != null) return false;
		if (effect.critical_multiplier != null) return false;
		if (effect.damage_boost_max != null) return false;
		if (effect.damage_boost_min != null) return false;
		if (effect.increase_attack_chance != null) return false;
		if (effect.increase_attack_cost != null) return false;
		if (effect.increase_block_chance != null) return false;
		if (effect.increase_critical_skill != null) return false;
		if (effect.increase_damage_resistance != null) return false;
		if (effect.increase_move_cost != null) return false;
		if (effect.increase_reequip_cost != null) return false;
		if (effect.increase_use_item_cost != null) return false;
		if (effect.max_ap_boost != null) return false;
		if (effect.max_hp_boost != null) return false;
		return true;
	}
	
	
	public static boolean isNull(Item.HitEffect effect) {
		if (effect.ap_boost_min != null) return false;
		if (effect.ap_boost_max != null) return false;
		if (effect.hp_boost_min != null) return false;
		if (effect.hp_boost_max != null) return false;
		if (effect.conditions_source != null) return false;
		if (effect.conditions_target != null) return false;
		return true;
	}
	

	public static boolean isNull(Item.KillEffect effect) {
		if (effect.ap_boost_min != null) return false;
		if (effect.ap_boost_max != null) return false;
		if (effect.hp_boost_min != null) return false;
		if (effect.hp_boost_max != null) return false;
		if (effect.conditions_source != null) return false;
		return true;
	}
	
	public class ItemFieldUpdater implements FieldUpdateListener {

		@Override
		public void valueChanged(JComponent source, Object value) {
			Item item = (Item)target;
			boolean updatePrice, updateEquip, updateHit, updateKill;
			updatePrice = updateEquip = updateHit = updateKill = false;
			if (source == idField) {
				//Events caused by cancel an ID edition. Dismiss.
				if (skipNext) {
					skipNext = false;
					return;
				}
				if (target.id.equals((String) value)) return;
				
				if (idChanging()) {
					item.id = (String) value;
					ItemEditor.this.name = item.getDesc();
					item.childrenChanged(new ArrayList<ProjectTreeNode>());
					ATContentStudio.frame.editorChanged(ItemEditor.this);
				} else {
					cancelIdEdit(idField);
					return;
				}
			} else if (source == nameField) {
				item.name = (String) value;
				ItemEditor.this.name = item.getDesc();
				item.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(ItemEditor.this);
			} else if (source == itemIcon) {
				item.icon_id = (String) value;
				item.childrenChanged(new ArrayList<ProjectTreeNode>());
				ItemEditor.this.icon = new ImageIcon(item.getProject().getIcon((String) value));
				ATContentStudio.frame.editorChanged(ItemEditor.this);
				itemIcon.setIcon(new ImageIcon(item.getProject().getImage((String) value)));
				itemIcon.revalidate();
				itemIcon.repaint();
			} else if (source == descriptionField) {
				item.description = descriptionField.getText();
			} else if (source == typeBox) {
				item.display_type = (Item.DisplayType) value;
			} else if (source == manualPriceBox) {
				item.has_manual_price = (Integer) value;
				if (!manualPriceBox.isSelected()) {
					baseCostField.setEnabled(false);
					updatePrice = true;
				} else {
					baseCostField.setEnabled(true);
					if (baseManualPrice != null) {
						baseCostField.setValue(baseManualPrice);
					}
				}
			} else if (source == baseCostField) {
				if (manualPriceBox.isSelected()) {
					item.base_market_cost = (Integer) value;
					baseManualPrice = item.base_market_cost;
				}
			} else if (source == categoryBox) {
				if (item.category != null) {
					item.category.removeBacklink(item);
				}
				item.category = (ItemCategory) value;
				if (item.category != null) {
					item.category_id = item.category.id;
					item.category.addBacklink(item);
				} else {
					item.category_id = null;
				}
				if (item.category == null || item.category.action_type == null || item.category.action_type == ItemCategory.ActionType.none) {
					equipEffectPane.setVisible(false);
					item.equip_effect = null;
					hitEffectPane.setVisible(false);
					item.hit_effect = null;
					killEffectPane.setVisible(false);
					item.kill_effect = null;
					ItemEditor.this.revalidate();
					ItemEditor.this.repaint();
				} else if (item.category.action_type == ItemCategory.ActionType.use) {
					equipEffectPane.setVisible(false);
					item.equip_effect = null;
					hitEffectPane.setVisible(false);
					item.hit_effect = null;
					killEffectPane.setVisible(true);
					updateKill = true;
					killEffectPane.setTitle(useLabel);
					ItemEditor.this.revalidate();
					ItemEditor.this.repaint();
				} else if (item.category.action_type == ItemCategory.ActionType.equip) {
					equipEffectPane.setVisible(true);
					updateEquip = true;
					hitEffectPane.setVisible(true);
					updateEquip = true;
					killEffectPane.setVisible(true);
					updateKill = true;
					killEffectPane.setTitle(killLabel);
					ItemEditor.this.revalidate();
					ItemEditor.this.repaint();
				}
				updatePrice = true;
			} else if (source == equipDmgMin) {
				equipEffect.damage_boost_min = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipDmgMax) {
				equipEffect.damage_boost_max = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipBoostHP) {
				equipEffect.max_hp_boost = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipBoostAP) {
				equipEffect.max_ap_boost = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipBoostAC) {
				equipEffect.increase_attack_chance = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipBoostBC) {
				equipEffect.increase_block_chance = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipBoostCS) {
				equipEffect.increase_critical_skill = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipSetCM) {
				equipEffect.critical_multiplier = (Double) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipBoostDR) {
				equipEffect.increase_damage_resistance = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipIncMoveCost) {
				equipEffect.increase_move_cost = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipIncUseCost) {
				equipEffect.increase_use_item_cost = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipIncReequipCost) {
				equipEffect.increase_reequip_cost = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipIncAttackCost) {
				equipEffect.increase_attack_cost = (Integer) value;
				updatePrice = true;
				updateEquip = true;
			} else if (source == equipConditionsList) {
				updateEquip = true;
			} else if (source == equipConditionBox) {
				if (selectedEquipEffectCondition.condition != null) {
					selectedEquipEffectCondition.condition.removeBacklink(item);
				}
				selectedEquipEffectCondition.condition = (ActorCondition) value;
				if (selectedEquipEffectCondition.condition != null) {
					selectedEquipEffectCondition.condition_id = selectedEquipEffectCondition.condition.id;
					selectedEquipEffectCondition.condition.addBacklink(item);
				} else {
					selectedEquipEffectCondition.condition_id = null;
				}
				equipConditionsModel.itemChanged(selectedEquipEffectCondition);
			} else if (source == equipConditionMagnitude) {
				selectedEquipEffectCondition.magnitude = (Integer) value;
				equipConditionsModel.itemChanged(selectedEquipEffectCondition);
			} else if (source == equipConditionImmunity && (Boolean) value) {
				selectedEquipEffectCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				equipConditionMagnitude.setEnabled(false);
			} else if (source == equipConditionWithMagnitude && (Boolean) value) {
				selectedEquipEffectCondition.magnitude = (Integer) equipConditionMagnitude.getValue();
				equipConditionMagnitude.setEnabled(true);
			} else if (source == hitHPMin) {
				hitEffect.hp_boost_min = (Integer) value;
				updatePrice = true;
				updateHit = true;
			} else if (source == hitHPMax) {
				hitEffect.hp_boost_max = (Integer) value;
				updatePrice = true;
				updateHit = true;
			} else if (source == hitAPMin) {
				hitEffect.ap_boost_min = (Integer) value;
				updatePrice = true;
				updateHit = true;
			} else if (source == hitAPMax) {
				hitEffect.ap_boost_max = (Integer) value;
				updatePrice = true;
				updateHit = true;
			} else if (source == hitSourceConditionsList) {
				updateHit = true;
			} else if (source == hitSourceConditionBox) {
				if (selectedHitEffectSourceCondition.condition != null) {
					selectedHitEffectSourceCondition.condition.removeBacklink(item);
				}
				selectedHitEffectSourceCondition.condition = (ActorCondition) value;
				if (selectedHitEffectSourceCondition.condition != null) {
					selectedHitEffectSourceCondition.condition_id = selectedHitEffectSourceCondition.condition.id;
					selectedHitEffectSourceCondition.condition.addBacklink(item);
				} else {
					selectedHitEffectSourceCondition.condition_id = null;
				}
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionClear && (Boolean) value) {
				selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectSourceCondition.duration = null;
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			}  else if (source == hitSourceConditionApply && (Boolean) value) {
				selectedHitEffectSourceCondition.magnitude = (Integer) hitSourceConditionMagnitude.getValue();
				selectedHitEffectSourceCondition.duration = hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitSourceConditionDuration.getValue();
				if (selectedHitEffectSourceCondition.duration == null) {
					selectedHitEffectSourceCondition.duration = 1;
				}
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionImmunity && (Boolean) value) {
				selectedHitEffectSourceCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectSourceCondition.duration = hitSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitSourceConditionDuration.getValue();
				if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectSourceCondition.duration = 1;
				}
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionMagnitude) {
				selectedHitEffectSourceCondition.magnitude = (Integer) value;
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionTimed && (Boolean) value) {
				selectedHitEffectSourceCondition.duration = (Integer) hitSourceConditionDuration.getValue();
				if (selectedHitEffectSourceCondition.duration == null || selectedHitEffectSourceCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectSourceCondition.duration = 1;
				}
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionForever && (Boolean) value) {
				selectedHitEffectSourceCondition.duration = ActorCondition.DURATION_FOREVER;
				updateHitSourceTimedConditionWidgets(selectedHitEffectSourceCondition);
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionDuration) {
				selectedHitEffectSourceCondition.duration = (Integer) value;
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitSourceConditionChance) {
				selectedHitEffectSourceCondition.chance = (Double) value;
				hitSourceConditionsModel.itemChanged(selectedHitEffectSourceCondition);
				updateHit = true;
			} else if (source == hitTargetConditionsList) {
				updateHit = true;
			} else if (source == hitTargetConditionBox) {
				if (selectedHitEffectTargetCondition.condition != null) {
					selectedHitEffectTargetCondition.condition.removeBacklink(item);
				}
				selectedHitEffectTargetCondition.condition = (ActorCondition) value;
				if (selectedHitEffectTargetCondition.condition != null) {
					selectedHitEffectTargetCondition.condition_id = selectedHitEffectTargetCondition.condition.id;
					selectedHitEffectTargetCondition.condition.addBacklink(item);
				} else {
					selectedHitEffectTargetCondition.condition_id = null;
				}
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionClear && (Boolean) value) {
				selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectTargetCondition.duration = null;
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			}  else if (source == hitTargetConditionApply && (Boolean) value) {
				selectedHitEffectTargetCondition.magnitude = (Integer) hitTargetConditionMagnitude.getValue();
				selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
				if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectTargetCondition.duration = 1;
				}
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionImmunity && (Boolean) value) {
				selectedHitEffectTargetCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedHitEffectTargetCondition.duration = hitTargetConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) hitTargetConditionDuration.getValue();
				if (selectedHitEffectTargetCondition.duration == null || selectedHitEffectTargetCondition.duration == ActorCondition.DURATION_NONE) {
					selectedHitEffectTargetCondition.duration = 1;
				}
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionMagnitude) {
				selectedHitEffectTargetCondition.magnitude = (Integer) value;
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionTimed && (Boolean) value) {
				selectedHitEffectTargetCondition.duration = (Integer) hitTargetConditionDuration.getValue();
				if (selectedHitEffectTargetCondition.duration == null) {
					selectedHitEffectTargetCondition.duration = 1;
				}
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionForever && (Boolean) value) {
				selectedHitEffectTargetCondition.duration = ActorCondition.DURATION_FOREVER;
				updateHitTargetTimedConditionWidgets(selectedHitEffectTargetCondition);
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionDuration) {
				selectedHitEffectTargetCondition.duration = (Integer) value;
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == hitTargetConditionChance) {
				selectedHitEffectTargetCondition.chance = (Double) value;
				hitTargetConditionsModel.itemChanged(selectedHitEffectTargetCondition);
				updateHit = true;
			} else if (source == killHPMin) {
				killEffect.hp_boost_min = (Integer) value;
				updatePrice = true;
				updateKill = true;
			} else if (source == killHPMax) {
				killEffect.hp_boost_max = (Integer) value;
				updatePrice = true;
				updateKill = true;
			} else if (source == killAPMin) {
				killEffect.ap_boost_min = (Integer) value;
				updatePrice = true;
				updateKill = true;
			} else if (source == killAPMax) {
				killEffect.ap_boost_max = (Integer) value;
				updatePrice = true;
				updateKill = true;
			} else if (source == killSourceConditionsList) {
				updateKill = true;
			} else if (source == killSourceConditionBox) {
				if (selectedKillEffectCondition.condition != null) {
					selectedKillEffectCondition.condition.removeBacklink(item);
				}
				selectedKillEffectCondition.condition = (ActorCondition) value;
				if (selectedKillEffectCondition.condition != null) {
					selectedKillEffectCondition.condition_id = selectedKillEffectCondition.condition.id;
					selectedKillEffectCondition.condition.addBacklink(item);
				} else {
					selectedKillEffectCondition.condition_id = null;
				}
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			}  else if (source == killSourceConditionClear && (Boolean) value) {
				selectedKillEffectCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedKillEffectCondition.duration = null;
				updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			}  else if (source == killSourceConditionApply && (Boolean) value) {
				selectedKillEffectCondition.magnitude = (Integer) killSourceConditionMagnitude.getValue();
				selectedKillEffectCondition.duration = killSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) killSourceConditionDuration.getValue();
				if (selectedKillEffectCondition.duration == null || selectedKillEffectCondition.duration == ActorCondition.DURATION_NONE) {
					selectedKillEffectCondition.duration = 1;
				}
				updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			} else if (source == killSourceConditionImmunity && (Boolean) value) {
				selectedKillEffectCondition.magnitude = ActorCondition.MAGNITUDE_CLEAR;
				selectedKillEffectCondition.duration = killSourceConditionForever.isSelected() ? ActorCondition.DURATION_FOREVER : (Integer) killSourceConditionDuration.getValue();
				if (selectedKillEffectCondition.duration == null || selectedKillEffectCondition.duration == ActorCondition.DURATION_NONE) {
					selectedKillEffectCondition.duration = 1;
				}
				updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			} else if (source == killSourceConditionMagnitude) {
				selectedKillEffectCondition.magnitude = (Integer) value;
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			} else if (source == killSourceConditionTimed && (Boolean) value) {
				selectedKillEffectCondition.duration = (Integer) killSourceConditionDuration.getValue();
				if (selectedKillEffectCondition.duration == null) {
					selectedKillEffectCondition.duration = 1;
				}
				updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			} else if (source == killSourceConditionForever && (Boolean) value) {
				selectedKillEffectCondition.duration = ActorCondition.DURATION_FOREVER;
				updateKillSourceTimedConditionWidgets(selectedKillEffectCondition);
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			} else if (source == killSourceConditionDuration) {
				selectedKillEffectCondition.duration = (Integer) value;
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			} else if (source == killSourceConditionChance) {
				selectedKillEffectCondition.chance = (Double) value;
				killSourceConditionsModel.itemChanged(selectedKillEffectCondition);
				updateKill = true;
			}
			
			if (updateEquip) {
				if (isNull(equipEffect)) {
					item.equip_effect = null;
				} else {
					item.equip_effect = equipEffect;
				}
			}
			if (updateHit) {
				if (isNull(hitEffect)) {
					item.hit_effect = null;
				} else {
					item.hit_effect = hitEffect;
				}
			}
			if (updateKill) {
				if (isNull(killEffect)) {
					item.kill_effect = null;
				} else {
					item.kill_effect = killEffect;
				}
			}
			if (updatePrice && !manualPriceBox.isSelected()) {
				baseCostField.setValue(item.computePrice());
			}

			
			if (item.state != GameDataElement.State.modified) {
				item.state = GameDataElement.State.modified;
				ItemEditor.this.name = item.getDesc();
				item.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(ItemEditor.this);
			}
			updateJsonViewText(item.toJsonString());
			
		}
		
	}

}
