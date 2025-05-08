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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magelan.core.style.Layer;


/**
 * A thickness control class.
 * 
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.01, 04/2002
 * @version 2.0, 07/2003
 */
public class ThicknessControl extends JComboBox {

	private static final int MAX_THICKNESS = 30;

	/**
	 * Creates the editor with a new default line style.
	 */
	public ThicknessControl() {
		setModel(new DefaultComboBoxModel(create(MAX_THICKNESS)));
		setRenderer(new CellRenderer());
	}


	public void setValue(Layer layer) {
		setValue((int) layer.getThickness());
	}

	public void setValue(int value) {
		if (value < 0) return;
		if (value > MAX_THICKNESS) {
			setModel(new DefaultComboBoxModel(create(value)));
		}
		setSelectedIndex(value);
	}

	public int getValue() {
		return getSelectedIndex();
	}


	private java.util.Vector create(int max) {
		java.util.Vector th = new Vector(max);
		for (int i = 0; i <= max; i++) {
			th.add(new Integer(i));
		}
		return th;
	}
	
	/**
	 * Renderer of the combobox.
	 */
	class CellRenderer extends JLabel implements ListCellRenderer {

		/**
		 * Creates a new CellRenderer object.
		 */
		public CellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
													  int index,
													  boolean isSelected,
													  boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			setIconTextGap(6);
			setIcon(new ThicknessIcon(((Integer) value).intValue(), getForeground(), this));
			setText("" + ((Integer) value).intValue()); //$NON-NLS-1$

			return this;
		}
	}

	/**
	 *
	 */
	class ThicknessIcon implements Icon {
		//~ Static fields/initializers -----------------------------------------

		static final int GAP = 3;

		//~ Instance fields ----------------------------------------------------

		private int width = 60;
		private Color color;
		private int thickness;
		private JLabel l;

		//~ Constructors -------------------------------------------------------

		/**
		 * Creates a new ThicknessIcon object.
		 *
		 * @param thickness DOCME!
		 * @param l DOCME!
		 */
		public ThicknessIcon(int thickness, JLabel l) {
			this(thickness, Color.gray, l);
		}

		/**
		 * Creates a new ThicknessIcon object.
		 *
		 * @param thickness DOCME!
		 * @param color DOCME!
		 * @param l DOCME!
		 */
		public ThicknessIcon(int thickness, Color color, JLabel l) {
			this.color = color;
			this.thickness = thickness;
			this.l = l;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			int th = Math.min(thickness, l.getHeight() - 2);
			int offset = (l.getHeight() - th) / 2;
			g.setColor(color);
			g.fillRect(x + GAP, offset, x + width, th);
		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight() {
			return l.getHeight();
		}
	}
}