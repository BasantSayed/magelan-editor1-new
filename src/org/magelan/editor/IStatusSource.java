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

import org.magelan.editor.ui.*;


/**
 * IStatusSource Interface represent an object that provides StausBar with
 * customization and status strings.
 *
 * @author larisa
 */
public interface IStatusSource {
	//~ Methods ----------------------------------------------------------------

	/**
	 * Notifies listener (statusBar) about Status change
	 */
	public abstract void fireStatusChanged(String dest, String status);

	/**
	 * Configure Satus bar with new panels
	 *
	 * @param statusBar
	 */
	public void configStatusListener(StatusBar statusBar);

	/**
	 * Remove custom panels from status bar
	 *
	 * @param statusBar
	 */
	public void deConfigStatusListener(StatusBar statusBar);

	/**
	 * Activates a status bar make sure that required panels are visible  and
	 * have an appropriate messages.
	 *
	 * @param statusBar
	 */
	public void activateStatusListener(StatusBar statusBar);

	/**
	 * DeActivates a status bar - hides panels.
	 *
	 * @param statusBar
	 */
	public void deActivateStatusListener(StatusBar statusBar);
}