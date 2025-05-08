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

import org.magelan.commons.ui.*;
import org.magelan.editor.ui.*;

import java.io.*;
import java.util.*;
import java.awt.Component;

import javax.swing.*;

import java.util.prefs.*;

import org.apache.log4j.*;


/**
 * An extension manager responsible for finding, adding and removing of
 * extensions to the editor.
 *
 * @author	larisa
 * @version 2.2, 03/2005
 */
public class DefaultExtensionManager implements IExtensionsManager {
	
	private static Logger log = Logger.getLogger(DefaultExtensionManager.class);
	private static Preferences prefs = Preferences.userNodeForPackage(DefaultExtensionManager.class);
	
	private List extensions;
	private Map mfMap;
	private boolean busy;
	
	
	/**
	 * A filter for manifest files. Recognizes ".manifest" and ".mf" file
	 * extensions.
	 */
	private FilenameFilter mfFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			if ((new File(dir, name)).isDirectory()) return false;
			return name.toLowerCase().endsWith(".manifest") || name.toLowerCase().endsWith(".mf"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	};
	
	
	/**
	 * Creates a default extension manager and loads the 
	 * available extensions.
	 */
	public DefaultExtensionManager() {
		extensions = new ArrayList();
		String FS = System.getProperty("file.separator"); //$NON-NLS-1$
		String PS = System.getProperty("path.separator"); //$NON-NLS-1$
		
		String EXTENSIONS = "." + FS + "extension" + FS; //$NON-NLS-1$ //$NON-NLS-2$
		String LIB = "." + FS + "lib" + FS; //$NON-NLS-1$ //$NON-NLS-2$
		String LIST = 
			EXTENSIONS + "point.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "line.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "polyline.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "hatch.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "circle.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "text.manifest" + PS + //$NON-NLS-1$
		//	EXTENSIONS + "path.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "ellipse.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "rectangle.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "image.manifest" + PS + //$NON-NLS-1$
			
			EXTENSIONS + "label.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "view.manifest" + PS + //$NON-NLS-1$
			
		//	EXTENSIONS + "delete.manifest" + PS + //$NON-NLS-1$
			
			EXTENSIONS + "zoom_in.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "zoom_out.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "zoom_r.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "pan.manifest" + PS + //$NON-NLS-1$
			
			EXTENSIONS + "layermanager.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "changelayer.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "colors.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "textedit.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "explode.manifest" + PS + //$NON-NLS-1$
			
			EXTENSIONS + "changestyle.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "changethickness.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "text_style_manager.manifest" + PS + //$NON-NLS-1$
		
			EXTENSIONS + "MemMonitor.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "comp_renderer_manager.manifest" + PS + //$NON-NLS-1$
		
			EXTENSIONS + "properties.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "drawing_explorer.manifest" + PS + //$NON-NLS-1$
			EXTENSIONS + "layerexplorer.manifest"; //$NON-NLS-1$
		
		
		/*
		 * Set default values for prefs
		 */
		if (prefs.get("path", "?").equals("?")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			log.debug("Saving default extension preferences"); //$NON-NLS-1$
			
			prefs.putBoolean("autoload", false); //$NON-NLS-1$
			prefs.put("path", EXTENSIONS + PS + LIB); //$NON-NLS-1$
			prefs.put("manifests", LIST); //$NON-NLS-1$
			
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				log.error("Could not save default extension preferences", e); //$NON-NLS-1$
			}
		}
		
