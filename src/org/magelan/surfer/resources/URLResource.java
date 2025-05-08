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
package org.magelan.surfer.resources;

import java.awt.Component;
import java.net.URL;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.magelan.commons.ui.Icons;
import org.magelan.surfer.Resource;

/**
 * 
 * <p>
 * @version	1.0, 2003-7-13
 * @author	Assen Antov
 */
public class URLResource implements Resource {
	
	private static URLResource ur;
	
	private URLResource() {}
	
	/**
	 * Instantiate the resource descriptor.
	 * <p>
	 * @return	resource descriptor
	 */
	public static URLResource getInstance() {
		if (ur == null) {
			ur = new URLResource();
		}
		return ur;
	}
	
	public boolean isSupported(Object obj) {
		return obj instanceof URL;
	}

	public String getTextFor(Object obj) {
		return obj.toString();
	}
	
	public boolean isValid(Object obj) {
		return true;	// TODO
	}
	
	public String getToolTipFor(Object obj) {
		if (!isValid(obj)) {
			return "Could not find resoure!";
		}
		return ((URL) obj).toExternalForm();
	}
	
	public Icon getIconFor(Object obj) {
		return Icons.HTML_ICON;
	}
	
	public Component getViewerFor(DefaultMutableTreeNode node) {
		return null;
	}
	
	public Component getEditorFor(DefaultMutableTreeNode node) {
		return null;
	}
	
	public Action[] getEditActionsFor(DefaultMutableTreeNode node) {
		return null;
	}
	
	public Action[] getViewActionsFor(DefaultMutableTreeNode node) {
		return null;
	}
	
	public Action getCreateActionFor(DefaultMutableTreeNode node) {
		return null;
	}
}
