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
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.magelan.core.style.LineStyle;


/**
 * Renderer for the combobox.
 */
public class LineStyleCellRenderer extends JLabel implements ListCellRenderer {
	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates a new LineStyleCellRenderer object.
	 */
	public LineStyleCellRenderer() {
		setOpaque(true);

		//setEnabled(true);
		//setVisible(true);
	}

	//~ Methods ----------------------------------------------------------------

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
		
		if (value == null) {
			setToolTipText(null);
			setIcon(null);
			setText(""); //$NON-NLS-1$
			return this;
		}
		
		setIconTextGap(8);
		setIcon(new LineStyleIcon((LineStyle) value, this));
		setText(((LineStyle) value).getName());
		setToolTipText(((LineStyle) value).getName());

		//LineStyleControl.this.combo.setToolTipText(((LineStyle) value).getName());
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setComponentOrientation(list.getComponentOrientation());

		return this;
	}

	//~ Inner Classes ----------------------------------------------------------

	/**
	 * Draws a linestyle preview icon.
	 */
	class LineStyleIcon implements Icon {
		//~ Static fields/initializers -----------------------------------------

		static final int GAP = 5;

		//~ Instance fields ----------------------------------------------------

		private int width = 70;
		private Color color;
		private LineStyle ls;
		private int thickness;
		private JLabel l;

		//~ Constructors -------------------------------------------------------

		/**
		 * Creates a new LineStyleIcon object.
		 *
		 * @param ls DOCME!
		 * @param l DOCME!
		 */
		public LineStyleIcon(LineStyle ls, JLabel l) {
			this(ls, 1, l.getForeground(), l);
		}

		/**
		 * Creates a new LineStyleIcon object.
		 *
		 * @param ls DOCME!
		 * @param thickness DOCME!
		 * @param l DOCME!
		 */
		public LineStyleIcon(LineStyle ls, int thickness, JLabel l) {
			this(ls, thickness, l.getForeground(), l);
		}

		/**
		 * Creates a new LineStyleIcon object.
		 *
		 * @param ls DOCME!
		 * @param thickness DOCME!
		 * @param color DOCME!
		 * @param l DOCME!
		 */
		public LineStyleIcon(LineStyle ls, int thickness, Color color, JLabel l) {
			this.ls = ls;
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
			int offset = y + l.getHeight() / 2;
			g.setColor(color);
			((Graphics2D) g).setStroke(ls.getStroke(1));
			g.drawLine(x + GAP, offset, x + width, offset);
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
			return l.getHeight();
		}

		// methods to modify linestyle preview size
		public void setIconWidth(float ratio) {
			width = (int) (ratio * l.getWidth());
		}

		/**
		 * DOCME!
		 *
		 * @param width DOCME!
		 */
		public void setIconWidth(int width) {
			this.width = width;
		}
	}
}