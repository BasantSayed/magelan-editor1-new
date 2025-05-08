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

import java.awt.*;
import java.beans.*;


/**
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.0, 01/2004
 */
public class AbstractEditorExtensionBeanInfo extends SimpleBeanInfo {

	private static org.magelan.commons.Lang lang = org.magelan.commons.Lang.getLang(Editor.STRINGS);
	
	private PropertyDescriptor[] descriptors;
	private BeanDescriptor bd;

	public AbstractEditorExtensionBeanInfo () {
	}

	/**
	 * Returns the bean class
	 *
	 * @return bean class
	 */
	public Class getBeanClass() {
		return AbstractEditorExtension.class;
	}

	/**
	 * Gets the beans <code>PropertyDescriptor</code>s.
	 *
	 * @return An array of PropertyDescriptors describing the editable
	 * 		   properties supported by this bean.  May return null if the
	 * 		   information should be obtained by automatic analysis.
	 *
	 * @see java.beans.BeanInfo#getPropertyDescriptors()
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		if (descriptors == null) {
			descriptors = new PropertyDescriptor[] {};
		}
		return descriptors;
	}

	/**
	 * Gets the beans <code>BeanDescriptor</code>.
	 *
	 * @return A BeanDescriptor providing overall information about the bean,
	 * 		   such as its displayName, its customizer, etc.  May return null
	 * 		   if the information should be obtained by automatic analysis.
	 */
	public BeanDescriptor getBeanDescriptor() {
		if (bd == null) {
			bd = new BeanDescriptor(AbstractEditorExtension.class);
			bd.setDisplayName(lang.getString("AbstractEditorExtensionBeanInfo.display.name")); //$NON-NLS-1$
			bd.setShortDescription(lang.getString("AbstractEditorExtensionBeanInfo.descr.short")); //$NON-NLS-1$
		}
		return bd;
	}

	/**
	 * 	returns an Icon that can be used to display bean in tollbars etc.
	 *
	 * @param iconKind 
	 *
	 * @return Icon that can be used to display bean in tollbars etc.
	 */
	public Image getIcon(int iconKind) {
		return (Image) org.magelan.commons.ui.Icons.JAR_ICON;
	}
}