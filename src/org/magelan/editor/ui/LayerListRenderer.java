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
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.magelan.core.CoreModel;
import org.magelan.core.style.Layer;


/**
 * 
 * @version	1.1, 07/2006
 * @author	Assen Antov
 */
public class LayerListRenderer extends JCheckBox implements ListCellRenderer {
	
	protected javax.swing.border.Border noFocusBorder = new javax.swing.border.EmptyBorder(1, 1, 1, 1);
	

	private CoreModel model;
	
	public LayerListRenderer(CoreModel model) {
		super();
		this.model = model;
	}


	/**
	 * 
	 * @param	list	the list to render
	 * @param	value	value to display
	 * @param	index	cell index
	 * @param	isSelected	is the cell selected
	 * @param	cellHasFocus	does the cell possess the focus
	 * 
	 * @return	a configured cell renderer component
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		Layer val = (Layer) value;
		
		if (model != null) {
			Layer current = (Layer) model.getCurrent(Layer.class);
			if (current == val) setFont(list.getFont().deriveFont(Font.ITALIC));
			else setFont(list.getFont().deriveFont(Font.PLAIN));
		}
		
		if (val != null) {
			setText(val.getName());
		}
		else {
			setText(""); //$NON-NLS-1$
		}
		setSelected(val.isVisible());
		setIcon(null);
		setToolTipText(val.getName());
		
		if (cellHasFocus) {
			setBorder(UIManager.getBorder("List.focusCellHighlightBorder")); //$NON-NLS-1$
		} else {
			setBorder(noFocusBorder);
		}

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setEnabled(list.isEnabled());

		return this;
	}
}