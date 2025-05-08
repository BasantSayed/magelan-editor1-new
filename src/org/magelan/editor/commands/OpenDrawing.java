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
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.*;


/**
 * Opens a drawing.
 * 
 * @author Assen Antov
 * @version 1.2, 07/2006
 */
public class OpenDrawing extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Preferences prefs = Preferences.userNodeForPackage(SingleFrameEditorManager.class);
	
	/**
	 * Creates a new OpenDrawing object.
	 */
	public OpenDrawing() {
		super();
		putValue(Action.NAME, lang.getString("OpenDrawing.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("OpenDrawing.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("OpenDrawing.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.OPEN_ICON);
		putValue(Action.ACCELERATOR_KEY,
				 KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
	}


	public void run(org.magelan.core.editor.DrawingEditor e) {
		IEditorManager m = Editor.getEditorManager();
		
		// for single frame managers first try to close the selected editor
		if (m instanceof SingleFrameEditorManager) {
			if (m.getSelectedEditor() != null && !m.closeSelectedEditor()) {
				return;
			}
		}
		
		JFrame f = m.getMainFrame();
		JFileChooser fileChooser = Editor.getFilesManager().getFileChooser();
		int returnVal = fileChooser.showOpenDialog(f);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			DrawingEditor ed;
			File selFile = fileChooser.getSelectedFile();
			ed = Editor.getFilesManager().decode(selFile);
			
			if (ed == null) {
				return;
			}

			// add the file to the list of recently opened files
			String recent = prefs.get("recentfiles", null); //$NON-NLS-1$
			java.util.List recentList;
			if (recent != null)
				recentList = ClassManifest.getClassPath(recent);
			else 
				recentList = new ArrayList();
			
			if (!recentList.contains(selFile.getAbsolutePath())) {
				recentList.add(0, selFile.getAbsolutePath());
				if (recentList.size() > SingleFrameEditorManager.MAX_FILES_TO_SHOW) 
					recentList.remove(recentList.size()-1);
				recent = "";
				Iterator iter = recentList.iterator();
				while (iter.hasNext()) {
					recent += (String) iter.next();
					if (iter.hasNext()) recent += ";";
				}
				prefs.put("recentfiles", recent);
			}
			
			// open the editor and set proper editor frame title
			m.getMainFrame().setTitle(selFile.getName());
			m.openEditor(ed);
			m.setFileNameFor(selFile.getAbsolutePath(), ed);
			DrawingFileHandler fh = Editor.getFilesManager().getHandlerFor(selFile);
			if (fh != null) {
				m.setIconFor(fh.getIcon(), ed);
			}
		}
	}
}