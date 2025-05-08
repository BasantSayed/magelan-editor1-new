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
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.table.*;

import org.magelan.commons.*;
import org.magelan.commons.ui.*;
import org.magelan.editor.*;
import org.apache.log4j.Logger;

/**
 * 
 * @version	1.0, 07/2003
 * @author	Assen Antov
 */
public class ExtensionsDialog extends javax.swing.JDialog implements 
	java.awt.event.ActionListener,
	javax.swing.event.ListSelectionListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Logger log = Logger.getLogger(TipsDialog.class);
	private static Preferences prefs = Preferences.userNodeForPackage(DefaultExtensionManager.class);
	
	private ManagerTable table;
	private ManifestEditor mfEditor;
	
	private ConfigDialog cfgDialog;
	
	

	/**
	 * Constructor for ExtensionsDialog.
	 */
	public ExtensionsDialog() {
		super(Editor.getEditorManager().getMainFrame(), true);
		
		IEditorManager m = Editor.getEditorManager();
		
		consructGUI();
		pack();
		org.magelan.commons.ui.SwingUtil.center(this);
		
		getRootPane().setDefaultButton(okButton);
	//	panel1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Extensions"));

		// javax.swing.JTable
		table = new ManagerTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setRowHeight(18);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	//	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// set the table model
		TableModel dataModel = new AbstractTableModel() {
			public int getColumnCount() { return 2; }
			public int getRowCount() {
				IEditorManager m = Editor.getEditorManager();
				return Editor.getExtensionsManager().getExtensions().size();
			}
			public Object getValueAt(int row, int col) {
				IEditorManager m = Editor.getEditorManager();
				return Editor.getExtensionsManager().getExtensions().get(row);
			}
			public String getColumnName(int column) {
				switch (column) {
					case 0: return lang.getString("Name_6"); //$NON-NLS-1$
					case 1: return lang.getString("Info_7"); //$NON-NLS-1$
				}
				return "";	// Shouldn't happen! //$NON-NLS-1$
			}
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			public void setValueAt(Object aValue, int row, int column) {
			
			}
		};
		table.setModel(dataModel);

		// selection change listener
		table.getSelectionModel().addListSelectionListener(this);
		
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(120);
		tcm.getColumn(1).setPreferredWidth(200);
		
		scroll.setViewportView(table);
		
		//table.addActionListener(this);
		configButton.addActionListener(this);
		mfButton.addActionListener(this);
		newMfButton.addActionListener(this);
		helpButton.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		scanButton.addActionListener(this);
	}
	
	/**
	 * Reload data.
	 */
	public void refresh() {
		String manifests = prefs.get("manifests", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (manifests != null && !("".equals(manifests))) { //$NON-NLS-1$
			mfPanel.setListData(ClassManifest.getClassPath(manifests));
		}
		pathPanel.setListData(ClassManifest.getClassPath(prefs.get("path", ""))); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Reloads the data each time the dialog is shown.
	 */
	public void setVisible(boolean v) {
		if (v) refresh();
		super.setVisible(v);
	}
	
	/**
	 * Reloads the data each time the dialog is shown.
	 */
	public void show() {
		refresh();
		super.show();
	}
	
	/**
	 * Listener to show status of extensions.
	 */
	public void valueChanged(javax.swing.event.ListSelectionEvent e) {
		AbstractEditorExtension ext = (AbstractEditorExtension) Editor.getExtensionsManager().getExtensions().get(table.getSelectedRow());
		
		Icon icon = (Icon) ext.getValue(AbstractEditorExtension.SMALL_ICON);
		if (icon == null) icon = Icons.GAP_ICON_16;
		info.setIcon(icon);
		info.setText(
			lang.getString("Group___12") + (String) ext.getValue(AbstractEditorExtension.GROUP) + "; " + //$NON-NLS-1$ //$NON-NLS-2$
			lang.getString("Version___14") + (String) ext.getValue(AbstractEditorExtension.VERSION) + "; " + //$NON-NLS-1$ //$NON-NLS-2$
			lang.getString("Author___16") + (String) ext.getValue(AbstractEditorExtension.AUTHOR)); //$NON-NLS-1$
		
		configButton.setEnabled(ext instanceof AdjustableEditorExtension);
	}
	
	class ManagerTable extends JTable {
		
		ManagerRenderer renderer = new ManagerRenderer();
		public TableCellRenderer getCellRenderer(int row, int column) {
			return renderer;
		}
	}
	
	/**
	 * 
	 * Custom table cell renderer.
	 */
	class ManagerRenderer extends DefaultTableCellRenderer {
		
		public ManagerRenderer() { super(); }
		
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
	
			if (isSelected) {
				super.setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			}
			else {
				super.setForeground(table.getForeground());
				super.setBackground(table.getBackground());
			}
			
			//setFont(table.getFont());
		
			if (hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); //$NON-NLS-1$
				if (table.isCellEditable(row, column)) {
					super.setForeground(UIManager.getColor("Table.focusCellForeground")); //$NON-NLS-1$
					super.setBackground(UIManager.getColor("Table.focusCellBackground")); //$NON-NLS-1$
				}
			} else {
				setBorder(noFocusBorder);
			}
			
			// CUSTOM CODE
			setIcon(null);
			Font f = table.getFont();
			
			AbstractEditorExtension ext = (AbstractEditorExtension) value;
			switch (column) {
				case 0:
					Icon icon = (Icon) ext.getValue(AbstractEditorExtension.SMALL_ICON);
					if (icon == null) icon = Icons.GAP_ICON_16;
					setIcon(icon);
					setText((String) ext.getValue(AbstractEditorExtension.NAME));
					setFont(new Font(f.getName(), Font.BOLD /*+ Font.ITALIC*/,
							 f.getSize()));
					setIconTextGap(4);
					break;
				case 1: 
					setText((String) ext.getValue(AbstractEditorExtension.LONG_DESCRIPTION)); 
					setFont(new Font(f.getName(), Font.PLAIN,
							 f.getSize()));
					break;
			}
			setToolTipText(getText());

			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) && ( back.equals(table.getBackground()) ) && table.isOpaque();
			setOpaque(!colorMatch);
			// ---- end optimization to aviod painting background ----
		
			return this;
		}
	}
	
	
	private static FilenameFilter MF_NAME_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			File f = new File(name);
			if (f.isDirectory()) return false;
			if (name.toLowerCase().endsWith(".mf") || name.toLowerCase().endsWith(".manifest")) { //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			return false;
		}
	};
	
	/**
			Listens for <code>ActionEvent</code>s.
			<p>
			@param	e	action event
	*/
	public void actionPerformed(java.awt.event.ActionEvent e) {
		String src = ((java.awt.Component) e.getSource()).getName();

		if ("configButton".equals(src)) { //$NON-NLS-1$
			if (cfgDialog == null) {
				cfgDialog = new ConfigDialog(Editor.getEditorManager().getMainFrame());
			}
			cfgDialog.setExtension((AbstractEditorExtension) Editor.getExtensionsManager().getExtensions().get(table.getSelectedRow()));
			cfgDialog.setVisible(true);
		}
		
		/*
		 * Scans the paths for manifest files
		 */
		if ("scanButton".equals(src)) { //$NON-NLS-1$
			java.util.List mfs = mfPanel.getListData();
			java.util.Iterator i = pathPanel.getListData().iterator();
			String mfNames = ""; //$NON-NLS-1$
			while (i.hasNext()) {
				try {
					File dir = new File((String) i.next());
					File[] files = dir.listFiles(MF_NAME_FILTER);
					if (files != null) {
						for (int j = 0; j < files.length; j++) {
							if (!mfs.contains(files[j].getPath())) {
								mfs.add(files[j].getPath());
								mfNames += files[j].getPath() + "\n"; //$NON-NLS-1$
							}
						}
					}
				} catch (Exception ex) {}
			}
			
			ErrorDialog.showInfoDialog(lang.getString("ExtensionsDialog.info.scan") + mfNames); //$NON-NLS-1$
			
			//mfPanel.getList().revalidate();
			mfPanel.setListData(mfs);
			tab.setSelectedIndex(1);
		}
		
		if ("helpButton".equals(src)) { //$NON-NLS-1$
		}
		if ("cancelButton".equals(src)) { //$NON-NLS-1$
			setVisible(false);
		}
		if ("okButton".equals(src)) { //$NON-NLS-1$
			String sep = System.getProperty("path.separator"); //$NON-NLS-1$
						
			/*
			 * Save manifests
			 */
			String str = ""; //$NON-NLS-1$
			java.util.Iterator iter = mfPanel.getListData().iterator();
			while (iter.hasNext()) {
				str += (String) iter.next() + (iter.hasNext()? sep : ""); //$NON-NLS-1$
			}
			prefs.put("manifests", str); //$NON-NLS-1$
			
			/*
			 * Save manifest path
			 */
			str = ""; //$NON-NLS-1$
			iter = pathPanel.getListData().iterator();
			while (iter.hasNext()) {
				str += (String) iter.next() + (iter.hasNext()? sep : ""); //$NON-NLS-1$
			}
			prefs.put("path", str); //$NON-NLS-1$
			
			try {
				prefs.flush();
			} catch (BackingStoreException bse) {
				log.error("Could not save preferences", bse); //$NON-NLS-1$
			}
			
			setVisible(false);
			
			// TEST CODE --> RESTART
			/*
			Editor.getInstance().getManager().exit();
			Editor.getInstance().shutdown();
			Editor.createSingleFrameInstance();
			Editor.getInstance().start();
			*/
		}
		
		if ("mfButton".equals(src)) { //$NON-NLS-1$
			if (mfEditor == null) {
				mfEditor = new ManifestEditor();
			}
			
			String mf = (String) mfPanel.getList().getSelectedValue();
			if (mf != null && !("".equals(mf))) { //$NON-NLS-1$
				mfEditor.setManifest(mf);
				SwingUtilities.updateComponentTreeUI(mfEditor);
				mfEditor.setVisible(true);
			}
		}
		if ("newMfButton".equals(src)) { //$NON-NLS-1$
			if (mfEditor == null) {
				mfEditor = new ManifestEditor();
			}
			
			mfEditor.setManifest(new ClassManifest());
			mfEditor.setVisible(true);

		}
	}

	/**
			Centers the component on screen.
			<p>
			@param	c	the component to center
	*/
	public void center(Component c) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		c.setLocation(screenSize.width/2-c.getSize().width/2, screenSize.height/2-c.getSize().height/2);
	}
	
	public static void main(String[] args) {
		System.setProperty("ef.manifest.path", ".;\\assen\\whatever"); //$NON-NLS-1$ //$NON-NLS-2$
		Vector e = new Vector();
		e.add(new org.magelan.editor.extension.NewLine());
		e.add(new org.magelan.editor.extension.NewCircle());
		(new ExtensionsDialog()).show();
	}




	public void consructGUI() {

		// javax.swing.JDialog
		setName("ManageExtensions"); //$NON-NLS-1$
		setBounds(240, 125, 520, 350);
		setTitle(lang.getString("ExtensionsDialog.title")); //$NON-NLS-1$
		setResizable(true);
		setModal(true);
		getContentPane().setLayout(new FlowLayout());
		setDefaultCloseOperation(1);
			
			getContentPane().setLayout(new BorderLayout());
			
			// banner
			JLabel title = org.magelan.commons.ui.UIFactory.createBanner(lang.getString("Manage_Extensions_40"), Icons.GEAR_ICON); //$NON-NLS-1$
			getContentPane().add(title, BorderLayout.NORTH);
		
			// tab pane
			tab = new JTabbedPane();
			getContentPane().add(tab, BorderLayout.CENTER);
			
			
			// javax.swing.JPanel
			panel1 = new javax.swing.JPanel();
			panel1.setName("panel1"); //$NON-NLS-1$
			panel1.setPreferredSize(new Dimension(455, 210));
			tab.addTab(lang.getString("Extensions_42")/*, Icons.GEAR_ICON*/, panel1); //$NON-NLS-1$ //$NON-NLS-2$
			tab.setToolTipTextAt(0, lang.getString("Configure_loaded_extensions_44")); //$NON-NLS-1$
			tab.setMnemonicAt(0, KeyEvent.VK_X);

				panel1.setLayout(new BorderLayout());

				// javax.swing.JScrollPane
				scroll = new javax.swing.JScrollPane();
				scroll.setName("scroll"); //$NON-NLS-1$
				scroll.setPreferredSize(new Dimension(340, 300));
				panel1.add(scroll, BorderLayout.CENTER);

				// javax.swing.JPanel
				panel2 = new javax.swing.JPanel();
				panel2.setName("panel2"); //$NON-NLS-1$
				//panel2.setPreferredSize(new Dimension(100, 300));
				panel1.add(panel2, BorderLayout.EAST);

					// javax.swing.JButton
					configButton = new javax.swing.JButton();
					configButton.setText(lang.getString("Configure_47")); //$NON-NLS-1$
					configButton.setName("configButton"); //$NON-NLS-1$
					//configButton.setPreferredSize(new Dimension(90, 27));
					configButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
					configButton.setIcon(Icons.WRENCH_ICON);
					configButton.setEnabled(false);
					panel2.add(configButton);
					
				info = new JLabel(""); //$NON-NLS-1$
				Font f = info.getFont();
				info.setFont(new Font(f.getName(), Font.PLAIN, f.getSize()-1));
				info.setIconTextGap(4);
				info.setIcon(Icons.GAP_ICON_16);
				panel1.add(info, BorderLayout.SOUTH);
			
			/*
			 * Manifest management panel
			 */
			mfPanel = new AddRemovePanel();
			mfPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
			mfPanel.getList().setCellRenderer(new MfRenderer());
			
				JPanel gap = new JPanel();
				gap.setPreferredSize(new Dimension(80, 5));
				mfPanel.addOption(gap);
		
				mfButton = new javax.swing.JButton();
				mfButton.setText(lang.getString("Edit_50")); //$NON-NLS-1$
				//mfButton.setMargin(new Insets(2, 4, 2, 6));
				mfButton.setName("mfButton"); //$NON-NLS-1$
				//mfButton.setPreferredSize(new Dimension(81, 27));
				mfButton.setMnemonic(java.awt.event.KeyEvent.VK_E);
				mfButton.setIcon(Icons.MF_ICON);
				mfPanel.addOption(mfButton);
				
				newMfButton = new javax.swing.JButton();
				newMfButton.setText(lang.getString("New_52")); //$NON-NLS-1$
				//newMfButton.setMargin(new Insets(2, 4, 2, 6));
				newMfButton.setName("newMfButton"); //$NON-NLS-1$
				//newMfButton.setPreferredSize(new Dimension(81, 27));
				newMfButton.setMnemonic(java.awt.event.KeyEvent.VK_N);
				newMfButton.setIcon(Icons.MF_ICON);
				mfPanel.addOption(newMfButton);
				
			tab.addTab(lang.getString("Manifests_54")/*, Icons.MF_ICON*/, mfPanel); //$NON-NLS-1$
			tab.setToolTipTextAt(1, lang.getString("Manage_manifest_files_55")); //$NON-NLS-1$
			tab.setMnemonicAt(1, KeyEvent.VK_F);
			
			
			/*
			 * Manifests path panel
			 */
			pathPanel = new ClasspathPanel();
			//pathPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));
			
				scanButton = new javax.swing.JButton();
				scanButton.setText(lang.getString("Scan_56")); //$NON-NLS-1$
				//scanButton.setMargin(new Insets(2, 4, 2, 6));
				scanButton.setName("scanButton"); //$NON-NLS-1$
				//scanButton.setPreferredSize(new Dimension(81, 27));
				scanButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
				scanButton.setIcon(Icons.PATH_ICON);
				pathPanel.addOption(scanButton);
				
			tab.addTab(lang.getString("Paths_58")/*, Icons.PATH_ICON*/, pathPanel); //$NON-NLS-1$
			tab.setToolTipTextAt(2, lang.getString("Paths_where_manifest_files_are_located_59")); //$NON-NLS-1$
			tab.setMnemonicAt(2, KeyEvent.VK_P);
			
		javax.swing.JPanel panel3 = new javax.swing.JPanel();
		getContentPane().add(panel3, BorderLayout.SOUTH);
		
		// VFOKButton
		okButton = new javax.swing.JButton();
		okButton.setText(lang.getString("OK_60")); //$NON-NLS-1$
		okButton.setActionCommand("OK"); //$NON-NLS-1$
		okButton.setIcon(Icons.OK_ICON); //$NON-NLS-1$
		okButton.setName("okButton"); //$NON-NLS-1$
		okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
		panel3.add(okButton);
		
		// JButton
		cancelButton = new javax.swing.JButton();
		cancelButton.setText(lang.getString("Cancel_64")); //$NON-NLS-1$
		cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
		cancelButton.setIcon(Icons.CANCEL_ICON); //$NON-NLS-1$
		cancelButton.setName("cancelButton"); //$NON-NLS-1$
		cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
		panel3.add(cancelButton);
		
		// VFHelpButton
		helpButton = new javax.swing.JButton();
		helpButton.setText(lang.getString("Help_68")); //$NON-NLS-1$
		helpButton.setActionCommand("Help"); //$NON-NLS-1$
		helpButton.setIcon(Icons.HELP_ICON);
		helpButton.setName("helpButton"); //$NON-NLS-1$
		helpButton.setMnemonic(java.awt.event.KeyEvent.VK_H);
		//panel3.add(helpButton);
	}

	// variables
	private javax.swing.JPanel component5;
	private javax.swing.JPanel panel1;
	private javax.swing.JScrollPane scroll;
	private javax.swing.JPanel panel2;
	private javax.swing.JButton configButton;
	private javax.swing.JButton mfButton;
	private javax.swing.JButton newMfButton;
	private javax.swing.JPanel component13;
	private javax.swing.JButton helpButton;
	private javax.swing.JButton okButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton scanButton;
	
	private JTabbedPane tab;
	private JLabel info;
	private ClasspathPanel pathPanel;
	private AddRemovePanel mfPanel;
	
	/**
	 * List cell renderer for manifests.
	 * <p>
	 * @version	2.0, 12/2003
	 */
	class MfRenderer extends ListRenderer {
		
		private File testFile;
		
		MfRenderer() {
			super();
		}
		
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			/* 
			 * Done to avoid creation of 2 file instances
			 */
			testFile = new java.io.File(value.toString());
			
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
		
		public Icon getIcon(Object value) {
			if (testFile.exists()) {
				if (value.toString().toLowerCase().endsWith(".mf") || //$NON-NLS-1$
					value.toString().toLowerCase().endsWith(".manifest")) { //$NON-NLS-1$
					return Icons.MF_ICON;
				}
				else {
					return Icons.WARNING_ICON;
				}
			}
			else {
				return Icons.ERROR_ICON;
			}
		}

		public String getToolTipText(Object value) {
			if (testFile.exists()) {
				return testFile.getAbsolutePath();
			}
			else {
				return lang.getString("MfRenderer.pathNotFound"); //$NON-NLS-1$
			}
		}
	}

}
