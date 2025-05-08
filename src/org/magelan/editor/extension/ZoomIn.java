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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.util.DrawingUtil;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;
import org.magelan.editor.Editor;

/**
 * Zoom in view operation.
 *
 * @author Assen Antov
 * @version 1.3, 03/2004
 */
public class ZoomIn extends AbstractEditorExtension implements AdjustableEditorExtension, MouseListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private float factor = 2;
	private DrawingEditor editor;
	private JScrollPane scroll;
	private Cursor cursor;

	/**
	 * Creates a new ZoomIn object.
	 */
	public ZoomIn() {
		super();

		putValue(Action.NAME, "Zoom In"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.3, 03/2004"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("ZoomIn.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("ZoomIn.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.ZOOM_IN_ICON);
		putValue(Action.ACCELERATOR_KEY,
         KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0));
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.navigate")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		
		java.awt.geom.Rectangle2D visible = ((JComponent) e).getVisibleRect();
		double cx = visible.getCenterX();
		double cy = visible.getCenterY();
		DrawingUtil.zoom(e, (int) cx, (int) cy, factor);
		//e.paint();
		/*
		if (e == null) return;
		editor = e;
		scroll = editor.getEnclosingScrollPane();
		editor.listenMouseEvents(false);
		editor.getContainer().addMouseListener(this);
		cursor = editor.getContainer().getCursor(); // !!!
		editor.getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		*/
	}

	/**
	 * Returns the zoom factor.
	 * @return float
	 */
	public float getZoomFactor() {
		return factor;
	}

	/**
	 * Sets the zoom factor.
	 * @param factor the zoom factor to set
	 */
	public void setZoomFactor(float factor) {
		this.factor = factor;
	}
	
	
	public void mouseClicked(MouseEvent e) {
		DrawingUtil.zoom(editor, e.getX(), e.getY(), factor);
		editor.getContainer().removeMouseListener(this);
		editor.listenMouseEvents(true);
		e.consume();
		editor.getContainer().setCursor(cursor);
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}