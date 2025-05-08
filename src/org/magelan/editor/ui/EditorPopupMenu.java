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

import org.magelan.editor.*;

import org.magelan.editor.event.*;

import java.awt.*;

import javax.swing.*;


/**
 * EditorPopupMenu is an impproved look and feel version of standard
 * JPopupMenu.     In oredr to ensure proper l&f - use myMenu.add(Command
 * doSomethingUseful).
 */
public class EditorPopupMenu extends JPopupMenu implements IStatusSource {
	//~ Instance fields --------------------------------------------------------

	/** Mouse over listener */
	private StatusMouseListener mouseOverList;

	/** StatusBar */
	private StatusBar statusBar;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates a new EditorPopupMenu with no text.
	 */
	public EditorPopupMenu() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * Creates a new EditorPopupMenu with the supplied string as its text
	 *
	 * @param label The text for the menu label
	 */
	public EditorPopupMenu(String label) {
		super(label);
		mouseOverList = new StatusMouseListener(this);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Creates a new menuitem attached to the specified  Command object and
	 * appends it to the end of this menu.
	 *
	 * @param c the ToggleCommand for the menuitem to be added
	 *
	 * @return DOCUMENT ME!
	 *
	 * @see Command, AbstarctAcrion
	 */
	public JCheckBoxMenuItem add(ToggleCommand c) {
		//create MenuItem
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem((String) c.getValue(Action.NAME));
		mi.setHorizontalTextPosition(JButton.RIGHT);
		mi.setVerticalTextPosition(JButton.CENTER);
		mi.setEnabled(c.isEnabled());
		mi.setSelected(c.isSelected());

		//add Command as acrion Listener
		mi.addActionListener(c);
		add(mi);

		//register listener to command's properties
		mi.addMouseListener(mouseOverList);

		return mi;
	}

	/**
	 * Empty implementation
	 *
	 * @param stBar
	 *
	 * @see magelan.preview.IStatusSource#configStatusBar(magelan.preview.StatusBar)
	 */
	public void configStatusListener(StatusBar stBar) {
		statusBar = stBar;

		MenuElement[] components = getSubElements();

		//go through children and add listener
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof EditorMenu) {
				((EditorMenu) components[i]).activateStatusListener(statusBar);
			}
		}
	}

	/**
	 * Menu uses default status section, so there is no customizations done
	 *
	 * @param stBar Statusbar
	 *
	 * @see magelan.preview.IStatusSource#activateStatusListener(magelan.preview.StatusBar)
	 */
	public void activateStatusListener(StatusBar stBar) {
	}

	/**
	 * Menu uses default status section, so there is nothing to remove from
	 * statusBar
	 *
	 * @param stBar statusBar
	 *
	 * @see magelan.preview.IStatusSource#deActivateStatusListener(magelan.preview.StatusBar)
	 */
	public void deActivateStatusListener(StatusBar stBar) {
	}

	/**
	 * Just clean up sefault status message
	 *
	 * @param stBar
	 *
	 * @see magelan.preview.IStatusSource#deConfigStatusBar(magelan.preview.StatusBar)
	 */
	public void deConfigStatusListener(StatusBar stBar) {
		MenuElement[] components = getSubElements();

		//go through children and remove listeners
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof EditorMenu) {
				((EditorMenu) components[i]).deConfigStatusListener(statusBar);
			}
		}

		statusBar = null;
	}

	/**
	 * Fires status Changed Event
	 *
	 * @see magelan.preview.IStatusSource#fireStatusChanged()
	 */
	public void fireStatusChanged(String dest, String status) {
		if (statusBar != null) {
			statusBar.statusChanged(new StatusChangedEvent(this, dest, status));
		}
	}

	/**
	 * We add all additional configuration and listeners addition here, as
	 * appropriate
	 *
	 * @param comp the component to be enhanced
	 * @param constraints the constraints to be enforced on the component
	 * @param index the index of the component
	 *
	 * @see java.awt.Container#addImpl(java.awt.Component, java.lang.Object,
	 * 		int)
	 */
	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);
		comp.addMouseListener(mouseOverList);
	}
}