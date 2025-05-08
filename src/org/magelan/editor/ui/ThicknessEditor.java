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

import java.awt.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;


/**
 * A thickness editor class.
 * 
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.0, 08/2001
 */
public class ThicknessEditor extends JComboBox
	implements java.beans.PropertyEditor {
	//~ Instance fields --------------------------------------------------------

	Vector thicknesses;
	int thickness;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates the editor with a new default line style.
	 */
	public ThicknessEditor() {
		this(1);
	}

	/**
	 * Creates the customizer with a line style to edit.
	 * 
	 * <p></p>
	 *
	 * @param thickness a line style name
	 */
	public ThicknessEditor(int thickness) {
		super();
		thicknesses = new Vector();

		for (int i = 1; i <= 30; i++)
			thicknesses.add(new Integer(i));

		setValue(new Integer(thickness));
		setModel(new DefaultComboBoxModel(thicknesses));
		setRenderer(new CellRenderer());
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		try {
			//			((DefaultLineStyle) lineStyle).removePropertyChangeListener(listener);
		} catch (Exception e) { /* some error handling? */
		}
	}

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		try {
			//			((DefaultLineStyle) lineStyle).addPropertyChangeListener(listener);
		} catch (Exception e) { /* some error handling? */
		}
	}

	/**
	 * DOCME!
	 *
	 * @param value DOCME!
	 */
	public void setValue(Object value) {
		if (value != null) {
			this.thickness = ((Integer) value).intValue();
		}
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public Object getValue() {
		return new Integer(thickness);
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public String getAsText() {
		return "" + thickness; //$NON-NLS-1$
	}

	/**
	 * DOCME!
	 *
	 * @param text DOCME!
	 *
	 * @throws IllegalArgumentException DOCME!
	 */
	public void setAsText(String text) throws IllegalArgumentException {
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public boolean isPaintable() {
		return false;
	}

	/**
	 * DOCME!
	 *
	 * @param gfx DOCME!
	 * @param box DOCME!
	 */
	public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public String[] getTags() {
		return null;
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public String getJavaInitializationString() {
		return null;
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public boolean supportsCustomEditor() {
		return true;
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public java.awt.Component getCustomEditor() {
		return this;
	}

	/**
	 * DOCME!
	 *
	 * @param args DOCME!
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(100, 100, 310, 250);
		f.getContentPane().add(new LayerCustomizer());
		f.setVisible(true);
	}

	//~ Inner Classes ----------------------------------------------------------

	/**
	 * Renderer of the combobox.
	 */
	class CellRenderer extends JLabel implements ListCellRenderer {
		//~ Constructors -------------------------------------------------------

		/**
		 * Creates a new CellRenderer object.
		 */
		public CellRenderer() {
			setOpaque(true);
		}

		//~ Methods ------------------------------------------------------------

		/**
		 * DOCME!
		 *
		 * @param list DOCME!
		 * @param value DOCME!
		 * @param index DOCME!
		 * @param isSelected DOCME!
		 * @param cellHasFocus DOCME!
		 *
		 * @return DOCME!
		 */
		public Component getListCellRendererComponent(JList list, Object value,
													  int index,
													  boolean isSelected,
													  boolean cellHasFocus) {
			setBounds(getX(), getY(), getWidth(), 16);
			setIconTextGap(8);
			setIcon(new ThicknessIcon(((Integer) value).intValue(), this));
			setText("" + ((Integer) value).intValue()); //$NON-NLS-1$

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
	}

	/**
	
				*/
	private class ThicknessIcon implements Icon {
		//~ Instance fields ----------------------------------------------------

		private int width = 50;
		private int height = 12;
		int offset = 0;
		Color color;
		int thickness;
		JLabel l;

		//~ Constructors -------------------------------------------------------

		/**
		 * Creates a new ThicknessIcon object.
		 *
		 * @param thickness DOCME!
		 * @param l DOCME!
		 */
		public ThicknessIcon(int thickness, JLabel l) {
			this(thickness, Color.black, l);
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

		//~ Methods ------------------------------------------------------------

		/**
		 * DOCME!
		 *
		 * @param c DOCME!
		 * @param g DOCME!
		 * @param x DOCME!
		 * @param y DOCME!
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			offset = (l.getHeight() - thickness) / 2;
			g.setColor(color);
			g.fillRect(x, offset, x + width, thickness);
		}

		/**
		 * DOCME!
		 *
		 * @return DOCME!
		 */
		public int getIconWidth() {
			return width;
		}

		/**
		 * DOCME!
		 *
		 * @return DOCME!
		 */
		public int getIconHeight() {
			return height;
		}
	}
}