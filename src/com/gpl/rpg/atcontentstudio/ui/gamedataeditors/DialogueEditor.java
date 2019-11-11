package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTextArea;
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
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.model.gamedata.NPC;
import com.gpl.rpg.atcontentstudio.model.gamedata.Quest;
import com.gpl.rpg.atcontentstudio.model.gamedata.QuestStage;
import com.gpl.rpg.atcontentstudio.model.gamedata.Requirement;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMap;
import com.gpl.rpg.atcontentstudio.ui.BooleanBasedCheckBox;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.OverlayIcon;
import com.gpl.rpg.atcontentstudio.ui.gamedataeditors.dialoguetree.DialogueGraphView;
import com.jidesoft.swing.JideBoxLayout;

public class DialogueEditor extends JSONElementEditor {

	private static final long serialVersionUID = 4140553240585599873L;

	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	private static final String graph_view_id = "Dialogue Tree";
	
	private Dialogue.Reward selectedReward;
	private Dialogue.Reply selectedReply;
	private Requirement selectedRequirement;
	
	
	private static final String[] replyTypes = new String[]{
			"Phrase leads to another without replies.", 
			"NPC replies too.", 
			"Reply ends dialogue.", 
			"Engage fight with NPC.",
			"Remove NPC from map.", 
			"Start trading with NPC."
			};
	private static final int GO_NEXT_INDEX = 0;
	private static final int STD_REPLY_INDEX = 1;
	private static final int END_INDEX = 2;
	private static final int FIGHT_INDEX = 3;
	private static final int REMOVE_INDEX = 4;
	private static final int SHOP_INDEX = 5;
	
	private JTextField idField;
	private JTextArea messageField;
	private MyComboBox switchToNpcBox;
	
	private RewardsListModel rewardsListModel;
	@SuppressWarnings("rawtypes")
	private JList rewardsList;
	@SuppressWarnings("rawtypes")
	private JComboBox rewardTypeCombo;
	private JPanel rewardsParamsPane;
	private MyComboBox rewardMap;
	private JTextField rewardObjId;
	@SuppressWarnings("rawtypes")
	private JComboBox rewardObjIdCombo;
	private MyComboBox rewardObj;
	private JComponent rewardValue;
	private JRadioButton rewardConditionTimed;
	private JRadioButton rewardConditionForever;
	private JRadioButton rewardConditionClear;
	
	private RepliesListModel repliesListModel;
	@SuppressWarnings("rawtypes")
	private JList repliesList;
	private JPanel repliesParamsPane;
	@SuppressWarnings("rawtypes")
	private JComboBox replyTypeCombo;
	private MyComboBox replyNextPhrase;
	private String replyTextCache = null;
	private JTextField replyText;
	
	private ReplyRequirementsListModel requirementsListModel;
	@SuppressWarnings("rawtypes")
	private JList requirementsList;
	@SuppressWarnings("rawtypes")
	private JComboBox requirementTypeCombo;
	private JPanel requirementParamsPane;
	private MyComboBox requirementObj;
	@SuppressWarnings("rawtypes")
	private JComboBox requirementSkill;
	private JComponent requirementObjId;
	private JComponent requirementValue;
	private BooleanBasedCheckBox requirementNegated;
	
