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
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.magelan.commons.ui.LabelledItemPanel;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.style.Layer;

/**
 * <p></p>
 *
 * @author Assen Antov
 * @version 2.0, 01/2004
 */
public class LayerControlPanel extends LabelledItemPanel {
	
	private Layer layer;

	// variables
	private JTextField nameField;
	private JPanel accessPanel;
	private LayerVisibleEditor visibleButton;
	private LayerFrozenEditor frozenButton;
	private LayerLockedEditor lockedButton;
	private JButton colorButton;
	private AlphaCompositeControl alpha;
	private LineStyleControl lineStyleCombo;
	private ThicknessEditor thicknessCombo;

	/**
	 *
	 */
	public LayerControlPanel() {
		this(new Layer());
	}

	/**
	 */
	public LayerControlPanel(Layer layer) {
		// JFrame
		setPreferredSize(new Dimension(160, 200));
		setMinimumSize(new Dimension(160, 200)); //

		//Lang.getString("Layer_Control_2")); //$NON-NLS-1$

		// JTextField
		nameField = new JTextField();
		nameField.setName("nameField"); //$NON-NLS-1$
		nameField.setPreferredSize(new Dimension(150, 20));
		addItem("Name:", nameField); //$NON-NLS-1$

		// JPanel
		accessPanel = new JPanel();
		accessPanel.setName("accessPanel"); //$NON-NLS-1$
		accessPanel.setPreferredSize(new Dimension(150, 27));
		accessPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		accessPanel.setPreferredSize(new Dimension(150, 32));
		addItem("Access:", accessPanel); //$NON-NLS-1$

		// JButton
		visibleButton = new LayerVisibleEditor();
		accessPanel.add(visibleButton);

		// JButton
		frozenButton = new LayerFrozenEditor();
		accessPanel.add(frozenButton);

		// JButton
		lockedButton = new LayerLockedEditor();
		accessPanel.add(lockedButton);

		// JButton
		colorButton = new JButton();
		colorButton.setText(""); //$NON-NLS-1$
		colorButton.setActionCommand("colorButton"); //$NON-NLS-1$
		colorButton.setName("colorButton"); //$NON-NLS-1$
		colorButton.setPreferredSize(new Dimension(20, 20));
		accessPanel.add(colorButton);

		alpha = new AlphaCompositeControl();
		alpha.setPreferredSize(new Dimension(150, 21));
		addItem("Alpha:", alpha); //$NON-NLS-1$

		// JComboBox
		lineStyleCombo = new LineStyleControl();
		
		lineStyleCombo.setDrawing(new DefaultCoreModel());	// FIX THIS!!!
		
		lineStyleCombo.setName("lineStyleCombo"); //$NON-NLS-1$
		lineStyleCombo.setPreferredSize(new Dimension(150, 20));
		addItem("Style:", lineStyleCombo); //$NON-NLS-1$

		// JComboBox
		thicknessCombo = new ThicknessEditor();
		thicknessCombo.setActionCommand("comboBoxChanged"); //$NON-NLS-1$
		thicknessCombo.setEditable(false);
		thicknessCombo.setMaximumRowCount(8);
		thicknessCombo.setName("thicknessCombo"); //$NON-NLS-1$
		thicknessCombo.setPreferredSize(new Dimension(150, 20));
		addItem("Thickness:", thicknessCombo); //$NON-NLS-1$

		//setLayer(layer);
	}

	//~ Methods ----------------------------------------------------------------

	public JPanel createPanel(String name) {
		JPanel panel = new JPanel();
		panel.setName(name);
		panel.setPreferredSize(new Dimension(150, 27));
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		return panel;
	}

	public void setLayer(Layer l) {
		layer = l;
		update(l);
	}

	public Layer getLayer() {
		return layer;
	}

	private void update(Layer layer) {
		nameField.setText(layer.getName());
		visibleButton.setLayer(layer);
		frozenButton.setLayer(layer);
		lockedButton.setLayer(layer);
		colorButton.setBackground(layer.getColor());
		alpha.setLayer(layer);
		lineStyleCombo.setSelectedItem(layer.getLineStyle());
		thicknessCombo.setValue(new Integer((int) layer.getThickness()));
	}


	public void setBackground(Color col) {
		super.setBackground(col);
		SwingUtil.setBackground(this, col);
	}

	public void setForeground(Color col) {
		super.setForeground(col);
		SwingUtil.setForeground(this, col);
	}

	public void setEnabled(boolean e) {
		super.setEnabled(e);
		SwingUtil.setEnabled(this, e);
	}


	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setBounds(100, 100, 170, 220);
		f.getContentPane().add(new LayerControlPanel());
		f.setVisible(true);
	}
}