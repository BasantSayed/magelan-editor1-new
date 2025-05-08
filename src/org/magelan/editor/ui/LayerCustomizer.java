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

import java.beans.PropertyChangeListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.style.Layer;
import org.magelan.editor.Editor;


/**
 * A layer customizer class.
 *
 * @author Assen Antov
 * @version 1.0, 08/2001
 */
public class LayerCustomizer extends LayerCustomizerLoader
	implements java.beans.Customizer, java.awt.event.ActionListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);

	private Layer layer;

	/**
	 * Creates the customizer with a new default layer.
	 */
	public LayerCustomizer() {
		this(new Layer());
	}

	/**
	 * Creates the customizer with a layer to edit.
	 *
	 * @param layer
	 */
	public LayerCustomizer(Layer layer) {
		nameField.addActionListener(this);
		visibleButton.addActionListener(this);
		frozenButton.addActionListener(this);
		colorButton.addActionListener(this);
		lockedButton.addActionListener(this);

		//	lineStyleCombo.addActionListener(this);
		thicknessCombo.addActionListener(this);

		setObject(layer);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Listens for <code>ActionEvent</code>s.
	 * 
	 * <p></p>
	 *
	 * @param e action event
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		String src = ((java.awt.Component) e.getSource()).getName();

		if ("nameField".equals(src)) { //$NON-NLS-1$
		} else if ("colorButton".equals(src)) { //$NON-NLS-1$
			colorButton.setBackground(JColorChooser.showDialog(this,
																lang.getString("Choose_layer_color_9"), //$NON-NLS-1$
																layer.getColor()));
		} else if ("visibleButton".equals(src)) { //$NON-NLS-1$
			visibleButton.setIcon(visibleButton.isSelected() ? Icons.VISIBLE_ICON
															 : Icons.INVISIBLE_ICON);
		} else if ("frozenButton".equals(src)) { //$NON-NLS-1$
			frozenButton.setIcon(frozenButton.isSelected() ? Icons.FROZEN_ICON
														   : Icons.UNFROZEN_ICON);
		} else if ("lockedButton".equals(src)) { //$NON-NLS-1$
			lockedButton.setIcon(lockedButton.isSelected() ? Icons.LOCKED_ICON
														   : Icons.UNLOCKED_ICON);
		} else if ("lineStyleCombo".equals(src)) { //$NON-NLS-1$
		} else if ("thicknessCombo".equals(src)) { //$NON-NLS-1$
		}
	}

	/**
	 * DOCME!
	 */
	public void apply() {
		layer.setName(nameField.getText());
		layer.setVisible(visibleButton.isSelected());
		layer.setFrozen(frozenButton.isSelected());
		layer.setLocked(lockedButton.isSelected());
		layer.setColor(colorButton.getBackground());

		//	layer.setLineStyle((LineStyle) lineStyleCombo.getSelectedItem());
		//	layer.setThickness();
	}

	/**
	 * DOCME!
	 *
	 * @param bean DOCME!
	 */
	public void setObject(Object bean) {
		//if (!(bean instanceof Layer)) {}
		this.layer = (Layer) bean;

		visibleButton.setIcon(layer.isVisible() ? Icons.VISIBLE_ICON : Icons.INVISIBLE_ICON);
		visibleButton.setSelected(layer.isVisible());
		frozenButton.setIcon(layer.isFrozen() ? Icons.FROZEN_ICON : Icons.UNFROZEN_ICON);
		frozenButton.setSelected(layer.isFrozen());
		lockedButton.setIcon(layer.isLocked() ? Icons.LOCKED_ICON : Icons.UNLOCKED_ICON);
		lockedButton.setSelected(layer.isLocked());
		colorButton.setBackground(layer.getColor());
	}

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		/*
		                Should try to operate with reflection, since layers must not necessarily
		                inherit the DefaultLayer class.
		*/
		try {
			((Layer) layer).removePropertyChangeListener(listener);
		} catch (Exception e) { /* some error handling? */
		}
	}

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		/*
		                Should try to operate with reflection, since layers must not necessarily
		                inherit the DefaultLayer class.
		*/
		try {
			((Layer) layer).addPropertyChangeListener(listener);
		} catch (Exception e) { /* some error handling? */
		}
	}

	/**
	 * DOCME!
	 *
	 * @param args DOCME!
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(100, 100, 320, 500);
		f.getContentPane().add(new LayerCustomizer());
		f.setVisible(true);
	}
}