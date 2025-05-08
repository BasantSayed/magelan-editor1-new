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
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;
import org.magelan.editor.file.FileException;


/**
 * <code>DrawingFileHandler</code>s are responsible for opening and saving
 * (further called encoding and decoding)  of drawings, as well as their
 * proper viewing in   <code>JFileChooser</code> components.
 * 
 * <p>
 * File handlers provide name, icon and description of the described
 * file/drawing type.
 * </p>
 *
 * @author Assen Antov
 * @version 1.3, 05/2007
 */
public abstract class DrawingFileHandler {

	/**
	 * Returns the display name of the file handler
	 *
	 * @return the name of the handler
	 */
	public abstract String getName();

	/**
	 * Handler's 16x16 icon.
	 *
	 * @return representing icon of the handler
	 */
	public abstract ImageIcon getIcon();

	/**
	 * Provides a description of the represented file/drawing type.
	 *
	 * @return a short description of the file type
	 */
	public abstract String getTypeDescription();

	/**
	 * Provision of a <code>FileFilter</code> for use in
	 * <code>JFileChooser</code> components.
	 *
	 * @return file filter to add to editor's file chooser
	 */
	public abstract FileFilter getFileFilter();

	/*
	 * Decoder
	 */

	/**
	 * Determines whether the given drawing type is supported 
	 * for decoding or not. The method will at least test the name
	 * of the file, but may also attempt to determine its type
	 * by reading parts of it.
	 *
	 * @param file a drawing file
	 * @return if this type of drawing is supported
	 */
	public abstract boolean isSupported(File file);

	/**
	 * Decodes a drawing instance from the given input stream
	 * and creates the corresponding editor instance for it.
	 *
	 * @param   in an input stream to read from
	 * @param	file the file to be decoded (for information)
	 * @return  editor with the decoded drawing set
	 * @since	1.3
	 */
	public abstract DrawingEditor decode(InputStream in, File file) throws FileException;


	/*
	 * Encoder
	 */

	/**
	 * Determines wheteher the given drawing type is supported for
	 * encoding or not.
	 * 
	 * @param dwg a drawing instance
	 * @return if this type of drawing is supported
	 */
	public abstract boolean isSupported(DrawingModel dwg);

	/**
	 * Encodes the given drawing instance to an output stream.
	 *
	 * @param dwg drawing model to encode
	 * @param renderer model renderer to use
	 * @param out an output stream to write to
	 */
	public abstract void encode(DrawingModel dwg, DrawingRenderer renderer, 
			java.io.OutputStream out) throws FileException;


	/**
	 * Class to build custom file filters.
	 */
	public class DefaultFileFilter extends FileFilter {

		String extension;
		String descr;


		/**
		 * Creates a new DefaultFileFilter object.
		 *
		 * @param	extension	file extension
		 * @param	descr			description for the file type
		 */
		public DefaultFileFilter(String extension, String descr) {
			this.extension = extension;
			this.descr = descr;
		}


		/**
		 * Determines whether to show files of the given type.
		 *
		 * @param		f	file type to test
		 * @return	true if the file type should be shown
		 */
		public boolean accept(java.io.File f) {
			String ext = ""; //$NON-NLS-1$
			if (f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('.');
				if (i > 0 && i < filename.length() - 1) {
					ext = filename.substring(i + 1).toLowerCase();
				}
			}

			if (extension.equals(ext) || f.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Description for the files of that type.
		 *
		 * @return	file type description
		 */
		public String getDescription() {
			return descr;
		}
	}
}