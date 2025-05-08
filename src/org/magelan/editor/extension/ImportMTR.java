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
package org.magelan.editor.extension;

import javax.swing.Action;
import javax.swing.JFileChooser;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;


/**
 * Import a Magelan TextEntity Representation (.MTR) file operation.
 *
 * @author Assen Antov
 * @version 1.0, 02/2002
 */
public class ImportMTR extends AbstractEditorExtension {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private JFileChooser fileChooser = new JFileChooser();

	/**
	 * Creates a new ImportMTR object.
	 */
	public ImportMTR() {
		super();

		putValue(Action.NAME, "ImportMTR"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 03/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Import_4")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Import_MTR_File_5")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.OPEN_ICON);
	}


	public void run(DrawingEditor e) {
		//fileChooser.setFileFilter(DefaultFileFilter.CLASS_FILTER);
		//		int returnVal = fileChooser.showOpenDialog(null);
		//		if (returnVal == JFileChooser.APPROVE_OPTION)
		//			try {
		//				String file = fileChooser.getSelectedFile().getCanonicalPath();
		//				NetParser parser = new NetParser();
		//				parser.loadFile(file);
		//				e.setDrawing(parser.n);
		//			} catch (Exception ee) {}
	}
}