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

import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JToolBar;

import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.event.EditorFrameListener;


/**
 * Defines an editor frame.
 *
 * @author Assen Antov
 * @version 1.0, 02/2004
 * 
 * @see EditorFrameEvent
 * @see EditorFrameListener
 */
public interface EditorFrame {

	/** A property change event, fired when frame's editor is changed. */
	public static final String EVENT_EDITOR = "frame.editor"; //$NON-NLS-1$

	/** A property change event, fired when frame's name is changed. */
	public static final String EVENT_NAME = "frame.name"; //$NON-NLS-1$

	/** A property change event, fired when frame's icon is changed. */
	public static final String EVENT_ICON = "frame.icon"; //$NON-NLS-1$


	/**
	 * Returns the drawing editor, hosted by the frame.
	 * 
	 * @return current drawing editor
	 */
	public DrawingEditor getDrawingEditor();

	/**
	 * Sets a new drawing editor to the frame. Fires a 
	 * <code>PropertyChangeEvent</code> named {@link #EVENT_EDITOR}.
	 * 
	 * @param ed new drawing editor
	 */
	public void setDrawingEditor(DrawingEditor ed);

	/**
	 * Returns the frame name. 
	 *
	 * @return the frame name
	 */
	public String getFrameName();

	/**
	 * Sets a new frame name. The name set will reflect on the title of the 
	 * frame. The frame implementation is the one to decide how exactly to represent
	 * the name of the frame (or the contained drawing) in its title. Fires a 
	 * <code>PropertyChangeEvent</code> named {@link #EVENT_NAME}.
	 *
	 * @param name the new frame name
	 */
	public void setFrameName(String name);

	/**
	 * Returns the frame icon.
	 *
	 * @return the frame icon
	 */
	public Icon getFrameIcon();

	/**
	 * Sets a new frame icon. Fires a <code>PropertyChangeEvent</code> 
	 * named {@link #EVENT_ICON}.
	 *
	 * @param title the new frame title
	 */
	public void setFrameIcon(Icon icon);

	/**
	 * Returns a toolbar for the frame. May return <code>null</code> if the 
	 * frame implementation does not provide support for a toolbar.
	 *
	 * @return the frame toolbar or <code>null</code>
	 */
	public JToolBar getToolBar();

	/**
	 * Clears the frame. Removes the drawing editor and the toolbar, sets
	 * null icon and title.
	 */
	public void clear();

	/**
	 * Adds the specified listener to receive editor frame events from this
	 * editor frame.
	 *
	 * @param l the editor frame listener
	 */
	public void addEditorFrameListener(EditorFrameListener l);

	/**
	 * Removes the specified editor frame listener so that it no longer
	 * receives editor frame events from this internal frame.
	 *
	 * @param l the editor frame listener
	 */
	public void removeEditorFrameListener(EditorFrameListener l);

	/**
	 * Returns an array of all the <code>EditorFrameListener</code>s added to
	 * this <code>EditorFrame</code> using <code>addEditorFrameListener</code>.
	 *
	 * @return all of the <code>EditorFrameListener</code>s added or an empty
	 * 		array if no listeners have been added
	 *
	 * @see #addEditorFrameListener
	 */
	public EditorFrameListener[] getEditorFrameListeners();

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is
	 * registered for all properties.
	 *
	 * @param listener the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes
	 * a PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will
	 * be invoked only when a call on firePropertyChange names that specific
	 * property.
	 *
	 * @param propertyName the name of the property to listen on.
	 * @param listener the PropertyChangeListener to be added
	 */
	public void addPropertyChangeListener(String propertyName,
										  PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener for a specific property. If listener
	 * is null, no exception is thrown and no action is performed.
	 *
	 * @param propertyName the name of the property that was listened on.
	 * @param listener the PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(String propertyName,
											 PropertyChangeListener listener);
}