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

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JLabel;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.editor.Editor;
import org.magelan.editor.EditorCommand;
import org.magelan.editor.ui.SplashScreen;

/**
 * Shows the splash screen.
 * 
 * @version	1.0, 07/2003
 * @author	Assen Antov
 */
public class About extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	/**
	 * Creates a new About command.
	 */
	public About() {
		super();
		putValue(Action.NAME, lang.getString("common.about")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("About.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("About.descr.long")); //$NON-NLS-1$

		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		putValue(Action.ACCELERATOR_KEY, null);
	}


	public void run(org.magelan.core.editor.DrawingEditor e) {
		final SplashScreen ss = new SplashScreen();
		ss.getStatus().setIcon(Icons.GLOBE_ICON);
		ss.getStatus().setHorizontalTextPosition(JLabel.TRAILING);
		ss.setStatusText("magelan.sourceforge.net"); //$NON-NLS-1$
		ss.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				ss.setVisible(false);
				ss.dispose();
			}
		});
		ss.setVisible(true);
	}
}