		/*
		 * Loads all found extensions if autoload is set.
		 */
		if (prefs.getBoolean("autoload", false)) { //$NON-NLS-1$
			addPath(ClassManifest.getClassPath(prefs.get("path",  //$NON-NLS-1$
					EXTENSIONS + PS + LIB))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		/*
		 * Loads all defined extensions.
		 */
		else {
			add(ClassManifest.getClassPath(prefs.get("manifests", LIST))); //$NON-NLS-1$
		}
	}
	
	
	public void addPath(List mfpath) {
		log.debug("Manifest Paths: " + mfpath); //$NON-NLS-1$
		// read all manifest file names
		List manifests = new ArrayList();
		Iterator iter = mfpath.iterator();
		while (iter.hasNext()) {
			File[] list = (new File((String) iter.next())).listFiles(mfFilter);
			if (list == null || list.length == 0) {
				continue;
			}
			
			for (int i = 0; i < list.length; i++)
				manifests.add(list[i]);
		}
		
		log.debug("Manifests found: " + manifests); //$NON-NLS-1$
		// now load all found extensions
		iter = manifests.iterator();
		while (iter.hasNext()) {
			add((File) iter.next());
		}
	}
	
	public void add(List manifests) {
		if (manifests == null) {
			return;
		}
		
		// load all extensions
		try {
			Iterator iter = manifests.iterator();
			while (iter.hasNext()) {
				add(new File((String) iter.next()));
			}
		} catch (Throwable e) {
			log.error("", e); //$NON-NLS-1$
		}
	}
	
	public List getExtensions() {
		return Collections.unmodifiableList(extensions);
	}
	
	/**
	 * @todo	register the extension to listen for all necessary
	 */
	public void add(AbstractEditorExtension extension) {
		log.debug("Adding: " + (String) extension.getValue(AbstractEditorExtension.NAME)); //$NON-NLS-1$
		extensions.add(extension);
		
		/*
		 * Adds the extension to the extension toolbar
		 */
		EditorToolBar extBar = Editor.getEditorManager().getExtensionToolbar();
		if (extension.getComponent() == null) {
			extBar.add(extension);
		}
		else {
			if (extension.getComponent() instanceof JScrollPane ||
					extension.getComponent() instanceof JPanel) {
				
				JTabbedPane extTab = Editor.getEditorManager().getExtensionTab();
				extTab.addTab(
						(String) extension.getValue(AbstractEditorExtension.NAME), 
						(ImageIcon) extension.getValue(AbstractEditorExtension.SMALL_ICON),
						extension.getComponent()
				);
			}
			else {
				extBar.add(
						(String) extension.getValue(AbstractEditorExtension.GROUP), 
						(JComponent) extension.getComponent()
				);
			}
			return;
		}
		
		/*
		 * Adds the extensions to the menu bar.
		 */
		EditorMenu extMenu = Editor.getEditorManager().getExtensionMenu();
		
		String name = (String) extension.getValue(AbstractEditorExtension.GROUP);
		if (name == null) {
			extMenu.add(UIFactory.createMenuItem(extension));
			return;
		}
		
		boolean found = false;
		JMenu group = null;
		Component[] items = extMenu.getMenuComponents();
		
		for (int i = 0; i < items.length; i++) {
			if (items[i] instanceof JMenu) {
				group = (JMenu) items[i];
				
				if (group != null && name.equals(group.getText())) {
					found = true;
					group.add(UIFactory.createMenuItem(extension));
				}
			}
		}
		
		if (!found) {
			group = new JMenu(name);
			group.setIcon(Icons.GAP_ICON_16);
			group.add(UIFactory.createMenuItem(extension));
			extMenu.add(group);
		}
	}
	
	public void remove(AbstractEditorExtension extension) {
		extensions.remove(extension);
		
		// remove the extension from the extension toolbar
		EditorToolBar extBar = Editor.getEditorManager().getExtensionToolbar();
		
		/*
		 * TODO: iterate all buttons and compare their actions; still
		 * not good enough as they might be modified.
		 */
	}
	
	public void add(File mf) {
		// create class manifest
		ClassManifest cmf = new ClassManifest(mf);
		
		// instantiate the extension
		Object ext = cmf.instantiate();
		
		if (ext != null) {
			// add to the map of extension/manifest pairs
			if (mfMap == null) {
				mfMap = new HashMap();
			}
			mfMap.put(ext, cmf);
			
			if (ext instanceof AbstractEditorExtension) {
				// add the extension to the editor
				add((AbstractEditorExtension) ext); // adds the extension
			}
			else {
				log.debug("Instantiated: " + (String) ext.getClass().getName()); //$NON-NLS-1$
			}
		}
	}
	
	public ClassManifest getManifestFor(AbstractEditorExtension extension) {
		return (ClassManifest) mfMap.get(extension);
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
}