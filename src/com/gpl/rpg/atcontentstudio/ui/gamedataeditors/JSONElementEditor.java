package com.gpl.rpg.atcontentstudio.ui.gamedataeditors;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.GameSource;
import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.SaveEvent;
import com.gpl.rpg.atcontentstudio.model.gamedata.GameDataCategory;
import com.gpl.rpg.atcontentstudio.model.gamedata.JSONElement;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMap;
import com.gpl.rpg.atcontentstudio.model.maps.WorldmapSegment;
import com.gpl.rpg.atcontentstudio.model.sprites.Spritesheet;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.Editor;
import com.gpl.rpg.atcontentstudio.ui.FieldUpdateListener;
import com.gpl.rpg.atcontentstudio.ui.IdChangeImpactWizard;
import com.gpl.rpg.atcontentstudio.ui.SaveItemsWizard;
import com.gpl.rpg.atcontentstudio.ui.sprites.SpriteChooser;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideTabbedPane;

public abstract class JSONElementEditor extends Editor {

	private static final long serialVersionUID = -5889046987755079563L;
	
	
	Map<String, JPanel> editorTabs = new LinkedHashMap<String, JPanel>();
	JideTabbedPane editorTabsHolder;
	RSyntaxTextArea jsonEditorPane;
	
	public JSONElementEditor(JSONElement target, String desc, Image icon) {
		super();
		this.target = target;
		this.name = desc;
		this.icon = new ImageIcon(icon);
		
		setLayout(new BorderLayout());
		editorTabsHolder = new JideTabbedPane(JideTabbedPane.BOTTOM);
		editorTabsHolder.setTabShape(JideTabbedPane.SHAPE_FLAT);
		editorTabsHolder.setUseDefaultShowCloseButtonOnTab(false);
		editorTabsHolder.setShowCloseButtonOnTab(false);
		add(editorTabsHolder, BorderLayout.CENTER);
	}
	
	public void addEditorTab(String id, JPanel editor) {
		JScrollPane scroller = new JScrollPane(editor);
		scroller.getVerticalScrollBar().setUnitIncrement(16);
		editorTabsHolder.addTab(id, scroller);
		editorTabs.put(id, editor);
	}

	public void removeEditorTab(String id) {
		if (id == null) return;
		for (int i =0; i <editorTabsHolder.getTabCount(); i++) {
			if (id.equals(editorTabsHolder.getTitleAt(i))) {
				editorTabsHolder.removeTabAt(i);
				editorTabs.remove(id);
			}
		}
	}
	public JPanel getJSONView() {
		jsonEditorPane = new RSyntaxTextArea();
		jsonEditorPane.setText(((JSONElement)target).toJsonString());
		jsonEditorPane.setEditable(((JSONElement)target).writable);
		jsonEditorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		result.add(jsonEditorPane, BorderLayout.CENTER);
		return result;
	}
	
	public void updateJsonViewText(String text) {
		jsonEditorPane.setText(text);
	}
	
	public JPanel getFormView() {
		JPanel pane = new JPanel();
		pane.setLayout(new JideBoxLayout(pane, JideBoxLayout.PAGE_AXIS, 6));
		if (((JSONElement)target).jsonFile != null) {
			addLabelField(pane, "JSON File: ", ((JSONElement)target).jsonFile.getAbsolutePath());
		}
		
		insertFormViewDataField(pane);
		
		addBacklinksList(pane, (JSONElement) target);

		//Placeholder. Fills the eventual remaining space.
		pane.add(new JPanel(), JideBoxLayout.VARY);
		
		return pane;
	}
	
	public abstract void insertFormViewDataField(JPanel pane);

	
	
