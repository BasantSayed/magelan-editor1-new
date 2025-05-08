/*
 * The Magelan Project - Vector Graphics Library and Editor
 * Copyright (c) 2003-2004, Assen Antov and Larisa Feldman. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * o Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 * 
 * o Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 * 
 * o Neither the name Magelan nor the names of project members and
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Suggestions, comments and fixes should be sent to:
 * aantov@users.sourceforge.net
 * larisa@users.sourceforge.net
 */
package org.magelan.editor.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.*;
import org.magelan.core.editor.*;
import org.magelan.core.entity.*;
import org.magelan.drawing.*;
import org.magelan.editor.*;
import org.magelan.editor.util.EditorUtil;

/**
 * Workspace editor dialog.
 * 
 * @version 2.0, 01/2004
 * @author Assen Antov
 */
public class WorkspaceDialog extends JDialog {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private static final String[] TOOLBAR_STYLES =  new String[] {lang.getString("WorkspaceDialog.toolbar.style.magelan"), lang.getString("WorkspaceDialog.toolbar.style.standard"), lang.getString("WorkspaceDialog.toolbar.style.plain"), lang.getString("WorkspaceDialog.toolbar.style.tabbed")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private static final Locale[] LANG = new Locale[] { Locale.US, new Locale("bg"), new Locale("fr")}; //$NON-NLS-1$ //$NON-NLS-2$
	
	private JTabbedPane tab;
	private JPanel editor, files, app;
	private JButton okButton;
	private JButton cancelButton;
	
	/*
	 * Editor tab controls
	 */
	private DrawingEditor preview;
	private JComboBox handleCombo, bgCombo, selCombo, snapCombo ;
	private JSpinner handleSpinner, sizeSpinner, ddSpinner, sdSpinner;
	
	/*
	 * Application tab controls
	 */
	private JList languages;
	private JComboBox style;
	private JCheckBox autoload, showTips;
	
	
	public WorkspaceDialog() {
		this((Frame) null);
	}
	
	public WorkspaceDialog(Frame parent) {
		super(parent, true);
		setTitle(lang.getString("Workspace_Settings_7")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());
		
		JLabel title = UIFactory.createBanner(lang.getString("WorkspaceDialog.banner"), Icons.WRENCH_ICON); //$NON-NLS-1$
		getContentPane().add(title, BorderLayout.NORTH);
		
		// the tabbed pane
		tab = new JTabbedPane();
		getContentPane().add(tab, BorderLayout.CENTER);
		
		/*
		 * Editor settings
		 */
		editor = constructEditorPanel();
		tab.addTab(lang.getString("WorkspaceDialog.tab.editor"), editor); //$NON-NLS-1$
		tab.setMnemonicAt(0, KeyEvent.VK_E);
		
		/*
		 * Editor app
		 */
		app = constructAppPanel();
		tab.addTab(lang.getString("WorkspaceDialog.tab.application"), app); //$NON-NLS-1$
		tab.setMnemonicAt(1, KeyEvent.VK_A);

		/*
		 * File Export/Import settings
		 */
		//files = constructFilesPanel();
		//tab.addTab("Export/Import", files);
		//tab.setMnemonicAt(2, KeyEvent.VK_X);
		
		/*
		 * Button panel
		 */
		JPanel bp = new JPanel();
		bp.setLayout(new FlowLayout());
		
		// OK button
		okButton = new javax.swing.JButton();
		okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
		okButton.setIcon(Icons.OK_ICON);
		okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePrefs();
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(okButton);
		bp.add(okButton);
		
		// Cancel button
		cancelButton = new javax.swing.JButton();
		cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
		cancelButton.setIcon(Icons.CANCEL_ICON);
		cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		bp.add(cancelButton);
		
		getContentPane().add(bp, BorderLayout.SOUTH);
		
		pack();
		SwingUtil.center(this);
	}
	
