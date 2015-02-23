package com.gpl.rpg.atcontentstudio.ui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.Scrollable;

import tiled.view.MapRenderer;
import tiled.view.OrthogonalRenderer;

import com.gpl.rpg.atcontentstudio.model.Project;
import com.gpl.rpg.atcontentstudio.model.maps.TMXMap;
import com.gpl.rpg.atcontentstudio.model.maps.WorldmapSegment;

public class WorldMapView extends JComponent implements Scrollable {

	private static final long serialVersionUID = -4111374378777093799L;
	
	public static final int TILE_SIZE = 32;
	public static final int MIN_ZOOM = 5;
	public static final int MAX_ZOOM = 250;
	public static final int INC_ZOOM = 5;
	public static final float ZOOM_RATIO = 0.01f;
	
	WorldmapSegment worldmap;
	Project proj;
	
	
	public Map<String, Rectangle> mapLocations = new HashMap<String, Rectangle>();
	
	public Set<String> selected = new HashSet<String>();
	
	public float zoomLevel = 0.1f;
	int sizeX = 0, sizeY = 0;
	int offsetX = 0, offsetY = 0;
	
	public WorldMapView(WorldmapSegment worldmap) {
		this.worldmap = worldmap;
		this.proj = worldmap.getProject();
		updateFromModel();
	}
	
	private void paintOnGraphics(Graphics2D g2) {
		g2.setPaint(new Color(100, 100, 100));
        g2.fillRect(0, 0, sizeX, sizeY);
        
        g2.setPaint(new Color(255, 0, 0));
        g2.setStroke(new BasicStroke(4));

        for (String s : mapLocations.keySet()) {

        	int x = mapLocations.get(s).x;
        	int y = mapLocations.get(s).y;
        	
        	g2.translate(x, y);
        	
        	
        	TMXMap map = proj.getMap(s);
        	if (map == null) continue;
        	MapRenderer renderer = new OrthogonalRenderer(map.tmxMap);
        	
        	// Draw each tile map layer
        	for (tiled.core.MapLayer layer : ((TMXMap)map).tmxMap) {
        		if (layer instanceof tiled.core.TileLayer && layer.isVisible()) {
        			if (layer.getName().equalsIgnoreCase("walkable")) continue;
        			renderer.paintTileLayer(g2, (tiled.core.TileLayer) layer);
        		} else if (layer instanceof tiled.core.ObjectGroup && layer.isVisible()) {
//        			paintObjectGroup(g2, map, (tiled.core.ObjectGroup) layer);
        		}
        	}
        	if (selected.contains(s)) {
        		g2.drawRect(0, 0, map.tmxMap.getWidth() * TILE_SIZE, map.tmxMap.getHeight() * TILE_SIZE);
        	}
        	
        	g2.translate(-x, -y);
        	
        }
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g.create();
		try {
			g2.scale(zoomLevel, zoomLevel);
//			g2.drawImage(img, 0, 0, null);
			paintOnGraphics(g2);

		} finally {
			g2.dispose();
		}
		
	}
	
//	private boolean paintObjectGroup(Graphics2D g2d, TMXMap map, tiled.core.ObjectGroup layer) {
//    	boolean paintSelected = false;
//    	for (MapObjectGroup group : map.groups) {
//			if (group.tmxGroup == layer) {
//				for (MapObject object : group.mapObjects) {
//					drawObject(object, g2d, new Color(20, 20, 190));
//				}
//				break;
//			}
//		}
//    	return paintSelected;
//	}
//
//	private void drawObject(MapObject object, Graphics2D g2d, Color color) {
//		g2d.setPaint(color);
//		g2d.drawRect(object.x+1, object.y+1, object.w-3, object.h-3);
//		g2d.drawRect(object.x+2, object.y+2, object.w-5, object.h-5);
//		g2d.setPaint(color.darker().darker());
//		g2d.drawLine(object.x, object.y + object.h - 1, object.x + object.w - 1, object.y + object.h - 1);
//		g2d.drawLine(object.x + object.w - 1, object.y, object.x + object.w - 1, object.y + object.h - 1);
//		g2d.drawLine(object.x + 3, object.y + 3, object.x + object.w - 4, object.y + 3);
//		g2d.drawLine(object.x + 3, object.y + 3, object.x + 3, object.y + object.h - 4);
//		g2d.setPaint(color.brighter().brighter().brighter());
//		g2d.drawLine(object.x, object.y, object.x + object.w - 1, object.y);
//		g2d.drawLine(object.x, object.y, object.x, object.y + object.h - 1);
//		g2d.drawLine(object.x + 3, object.y + object.h - 4, object.x + object.w - 4, object.y + object.h - 4);
//		g2d.drawLine(object.x + object.w - 4, object.y + 3, object.x + object.w - 4, object.y + object.h - 4);
//		Image img = object.getIcon();
//		g2d.setColor(new Color(255, 255, 255, 120));
//		g2d.fillRect(object.x + 2, object.y + 2, img.getWidth(null), img.getHeight(null));
//		g2d.drawImage(object.getIcon(), object.x + 2, object.y + 2, null);
//	}

