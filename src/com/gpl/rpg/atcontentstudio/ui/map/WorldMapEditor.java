package com.gpl.rpg.atcontentstudio.ui.map;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.GameDataElement;
import com.gpl.rpg.atcontentstudio.model.GameSource;
import com.gpl.rpg.atcontentstudio.model.ProjectTreeNode;
import com.gpl.rpg.atcontentstudio.model.SaveEvent;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMap;
import com.gpl.rpg.atcontentstudio.model.maps.Worldmap;
import com.gpl.rpg.atcontentstudio.model.maps.WorldmapSegment;
import com.gpl.rpg.atcontentstudio.model.maps.WorldmapSegment.NamedArea;
import com.gpl.rpg.atcontentstudio.ui.DefaultIcons;
import com.gpl.rpg.atcontentstudio.ui.Editor;
import com.gpl.rpg.atcontentstudio.ui.SaveItemsWizard;
import com.gpl.rpg.atcontentstudio.ui.WorldmapLabelEditionWizard;
import com.jidesoft.swing.ComboBoxSearchable;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideTabbedPane;

public class WorldMapEditor extends Editor {

	private static final long serialVersionUID = -8358238912588729094L;
	
	public EditMode editMode = EditMode.moveViewSelect;
	
	public enum EditMode {
		moveViewSelect,
		moveMaps,
		deleteMaps,
		addMap,
		editLabelCoverage
	}
	
	public String mapBeingAddedID = null;
	public String selectedLabel = null;

	public WorldMapEditor(WorldmapSegment worldmap) {
		target = worldmap;
		this.name = worldmap.id;
		this.icon = new ImageIcon(worldmap.getIcon());
		setLayout(new BorderLayout());
		
		JideTabbedPane editorTabsHolder = new JideTabbedPane(JideTabbedPane.BOTTOM);
		editorTabsHolder.setTabShape(JideTabbedPane.SHAPE_FLAT);
		editorTabsHolder.setUseDefaultShowCloseButtonOnTab(false);
		editorTabsHolder.setShowCloseButtonOnTab(false);
		add(editorTabsHolder, BorderLayout.CENTER);

		editorTabsHolder.add("Map", buildSegmentTab(worldmap));
	}
	
