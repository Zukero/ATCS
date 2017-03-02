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
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist;
import com.gpl.rpg.atcontentstudio.model.gamedata.Droplist.DroppedItem;
import com.gpl.rpg.atcontentstudio.model.gamedata.Item;
import com.gpl.rpg.atcontentstudio.ui.CollapsiblePanel;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.jidesoft.swing.JideBoxLayout;

public class DroplistEditor extends JSONElementEditor {

	private static final long serialVersionUID = 1139455254096811058L;
	
	private static final String form_view_id = "Form";
	private static final String json_view_id = "JSON";
	
	private Droplist.DroppedItem selectedItem;
	
	private JTextField idField;
	private MyComboBox itemCombo;
	private DroppedItemsListModel droppedItemsListModel;
	private JSpinner qtyMinField;
	private JSpinner qtyMaxField;
	private JSpinner chanceField;
	
	public DroplistEditor(Droplist droplist) {
		super(droplist, droplist.getDesc(), droplist.getIcon());
		addEditorTab(form_view_id, getFormView());
		addEditorTab(json_view_id, getJSONView());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void insertFormViewDataField(JPanel pane) {
		
		final Droplist droplist = (Droplist)target;
		final FieldUpdateListener listener = new DroplistFieldUpdater();
		
		createButtonPane(pane, droplist.getProject(), droplist, Droplist.class, Droplist.getImage(), null, listener);
		
		idField = addTextField(pane, "Droplist ID: ", droplist.id, droplist.writable, listener);
		
		CollapsiblePanel itemsPane = new CollapsiblePanel("Items in this droplist: ");
		itemsPane.setLayout(new JideBoxLayout(itemsPane, JideBoxLayout.PAGE_AXIS));
		droppedItemsListModel = new DroppedItemsListModel(droplist);
		final JList itemsList = new JList(droppedItemsListModel);
		itemsList.setCellRenderer(new DroppedItemsCellRenderer());
		itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemsPane.add(new JScrollPane(itemsList), JideBoxLayout.FIX);
		final JPanel droppedItemsEditorPane = new JPanel();
		final JButton createDroppedItem = new JButton(new ImageIcon(DefaultIcons.getCreateIcon()));
		final JButton deleteDroppedItem = new JButton(new ImageIcon(DefaultIcons.getNullifyIcon()));
		deleteDroppedItem.setEnabled(false);
		itemsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedItem = (Droplist.DroppedItem) itemsList.getSelectedValue();
				if (selectedItem == null) {
					deleteDroppedItem.setEnabled(false);
				} else {
					deleteDroppedItem.setEnabled(true);
				}
				updateDroppedItemsEditorPane(droppedItemsEditorPane, selectedItem, listener);
			}
		});
		if (droplist.writable) {
			JPanel listButtonsPane = new JPanel();
			listButtonsPane.setLayout(new JideBoxLayout(listButtonsPane, JideBoxLayout.LINE_AXIS, 6));
			createDroppedItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Droplist.DroppedItem tempItem = new Droplist.DroppedItem();
					droppedItemsListModel.addItem(tempItem);
					itemsList.setSelectedValue(tempItem, true);
					listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
				}
			});
			deleteDroppedItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedItem != null) {
						droppedItemsListModel.removeItem(selectedItem);
						selectedItem = null;
						itemsList.clearSelection();
						listener.valueChanged(new JLabel(), null); //Item changed, but we took care of it, just do the usual notification and JSON update stuff.
					}
				}
			});
			
			listButtonsPane.add(createDroppedItem, JideBoxLayout.FIX);
			listButtonsPane.add(deleteDroppedItem, JideBoxLayout.FIX);
			listButtonsPane.add(new JPanel(), JideBoxLayout.VARY);
			itemsPane.add(listButtonsPane, JideBoxLayout.FIX);
		}
		droppedItemsEditorPane.setLayout(new JideBoxLayout(droppedItemsEditorPane, JideBoxLayout.PAGE_AXIS));
		itemsPane.add(droppedItemsEditorPane, JideBoxLayout.FIX);
		if (droplist.dropped_items == null || droplist.dropped_items.isEmpty()) {
			itemsPane.collapse();
		}

		pane.add(itemsPane, JideBoxLayout.FIX);
		
	}
	
	public void updateDroppedItemsEditorPane(JPanel pane, DroppedItem di, FieldUpdateListener listener) {
		boolean writable = ((Droplist)target).writable;
		Project proj = ((Droplist)target).getProject();
		pane.removeAll();
		if (itemCombo != null) {
			removeElementListener(itemCombo);
		}
		if (di != null) {
			itemCombo = addItemBox(pane, proj, "Item: ", di.item, writable, listener);
			qtyMinField = addIntegerField(pane, "Quantity min: ", di.quantity_min, false, writable, listener);
			qtyMaxField = addIntegerField(pane, "Quantity max: ", di.quantity_max, false, writable, listener);
			chanceField = addDoubleField(pane, "Chance: ", di.chance, writable, listener);
		}
		pane.revalidate();
		pane.repaint();
	}
	
	public class DroppedItemsListModel implements ListModel<Droplist.DroppedItem> {
		
		Droplist source;
		
		public DroppedItemsListModel(Droplist droplist) {
			this.source = droplist;
		}

		@Override
		public int getSize() {
			if (source.dropped_items == null) return 0;
			return source.dropped_items.size();
		}
		
		@Override
		public Droplist.DroppedItem getElementAt(int index) {
			if (source.dropped_items == null) return null;
			return source.dropped_items.get(index);
		}
		
		public void addItem(Droplist.DroppedItem item) {
			if (source.dropped_items == null) {
				source.dropped_items = new ArrayList<Droplist.DroppedItem>();
			}
			source.dropped_items.add(item);
			int index = source.dropped_items.indexOf(item);
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
			}
		}
		
		public void removeItem(Droplist.DroppedItem item) {
			int index = source.dropped_items.indexOf(item);
			source.dropped_items.remove(item);
			if (source.dropped_items.isEmpty()) {
				source.dropped_items = null;
			}
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
			}
		}

		public void itemChanged(Droplist.DroppedItem item) {
			int index = source.dropped_items.indexOf(item);
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
	
	public static class DroppedItemsCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 7987880146189575234L;

		@Override
		public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				JLabel label = ((JLabel)c);
				Droplist.DroppedItem di = (Droplist.DroppedItem)value;
				if (di.item != null) {
					label.setIcon(new ImageIcon(di.item.getIcon()));
					label.setText(di.chance+"% to get "+di.quantity_min+"-"+di.quantity_max+" "+di.item.getDesc());
				} else if (!isNull(di)) {
					label.setText(di.chance+"% to get "+di.quantity_min+"-"+di.quantity_max+" "+di.item_id);
				} else {
					label.setText("New, undefined, dropped item.");
				}
			}
			return c;
		}
		
		public boolean isNull(Droplist.DroppedItem item) {
			return ((item == null) || (
					item.item == null &&
					item.item_id == null &&
					item.quantity_min == null &&
					item.quantity_max == null &&
					item.chance == null
					)); 
		}
	}
	
	
	public class DroplistFieldUpdater implements FieldUpdateListener {
		@Override
		public void valueChanged(JComponent source, Object value) {
			Droplist droplist = ((Droplist)target);
			if (source == idField) {
				droplist.id = (String) value;
				DroplistEditor.this.name = droplist.getDesc();
				droplist.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(DroplistEditor.this);
			} else if (source == itemCombo) {
				if (selectedItem.item != null) {
					selectedItem.item.removeBacklink(droplist);
				}
				selectedItem.item = (Item) value;
				if (selectedItem.item != null) {
					selectedItem.item_id = selectedItem.item.id;
					selectedItem.item.addBacklink(droplist);
				} else {
					selectedItem.item_id = null;
				}
				droppedItemsListModel.itemChanged(selectedItem);
			} else if (source == qtyMinField) {
				selectedItem.quantity_min = (Integer) value;
				droppedItemsListModel.itemChanged(selectedItem);
			} else if (source == qtyMaxField) {
				selectedItem.quantity_max = (Integer) value;
				droppedItemsListModel.itemChanged(selectedItem);
			} else if (source == chanceField) {
				selectedItem.chance = (Double) value;
				droppedItemsListModel.itemChanged(selectedItem);
			}

			if (droplist.state != GameDataElement.State.modified) {
				droplist.state = GameDataElement.State.modified;
				DroplistEditor.this.name = droplist.getDesc();
				droplist.childrenChanged(new ArrayList<ProjectTreeNode>());
				ATContentStudio.frame.editorChanged(DroplistEditor.this);
			}
			updateJsonViewText(droplist.toJsonString());
		}
	}
}
