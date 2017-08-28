package com.gpl.rpg.atcontentstudio.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

public class OverlayIcon implements Icon {
	
	private Image background;
	private Image overlay;
	
	public OverlayIcon(Image background, Image overlay) {
		this.background = background;
		this.overlay = overlay;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(background, x, y, null);
		g.drawImage(overlay, x, y, null);
	}

	@Override
	public int getIconWidth() {
		return Math.max(background.getWidth(null), overlay.getWidth(null));
	}

	@Override
	public int getIconHeight() {
		return Math.max(background.getHeight(null), overlay.getHeight(null));
	}

}
