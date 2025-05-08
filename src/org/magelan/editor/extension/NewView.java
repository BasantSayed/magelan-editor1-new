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
import java.awt.event.MouseMotionListener;

import javax.swing.Action;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.entity.ViewEntity;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.Viewport;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;
import org.magelan.editor.Editor;


/**
 *
 * @author Assen Antov
 * @version 1.0, 08/2004
 */
public class NewView extends AbstractEditorExtension implements MouseListener,
																MouseMotionListener,
																AdjustableEditorExtension {
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private DrawingEditor editor;
	private ViewEntity view = null;

	
	public NewView() {
		super();

		putValue(Action.NAME, "View"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 08/2004"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("NewView.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("NewView.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.VIEW_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.draw2")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
    if (e == null) return;
		editor = e;
		editor.listenMouseEvents(false);
		editor.getContainer().addMouseListener(this);
		editor.getContainer().addMouseMotionListener(this);
		
		Editor.getEditorManager().getAssistanceLog().info(lang.getString("NewView.assist.1")); //$NON-NLS-1$
	}

	public void mouseClicked(MouseEvent e) {
		DrawingModel dwg = editor.getModel();
		Viewport viewport = dwg.getViewport();
		
		// event coordinates in drawing units
		double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
		double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();
		
		if (view == null) {
			view = new ViewEntity(x, y, 0, 0, dwg);
			dwg.add(view);
			
			Editor.getEditorManager().getAssistanceLog().info(lang.getString("NewView.assist.2")); //$NON-NLS-1$
		} else {
			view = null;
			editor.getContainer().removeMouseListener(this);
			editor.getContainer().removeMouseMotionListener(this);
			editor.listenMouseEvents(true);
			editor.repaint();
			//editor.getContainer().setToolTipText(null);
			
			Editor.getEditorManager().getAssistanceLog().info(lang.getString("NewView.assist.3")); //$NON-NLS-1$
		}

		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
		DrawingModel dwg = editor.getModel();
		Viewport viewport = dwg.getViewport();

		// event coordinates in drawing units
		double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
		double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();
		
		if (view == null) {
			//editor.getContainer().setToolTipText("" + x + ": " + y); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		view.setWidth(- view.getX() + x);
		view.setHeight(- view.getY() + y);

		editor.getSnap().snap(x, y, view.getModifiers()[5]);
				
		editor.repaint();
		editor.getSnap().paint(editor.getContainer().getGraphics());
		//editor.getContainer().setToolTipText("" + line.getLength()); //$NON-NLS-1$
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}
}