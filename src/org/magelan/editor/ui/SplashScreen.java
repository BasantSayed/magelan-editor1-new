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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.editor.Editor;

/**
 * Editor splash screen.
 * 
 * @version	1.0, 2003-7-7
 * @author	Assen Antov
 */
public class SplashScreen extends JWindow {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private static final Color TEXT_COLOR = new Color(0x99, 0, 0);
	private static final Color LIGHT_COLOR = new Color(0xFF, 0xEE, 0xEE);
	private static final String TEXT = lang.getString("SplashScreen.license"); //$NON-NLS-1$

	private JLabel status;
	
	/**
	 * Constructor for SplashScreen.
	 */
	public SplashScreen() {
		super();
		
		getContentPane().setLayout(new BorderLayout(5, 5));
		getContentPane().setBackground(Color.white);
		((JComponent) getContentPane()).setBorder(
			new CompoundBorder(LineBorder.createGrayLineBorder(), new EmptyBorder(2, 5, 5, 5)));
		
		// LOGO
		JLabel logo = new JLabel(lang.getString("common.version") + Editor.VERSION); //$NON-NLS-1$
		logo.setVerticalTextPosition(JLabel.TOP);
		logo.setFont(new Font("Tahoma", Font.BOLD, 13)); //$NON-NLS-1$
		logo.setForeground(TEXT_COLOR);
		logo.setBackground(Color.white);
		//logo.setOpaque(true);
		logo.setIcon(Icons.MAGELAN_LOGO_ICON);
		getContentPane().add(logo, BorderLayout.NORTH);
		
		// Texts
		JLabel text = new JLabel(TEXT);
		text.setBorder(
			new CompoundBorder(new javax.swing.border.LineBorder(TEXT_COLOR, 1), new EmptyBorder(0, 4 , 0, 4)));
		//text.setPreferredSize(new Dimension(350, 190));
		text.setBackground(LIGHT_COLOR);
		text.setOpaque(true);
		getContentPane().add(text, BorderLayout.CENTER);
		
		// Status
		status = new JLabel(" "); //$NON-NLS-1$
		status.setBorder(
			new CompoundBorder(new javax.swing.border.LineBorder(TEXT_COLOR, 1), new EmptyBorder(2, 4 , 2, 2)));
		status.setFont(new Font("Arial", Font.BOLD, 11)); //$NON-NLS-1$
		status.setForeground(TEXT_COLOR);
		status.setBackground(LIGHT_COLOR);
		status.setHorizontalTextPosition(JLabel.LEADING);
		status.setIcon(Icons.HOURGLASS_ICON);
		//text.setOpaque(true);
		status.setPreferredSize(new Dimension(text.getPreferredSize().width, status.getPreferredSize().height));
		getContentPane().add(status, BorderLayout.SOUTH);
		
		pack();
		SwingUtil.center(this);
		
	}

	
	public static void main(String[] args) {
		SplashScreen ss = new SplashScreen();
		ss.setStatusText("Preparing to load the editor..."); //$NON-NLS-1$
		ss.setVisible(true);
	}
	
	/**
	 * Returns the status.
	 * @return	status line
	 */
	public JLabel getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * @param	status	the status to set
	 */
	public void setStatusText(String status) {
		this.status.setText(status);
	}

}
