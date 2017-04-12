package com.gpl.rpg.atcontentstudio.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.gpl.rpg.atcontentstudio.ATContentStudio;
import com.gpl.rpg.atcontentstudio.model.WorkspaceSettings;
import com.jidesoft.swing.JideBoxLayout;

public class WorkspaceSettingsEditor extends JDialog {

	private static final long serialVersionUID = -1326158719217162879L;
	
	WorkspaceSettings settings;
	
	JRadioButton useSystemDefaultMapEditorButton, useCustomMapEditorButton;
	JTextField mapEditorCommandField;
	
	JRadioButton useSystemDefaultImageViewerButton, useSystemDefaultImageEditorButton, useCustomImageEditorButton;
	JTextField imageEditorCommandField;
	
	JCheckBox translatorModeBox;
	JComboBox<String> translatorLanguagesBox; 
	JCheckBox useInternetBox;
	
	
	
	public WorkspaceSettingsEditor(WorkspaceSettings settings) {
		super(ATContentStudio.frame, "Workspace settings", true);
		setIconImage(DefaultIcons.getMainIconImage());
		
		this.settings = settings;
		
		JPanel pane = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(pane), BorderLayout.CENTER);
		pane.setLayout(new JideBoxLayout(pane, JideBoxLayout.PAGE_AXIS));
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new JideBoxLayout(buttonPane, JideBoxLayout.LINE_AXIS));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		
		pane.add(getExternalToolsPane(), JideBoxLayout.FIX);
		pane.add(getTranslatorModePane(), JideBoxLayout.FIX);
		pane.add(new JPanel(), JideBoxLayout.VARY);

		buttonPane.add(new JPanel(), JideBoxLayout.VARY);
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pushToModel();
				dispose();
			}
		});
		buttonPane.add(ok, JideBoxLayout.FIX);
		JButton reset = new JButton("Reset to defaults");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetDefaults();
			}
		});
		buttonPane.add(reset, JideBoxLayout.FIX);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPane.add(cancel, JideBoxLayout.FIX);
		
		loadFromModel();
		pack();
		setVisible(true);
		
	}
	
	public JPanel getExternalToolsPane() {
		CollapsiblePanel pane = new CollapsiblePanel("External tools");
		pane.setLayout(new JideBoxLayout(pane, JideBoxLayout.PAGE_AXIS));
		
		//Tiled
		CollapsiblePanel tiledPane = new CollapsiblePanel("TMX Map viewer/editor");
		tiledPane.setLayout(new JideBoxLayout(tiledPane, JideBoxLayout.PAGE_AXIS));
		ButtonGroup tiledRadioGroup = new ButtonGroup();
		useSystemDefaultMapEditorButton = new JRadioButton("Use system-default TMX Map editor");
		tiledRadioGroup.add(useSystemDefaultMapEditorButton);
		tiledPane.add(useSystemDefaultMapEditorButton, JideBoxLayout.FIX);
		useCustomMapEditorButton = new JRadioButton("Use custom command to open TMX Map files");
		tiledRadioGroup.add(useCustomMapEditorButton);
		tiledPane.add(useCustomMapEditorButton, JideBoxLayout.FIX);
		mapEditorCommandField = new JTextField();
		tiledPane.add(mapEditorCommandField, JideBoxLayout.FIX);
		ActionListener tiledRadioListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useSystemDefaultMapEditorButton.equals(e.getSource())) {
					mapEditorCommandField.setEnabled(false);
				} else if (useCustomMapEditorButton.equals(e.getSource())) {
					mapEditorCommandField.setEnabled(true);
				}
			}
		};
		useSystemDefaultMapEditorButton.addActionListener(tiledRadioListener);
		useCustomMapEditorButton.addActionListener(tiledRadioListener);
		pane.add(tiledPane, JideBoxLayout.FIX);
		
		//Images
		CollapsiblePanel imgPane = new CollapsiblePanel("Image viewer/editor");
		imgPane.setLayout(new JideBoxLayout(imgPane, JideBoxLayout.PAGE_AXIS));
		ButtonGroup imgRadioGroup = new ButtonGroup();
		useSystemDefaultImageViewerButton = new JRadioButton("Use system-default image viewer");
		imgRadioGroup.add(useSystemDefaultImageViewerButton);
		imgPane.add(useSystemDefaultImageViewerButton, JideBoxLayout.FIX);
		useSystemDefaultImageEditorButton = new JRadioButton("Use system-default image editor");
		imgRadioGroup.add(useSystemDefaultImageEditorButton);
		imgPane.add(useSystemDefaultImageEditorButton, JideBoxLayout.FIX);
		useCustomImageEditorButton = new JRadioButton("Use custom command to open images");
		imgRadioGroup.add(useCustomImageEditorButton);
		imgPane.add(useCustomImageEditorButton, JideBoxLayout.FIX);
		imageEditorCommandField = new JTextField();
		imgPane.add(imageEditorCommandField, JideBoxLayout.FIX);
		ActionListener imgRadioListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useSystemDefaultMapEditorButton.equals(e.getSource())) {
					imageEditorCommandField.setEnabled(false);
				} else if (useSystemDefaultImageViewerButton.equals(e.getSource())) {
					imageEditorCommandField.setEnabled(false);
				} else if (useCustomImageEditorButton.equals(e.getSource())) {
					imageEditorCommandField.setEnabled(true);
				}
			}
		};
		useSystemDefaultImageViewerButton.addActionListener(imgRadioListener);
		useSystemDefaultImageEditorButton.addActionListener(imgRadioListener);
		useCustomImageEditorButton.addActionListener(imgRadioListener);
		pane.add(imgPane, JideBoxLayout.FIX);
		
		pane.expand();
		return pane;
	}
	
	public JPanel getTranslatorModePane() {
		CollapsiblePanel pane = new CollapsiblePanel("Translator options");
		pane.setLayout(new JideBoxLayout(pane, JideBoxLayout.PAGE_AXIS));
		
		translatorModeBox = new JCheckBox("Activate translator mode");
		pane.add(translatorModeBox, JideBoxLayout.FIX);
		
		JPanel langPane = new JPanel();
		langPane.setLayout(new JideBoxLayout(langPane, JideBoxLayout.LINE_AXIS));
		langPane.add(new JLabel("Language code: "), JideBoxLayout.FIX);
		translatorLanguagesBox = new JComboBox<String>(WorkspaceSettings.LANGUAGE_LIST);
		langPane.add(translatorLanguagesBox);
		pane.add(langPane, JideBoxLayout.FIX);

		pane.add(new JLabel("If your language isn't here, complain on the forums at https://andorstrail.com/"), JideBoxLayout.FIX);
		
		useInternetBox = new JCheckBox("Allow connecting to internet to retrieve data from weblate.");
		pane.add(useInternetBox, JideBoxLayout.FIX);
		
		translatorModeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				translatorLanguagesBox.setEnabled(translatorModeBox.isSelected());
				useInternetBox.setEnabled(translatorModeBox.isSelected());
			}
		});
		
		
		return pane;
	}
	
	public void loadFromModel() {
		//Tiled
		useSystemDefaultMapEditorButton.setSelected(settings.useSystemDefaultMapEditor.getCurrentValue());
		useCustomMapEditorButton.setSelected(!settings.useSystemDefaultMapEditor.getCurrentValue());
		mapEditorCommandField.setText(settings.mapEditorCommand.getCurrentValue());
		//Images
		useSystemDefaultImageViewerButton.setSelected(settings.useSystemDefaultImageViewer.getCurrentValue());
		useSystemDefaultImageEditorButton.setSelected(settings.useSystemDefaultImageEditor.getCurrentValue());
		useCustomImageEditorButton.setSelected(!(settings.useSystemDefaultImageViewer.getCurrentValue() || settings.useSystemDefaultImageEditor.getCurrentValue()));
		imageEditorCommandField.setText(settings.imageEditorCommand.getCurrentValue());
		//Translator
		if (settings.translatorLanguage.getCurrentValue() != null) {
			translatorModeBox.setSelected(true);
			translatorLanguagesBox.setSelectedItem(settings.translatorLanguage.getCurrentValue());
			translatorLanguagesBox.setEnabled(true);
			useInternetBox.setEnabled(true);
		} else {
			translatorModeBox.setSelected(false);
			translatorLanguagesBox.setSelectedItem(null);
			translatorLanguagesBox.setEnabled(false);
			useInternetBox.setEnabled(false);
		}
		useInternetBox.setSelected(settings.useInternet.getCurrentValue());
	}
	
	public void pushToModel() {
		//Tiled
		settings.useSystemDefaultMapEditor.setCurrentValue(useSystemDefaultMapEditorButton.isSelected());
		settings.mapEditorCommand.setCurrentValue(mapEditorCommandField.getText());
		//Images
		settings.useSystemDefaultImageViewer.setCurrentValue(useSystemDefaultImageViewerButton.isSelected());
		settings.useSystemDefaultImageEditor.setCurrentValue(useSystemDefaultImageEditorButton.isSelected());
		settings.imageEditorCommand.setCurrentValue(imageEditorCommandField.getText());
		//Translator
		if (translatorModeBox.isSelected()) {
			settings.translatorLanguage.setCurrentValue((String)translatorLanguagesBox.getSelectedItem());
		} else {
			settings.translatorLanguage.resetDefault();
		}
		settings.useInternet.setCurrentValue(useInternetBox.isSelected());
		settings.save();
	}
	
	public void resetDefaults() {
		settings.resetDefault();
		settings.save();
		loadFromModel();
	}

}
