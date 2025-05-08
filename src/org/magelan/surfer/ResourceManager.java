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
package org.magelan.surfer;

import org.magelan.surfer.resources.*;
import java.util.*;

/**
 * Manager for resource descriptors. All resource descriptors that need to
 * be recognized by the <code>DataTree</code> renderer must be first added
 * to this manager.
 * 
 * @version	2.0, 02/2004
 * @author	Assen Antov
 */
public class ResourceManager {
	
	private static ResourceManager rm;
	private List rd;
	
	static {
		//add(FileResource.getInstance());
		add(StringResource.getInstance());
		add(HelpResource.getInstance());
		add(URLResource.getInstance());
	}
	
	/**
	 * Do not allow anyone to instantiate the manager.
	 */
	private ResourceManager() {}
	
	private static ResourceManager getInstance() {
		if (rm == null) {
			rm = new ResourceManager();
		}
		return rm;
	}
	
	/**
	 * Returns a resource descriptor for the parameter object.
	 * 
	 * @param	obj	data object
	 * @return	a <code>Resource</code> descriptor
	 */
	public static Resource getResourceFor(Object obj) {
		ResourceManager rm = getInstance();
		
		if (rm.rd == null) {
			return null;
		}
		
		Iterator iter = rm.rd.iterator();
		Resource res = null;
		while (iter.hasNext()) {
			res = (Resource) iter.next();
			if (res.isSupported(obj)) {
				return res;
			}
		}
		return null;
	}
	
	/**
	 * Adds a resource descriptor to the resource manager.
	 * 
	 * @param	res	resource descriptortor to add
	 * @see	Resource
	 */
	public static void add(Resource res) {
		ResourceManager rm = getInstance();
		
		if (rm.rd == null) {
			rm.rd = new ArrayList(10);
		}

		// add the resource descriptor
		rm.rd.add(res);
	}
	
	/**
	 * Removes a resource descriptor from the resource manager.
	 * 
	 * @param	res	resource descriptor to remove
	 */
	public static void remove(Resource res) {
		ResourceManager rm = getInstance();
		
		if (rm.rd != null) {
			rm.rd.remove(res);
		}
	}

	/**
	 * Returns an unmodifiable list of all resource descriptors.
	 * 
	 * @return	a list of <code>Resource</code> descriptors or null
	 */
	public static List getResources() {
		ResourceManager rm = getInstance();

		if (rm.rd == null) {
			return null;
		}

		return Collections.unmodifiableList(rm.rd);
	}
}
