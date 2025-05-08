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
package org.magelan.editor.util;

import java.awt.Color;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.editor.Snap;
import org.magelan.editor.Editor;
import org.magelan.editor.IEditorManager;

/**
 * 
 * @version	1.0, 01/2004
 * @author	Assen Antov
 */
public class EditorUtil {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Logger log = Logger.getLogger(EditorUtil.class);
	private static Preferences editorPrefs = Preferences.userNodeForPackage(DrawingEditor.class);


	/**
	 * Configures a <code>DrawingEditor</code>.
	 *
	 * @param e the editor to configure
	 */
	public static void configure(DrawingEditor e) {
		
		/*
		 * Editor configuration
		 */
		e.putValue(DrawingEditor.KEY_BACKGROUND_COLOR,
			Color.decode(editorPrefs.get(DrawingEditor.KEY_BACKGROUND_COLOR, "0x000000")) //$NON-NLS-1$
		);
		e.putValue(DrawingEditor.KEY_HANDLE_COLOR,
			Color.decode(editorPrefs.get(DrawingEditor.KEY_HANDLE_COLOR, "0xFF6000")) //$NON-NLS-1$
		);
		e.putValue(DrawingEditor.KEY_HANDLE_SIZE,
			new Integer(editorPrefs.getInt(DrawingEditor.KEY_HANDLE_SIZE, 8))
		);
		e.putValue(DrawingEditor.KEY_SELECTION_COLOR,
			Color.decode(editorPrefs.get(DrawingEditor.KEY_SELECTION_COLOR, "0xCCCCCC")) //$NON-NLS-1$
		);
		
		/*
		 * Snap configuration
		 */
		Snap snap = e.getSnap();
		if (snap != null) {
			snap.setEnabled(editorPrefs.getBoolean("snap.enabled", true)); //$NON-NLS-1$
			snap.setSnapDistance(editorPrefs.getInt("snap.distance", 8)); //$NON-NLS-1$
			snap.setSize(editorPrefs.getInt("snap.size", 11)); //$NON-NLS-1$
			snap.setColor(Color.decode(editorPrefs.get("snap.color", "0xFFFF00"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Stores the configuration of a <code>DrawingEditor</code>.
	 *
	 * @param e the editor to store its configuration
	 */
	public static void storeConfiguration(DrawingEditor e) {
		
		/*
		 * Editor configuration
		 */
		editorPrefs.put(DrawingEditor.KEY_BACKGROUND_COLOR, 
			SwingUtil.color2Hex((Color) e.getValue(DrawingEditor.KEY_BACKGROUND_COLOR)));
		editorPrefs.put(DrawingEditor.KEY_HANDLE_COLOR, 
			SwingUtil.color2Hex((Color) e.getValue(DrawingEditor.KEY_HANDLE_COLOR)));
		editorPrefs.put(DrawingEditor.KEY_HANDLE_SIZE, e.getValue(DrawingEditor.KEY_HANDLE_SIZE).toString());
		editorPrefs.put(DrawingEditor.KEY_SELECTION_COLOR, 
			SwingUtil.color2Hex((Color) e.getValue(DrawingEditor.KEY_SELECTION_COLOR)));
		
		/*
		 * Snap configuration
		 */
		Snap snap = e.getSnap();
		if (snap != null) {
			editorPrefs.putBoolean("snap.enabled", snap.getEnabled()); //$NON-NLS-1$
			editorPrefs.putInt("snap.distance", snap.getSnapDistance()); //$NON-NLS-1$
			editorPrefs.putInt("snap.draw.distance", snap.getDrawDistance()); //$NON-NLS-1$
			editorPrefs.putInt("snap.size", snap.getSize()); //$NON-NLS-1$
			editorPrefs.put("snap.color", SwingUtil.color2Hex(snap.getColor())); //$NON-NLS-1$
		}
	}
	
	/**
	 * Changes the L&F of the editor. Updates the L&F property accordingly.
	 * 
	 * @param	laf	L&F class name to set
	 */
	public static boolean setLookAndFeel(String laf) {
		try {
			IEditorManager m = Editor.getEditorManager();
			/*
			// menu bar visual settings
			m.getJMenuBar().putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
			m.getJMenuBar().putClientProperty(PlasticLookAndFeel.BORDER_STYLE_KEY, BorderStyle.SEPARATOR);
			m.getJMenuBar().putClientProperty(PlasticLookAndFeel.IS_3D_KEY, Boolean.FALSE);
			
			m.getEditorToolbar().putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
			//m.getEditorToolbar().putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
			m.getEditorToolbar().putClientProperty(PlasticLookAndFeel.IS_3D_KEY, Boolean.FALSE);
			
			m.getExtensionToolbar().putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
			m.getExtensionToolbar().putClientProperty(PlasticLookAndFeel.IS_3D_KEY, Boolean.FALSE);
			*/
			UIManager.put("jgoodies.useNarrowButtons", Boolean.TRUE); //$NON-NLS-1$
			//m.getExtensionTab().putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.FALSE);
			//m.getExtensionTab().putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
			//Options.setTabIconsEnabled(false);

			UIManager.setLookAndFeel(laf);

			// update components UI
			if (m != null && m.getMainFrame() != null) {
				SwingUtilities.updateComponentTreeUI(m.getMainFrame());
				SwingUtilities.updateComponentTreeUI(
						Editor.getFilesManager().getFileChooser());
			}

			Preferences.userNodeForPackage(Editor.class).
				put("lookandfeel", laf); //$NON-NLS-1$

		} catch (ClassNotFoundException e) {
			log.warn("Failed to find Look & Feel class \"" + laf + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showErrorDialog(lang.getString("SwingUtil.laf.exception.classNotFound"), e); //$NON-NLS-1$
			return false;
		} catch (InstantiationException e2) {
			log.warn("Failed to instantiate Look & Feel \"" + laf + "\"", e2); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showErrorDialog(lang.getString("SwingUtil.laf.exception.instantiation"), e2); //$NON-NLS-1$
			return false;
		} catch (Throwable e3) {
			log.warn("Failed to load Look & Feel \"" + laf + "\"", e3); //$NON-NLS-1$ //$NON-NLS-2$
			org.magelan.commons.ui.ErrorDialog.showErrorDialog(lang.getString("SwingUtil.laf.exception.other"), e3); //$NON-NLS-1$
			return false;
		}

		return true;
	}
}