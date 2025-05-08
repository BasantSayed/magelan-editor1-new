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

import java.awt.Color;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JColorChooser;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.CoreModel;
import org.magelan.core.Entity;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;


/**
 * Change the current color if no selection, or the color of the  entities
 * selected.
 *
 * @author Assen Antov
 * @version 1.02, 04/2002
 */
public class Colors extends AbstractEditorExtension {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	/**
	 * Creates a new Colors object.
	 */
	public Colors() {
		super();

		putValue(Action.NAME, "Colors"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.02, 04/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("ChangeColor.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("ChangeColor.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.COLORS_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		CoreModel dwg = (CoreModel) e.getModel();
		java.util.List selection = e.getSelection();

		// choose new color
		Color color = JColorChooser.showDialog(Editor.getEditorManager().getMainFrame(), 
				lang.getString("Choose_Current_Color_8"), //$NON-NLS-1$
				dwg.getColor());

		// if no selection change current color
		if (selection.size() == 0) {
			dwg.setColor(color);
		}
		// if selected entitites, change their color
		else {
			Iterator elements = selection.iterator();

			while (elements.hasNext())
				((Entity) elements.next()).setColor(color);
		}

		e.repaint();
	}
}