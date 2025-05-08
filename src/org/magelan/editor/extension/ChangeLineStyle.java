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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.Entity;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.style.LineStyle;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.LineStyleControl;


/**
 * A line style chooser extension class.
 *
 * @author Assen Antov
 * @version 1.01, 04/2002
 */
public class ChangeLineStyle extends AbstractEditorExtension
	implements EditorFrameListener, ItemListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private LineStyleControl control;
	private DrawingEditor editor;


	/**
	 * Creates a new ChangeLineStyle object.
	 */
	public ChangeLineStyle() {
		super();

		putValue(Action.NAME, "Change Line Style"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 03/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("ChangeLineStyle.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("ChangeLineStyle.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.LINESTYLE_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.styles")); //$NON-NLS-1$
	}


	public Component getComponent() {
		if (control == null) {
			control = new LineStyleControl();
			control.setPreferredSize(new Dimension(180/*control.getPreferredSize().width*/,
												   25));
			control.setMaximumSize(control.getPreferredSize());
			control.addItemListener(this);
			Editor.getEditorManager().addEditorFrameListener(this);
		}

		return control;
	}

	public void run(DrawingEditor e) {
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}

		LineStyle ls = (LineStyle) e.getItem();

		//if (ls == null) return;
		if (editor == null) return;
		List selection = editor.getSelection();
		if (selection == null) return;
		
		if (selection.size() == 0) {
			control.getDrawing().setCurrent(ls);
		}
		// if selected entitites, change their linestyle
		else {
			Iterator elements = selection.iterator();

			while (elements.hasNext())
				((Entity) elements.next()).setLineStyle(ls);
		}

		editor.repaint();
	}

	public void editorFrameActivated(EditorFrameEvent e) {
		DrawingEditor editor = Editor.getEditorManager().getSelectedEditor();
		control.setDrawing(editor.getModel());
		control.setSelectedItem(editor.getModel().getCurrent(LineStyle.class));
		control.setEnabled(true);
		this.editor = editor;
	}

	public void editorFrameClosed(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	public void editorFrameOpened(EditorFrameEvent e) {
	}

	public void editorFrameClosing(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	public void editorFrameIconified(EditorFrameEvent e) {
	}

	public void editorFrameDeiconified(EditorFrameEvent e) {
	}

	public void editorFrameDeactivated(EditorFrameEvent e) {
	}
}