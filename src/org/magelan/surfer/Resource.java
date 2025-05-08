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
package org.magelan.surfer;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * A resource descriptor. The information returned by the methods of this class
 * will be used by the cell renderers of <code>DataTree</code>s. 
 * <p>
 * Note that by convention each resource descriptor class should have only 
 * one instance, obtained using a static <code>getInstance</code> method. 
 * 
 * @version	1.1, 02/2004
 * @author	Assen Antov
 */
public interface Resource {

	/**
	 * Determines whether or not the parameter object is supported by
	 * this resource descriptor.
	 * 
	 * @param	obj	resource instance
	 */
	public boolean isSupported(Object obj);

	/**
	 * Get display text for the resource object.
	 * 
	 * @param	obj	resource instance
	 */
	public String getTextFor(Object obj);
	
	/**
	 * Determines whether the object represents a valid resource. For example
	 * the existance of a resource file or link could be checked.
	 * 
	 * @param	obj	resource instance
	 * @return	resource data validity
	 */
	public boolean isValid(Object obj);

	/**
	 * A tool tip text to be displayed for the resource data.
	 * 
	 * @param	obj	resource instance
	 * @return	resource tool tip text
	 */
	public String getToolTipFor(Object obj);

	/**
	 * An icon to be displayed for the resource data.
	 * 
	 * @param	obj	resource instance
	 * @return	resource icon
	 */
	public Icon getIconFor(Object obj);

	/**
	 * Returns a component that could be used to view the resource.
	 * 
	 * @param	node	a mutable tree node, containing the resource instance
	 * @return	viewer <code>Component</code>
	 */
	public Component getViewerFor(DefaultMutableTreeNode node);

	/**
	 * Returns a component to edit the resource.
	 * 
	 * @param	node	a mutable tree node, containing the resource instance
	 * @return	viewer <code>Component</code>
	 */
	public Component getEditorFor(DefaultMutableTreeNode node);

	/**
	 * Returns a set of <code>Action</code>s to be used for editing resource 
	 * data. The actions are to be managed by the <code>DataTree</code> 
	 * container.
	 *
	 * @param	node	a mutable tree node, containing the resource instance
	 * @return	an array of <code>Action</code>s
	 */
	public Action[] getEditActionsFor(DefaultMutableTreeNode node);

	/**
	 * Returns a set of <code>Action</code>s to be used for viewing resource 
	 * data. The actions are to be managed by the <code>DataTree</code> 
	 * container.
	 *
	 * @param	node	a mutable tree node, containing the resource instance
	 * @return	an array of <code>Action</code>s
	 */
	public Action[] getViewActionsFor(DefaultMutableTreeNode node);
	
	/**
	 * Returns an <code>Action</code> capable of creating a new node containing 
	 * data of the type supported by this resource descriptor. The new node 
	 * should be then added by the action to the parameter node. The action is 
	 * to be managed by the <code>DataTree</code>.
	 *
	 * @param	node	a mutable tree node, containing the resource instance
	 * @return	an <code>Action</code> to create a new node
	 */
	public Action getCreateActionFor(DefaultMutableTreeNode node);
}
