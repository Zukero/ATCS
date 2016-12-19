package com.gpl.rpg.atcontentstudio.ui;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.DefaultFormatter;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectElementListener;
import com.gpl.rpg.atcontentstudio.model.gamedata.ActorCondition;
import com.gpl.rpg.atcontentstudio.model.gamedata.Dialogue;
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist;
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.model.gamedata.ItemCategory;
import com.gpl.rpg.atcontentstudio.model.gamedata.JSONElement;
import com.gpl.rpg.atcontentstudio.model.gamedata.NPC;
import com.gpl.rpg.atcontentstudio.model.gamedata.Quest;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMap;
import com.jidesoft.swing.ComboBoxSearchable;
import com.jidesoft.swing.JideBoxLayout;

public abstract class Editor extends JPanel implements ProjectElementListener {

	private static final long serialVersionUID = 241750514033596878L;
	private static final FieldUpdateListener nullListener = new FieldUpdateListener() {@Override public void valueChanged(JComponent source, Object value) {}};
	
	public static final String SAVE = "Save";
	public static final String DELETE = "Delete";
	public static final String REVERT = "Revert to original";
	public static final String ALTER = "Alter";
	public static final String GO_TO_ALTERED = "Go to altered";
	
	
	public static final String READ_ONLY_MESSAGE = 
				"<html><i>" +
				"This element is not modifiable.<br/>" +
				"Click on the \"Alter\" button to create a writable copy." +
				"</i></html>";

	public static final String ALTERED_EXISTS_MESSAGE = 
				"<html><i>" +
				"This element is not modifiable.<br/>" +
				"A writable copy exists in this project. Click on \"Go to altered\" to open it." +
				"</i></html>";
	
	public static final String ALTERED_MESSAGE =
				"<html><i>" +
				"This element is a writable copy of an element of the referenced game source.<br/>" +
				"Take care not to break existing content when modifying it." +
				"</i></html>";

	public static final String CREATED_MESSAGE =
				"<html><i>" +
				"This element is a creation of yours.<br/>" +
				"Do as you please." +
				"</i></html>";
	

	public String name = "Editor";
	public Icon icon = null;
	public GameDataElement target = null;

	public JLabel message = null;
	
	
	public static JTextField addLabelField(JPanel pane, String label, String value) {
		return addTextField(pane, label, value, false, nullListener);
	}
	
	public static JTextField addTextField(JPanel pane, String label, String initialValue, boolean editable, final FieldUpdateListener listener) {
		JPanel tfPane = new JPanel();
		tfPane.setLayout(new JideBoxLayout(tfPane, JideBoxLayout.LINE_AXIS, 6));
		JLabel tfLabel = new JLabel(label);
		tfPane.add(tfLabel, JideBoxLayout.FIX);
		final JTextField tfField = new JTextField(initialValue);
		tfField.setEditable(editable);
		tfPane.add(tfField, JideBoxLayout.VARY);
		JButton nullify = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		tfPane.add(nullify, JideBoxLayout.FIX);
		nullify.setEnabled(editable);
		pane.add(tfPane, JideBoxLayout.FIX);
		
		nullify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tfField.setText("");
				listener.valueChanged(tfField, null);
			}
		});
		tfField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				listener.valueChanged(tfField, tfField.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				listener.valueChanged(tfField, tfField.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				listener.valueChanged(tfField, tfField.getText());
			}
		});
		tfField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(tfField, tfField.getText());
			}
		});
		return tfField;
	}
	
	public static JTextArea addTextArea(JPanel pane, String label, String initialValue, boolean editable, final FieldUpdateListener listener) {
		String text= initialValue == null ? "" : initialValue.replaceAll("\\n", "\n");
		
		JPanel tfPane = new JPanel();
		tfPane.setLayout(new JideBoxLayout(tfPane, JideBoxLayout.LINE_AXIS, 6));
		JLabel tfLabel = new JLabel(label);
		tfPane.add(tfLabel, JideBoxLayout.FIX);
		final JTextArea tfArea = new JTextArea(text);
		tfArea.setEditable(editable);
		tfPane.add(new JScrollPane(tfArea), JideBoxLayout.VARY);
		JButton nullify = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		tfPane.add(nullify, JideBoxLayout.FIX);
		nullify.setEnabled(editable);
		pane.add(tfPane, JideBoxLayout.FIX);
		
		nullify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tfArea.setText("");
				listener.valueChanged(tfArea, null);
			}
		});
		tfArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				listener.valueChanged(tfArea, tfArea.getText().replaceAll("\n", Matcher.quoteReplacement("\n")));
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				listener.valueChanged(tfArea, tfArea.getText().replaceAll("\n", Matcher.quoteReplacement("\n")));
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				listener.valueChanged(tfArea, tfArea.getText().replaceAll("\n", Matcher.quoteReplacement("\n")));
			}
		});
