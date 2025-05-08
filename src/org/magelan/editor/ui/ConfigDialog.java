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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.commons.ui.propertytable.PropertyTable;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;

/**
 * A dialog for configuration of extensions. Invoked by {@link ExtensionsDialog}.
 * <p>
 * @version	1.0, 01/2004
 * @author	Assen Antov
 */
public class ConfigDialog extends JDialog {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private AbstractEditorExtension ext;
	
	private PropertyTable pt;
	private JButton okButton;
	private JButton cancelButton;
		
	public ConfigDialog() {
		super((Frame) null, true);
	}
		
	public ConfigDialog(Frame parent) {
		super(parent, true);
		setTitle(lang.getString("ConfigDialog.title")); //$NON-NLS-1$
		setBounds(0, 0, 300, 250);
		SwingUtil.center(this);
		getContentPane().setLayout(new BorderLayout());
		
		
		pt = new PropertyTable();
		getContentPane().add(new JScrollPane(pt), BorderLayout.CENTER);
		
		// add the button panel
		JPanel bp = new JPanel();
		bp.setLayout(new FlowLayout());
		
			// OK button
			okButton = new javax.swing.JButton();
			okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
			okButton.setIcon(Icons.OK_ICON);
			okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						//panel.save(ClasspathPanel.CLASSPATH_FILE);
					} catch (Exception ex) {}
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
	}
	
	public void setExtension(AbstractEditorExtension ext) {
		this.ext = ext;
		pt.setBean(ext);
	}
	
	
}
