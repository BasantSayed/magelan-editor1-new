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

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.util.DrawingUtil;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;
import org.magelan.editor.Editor;

/**
 * Zoom out view operation.
 *
 * @author Assen Antov
 * @version 1.0, 01/2002
 */
public class ZoomOut extends AbstractEditorExtension implements AdjustableEditorExtension {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private float factor = 2;
	
	/**
	 * Creates a new ZoomOut object.
	 */
	public ZoomOut() {
		super();

		putValue(Action.NAME, "Zoom Out"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.2, 01/2004"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("ZoomOut.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("ZoomOut.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.ZOOM_OUT_ICON);
		putValue(Action.ACCELERATOR_KEY,
         KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0));
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.navigate")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		java.awt.geom.Rectangle2D visible = ((JComponent) e).getVisibleRect();
		double cx = visible.getCenterX();
		double cy = visible.getCenterY();
		DrawingUtil.zoom(e, (int) cx, (int) cy, 1/factor);
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
}