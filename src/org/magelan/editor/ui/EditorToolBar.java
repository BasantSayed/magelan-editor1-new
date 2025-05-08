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

import org.magelan.editor.*;
import org.magelan.editor.event.*;
import org.magelan.commons.ui.toolbar.*;


/**
 * EditorMenu is an impproved look and feel version of standard JToolBar. It
 * has exactly the same functionality as JToolBar. 
 * 
 * @author larisa
 * @version 1.0, 03/2003
 */
public class EditorToolBar extends SectionToolBar implements IStatusSource {

	/** StatusBar */
	private StatusBar statusBar;

	/** Mouse over listener */
	private StatusMouseListener mouseOverList;

	
	/**
	 */
	public EditorToolBar() {
		this(SPLIT);
	}
	
	/**
	 */
	public EditorToolBar(int mode) {
		super(mode);
		
		setFloatable(false);
		setBorderPainted(false);
		setRollover(true);
		mouseOverList = new StatusMouseListener(this);
	}

	
	/**
	 * Add a new JButton which dispatches the command.
	 *
	 * @param c the Command object to add as a new menu item
	 *
	 * @return
	 */
	public JButton add(AbstractEditorExtension c) {
		JButton b = add((String) c.getValue(AbstractEditorExtension.GROUP), (EditorCommand) c);
		b.addMouseListener(mouseOverList);
		if (!isEnabled()) {
			b.setEnabled(false);
		}
		return b;
	}

	/**
	 * Add a new JToggleButton which dispatches the command.
	 *
	 * @param c the ToggleCommand object to add as a new menu item
	 *
	 * @return
	 */
	public JButton add(ToggleCommand c) {
		//return add((EditorCommand) c);
		JButton b = add((EditorCommand) c);
		b.addMouseListener(mouseOverList);
		if (!isEnabled()) {
			b.setEnabled(false);
		}
		return b;
	}
	
	public void add(String sectionName, JComponent comp) {
		comp.addMouseListener(mouseOverList);
		if (!isEnabled()) {
			comp.setEnabled(false);
		}
		super.add(sectionName, comp);
	}
	
	/**
	 * Enables/disables the components contained by the tool bar.
	 *
	 * @param b enable/disable flag
	 */
	public void setEnabled(boolean b) {
		Object[] comp = getComponents();

		for (int i = 0; i < comp.length; i++) {
			((Component) comp[i]).setEnabled(b);
		}
	}

	/**
	 * ToolBar uses default status section, so there is no customizations done
	 *
	 * @param stBar Statusbar
	 *
	 * @see org.magelan.editor.IStatusSource#activateStatusListener(org.magelan.editor.StatusBar)
	 */
	public void activateStatusListener(StatusBar stBar) {
	}

	/**
	 * Empty implementation
	 *
	 * @param stBar
	 *
	 * @see org.magelan.editor.IStatusSource#configStatusBar(org.magelan.editor.StatusBar)
	 */
	public void configStatusListener(StatusBar stBar) {
		statusBar = stBar;
	}

	/**
	 * ToolBar uses default status section, so there is nothing to remove from
	 * statusBr
	 *
	 * @param stBar statusBar
	 *
	 * @see org.magelan.editor.IStatusSource#deActivateStatusListener(org.magelan.editor.StatusBar)
	 */
	public void deActivateStatusListener(StatusBar stBar) {
	}

	/**
	 * Just clean up default status message and refernces
	 *
	 * @param statusBar
	 *
	 * @see org.magelan.editor.IStatusSource#deConfigStatusBar(org.magelan.editor.StatusBar)
	 */
	public void deConfigStatusListener(StatusBar statusBar) {
		fireStatusChanged(null, null);
		statusBar = null;
	}

	/**
	 * Fres status Changed Event
	 *
	 * @see org.magelan.editor.IStatusSource#fireStatusChanged()
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
		if (!isEnabled()) {
			comp.setEnabled(false);
		}

		comp.addMouseListener(mouseOverList);
	}

}