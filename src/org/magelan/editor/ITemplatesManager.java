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

import java.util.List;


/**
 * Contains and manages <code>DrawingTemplate</code>s.  The implementations of
 * this interface are extensively used by open and close methods of
 * <code>IEditorManager</code>s.
 *
 * @author larisa
 * @version 1.1, 03/2004
 *
 * @see DrawingTemplate
 * @see IEditorManager
 */
public interface ITemplatesManager {

	/**
	 * Returns a template which is to be used on creation of a new drawing. The
	 * templates manager implementation must ensure that there is always a
	 * default template. Editor managers will use this method on calls of
	 * their  <code>openNewDefaultEditor()</code> method.
	 * 
	 * @return the default template to be used on creation of     a new
	 * 		   document; cannot be null
	 *
	 * @see IEditorManager#openNewDefaultEditor()
	 */
	public DrawingTemplate getDefaultTemplate();

	/**
	 * Sets the new default template.
	 *
	 * @param template the new default template to be used on creation of a
	 * 		  new document
	 */
	public void setDefaultTemplate(DrawingTemplate template);

	/**
	 * Adds a new <code>DrawingTemplate</code> to the list of available
	 * templates.
	 *
	 * @param template the template to add to the lits of editor's
	 * 		  available drawing templates
	 */
	public void add(DrawingTemplate template);

	/**
	 * Removes the parameter <code>DrawingTemplate</code> from the list of
	 * available templates.
	 *
	 * @param template the template to remove from the lits of editor's
	 * 		  available drawing templates
	 */
	public void remove(DrawingTemplate template);

	/**
	 * Returns an unmodifiable list of the <code>DrawingTemplate</code>s 
	 * available to the editor.
	 *
	 * @return a list of available templates
	 */
	public List getTemplates();

	/**
	 * Queries the list of templates to find the template for a specific class
	 * of drawing. The method will first search for the exact template and if such
	 * is not found will return the first compatible template. 
	 * 
	 * @return a template for drawing or <code>null</code>
	 */
	public DrawingTemplate getTemplateFor(org.magelan.drawing.DrawingModel dwg);
}