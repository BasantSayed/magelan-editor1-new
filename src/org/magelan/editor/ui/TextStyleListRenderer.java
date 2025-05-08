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
package org.magelan.editor.ui;

import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.Introspector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.ListRenderer;
import org.magelan.core.style.TextStyle;
import org.magelan.editor.Editor;


/**
 * 
 * @version	1.0, 08/2004
 * @author	Assen Antov
 */
public class TextStyleListRenderer extends ListRenderer {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	

	public TextStyleListRenderer() {
		super();
	}


	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		TextStyle val = (TextStyle) value;
		if (val != null) {
			setText(val.getName());
			setToolTipText(val.getName());
		}
		else {
			setText(lang.getString("color.ByLayer")); //$NON-NLS-1$
			setToolTipText(null);
		}
		return comp;
	}
	
	/**
	 * @see org.magelan.commons.ui.ListRenderer#getIcon(java.lang.Object)
	 */
	public Icon getIcon(Object value) {
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(Introspector.getBeanInfo(value.getClass()).getIcon(BeanInfo.ICON_COLOR_16x16));
		} catch (Throwable e) {return Icons.GAP_ICON_16;}
		return icon;
	}
}