	@Override
	public void targetUpdated() {
		// TODO Auto-generated method stub

	}

	
	private JPanel buildSegmentTab(final WorldmapSegment worldmap) {
		JPanel pane = new JPanel();
		pane.setLayout(new JideBoxLayout(pane,  JideBoxLayout.PAGE_AXIS));

		addLabelField(pane, "Worldmap File: ", ((Worldmap)worldmap.getParent()).worldmapFile.getAbsolutePath());
		pane.add(createButtonPane(worldmap), JideBoxLayout.FIX);
		
		final WorldMapView mapView = new WorldMapView(worldmap);
		JScrollPane mapScroller = new JScrollPane(mapView);
		final JViewport vPort = mapScroller.getViewport();
		
		final JSlider zoomSlider = new JSlider(WorldMapView.MIN_ZOOM, WorldMapView.MAX_ZOOM, (int)(mapView.zoomLevel / WorldMapView.ZOOM_RATIO));
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setMinorTickSpacing(WorldMapView.INC_ZOOM);
		zoomSlider.setOrientation(JSlider.VERTICAL);
		JPanel zoomSliderPane = new JPanel();
		zoomSliderPane.setLayout(new JideBoxLayout(zoomSliderPane, JideBoxLayout.PAGE_AXIS));
		zoomSliderPane.add(zoomSlider, JideBoxLayout.VARY);
		zoomSliderPane.add(new JLabel(new ImageIcon(DefaultIcons.getZoomIcon())), JideBoxLayout.FIX);
		
		final JRadioButton editLabelCoverage = new JRadioButton("Edit label coverage");
		final JButton editLabel = new JButton("Edit map label");
		final JButton createLabel = new JButton("Create map label");
		final JButton deleteLabel = new JButton("Delete map label");
		
		if (target.writable) {
			JPanel mapToolsPane = new JPanel();
			mapToolsPane.setLayout(new JideBoxLayout(mapToolsPane, JideBoxLayout.LINE_AXIS));
			ButtonGroup mapToolsGroup = new ButtonGroup();
			JRadioButton moveView = new JRadioButton("Select maps");
			mapToolsGroup.add(moveView);
			mapToolsPane.add(moveView, JideBoxLayout.FIX);
			JRadioButton moveMaps = new JRadioButton("Move selected map(s)");
			mapToolsGroup.add(moveMaps);
			mapToolsPane.add(moveMaps, JideBoxLayout.FIX);
			JRadioButton deleteMaps = new JRadioButton("Delete maps");
			mapToolsGroup.add(deleteMaps);
			mapToolsPane.add(deleteMaps, JideBoxLayout.FIX);
			JRadioButton addMap = new JRadioButton("Add map");
			mapToolsGroup.add(addMap);
			mapToolsPane.add(addMap, JideBoxLayout.FIX);
			final GDEComboModel<TMXMap> mapComboModel = new GDEComboModel<TMXMap>(worldmap.getProject(), null){
				private static final long serialVersionUID = 2638082961277241764L;
				@Override
				public Object getTypedElementAt(int index) {
					return project.getMap(index);
				}
				@Override
				public int getSize() {
					return project.getMapCount()+1;
				}
			};
			final MyComboBox mapBox = new MyComboBox(TMXMap.class, mapComboModel);
			mapBox.setRenderer(new GDERenderer(false, false));
			new ComboBoxSearchable(mapBox){
				@Override
				protected String convertElementToString(Object object) {
					if (object == null) return "none";
					else return ((GameDataElement)object).getDesc();
				}
			};
			mapBox.setEnabled(false);
			mapToolsPane.add(mapBox, JideBoxLayout.FIX);
			
			mapToolsPane.add(new JPanel(), JideBoxLayout.VARY);
			moveView.setSelected(true);
			pane.add(mapToolsPane, JideBoxLayout.FIX);
			
			JPanel labelToolsPane = new JPanel();
			labelToolsPane.setLayout(new JideBoxLayout(labelToolsPane, JideBoxLayout.LINE_AXIS));
			mapToolsGroup.add(editLabelCoverage);
			editLabelCoverage.setEnabled(false);
			labelToolsPane.add(editLabelCoverage, JideBoxLayout.FIX);
			editLabel.setEnabled(false);
			labelToolsPane.add(editLabel, JideBoxLayout.FIX);
			deleteLabel.setEnabled(false);
			labelToolsPane.add(deleteLabel, JideBoxLayout.FIX);
			createLabel.setEnabled(false);
			labelToolsPane.add(createLabel, JideBoxLayout.FIX);
			
			labelToolsPane.add(new JPanel(), JideBoxLayout.VARY);
			pane.add(labelToolsPane, JideBoxLayout.FIX);
			
			
			moveView.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editMode = EditMode.moveViewSelect;
					mapBox.setEnabled(false);
					if (mapBeingAddedID != null) {
						mapView.mapLocations.remove(mapBeingAddedID);
						mapBeingAddedID = null;
						mapView.revalidate();
						mapView.repaint();
					}
				}
			});

			moveMaps.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editMode = EditMode.moveMaps;
					mapBox.setEnabled(false);
					if (mapBeingAddedID != null) {
						mapView.mapLocations.remove(mapBeingAddedID);
						mapBeingAddedID = null;
						mapView.revalidate();
						mapView.repaint();
					}
				}
			});

			deleteMaps.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editMode = EditMode.deleteMaps;
					mapBox.setEnabled(false);
					if (mapBeingAddedID != null) {
						mapView.mapLocations.remove(mapBeingAddedID);
						mapBeingAddedID = null;
						mapView.revalidate();
						mapView.repaint();
					}
				}
			});
			
			addMap.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editMode = EditMode.addMap;
					mapBox.setEnabled(true);
					if (mapBox.getSelectedItem() != null) {
						mapBeingAddedID = ((TMXMap)mapBox.getSelectedItem()).id;
					}
				}
			});
			
			mapBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (mapBox.getSelectedItem() == null) {
						mapBeingAddedID = null;
					} else {
						if (mapBeingAddedID != null) {
							mapView.updateFromModel();
						}
						mapBeingAddedID = ((TMXMap)mapBox.getSelectedItem()).id;
						if (mapView.mapLocations.isEmpty()) {
							TMXMap map = target.getProject().getMap(mapBeingAddedID);
							int w = map.tmxMap.getWidth() * WorldMapView.TILE_SIZE;
							int h = map.tmxMap.getHeight() * WorldMapView.TILE_SIZE;
							mapView.mapLocations.put(mapBeingAddedID, new Rectangle(0, 0, w, h));
							mapView.recomputeSize();
							mapView.revalidate();
							mapView.repaint();
						}
					}
				}
			});
			
			editLabelCoverage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					editMode = EditMode.editLabelCoverage;
					mapBox.setEnabled(false);
					mapView.selected.clear();
					mapView.selected.addAll(((WorldmapSegment)target).labelledMaps.get(selectedLabel));
					if (mapBeingAddedID != null) {
						mapView.mapLocations.remove(mapBeingAddedID);
						mapBeingAddedID = null;
					}
					mapView.revalidate();
					mapView.repaint();
				}
			});
			
			editLabelCoverage.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						WorldmapSegment map = (WorldmapSegment)target;
						if (map.labelledMaps.get(selectedLabel) != null) {
							map.labelledMaps.get(selectedLabel).clear();
						} else {
							map.labelledMaps.put(selectedLabel, new LinkedList<String>());
						}
						for (String s : mapView.selected) {
							map.labelledMaps.get(selectedLabel).add(s);
						}
						mapView.revalidate();
						mapView.repaint();
					}
				}
			});
			
			editLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mapView.selected.clear();
					mapView.selected.addAll(((WorldmapSegment)target).labelledMaps.get(selectedLabel));
					mapView.revalidate();
					mapView.repaint();
					WorldmapLabelEditionWizard wiz = new WorldmapLabelEditionWizard(worldmap, worldmap.labels.get(selectedLabel));
					wiz.addCreationListener(new WorldmapLabelEditionWizard.CreationCompletedListener() {
						@Override
						public void labelCreated(NamedArea created) {
							if (!created.id.equals(selectedLabel)) {
								worldmap.labelledMaps.put(created.id, worldmap.labelledMaps.get(selectedLabel));
								worldmap.labelledMaps.remove(selectedLabel);
								worldmap.labels.put(created.id, created);
								worldmap.labels.remove(selectedLabel);
								selectedLabel = created.id;
								mapView.revalidate();
								mapView.repaint();
							}
						}
					});
					wiz.setVisible(true);
				}
			});
			
			deleteLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					worldmap.labelledMaps.remove(selectedLabel);
					worldmap.labels.remove(selectedLabel);
					selectedLabel = null;
					mapView.revalidate();
					mapView.repaint();
				}
			});
			
			createLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WorldmapLabelEditionWizard wiz = new WorldmapLabelEditionWizard(worldmap);
					wiz.addCreationListener(new WorldmapLabelEditionWizard.CreationCompletedListener() {
						@Override
						public void labelCreated(NamedArea created) {
							worldmap.labelledMaps.put(created.id, new LinkedList<String>());
							worldmap.labelledMaps.get(created.id).addAll(mapView.selected);
							mapView.revalidate();
							mapView.repaint();
						}
					});
					wiz.setVisible(true);
				}
			});
			
			
			

		}
		
		JPanel mapZoomPane = new JPanel();
		mapZoomPane.setLayout(new BorderLayout());
		mapZoomPane.add(zoomSliderPane, BorderLayout.WEST);
		mapZoomPane.add(mapScroller, BorderLayout.CENTER);
		pane.add(mapZoomPane, JideBoxLayout.VARY);
		
		zoomSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				
				Rectangle view = vPort.getViewRect();
				
				float oldZoomLevel = mapView.zoomLevel;
				mapView.zoomLevel = zoomSlider.getValue() * WorldMapView.ZOOM_RATIO;
				
				int newCenterX = (int) (view.getCenterX() / oldZoomLevel * mapView.zoomLevel);
				int newCenterY = (int) (view.getCenterY() / oldZoomLevel * mapView.zoomLevel);

				view.x = newCenterX - (view.width / 2);
				view.y = newCenterY - (view.height / 2);
				
				mapView.scrollRectToVisible(view);
				mapView.revalidate();
				mapView.repaint();
			}
		});
		
		MouseAdapter mouseListener = new MouseAdapter() {
			final int skipRecomputeDefault = 5;
			Point dragStart = null;
			int skipRecompute = 0;
			
			@Override
			public void mouseClicked(MouseEvent e) {
				String selectedMap = null;
				boolean update = false;
				int x = (int) (e.getX() / mapView.zoomLevel);
				int y = (int) (e.getY() / mapView.zoomLevel);
				for (String s : mapView.mapLocations.keySet()) {
					if (mapView.mapLocations.get(s).contains(x, y)) {
						selectedMap = s;
						break;
					}
				}
				if (editMode == EditMode.moveViewSelect || editMode == EditMode.editLabelCoverage) {
					if (selectedMap == null) return;
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (e.isControlDown() || e.isShiftDown()) {
							if (mapView.selected.contains(selectedMap)) {
								if (editMode != EditMode.editLabelCoverage || mapView.selected.size() > 1) {
									mapView.selected.remove(selectedMap);
									mapSelectionChanged();
									update = true;
								}
							} else {
								mapView.selected.add(selectedMap);
								mapSelectionChanged();
								update = true;
							}
						} else {
							mapView.selected.clear();
							mapView.selected.add(selectedMap);
							mapSelectionChanged();
							update = true;
						}
						if (e.getClickCount() == 2) {
							ATContentStudio.frame.openEditor(worldmap.getProject().getMap(selectedMap));
						}
					}
				} else if (editMode == EditMode.deleteMaps) {
					worldmap.mapLocations.remove(selectedMap);
					worldmap.labels.remove(selectedMap);
					mapView.selected.remove(selectedMap);
					mapSelectionChanged();
					mapView.updateFromModel();
					update = true;
				} else if (editMode == EditMode.addMap && mapBeingAddedID != null) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						mapView.recomputeSize();
						mapView.pushToModel();
					}
					mapView.updateFromModel();
					update = true;
					mapBeingAddedID = null;
				}
				if (update) {
					mapView.revalidate();
					mapView.repaint();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					dragStart = e.getLocationOnScreen();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				dragStart = null;
				if (editMode == EditMode.moveMaps) {
					mapView.pushToModel();
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if (editMode == EditMode.addMap && mapBeingAddedID != null) {
					int x = (int) (e.getX() / mapView.zoomLevel);
					int y = (int) (e.getY() / mapView.zoomLevel);
					TMXMap map = target.getProject().getMap(mapBeingAddedID);
					int w = map.tmxMap.getWidth() * WorldMapView.TILE_SIZE;
					int h = map.tmxMap.getHeight() * WorldMapView.TILE_SIZE;
					x -= w / 2;
					x -= x % WorldMapView.TILE_SIZE;
					y -= h / 2;
					y -= y % WorldMapView.TILE_SIZE;
					mapView.mapLocations.put(mapBeingAddedID, new Rectangle(x, y, w, h));
					if (--skipRecompute <= 0) {
						if (mapView.recomputeSize()) {
							skipRecompute = skipRecomputeDefault;
						}
					}
					mapView.revalidate();
					mapView.repaint();
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (dragStart == null) return;
				int deltaX = e.getXOnScreen() - dragStart.x;
				int deltaY = e.getYOnScreen() - dragStart.y;

				if (editMode != EditMode.moveMaps) {
					Rectangle view = vPort.getViewRect();
					view.setLocation(view.x - deltaX, view.y - deltaY);
					mapView.scrollRectToVisible(view);

					dragStart = e.getLocationOnScreen();
				} else {
					int mapDeltaX = (int) ((deltaX / mapView.zoomLevel));
					int mapDeltaY = (int) ((deltaY / mapView.zoomLevel));
					
					mapDeltaX -= mapDeltaX % WorldMapView.TILE_SIZE;
					mapDeltaY -= mapDeltaY % WorldMapView.TILE_SIZE;
					
					for (String s : mapView.selected) {
						mapView.mapLocations.get(s).x = (worldmap.mapLocations.get(s).x * WorldMapView.TILE_SIZE) + mapDeltaX;
						mapView.mapLocations.get(s).y = (worldmap.mapLocations.get(s).y * WorldMapView.TILE_SIZE) + mapDeltaY;
					}

					if (--skipRecompute <= 0) {
						if (mapView.recomputeSize()) {
							skipRecompute = skipRecomputeDefault;
						}
					}

					mapView.revalidate();
					mapView.repaint();
				}
			}
			
			public void mapSelectionChanged() {
				if (mapView.selected.isEmpty()) {
					editLabelCoverage.setEnabled(false);
					editLabel.setEnabled(false);
					createLabel.setEnabled(false);
					selectedLabel = null;
				} else {
					String label = null;
					boolean multiLabel = false;
					for (String map : mapView.selected) {
						for (String existingLabel : ((WorldmapSegment)target).labelledMaps.keySet()) {
							if (((WorldmapSegment)target).labelledMaps.get(existingLabel).contains(map)) {
								if (label != null && !label.equals(existingLabel)) {
									multiLabel = true;
								}
								label = existingLabel;
							}
						}
					}
					if (multiLabel) {
						editLabelCoverage.setEnabled(false);
						editLabel.setEnabled(false);
						createLabel.setEnabled(false);
						deleteLabel.setEnabled(false);
						selectedLabel = null;
					} else if (label != null) {
						editLabelCoverage.setEnabled(true);
						editLabel.setEnabled(true);
						deleteLabel.setEnabled(true);
						createLabel.setEnabled(false);
						selectedLabel = label;
					} else {
						editLabelCoverage.setEnabled(false);
						editLabel.setEnabled(false);
						deleteLabel.setEnabled(false);
						createLabel.setEnabled(true);
						selectedLabel = null;
					}
				}
			}
		};

		mapView.addMouseListener(mouseListener);
		mapView.addMouseMotionListener(mouseListener);
		
		return pane;
	}
	
	public JPanel createButtonPane(final WorldmapSegment node) {
		final JButton gdeIcon = new JButton(new ImageIcon(DefaultIcons.getUIMapImage()));
		JPanel savePane = new JPanel();
		savePane.add(gdeIcon, JideBoxLayout.FIX);
		savePane.setLayout(new JideBoxLayout(savePane, JideBoxLayout.LINE_AXIS, 6));
		if (node.writable) {
			if (node.getDataType() == GameSource.Type.altered) {
				savePane.add(message = new JLabel(ALTERED_MESSAGE), JideBoxLayout.FIX);
			} else if (node.getDataType() == GameSource.Type.created) {
				savePane.add(message = new JLabel(CREATED_MESSAGE), JideBoxLayout.FIX);
			}
			JButton save = new JButton(SAVE);
			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (node.getParent() instanceof Worldmap) {
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
					if (node.getParent() instanceof Worldmap) {
						((Worldmap)node.getParent()).remove(node);
						node.save();
						for (GameDataElement backlink : node.getBacklinks()) {
							backlink.elementChanged(node, node.getProject().getWorldmapSegment(node.id));
						}
					}
				}
			});
			savePane.add(delete, JideBoxLayout.FIX);
		} else {
			if (node.getProject().alteredContent.getWorldmapSegment(node.id) != null) {
				savePane.add(message = new JLabel(ALTERED_EXISTS_MESSAGE), JideBoxLayout.FIX);
				JButton makeWritable = new JButton("Go to altered");
				makeWritable.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (node.getProject().getWorldmapSegment(node.id) != node) {
							ATContentStudio.frame.openEditor(node.getProject().getWorldmapSegment(node.id));
							ATContentStudio.frame.closeEditor(node);
							ATContentStudio.frame.selectInTree(node.getProject().getWorldmapSegment(node.id));
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
						if (node.getProject().getWorldmapSegment(node.id) == node) {
							node.getProject().makeWritable(node);
						}
						if (node.getProject().getWorldmapSegment(node.id) != node) {
							ATContentStudio.frame.openEditor(node.getProject().getWorldmapSegment(node.id));
							ATContentStudio.frame.closeEditor(node);
							ATContentStudio.frame.selectInTree(node.getProject().getWorldmapSegment(node.id));
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
		return savePane;
	}
	
	public void updateMessage() {
		
		//TODO make this a full update of the button panel.
		WorldmapSegment node = (WorldmapSegment) target;
		if (node.writable) {
			if (node.getDataType() == GameSource.Type.altered) {
				message.setText(ALTERED_MESSAGE);
			} else if (node.getDataType() == GameSource.Type.created) {
				message.setText(CREATED_MESSAGE);
			}
		} else if (node.getProject().alteredContent.getWorldmapSegment(node.id) != null) {
			message.setText(ALTERED_EXISTS_MESSAGE);
		} else {
			message.setText(READ_ONLY_MESSAGE);
		}
		message.revalidate();
		message.repaint();
	}
	
}
