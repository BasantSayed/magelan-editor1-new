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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.OptionDialog;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.entity.TextEntity;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.IEditorManager;
import org.magelan.editor.ui.TextEditDialog;


/**
 * Edits the contents of the selected entities.
  *
 * @author Assen Antov
 * @version 1.0,  05/2003
 */
public class EditText extends AbstractEditorExtension {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	public EditText() {
		super();

		putValue(Action.NAME, "Edit Text"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 05/2003"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("EditText.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("EditText.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.EDIT_TEXT_ICON);
		putValue(Action.ACCELERATOR_KEY,
				 KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		DrawingModel dwg = e.getModel();
		List selection = e.getSelection();
		java.awt.Frame parent = Editor.getEditorManager().getMainFrame();
		boolean tes = false;
		IEditorManager em = Editor.getEditorManager();
		
		if (selection.size() == 0) {
			em.getAssistanceLog().warn(lang.getString("EditText.info")); //$NON-NLS-1$
			OptionDialog.showInfoDialog(em.getMainFrame(), lang.getString("EditText.info")); //$NON-NLS-1$
			return;
		}
		
		else {
			Iterator iter = selection.iterator();
			while (iter.hasNext()) {
				Object entity = iter.next();
				if (entity instanceof TextEntity) {
					tes = true;
					(new TextEditDialog((TextEntity) entity, dwg, parent)).setVisible(true);
				}
			}
		}
		
		if (!tes) {
			em.getAssistanceLog().warn(lang.getString("EditText.info")); //$NON-NLS-1$
			OptionDialog.showInfoDialog(em.getMainFrame(), lang.getString("EditText.info")); //$NON-NLS-1$
		}
	}
}