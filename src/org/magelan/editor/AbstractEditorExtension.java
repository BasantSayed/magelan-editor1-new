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

import java.awt.event.ActionEvent;

import org.magelan.commons.*;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;

import org.apache.log4j.*;

/**
 * An extension of EditorCommand.
 * 
 * @version 2.0, 08/2004
 * @author Assen Antov
 */
public abstract class AbstractEditorExtension extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Logger log = Logger.getLogger(AbstractEditorExtension.class);
	
	/** The key used for storing Version property of command */
	public static final String VERSION = "Version"; //$NON-NLS-1$

	/** The key used for storing Author property */
	public static final String AUTHOR = "Author"; //$NON-NLS-1$

	/** The group of extensions the extension belongs to. */
	public static final String GROUP = "Group"; //$NON-NLS-1$


	/**
	 * Construct extension.
	 */
	public AbstractEditorExtension() {
		putValue(COMPATIBLE, DrawingModel.class);
		putValue(AVAILABLE, AVAILABLE_ALONE);
	}


	/**
	 * Redirects an action to the command - specific run(DrawingEditor)implementation
	 *
	 * @param e action event
	 */
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		/*
		try {
			run(Editor.getInstance().getManager().getSelectedEditor());
		} catch (Throwable ex) {
			log.error("unexpected error executing extension", ex); //$NON-NLS-1$
			org.magelan.commons.ui.ErrorDialog.showErrorDialog(
				lang.getString("AbstractEditorExtension.error") + getClass().getName(), ex); //$NON-NLS-1$
		}
		*/
	}

	/**
	 * Runs the extension. 
	 *
	 * @param e active drawing editor
	 */
	public abstract void run(DrawingEditor e);

	/**
	 * Returns a String that represents the value of this object.
	 *
	 * @return a string representation of the receiver
	 */
	public String toString() {
		return super.toString();
	}
}