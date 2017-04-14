package com.gpl.rpg.atcontentstudio;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import prefuse.data.expression.parser.ExpressionParser;

import com.gpl.rpg.atcontentstudio.model.Workspace;
import com.gpl.rpg.atcontentstudio.ui.StudioFrame;
import com.gpl.rpg.atcontentstudio.ui.WorkerDialog;
import com.gpl.rpg.atcontentstudio.ui.WorkspaceSelector;


public class ATContentStudio {

	public static final String APP_NAME = "Andor's Trail Content Studio";
	public static final String APP_VERSION = "v0.6.1";

	public static boolean STARTED = false;
	public static StudioFrame frame = null;

	//Need to keep a strong reference to it, to avoid garbage collection that'll reset these loggers.
	public static final List<Logger> configuredLoggers = new LinkedList<Logger>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ConfigCache.init();
		
		try {
			String laf = ConfigCache.getFavoriteLaFClassName();
			if (laf == null) laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(laf);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		//Need to keep a strong reference to it, to avoid garbage collection that'll reset this setting.
		Logger l = Logger.getLogger(ExpressionParser.class.getName());
		l.setLevel(Level.OFF);
		configuredLoggers.add(l); 
		
		final WorkspaceSelector wsSelect = new WorkspaceSelector();
		wsSelect.pack();
		Dimension sdim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension wdim = wsSelect.getSize();
		wsSelect.setLocation((sdim.width - wdim.width)/2, (sdim.height - wdim.height)/2);
		wsSelect.setVisible(true);
		
		wsSelect.addWindowListener(new WindowAdapter() {
			@Override
			public synchronized void windowClosed(WindowEvent e) {
				if (wsSelect.selected != null && !STARTED) {
					ATContentStudio.STARTED = true;
					final File workspaceRoot = new File(wsSelect.selected);
					WorkerDialog.showTaskMessage("Loading your workspace...", null, new Runnable(){
						public void run() {
							Workspace.setActive(workspaceRoot);
							frame = new StudioFrame(APP_NAME+" "+APP_VERSION);
							frame.setVisible(true);
							frame.setDefaultCloseOperation(StudioFrame.DO_NOTHING_ON_CLOSE);
						};
					});
					for (File f : ConfigCache.getKnownWorkspaces()) {
						if (workspaceRoot.equals(f)) {
							if (!workspaceRoot.equals(ConfigCache.getLatestWorkspace())) {
								ConfigCache.setLatestWorkspace(f);
							}
							return;
						}
					}
					ConfigCache.addWorkspace(workspaceRoot);
					ConfigCache.setLatestWorkspace(workspaceRoot);

				}
			}
		});
	}
	
}
