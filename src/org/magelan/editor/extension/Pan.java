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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Action;
import javax.swing.JScrollPane;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.util.DrawingUtil;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;


/**
 * Pan navigation.
 *
 * @author Assen Antov
 * @version 1.1, 03/2003
 */
public class Pan extends AbstractEditorExtension
	implements MouseListener, MouseMotionListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private DrawingEditor editor;
	private JScrollPane scroll;
	private int x;
	private int y;
	private Point oldView;
	private Cursor cursor;


	/**
	 * Creates a new Pan object.
	 */
	public Pan() {
		super();

		putValue(Action.NAME, "Pan"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 03/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Pan.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Pan.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.PAN_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.navigate")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		if (e == null) return;
		editor = e;
		scroll = editor.getEnclosingScrollPane();
		editor.listenMouseEvents(false);
		editor.getContainer().addMouseListener(this);
		editor.getContainer().addMouseMotionListener(this);
		cursor = editor.getContainer().getCursor(); // !!!
		editor.getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		Editor.getEditorManager().getAssistanceLog().info(lang.getString("Pan.assist.1")); //$NON-NLS-1$
	}

	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			editor.getContainer().removeMouseListener(this);
			editor.getContainer().removeMouseMotionListener(this);
			editor.listenMouseEvents(true);
			e.consume();
			editor.getContainer().setCursor(cursor);
		} else {
			DrawingUtil.zoom(editor, e.getX(), e.getY(), 1);
			/*
			DrawingModel dwg = editor.getModel();
			Viewport viewport = dwg.getViewport();
			
			// event coordinates in drawing units
			double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
			double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();
			*/
			x = e.getX();
			y = e.getY();
			oldView = scroll.getViewport().getViewPosition();
		}
	}

	public void mouseDragged(MouseEvent e) {
		int newX = oldView.x + (x - e.getX());
		int newY = oldView.y + (y - e.getY());
		//int maxX = editor.getPreferredSize().width - scroll.getPreferredSize().width;
		//int maxY = editor.getPreferredSize().height - scroll.getPreferredSize().height;
		
		// fix to be inside the bounds
		//if (newX < 0) newX = 0;
		//if (newY < 0) newY = 0;
		//if (maxX > 0 && newX > maxX) newX = maxX;
		//if (maxY > 0 && newY > maxY) newY = maxY;
		
		Point p = new Point(newX, newY);
		scroll.getViewport().setViewPosition(p);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}