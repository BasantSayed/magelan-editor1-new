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

import javax.swing.Action;
import javax.swing.JSplitPane;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.CoreModel;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.LayerManagerPanel;


/**
 *
 * @author Assen Antov
 * @version 1.0, 06/2004
 */
public class LayerExplorer extends AbstractEditorExtension implements EditorFrameListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	protected DrawingEditor editor;
	
	private LayerManagerPanel control;


	/**
	 * Creates a new Drawing Explorer.
	 */
	public LayerExplorer() {
		super();

		putValue(Action.NAME, "Layers"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 06/2004"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("LayersExplorer.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("LayersExplorer.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.LAYERS_ICON);
 		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}


	public Component getComponent() {
		if (control == null) {
			control = new LayerManagerPanel();
			control.setSplitOrientation(JSplitPane.VERTICAL_SPLIT);
			
			control.setPreferredSize(new Dimension(200, 350));
			control.setMinimumSize(new Dimension(200, 350));
			
			Editor.getEditorManager().addEditorFrameListener(this);
		}

		return control;
	}

	public void run(DrawingEditor e) {
	}

	protected void refresh(DrawingEditor editor) {
		DrawingModel model = editor.getModel();
		if (model instanceof CoreModel) {
			control.setModel((CoreModel) model);
		}
		else {
			control.setModel(null);
		}
	}


	/*
	 * EditorFrameListener
	 */
	
	public void editorFrameActivated(EditorFrameEvent e) {
		editor = Editor.getEditorManager().getSelectedEditor();
		refresh(editor);
		control.setEnabled(true);
	}


	public void editorFrameClosed(EditorFrameEvent e) {
		control.setModel(null);
		control.setEnabled(false);
	}

	public void editorFrameOpened(EditorFrameEvent e) {
	}

	public void editorFrameClosing(EditorFrameEvent e) {
	}

	public void editorFrameIconified(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	public void editorFrameDeiconified(EditorFrameEvent e) {
		control.setEnabled(false);
	}

	public void editorFrameDeactivated(EditorFrameEvent e) {
		control.setModel(null);
		control.setEnabled(false);
	}
}