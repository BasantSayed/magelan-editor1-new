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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Action;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DefaultDrawingEditor;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.entity.RectangleEntity;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.Viewport;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;
import org.magelan.editor.Editor;


/**
 *
 * @author Bernard Desprez
 * @version 1.0, Feb 2004
 */
public class NewRect extends AbstractEditorExtension
	implements AdjustableEditorExtension, MouseListener, MouseMotionListener {

	private static Lang lang = Lang.getLang(Editor.STRINGS);

	private DefaultDrawingEditor editor;
	private RectangleEntity rect = null;


	/**
	 * Creates a new NewRect object.
	 */
	public NewRect() {
		super();

		putValue(Action.NAME, lang.getString("NewRect.name"));  //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, lang.getString("NewRect.version"));  //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, lang.getString("NewRect.author"));  //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString(lang.getString("NewRect.descr.short")));  //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION,
        	lang.getString(lang.getString("NewRect.descr.long")));  //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.getImageIcon("rectangle"));  //$NON-NLS-1$
		putValue(AbstractEditorExtension.GROUP, lang.getString(lang.getString("common.draw")));  //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		if (e instanceof DefaultDrawingEditor) {
			editor = (DefaultDrawingEditor) e;
			editor.listenMouseEvents(false);
			editor.addMouseListener(this);
			editor.addMouseMotionListener(this);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		DrawingModel dwg = editor.getModel();
		Viewport viewport = dwg.getViewport();

		// event coordinates in drawing units
		double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
		double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

                if (rect == null) {
                        rect = new RectangleEntity(x,y,0,0);
                        dwg.add(rect);
		} else {
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                                rect = null;
				editor.removeMouseListener(this);
				editor.removeMouseMotionListener(this);
				editor.listenMouseEvents(true);
				editor.repaint();
                        } 
		}

		e.consume();

	}

	public void mouseReleased(MouseEvent e) {
                                rect = null;
				editor.removeMouseListener(this);
				editor.removeMouseMotionListener(this);
				editor.listenMouseEvents(true);
				editor.repaint();

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
                if (rect == null ) {
			return;
		}

		DrawingModel dwg = editor.getModel();
		Viewport viewport = dwg.getViewport();

		// event coordinates in drawing units
		double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
		double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

		rect.setWidth(x);
        rect.setHeight(y);
		editor.repaint();

	}
}
