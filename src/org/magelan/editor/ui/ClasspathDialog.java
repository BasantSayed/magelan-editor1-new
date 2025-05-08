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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.ClasspathPanel;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.commons.ui.UIFactory;
import org.magelan.editor.Editor;

/**
 * Classpath editor dialog.
 * 
 * @version 2.0, 01/2004
 * @author Assen Antov
 */
public class ClasspathDialog extends JDialog {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private ClasspathPanel panel;
	
	private JButton okButton;
	private JButton cancelButton;
	
	
	public ClasspathDialog() {
		this((Frame) null);
	}
	
	public ClasspathDialog(Frame parent) {
		super(parent, true);
		setTitle(lang.getString("ClasspathDialog.title")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());
		
		JLabel title = UIFactory.createBanner(lang.getString("ClasspathDialog.banner"), Icons.PATH_ICON); //$NON-NLS-1$
		getContentPane().add(title, BorderLayout.NORTH);
		
		/*
		 * Classpath panel
		 */
		panel = new ClasspathPanel(ClasspathPanel.CLASSPATH_FILE);
		panel.setPreferredSize(new Dimension(350, 200));
		panel.setBorder(new javax.swing.border.EmptyBorder(3, 5, 0, 3));
		getContentPane().add(panel, BorderLayout.CENTER);
		
		/*
		 * Buttons
		 */
		
		javax.swing.JPanel bp = new javax.swing.JPanel();
		
		bp.setBorder(UIFactory.createTopBorder());
		
		// OK button
		okButton = new javax.swing.JButton();
		okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
		okButton.setIcon(Icons.OK_ICON);
		okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					panel.save(ClasspathPanel.CLASSPATH_FILE);
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
		
		pack();
		SwingUtil.center(this);
	}

	public static void main(String[] params) {
		JDialog f = new ClasspathDialog();
		
		f.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		
		try {
			UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.Plastic3DLookAndFeel"); //$NON-NLS-1$
			SwingUtilities.updateComponentTreeUI(f);
		} catch (Exception e) {}
		
		f.setVisible(true);
	}
}