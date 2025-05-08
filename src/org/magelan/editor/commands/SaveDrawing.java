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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.Editor;
import org.magelan.editor.EditorCommand;
import org.magelan.editor.IEditorManager;
import org.magelan.editor.IFilesManager;


/**
 *
 * @author Assen Antov
 * @version 1.0, 10/2003
 */
public class SaveDrawing extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	/**
	 * Creates a new OpenDrawing object.
	 */
  public SaveDrawing() {
		super();
		putValue(Action.NAME, lang.getString("common.save")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("SaveDrawing.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("SaveDrawing.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.SAVE_ICON);
		putValue(Action.ACCELERATOR_KEY,
				 KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
	}


	public void run(DrawingEditor e) {
		IEditorManager m = Editor.getEditorManager();
		String name = m.getFileNameFor(m.getSelectedEditor());
		if (name == null) {
			JFrame f = m.getMainFrame();
			JFileChooser fileChooser = Editor.getFilesManager().getFileChooser();
			int returnVal = fileChooser.showSaveDialog(f);
	
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				DrawingEditor editor = m.getSelectedEditor();
				int res = Editor.getFilesManager().encode(editor.getModel(), editor.getRenderer(), fileChooser.getSelectedFile());

				// change title on success
				if (res == IFilesManager.SUCCESS) {
					m.getMainFrame().setTitle(fileChooser.getSelectedFile().getName());
					m.setFileNameFor(fileChooser.getSelectedFile().getName(), m.getSelectedEditor());
				}
			}
		}
		
		else {
			DrawingEditor editor = m.getSelectedEditor();
			int res = Editor.getFilesManager().encode(editor.getModel(), editor.getRenderer(), new java.io.File(name));
		}
	}
}