	public JButton createButtonPane(JPanel pane, final Project proj, final JSONElement node, final Class<? extends JSONElement> concreteNodeClass, Image icon, final Spritesheet.Category iconCat, final FieldUpdateListener listener) {
		final JButton gdeIcon = new JButton(new ImageIcon(icon));
		JPanel savePane = new JPanel();
		savePane.add(gdeIcon, JideBoxLayout.FIX);
		savePane.setLayout(new JideBoxLayout(savePane, JideBoxLayout.LINE_AXIS, 6));
		if (node.writable) {
			if (iconCat != null) {
				gdeIcon.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						SpriteChooser chooser = SpriteChooser.getChooser(proj, iconCat);
						chooser.setSelectionListener(new SpriteChooser.SelectionListener() {
							@Override
							public void iconSelected(String selected) {
								if (selected != null) {
									listener.valueChanged(gdeIcon, selected);
								}
							}
						});
						chooser.setVisible(true);
					}
				});
			}
			if (node.getDataType() == GameSource.Type.altered) {
				savePane.add(message = new JLabel(ALTERED_MESSAGE), JideBoxLayout.FIX);
			} else if (node.getDataType() == GameSource.Type.created) {
				savePane.add(message = new JLabel(CREATED_MESSAGE), JideBoxLayout.FIX);
			}
			JButton save = new JButton(SAVE);
			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (node.getParent() instanceof GameDataCategory<?>) {
						if (node.state != GameDataElement.State.saved) { 
							final List<SaveEvent> events = node.attemptSave();
							if (events == null) {
								ATContentStudio.frame.nodeChanged(node);
							} else {
								new Thread() {
									@Override
									public void run() {
										new SaveItemsWizard(events, node).setVisible(true);
									}
								}.start();
							}
						}
					}
				}
			});
			savePane.add(save, JideBoxLayout.FIX);
			JButton delete = new JButton(DELETE);
			if (node.getDataType() == GameSource.Type.altered) {
				delete.setText(REVERT);
			}
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ATContentStudio.frame.closeEditor(node);
					node.childrenRemoved(new ArrayList<ProjectTreeNode>());
					if (node.getParent() instanceof GameDataCategory<?>) {
						((GameDataCategory<?>)node.getParent()).remove(node);
						node.save();
						for (GameDataElement backlink : node.getBacklinks()) {
							backlink.elementChanged(node, proj.getGameDataElement(node.getClass(), node.id));
						}
					}
				}
			});
			savePane.add(delete, JideBoxLayout.FIX);
		} else {
			if (proj.alteredContent.gameData.getGameDataElement(concreteNodeClass, node.id) != null) {
				savePane.add(message = new JLabel(ALTERED_EXISTS_MESSAGE), JideBoxLayout.FIX);
				JButton makeWritable = new JButton("Go to altered");
				makeWritable.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (node.getProject().getGameDataElement(concreteNodeClass, node.id) != node) {
							ATContentStudio.frame.openEditor(node.getProject().getGameDataElement(concreteNodeClass, node.id));
							ATContentStudio.frame.closeEditor(node);
							ATContentStudio.frame.selectInTree(node.getProject().getGameDataElement(concreteNodeClass, node.id));
						}
					}
				});
				savePane.add(makeWritable, JideBoxLayout.FIX);

			} else {
				savePane.add(message = new JLabel(READ_ONLY_MESSAGE), JideBoxLayout.FIX);
				JButton makeWritable = new JButton("Alter");
				makeWritable.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (node.getProject().getGameDataElement(concreteNodeClass, node.id) == node) {
							node.getProject().makeWritable(node);
						}
						if (node.getProject().getGameDataElement(concreteNodeClass, node.id) != node) {
							ATContentStudio.frame.openEditor(node.getProject().getGameDataElement(concreteNodeClass, node.id));
							ATContentStudio.frame.closeEditor(node);
							ATContentStudio.frame.selectInTree(node.getProject().getGameDataElement(concreteNodeClass, node.id));
						}
						updateMessage();
					}
				});
				savePane.add(makeWritable, JideBoxLayout.FIX);
			}
		}
		JButton prev = new JButton(new ImageIcon(DefaultIcons.getArrowLeftIcon()));
		JButton next = new JButton(new ImageIcon(DefaultIcons.getArrowRightIcon()));
		savePane.add(prev, JideBoxLayout.FIX);
		savePane.add(next, JideBoxLayout.FIX);
		if (node.getParent().getIndex(node) == 0) {
			prev.setEnabled(false);
		}
		if (node.getParent().getIndex(node) == node.getParent().getChildCount() - 1) {
			next.setEnabled(false);
		}
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectTreeNode prevNode = (ProjectTreeNode) node.getParent().getChildAt(node.getParent().getIndex(node) - 1);
				if (prevNode != null && prevNode instanceof GameDataElement) {
					ATContentStudio.frame.openEditor((GameDataElement) prevNode);
				}
			}
		});
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectTreeNode nextNode = (ProjectTreeNode) node.getParent().getChildAt(node.getParent().getIndex(node) + 1);
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


	@Override
	public void targetUpdated() {
		this.icon = new ImageIcon(((GameDataElement)target).getIcon());
		this.name = ((GameDataElement)target).getDesc();
		updateMessage();
	}
	
	public void updateMessage() {
		
		//TODO make this a full update of the button panel.
		JSONElement node = (JSONElement) target;
		if (node.writable) {
			if (node.getDataType() == GameSource.Type.altered) {
				message.setText(ALTERED_MESSAGE);
			} else if (node.getDataType() == GameSource.Type.created) {
				message.setText(CREATED_MESSAGE);
			}
		} else if (node.getProject().alteredContent.gameData.getGameDataElement(node.getClass(), node.id) != null) {
			message.setText(ALTERED_EXISTS_MESSAGE);
		} else {
			message.setText(READ_ONLY_MESSAGE);
		}
		message.revalidate();
		message.repaint();
	}
	

	public boolean idChanging() {
		JSONElement node = (JSONElement) target;
		List<GameDataElement> toModify = new LinkedList<GameDataElement>();
		List<GameDataElement> toAlter = new LinkedList<GameDataElement>();
		for (GameDataElement element : node.getBacklinks()) {
			GameDataElement activeElement = element;
			if (element instanceof JSONElement) {
				activeElement = node.getProject().getGameDataElement((Class<? extends JSONElement>) element.getClass(), element.id);
			} else if (element instanceof TMXMap) {
				activeElement = node.getProject().getMap(element.id);
			} else if (element instanceof WorldmapSegment) {
				activeElement = node.getProject().getWorldmapSegment(element.id);
			}
			if (activeElement.writable) {
				//No need to alter. Check if we flag a new modification.
				if (!activeElement.needsSaving()) {
					toModify.add(activeElement);
				}
			} else {
				toAlter.add(activeElement);
			}
		}
		if (!(toModify.isEmpty() && toAlter.isEmpty())) {
			IdChangeImpactWizard.Result result = IdChangeImpactWizard.showIdChangeImapctWizard(target, toModify, toAlter);
			if (result == IdChangeImpactWizard.Result.ok) {
				for (GameDataElement element : toModify) {
					element.state = GameDataElement.State.modified;
					element.childrenChanged(new ArrayList<ProjectTreeNode>());
				}
				for (GameDataElement element : toAlter) {
					if (element instanceof JSONElement) {
						node.getProject().makeWritable((JSONElement)element);
					} else if (element instanceof TMXMap) {
						node.getProject().makeWritable((TMXMap)element);
					} else if (element instanceof WorldmapSegment) {
						node.getProject().makeWritable((WorldmapSegment)element);
					}
				}
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
	
	//setText in cancelIdEdit generates to edit events, one replacing the contents with the empty string, and one with the target.id. We want to skip the first one.
	public boolean skipNext = false;
	public void cancelIdEdit(final JTextField idField) {
		Runnable revertField = new Runnable(){
			@Override
			public void run() {
				skipNext = true;
				idField.setText(target.id);
			}
		};
		SwingUtilities.invokeLater(revertField);
	}
}
