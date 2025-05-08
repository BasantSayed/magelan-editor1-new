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

import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.AddRemovePanel;
import org.magelan.commons.ui.ClasspathPanel;
import org.magelan.commons.ui.TipsDialog;
import org.magelan.editor.ui.SplashScreen;


/**
 * <code>Editor</code> is the main class that is used to construct an editor.
 * There are two modes supported by editor - single frame mode and multi
 * frame mode. To construct an Editor - use one of the factory methods:
 * {@link #createSingleFrameInstance()} or {@link #createMultiFrameInstance()}.
 * 
 * <p>After <code>Editor</code> has been created it can be accesed using 
 * {@link #getInstance()} method. Editor is a singleton.  The editor 
 * can be configured by accessing its {@link TemplateManager}
 * and {@link ExtensionManager}.
 * </p>
 * <p>To show the editor frame and start work, use the {@link #start()} method.
 * </p>
 * <p>As of 1.1 the current manager can be exitted using the {@link #shutdown()} 
 * method; <code>start()</code> can be called again afterwards.
 * </p>
 * <p>As of 2.3 the editor and all manager classes are loaded using the 
 * user-defined classpath.
 * </p>
 * 
 * @author	larisa
 * @author	Assen Antov
 * @version	2.3, 02/2005
 * @see		IEditorManager
 */
public class Editor {
	
	private static Logger log = Logger.getLogger(Editor.class);
	private static Preferences prefs = Preferences.userNodeForPackage(Editor.class);

	/**
	 * Version marker.
	 */
	public static final String VERSION = "1.0 RC4"; //$NON-NLS-1$

	/**
	 * Name of the file errors are dumped to.
	 */
	public static final String ERROR_DUMP = "exception.log"; //$NON-NLS-1$
	
	/**
	 * Resource bundle name for all strings.
	 */
	public static String STRINGS = "org.magelan.editor.magelan"; //$NON-NLS-1$
	
	private static Lang lang = Lang.getLang(STRINGS);
	
	/**	Single frame mode */
	public static final int SINGLE_FRAME = 1;
	
	/**	Multiple frame mode */
	public static final int MULTI_FRAME = 2;

	/** The singleton instance of the Editor */
	private static Editor editor;

	private ClassLoader loader;
	
	/**
	 * Editor manager that does all the job
	 */
	private IEditorManager manager;

	private IExtensionsManager extManager;
	private ITemplatesManager templManager;
	private IFilesManager filesManager;
	private IFeaturesManager featuresManager;
	

	/**
	 * Private constructor - use <code>createSingleFrameInstance()</code> or
	 * <code>createMultiFrameInstance()</code> to create an Editor and 
	 * <code>getIntsnce()</code> to obtain an instance of Editor.
	 */
	private Editor() {
		log.debug("<init>;"); //$NON-NLS-1$
		
		//Thread.currentThread().setContextClassLoader(getClassLoader());
		
		/*
		 * Dump exceptions to a file
		 */
		try {
			PrintStream err = new PrintStream(new FileOutputStream(ERROR_DUMP), true);
			System.setErr(err);
		} catch (FileNotFoundException e) {
			log.warn("could not create exception log file: " + ERROR_DUMP); //$NON-NLS-1$
		}
	}
	

