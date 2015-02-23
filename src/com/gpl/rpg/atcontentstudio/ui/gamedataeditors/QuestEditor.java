package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.gamedata.Quest;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.IntegerBasedCheckBox;
import com.jidesoft.swing.JideBoxLayout;

public class QuestEditor extends JSONElementEditor {

	private static final long serialVersionUID = 5701667955210615366L;

	private static final Integer one = 1;
	
	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	
	private JTextField idField;
	private JTextField nameField;
	private IntegerBasedCheckBox visibleBox;
	private QuestStageTableModel stagesModel;
	private JTable stagesTable;
	private JButton createStage;
	private JButton deleteStage;
	private JButton moveUp;
	private JButton moveDown;
	
	
	public QuestEditor(Quest quest) {
		super(quest, quest.getDesc(), quest.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
	}

	public void insertFormViewDataField(JPanel pane) {
		final Quest quest = ((Quest)target);

		final FieldUpdateListener listener = new QuestFieldUpdater();
		
		createButtonPane(pane, quest.getProject(), quest, Quest.class, quest.getImage(), null, listener);
		

		addTextField(pane, "Internal ID: ", quest.id, quest.writable, listener);
		addTextField(pane, "Quest Name: ", quest.name, quest.writable, listener);
		addIntegerBasedCheckBox(pane, "Visible in quest log", quest.visible_in_log, quest.writable, listener);

		JPanel stagesPane = new JPanel();
		stagesPane.setLayout(new JideBoxLayout(stagesPane, JideBoxLayout.PAGE_AXIS, 6));
		stagesModel = new QuestStageTableModel(quest, listener);
		stagesTable = new JTable(stagesModel);
		stagesTable.getColumnModel().getColumn(0).setMinWidth(100);
		stagesTable.getColumnModel().getColumn(0).setMaxWidth(100);
//		stagesTable.getColumnModel().getColumn(1).setPreferredWidth(40);
//		stagesTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		stagesTable.getColumnModel().getColumn(2).setMinWidth(100);
		stagesTable.getColumnModel().getColumn(2).setMaxWidth(100);
		stagesTable.getColumnModel().getColumn(3).setMinWidth(130);
		stagesTable.getColumnModel().getColumn(3).setMaxWidth(130);
		stagesTable.setCellSelectionEnabled(true);
		stagesPane.add(new JScrollPane(stagesTable), BorderLayout.CENTER);
		if (quest.writable) {
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new JideBoxLayout(buttonPane, JideBoxLayout.LINE_AXIS, 6));
			createStage = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
			deleteStage = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
			moveUp = new JButton(new ImageIcon(DefaultIcons.getArrowUpIcon()));
			moveDown = new JButton(new ImageIcon(DefaultIcons.getArrowDownIcon()));
			buttonPane.add(createStage, JideBoxLayout.FIX);
			buttonPane.add(deleteStage, JideBoxLayout.FIX);
			buttonPane.add(moveUp, JideBoxLayout.FIX);
			buttonPane.add(moveDown, JideBoxLayout.FIX);
			buttonPane.add(new JPanel(), JideBoxLayout.VARY);
			deleteStage.setEnabled(false);
			moveUp.setEnabled(false);
			moveDown.setEnabled(false);
			
			stagesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					updateTableButtons();
				}
			});
			
			createStage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					stagesModel.createStage();
					listener.valueChanged(stagesTable, null);
					stagesTable.revalidate();
					stagesTable.repaint();
				}
			});
			deleteStage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					stagesModel.deleteRow(stagesTable.getSelectedRow());
					listener.valueChanged(stagesTable, null);
					stagesTable.revalidate();
					stagesTable.repaint();
					updateTableButtons();
				}
			});
			moveUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					stagesModel.moveRow(stagesTable.getSelectedRow(), true);
					listener.valueChanged(stagesTable, null);
					stagesTable.setRowSelectionInterval(stagesTable.getSelectedRow() - 1, stagesTable.getSelectedRow() - 1);
					updateTableButtons();
				}
			});
			moveDown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					stagesModel.moveRow(stagesTable.getSelectedRow(), false);
					listener.valueChanged(stagesTable, null);
					stagesTable.setRowSelectionInterval(stagesTable.getSelectedRow() + 1, stagesTable.getSelectedRow() + 1);
					updateTableButtons();
				}
			});
			stagesPane.add(buttonPane, JideBoxLayout.FIX);
		}
		pane.add(stagesPane, JideBoxLayout.FIX);

	}
	
	public void updateTableButtons() {

		if (stagesTable.getSelectedRow() >= 0 && stagesTable.getSelectedRow() < stagesModel.getRowCount()) {
			deleteStage.setEnabled(true);
			if (stagesTable.getSelectedRow() == 0) {
				moveUp.setEnabled(false);
			} else {
				moveUp.setEnabled(true);
			}
			if (stagesTable.getSelectedRow() >= stagesModel.getRowCount() - 1) {
				moveDown.setEnabled(false);
			} else {
				moveDown.setEnabled(true);
			}
		} else {
			deleteStage.setEnabled(false);
			moveUp.setEnabled(false);
			moveDown.setEnabled(false);
		}
	
	}
	
	public class QuestStageTableModel implements TableModel {

		Quest quest;
		FieldUpdateListener listener;
		
		public QuestStageTableModel(Quest q, FieldUpdateListener listener) {
			this.quest = q;
			this.listener = listener;
		}
		
		@Override
		public int getRowCount() {
			if (quest.stages == null) return 0;
			return quest.stages.size();
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "Progress ID";
			case 1:
				return "Log text";
			case 2:
				return "XP reward";
			case 3:
				return "Finishes quest";
			default:
				return "???";
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Integer.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			case 3:
				return Boolean.class;
			default:
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return quest.writable;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return quest.stages.get(rowIndex).progress;
			case 1:
				return quest.stages.get(rowIndex).log_text;
			case 2:
				return quest.stages.get(rowIndex).exp_reward;
			case 3:
				return quest.stages.get(rowIndex).finishes_quest != null && quest.stages.get(rowIndex).finishes_quest.equals(1);
			default:
				return null;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				quest.stages.get(rowIndex).progress = (Integer)aValue;
				break;
			case 1:
				quest.stages.get(rowIndex).log_text = (String)aValue;
				break;
			case 2:
				quest.stages.get(rowIndex).exp_reward = (Integer)aValue;
				break;
			case 3:
				quest.stages.get(rowIndex).finishes_quest = ((Boolean)aValue) ? one : null;
				break;
			}
			listener.valueChanged(stagesTable, aValue);
		}

		public void createStage() {
			quest.stages.add(new Quest.QuestStage());
			for (TableModelListener l: listeners) {
				l.tableChanged(new TableModelEvent(this, quest.stages.size() - 1));
			}
		}
		
		public void moveRow(int rowNumber, boolean moveUp) {
			Quest.QuestStage stage = quest.stages.get(rowNumber);
			quest.stages.remove(stage);
			quest.stages.add(rowNumber + (moveUp ? -1 : 1), stage);
			for (TableModelListener l : listeners) {
				l.tableChanged(new TableModelEvent(this, rowNumber + (moveUp ? -1 : 0), rowNumber + (moveUp ? 0 : 1)));
			}
		}
		
		public void deleteRow(int rowNumber) {
			quest.stages.remove(rowNumber);
			for (TableModelListener l: listeners) {
				l.tableChanged(new TableModelEvent(this, rowNumber, quest.stages.size()));
			}
		}
		
		public List<TableModelListener> listeners = new ArrayList<TableModelListener>();
		
		@Override
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}
		
	}
	
	public class QuestFieldUpdater implements FieldUpdateListener {

		@Override
		public void valueChanged(JComponent source, Object value) {
			Quest quest = (Quest) target;
			if (source == idField) {
				quest.id = (String) value;
				QuestEditor.this.name = quest.getDesc();
				quest.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(QuestEditor.this);
			} else if (source == nameField) {
				quest.name = (String) value;
				QuestEditor.this.name = quest.getDesc();
				quest.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(QuestEditor.this);
			} else if (source == visibleBox) {
				quest.visible_in_log = (Integer) value;
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
