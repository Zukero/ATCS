package com.gpl.rpg.atcontentstudio;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gpl.rpg.atcontentstudio.model.Workspace;
import com.gpl.rpg.atcontentstudio.ui.StudioFrame;
import com.gpl.rpg.atcontentstudio.ui.WorkerDialog;
import com.gpl.rpg.atcontentstudio.ui.WorkspaceSelector;


public class ATContentStudio {

	public static final String APP_NAME = "Andor's Trail Content Studio";
	public static final String APP_VERSION = "v0.3.4.dev";

	public static boolean STARTED = false;
	public static StudioFrame frame = null;
	
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
							frame.setDefaultCloseOperation(StudioFrame.EXIT_ON_CLOSE);
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
