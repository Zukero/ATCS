package com.gpl.rpg.atcontentstudio.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.gpl.rpg.atcontentstudio.Notification;
import com.gpl.rpg.atcontentstudio.NotificationListener;


@SuppressWarnings("rawtypes")
public class NotificationsPane extends JList {

	private static final long serialVersionUID = -1100364214372392608L;

	public static final Map<Notification.Type, Icon> icons = new LinkedHashMap<Notification.Type, Icon>(Notification.Type.values().length);
	
	static {
		icons.put(Notification.Type.SUCCESS, new ImageIcon(DefaultIcons.getStatusGreenIcon()));
		icons.put(Notification.Type.INFO, new ImageIcon(DefaultIcons.getStatusBlueIcon()));
		icons.put(Notification.Type.WARN, new ImageIcon(DefaultIcons.getStatusOrangeIcon()));
		icons.put(Notification.Type.ERROR, new ImageIcon(DefaultIcons.getStatusRedIcon()));
	}
	
	
	@SuppressWarnings("unchecked")
	public NotificationsPane() {
		super();
		MyListModel model = new MyListModel();
		setModel(model);
		setCellRenderer(new ListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel();
				Font f = label.getFont();
				label.setIcon(NotificationsPane.icons.get(((Notification)value).type));
				label.setText(((Notification)value).text);
				if (isSelected) {
//					label.setBackground(Color.RED);
					label.setBorder(BorderFactory.createLineBorder(Color.BLUE));
//					label.setForeground(Color.WHITE);
				}
				f = f.deriveFont(10f);
				label.setFont(f);
				return label;
			}
		});
		Notification.addNotificationListener(model);
	}
	
	
	private class MyListModel implements ListModel, NotificationListener {
		
		@Override
		public Object getElementAt(int index) {
			return Notification.notifs.get(index);
		}
		
		@Override
		public int getSize() {
			return Notification.notifs.size();
		}
		
		@Override
		public void onNewNotification(Notification n) {
			for (ListDataListener l : listeners) {
				l.intervalAdded(new ListDataEvent(NotificationsPane.this, ListDataEvent.INTERVAL_ADDED, Notification.notifs.size() - 1 , Notification.notifs.size() - 1));
			}
			NotificationsPane.this.ensureIndexIsVisible(Notification.notifs.indexOf(n));
		}
		
		@Override
		public void onListCleared(int i) {
			for (ListDataListener l : listeners) {
				l.intervalRemoved(new ListDataEvent(NotificationsPane.this, ListDataEvent.INTERVAL_REMOVED, 0 , i));
			}
		}
		
		private List<ListDataListener> listeners = new CopyOnWriteArrayList<ListDataListener>();
		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}
		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}
		
	}
}