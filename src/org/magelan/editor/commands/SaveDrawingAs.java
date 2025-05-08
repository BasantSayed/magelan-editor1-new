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
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.OptionDialog;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.*;

/**
 * 
 * @author Assen Antov
 * @version 1.0, 04/2003
 */
public class SaveDrawingAs extends EditorCommand {

	private static Lang lang = Lang.getLang(Editor.STRINGS);

	/**
	 * Creates a new OpenDrawing object.
	 */
	public SaveDrawingAs() {
		super();
		putValue(Action.NAME, lang.getString("SaveDrawingAs.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("SaveDrawingAs.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("SaveDrawingAs.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, null);
		//putValue(Action.ACCELERATOR_KEY,
		//		 KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
	}

	public void run(DrawingEditor e) {
		IEditorManager m = Editor.getEditorManager();
		JFileChooser fileChooser = Editor.getFilesManager().getFileChooser();
		
		File file = null;
		int returnVal;
		boolean repeat = false;
		do {
			returnVal = fileChooser.showSaveDialog(m.getMainFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				repeat = false;
				if (file.exists()) {
					int opt = OptionDialog.showQuestionDialog(m.getMainFrame(), lang.getString("SaveDrawingAs.question.overwrite") + file.getName()); //$NON-NLS-1$
					if (opt == 1) {
						repeat = true;
					} 
					else if (opt == 2) {
						return;
					} 
				}
			}
			else {
				repeat = false;
			}
		} while (repeat);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			DrawingEditor editor = m.getSelectedEditor();
			int res = Editor.getFilesManager().encode(editor.getModel(),
					editor.getRenderer(), file);

			// change title on success
			if (res == IFilesManager.SUCCESS) {
				m.getMainFrame().setTitle(fileChooser.getSelectedFile().getName());
				m.setFileNameFor(fileChooser.getSelectedFile().getAbsolutePath(), m
						.getSelectedEditor());
				DrawingFileHandler fh = Editor.getFilesManager().getHandlerFor(
						fileChooser.getSelectedFile());
				if (fh != null) {
					m.setIconFor(fh.getIcon(), m.getSelectedEditor());
				}
			}
		}
	}
}