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
package org.magelan.editor.file.svg;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.CoreModel;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.editor.DefaultDrawingEditor;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;
import org.magelan.editor.DrawingFileHandler;
import org.magelan.editor.Editor;
import org.magelan.editor.file.FileException;

/**
 * The SVG file handler that presents filters, views, encoder and
 * decoder to operate with <code>SVG</code> files.
 *
 * @author Bernard Desprez
 * @version 1.0, sept 2003
 */
public class SVGFileHandler extends DrawingFileHandler {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Logger log = Logger.getLogger(SVGFileHandler.class);


	public String getName() {
		return lang.getString("SVGFileHandler.name"); //$NON-NLS-1$
	}

	public String getTypeDescription() {
		return lang.getString("SVGFileHandler.descr"); //$NON-NLS-1$
	}

	public ImageIcon getIcon() {
		return (ImageIcon) Icons.NEW_ICON;
	}

	public FileFilter getFileFilter() {
		return new DefaultFileFilter("svg", lang.getString("SVGFileHandler.filter")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean isSupported(File f) {
		String ext = ""; //$NON-NLS-1$
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');

			if (i > 0 && i < filename.length() - 1) {
				ext = filename.substring(i + 1).toLowerCase();
			}
		}

		if ("svg".equals(ext)) { //$NON-NLS-1$
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Decode an SVG drawing.
	 */
	public DrawingEditor decode(InputStream in, File file) throws FileException {
		CoreModel dwg = new DefaultCoreModel();
		DrawingEditor editor = new DefaultDrawingEditor();
		editor.setModel(dwg);
		
		SVGDecoder SVGD = new SVGDecoder();  // static pb
		try {
			SVGD.decode(in, dwg,editor);
		} catch (Throwable e) {
			throw new FileException("error decoding file", e); //$NON-NLS-1$
		}
		
		editor.repaint();
		return editor; 
	}

	public boolean isSupported(DrawingModel dwg) {
		return true;
	}

	/**
	 * Encode a drawing to SVG.
	 */
	public void encode(DrawingModel dwg, DrawingRenderer renderer, OutputStream out) throws FileException {
		try {
			SVGEncoder.encode(dwg, renderer, out);
		} catch (Throwable e) {
			throw new FileException("error encoding file", e); //$NON-NLS-1$
		}
		

	}
}