	private DialogueGraphView dialogueGraphView;
	
	
	public DialogueEditor(Dialogue dialogue) {
		super(dialogue, dialogue.getDesc(), dialogue.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
		addEditorTab(graph_view_id, createDialogueGraphView(dialogue));
	}
	
	public JPanel createDialogueGraphView(final Dialogue dialogue) {
		final JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		
		dialogueGraphView = new DialogueGraphView(dialogue, null);
		pane.add(dialogueGraphView, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new JideBoxLayout(buttonPane, JideBoxLayout.LINE_AXIS));
		JButton reloadButton = new JButton("Refresh graph");
		buttonPane.add(reloadButton, JideBoxLayout.FIX);
		buttonPane.add(new JPanel(), JideBoxLayout.VARY);
		pane.add(buttonPane, BorderLayout.NORTH);
		
		
		reloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pane.remove(dialogueGraphView);
				dialogueGraphView = new DialogueGraphView(dialogue, null);
				pane.add(dialogueGraphView, BorderLayout.CENTER);
				pane.revalidate();
				pane.repaint();
			}
		});
		
		return pane;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void insertFormViewDataField(final JPanel pane) {
		
		final Dialogue dialogue = (Dialogue) target;
		final FieldUpdateListener listener = new DialogueFieldUpdater();
		
		createButtonPane(pane, dialogue.getProject(), dialogue, Dialogue.class, dialogue.getImage(), null, listener);
		
		idField = addTextField(pane, "Internal ID: ", dialogue.id, dialogue.writable, listener);
		messageField = addTranslatableTextArea(pane, "Message: ", dialogue.message, dialogue.writable, listener);
		switchToNpcBox = addNPCBox(pane, dialogue.getProject(), "Switch active NPC to: ", dialogue.switch_to_npc, dialogue.writable, listener);
		
		CollapsiblePanel rewards = new CollapsiblePanel("Reaching this phrase gives the following rewards: ");
		rewards.setLayout(new JideBoxLayout(rewards, JideBoxLayout.PAGE_AXIS));
		rewardsListModel = new RewardsListModel(dialogue);
		rewardsList = new JList(rewardsListModel);
		rewardsList.setCellRenderer(new RewardsCellRenderer());
		rewardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rewards.add(new JScrollPane(rewardsList), JideBoxLayout.FIX);
		final JPanel rewardsEditorPane = new JPanel();
		final JButton createReward = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteReward = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		deleteReward.setEnabled(false);
		rewardsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedReward = (Dialogue.Reward) rewardsList.getSelectedValue();
				if (selectedReward == null) {
					deleteReward.setEnabled(false);
				} else {
					deleteReward.setEnabled(true);
				}
				updateRewardsEditorPane(rewardsEditorPane, selectedReward, listener);
			}
		});
		if (dialogue.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createReward.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Dialogue.Reward reward = new Dialogue.Reward();
					rewardsListModel.addItem(reward);
					rewardsList.setSelectedValue(reward, true);
					listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteReward.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedReward != null) {
						rewardsListModel.removeItem(selectedReward);
						selectedReward = null;
						rewardsList.clearSelection();
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createReward, JideBoxLayout.FIX);
			listButtonsPane.add(deleteReward, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			rewards.add(listButtonsPane, JideBoxLayout.FIX);
		}
		if (dialogue.rewards == null || dialogue.rewards.isEmpty()) {
			rewards.collapse();
		}
		rewardsEditorPane.setLayout(new JideBoxLayout(rewardsEditorPane, JideBoxLayout.PAGE_AXIS));
		rewards.add(rewardsEditorPane, JideBoxLayout.FIX);

		pane.add(rewards, JideBoxLayout.FIX);
		
		CollapsiblePanel replies = new CollapsiblePanel("Replies / Next Phrase: ");
		replies.setLayout(new JideBoxLayout(replies, JideBoxLayout.PAGE_AXIS));
		repliesListModel = new RepliesListModel(dialogue);
		repliesList = new JList(repliesListModel);
		repliesList.setCellRenderer(new RepliesCellRenderer());
		repliesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		replies.add(new JScrollPane(repliesList), JideBoxLayout.FIX);
		final JPanel repliesEditorPane = new JPanel();
		final JButton createReply = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteReply = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		final JButton moveReplyUp = new JButton(new ImageIcon(DefaultIcons.getArrowUpIcon()));
		final JButton moveReplyDown = new JButton(new ImageIcon(DefaultIcons.getArrowDownIcon()));
		deleteReply.setEnabled(false);
		moveReplyUp.setEnabled(false);
		moveReplyDown.setEnabled(false);
		repliesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedReply = (Dialogue.Reply) repliesList.getSelectedValue();
				if (selectedReply != null && !Dialogue.Reply.GO_NEXT_TEXT.equals(selectedReply.text)) {
					replyTextCache = selectedReply.text;
				} else {
					replyTextCache = null;
				}
				if (selectedReply != null) {
					deleteReply.setEnabled(true);
					moveReplyUp.setEnabled(repliesList.getSelectedIndex() > 0);
					moveReplyDown.setEnabled(repliesList.getSelectedIndex() < (repliesListModel.getSize() - 1));
				} else {
					deleteReply.setEnabled(false);
					moveReplyUp.setEnabled(false);
					moveReplyDown.setEnabled(false);
				}
				updateRepliesEditorPane(repliesEditorPane, selectedReply, listener);
			}
		});
		if (dialogue.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createReply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Dialogue.Reply reply = new Dialogue.Reply();
					repliesListModel.addItem(reply);
					repliesList.setSelectedValue(reply, true);
					listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteReply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedReply != null) {
						repliesListModel.removeItem(selectedReply);
						selectedReply = null;
						repliesList.clearSelection();
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			moveReplyUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedReply != null) {
						repliesListModel.moveUp(selectedReply);
						repliesList.setSelectedValue(selectedReply, true);
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			moveReplyDown.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedReply != null) {
						repliesListModel.moveDown(selectedReply);
						repliesList.setSelectedValue(selectedReply, true);
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			listButtonsPane.add(createReply, JideBoxLayout.FIX);
			listButtonsPane.add(deleteReply, JideBoxLayout.FIX);
			listButtonsPane.add(moveReplyUp, JideBoxLayout.FIX);
			listButtonsPane.add(moveReplyDown, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			replies.add(listButtonsPane, JideBoxLayout.FIX);
		}
		if (dialogue.replies == null || dialogue.replies.isEmpty()) {
			replies.collapse();
		}
		repliesEditorPane.setLayout(new JideBoxLayout(repliesEditorPane, JideBoxLayout.PAGE_AXIS));
		replies.add(repliesEditorPane, JideBoxLayout.FIX);

		pane.add(replies, JideBoxLayout.FIX);
		
		
	}
	
	public void updateRewardsEditorPane(final JPanel pane, final Dialogue.Reward reward, final FieldUpdateListener listener) {
		pane.removeAll();
		if (rewardMap != null) {
			removeElementListener(rewardMap);
		}
		if (rewardObj != null) {
			removeElementListener(rewardObj);
		}
		
		if (reward != null) {
			rewardTypeCombo = addEnumValueBox(pane, "Reward type: ", Dialogue.Reward.RewardType.values(), reward.type, ((Dialogue)target).writable, listener);
			rewardsParamsPane = new JPanel();
			rewardsParamsPane.setLayout(new JideBoxLayout(rewardsParamsPane, JideBoxLayout.PAGE_AXIS));
			updateRewardsParamsEditorPane(rewardsParamsPane, reward, listener);
			pane.add(rewardsParamsPane, JideBoxLayout.FIX);
		}
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateRewardsParamsEditorPane(final JPanel pane, final Dialogue.Reward reward, final FieldUpdateListener listener) {
		boolean writable = ((Dialogue)target).writable;
		pane.removeAll();
		if (rewardMap != null) {
			removeElementListener(rewardMap);
		}
		if (rewardObj != null) {
			removeElementListener(rewardObj);
		}
		boolean immunity = false;
		if (reward.type != null) {
			switch (reward.type) {
			case activateMapObjectGroup:
			case deactivateMapObjectGroup:
				rewardMap = addMapBox(pane, ((Dialogue)target).getProject(), "Map Name: ", reward.map, writable, listener);
				rewardObjId = addTextField(pane, "Group ID: ", reward.reward_obj_id, writable, listener);
				rewardObjIdCombo = null;
				rewardObj = null;
				rewardValue = null;
				break;
			case changeMapFilter:
				rewardMap = addMapBox(pane, ((Dialogue)target).getProject(), "Map Name: ", reward.map, writable, listener);
				rewardObjId = null;
				rewardObjIdCombo = addEnumValueBox(pane, "Color Filter", TMXMap.ColorFilter.values(), reward.reward_obj_id != null ? TMXMap.ColorFilter.valueOf(reward.reward_obj_id) : TMXMap.ColorFilter.none, writable, listener);
				rewardObj = null;
				rewardValue = null;
				break;
			case deactivateSpawnArea:
			case removeSpawnArea:
			case spawnAll:
				rewardMap = addMapBox(pane, ((Dialogue)target).getProject(), "Map Name: ", reward.map, writable, listener);
				rewardObjId = addTextField(pane, "Area ID: ", reward.reward_obj_id, writable, listener);
				rewardObjIdCombo = null;
				rewardObj = null;
				rewardValue = null;
				break;
			case actorConditionImmunity:
				immunity = true;
			case actorCondition:
				
				rewardMap = null;
				rewardObjId = null;
				rewardObjIdCombo = null;
				rewardObj = addActorConditionBox(pane, ((Dialogue)target).getProject(), "Actor Condition: ", (ActorCondition) reward.reward_obj, writable, listener);
				rewardConditionTimed = new JRadioButton("For a number of rounds");
				pane.add(rewardConditionTimed, JideBoxLayout.FIX);
				rewardValue = addIntegerField(pane, "Duration: ", reward.reward_value, 1, false, writable, listener);
				rewardConditionForever = new JRadioButton("Forever");
				pane.add(rewardConditionForever, JideBoxLayout.FIX);
				if (!immunity) {
					rewardConditionClear = new JRadioButton("Clear actor condition");
					pane.add(rewardConditionClear, JideBoxLayout.FIX);
				}
				
				ButtonGroup radioGroup = new ButtonGroup();
				radioGroup.add(rewardConditionTimed);
				radioGroup.add(rewardConditionForever);
				if (!immunity) radioGroup.add(rewardConditionClear);
				
				if (immunity) {
					rewardConditionTimed.setSelected(reward.reward_value == null || (reward.reward_value != ActorCondition.DURATION_FOREVER && reward.reward_value != ActorCondition.MAGNITUDE_CLEAR));
					rewardConditionForever.setSelected(reward.reward_value != null && reward.reward_value != ActorCondition.DURATION_FOREVER);
					rewardConditionClear.setSelected(reward.reward_value != null && reward.reward_value != ActorCondition.MAGNITUDE_CLEAR);
				} else {
					rewardConditionTimed.setSelected(reward.reward_value != null && reward.reward_value != ActorCondition.DURATION_FOREVER);
					rewardConditionForever.setSelected(reward.reward_value == null || reward.reward_value == ActorCondition.DURATION_FOREVER);
				}
				rewardValue.setEnabled(rewardConditionTimed.isSelected());
				
				rewardConditionTimed.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listener.valueChanged(rewardConditionTimed, new Boolean(rewardConditionTimed.isSelected()));
					}
				});
				rewardConditionForever.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listener.valueChanged(rewardConditionForever, new Boolean(rewardConditionForever.isSelected()));
					}
				});
				if (!immunity) {
					rewardConditionClear.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							listener.valueChanged(rewardConditionClear, new Boolean(rewardConditionClear.isSelected()));
						}
					});
				}
				break;
			case alignmentChange:
			case alignmentSet:
				rewardMap = null;
				rewardObjId = addTextField(pane, "Faction: ", reward.reward_obj_id, writable, listener);
				rewardObjIdCombo = null;
				rewardObj = null;
				rewardValue = addIntegerField(pane, "Value: ", reward.reward_value, true, writable, listener);
				break;
			case createTimer:
				rewardMap = null;
				rewardObjId = addTextField(pane, "Timer ID: ", reward.reward_obj_id, writable, listener);
				rewardObjIdCombo = null;
				rewardObj = null;
				rewardValue = null;
				break;
			case dropList:
				rewardMap = null;
				rewardObjId = null;
				rewardObjIdCombo = null;
				rewardObj = addDroplistBox(pane, ((Dialogue)target).getProject(), "Droplist: ", (Droplist) reward.reward_obj, writable, listener);
				rewardValue = null;
				break;
			case giveItem:
				rewardMap = null;
				rewardObjId = null;
				rewardObj = addItemBox(pane, ((Dialogue)target).getProject(), "Item: ", (Item) reward.reward_obj, writable, listener);
				rewardValue = addIntegerField(pane, "Quantity: ", reward.reward_value, true, writable, listener);
				break;
			case removeQuestProgress:
			case questProgress:
				rewardMap = null;
				rewardObjId = null;
				rewardObjIdCombo = null;
				rewardObj = addQuestBox(pane, ((Dialogue)target).getProject(), "Quest: ", (Quest) reward.reward_obj, writable, listener);
				rewardValue = addQuestStageBox(pane, ((Dialogue)target).getProject(), "Quest stage: ", reward.reward_value, writable, listener, (Quest) reward.reward_obj, rewardObj);
				break;
			case skillIncrease:
				Requirement.SkillID skillId = null;
				try {
					skillId = reward.reward_obj_id == null ? null : Requirement.SkillID.valueOf(reward.reward_obj_id);
				} catch(IllegalArgumentException e) {}
				rewardMap = null;
				rewardObjId = null;// addTextField(pane, "Skill ID: ", reward.reward_obj_id, writable, listener);
				rewardObjIdCombo = addEnumValueBox(pane, "Skill ID: ", Requirement.SkillID.values(), skillId, writable, listener);
				rewardObj = null;
				rewardValue = null;
				break;

			}
		}
		pane.revalidate();
		pane.repaint();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateRepliesEditorPane(final JPanel pane, final Dialogue.Reply reply, final FieldUpdateListener listener) {
		pane.removeAll();
		if (replyNextPhrase != null) {
			removeElementListener(replyNextPhrase);
		}
		if (requirementObj != null) {
			removeElementListener(requirementObj);
		}
		if (reply == null) return;
		
		JPanel comboPane = new JPanel();
		comboPane.setLayout(new BorderLayout());
		JLabel comboLabel = new JLabel("Reply type: ");
		comboPane.add(comboLabel, BorderLayout.WEST);
		
		replyTypeCombo = new JComboBox(replyTypes);
		replyTypeCombo.setEnabled(((Dialogue)target).writable);
		repliesParamsPane = new JPanel();
		repliesParamsPane.setLayout(new JideBoxLayout(repliesParamsPane, JideBoxLayout.PAGE_AXIS));
		if (Dialogue.Reply.GO_NEXT_TEXT.equals(reply.text)) {
			replyTypeCombo.setSelectedItem(replyTypes[GO_NEXT_INDEX]);
		} else if (Dialogue.Reply.EXIT_PHRASE_ID.equals(reply.next_phrase_id)) {
			replyTypeCombo.setSelectedItem(replyTypes[END_INDEX]);
		} else if (Dialogue.Reply.FIGHT_PHRASE_ID.equals(reply.next_phrase_id)) {
			replyTypeCombo.setSelectedItem(replyTypes[FIGHT_INDEX]);
		} else if (Dialogue.Reply.REMOVE_PHRASE_ID.equals(reply.next_phrase_id)) {
			replyTypeCombo.setSelectedItem(replyTypes[REMOVE_INDEX]);
		} else if (Dialogue.Reply.SHOP_PHRASE_ID.equals(reply.next_phrase_id)) {
			replyTypeCombo.setSelectedItem(replyTypes[SHOP_INDEX]);
		} else {
			replyTypeCombo.setSelectedItem(replyTypes[STD_REPLY_INDEX]);
		}
		replyTypeCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (replyTypes[GO_NEXT_INDEX].equals(e.getItem())) {
						if (!Dialogue.Reply.GO_NEXT_TEXT.equals(reply.text) && reply.text != null) {
							replyTextCache = reply.text;
						}
						reply.text = Dialogue.Reply.GO_NEXT_TEXT;
						if (Dialogue.Reply.KEY_PHRASE_ID.contains(selectedReply.next_phrase_id)) {
							reply.next_phrase_id = null;
							reply.next_phrase = null;
						}
					} else {
						if (Dialogue.Reply.GO_NEXT_TEXT.equals(reply.text) || reply.text == null) {
							reply.text = replyTextCache;
						}
						if (!replyTypes[STD_REPLY_INDEX].equals(e.getItem())) {
							if (replyTypes[END_INDEX].equals(e.getItem())) {
								reply.next_phrase_id = Dialogue.Reply.EXIT_PHRASE_ID;
								reply.next_phrase = null;
							} else if (replyTypes[FIGHT_INDEX].equals(e.getItem())) {
								reply.next_phrase_id = Dialogue.Reply.FIGHT_PHRASE_ID;
								reply.next_phrase = null;
							} else if (replyTypes[REMOVE_INDEX].equals(e.getItem())) {
								reply.next_phrase_id = Dialogue.Reply.REMOVE_PHRASE_ID;
								reply.next_phrase = null;
							} else if (replyTypes[SHOP_INDEX].equals(e.getItem())) {
								reply.next_phrase_id = Dialogue.Reply.SHOP_PHRASE_ID;
								reply.next_phrase = null;
							} 
						} else if (Dialogue.Reply.KEY_PHRASE_ID.contains(selectedReply.next_phrase_id)) {
							reply.next_phrase_id = null;
							reply.next_phrase = null;
						}
					}
					listener.valueChanged(replyTypeCombo, null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			}
		});
		comboPane.add(replyTypeCombo, BorderLayout.CENTER);
		pane.add(comboPane, JideBoxLayout.FIX);
		updateRepliesParamsEditorPane(repliesParamsPane, reply, listener);
		pane.add(repliesParamsPane, JideBoxLayout.FIX);
		
		CollapsiblePanel requirementsPane = new CollapsiblePanel("Requirements the player must fulfill to select this reply: ");
		requirementsPane.setLayout(new JideBoxLayout(requirementsPane, JideBoxLayout.PAGE_AXIS));
		requirementsListModel = new ReplyRequirementsListModel(reply);
		requirementsList = new JList(requirementsListModel);
		requirementsList.setCellRenderer(new ReplyRequirementsCellRenderer());
		requirementsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		requirementsPane.add(new JScrollPane(requirementsList), JideBoxLayout.FIX);
		final JPanel requirementsEditorPane = new JPanel();
		final JButton createReq = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteReq = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		deleteReq.setEnabled(false);
		requirementsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedRequirement = (Requirement) requirementsList.getSelectedValue();
				if (selectedRequirement != null) {
					deleteReq.setEnabled(true);
				} else {
					deleteReq.setEnabled(false);
				}
				updateRequirementsEditorPane(requirementsEditorPane, selectedRequirement, listener);
			}
		});
		requirementsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (requirementsList.getSelectedValue() != null && ((Requirement)requirementsList.getSelectedValue()).required_obj != null) {
						ATContentStudio.frame.openEditor(((Requirement)requirementsList.getSelectedValue()).required_obj);
						ATContentStudio.frame.selectInTree(((Requirement)requirementsList.getSelectedValue()).required_obj);
					}
				}
			}
		});
		requirementsList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ATContentStudio.frame.openEditor(((Requirement)requirementsList.getSelectedValue()).required_obj);
					ATContentStudio.frame.selectInTree(((Requirement)requirementsList.getSelectedValue()).required_obj);
				}
			}
		});
		if (((Dialogue)target).writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createReq.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Requirement req = new Requirement();
					requirementsListModel.addItem(req);
					requirementsList.setSelectedValue(req, true);
					listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteReq.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedRequirement != null) {
						requirementsListModel.removeItem(selectedRequirement);
						selectedRequirement = null;
						requirementsList.clearSelection();
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createReq, JideBoxLayout.FIX);
			listButtonsPane.add(deleteReq, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			requirementsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		requirementsEditorPane.setLayout(new JideBoxLayout(requirementsEditorPane, JideBoxLayout.PAGE_AXIS));
		requirementsPane.add(requirementsEditorPane, JideBoxLayout.FIX);
		if (reply.requirements == null || reply.requirements.isEmpty()) {
			requirementsPane.collapse();
		}
		pane.add(requirementsPane, JideBoxLayout.FIX);
		
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateRepliesParamsEditorPane(final JPanel pane, final Dialogue.Reply reply, final FieldUpdateListener listener) {
		boolean writable = ((Dialogue)target).writable;
		pane.removeAll();

		if (replyNextPhrase != null) {
			removeElementListener(replyNextPhrase);
		}
		if (requirementObj != null) {
			removeElementListener(requirementObj);
		}
		
		if (Dialogue.Reply.GO_NEXT_TEXT.equals(reply.text)) {
			replyText = null;
			replyNextPhrase = addDialogueBox(pane, ((Dialogue)target).getProject(), "Next phrase: ", reply.next_phrase, writable, listener);
		} else if (Dialogue.Reply.KEY_PHRASE_ID.contains(reply.next_phrase_id)) {
			replyText = addTranslatableTextField(pane, "Reply text: ", reply.text, writable, listener);
			replyNextPhrase = null;
		} else {
			replyText = addTranslatableTextField(pane, "Reply text: ", reply.text, writable, listener);
			replyNextPhrase = addDialogueBox(pane, ((Dialogue)target).getProject(), "Next phrase: ", reply.next_phrase, writable, listener);
		}
		

		pane.revalidate();
		pane.repaint();
	}

	public void updateRequirementsEditorPane(final JPanel pane, final Requirement requirement, final FieldUpdateListener listener) {
		boolean writable = ((Dialogue)target).writable;
		pane.removeAll();

		if (requirementObj != null) {
			removeElementListener(requirementObj);
		}
		
		requirementTypeCombo = addEnumValueBox(pane, "Requirement type: ", Requirement.RequirementType.values(), requirement == null ? null : requirement.type, writable, listener);
		requirementParamsPane = new JPanel();
		requirementParamsPane.setLayout(new JideBoxLayout(requirementParamsPane, JideBoxLayout.PAGE_AXIS));
		updateRequirementParamsEditorPane(requirementParamsPane, requirement, listener);
		pane.add(requirementParamsPane, JideBoxLayout.FIX);
		pane.revalidate();
		pane.repaint();
	}
	
	public void updateRequirementParamsEditorPane(final JPanel pane, final Requirement requirement, final FieldUpdateListener listener) {
		boolean writable = ((Dialogue)target).writable;
		Project project = ((Dialogue)target).getProject();
		pane.removeAll();
		if (requirementObj != null) {
			removeElementListener(requirementObj);
		}
		
		if (requirement != null && requirement.type != null) {
			switch (requirement.type) {
			case consumedBonemeals:
			case spentGold:
				requirementObj = null;
				requirementObjId = null;
				requirementValue = addIntegerField(pane, "Quantity: ", requirement.required_value, false, writable, listener);
				break;
			case random:
				requirementObj = null;
				requirementObjId = addChanceField(pane, "Chance: ", requirement.required_obj_id, "50/100", writable, listener);
				requirementValue = null;
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
				requirementValue = addQuestStageBox(pane, project, "Quest stage: ", requirement.required_value, writable, listener, (Quest) requirement.required_obj, requirementObj);
				break;
			case skillLevel:
				Requirement.SkillID skillId = null;
				try {
					skillId = requirement.required_obj_id == null ? null : Requirement.SkillID.valueOf(requirement.required_obj_id);
				} catch(IllegalArgumentException e) {}
				requirementObj = null;
				requirementSkill = addEnumValueBox(pane, "Skill ID:", Requirement.SkillID.values(), skillId, writable, listener);
				requirementObjId = null;//addTextField(pane, "Skill ID:", requirement.required_obj_id, writable, listener);
				requirementValue = addIntegerField(pane, "Level: ", requirement.required_value, false, writable, listener);
				break;
			case timerElapsed:
				requirementObj = null;
				requirementObjId = addTextField(pane, "Timer ID:", requirement.required_obj_id, writable, listener);
				requirementValue = addIntegerField(pane, "Timer value: ", requirement.required_value, false, writable, listener);
				break;
			case factionScore:
				requirementObj = null;
				requirementObjId = addTextField(pane, "Faction ID:", requirement.required_obj_id, writable, listener);
				requirementValue = addIntegerField(pane, "Minimum score: ", requirement.required_value, true, writable, listener);
				break;
			case factionScoreEquals:
				requirementObj = null;
				requirementObjId = addTextField(pane, "Faction ID:", requirement.required_obj_id, writable, listener);
				requirementValue = addIntegerField(pane, "Exact value: ", requirement.required_value, true, writable, listener);
				break;
			case wear:
				requirementObj = addItemBox(pane, project, "Item: ", (Item) requirement.required_obj, writable, listener);
				requirementObjId = null;
				requirementValue = null;
				break;
			}
			requirementNegated = addBooleanBasedCheckBox(pane, "Negate this requirement.", requirement.negated, writable, listener);
		}
		pane.revalidate();
		pane.repaint();
	}
	
	
	public static class RewardsListModel implements ListModel<Dialogue.Reward> {
		
		Dialogue source;
		
		public RewardsListModel(Dialogue dialogue) {
			this.source = dialogue;
		}

		@Override
		public int getSize() {
			if (source.rewards == null) return 0;
			return source.rewards.size();
		}
		
		@Override
		public Dialogue.Reward getElementAt(int index) {
			if (source.rewards == null) return null;
			return source.rewards.get(index);
		}
		
		public void addItem(Dialogue.Reward item) {
			if (source.rewards == null) {
				source.rewards = new ArrayList<Dialogue.Reward>();
			}
			source.rewards.add(item);
			int index = source.rewards.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Dialogue.Reward item) {
			int index = source.rewards.indexOf(item);
			source.rewards.remove(item);
			if (source.rewards.isEmpty()) {
				source.rewards = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Dialogue.Reward item) {
			int index = source.rewards.indexOf(item);
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
	
	public static class RewardsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				Dialogue.Reward reward = (Dialogue.Reward)value;
				
				decorateRewardJLabel(label, reward);
			}
			return c;
		}
	}
	
	public static void decorateRewardJLabel(JLabel label, Dialogue.Reward reward) {
		if (reward.type != null) {
			String rewardObjDesc = null;
			if( reward.reward_obj != null) {
				rewardObjDesc = reward.reward_obj.getDesc();
			} else if (reward.reward_obj_id != null) {
				rewardObjDesc = reward.reward_obj_id;
			}
			switch (reward.type) {
			case activateMapObjectGroup:
				label.setText("Activate map object group "+rewardObjDesc+" on map "+reward.map_name);
				label.setIcon(new ImageIcon(DefaultIcons.getObjectLayerIcon()));
				break;
			case actorCondition:
				boolean rewardClear = reward.reward_value != null && reward.reward_value.intValue() == ActorCondition.MAGNITUDE_CLEAR;
				if (rewardClear) {
					label.setText("Clear actor condition "+rewardObjDesc);
				} else {
					boolean rewardForever = reward.reward_value != null && reward.reward_value.intValue() == ActorCondition.DURATION_FOREVER;
					label.setText("Give actor condition "+rewardObjDesc+(rewardForever ? " forever" : " for "+reward.reward_value+" turns"));
				}
				if (reward.reward_obj != null) label.setIcon(new ImageIcon(reward.reward_obj.getIcon()));
				break;
			case actorConditionImmunity:
				boolean rewardForever = reward.reward_value == null || reward.reward_value.intValue() == ActorCondition.DURATION_FOREVER;
				label.setText("Give immunity to actor condition "+rewardObjDesc+(rewardForever ? " forever" : " for "+reward.reward_value+" turns"));
				if (reward.reward_obj != null) label.setIcon(new OverlayIcon(reward.reward_obj.getIcon(), DefaultIcons.getImmunityIcon()));
				break;
			case alignmentChange:
				label.setText("Change alignment for faction "+rewardObjDesc+" : "+reward.reward_value);
				label.setIcon(new ImageIcon(DefaultIcons.getAlignmentIcon()));
				break;
			case alignmentSet:
				label.setText("Set alignment for faction "+rewardObjDesc+" : "+reward.reward_value);
				label.setIcon(new ImageIcon(DefaultIcons.getAlignmentIcon()));
				break;
			case createTimer:
				label.setText("Create timer "+rewardObjDesc);
				label.setIcon(new ImageIcon(DefaultIcons.getTimerIcon()));
				break;
			case deactivateMapObjectGroup:
				label.setText("Deactivate map object group "+rewardObjDesc+" on map "+reward.map_name);
				label.setIcon(new ImageIcon(DefaultIcons.getObjectLayerIcon()));
				break;
			case deactivateSpawnArea:
				label.setText("Deactivate spawnarea area "+rewardObjDesc+" on map "+reward.map_name);
				label.setIcon(new ImageIcon(DefaultIcons.getNPCIcon()));
				break;
			case dropList:
				label.setText("Give contents of droplist "+rewardObjDesc);
				if (reward.reward_obj != null) label.setIcon(new ImageIcon(reward.reward_obj.getIcon()));
				break;
			case giveItem:
				label.setText("Give "+reward.reward_value+" "+rewardObjDesc);
				if (reward.reward_obj != null) label.setIcon(new ImageIcon(reward.reward_obj.getIcon()));
				break;
			case questProgress:
				label.setText("Give quest progress "+rewardObjDesc+":"+reward.reward_value);
				if (reward.reward_obj != null) label.setIcon(new ImageIcon(reward.reward_obj.getIcon()));
				break;
			case removeQuestProgress:
				label.setText("Removes quest progress "+rewardObjDesc+":"+reward.reward_value);
				if (reward.reward_obj != null) label.setIcon(new ImageIcon(reward.reward_obj.getIcon()));
				break;
			case removeSpawnArea:
				label.setText("Remove all monsters in spawnarea area "+rewardObjDesc+" on map "+reward.map_name);
				label.setIcon(new ImageIcon(DefaultIcons.getNPCIcon()));
				break;
			case skillIncrease:
				label.setText("Increase skill "+rewardObjDesc+" level");
				label.setIcon(new ImageIcon(DefaultIcons.getSkillIcon()));
				break;
			case spawnAll:
				label.setText("Respawn all monsters in spawnarea area "+rewardObjDesc+" on map "+reward.map_name);
				label.setIcon(new ImageIcon(DefaultIcons.getNPCIcon()));
				break;
			case changeMapFilter:
				label.setText("Change map filter to "+rewardObjDesc+" on map "+reward.map_name);
				label.setIcon(new ImageIcon(DefaultIcons.getReplaceIcon()));
				break;
			}
		} else {
			label.setText("New, undefined reward");
		}
	}
	
	
	public static class RepliesListModel implements ListModel<Dialogue.Reply> {

		Dialogue source;

		public RepliesListModel(Dialogue dialogue) {
			this.source = dialogue;
		}

		
		@Override
		public int getSize() {
			if (source.replies == null) return 0;
			return source.replies.size();
		}

		@Override
		public Dialogue.Reply getElementAt(int index) {
			if (source.replies == null) return null;
			return source.replies.get(index);
		}
		
		public void addItem(Dialogue.Reply item) {
			if (source.replies == null) {
				source.replies = new ArrayList<Dialogue.Reply>();
			}
			source.replies.add(item);
			int index = source.replies.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Dialogue.Reply item) {
			int index = source.replies.indexOf(item);
			source.replies.remove(item);
			if (source.replies.isEmpty()) {
				source.replies = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Dialogue.Reply item) {
			int index = source.replies.indexOf(item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
			}
		}
		
		public void moveUp(Dialogue.Reply item) {
			int index = source.replies.indexOf(item);
			Dialogue.Reply exchanged = source.replies.get(index - 1);
			source.replies.set(index, exchanged);
			source.replies.set(index - 1, item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index - 1, index));
			}
		}

		public void moveDown(Dialogue.Reply item) {
			int index = source.replies.indexOf(item);
			Dialogue.Reply exchanged = source.replies.get(index + 1);
			source.replies.set(index, exchanged);
			source.replies.set(index + 1, item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index + 1));
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

	public static class RepliesCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				Dialogue.Reply reply = (Dialogue.Reply)value;
				StringBuffer buf = new StringBuffer();
				if (reply.requirements != null) {
					buf.append("[Reqs]");
				}
				if (reply.next_phrase_id != null && reply.next_phrase_id.equals(Dialogue.Reply.EXIT_PHRASE_ID)) {
					buf.append("[Ends dialogue] ");
					buf.append((reply.text != null ? reply.text : ""));
					label.setIcon(new ImageIcon(DefaultIcons.getNullifyIcon()));
				} else if (reply.next_phrase_id != null && reply.next_phrase_id.equals(Dialogue.Reply.FIGHT_PHRASE_ID)) {
					buf.append("[Starts fight] ");
					buf.append((reply.text != null ? reply.text : ""));
					label.setIcon(new ImageIcon(DefaultIcons.getCombatIcon()));
				} else if (reply.next_phrase_id != null && reply.next_phrase_id.equals(Dialogue.Reply.REMOVE_PHRASE_ID)) {
					buf.append("[NPC vanishes] ");
					buf.append((reply.text != null ? reply.text : ""));
					label.setIcon(new ImageIcon(DefaultIcons.getNPCCloseIcon()));
				} else if (reply.next_phrase_id != null && reply.next_phrase_id.equals(Dialogue.Reply.SHOP_PHRASE_ID)) {
					buf.append("[Start trading] ");
					buf.append((reply.text != null ? reply.text : ""));
					label.setIcon(new ImageIcon(DefaultIcons.getGoldIcon()));
				} else if (reply.text != null && reply.text.equals(Dialogue.Reply.GO_NEXT_TEXT)) {
					buf.append("[NPC keeps talking] ");
					buf.append(reply.next_phrase_id);
					label.setIcon(new ImageIcon(DefaultIcons.getArrowRightIcon()));
				} else if (reply.next_phrase_id != null) {
					buf.append("[Dialogue goes on] ");
					buf.append((reply.text != null ? reply.text : ""));
					buf.append(" -> ");
					buf.append(reply.next_phrase_id);
					label.setIcon(new ImageIcon(DefaultIcons.getDialogueIcon()));
				} else if (reply.next_phrase == null && reply.next_phrase_id == null && reply.requirements == null && reply.text == null) {
					buf.append("New, undefined reply");
				} else {
					buf.append("[Incomplete reply]");
				}
				label.setText(buf.toString());
			}
			return c;
		}
	}

	public static class ReplyRequirementsListModel implements ListModel<Requirement> {

		Dialogue.Reply reply;
		
		public ReplyRequirementsListModel(Dialogue.Reply reply) {
			this.reply = reply;
		}
		
		@Override
		public int getSize() {
			if (reply.requirements == null) return 0;
			return reply.requirements.size();
		}

		@Override
		public Requirement getElementAt(int index) {
			if (reply.requirements == null) return null;
			return reply.requirements.get(index);
		}
		

		
		public void addItem(Requirement item) {
			if (reply.requirements == null) {
				reply.requirements = new ArrayList<Requirement>();
			}
			reply.requirements.add(item);
			int index = reply.requirements.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Requirement item) {
			int index = reply.requirements.indexOf(item);
			reply.requirements.remove(item);
			if (reply.requirements.isEmpty()) {
				reply.requirements = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Requirement item) {
			int index = reply.requirements.indexOf(item);
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
	
	public static class ReplyRequirementsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				decorateRequirementJLabel((JLabel)c, (Requirement)value);
			}
			return c;
		}
	}
	
	public static void decorateRequirementJLabel(JLabel label, Requirement req) {
		label.setText(req.getDesc());
		if (req.required_obj != null) {
			if (req.required_obj.getIcon() != null) {
				label.setIcon(new ImageIcon(req.required_obj.getIcon()));
			}
		} else if (req.type == Requirement.RequirementType.skillLevel) {
			label.setIcon(new ImageIcon(DefaultIcons.getSkillIcon()));
		} else if (req.type == Requirement.RequirementType.spentGold) {
			label.setIcon(new ImageIcon(DefaultIcons.getGoldIcon()));
		} else if (req.type == Requirement.RequirementType.consumedBonemeals) {
			label.setIcon(new ImageIcon(DefaultIcons.getBonemealIcon()));
		} else if (req.type == Requirement.RequirementType.timerElapsed) {
			label.setIcon(new ImageIcon(DefaultIcons.getTimerIcon()));
		} else if (req.type == Requirement.RequirementType.factionScore || req.type == Requirement.RequirementType.factionScoreEquals) {
			label.setIcon(new ImageIcon(DefaultIcons.getAlignmentIcon()));
		}
		if (req.type == null) {
			label.setText("New, undefined requirement.");
		}
	}
	
	public class DialogueFieldUpdater implements FieldUpdateListener {
		@Override
		public void valueChanged(JComponent source, Object value) {
			Dialogue dialogue = (Dialogue) target;
			if (source == idField) {
				//Events caused by cancel an ID edition. Dismiss.
				if (skipNext) {
					skipNext = false;
					return;
				}
				if (target.id.equals((String) value)) return;
				
				if (idChanging()) {
					dialogue.id = (String) value;
					DialogueEditor.this.name = dialogue.getDesc();
					dialogue.childrenChanged(new ArrayList<ProjectTreeNode>());
					ATContentStudio.frame.editorChanged(DialogueEditor.this);
				} else {
					cancelIdEdit(idField);
					return;
				}
			} else if (source == messageField) {
				dialogue.message = (String) value;
			} else if (source == switchToNpcBox) {
				if (dialogue.switch_to_npc != null) {
					dialogue.switch_to_npc.removeBacklink(dialogue);
				}
				dialogue.switch_to_npc = (NPC) value;
				if (dialogue.switch_to_npc != null) {
					dialogue.switch_to_npc_id = dialogue.switch_to_npc.id;
					dialogue.switch_to_npc.addBacklink(dialogue);
				} else {
					dialogue.switch_to_npc_id = null;
				}
			} else if (source == rewardTypeCombo) {
				if (selectedReward.type != value) {
					selectedReward.type = (Dialogue.Reward.RewardType) value;
					if (selectedReward.map != null) {
						selectedReward.map.removeBacklink(dialogue);
					}
					selectedReward.map = null;
					selectedReward.map_name = null;
					selectedReward.reward_obj = null;
					selectedReward.reward_obj_id = null;
					selectedReward.reward_value = null;
					rewardsListModel.itemChanged(selectedReward);
					updateRewardsParamsEditorPane(rewardsParamsPane, selectedReward, this);
				}
			} else if (source == rewardMap) {
				if (selectedReward.map != null) {
					selectedReward.map.removeBacklink(dialogue);
				}
				selectedReward.map = (TMXMap) value;
				if (selectedReward.map != null) {
					selectedReward.map_name = selectedReward.map.id;
					selectedReward.map.addBacklink(dialogue);
				} else {
					selectedReward.map_name = null;
				}
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardObjId) {
				selectedReward.reward_obj_id = rewardObjId.getText();
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardObjIdCombo) {
				selectedReward.reward_obj_id = rewardObjIdCombo.getSelectedItem().toString();
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardObj) {
				if (selectedReward.reward_obj != null) {
					selectedReward.reward_obj.removeBacklink(dialogue);
				}
				selectedReward.reward_obj = (GameDataElement) value;
				if (selectedReward.reward_obj != null) {
					selectedReward.reward_obj_id = selectedReward.reward_obj.id;
					selectedReward.reward_obj.addBacklink(dialogue);
				} else {
					selectedReward.reward_obj_id = null;
				}
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardValue) {
				//Backlink removal to quest stages when selecting another quest are handled in the addQuestStageBox() method. Too complex too handle here
				Quest quest = null;
				QuestStage stage = null;
				if (rewardValue instanceof JComboBox<?>) {
					quest = ((Quest)selectedReward.reward_obj);
					if (quest != null && selectedReward.reward_value != null) {
						stage = quest.getStage(selectedReward.reward_value);
						if (stage != null) stage.removeBacklink(dialogue);
					}
				}
				selectedReward.reward_value = (Integer) value;
				if (quest != null) {
					stage = quest.getStage(selectedReward.reward_value);
					if (stage != null) stage.addBacklink(dialogue);
				}
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardConditionClear) {
				selectedReward.reward_value = ActorCondition.MAGNITUDE_CLEAR;
				rewardValue.setEnabled(false);
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardConditionForever) {
				selectedReward.reward_value = ActorCondition.DURATION_FOREVER;
				rewardValue.setEnabled(false);
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == rewardConditionTimed) {
				selectedReward.reward_value = (Integer) ((JSpinner)rewardValue).getValue();
				rewardValue.setEnabled(true);
				rewardsListModel.itemChanged(selectedReward);
			} else if (source == replyTypeCombo) {
				updateRepliesParamsEditorPane(repliesParamsPane, selectedReply, this);
				repliesListModel.itemChanged(selectedReply);
			} else if (source == replyText) {
				selectedReply.text = (String) value;
				repliesListModel.itemChanged(selectedReply);
			} else if (source == replyNextPhrase) {
				if (selectedReply.next_phrase != null) {
					selectedReply.next_phrase.removeBacklink(dialogue);
				}
				selectedReply.next_phrase = (Dialogue) value;
				if (selectedReply.next_phrase != null) {
					selectedReply.next_phrase_id = selectedReply.next_phrase.id;
					selectedReply.next_phrase.addBacklink(dialogue);
				} else {
					selectedReply.next_phrase_id = null;
				}
				repliesListModel.itemChanged(selectedReply);
			} else if (source == requirementTypeCombo) {
				selectedRequirement.changeType((Requirement.RequirementType)requirementTypeCombo.getSelectedItem());
				updateRequirementParamsEditorPane(requirementParamsPane, selectedRequirement, this);
				requirementsListModel.itemChanged(selectedRequirement);
			} else if (source == requirementObj) {
				if (selectedRequirement.required_obj != null) {
					selectedRequirement.required_obj.removeBacklink(dialogue);
				}
				selectedRequirement.required_obj = (GameDataElement) value;
				if (selectedRequirement.required_obj != null) {
					selectedRequirement.required_obj_id = selectedRequirement.required_obj.id;
					selectedRequirement.required_obj.addBacklink(dialogue);
				} else {
					selectedRequirement.required_obj_id = null;
				}
				requirementsListModel.itemChanged(selectedRequirement);
			} else if (source == requirementSkill) {
				if (selectedRequirement.required_obj != null) {
					selectedRequirement.required_obj.removeBacklink(dialogue);
					selectedRequirement.required_obj = null;
				}
				if (selectedRequirement.type == Requirement.RequirementType.skillLevel) {
					selectedRequirement.required_obj_id = value == null ? null : value.toString();
				}
				requirementsListModel.itemChanged(selectedRequirement);
			} else if (source == requirementObjId) {
				selectedRequirement.required_obj_id = (String) value;
				selectedRequirement.required_obj = null;
				requirementsListModel.itemChanged(selectedRequirement);
			} else if (source == requirementValue) {
				//Backlink removal to quest stages when selecting another quest are handled in the addQuestStageBox() method. Too complex too handle here
				Quest quest = null;
				QuestStage stage = null;
				if (requirementValue instanceof JComboBox<?>) {
					quest = ((Quest)selectedRequirement.required_obj);
					if (quest != null && selectedRequirement.required_value != null) {
						stage = quest.getStage(selectedRequirement.required_value);
						if (stage != null) stage.removeBacklink(dialogue);
					}
				}
				selectedRequirement.required_value = (Integer) value;
				if (quest != null) {
					stage = quest.getStage(selectedRequirement.required_value);
					if (stage != null) stage.addBacklink(dialogue);
				}
				requirementsListModel.itemChanged(selectedRequirement);
			} else if (source == requirementNegated) {
				selectedRequirement.negated = (Boolean) value;
			} 
			
			if (dialogue.state != GameDataElement.State.modified) {
				dialogue.state = GameDataElement.State.modified;
				DialogueEditor.this.name = dialogue.getDesc();
				dialogue.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(DialogueEditor.this);
			}
			updateJsonViewText(dialogue.toJsonString());
		}
	}
	
}
