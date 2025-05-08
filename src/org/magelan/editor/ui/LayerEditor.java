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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.magelan.core.style.Layer;


/**
 * A layer editor class.
 * 
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.0, 02/2002
 */
public class LayerEditor extends JList implements java.beans.PropertyEditor {
	//~ Instance fields --------------------------------------------------------

	Vector layers;
	Layer layer;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates the editor with a new default layer.
	 */
	public LayerEditor() {
		this(new Vector());
		layers.add(new Layer());
	}

	/**
	 * Creates the customizer with a list of layers to edit.
	 * 
	 * <p></p>
	 *
	 * @param layers a list of layers
	 */
	public LayerEditor(Vector layers) {
		super(layers);
		this.layers = layers;

		setCellRenderer(new CellRenderer());

		if (layers.size() > 0) {
			setValue((Layer) layers.elementAt(0));
		}

		//addItem((Layer) layers.elementAt(0));
		//setModel(new DefaultComboBoxModel(layers));
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		//	try {
		//		((DefaultLineStyle) lineStyle).removePropertyChangeListener(listener);
		//	} catch (Exception e) {/* some error handling? */}
	}

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		//	try {
		//		((DefaultLineStyle) lineStyle).addPropertyChangeListener(listener);
		//	} catch (Exception e) {/* some error handling? */}
	}

	/**
	 * DOCME!
	 *
	 * @param value DOCME!
	 */
	public void setValue(Object value) {
		this.layer = (Layer) value;

		//layers.add(value);
		//setModel(new DefaultComboBoxModel(layers));
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public Object getValue() {
		return layer;
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public String getAsText() {
		return layer.getName();
	}

	/**
	 * DOCME!
	 *
	 * @param text DOCME!
	 *
	 * @throws IllegalArgumentException DOCME!
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		/*
		                Should try to instantiate the layer bean!
		*/
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
	class CellRenderer implements ListCellRenderer {
		//~ Constructors -------------------------------------------------------

		/**
		 * Creates a new CellRenderer object.
		 */
		public CellRenderer() {
			super();
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
			Layer l = (Layer) value;

			JPanel p = new JPanel();
			p.setOpaque(true);
			p.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

			LayerVisibleEditor ve = new LayerVisibleEditor(l);
			ve.setBorderPainted(false);
			ve.setPreferredSize(new Dimension(ve.getIcon().getIconWidth(),
											  ve.getIcon().getIconHeight()));
			p.add(ve);

			JLabel label = new JLabel();

			if (l != null) {
				label.setText(l.getName());

				//label.setForeground(l.getColor());
			}

			p.add(label);

			p.setPreferredSize(new Dimension(130, 16));

			//			p.setPreferredSize(new Dimension(
			//				ve.getIcon().getIconWidth()+label.getWidth()+10,
			//				ve.getIcon().getIconHeight()+label.getHeight()
			//			));
			if (isSelected) {
				p.setBackground(list.getSelectionBackground());
				p.setForeground(list.getSelectionForeground());
				ve.setBackground(list.getSelectionBackground());
			} else {
				p.setBackground(list.getBackground());
				p.setForeground(list.getForeground());
				ve.setBackground(list.getBackground());
			}

			//			p.setBorder((cellHasFocus) ? 
			//				UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1, 1, 1, 1));
			p.setEnabled(list.isEnabled());
			p.setFont(list.getFont());

			return p;
		}
	}
}