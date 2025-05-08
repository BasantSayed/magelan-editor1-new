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

import org.magelan.commons.ui.*;
import org.magelan.core.style.Layer;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;


/**
 * A layer chooser/modifier combo box.
 * 
 * <p></p>
 *
 * @version	2.0, 12/2003
 * @author	Assen Antov
 */
public class LayerComboBox extends JComboBox {

	/**
	 * Creates the editor with a new default drawing.
	 */
	public LayerComboBox() {
		this(new ArrayList(), null);
	}

	/**
	 * Creates the combo box with a list of layers to edit.
	 * 
	 * <p></p>
	 *
	 * @param	d	drawing to extract layers from
	 */
	public LayerComboBox(org.magelan.core.CoreModel d) {
		this(d.getFeatures(Layer.class), (Layer) d.getCurrent(Layer.class));
	}
	
	/**
	 * Creates the combo box with a list of layers to edit.
	 * 
	 * <p></p>
	 *
	 * @param	layers	list of layers
	 * @param	seelcted	the selected layer
	 */
	public LayerComboBox(java.util.List layers, Layer selected) {
		setEditable(true);
		
		/*
		 * Set editor and renderer. Note that we need 2 separate instances 
		 * of the class for renderer and editor, not one as one may expect.
		 */
		setRenderer(new ItemEditor());
		setEditor(new ItemEditor());
		
		/*
		 * Set the data
		 */
		setData(layers);
		setSelectedItem(selected);
	}


	/**
	 * This method must be used to set the data of the combo box until the 
	 * <code>DefaultComboBoxModel</code> class from Swing is finally added 
	 * a constructor with a <code>List</code> argument.
	 * <p>
	 * @param	layers	list of layers to display
	 */
	public void setData(java.util.List layers) {
		setModel(new DefaultComboBoxModel(new Vector(layers)));
	}
	
	/**
	 * A convenience method that sets the combo box data after the layers
	 * of the parameter drawing.
	 * <p>
	 * @param	d	drawing to extract layers from
	 */
	public void setFromDrawing(org.magelan.drawing.DrawingModel d) {
		setData(d.getFeatures(Layer.class));
		setSelectedItem(d.getCurrent(Layer.class));
	}

	/**
	 * Test method.
	 *
	 * @param args command line
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(100, 100, 350, 400);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		java.util.List layers = new ArrayList();
		layers.add(new Layer());
		layers.add(new Layer("NewLayer")); //$NON-NLS-1$
		Layer l = new Layer("Selected"); //$NON-NLS-1$
		layers.add(l);
		f.getContentPane().add(new LayerComboBox(layers, l), BorderLayout.NORTH);
	//	f.getContentPane().add(new BooleanPropertyEditor(
	//		l, "frozen", //$NON-NLS-1$
	//		Icons.getImageIcon("/org/magelan/images/frozen.gif"), //$NON-NLS-1$
	//		Icons.getImageIcon("/org/magelan/images/unfrozen.gif") //$NON-NLS-1$
	//	), BorderLayout.CENTER);
		f.getContentPane().add(new LayerComboBox(), BorderLayout.SOUTH);
		f.setVisible(true);
	}


	//~ Inner Classes ----------------------------------------------------------

	/**
	 * Editor/renderer for the combobox. 
	 * <p>
	 * @version	2.0, 12/2003
	 * @author	Assen Antov
	 */
	private class ItemEditor extends JPanel implements ComboBoxEditor, ListCellRenderer {
		//~ Instance fields ----------------------------------------------------

		private LayerVisibleEditor visible = new LayerVisibleEditor();
		private BooleanPropertyEditor frozen = new BooleanPropertyEditor(
			null, "frozen", //$NON-NLS-1$
			Icons.FROZEN_ICON, 
			Icons.UNFROZEN_ICON
		);
		
		
		private LayerLockedEditor locked = new LayerLockedEditor();
		private JPanel color = new JPanel();
		private AlphaCompositeView alpha = new AlphaCompositeView();
		private JLabel label;
		private Layer layer;

		//~ Constructors -------------------------------------------------------

		/**
		 * Creates a new ItemEditor object.
		 */
		public ItemEditor() {
			setLayout(new FlowLayout(0, 0, FlowLayout.LEFT));
			//setOpaque(true);
			
			setBorder(new javax.swing.border.EtchedBorder());
			//setBorder(UIManager.getBorder("border")); //$NON-NLS-1$
			
			visible.setFocusPainted(false);
			visible.setBorderPainted(false);
			add(visible);

			frozen.setFocusPainted(false);
			frozen.setBorderPainted(false);
			add(frozen);

			locked.setFocusPainted(false);
			locked.setBorderPainted(false);
			add(locked);

			JPanel panel1 = new JPanel();
			panel1.setPreferredSize(new Dimension(3, 0));
			add(panel1);

			color.setPreferredSize(new Dimension(12, 12));

			//color.setBorder(javax.swing.border.LineBorder.createBlackLineBorder());
			color.setBorder(new javax.swing.border.EtchedBorder());
			add(color);

			JPanel panel2 = new JPanel();
			panel2.setPreferredSize(new Dimension(5, 0));
			add(panel2);

			add(alpha);

			JPanel panel3 = new JPanel();
			panel3.setPreferredSize(new Dimension(8, 0));
			add(panel3);

			label = new JLabel();
			add(label);
			
			setVisible(true);
		}

		//~ Methods ------------------------------------------------------------

		/**
		 * @see	ComboBoxEditor
		 */
		public Component getEditorComponent() {
			return this;
		}

		/**
		 * @see	ComboBoxEditor
		 */
		public void setItem(Object anObject) {
			if (anObject == null) {
				return;
			}
	
			this.layer = (Layer) anObject;

			visible.setLayer((Layer) anObject);
			frozen.setBean((Layer) anObject);
			locked.setLayer((Layer) anObject);
			color.setBackground(((Layer) anObject).getColor());
			alpha.setLayer((Layer) anObject);
			label.setText(((Layer) anObject).getName());
			label.setToolTipText(((Layer) anObject).getName());
		}

		/**
		 * @see	ComboBoxEditor
		 */
		public Object getItem() {
			return layer;
		}

		/**
		 * @see	ComboBoxEditor
		 */
		public void selectAll() {
		}

		/**
		 * @see	ComboBoxEditor
		 */
		public void addActionListener(ActionListener l) {
			listenerList.add(ActionListener.class, l);
		}

		/**
		 * @see	ComboBoxEditor
		 */
		public void removeActionListener(ActionListener l) {
			if (l != null) {
				listenerList.remove(ActionListener.class, l);
			}
		}

		/**
		 * @see	ListCellRenderer
		 */
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

			setItem(value);
			setOpaque(true);
			setBorder(null);

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

		public void setBackground(Color col) {
			super.setBackground(col);
			if (visible != null) {
				visible.setBackground(col);
				frozen.setBackground(col);
				locked.setBackground(col);
				alpha.setBackground(col);
				label.setBackground(col);
			}
		}

		public void setForeground(Color col) {
			super.setForeground(col);
			if (visible != null) {
				visible.setForeground(col);
				frozen.setForeground(col);
				locked.setForeground(col);
				alpha.setForeground(col);
				label.setForeground(col);
			}
		}

		public void setEnabled(boolean b) {
			super.setEnabled(b);
			if (visible != null) {
				visible.setEnabled(b);
				frozen.setEnabled(b);
				locked.setEnabled(b);
				color.setEnabled(b);
				alpha.setEnabled(b);
				label.setEnabled(b);
			}
		}
	}
}