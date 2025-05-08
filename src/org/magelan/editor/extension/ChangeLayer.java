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

import javax.swing.Action;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.Entity;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.style.Layer;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.LayerComboBox;


/**
 * A layer chooser/modifier extension class.
 *
 * @author Assen Antov
 * @version 1.0, 04/2002
 */
public class ChangeLayer extends AbstractEditorExtension
	implements EditorFrameListener, ItemListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private LayerComboBox control;
	private DrawingEditor editor;


	/**
	 * Creates a new ChangeLayer object.
	 */
	public ChangeLayer() {
		super();

		putValue(Action.NAME, "ChangeLayer"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 03/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("ChangeLayer.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("ChangeLayer.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.LAYERS_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$

		control = new LayerComboBox();
		control.setPreferredSize(new Dimension(250, 25));
		control.setMaximumSize(control.getPreferredSize());
		control.addItemListener(this);
		Editor.getEditorManager().addEditorFrameListener(this);
	}


	public Component getComponent() {
		return control;
	}
	
	public void run(DrawingEditor e) {
	}


	/**
	 * @see	ItemListener
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		
		editor = Editor.getEditorManager().getSelectedEditor();
		Layer l = (Layer) e.getItem();

		java.util.List selection = editor.getSelection();

		if (selection.size() == 0) {
			editor.getModel().setCurrent(l);
		}
		// if selected entitites, change their layer
		else {
			java.util.Iterator elements = selection.iterator();

			while (elements.hasNext())
				((Entity) elements.next()).setLayer(l);
		}

		if (editor != null) {
			editor.repaint();
		}
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameActivated(EditorFrameEvent e) {
		control.setEnabled(true);
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameClosed(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameOpened(EditorFrameEvent e) {
		editor = Editor.getEditorManager().getSelectedEditor();
		control.setFromDrawing(editor.getModel());
		control.setEnabled(true);
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameClosing(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameIconified(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameDeiconified(EditorFrameEvent e) {
		control.setEnabled(true);
	}

	/**
	 * @param	e	editor frame event to process
	 * @see	EditorFrameListener
	 */
	public void editorFrameDeactivated(EditorFrameEvent e) {
		control.setEnabled(false);
	}
}