/*
*  The Magelan Project - Java Vector Graphics Editor Library
*  Copyright (C) 2003 Assen Antov and Larisa Feldman
*
*  This library is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This library is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*  See the GNU Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public
*  License along with this library; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-
*  1307  USA
*
*  For detailed information on this project please visit:
*  http://magelan.sourceforge.net/
*
*  Bug fixes should be submitted here:
*  https://sourceforge.net/tracker/?func=add&group_id=73971&atid=539550
*
*  Suggestions and comments should be sent to:
*  aantov@users.sourceforge.net
*  larisa@users.sourceforge.net
*/
package org.wayoda.magelan;

import org.magelan.editor.event.*;

/**
 * This is simply an adapter class that implements all the methods in 
 * EditorFrameListener with do nothing. See java.awt.event.WindowAdapter
 * for another example.
 *
 * @author eberhard
 */
public class EditorFrameAdapter implements EditorFrameListener {
	//~ Methods ----------------------------------------------------------------

	/**
	 * Invoked when a editor frame has been opened.
	 */
	public void editorFrameOpened(EditorFrameEvent e) {
	};

	/**
	 * Invoked when an editor frame is in the process of being closed. The
	 * close operation can be overridden at this point.
	 */
	public void editorFrameClosing(EditorFrameEvent e) {
	};

	/**
	 * Invoked when an editor frame has been closed.
	 */
	public void editorFrameClosed(EditorFrameEvent e) {
	};
	
	/**
	 * Invoked when an editor frame is iconified.
	 */
	public void editorFrameIconified(EditorFrameEvent e) {
	};

	/**
	 * Invoked when an editor frame is de-iconified.
	 */
	public void editorFrameDeiconified(EditorFrameEvent e) {
	};

	/**
	 * Invoked when an editor frame is activated.
	 */
	public void editorFrameActivated(EditorFrameEvent e) {
	};

	/**
	 * Invoked when an editor frame is de-activated.
	 */
	public void editorFrameDeactivated(EditorFrameEvent e) {
	};
}
