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

import java.util.*;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;


/**
 * Contains and manages drawing features.  
 *
 * @author Assen Antov
 * @version 1.0, 02/2005
 */
public class DefaultFeaturesManager implements IFeaturesManager {

	private static Logger log = Logger.getLogger(DefaultFeaturesManager.class);
	private static Preferences prefs = Preferences.userNodeForPackage(DefaultFeaturesManager.class);
	
	private List features;
	private String PS;
	
	public DefaultFeaturesManager() {
		PS = System.getProperty("path.separator"); //$NON-NLS-1$
		String f = prefs.get("features",  //$NON-NLS-1$
			"org.magelan.core.style.Layer" + PS +   //$NON-NLS-1$
			"org.magelan.core.style.DefaultLineStyle" + PS +   //$NON-NLS-1$
			"org.magelan.core.style.DefaultTextStyle" + PS +  //$NON-NLS-1$
		//	"org.magelan.core.Block" + PS + //$NON-NLS-1$
			"org.magelan.core.renderer.CompoundRenderer" + PS + //$NON-NLS-1$
			"org.magelan.core.db.DefaultDataSource" + PS + //$NON-NLS-1$
			"org.magelan.core.db.DelimitedFileDataSource"// + PS +  //$NON-NLS-1$
		//	"org.magelan.core.db.SQLDataSource" //$NON-NLS-1$
		);
		features = ClassManifest.getClassPath(f);
		
	}
	
	public void add(String feature) {
		addImpl(feature);
		save();
	}

	private void addImpl(String feature) {
		if (features == null) {
			features = new ArrayList();
		}
		if (!features.contains(feature)) {
			features.add(feature);
			log.debug("Adding: " + feature); //$NON-NLS-1$
			if (!exists(feature)) {
				log.info("feature class not found: " + feature);  //$NON-NLS-1$
			}
		}
	}
	
	public void remove(String feature) {
		if (features != null) {
			features.remove(feature);
			save();
		}
	}

	private boolean exists(String feature) {
		try {
			Class.forName(feature);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private void save() {
		String f = "";  //$NON-NLS-1$
		Iterator iter = features.iterator();
		while (iter.hasNext()) {
			f += (String) iter.next();
			if (iter.hasNext()) {
				f += PS;
			}
		}
		prefs.put("features", f);  //$NON-NLS-1$
	}

	public List getFeatures() {
		return Collections.unmodifiableList(features);
	}
	
	public void setFeatures(Collection f) {
		features = new ArrayList();
		Iterator iter = f.iterator();
		while (iter.hasNext()) {
			addImpl((String) iter.next());
		}
		save();
	}
}