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
package org.magelan.editor.event;

import org.magelan.core.editor.*;

import org.magelan.editor.*;

import java.awt.*;


/**
 * This Event was specificly created to handle Extensions that depend on the
 * parent Frame of DrawingEditor. The parent InternalFrame from Swing is not
 * JFrame, so it's impossible to generalize this type of event delivery. To
 * enable the delivery of InternalFrame independenly from the fact wheather
 * Internal Frame exists or not - use this Event.
 *
 * @author larisa
 * @version 1.0, ??/2003
 */
public class EditorFrameEvent extends AWTEvent {

	/** The first number in the range of IDs used for editor frame events. */
	public static final int EDITOR_FRAME_FIRST = 80549;

	/** The last number in the range of IDs used for editor frame events. */
	public static final int EDITOR_FRAME_LAST = 80555;

	/**
	 * The "window opened" event.  This event is delivered only the first time
	 * the editor frame is made visible.
	 *
	 * @see JInternalFrame#show
	 */
	public static final int EDITOR_FRAME_OPENED = EDITOR_FRAME_FIRST;

	/**
	 * The "window is closing" event. This event is delivered when the user
	 * attempts to close the editor frame, such as by clicking the editor
	 * frame's close button, or when a program attempts to close the editor
	 * frame  by invoking the <code>setClosed</code> method.
	 *
	 * @see JInternalFrame#setDefaultCloseOperation
	 * @see JInternalFrame#doDefaultCloseAction
	 * @see JInternalFrame#setClosed
	 */
	public static final int EDITOR_FRAME_CLOSING = 1 + EDITOR_FRAME_FIRST;

	/**
	 * The "window closed" event. This event is delivered after the editor
	 * frame has been closed as the result of a call to  the
	 * <code>setClosed</code> or <code>dispose</code> method.
	 *
	 * @see JInternalFrame#setClosed
	 * @see JInternalFrame#dispose
	 */
	public static final int EDITOR_FRAME_CLOSED = 2 + EDITOR_FRAME_FIRST;

	/**
	 * The "window iconified" event. This event indicates that the editor frame
	 * was shrunk down to a small icon.
	 *
	 * @see JInternalFrame#setIcon
	 */
	public static final int EDITOR_FRAME_ICONIFIED = 3 + EDITOR_FRAME_FIRST;

	/**
	 * The "window deiconified" event type. This event indicates that the
	 * editor frame has been restored to its normal size.
	 *
	 * @see JInternalFrame#setIcon
	 */
	public static final int EDITOR_FRAME_DEICONIFIED = 4 + EDITOR_FRAME_FIRST;

	/**
	 * The "window activated" event type. This event indicates that keystrokes
	 * and mouse clicks are directed towards this editor frame.
	 *
	 * @see JInternalFrame#show
	 * @see JInternalFrame#setSelected
	 */
	public static final int EDITOR_FRAME_ACTIVATED = 5 + EDITOR_FRAME_FIRST;

	/**
	 * The "window deactivated" event type. This event indicates that
	 * keystrokes and mouse clicks are no longer directed to the editor frame.
	 *
	 * @see JEditorFrame#setSelected
	 */
	public static final int EDITOR_FRAME_DEACTIVATED = 6 + EDITOR_FRAME_FIRST;


	private DrawingEditor editor;


	/**
	 * Constructs an <code>EditorFrameEvent</code> object.
	 *
	 * @param source the <code>EditorFrame</code> object that originated the
	 * 		  event
	 * @param id an integer indicating the type of event
	 */
	public EditorFrameEvent(EditorFrame source, int id) {
		super(source, id);
		if (source != null) {
			editor = source.getDrawingEditor();
		}
	}

	/**
	 * Constructs an <code>EditorFrameEvent</code> object.
	 *
	 * @param ed the<code>EditorFrame</code> object that originated the event
	 * @param id an integer indicating the type of event
	 */
	public EditorFrameEvent(DrawingEditor ed, int id) {
		super(new Object(), id);	// changed from null to Object
		editor = ed;
	}


	/**
	 * Returns a parameter string identifying this event. This method is useful
	 * for event logging and for debugging.
	 *
	 * @return a string identifying the event and its attributes
	 */
	public String paramString() {
		String typeStr;
		switch (id) {
			case EDITOR_FRAME_OPENED:
				typeStr = "EDITOR_FRAME_OPENED"; //$NON-NLS-1$
				break;
			case EDITOR_FRAME_CLOSING:
				typeStr = "EDITOR_FRAME_CLOSING"; //$NON-NLS-1$
				break;
			case EDITOR_FRAME_CLOSED:
				typeStr = "EDITOR_FRAME_CLOSED"; //$NON-NLS-1$
				break;
			case EDITOR_FRAME_ICONIFIED:
				typeStr = "EDITOR_FRAME_ICONIFIED"; //$NON-NLS-1$
				break;
			case EDITOR_FRAME_DEICONIFIED:
				typeStr = "EDITOR_FRAME_DEICONIFIED"; //$NON-NLS-1$
				break;
			case EDITOR_FRAME_ACTIVATED:
				typeStr = "EDITOR_FRAME_ACTIVATED"; //$NON-NLS-1$
				break;
			case EDITOR_FRAME_DEACTIVATED:
				typeStr = "EDITOR_FRAME_DEACTIVATED"; //$NON-NLS-1$
				break;
			default:
				typeStr = "unknown type"; //$NON-NLS-1$
		}

		return typeStr;
	}

	/**
	 * Returns the frame originator of the event.
	 * 
	 * @return the <code>EditorFrame</code> object that originated the event
	 */
	public EditorFrame getEditorFrame() {
		return (source instanceof EditorFrame) ? (EditorFrame) source : null;
	}

	/**
	 * The editor originator of the event.
	 * 
	 * @return a DrawingEditor
	 */
	public DrawingEditor getEditor() {
		return editor;
	}
}