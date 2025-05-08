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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DefaultDrawingEditor;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.entity.ImageEntity;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.Viewport;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;
import org.magelan.editor.Editor;


/**
 * 
 * @author Assen Antov
 * @version 1.0, 04/2002
 */
public class NewImage extends AbstractEditorExtension
	implements AdjustableEditorExtension, MouseListener {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private DefaultDrawingEditor editor;


	/**
	 * Creates a new NewImage object.
	 */
	public NewImage() {
		super();

		putValue(Action.NAME, "Image"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 04/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Create_new_Image_4")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Create_New_Image_in_Selected_Editor_5")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.GEAR_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.draw")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		if (e instanceof DefaultDrawingEditor) {
			editor = (DefaultDrawingEditor) e;
			editor.listenMouseEvents(false);
			editor.addMouseListener(this);
			
			Editor.getEditorManager().getAssistanceLog().info(lang.getString("NewImage.assist.1")); //$NON-NLS-1$
		}
	}


	public void mouseClicked(MouseEvent e) {
		DrawingModel dwg = editor.getModel();
		Viewport viewport = dwg.getViewport();

		// event coordinates in drawing units
		double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
		double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

		dwg.add(new ImageEntity("/org/magelan/commons/images/magelan.gif", x, y)); //$NON-NLS-1$

		editor.removeMouseListener(this);
		editor.listenMouseEvents(true);
		editor.repaint();
		e.consume();
		
		Editor.getEditorManager().getAssistanceLog().info(lang.getString("NewImage.assist.2")); //$NON-NLS-1$
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}