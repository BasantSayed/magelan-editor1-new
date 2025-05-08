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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.Editor;
import org.magelan.editor.EditorCommand;

/**
 * Cut to clipboard operation.
 *
 * @author Assen Antov
 * @version 1.0, 01/2002
 */
public class Cut extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	/**
	 * Creates a new Cut object.
	 */
	public Cut() {
		super();

		putValue(Action.NAME, lang.getString("common.cut")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Cut.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Cut.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.CUT_ICON);
		putValue(Action.ACCELERATOR_KEY,
				 KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
		putValue(COMPATIBLE, DrawingModel.class);
	}


	public void run(DrawingEditor e) {
		//		CoreModel dwg = e.getDrawing();
		//		Vector selection = e.getSelection();
		//		Vector clipboard = new Vector(selection);
		//		
		//		Enumeration enum = selection.elements();
		//		while (enum.hasMoreElements()) {
		//			Entity entity = (Entity) enum.nextElement();
		//			entity.hide(e);		// remove later
		//			dwg.remove(entity);
		//			e.paint();
		//		}
		//		System.setProperty("clipboard", new Vector());
	}
}