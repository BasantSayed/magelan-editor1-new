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

import java.io.File;
import java.util.*;


/**
 * Manages the extensions. Defines methods to register extensions to the editor.
 *
 * @version	2.0, 08/2004
 * @author larisa
 */
public interface IExtensionsManager {

	/**
	 * Returns an unmodifiable list of the loaded extensions. To add or remove 
	 * extensions use the add or remove methods of the manager class.
	 *
	 * @return a list of all loaded extensions
	 */
	public List getExtensions();

	/**
	 * Adds an extension to the editor.
	 *
	 * @param extension extension to load
	 */
	public void add(AbstractEditorExtension extension);

	/**
	 * Adds an extension to the editor, declared by a manifest file.
	 *
	 * @param mf extension's manifest file
	 */
	public void add(File mf);
	
	/**
	 * Adds all extensions with manifest files found in the  parameter list of
	 * paths.
	 *
	 * @param mfpath path strings where manifest files are supposed  to be
	 * 		  found
	 * @see ClassManifest
	 */
	public void addPath(List mfpath);

	/**
	 * Adds all extensions declared by the parameter manifest files.
	 *
	 * @param manifests list of manifest files
	 * @see ClassManifest
	 */
	public void add(List manifests);

	/**
	 * Removes an extension.
	 *
	 * @param extension the extension to remove
	 */
	public void remove(AbstractEditorExtension extension);
	
	/**
	 * Returns the <code>ClassManifest</code> that was used to declare the extension.
	 * Note that is an extension is added using the 
	 * {@link #add(AbstractEditorExtension)} method, the <code>getManifestFor</code>
	 * method will not be able to locate its manifest and will return <code>null</code>.
	 * Where possible use of the {@link #add(File)} method is recommended.
	 * 
	 * @param extension extension to lookup
	 * @return a <code>ClassManifest</code> or <code>null</code>
	 */
	public ClassManifest getManifestFor(AbstractEditorExtension extension);
	
	/**
	 * Checks whether there is an extension runing.
	 * 
	 * @return the busy status
	 */
	public boolean isBusy();
	
	/**
	 * 
	 * @param busy the new value of the busy to set
	 */
	public void setBusy(boolean busy);
}