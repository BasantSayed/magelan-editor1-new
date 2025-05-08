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

import java.net.*;

import java.util.*;

import org.apache.log4j.*;


/**
 * A class file manifest. A standard way of extending the editor.
 * 
 * <p>
 * Extending classes are expected to provide manifest files, describing them.
 * This <code>.manifest</code> file must  contain class name, classpath, help
 * file  location. The  <code>DrawingEditorExtension</code> container is
 * supposed to  search for such manifest files, to locate its extending
 * classes.
 * </p>
 * 
 * <p>
 * The current manifest implementation supports the following properties:
 * </p>
 * 
 * <p>
 * 
 * <ul>
 * <li>
 * <code>Manifest-Version</code> - manifest file version;
 * </li>
 * <li>
 * <code>ClassName</code> - the full Java class name of the extension;
 * </li>
 * <li>
 * <code>ClassPath</code> - classpath of the extension and its classes and
 * resources, multiple paths divided by  semicolons;
 * </li>
 * <li>
 * <code>HelpPath</code> - full path of extension's HTML  formatted help file;
 * </li>
 * <li>
 * <code>Info</code> - short extension info;
 * </li>
 * <li>
 * <code>Created-By</code> - provider info
 * </li>
 * </ul>
 * </p>
 * 
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.0, 02/2002
 */
public class ClassManifest {
	//~ Static fields/initializers ---------------------------------------------
	private static Logger log = Logger.getLogger(ClassManifest.class);
	
	/** The current manifest implementation version. */
	public static String manifestVersion = "1.0"; //$NON-NLS-1$
	public static final String MANIFEST_VERSION = "Manifest-Version"; //$NON-NLS-1$
	public static final String CLASS_NAME = "ClassName"; //$NON-NLS-1$
	public static final String CLASS_PATH = "ClassPath"; //$NON-NLS-1$
	public static final String HELP_PATH = "HelpPath"; //$NON-NLS-1$
	public static final String INFO = "Info"; //$NON-NLS-1$
	public static final String CREATED_BY = "Created-By"; //$NON-NLS-1$

	//~ Instance fields --------------------------------------------------------

