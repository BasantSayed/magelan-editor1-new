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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.ArrayList;

import javax.swing.*;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.*;
import org.magelan.editor.Editor;
import org.magelan.editor.EditorCommand;
import org.magelan.editor.IEditorManager;


/**
 *
 * @author Assen Antov
 * @version 1.0, 02/2005
 */
public class FeaturesManager extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private FDialog fd;
	
	public FeaturesManager() {
		super();
		putValue(Action.NAME, lang.getString("FeaturesManager.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("FeaturesManager.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("FeaturesManager.descr.long")); //$NON-NLS-1$

		putValue(Action.SMALL_ICON, Icons.GAP_ICON_16);
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
		putValue(Action.ACCELERATOR_KEY, null);
	}

	public void run(org.magelan.core.editor.DrawingEditor e) {
		// get the active manager
		IEditorManager m = Editor.getEditorManager();
		
		//if (fd == null) {
			fd = new FDialog(m.getMainFrame());
		//}
		//SwingUtilities.updateComponentTreeUI(fd);
		fd.setVisible(true);
	}
	
	
	class FDialog extends JDialog {

		private AddRemovePanel panel;
		private JButton okButton;
		private JButton cancelButton;
		
		FDialog() {
			super((Frame) null, true);
		}
		
		FDialog(final Frame parent) {
			super(parent, true);
			setTitle(lang.getString("FeaturesManager.title")); //$NON-NLS-1$
			setBounds(0, 0, 450, 300);
			SwingUtil.center(this);
			getContentPane().setLayout(new BorderLayout());
			
			JLabel title = UIFactory.createBanner(lang.getString("FeaturesManager.banner")/*, Icons.GAP_ICON_16*/); //$NON-NLS-1$
			getContentPane().add(title, BorderLayout.NORTH);
		
			// add the LF panel
			panel = new AddRemovePanel();
			panel.setBorder(new javax.swing.border.EmptyBorder(3, 5, 0, 3));

			java.util.List features = new ArrayList(Editor.getFeaturesManager().getFeatures());
			panel.setListData(features);

			panel.getList().setCellRenderer(new FRenderer());
			/*
			panel.getList().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = panel.getList().locationToIndex(e.getPoint());
	
						if (index != -1) {
							OptionDialog.showWarningDialog(parent, "Some Look and Feels require application restart for best results.", "warn.laf");
							setLF();
						}
						setVisible(false);
						dispose();
					}
				}
			});
			*/
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
						//OptionDialog.showWarningDialog(parent, "Some Look and Feels require application restart for best results.", "warn.laf");
						//setLF();
						Editor.getFeaturesManager().setFeatures(panel.getListData());
						setVisible(false);
						dispose();
					}
				});
				bp.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				// Cancel button
				cancelButton = new javax.swing.JButton();
				cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
				cancelButton.setIcon(org.magelan.commons.ui.Icons.CANCEL_ICON);
				cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}
				});
				bp.add(cancelButton);
				
			
			getContentPane().add(bp, BorderLayout.SOUTH);
		}
	}


	class FRenderer extends ListRenderer {

		private Class featureClass = null;
		private BeanInfo bi;
		
		public FRenderer() {
			super();
		}
	
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
			featureClass = null;
			try {
				featureClass = Class.forName(value.toString());
			} 
			catch (ClassNotFoundException e) {}
			if (featureClass != null) {
				try {
					bi = Introspector.getBeanInfo(featureClass);
				} catch (IntrospectionException ie) {}
			}
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
		
		public Icon getIcon(Object value) {
			if (featureClass != null && bi != null) {
				Image image = bi.getIcon(BeanInfo.ICON_COLOR_16x16);
				if (image != null) {
					return new ImageIcon(image);
				}
				else {
					return Icons.NEW_ICON;
				}
			}
			else {
				return (featureClass == null )? Icons.ERROR_ICON : Icons.WARNING_ICON;
			}
		}

		public String getText(Object value) {
			if (featureClass != null && bi != null) {
				return bi.getBeanDescriptor().getDisplayName();
			}
			return value.toString();
		}

		public String getToolTipText(Object value) {
			if (featureClass != null && bi != null) {
				return bi.getBeanDescriptor().getShortDescription();
			}
			else {
				return lang.getString("FeaturesManager.tooltop.notFound"); //$NON-NLS-1$
			}
		}
	}
}
