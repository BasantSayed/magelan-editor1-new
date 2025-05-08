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
package org.magelan.editor;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;

import org.magelan.commons.Lang;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.Viewport;
import org.magelan.editor.event.StatusChangedEvent;
import org.magelan.editor.ui.HintDisplayPanel;
import org.magelan.editor.ui.StatusBar;


/**
 * A listener to transform mouse motions from
 * <code>DefaultDrawingEditor</code>s to status events.
 *
 * @author Assen Antov
 * @version 1.0, 04/2003
 */
public class StatusSourceEditor implements MouseListener, MouseMotionListener, IStatusSource {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
  /** StatusBar */
  private StatusBar statusBar;
  private ArrayList statusPanels;


	/**
	 * Wrapped <code>DrawingEditor</code>
	 */
	private DrawingEditor editor;
  
	//~ Constructors -----------------------------------------------------------

	public StatusSourceEditor(DrawingEditor editor) {
		this.editor = editor;
		editor.getContainer().addMouseListener(this);
		editor.getContainer().addMouseMotionListener(this);
	}


	//~ Methods ----------------------------------------------------------------


	public void mouseClicked(MouseEvent e) {
		if (editor.getState() == DrawingEditor.STATE_SELECT) {
      fireStatusChanged("mode", lang.getString("Select_2")); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
      fireStatusChanged("mode", ""); //$NON-NLS-1$ //$NON-NLS-2$
	  }
	}

	public void mousePressed(MouseEvent e) {
    if (editor.getState() == DrawingEditor.STATE_MODIFY) {
      
      Viewport viewport = editor.getModel().getViewport();
      double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
      double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();
  
		  fireStatusChanged("mode", lang.getString("Modify_6")); //$NON-NLS-1$ //$NON-NLS-2$
		  fireStatusChanged("mouse_pos", x + ":" + y); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void mouseDragged(MouseEvent e) {
    Viewport viewport = editor.getModel().getViewport();
    double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
    double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

		fireStatusChanged("mouse_pos", x + ":" + y); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void mouseReleased(MouseEvent e) {
		if (editor.getState() == DrawingEditor.STATE_MODIFY) {
			fireStatusChanged("mode", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void mouseMoved(MouseEvent e) {
    Viewport viewport = editor.getModel().getViewport();
    double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
    double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

		fireStatusChanged("mouse_pos", x + ":" + y); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void mouseExited(MouseEvent e) {
		fireStatusChanged("mouse_pos", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void mouseEntered(MouseEvent e) {
	}


	public void fireStatusChanged(String dest, String status) {
		if (statusBar != null) {
			statusBar.statusChanged(new StatusChangedEvent(this, dest, status));
		}
	}

	public void configStatusListener(StatusBar statBar) {
		statusBar = statBar;

		ArrayList panels = getStatusPanels();
		Iterator iter = panels.iterator();

		while (iter.hasNext()) {
			statusBar.addHintPanel((HintDisplayPanel) iter.next());
		}
	}

	private ArrayList getStatusPanels() {
		if (statusPanels == null) {
			statusPanels = new ArrayList();
			statusPanels.add(StatusBar.createNewHintDispalyPanel("mode", 80)); //$NON-NLS-1$
			statusPanels.add(StatusBar.createNewHintDispalyPanel("mouse_pos", 70)); //$NON-NLS-1$
		}

		return statusPanels;
	}

	public void deConfigStatusListener(StatusBar statusBar) {
		ArrayList panels = getStatusPanels();
		Iterator iter = panels.iterator();

		while (iter.hasNext()) {
			statusBar.removeHintPanel((HintDisplayPanel) iter.next());
		}

		statusBar = null;
	}

	public void activateStatusListener(StatusBar statusBar) {
		ArrayList panels = getStatusPanels();
		Iterator iter = panels.iterator();

		while (iter.hasNext()) {
			HintDisplayPanel panel = (HintDisplayPanel) iter.next();
			statusBar.setHintDisplayVisible(panel, true);
			fireStatusChanged(panel.getName(), ""); //$NON-NLS-1$
		}
	}

	public void deActivateStatusListener(StatusBar statusBar) {
		ArrayList panels = getStatusPanels();
		Iterator iter = panels.iterator();

		while (iter.hasNext()) {
			HintDisplayPanel panel = (HintDisplayPanel) iter.next();
			statusBar.setHintDisplayVisible(panel, false);
		}
	}
	
	public DrawingEditor getEditor() {
    return editor;
  }
}