	/**
	 * Creates an editor instance.
	 * 
	 * @param mode SINGLE_FRAME or MULTI_FRAME
	 *
	 * @throws	IllegalArgumentException	when the mode is not supported
	 * @throws	IllegalStateException	when there is already an editor created
	 * 
	 * @since	1.1
	 */
	private static synchronized void create(int mode) {
		log.debug("create("+mode+");"); //$NON-NLS-1$ //$NON-NLS-2$

		ClassLoader cl = createClassLoader();
		
		/*
		 * Create an Editor instance, if necessary
		 */
		if (editor == null) {
			try {
				editor = (Editor) instantiateClass("org.magelan.editor.Editor", cl);
			} catch (Throwable thr) {
				org.magelan.commons.ui.ErrorDialog.showFatalDialog(
					"Cannot load main Editor class. Please see the details below for more information. " + //$NON-NLS-1$
					"This is a fatal error and the program cannot run. " + //$NON-NLS-1$
					"Please report it by copying the error details to the form at the " + //$NON-NLS-1$
					"URL below.", //$NON-NLS-1$
					thr, false);
			}
			
			//editor = new Editor();
		}
		
		/*
		 * Instantiate an editor manager
		 */
		if (editor.manager == null) {

			if (mode == SINGLE_FRAME) {
				try {
					editor.manager = (IEditorManager) instantiateClass("org.magelan.editor.SingleFrameEditorManager", cl);
				} catch (Throwable thr) {
					org.magelan.commons.ui.ErrorDialog.showFatalDialog(
						"Cannot load Editor Manager class. Please see the details below for more information. " + //$NON-NLS-1$
						"This is a fatal error and the program cannot run. " + //$NON-NLS-1$
						"Please report it by copying the error details to the form at the " + //$NON-NLS-1$
						"URL below.", //$NON-NLS-1$
						thr, false);
				}
				
				// editor.manager = new SingleFrameEditorManager();
			}
			
			else if (mode == MULTI_FRAME) {
				log.error("MULTI_FRAME editor mode not yet implemented!"); //$NON-NLS-1$
				throw new IllegalArgumentException("MULTI_FRAME editor mode not yet implemented!"); //$NON-NLS-1$
			}
			
			else {
				log.error("Unsupported instantiation mode: " + mode); //$NON-NLS-1$
				throw new IllegalArgumentException("Unsupported instantiation mode: " + mode); //$NON-NLS-1$
			}
		} else {
			log.error("To create a new editor manager call shutdown() first"); //$NON-NLS-1$
			throw new IllegalStateException("To create a new editor manager call shutdown() first"); //$NON-NLS-1$
		}
		
		/*
		 * ErrorDialog settings
		 */
		System.setProperty("magelan.version", VERSION); //$NON-NLS-1$
		System.setProperty("magelan.manager", editor.manager.getClass().getName()); //$NON-NLS-1$
		
		/*
		 * Get system properties
		 */
		List keys = org.magelan.commons.ui.ErrorDialog.getDisplayedProperties();
		String res = ""; //$NON-NLS-1$
		String lf = System.getProperty("line.separator"); //$NON-NLS-1$
		Iterator iter = keys.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			res += key + "=" + System.getProperty(key) + lf; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// Write the properties to the exception log
		System.err.println("[" + new Date() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		System.err.println(res);
	}
	
	/**
	 * Creates a single frame editor
	 *
	 * @throws IllegalStateException
	 */
	public static void createSingleFrameInstance() {
		create(SINGLE_FRAME);
	}

	/**
	 * Creates a multi frame editor
	 *
	 * @throws IllegalStateException
	 */
	public static void createMultiFrameInstance() {
		create(MULTI_FRAME);
	}

	/**
	 * Returns a singleton instance of editor. Will return <code>null</code>
	 * if invoked befor an editor is created.
	 * 
	 * @return Editor
	 * @throws IllegalStateException
	 */
	public static Editor getInstance() {
		/*if (editor == null) {
			log.error("getInstance() used before an Editor had been created"); //$NON-NLS-1$
			throw new IllegalStateException("getInstance() used before an Editor had been created"); //$NON-NLS-1$
		}  */
		return editor;
	}
	
	/**
	 * Instantiates a class by name using the parameter <code>ClassLoader</code>.
	 * 
	 * @param name name of the class to instantiate 
	 * @param cl <code>ClassLoader</code> to use
	 * @return an instance
	 * @since 2.3
	 */
	public static Object instantiateClass(String name, ClassLoader cl) {
		Class c = null;
		
		// load the class
		try {
			c = cl.loadClass(name);
		} catch (ClassNotFoundException cnfe) {
			log.error("Cannot load class: " + name , cnfe); //$NON-NLS-1$
			throw new RuntimeException("Cannot load class: " + name , cnfe); //$NON-NLS-1$
		}

		// instantiate it
		if (c != null) {
			try {
				return c.newInstance();
			} catch (InstantiationException e1) {
				log.error("Cannot instantiate class: " + c, e1); //$NON-NLS-1$
				throw new RuntimeException("Cannot instantiate class: " + c, e1); //$NON-NLS-1$
			} catch (IllegalAccessException e2) {
				log.error("Illegal access to class: " + c, e2); //$NON-NLS-1$
				throw new RuntimeException("Illegal access to class: " + c, e2); //$NON-NLS-1$
			}
		}
		return null;
	}
	
	/**
	 * Returns the editor <code>ClassLoader</code> with the user-defined
	 * CLASSPATH.
	 * 
	 * @return a <code>ClassLoader</code> with the user-defined CLASSPATH
	 * @since 2.3
	 */
	public synchronized ClassLoader getClassLoader() {
		if (loader == null) {
			loader = createClassLoader();
		}
		return loader;
	}

	/**
	 * Creates a <code>ClassLoader</code> with the user-defined CLASSPATH.
	 * 
	 * @return a <code>ClassLoader</code> with the user-defined CLASSPATH
	 * @since 2.3
	 */
	public static ClassLoader createClassLoader() {
		/*
		 * Read the CLASSPATH
		 */
		List cp = new ArrayList();
		String cpfn = ClasspathPanel.CLASSPATH_FILE;
		try {
			AddRemovePanel.load(cpfn, cp);
		} catch (IOException ioe) {
			log.warn("Cannot load classpath file: " + cpfn, ioe);
		}
		
		/*
		 * Create a ClassLoader
		 */
		URL[] urls = new URL[cp.size()];
		String userClassPath = "";
		for (int i = 0; i < cp.size(); i++)
			try {
				String s = (String) cp.get(i);
				File f = new File(s);
				if (!f.exists()) {
					log.warn("Classpath entry does not exist: " + s); //$NON-NLS-1$
				}
				urls[i] = f.toURL();
				userClassPath += s + ";";
			} catch (MalformedURLException ex1) {
				log.warn("Invalid classpath element \"" + cp.get(i) + "\"", ex1); //$NON-NLS-1$ //$NON-NLS-2$
			}
		
		System.setProperty("magelan.class.path", userClassPath); //$NON-NLS-1$
			
		return new URLClassLoader(urls);
	}
	
	/**
	 * This method should be invoked to make an editor visible.
	 * 
	 * Editor.createSingleFrameInstance();
	 * 
	 * ITemplatesManager templManager = e.getManager().getTemplatesManager();
	 * templManager.setDefaultTemplate(new DefaultDrawingTemplate());
	 * 
	 * IExtensionsManager extMgr = e.getManager().getExtensionsManager();
	 * 
	 * getInstance().start();
	 */
	public synchronized void start() {
		log.debug("start();"); //$NON-NLS-1$
		log.info("[Magelan " + VERSION + "; " + new java.util.Date() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		/*
		 * Create the splash screen
		 */
		log.debug("Creating splash screen"); //$NON-NLS-1$
		SplashScreen splash = new SplashScreen();
		splash.setVisible(true);

		/*
		 * Create the editor manager
		 */
		//log.info("Creating the editor"); //$NON-NLS-1$
		//splash.setStatusText(lang.getString("Editor.splash.editor")); //$NON-NLS-1$

		/*
		 * Set a default template
		 */
		log.debug("Initializing templates"); //$NON-NLS-1$
		splash.setStatusText(lang.getString("Editor.splash.templates")); //$NON-NLS-1$
		ITemplatesManager templManager = getTemplatesManager();
		templManager.setDefaultTemplate(new DefaultDrawingTemplate());

		/*
		 * Initialize extensions
		 */
		log.info("Start loading extensions"); //$NON-NLS-1$
		splash.setStatusText(lang.getString("Editor.splash.extensions")); //$NON-NLS-1$
		IExtensionsManager extMgr = getExtensionsManager();

		/*
		 * Start the editor
		 */
		log.info("Starting the editor"); //$NON-NLS-1$
		splash.setStatusText(lang.getString("Editor.splash.starting")); //$NON-NLS-1$

		// let manager to create gui components
		getEditorManager().initialize();
	
		// show the editor
		log.debug("manager.getMainFrame().show();"); //$NON-NLS-1$
		manager.getMainFrame().setVisible(true);

		splash.setVisible(false);
		
		/*
		 * Show tips if necessary
		 */
		prefs = Preferences.userNodeForPackage(TipsDialog.class);
		if (prefs.getBoolean("tips.show", true)) { //$NON-NLS-1$
			(new TipsDialog(Editor.getEditorManager().getMainFrame())).setVisible(true);
		}
	}

	/**
	 * Safely exits and destroys the currently instantiated editor manager 
	 * but <strong>does not terminate the application</strong>. Another 
	 * manager instance can be created using designated factory methods 
	 * and {@link start()} used again.
	 * 
	 * @since	1.1
	 */
	public synchronized void shutdown() {
		log.debug("shutdown();"); //$NON-NLS-1$

		if (editor.manager == null) {
			log.error("Editor manager is null; editor not started or already shut-down."); //$NON-NLS-1$
			throw new NullPointerException("Editor manager is null; editor not started or already shut-down."); //$NON-NLS-1$
		}
		
		if (editor.manager.getMainFrame() != null) {
			editor.manager.getMainFrame().dispose();
		}
		editor.manager = null;
		
		log.info("--->"); //$NON-NLS-1$
	}
	
	/**
	 * Returns EditorManager for this Editor
	 *
	 * @return IEditorManager
	 */
	public static IEditorManager getEditorManager() {
		Editor e = getInstance();
		if (e == null) {
			log.error("no Editor instance had been created"); //$NON-NLS-1$
			throw new IllegalStateException("no Editor instance had been created"); //$NON-NLS-1$
		}
		/*
		if (e.manager == null) {
			log.error("getManager() used before an Editor had been created"); //$NON-NLS-1$
			throw new IllegalStateException("getManager() used before an Editor had been created"); //$NON-NLS-1$
		}
		*/
		return e.manager;
	}

	/**
	 * Returns the extensions manager. Moved here from <code>IEditorManager</code> as 
	 * of version 2.0.
	 * 
	 * @return IExtensionsManager
	 */
	public static synchronized IExtensionsManager getExtensionsManager() {
		Editor e = getInstance();
		if (e == null) {
			log.error("no Editor instance had been created"); //$NON-NLS-1$
			throw new IllegalStateException("no Editor instance had been created"); //$NON-NLS-1$
		}
		if (e.extManager == null) {
			try {
				e.extManager = (IExtensionsManager) instantiateClass("org.magelan.editor.DefaultExtensionManager", e.getClassLoader());
			} catch (Throwable thr) {
				org.magelan.commons.ui.ErrorDialog.showFatalDialog(
					"Cannot load Extensions Manager class. Please see the details below for more information. " + //$NON-NLS-1$
					"This is a fatal error and the program cannot run. " + //$NON-NLS-1$
					"Please report it by copying the error details to the form at the " + //$NON-NLS-1$
					"URL below.", //$NON-NLS-1$
					thr, false);
			}
			// e.extManager = new DefaultExtensionManager();
		}

		return e.extManager;
	}

	/**
	 * Returns the templates manager. Moved here from <code>IEditorManager</code> as 
	 * of version 2.0.
	 * 
	 * @return ITemplatesManager
	 */
	public static synchronized ITemplatesManager getTemplatesManager() {
		Editor e = getInstance();
		if (e == null) {
			log.error("no Editor instance had been created"); //$NON-NLS-1$
			throw new IllegalStateException("no Editor instance had been created"); //$NON-NLS-1$
		}
		if (e.templManager == null) {
			try {
				e.templManager = (ITemplatesManager) instantiateClass("org.magelan.editor.DefaultTemplatesManager", e.getClassLoader());
			} catch (Throwable thr) {
				org.magelan.commons.ui.ErrorDialog.showFatalDialog(
					"Cannot load Templates Manager class. Please see the details below for more information. " + //$NON-NLS-1$
					"This is a fatal error and the program cannot run. " + //$NON-NLS-1$
					"Please report it by copying the error details to the form at the " + //$NON-NLS-1$
					"URL below.", //$NON-NLS-1$
					thr, false);
			}
			// e.templManager = new DefaultTemplatesManager();
		}

		return e.templManager;
	}

	/**
	 * Returns the files manager. Moved here from <code>IEditorManager</code> as 
	 * of version 2.0.
	 * 
	 * @return IFilesManager
	 */
	public static synchronized IFilesManager getFilesManager() {
		Editor e = getInstance();
		if (e == null) {
			log.error("no Editor instance had been created"); //$NON-NLS-1$
			throw new IllegalStateException("no Editor instance had been created"); //$NON-NLS-1$
		}
		if (e.filesManager == null) {
			try {
				e.filesManager = (IFilesManager) instantiateClass("org.magelan.editor.DefaultFilesManager", e.getClassLoader());
			} catch (Throwable thr) {
				org.magelan.commons.ui.ErrorDialog.showFatalDialog(
					"Cannot load Files Manager class. Please see the details below for more information. " + //$NON-NLS-1$
					"This is a fatal error and the program cannot run. " + //$NON-NLS-1$
					"Please report it by copying the error details to the form at the " + //$NON-NLS-1$
					"URL below.", //$NON-NLS-1$
					thr, false);
			}
			// e.filesManager = new DefaultFilesManager();
		}

		return e.filesManager;
	}
	
	/**
	 * Returns the features manager. As of version 2.2.
	 * 
	 * @return IFeaturesManager
	 */
	public static synchronized IFeaturesManager getFeaturesManager() {
		Editor e = getInstance();
		if (e == null) {
			log.error("no Editor instance had been created"); //$NON-NLS-1$
			throw new IllegalStateException("no Editor instance had been created"); //$NON-NLS-1$
		}
		if (e.featuresManager == null) {
			try {
				e.featuresManager = (IFeaturesManager) instantiateClass("org.magelan.editor.DefaultFeaturesManager", e.getClassLoader());
			} catch (Throwable thr) {
				org.magelan.commons.ui.ErrorDialog.showFatalDialog(
					"Cannot load Features Manager class. Please see the details below for more information. " + //$NON-NLS-1$
					"This is a fatal error and the program cannot run. " + //$NON-NLS-1$
					"Please report it by copying the error details to the form at the " + //$NON-NLS-1$
					"URL below.", //$NON-NLS-1$
					thr, false);
			}
			// e.featuresManager = new DefaultFeaturesManager();
		}

		return e.featuresManager;
	}


	/**
	 * Editor's main method - shows a splash screen and loads the editor.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("<---"); //$NON-NLS-1$
		Editor.createSingleFrameInstance();
		Editor.getInstance().start();
	}
}