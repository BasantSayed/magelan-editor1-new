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

import org.magelan.commons.ui.*;
import org.magelan.surfer.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * File resource descriptor.
 * 
 * @version	1.0, 2003-7-13
 * @author	Assen Antov
 */
public class FileResource implements Resource {
	
	private static FileResource fr;
	
	private FileResource() {}
	
	/**
	 * Instantiate the resource descriptor.
	 * 
	 * @return	resource descriptor
	 */
	public static FileResource getInstance() {
		if (fr == null) {
			fr = new FileResource();
		}
		return fr;
	}
	
	public boolean isSupported(Object obj) {
		return obj instanceof File;
	}

	public String getTextFor(Object obj) {
		return obj.toString();
	}
	
	public boolean isValid(Object obj) {
		return ((File) obj).exists();
	}
	
	public String getToolTipFor(Object obj) {
		if (!isValid(obj)) {
			return "Could not find resoure!";
		}
		return ((File) obj).getAbsolutePath();
	}
	
	public Icon getIconFor(Object obj) {
		/*
		 * Does exist?
		 */
		if (!isValid(obj)) {
			return Icons.ERROR_ICON;
		}
		
		else {
			if (((java.io.File) obj).isDirectory()) {
				return Icons.PATH_ICON;
			}
			
			else {
				String ext = getExtension(obj.toString());
				if (ext.equals("html") || ext.equals("htm")) {
					return Icons.HTML_ICON;
				}
				else if (ext.equals("dxf")) {
					return Icons.ACAD_ICON;
				}
				else if (ext.equals("jar")) {
					return Icons.JAR_ICON;
				}
				else if (ext.equals("drawing")) {
					return Icons.MAGELAN_ICON;
				}
			}
		}
		
		return Icons.NEW_ICON;
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
	
	private String getExtension(String s) {
		if (s.indexOf('.') == -1) return "";
		return s.toLowerCase().substring(s.indexOf(".")+1, s.length());
	}
	
	public Action getCreateActionFor(DefaultMutableTreeNode node) {
		return null;
	}
}