	/**
	 * Construct an editor configuration panel.
	 */
	private JPanel constructEditorPanel() {
		JPanel res = new JPanel();
		res.setLayout(new GridLayout(1, 2));
		
		/*
		 * Left panel
		 */
		LabelledItemPanel lp1 = new LabelledItemPanel();
		
		/*
		 * Preview editor
		 */
		preview = new DefaultDrawingEditor();
		DrawingModel d = preview.getModel();
		d.add(new CircleEntity(70, 70, 60));
		d.add(new LineEntity(70, 20, 120, 70));
		d.add(new LineEntity(120, 70, 70, 120));
		d.add(new LineEntity(70, 120, 20, 70));
		d.add(new LineEntity(20, 70, 70, 20));
		d.add(new PointEntity(200, 200));
		EditorUtil.configure(preview);
		
		
		handleCombo = new ColorComboBox();
		bgCombo = new ColorComboBox();
		selCombo = new ColorComboBox();
		snapCombo = new ColorComboBox();

		ComboListener comboListener = new ComboListener();	
		handleCombo.addItemListener(comboListener);
		bgCombo.addItemListener(comboListener);
		selCombo.addItemListener(comboListener);
		snapCombo.addItemListener(comboListener);
		
		handleSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
		sizeSpinner = new JSpinner(new SpinnerNumberModel(11, 5, 20, 1));
		ddSpinner =  new JSpinner(new SpinnerNumberModel(40, 1, 100, 5));
		sdSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 50, 1));
		