	 @Override
	public Dimension getPreferredSize() {
		return new Dimension((int)(zoomLevel * sizeX), (int)(zoomLevel * sizeY));
	}
	 
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return TILE_SIZE;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 4 * TILE_SIZE;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean recomputeSize() {
		sizeX = sizeY = 0;
		boolean originMoved = false;
		int minX = Integer.MAX_VALUE, minY= Integer.MAX_VALUE;
		for (String s : mapLocations.keySet()) {
			int x = mapLocations.get(s).x;
			int w = proj.getMap(s).tmxMap.getWidth() * TILE_SIZE;
			int y = mapLocations.get(s).y;
			int h = proj.getMap(s).tmxMap.getHeight() * TILE_SIZE;
			
			sizeX = Math.max(sizeX, x + w);
			sizeY = Math.max(sizeY, y + h);
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
		}
		
		if (minX != 0) {
			for (String s : mapLocations.keySet()) {
				mapLocations.get(s).x -= minX;
			}
			sizeX -= minX;
			offsetX += minX;
			originMoved = true;
		}
		
		if (minY != 0) {
			for (String s : mapLocations.keySet()) {
				mapLocations.get(s).y -= minY;
			}
			sizeY -= minY;
			offsetY += minY;
			originMoved = true;
		}
		return originMoved;
	}
	
	public void updateFromModel() {
		mapLocations.clear();
		sizeX = sizeY = 0;
		offsetX = worldmap.segmentX * TILE_SIZE;
		offsetY = worldmap.segmentY * TILE_SIZE;
		for (String s : worldmap.mapLocations.keySet()) {
			int x = worldmap.mapLocations.get(s).x * TILE_SIZE;
			int w = proj.getMap(s).tmxMap.getWidth() * TILE_SIZE;
			int y = worldmap.mapLocations.get(s).y * TILE_SIZE;
			int h = proj.getMap(s).tmxMap.getHeight() * TILE_SIZE;
			
			sizeX = Math.max(sizeX, x + w);
			sizeY = Math.max(sizeY, y + h);
			
			mapLocations.put(s, new Rectangle(x, y, w, h));
		}
	}
	
	public void pushToModel() {
		worldmap.segmentX = offsetX / TILE_SIZE;
		worldmap.segmentY = offsetY / TILE_SIZE;
		worldmap.mapLocations.clear();
		for (String s : mapLocations.keySet()) {
			int x = mapLocations.get(s).x / TILE_SIZE;
			int y = mapLocations.get(s).y / TILE_SIZE;
			
			worldmap.mapLocations.put(s, new Point(x, y));
		}
		
		List<String> toRemove = new ArrayList<String>();
		for (String s : worldmap.labelLocations.keySet()) {
			if (!mapLocations.containsKey(s)) {
				toRemove.add(s);
			}
		}
		for (String s : toRemove) {
			worldmap.labelLocations.remove(s);
		}
	}
	
}
