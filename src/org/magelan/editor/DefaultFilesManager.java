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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.ProgressInputStream;
import org.magelan.commons.ui.ProgressPanel;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;


/**
 * A default implementation of a <code>IFilesManager</code>.
 *
 * @author Assen Antov
 * @version 2.1, 08/2004
 *
 * @see IFilesManager
 */
public class DefaultFilesManager implements IFilesManager {

	private static Logger log = Logger.getLogger(DefaultFilesManager.class);
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private List handlers;
	private javax.swing.JFileChooser fileChooser;

	/** A configured file view. */
	private javax.swing.filechooser.FileView view = new javax.swing.filechooser.FileView() {
		public String getName(File f) {
			return null;
		}

		public String getDescription(File f) {
			DrawingFileHandler h = supportedBy(f);
			if (h != null) {
				return h.getTypeDescription();
			}

			return null;
		}

		public String getTypeDescription(File f) {
			DrawingFileHandler h = supportedBy(f);
			if (h != null) {
				return h.getTypeDescription();
			}

			return null;
		}

		public javax.swing.Icon getIcon(File f) {
			DrawingFileHandler h = supportedBy(f);
			if (h != null) {
				return h.getIcon();
			}

			return null;
		}
	};


	/**
	 * Constructs the file manager and adds a
	 * <code>DefaultDrawingFileHandler</code> to it.
	 *
	 * @see DefaultDrawingFileHandler
	 */
	public DefaultFilesManager() {
		handlers = new ArrayList();
		add(new org.magelan.editor.file.dxf.DXFFileHandler());
		add(new org.magelan.editor.file.eps.EPSFileHandler());
		add(new org.magelan.editor.file.svg.SVGFileHandler());
		//add(new DefaultDrawingFileHandler());
		//add(new org.magelan.editor.file.binary.BinaryFileHandler());
		add((DrawingFileHandler) Editor.instantiateClass("org.magelan.editor.file.binary.BinaryFileHandler", Editor.getInstance().getClassLoader()));
	}


	public void add(DrawingFileHandler handler) {
		handlers.add(handler);

		//getFileChooser().setFileFilter(handler.getFileFilter());
	}

	/**
	 * @todo remove file filter
	 */
	public void remove(DrawingFileHandler handler) {
		handlers.remove(handler);
	}

	public List getFileHandlers() {
		return Collections.unmodifiableList(handlers);
	}

	/**
	 * Returns a configured <code>FileView</code> for use by the
	 * <code>JFileChooser</code> component of the owning editor  manager.
	 * 
	 * @return a <code>FileView</code> instance
	 *
	 * @see IFilesManager#getFileChooser
	 */
	private javax.swing.filechooser.FileView getFileView() {
		return view;
	}

	public javax.swing.JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new javax.swing.JFileChooser();

			Iterator i = handlers.iterator();
			while (i.hasNext())
				fileChooser.setFileFilter(((DrawingFileHandler) i.next()).getFileFilter());

			fileChooser.setFileView(getFileView());
		}

		return fileChooser;
	}

	public int encode(DrawingModel model, DrawingRenderer renderer, File file) {
		DrawingFileHandler h = supportedBy(file);
		if (h == null) {
			log.info("File format not supported for encoding \"" + file + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showInfoDialog(
				lang.getString("DefaultFilesManager.encodingNotSupported") + ": " + file //$NON-NLS-1$ //$NON-NLS-2$
			);
			return NOT_SUPPORTED;
		}

		long time = System.currentTimeMillis();
		JDialog pd = null;
		
		try {
			log.info("Start encoding to file \"" + file + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			
			pd = ProgressPanel.createProgressDialog(Editor.getEditorManager().getMainFrame(), lang.getString("DefaultFilesManager.progress.writing") + file.getName()); //$NON-NLS-1$
			pd.setVisible(true);
			
			h.encode(model, renderer, new FileOutputStream(file));
			
			pd.setVisible(false);
		} catch (Throwable e) {
			log.error("unexpected error encoding file \"" + file + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showErrorDialog(
				lang.getString("DefaultFilesManager.errorEncoding") + ": " + file, //$NON-NLS-1$ //$NON-NLS-2$
				e);
			pd.setVisible(false);
			return ERROR_WRITING;
		}
	
		log.info("Completed encoding in " +  //$NON-NLS-1$
			(System.currentTimeMillis() - time) + " ms"); //$NON-NLS-1$
		return SUCCESS;
	}

	public DrawingEditor decode(File file) {
		DrawingFileHandler h = supportedBy(file);
		if (h == null) {
			log.info("File format not supported for decoding \"" + file + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showInfoDialog(
				lang.getString("DefaultFilesManager.decodingNotSupported") + ": " + file //$NON-NLS-1$ //$NON-NLS-2$
			);
			return null;
		}
		
		long time = System.currentTimeMillis();
		
		DrawingEditor ed = null;
		JDialog pd = null;
		try {
			log.info("Start decoding file \"" + file + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			
			// configure progress dialog
			ProgressInputStream pm = new ProgressInputStream(new FileInputStream(file));
			pd = ProgressPanel.createProgressDialog(Editor.getEditorManager().getMainFrame(), lang.getString("DefaultFilesManager.progress.reading") + file.getName(), pm.getBoundedRangeModel()); //$NON-NLS-1$
			pd.setVisible(true);
			
			ed = h.decode(pm, file);

			pd.setVisible(false);
		} catch (Throwable e) {
			log.error("unexpected error decoding file \"" + file + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showErrorDialog(
				lang.getString("DefaultFilesManager.errorDecoding") + ": " + file, //$NON-NLS-1$ //$NON-NLS-2$
				e);
			if (pd != null) pd.setVisible(false);
			return null;
		}
		
		log.info("Completed decoding in " +  //$NON-NLS-1$
			(System.currentTimeMillis() - time) + " ms"); //$NON-NLS-1$
		return ed;
	}

	public DrawingFileHandler getHandlerFor(File file) {
		return supportedBy(file);
	}

	public DrawingFileHandler getHandlerFor(DrawingModel model) {
		return supportedBy(model);
	}

	private DrawingFileHandler supportedBy(File f) {
		Iterator iter = handlers.iterator();
		while (iter.hasNext()) {
			DrawingFileHandler h = (DrawingFileHandler) iter.next();
			if (h.isSupported(f)) {
				return h;
			}
		}

		return null;
	}

	private DrawingFileHandler supportedBy(DrawingModel d) {
		Iterator iter = handlers.iterator();
		while (iter.hasNext()) {
			DrawingFileHandler h = (DrawingFileHandler) iter.next();
			if (h.isSupported(d)) {
				return h;
			}
		}

		return null;
	}
}