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
import java.util.List;

import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;


/**
 * Contains and manages {@link DrawingFileHandler}s.  The implementations
 * of this interface are extensively used by save and open methods of
 * <code>IEditorManager</code>s.
 * 
 * <p>
 * In addition to maintaining file handlers, file managers are expected to
 * provide a properly configured  <code>JFileChooser</code> component for the
 * use of the  owning editor manager.
 * </p>
 * 
 * @author	Assen Antov
 * @version	2.0, 05/2004
 *
 * @see DrawingFileHandler
 * @see IEditorManager
 */
public interface IFilesManager {
  
  /**
   * File successfully encoded.
   */
  public static final int SUCCESS = 0;
  
  /**
   * File not supported for encoding.
   */
  public static final int NOT_SUPPORTED = 1;
  
  /**
   * Error writing/encoding the file.
   */
  public static final int ERROR_WRITING = 2;
  
  

	/**
	 * Adds a new <code>DrawingHandler</code> to the list of available
	 * handlers.
	 *
	 * @param handler the handler to add to the lits of  editor's available
	 * 		  handlers
	 */
	public void add(DrawingFileHandler handler);

	/**
	 * Removes the parameter <code>DrawingHandler</code> from the list of
	 * available handlers.
	 *
	 * @param handler the handler to remove from the lits of  editor's
	 * 		  available drawing handlers
	 */
	public void remove(DrawingFileHandler handler);

	/**
	 * Returns a list of the <code>DrawingHandler</code>s available to the
	 * editor.
	 *
	 * @return a list of available handlers
	 */
	public List getFileHandlers();
	 
	/**
	 * The file chooser returned by this method is configured using the data
	 * provided by the <code>IFileManager</code>.
	 *
	 * @return the file chooser of the editor manager
	 *
	 * @see IFilesManager
	 */
	public javax.swing.JFileChooser getFileChooser();

	/**
	 * Writes the parameter <code>DrawingModel</code> to a file.
	 *
	 * @param dwg   <code>DrawingModel</code> instance to encode
	 * @param renderer a <code>DrawingRenderer</code> to use for encoding 
	 * @param file  file to write the drawing to
	 *
	 * @return result of the encoding
	 */
	public int encode(DrawingModel dwg, DrawingRenderer renderer, File file);

	/**
	 * Reads a drawing file and creates a <code>DrawingEditor</code> with 
	 * the decoded <code>DrawingModel</code> set.
	 * 
	 * @param file file to decode    
	 *
	 * @return  a <code>DrawingEditor</code> or 
	 *          <code>null</code> on error
	 */
	public DrawingEditor decode(File file);
  
	/**
	 * Returns a <code>DrawingFileHandler</code> for a given file.
	 * 
	 * @param file file instance to lookup
	 * @return the proper file handler or null
	 */
	public DrawingFileHandler getHandlerFor(File file);
	
	public DrawingFileHandler getHandlerFor(DrawingModel model);
}