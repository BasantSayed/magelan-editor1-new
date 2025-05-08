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

import javax.swing.*;


/**
 * This calss adds consistancy to the package. It has only one additional
 * funcionality compearing to JMenuBar- it allows to add UserHintListener to
 * the MenuBar. It is possible however to add this listener directly to the
 * Menu.  This call will go through all menus and add this listener to them.
 * NOTE: this class does not implement IStatus source
 */
public class EditorMenuBar extends JMenuBar {
	//~ Instance fields --------------------------------------------------------

	/** StatusBar */
	private StatusBar statusBar;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Constructs EditorMenuBar.
	 */
	public EditorMenuBar() {
		super();
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Appends the specified menu to the end of the menu bar.
	 *
	 * @param c the JMenu component to add
	 *
	 * @return c EditorMenu
	 */
	public EditorMenu add(EditorMenu c) {
		super.add(c);

		//pass UserHintListenere to the menu if one exists
		if (statusBar != null) {
			c.configStatusListener(statusBar);
		}

		return c;
	}

	/**
	 * Sets status bar to the menu - propagates call to EditorMenues
	 *
	 * @param stBar status bar
	 */
	public void setStatusBar(StatusBar stBar) {
		statusBar = stBar;

		Component[] components = getComponents();

		//go through children and add listener
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof EditorMenu) {
				((EditorMenu) components[i]).configStatusListener(statusBar);
			}
		}
	}

	/**
	 * Appends the specified menu to the end of the menu bar.
	 *
	 * @param c the JMenu component to add
	 *
	 * @return c EditorMenu
	 */
	public EditorMenu remove(EditorMenu c) {
		super.remove(c);

		//remove hintListener if there is one
		if (statusBar != null) {
			c.deConfigStatusListener(statusBar);
		}

		return c;
	}

	/**
	 * Removess UserHintListener from the MemuBar
	 */
	public void removeHintListener() {
		Component[] components = getComponents();

		//go through children and remove listeners
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof EditorMenu) {
				((EditorMenu) components[i]).deConfigStatusListener(statusBar);
			}
		}

		statusBar = null;
	}
}