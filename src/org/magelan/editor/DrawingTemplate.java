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

import org.magelan.core.*;

import org.magelan.core.editor.*;

import javax.swing.*;


/**
 * This interface is used to represent pluggable templates for the
 * editor environment. It provides information for visualisation of
 * the template, as well as methods to obtain configured 
 * <code>CoreModel</code> and <code>DrawingEditor</code> instances.
 * They are returned by the corresponding {@link #getDrawing()} and 
 * {@link #getEditor()} methods.
 *
 * @author	Assen Antov
 * @version	1.1, 03/2004
 * 
 * @see		DefaultDrawingTemplate
 */
public interface DrawingTemplate {

	/**
	 * Returns the name of the template.
	 *
	 * @return the template name
	 */
	public String getName();

	/**
	 * Returns a short description of the template.
	 *
	 * @return template description
	 */
	public String getDescription();

	/**
	 * Icon, representing the template.
	 *
	 * @return icon that should be used to represent the template
	 */
	public ImageIcon getIcon();

	/**
	 * CoreModel instance for the template. The editor will use
	 * the {@link #getEditor()} method to obtain the specific
	 * editor used for editing the drawing type returned by this
	 * method. 
	 *
	 * @return	drawing instance
	 */
	public CoreModel getDrawing();

	/**
	 * Editor instance, used for editing drawings, obtained 
	 * using {@link #getDrawing()}. The editor returned is 
	 * expected have a proper drawing instance set.
	 *
	 * @return	editor instance
	 */
	public DrawingEditor getEditor();
	
	/**
	 * Class of the drawing represented by this template. 
	 *
	 * @return drawing class
	 * @see #getDrawing()
	 */
	public Class getDrawingClass();

	/**
	 * Class of the editor represented by this template. 
	 *
	 * @return editor class
	 * @see #getEditor()
	 */
	public Class getEditorClass();
}