//		tfArea.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				listener.valueChanged(tfArea, tfArea.getText().replaceAll("\n", "\\n"));
//			}
//		});
		return tfArea;
	}

//	public static JSpinner addIntegerField(JPanel pane, String label, Integer initialValue, boolean allowNegatives, boolean editable) {
//		return addIntegerField(pane, label, initialValue, allowNegatives, editable, nullListener);
//	}
	
	public static JSpinner addIntegerField(JPanel pane, String label, Integer initialValue, boolean allowNegatives, boolean editable, final FieldUpdateListener listener) {
		JPanel tfPane = new JPanel();
		tfPane.setLayout(new JideBoxLayout(tfPane, JideBoxLayout.LINE_AXIS, 6));
		JLabel tfLabel = new JLabel(label);
		tfPane.add(tfLabel, JideBoxLayout.FIX);
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(initialValue != null ? initialValue.intValue() : 0, allowNegatives ? Integer.MIN_VALUE : 0, Integer.MAX_VALUE, 1));
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		spinner.setEnabled(editable);
		((DefaultFormatter)((NumberEditor)spinner.getEditor()).getTextField().getFormatter()).setCommitsOnValidEdit(true);
		tfPane.add(spinner, JideBoxLayout.VARY);
		JButton nullify = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		tfPane.add(nullify, JideBoxLayout.FIX);
		nullify.setEnabled(editable);
		pane.add(tfPane, JideBoxLayout.FIX);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				listener.valueChanged(spinner, spinner.getValue());
			}
		});
		nullify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spinner.setValue(0);
				listener.valueChanged(spinner, null);
			}
		});
		return spinner;
	}
	