		SpinnerListener spinnerListener = new SpinnerListener();
		handleSpinner.addChangeListener(spinnerListener);
		sizeSpinner.addChangeListener(spinnerListener);
		ddSpinner.addChangeListener(spinnerListener);
		sdSpinner.addChangeListener(spinnerListener);
		
		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.editorPreferences"))); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.handleSize"), handleSpinner, false); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.handleColor"), handleCombo); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.backgroundColor"), bgCombo); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.selectionColor"), selCombo); //$NON-NLS-1$

		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.snapMarker"))); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.size"), sizeSpinner, false); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.color"), snapCombo); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.displayDistance"), ddSpinner, false); //$NON-NLS-1$
		lp1.addItem(lang.getString("WorkspaceDialog.label.snapDistance"), sdSpinner, false); //$NON-NLS-1$
		
		res.add(lp1);
		
		LabelledItemPanel lp2 = new LabelledItemPanel();
		
		/*
		lp2.addDivider(new DividerLabel("Snap Points"));
		JPanel points = new JPanel();
		points.setLayout(new GridLayout(2, 2));
		points.add(new JCheckBox("End"));
		points.add(new JCheckBox("Middle"));
		points.add(new JCheckBox("Center"));
		points.add(new JCheckBox("Quadrant"));
		//points.add(new JCheckBox("Secondary"));
		lp2.addItem(points);
		*/
		
		lp2.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.preview"))); //$NON-NLS-1$
		//preview.paint();
		JScrollPane previewScroll = new JScrollPane(preview.getContainer());
		previewScroll.setPreferredSize(new Dimension(150, 150));
		previewScroll.setMinimumSize(new Dimension(150, 150));
		lp2.addItem(previewScroll);
		
		res.add(lp2);
		
		return res;
	}
	
	/**
	 * 
	 */
	private JPanel constructFilesPanel() {
		JPanel res = new JPanel();
		res.setLayout(new GridLayout(1, 2));
		
		LabelledItemPanel lp1 = new LabelledItemPanel();
		
		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.DXF"))); //$NON-NLS-1$
		lp1.addItem(new JCheckBox(lang.getString("WorkspaceDialog.checkbox.preserveAutoCADVariables"))); //$NON-NLS-1$
		
		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.SVG"))); //$NON-NLS-1$
		lp1.addItem(new JCheckBox(lang.getString("WorkspaceDialog.checkbox.preserveBackground"))); //$NON-NLS-1$

		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.EPS"))); //$NON-NLS-1$
		lp1.addItem(new JCheckBox(lang.getString("WorkspaceDialog.checkbox.exactFonts"))); //$NON-NLS-1$
		lp1.addItem(new JCheckBox(lang.getString("WorkspaceDialog.checkbox.noBuffer"))); //$NON-NLS-1$
		
		res.add(lp1);
		
		return res;
	}
	
	/**
	 * 
	 */
	private JPanel constructAppPanel() {
		JPanel resp = new JPanel();
		resp.setLayout(new BorderLayout());
		JPanel res = new JPanel();
		res.setLayout(new GridLayout(1, 2));
		
		LabelledItemPanel lp1 = new LabelledItemPanel();
		
		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.language"))); //$NON-NLS-1$
		languages = new JList(LANG);
		languages.setCellRenderer(new LangRenderer());
		
		JScrollPane langScroll = new JScrollPane(languages);
		langScroll.setPreferredSize(new Dimension(100, 70));
		langScroll.setMinimumSize(new Dimension(100, 70));
		lp1.addItem(langScroll);
		
		lp1.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.toolbarStyle"))); //$NON-NLS-1$
		style = new JComboBox(TOOLBAR_STYLES);
		lp1.addItem(style);
		
		res.add(lp1);
		
		LabelledItemPanel lp2 = new LabelledItemPanel();
		
		autoload = new JCheckBox(lang.getString("WorkspaceDialog.checkbox.autoload")); //$NON-NLS-1$
		lp2.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.extensions"))); //$NON-NLS-1$
		lp2.addItem(autoload);
		
		showTips = new JCheckBox(lang.getString("WorkspaceDialog.checkbox.showTips")); //$NON-NLS-1$
		lp2.addDivider(new DividerLabel(lang.getString("WorkspaceDialog.divider.startup"))); //$NON-NLS-1$
		lp2.addItem(showTips);
		
		res.add(lp2);
		
		resp.add(res, BorderLayout.CENTER);
		
		JLabel warn = new JLabel(lang.getString("WorkspaceDialog.label.warning")); //$NON-NLS-1$
		warn.setIcon(Icons.WARNING_ICON);
		resp.add(warn, BorderLayout.SOUTH);
		
		return resp;
	}
	
	/**
	 * Reads the preferences and sets the proper values to all controls.
	 */
	private void loadPrefs() {
		/*
		 * Editor tab
		 */
		EditorUtil.configure(preview);
		
		handleCombo.setSelectedItem(preview.getValue(DefaultDrawingEditor.KEY_HANDLE_COLOR));
		bgCombo.setSelectedItem(preview.getValue(DefaultDrawingEditor.KEY_BACKGROUND_COLOR));
		selCombo.setSelectedItem(preview.getValue(DefaultDrawingEditor.KEY_SELECTION_COLOR));
		snapCombo.setSelectedItem(preview.getSnap().getColor());
		handleSpinner.setValue(preview.getValue(DefaultDrawingEditor.KEY_HANDLE_SIZE));
		sizeSpinner.setValue(new Integer(preview.getSnap().getSize()));
		ddSpinner.setValue(new Integer(preview.getSnap().getDrawDistance()));
		sdSpinner.setValue(new Integer(preview.getSnap().getSnapDistance()));
		
		/*
		 * Application tab
		 */
		Preferences p = Preferences.userNodeForPackage(SingleFrameEditorManager.class);
		style.setSelectedIndex(p.getInt("toolbar.extension.mode", EditorToolBar.SPLIT)); //$NON-NLS-1$
		
		p = Preferences.userNodeForPackage(Lang.class);
		String lang = p.get(Lang.USER_LANGUAGE, "en"); //$NON-NLS-1$
		for (int i = 0; i < LANG.length; i++) {
			if (LANG[i].getLanguage().equals(lang)) {
				languages.setSelectedIndex(i);
				break;
			}
		}
		
		p = Preferences.userNodeForPackage(DefaultExtensionManager.class);
		autoload.setSelected(p.getBoolean("autoload", false)); //$NON-NLS-1$

		p = Preferences.userNodeForPackage(TipsDialog.class);
		showTips.setSelected(p.getBoolean("tips.show", true)); //$NON-NLS-1$
	}
	
	/**
	 * Saves the preferences.
	 */
	private void savePrefs() {
		/*
		 * Editor tab
		 */
		EditorUtil.storeConfiguration(preview);
		
		DrawingEditor[] e = Editor.getEditorManager().getEditors();
		for (int i = 0; i < e.length; i++) {
			EditorUtil.configure(e[i]);
			e[i].repaint();
		}
		
		/*
		 * Application tab
		 */
		Preferences p = Preferences.userNodeForPackage(SingleFrameEditorManager.class);
		p.putInt("toolbar.extension.mode", style.getSelectedIndex()); //$NON-NLS-1$
		
		p = Preferences.userNodeForPackage(Lang.class);
		Locale sl = (Locale) languages.getSelectedValue();
		p.put(Lang.USER_LANGUAGE, sl.getLanguage());
		p.put(Lang.USER_COUNTRY, sl.getCountry());
		
		p = Preferences.userNodeForPackage(DefaultExtensionManager.class);
		p.putBoolean("autoload", autoload.isSelected()); //$NON-NLS-1$

		p = Preferences.userNodeForPackage(TipsDialog.class);
		p.putBoolean("tips.show", showTips.isSelected()); //$NON-NLS-1$
	}
	
	public void show() {
		loadPrefs();
		super.show();
	}
	
	/**
	 * Language list cell renderer. Shows country icons next
	 * to the language names. 
	 * 
	 * @version	1.0, 01/2004
	 */
	class LangRenderer extends ListRenderer {
		
		LangRenderer() {
			super();
		}
		
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			JLabel res = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			res.setText(((Locale) value).getDisplayName());
			return res;
		}
		
		public Icon getIcon(Object value) {
			Locale l = (Locale) value;
			
			// determine the correct icon
			ImageIcon icon;
			icon = Icons.getImageIcon("flag_" + l.getLanguage() + "_" + l.getCountry()); //$NON-NLS-1$ //$NON-NLS-2$
			if (icon == null) {
				icon = Icons.getImageIcon("flag_" + l.getLanguage()); //$NON-NLS-1$
			}
			if (icon != null) {
				return icon;
			}

			return Icons.GAP_ICON_16;
		}

		public String getToolTipText(Object value) {
			return null;
		}
	}
	
	
	/**
	 * 
	 */
	class ComboListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			Component src = (Component) e.getSource();
			if (src == handleCombo) {
				preview.putValue(DrawingEditor.KEY_HANDLE_COLOR, (Color) e.getItem());
			}
			else if (src == bgCombo) {
				preview.putValue(DrawingEditor.KEY_BACKGROUND_COLOR, (Color) e.getItem());
			}
			else if (src == selCombo) {
				preview.putValue(DrawingEditor.KEY_SELECTION_COLOR, (Color) e.getItem());
			}
			else if (src == snapCombo) {
				preview.getSnap().setColor((Color) e.getItem());
			}
			preview.repaint();
		}
	}
	
	/**
	 * 
	 */
	class SpinnerListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSpinner src = (JSpinner) e.getSource();
			if (src == handleSpinner) {
				preview.putValue(DrawingEditor.KEY_HANDLE_SIZE, (Integer) src.getValue());
			}
			else if (src == sizeSpinner) {
				preview.getSnap().setSize(((Integer) src.getValue()).intValue());
			}
			else if (src == sdSpinner) {
				preview.getSnap().setSnapDistance(((Integer) src.getValue()).intValue());
			}
			else if (src == ddSpinner) {
				preview.getSnap().setDrawDistance(((Integer) src.getValue()).intValue());
			}
			preview.repaint();
		}
	}
	
	public static void main(String[] params) {
		try {
			UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.Plastic3DLookAndFeel"); //$NON-NLS-1$
		} catch (Exception e) {}
		
		JDialog f = new WorkspaceDialog();
		f.setVisible(true);
	}
}