	private Properties prop;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates an empty manifest.
	 */
	public ClassManifest() {
		newManifest("", "."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Creates a manifest instanece from file.
	 * 
	 * @param fileName manifest file name
	 */
	public ClassManifest(String fileName) {
		newManifest("", "."); //$NON-NLS-1$ //$NON-NLS-2$
		load(fileName);
	}

	/**
	 * Creates a manifest instanece from file.
	 * 
	 * @param file manifest file
	 */
	public ClassManifest(File file) {
		newManifest("", "."); //$NON-NLS-1$ //$NON-NLS-2$
		load(file);
	}
	
	//~ Methods ----------------------------------------------------------------

	/**
	 * Loads a manifest from file.
	 *
	 * @param fileName manifest file name
	 * @return	success or failure
	 */
	public boolean load(String fileName) {
		return load(new File(fileName));
	}

	/**
	 * Loads a manifest from file.
	 *
	 * @param file manifest file
	 * @return	success or failure
	 */
	public boolean load(File file) {
		try {
			prop = new Properties();
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			log.warn("Cannot load manifest file \"" + file + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		return true;
	}
	
	/**
	 * Saves the manifest to file.
	 * 
	 * <p></p>
	 *
	 * @param file manifest file name
	 * @return success or failure
	 */
	public boolean save(String file) {
		try {
			prop.store(new FileOutputStream(file), "Class Manifest File"); //$NON-NLS-1$
		} catch (IOException e) {
			log.warn("Cannot save manifest file \"" + file + "\"", e); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		return true;
	}

	/**
	 * Creates a new manifest template with class name and path     set and
	 * other values - defaults.
	 * 
	 * <p></p>
	 *
	 * @param className name of the class
	 * @param classPath required class path
	 */
	public void newManifest(String className, String classPath) {
		prop = new Properties();
		prop.setProperty(MANIFEST_VERSION, manifestVersion);
		prop.setProperty(CLASS_NAME, className);
		prop.setProperty(CLASS_PATH, classPath);
		prop.setProperty(HELP_PATH, "index.html"); //$NON-NLS-1$
		prop.setProperty(INFO, "Class file " + className); //$NON-NLS-1$
		prop.setProperty(CREATED_BY, getClass().getName());
	}

	/**
	 * Creates a new manifest template with class name and path     set and
	 * other values - defaults. The classpath will be equal     to the system
	 * <code>java.class.path</code> variable.
	 * 
	 * <p></p>
	 *
	 * @param className name of the class
	 */
	public void newManifest(String className) {
		newManifest(className, System.getProperty("java.class.path")); //$NON-NLS-1$
	}

	/**
	 * Returns the full Java name of the class.
	 * 
	 * <p></p>
	 *
	 * @return the class name
	 */
	public String getName() {
		return prop.getProperty(CLASS_NAME);
	}

	/**
	 * Sets the full Java name of the class.
	 * 
	 * <p></p>
	 *
	 * @param name the class name
	 */
	public void setName(String name) {
		prop.setProperty(CLASS_NAME, name);
	}

	/**
	 * Returns the class path. A <code>Vector</code>     of
	 * <code>String</code>s, each representing a path.
	 * 
	 * <p></p>
	 *
	 * @return class path as vector
	 */
	public List getClassPath() {
		return getClassPath(prop.getProperty(CLASS_PATH));
	}

	/**
	 * Transforms a ";" separated list of paths to a <code>List</code> of
	 * <code>String</code>s, each representing a path.
	 *
	 * @param classPath class path
	 * @return class path as vector
	 */
	public static List getClassPath(String classPath) {
		List path = new ArrayList();
		if (classPath == null) return path;
		String separator = System.getProperty("path.separator"); //$NON-NLS-1$
		int idx1 = 0;

		while (idx1 != -1) {
			int idx2 = classPath.indexOf(separator, idx1);
			path.add(classPath.substring(idx1,
										 (idx2 != -1) ? idx2 : classPath.length()));

			if (idx2 == -1) {
				break;
			}

			idx1 = idx2 + 1;
		}

		return path;
	}

	/**
	 * Sets the class path.
	 * 
	 * <p></p>
	 *
	 * @param path class path
	 */
	public void setClassPath(List path) {
		String cp = ""; //$NON-NLS-1$
		String separator = System.getProperty("path.separator"); //$NON-NLS-1$
		Iterator e = path.iterator();

		while (e.hasNext())
			cp += (String) e.next() + separator;

		setClassPath(cp);
	}

	/**
	 * Sets the class path. (";" delimited)
	 * 
	 * <p></p>
	 *
	 * @param path extension class path
	 */
	public void setClassPath(String path) {
		prop.setProperty(CLASS_PATH, path);
	}

	/**
	 * This method will read the classpath provided and will try to load
	 * the external class.
	 * 
	 * <p></p>
	 *
	 * @return class object
	 */
	public Class getExtClass() {
		if (getName() == null) return null;
		
		List paths = getClassPath();
		URL[] urls = new URL[paths.size()];

		for (int i = 0; i < paths.size(); i++)
			try {
				urls[i] = new File((String) paths.get(i)).toURL();
			} catch (MalformedURLException ex1) {
				log.warn("Invalid classpath element \"" + paths.get(i) + "\"", ex1); //$NON-NLS-1$ //$NON-NLS-2$
			}

		//URLClassLoader loader = new URLClassLoader(urls);
		URLClassLoader loader = new URLClassLoader(urls, Editor.getInstance().getClassLoader());

		try {
			return loader.loadClass(getName());
		} catch (ClassNotFoundException ex2) {
			log.warn("Cannot load class for extension \"" + getName() + "\"", ex2); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}

	/**
	 * This method will try to instantiate the external class.
	 * 
	 * <p></p>
	 *
	 * @return external class loaded
	 */
	public Object instantiate() {
		Class c = getExtClass();

		if (c != null) {
			try {
				return c.newInstance();
			} catch (InstantiationException e1) {
				log.error("Cannot instantiate class \"" + c + "\"", e1); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			} catch (IllegalAccessException e2) {
				log.error("Illegal access of class \"" + c + "\"", e2); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}
		}

		return null;
	}

	/**
	 * This method will load the manifest and then try to instantiate     the
	 * external class.
	 * 
	 * <p></p>
	 *
	 * @param mf manifest file to load
	 *
	 * @return external class loaded
	 */
	public static Object instantiate(String mf) {
		return (new ClassManifest(mf)).instantiate();
	}

	/**
	 * This method will load the manifest and then try to instantiate     the
	 * external class.
	 * 
	 * <p></p>
	 *
	 * @param mf manifest file to load
	 *
	 * @return external class loaded
	 */
	public static Object instantiate(java.io.File mf) {
		return (new ClassManifest(mf.getAbsolutePath())).instantiate();
	}

	/**
	 * Returns the full path and name of extension's HTML     formatted help
	 * file.
	 * 
	 * <p></p>
	 *
	 * @return extension help file
	 */
	public String getHelpPath() {
		return prop.getProperty(HELP_PATH);
	}

	/**
	 * Returns the version of the manifest file.
	 * 
	 * <p></p>
	 *
	 * @return manifest version
	 */
	public String getVersion() {
		return prop.getProperty(MANIFEST_VERSION);
	}

	/**
	 * Returns extension info.
	 * 
	 * <p></p>
	 *
	 * @return extension info
	 */
	public String getInfo() {
		return prop.getProperty(INFO);
	}

	/**
	 * Returns the name of extension author.
	 * 
	 * <p></p>
	 *
	 * @return extension author name
	 */
	public String getCreatedBy() {
		return prop.getProperty(CREATED_BY);
	}

	/**
	 * Sets the full path and name of extension's HTML     formatted help file.
	 * 
	 * <p></p>
	 *
	 * @param hp extension help file
	 */
	public void setHelpPath(String hp) {
		prop.setProperty(HELP_PATH, hp);
	}

	/**
	 * Sets the version of the manifest file.
	 * 
	 * <p></p>
	 *
	 * @param ver manifest version
	 */
	public void setVersion(String ver) {
		prop.setProperty(MANIFEST_VERSION, ver);
	}

	/**
	 * Sets extension info.
	 * 
	 * <p></p>
	 *
	 * @param info extension info
	 */
	public void setInfo(String info) {
		prop.setProperty(INFO, info);
	}

	/**
	 * Sets the name of extension author.
	 * 
	 * <p></p>
	 *
	 * @param cb extension author name
	 */
	public void setCreatedBy(String cb) {
		prop.setProperty(CREATED_BY, cb);
	}
}