//	public static JSpinner addDoubleField(JPanel pane, String label, Double initialValue, boolean editable) {
//		return addDoubleField(pane, label, initialValue, editable, nullListener);
//	}
	
	public static JSpinner addDoubleField(JPanel pane, String label, Double initialValue, boolean editable, final FieldUpdateListener listener) {
		JPanel tfPane = new JPanel();
		tfPane.setLayout(new JideBoxLayout(tfPane, JideBoxLayout.LINE_AXIS, 6));
		JLabel tfLabel = new JLabel(label);
		tfPane.add(tfLabel, JideBoxLayout.FIX);
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(initialValue != null ? initialValue.doubleValue() : 0.0d, 0.0d, new Float(Float.MAX_VALUE).doubleValue(), 1.0d));
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		spinner.setEnabled(editable);
		((DefaultFormatter)((NumberEditor)spinner.getEditor()).getTextField().getFormatter()).setCommitsOnValidEdit(true);
		tfPane.add(spinner, JideBoxLayout.VARY);
		JButton nullify = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		tfPane.add(nullify, JideBoxLayout.FIX);
		nullify.setEnabled(editable);
		pane.add(tfPane, JideBoxLayout.FIX);
		pane.add(tfPane, JideBoxLayout.FIX);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				listener.valueChanged(spinner, spinner.getValue());
			}
		});
		nullify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spinner.setValue(0.0d);
				listener.valueChanged(spinner, null);
			}
		});
		return spinner;
	}
	
	public static IntegerBasedCheckBox addIntegerBasedCheckBox(JPanel pane, String label, Integer initialValue, boolean editable) {
		return addIntegerBasedCheckBox(pane, label, initialValue, editable, nullListener);
	}
	
	public static IntegerBasedCheckBox addIntegerBasedCheckBox(JPanel pane, String label, Integer initialValue, boolean editable, final FieldUpdateListener listener) {
		JPanel ibcbPane = new JPanel();
		ibcbPane.setLayout(new BorderLayout());
		final IntegerBasedCheckBox ibcb = new IntegerBasedCheckBox();
		ibcb.setText(label);
		ibcb.setIntegerValue(initialValue);
		ibcb.setEnabled(editable);
		ibcbPane.add(ibcb, BorderLayout.WEST);
		ibcbPane.add(new JPanel(), BorderLayout.CENTER);
		pane.add(ibcbPane, JideBoxLayout.FIX);
		ibcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(ibcb, ibcb.getIntegerValue());
			}
		});
		return ibcb;
	}
	
	public static BooleanBasedCheckBox addBooleanBasedCheckBox(JPanel pane, String label, Boolean initialValue, boolean editable, final FieldUpdateListener listener) {
		JPanel bbcbPane = new JPanel();
		bbcbPane.setLayout(new BorderLayout());
		final BooleanBasedCheckBox bbcb = new BooleanBasedCheckBox();
		bbcb.setText(label);
		bbcb.setBooleanValue(initialValue);
		bbcb.setEnabled(editable);
		bbcbPane.add(bbcb, BorderLayout.WEST);
		bbcbPane.add(new JPanel(), BorderLayout.CENTER);
		pane.add(bbcbPane, JideBoxLayout.FIX);
		bbcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.valueChanged(bbcb, bbcb.isSelected());
			}
		});
		return bbcb;
	}
	
	@SuppressWarnings("rawtypes")
	public static JComboBox addEnumValueBox(JPanel pane, String label, Enum[] values, Enum initialValue, boolean writable) {
		return addEnumValueBox(pane, label, values, initialValue, writable, new FieldUpdateListener() {@Override public void valueChanged(JComponent source, Object value) {}});
	}
	
	@SuppressWarnings("rawtypes")
	public static JComboBox addEnumValueBox(JPanel pane, String label, Enum[] values, Enum initialValue, boolean writable, final FieldUpdateListener listener) {
		JPanel comboPane = new JPanel();
		comboPane.setLayout(new JideBoxLayout(comboPane, JideBoxLayout.LINE_AXIS, 6));
		JLabel comboLabel = new JLabel(label);
		comboPane.add(comboLabel, JideBoxLayout.FIX);
		@SuppressWarnings("unchecked")
		final JComboBox enumValuesCombo = new JComboBox(values);
		enumValuesCombo.setEnabled(writable);
		enumValuesCombo.setSelectedItem(initialValue);
		comboPane.add(enumValuesCombo, JideBoxLayout.VARY);
		enumValuesCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					listener.valueChanged(enumValuesCombo, e.getItem());
				}
			}
		});
		JButton nullify = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		comboPane.add(nullify, JideBoxLayout.FIX);
		nullify.setEnabled(writable);
		nullify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enumValuesCombo.setSelectedItem(null);
				listener.valueChanged(enumValuesCombo, null);
			}
		});
		
		pane.add(comboPane, JideBoxLayout.FIX);
		return enumValuesCombo;
	}

	
	public MyComboBox addNPCBox(JPanel pane, Project proj, String label, NPC npc, boolean writable, FieldUpdateListener listener) {
		final GDEComboModel<NPC> comboModel = new GDEComboModel<NPC>(proj, npc){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public NPC getTypedElementAt(int index) {
				return project.getNPC(index);
			}
			@Override
			public int getSize() {
				return project.getNPCCount()+1;
			}
		};
		return addGDEBox(pane, label, npc, NPC.class, comboModel, writable, listener);
	}
	
	public MyComboBox addActorConditionBox(JPanel pane, Project proj, String label, ActorCondition acond, boolean writable, FieldUpdateListener listener) {
		final GDEComboModel<ActorCondition> comboModel = new GDEComboModel<ActorCondition>(proj, acond){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public ActorCondition getTypedElementAt(int index) {
				return project.getActorCondition(index);
			}
			@Override
			public int getSize() {
				return project.getActorConditionCount()+1;
			}
		};
		return addGDEBox(pane, label, acond, ActorCondition.class, comboModel, writable, listener);
	}
	
	public MyComboBox addItemBox(JPanel pane, Project proj, String label, Item item, boolean writable, FieldUpdateListener listener) {
		final GDEComboModel<Item> comboModel = new GDEComboModel<Item>(proj, item){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public Item getTypedElementAt(int index) {
				return project.getItem(index);
			}
			@Override
			public int getSize() {
				return project.getItemCount()+1;
			}
		};
		return addGDEBox(pane, label, item, Item.class, comboModel, writable, listener);
	}
	
	public MyComboBox addItemCategoryBox(JPanel pane, Project proj, String label, ItemCategory ic, boolean writable, FieldUpdateListener listener) {
		final GDEComboModel<ItemCategory> comboModel = new GDEComboModel<ItemCategory>(proj, ic){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public ItemCategory getTypedElementAt(int index) {
				return project.getItemCategory(index);
			}
			@Override
			public int getSize() {
				return project.getItemCategoryCount()+1;
			}
		};
		return addGDEBox(pane, label, ic, ItemCategory.class, comboModel, writable, listener);
	}

	public MyComboBox addQuestBox(JPanel pane, Project proj, String label, Quest quest, boolean writable, FieldUpdateListener listener) {
		final GDEComboModel<Quest> comboModel = new GDEComboModel<Quest>(proj, quest){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public Quest getTypedElementAt(int index) {
				return project.getQuest(index);
			}
			@Override
			public int getSize() {
				return project.getQuestCount()+1;
			}
		};
		return addGDEBox(pane, label, quest, Quest.class, comboModel, writable, listener);
	}

	public MyComboBox addDroplistBox(JPanel pane, Project proj, String label, Droplist droplist, boolean writable, FieldUpdateListener listener) {
		final GDEComboModel<Droplist> comboModel = new GDEComboModel<Droplist>(proj, droplist){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public Droplist getTypedElementAt(int index) {
				return project.getDroplist(index);
			}
			@Override
			public int getSize() {
				return project.getDroplistCount()+1;
			}
		};
		return addGDEBox(pane, label, droplist, Droplist.class, comboModel, writable, listener);
	}
	
	public MyComboBox addDialogueBox(JPanel pane, Project proj, String label, Dialogue dialogue, boolean writable, final FieldUpdateListener listener) {
		final GDEComboModel<Dialogue> comboModel = new GDEComboModel<Dialogue>(proj, dialogue){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public Dialogue getTypedElementAt(int index) {
				return project.getDialogue(index);
			}
			@Override
			public int getSize() {
				return project.getDialogueCount()+1;
			}
		};
		return addGDEBox(pane, label, dialogue, Dialogue.class, comboModel, writable, listener);
	}

	public MyComboBox addMapBox(JPanel pane, Project proj, String label, TMXMap map, boolean writable, final FieldUpdateListener listener) {
		final GDEComboModel<TMXMap> comboModel = new GDEComboModel<TMXMap>(proj, map){
			private static final long serialVersionUID = 2638082961277241764L;
			@Override
			public TMXMap getTypedElementAt(int index) {
				return project.getMap(index);
			}
			@Override
			public int getSize() {
				return project.getMapCount()+1;
			}
		};
		return addGDEBox(pane, label, map, TMXMap.class, comboModel, writable, listener);
	}
	
	@SuppressWarnings("unchecked")
	public MyComboBox addGDEBox(JPanel pane, String label, GameDataElement gde, final Class<? extends GameDataElement> dataClass, final GDEComboModel<? extends GameDataElement> comboModel, final boolean writable, final FieldUpdateListener listener) {
		JPanel gdePane = new JPanel();
		gdePane.setLayout(new JideBoxLayout(gdePane, JideBoxLayout.LINE_AXIS, 6));
		JLabel gdeLabel = new JLabel(label);
		gdePane.add(gdeLabel, JideBoxLayout.FIX);
		final MyComboBox gdeBox = new MyComboBox(dataClass, comboModel);
		gdeBox.setRenderer(new GDERenderer(false, writable));
		new ComboBoxSearchable(gdeBox){
			@Override
			protected String convertElementToString(Object object) {
				if (object == null) return "none";
				else return ((GameDataElement)object).getDesc();
			}
		};
		gdeBox.setEnabled(writable);
		gdePane.add(gdeBox, JideBoxLayout.VARY);
		final JButton goToGde = new JButton((Icon) ((gde != null) ? new ImageIcon(gde.getIcon()) : (writable ? new ImageIcon(DefaultIcons.getCreateIcon()) : null)));
		goToGde.setEnabled(gde != null || writable);
		goToGde.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameDataElement selected = ((GameDataElement)comboModel.getSelectedItem());
				if (selected != null) {
					ATContentStudio.frame.openEditor(((GameDataElement)comboModel.getSelectedItem()));
					ATContentStudio.frame.selectInTree((GameDataElement)comboModel.getSelectedItem());
				} else if (writable) {
					JSONCreationWizard wizard = new JSONCreationWizard(((GameDataElement)target).getProject(), dataClass);
					wizard.addCreationListener(new JSONCreationWizard.CreationCompletedListener() {
						
						@Override
						public void elementCreated(JSONElement created) {
							gdeBox.setSelectedItem(created);
						}
					});
					wizard.setVisible(true);
				}
			}
		});
		gdePane.add(goToGde, JideBoxLayout.FIX);
		gdeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gdeBox.getModel().getSelectedItem() == null) {
					goToGde.setIcon((writable ? new ImageIcon(DefaultIcons.getCreateIcon()) : null));
					goToGde.setEnabled(writable);
				} else {
					goToGde.setIcon(new ImageIcon(((GameDataElement)comboModel.getSelectedItem()).getIcon()));
					goToGde.setEnabled(true);
				}
				listener.valueChanged(gdeBox, gdeBox.getModel().getSelectedItem());
			}
		});
		JButton nullify = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		gdePane.add(nullify, JideBoxLayout.FIX);
		nullify.setEnabled(writable);
		nullify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gdeBox.setSelectedItem(null);
			}
		});
		pane.add(gdePane, JideBoxLayout.FIX);
		
		return gdeBox;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JList addBacklinksList(JPanel pane, GameDataElement gde) {
		final JList list = new JList(new GDEBacklinksListModel(gde));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ATContentStudio.frame.openEditor((GameDataElement)list.getSelectedValue());
					ATContentStudio.frame.selectInTree((GameDataElement)list.getSelectedValue());
				}
			}
		});
		list.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ATContentStudio.frame.openEditor((GameDataElement)list.getSelectedValue());
					ATContentStudio.frame.selectInTree((GameDataElement)list.getSelectedValue());
				}
			}
		});
		list.setCellRenderer(new GDERenderer(true, false));
		CollapsiblePanel colPane = new CollapsiblePanel("Elements linking to this one");
		colPane.setLayout(new JideBoxLayout(colPane, JideBoxLayout.PAGE_AXIS));
		colPane.add(new JScrollPane(list), JideBoxLayout.FIX);
		colPane.add(new JPanel(), JideBoxLayout.FIX);
		if (gde.getBacklinks() == null || gde.getBacklinks().isEmpty()) {
			colPane.collapse();
		}
		pane.add(colPane, JideBoxLayout.FIX);
		return list;
	}
	
	public static abstract class GDEComboModel<E extends GameDataElement> extends AbstractListModel<E> implements ComboBoxModel<E> {

		private static final long serialVersionUID = -5854574666510314715L;
		
		public Project project;
		public E selected;
		
		public GDEComboModel(Project proj, E initial) {
			this.project = proj;
			this.selected = initial;
		}
		
		@Override
		public abstract int getSize();

		@Override
		public E getElementAt(int index) {
			if (index == 0) {
				return null;
			}
			return getTypedElementAt(index - 1);
		}
		
		public abstract E getTypedElementAt(int index);
		
		@SuppressWarnings("unchecked")
		@Override
		public void setSelectedItem(Object anItem) {
			selected = (E) anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selected;
		}
		
		public void itemAdded(E item, int index) {
			fireIntervalAdded(this, index, index);
		}
		
		public void itemRemoved(E item, int index) {
			fireIntervalRemoved(this, index, index);
		}
		
	}
	
	public static class GDERenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 6819681566800482793L;

		private boolean includeType = false;
		private boolean writable = false;
		
		public GDERenderer(boolean includeType, boolean writable) {
			super();
			this.includeType = includeType;
			this.writable = writable;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value == null) {
				label.setText("None"+(writable ? ". Click on the button to create one." : ""));
			} else {
				if (includeType && ((GameDataElement)value).getDataType() != null) {
					label.setText(((GameDataElement)value).getDataType().toString()+"/"+((GameDataElement)value).getDesc());
				} else {
					label.setText(((GameDataElement)value).getDesc());
				}
				if (((GameDataElement)value).getIcon() == null) {
					Notification.addError("Unable to find icon for "+((GameDataElement)value).getDesc());
				} else {
					label.setIcon(new ImageIcon(((GameDataElement)value).getIcon()));
				}
			}
			return label;
		}
		
	}
	
	public static class GDEBacklinksListModel implements ListModel<GameDataElement> {
		
		GameDataElement source;
		
		public GDEBacklinksListModel(GameDataElement source) {
			super();
			this.source = source;
			source.addBacklinkListener(new GameDataElement.BacklinksListener() {
				@Override
				public void backlinkRemoved(GameDataElement gde) {
					fireListChanged();
				}
				@Override
				public void backlinkAdded(GameDataElement gde) {
					fireListChanged();
				}
			});
		}
		
		@Override
		public int getSize() {
			return source.getBacklinks().size();
		}

		@Override
		public GameDataElement getElementAt(int index) {
			for (GameDataElement gde : source.getBacklinks()) {
				if (index == 0) return gde;
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
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public class MyComboBox extends JComboBox implements ProjectElementListener {
		
		private static final long serialVersionUID = -4184228604170642567L;
		
		Class<? extends GameDataElement> dataType;
		
		public MyComboBox(Class<? extends GameDataElement> dataType, ComboBoxModel model) {
			super(model);
			this.dataType = dataType;
			Editor.this.addElementListener(dataType, this);
		}
		
		@Override
		public void elementAdded(GameDataElement added, int index) {
			((GDEComboModel)getModel()).itemAdded(added, index);
		}

		@Override
		public void elementRemoved(GameDataElement removed, int index) {
			((GDEComboModel)getModel()).itemRemoved(removed, index);
		}
		
		@Override
		public Class<? extends GameDataElement> getDataType() {
			return dataType;
		}
		
	}
	
	public abstract void targetUpdated();
	

	
	transient Map<Class<? extends GameDataElement>, List<ProjectElementListener>> projectElementListeners = new HashMap<Class<? extends GameDataElement>, List<ProjectElementListener>>();
	
	public void addElementListener(Class<? extends GameDataElement> interestingType, ProjectElementListener listener) {
		if (projectElementListeners.get(interestingType) == null) {
			projectElementListeners.put(interestingType, new ArrayList<ProjectElementListener>());
			target.getProject().addElementListener(interestingType, this);
		}
		projectElementListeners.get(interestingType).add(listener);
	}
	
	public void removeElementListener(ProjectElementListener listener) {
		if (listener == null) return;
		if (projectElementListeners.get(listener.getDataType()) != null) {
			projectElementListeners.get(listener.getDataType()).remove(listener);
			if (projectElementListeners.get(listener.getDataType()).isEmpty()) {
				target.getProject().removeElementListener(listener.getDataType(), this);
				projectElementListeners.remove(listener.getDataType());
			}
		}
	}
	
	public void elementAdded(GameDataElement element, int index) {
		if (projectElementListeners.get(element.getClass()) != null) {
			for (ProjectElementListener l : projectElementListeners.get(element.getClass())) {
				l.elementAdded(element, index);
			}
		}
	}

	public void elementRemoved(GameDataElement element, int index) {
		if (projectElementListeners.get(element.getClass()) != null) {
			for (ProjectElementListener l : projectElementListeners.get(element.getClass())) {
				l.elementRemoved(element, index);
			}
		}
	}
	
	public void clearElementListeners() {
		for (Class<? extends GameDataElement> type : projectElementListeners.keySet()) {
			target.getProject().removeElementListener(type, this);
		}
	}
	
	public Class<? extends GameDataElement> getDataType() {
		return null;
	}


	
	
}
