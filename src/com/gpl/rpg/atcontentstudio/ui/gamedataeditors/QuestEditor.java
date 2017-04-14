package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
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
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.Quest;
import com.gpl.rpg.atcontentstudio.model.gamedata.QuestStage;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.IntegerBasedCheckBox;
import com.jidesoft.swing.JideBoxLayout;

public class QuestEditor extends JSONElementEditor {

	private static final long serialVersionUID = 5701667955210615366L;

	private static final Integer one = 1;
	
	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	
	private QuestStage selectedStage = null;
	
	private JTextField idField;
	private JTextField nameField;
	private IntegerBasedCheckBox visibleBox;
	private StagesListModel stagesListModel;
	private JList<QuestStage> stagesList;
	
//	private JPanel stagesParamPane;
	private JSpinner progressField;
	private JTextArea logTextField;
	private JSpinner xpRewardField;
	private IntegerBasedCheckBox finishQuestBox;
	
	
	
	public QuestEditor(Quest quest) {
		super(quest, quest.getDesc(), quest.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
	}

	public void insertFormViewDataField(JPanel pane) {
		final Quest quest = ((Quest)target);

		final FieldUpdateListener listener = new QuestFieldUpdater();
		
		createButtonPane(pane, quest.getProject(), quest, Quest.class, quest.getImage(), null, listener);
		

		idField = addTextField(pane, "Internal ID: ", quest.id, quest.writable, listener);
		nameField = addTranslatableTextField(pane, "Quest Name: ", quest.name, quest.writable, listener);
		visibleBox = addIntegerBasedCheckBox(pane, "Visible in quest log", quest.visible_in_log, quest.writable, listener);

		CollapsiblePanel stagesPane = new CollapsiblePanel("Quest stages: ");
		stagesPane.setLayout(new JideBoxLayout(stagesPane, JideBoxLayout.PAGE_AXIS));
		stagesListModel = new StagesListModel(quest);
		stagesList = new JList<QuestStage>(stagesListModel);
		stagesList.setCellRenderer(new StagesCellRenderer());
		stagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stagesPane.add(new JScrollPane(stagesList), JideBoxLayout.FIX);
		final JPanel stagesEditorPane = new JPanel();
		final JButton createStage = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteStage = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		final JButton moveStageUp = new JButton(new ImageIcon(DefaultIcons.getArrowUpIcon()));
		final JButton moveStageDown = new JButton(new ImageIcon(DefaultIcons.getArrowDownIcon()));
		deleteStage.setEnabled(false);
		moveStageUp.setEnabled(false);
		moveStageDown.setEnabled(false);
		stagesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedStage = (QuestStage) stagesList.getSelectedValue();
				if (selectedStage != null) {
					deleteStage.setEnabled(true);
					moveStageUp.setEnabled(stagesList.getSelectedIndex() > 0);
					moveStageDown.setEnabled(stagesList.getSelectedIndex() < (stagesListModel.getSize() - 1));
				} else {
					deleteStage.setEnabled(false);
					moveStageUp.setEnabled(false);
					moveStageDown.setEnabled(false);
				}
				updateStageEditorPane(stagesEditorPane, selectedStage, listener);
			}
		});
		if (quest.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createStage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					QuestStage stage = new QuestStage(quest);
					stagesListModel.addItem(stage);
					stagesList.setSelectedValue(stage, true);
					listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteStage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedStage != null) {
						stagesListModel.removeItem(selectedStage);
						selectedStage = null;
						stagesList.clearSelection();
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			moveStageUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedStage != null) {
						stagesListModel.moveUp(selectedStage);
						stagesList.setSelectedValue(selectedStage, true);
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			moveStageDown.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedStage != null) {
						stagesListModel.moveDown(selectedStage);
						stagesList.setSelectedValue(selectedStage, true);
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			listButtonsPane.add(createStage, JideBoxLayout.FIX);
			listButtonsPane.add(deleteStage, JideBoxLayout.FIX);
			listButtonsPane.add(moveStageUp, JideBoxLayout.FIX);
			listButtonsPane.add(moveStageDown, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			stagesPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		if (quest.stages == null || quest.stages.isEmpty()) {
			stagesPane.collapse();
		}
		stagesEditorPane.setLayout(new JideBoxLayout(stagesEditorPane, JideBoxLayout.PAGE_AXIS));
		stagesPane.add(stagesEditorPane, JideBoxLayout.FIX);
		pane.add(stagesPane, JideBoxLayout.FIX);

	}
	
	public void updateStageEditorPane(JPanel pane, QuestStage selectedStage, FieldUpdateListener listener) {
		pane.removeAll();
		if (selectedStage != null) {
			boolean writable = ((Quest)target).writable;
			progressField = addIntegerField(pane, "Progress ID: ", selectedStage.progress, false, writable, listener);
			logTextField = addTranslatableTextArea(pane, "Log text: ", selectedStage.log_text, writable, listener);
			xpRewardField = addIntegerField(pane, "XP Reward: ", selectedStage.exp_reward, false, writable, listener);
			finishQuestBox = addIntegerBasedCheckBox(pane, "Finishes quest", selectedStage.finishes_quest, writable, listener);
			addBacklinksList(pane, selectedStage, "Elements linking to this quest stage");

		}
		pane.revalidate();
		pane.repaint();
	}
	
	public static class StagesListModel implements ListModel<QuestStage> {

		Quest source;

		public StagesListModel(Quest quest) {
			this.source = quest;
		}

		
		@Override
		public int getSize() {
			if (source.stages == null) return 0;
			return source.stages.size();
		}

		@Override
		public QuestStage getElementAt(int index) {
			if (source.stages == null) return null;
			return source.stages.get(index);
		}
		
		public void addItem(QuestStage item) {
			if (source.stages == null) {
				source.stages = new ArrayList<QuestStage>();
			}
			source.stages.add(item);
			int index = source.stages.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(QuestStage item) {
			int index = source.stages.indexOf(item);
			source.stages.remove(item);
			if (source.stages.isEmpty()) {
				source.stages = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(QuestStage item) {
			int index = source.stages.indexOf(item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
			}
		}
		
		public void moveUp(QuestStage item) {
			int index = source.stages.indexOf(item);
			QuestStage exchanged = source.stages.get(index - 1);
			source.stages.set(index, exchanged);
			source.stages.set(index - 1, item);
			for (ListDataListener l : listeners) {
				l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index - 1, index));
			}
		}

		public void moveDown(QuestStage item) {
			int index = source.stages.indexOf(item);
			QuestStage exchanged = source.stages.get(index + 1);
			source.stages.set(index, exchanged);
			source.stages.set(index + 1, item);
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
	
	
	public static class StagesCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				label.setText(((QuestStage)value).getDesc());
				label.setIcon(new ImageIcon(((QuestStage)value).getIcon()));
			}
			return c;
		}
	}
	
	
	public class QuestFieldUpdater implements FieldUpdateListener {

		@Override
		public void valueChanged(JComponent source, Object value) {
			Quest quest = (Quest) target;
			if (source == idField) {
				//Events caused by cancel an ID edition. Dismiss.
				if (skipNext) {
					skipNext = false;
					return;
				}
				if (target.id.equals((String) value)) return;
				
				if (idChanging()) {
					quest.id = (String) value;
					QuestEditor.this.name = quest.getDesc();
					quest.childrenChanged(new ArrayList<ProjectTreeNode>());
					ATContentStudio.frame.editorChanged(QuestEditor.this);
				} else {
					cancelIdEdit(idField);
					return;
				}
			} else if (source == nameField) {
				quest.name = (String) value;
				QuestEditor.this.name = quest.getDesc();
				quest.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(QuestEditor.this);
			} else if (source == visibleBox) {
				quest.visible_in_log = (Integer) value;
			} else if (source == progressField) {
				selectedStage.progress = (Integer) value;
				stagesListModel.itemChanged(selectedStage);
			} else if (source == logTextField) {
				selectedStage.log_text = (String) value;
				stagesListModel.itemChanged(selectedStage);
			} else if (source == xpRewardField) {
				selectedStage.exp_reward = (Integer) value;
				stagesListModel.itemChanged(selectedStage);
			} else if (source == finishQuestBox) {
				selectedStage.finishes_quest = (Integer) value;
				stagesListModel.itemChanged(selectedStage);
			}
			

			if (quest.state != GameDataElement.State.modified) {
				quest.state = GameDataElement.State.modified;
				QuestEditor.this.name = quest.getDesc();
				quest.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(QuestEditor.this);
			}
			updateJsonViewText(quest.toJsonString());
		}
		
		
	}
	

}
