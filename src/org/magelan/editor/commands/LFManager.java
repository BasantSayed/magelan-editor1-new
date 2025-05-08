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
package org.magelan.editor.commands;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.*;
import org.magelan.editor.*;
import org.magelan.editor.util.EditorUtil;


/**
 *
 * @author Assen Antov
 * @version 1.0, 07/2003
 */
public class LFManager extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private LFDialog lf;
	public static final String LF_FILE = "lf.properties"; //$NON-NLS-1$
	
	public LFManager() {
		super();
		putValue(Action.NAME, lang.getString("common.laf")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("LFManager.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("LFManager.descr.long")); //$NON-NLS-1$

		putValue(Action.SMALL_ICON, Icons.LF_ICON);
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		putValue(Action.ACCELERATOR_KEY, null);
	}

	public void run(org.magelan.core.editor.DrawingEditor e) {
		// get the active manager
		IEditorManager m = Editor.getEditorManager();
		
		if (lf == null) {
			lf = new LFDialog(m.getMainFrame());
		}
		SwingUtilities.updateComponentTreeUI(lf);
		lf.setVisible(true);
	}
	
	
	/**
	 * Look and Feel Manager Dialog.
	 */
	class LFDialog extends JDialog {

		private AddRemovePanel panel;
		private JButton okButton;
		private JButton cancelButton;
		
		LFDialog() {
			super((Frame) null, true);
		}
		
		LFDialog(final Frame parent) {
			super(parent, true);
			setTitle(lang.getString("LFManager.title")); //$NON-NLS-1$
			setBounds(0, 0, 450, 300);
			SwingUtil.center(this);
			getContentPane().setLayout(new BorderLayout());
			
			JLabel title = UIFactory.createBanner(lang.getString("LFManager.banner"), Icons.LF_ICON); //$NON-NLS-1$
			getContentPane().add(title, BorderLayout.NORTH);
		
			// add the LF panel
			panel = new AddRemovePanel();
			panel.setBorder(new javax.swing.border.EmptyBorder(3, 5, 0, 3));
			try {
				panel.load(LF_FILE);
			} catch (Exception e) {}
			panel.getList().setCellRenderer(new LFRenderer());
			panel.getList().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = panel.getList().locationToIndex(e.getPoint());
	
						if (index != -1) {
							OptionDialog.showWarningDialog(parent, lang.getString("LFManager.warning"), "warn.laf"); //$NON-NLS-1$ //$NON-NLS-2$
							setLF();
						}
						setVisible(false);
						dispose();
					}
				}
			});
			getContentPane().add(panel, BorderLayout.CENTER);
			
			// add the button panel
			JPanel bp = new JPanel();
			bp.setLayout(new FlowLayout());
			bp.setBorder(UIFactory.createTopBorder());
			
				// OK button
				okButton = new javax.swing.JButton();
				okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
				okButton.setIcon(org.magelan.commons.ui.Icons.OK_ICON);
				okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						OptionDialog.showWarningDialog(parent, lang.getString("LFManager.warning"), "warn.laf"); //$NON-NLS-1$ //$NON-NLS-2$
						setLF();
						setVisible(false);
						dispose();
					}
				});
				getRootPane().setDefaultButton(okButton);
				bp.add(okButton);
				
				// Cancel button
				cancelButton = new javax.swing.JButton();
				cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
				cancelButton.setIcon(org.magelan.commons.ui.Icons.CANCEL_ICON);
				cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				bp.add(cancelButton);
			
			getContentPane().add(bp, BorderLayout.SOUTH);
		}
		
		private void setLF() {
			EditorUtil.setLookAndFeel((String) panel.getSelectedItem());
			try {
				panel.save(LF_FILE);
			} catch (java.io.IOException ex) {}
		}
	}

	/**
	 * L&F list cell renderer.
	 * <p>
	 * @version	1.0, 12/2003
	 */
	class LFRenderer extends ListRenderer {

		private boolean exists;
		
		public LFRenderer() {
			super();
		}
	
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			/* 
			 * Done to avoid creation of 2 file instances
			 */
			try {
				Class.forName(value.toString());
				exists = true;
			} 
			catch (ClassNotFoundException e) {
				exists = false;
			}
			
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
		
		public Icon getIcon(Object value) {
			if (exists) {
				return Icons.LF_ICON;
			}
			else {
				return Icons.ERROR_ICON;
			}
		}

		public String getToolTipText(Object value) {
			if (exists) {
				return null;
			}
			else {
				return lang.getString("LFRenderer.classNotFound"); //$NON-NLS-1$
			}
		}
	}
}
