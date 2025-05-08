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
package org.magelan.editor;

import java.awt.event.*;


/**
 * Toggle command represents a Two- state command that may be used to add a
 * CheckBox-like items to the menues and toolbars.
 */
public abstract class ToggleCommand extends EditorCommand {
	//~ Instance fields --------------------------------------------------------

	/** Selected state of Command */
	private boolean selected = false;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Construct ToggleCommand.
	 */
	public ToggleCommand() {
		super();
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Redirects an action to the command - specific execute implementation
	 *
	 * @param e 
	 */
	public void actionPerformed(ActionEvent e) {
		setSelected(!selected);
		super.actionPerformed(e);
	}

	/**
	 * Returns selected state of the command.
	 *
	 * @return boolean selected state of the command
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets selected state of the command.
	 *
	 * @param newValue boolean new selected state
	 */
	public void setSelected(boolean newValue) {
		boolean oldValue = selected;
		selected = newValue;
		firePropertyChange("selected", new Boolean(oldValue), //$NON-NLS-1$
						   new Boolean(newValue));
	}
}