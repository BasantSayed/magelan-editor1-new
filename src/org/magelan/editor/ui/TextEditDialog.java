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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.ColorComboBox;
import org.magelan.commons.ui.DividerLabel;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.LabelledItemPanel;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.entity.TextEntity;
import org.magelan.core.style.TextStyle;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.Editor;


/**
 * 
 * @author Assen Antov
 * @version 2.0, 08/2004
 */
public class TextEditDialog extends JDialog implements ActionListener {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	protected TextEntity text;
	protected JLabel banner;
	protected JTabbedPane tab;
	
	protected DrawingModel model;
	
	private LabelledItemPanel pane;
	private JTextField field, size, x, y, rotation;
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox style;
	private ColorComboBox color;
	
	
	public TextEditDialog() {
		this(new TextEntity(), new DefaultCoreModel(), null);
	}

	public TextEditDialog(TextEntity text, DrawingModel model, java.awt.Frame parent) {
		super(parent, true);

		this.text = text;
		this.model = model;
		
		construct();
	}
	
	protected void construct() {

		// JFrame
		setName("Edit"); //$NON-NLS-1$
		setBounds(0, 0, 300, 60);
		setTitle(lang.getString("TextEditDialog.title")); //$NON-NLS-1$
		setResizable(true);
		getContentPane().setLayout(new BorderLayout(5, 5));

		//
		banner = new JLabel();
		UIFactory.configureBanner(banner, lang.getString("TextEditDialog.banner"), null/*Icons.EDIT_TEXT_ICON*/); //$NON-NLS-1$
		getContentPane().add(banner, BorderLayout.NORTH);
		
//		tab = new JTabbedPane();
//		getContentPane().add(tab, BorderLayout.CENTER);
		
		pane = new LabelledItemPanel();
		
			DividerLabel div1 = new DividerLabel(lang.getString("TextEditDialog.divider.general")); //$NON-NLS-1$
			pane.addDivider(div1);
			
			// text 
			field = new JTextField();
			field.setText(text.getText());
			field.selectAll();
			Dimension dim1 = new Dimension(200, field.getPreferredSize().height);
			field.setPreferredSize(dim1);
			field.setMinimumSize(dim1);
			pane.addItem(lang.getString("TextEditDialog.label.edit"), field); //$NON-NLS-1$
			
			// text style
			Vector v = new Vector();
			v.add(null);
			v.addAll(model.getFeatures(TextStyle.class));
			
			style = new JComboBox(v);
			style.setRenderer(new TextStyleListRenderer());
//			style.setSelectedItem(model.getCurrent(TextStyle.class));
			style.setSelectedItem(text.getTextStyle());
//			Dimension dim3 = new Dimension(75, style.getPreferredSize().height);
//			style.setPreferredSize(dim3);
//			style.setMinimumSize(dim3);
			pane.addItem(lang.getString("TextEditDialog.label.style"), style, false); //$NON-NLS-1$
			
			// entity color
			color = new ColorComboBox();
			color.setSelectedItem(text.getColor());
			//Dimension dim3 = new Dimension(75, color.getPreferredSize().height);
			//color.setPreferredSize(dim3);
			//color.setMinimumSize(dim3);
			pane.addItem(lang.getString("TextEditDialog.label.color"), color, false); //$NON-NLS-1$
			
			DividerLabel div2 = new DividerLabel(lang.getString("TextEditDialog.label.geometry")); //$NON-NLS-1$
			pane.addDivider(div2);
			
			// x coordinate
			x = new JTextField();
			x.setText("" + text.getX()); //$NON-NLS-1$
			Dimension dim4 = new Dimension(80, x.getPreferredSize().height);
			x.setPreferredSize(dim4);
			x.setMinimumSize(dim4);
			pane.addItem(lang.getString("TextEditDialog.label.x"), x, false); //$NON-NLS-1$
			
			// y coordinate
			y = new JTextField();
			y.setText("" + text.getY()); //$NON-NLS-1$
			y.setPreferredSize(dim4);
			y.setMinimumSize(dim4);
			pane.addItem(lang.getString("TextEditDialog.label.y"), y, false); //$NON-NLS-1$
			
			// text size
			size = new JTextField();
			size.setText("" + text.getSize()); //$NON-NLS-1$
			Dimension dim2 = new Dimension(50, size.getPreferredSize().height);
			size.setPreferredSize(dim2);
			size.setMinimumSize(dim2);
			pane.addItem(lang.getString("TextEditDialog.label.size"), size, false); //$NON-NLS-1$
			
			// rotation
			rotation = new JTextField();
			rotation.setText("" + text.getRotation()); //$NON-NLS-1$
			Dimension dim5 = new Dimension(50, rotation.getPreferredSize().height);
			rotation.setPreferredSize(dim5);
			rotation.setMinimumSize(dim5);
			pane.addItem(lang.getString("TextEditDialog.label.rotation"), rotation, false); //$NON-NLS-1$
			
		//tab.add("Text", pane);
		getContentPane().add(pane, BorderLayout.CENTER);
		
		//
		JPanel p2 = new JPanel();
		
		p2.setBorder(UIFactory.createTopBorder());
		
			// JButton
			okButton = new JButton();
			okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
			okButton.setIcon(Icons.OK_ICON);
			okButton.setName("okButton"); //$NON-NLS-1$
			p2.add(okButton);
	
			// JButton
			cancelButton = new JButton();
			cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
			cancelButton.setIcon(Icons.CANCEL_ICON);
			cancelButton.setName("cancelButton"); //$NON-NLS-1$
			p2.add(cancelButton);

		getContentPane().add(p2, BorderLayout.SOUTH);
		
		pack();
		org.magelan.commons.ui.SwingUtil.center(this);

		getRootPane().setDefaultButton(okButton);

		// set listeners
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}


	protected void apply() {
		text.setText(field.getText());
		text.setTextStyle((TextStyle) style.getSelectedItem());
		text.setColor((Color) color.getSelectedItem());
		text.setSize(Float.parseFloat(size.getText()));
		text.setX(Double.parseDouble(x.getText()));
		text.setY(Double.parseDouble(y.getText()));
		text.setRotation(Float.parseFloat(rotation.getText()));
	}
		
	/**
	 * Listens for action events.
	 *
	 * @param e an event
	 */
	public void actionPerformed(ActionEvent e) {
		String src = ((java.awt.Component) e.getSource()).getName();

		if ("okButton".equals(src)) { //$NON-NLS-1$
			apply();
			setVisible(false);
			dispose();
		}
		else if ("cancelButton".equals(src)) { //$NON-NLS-1$
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Opens a new dialog.
	 *
	 * @param args array of parameter strings
	 */
	public static void main(String[] args) {
		(new TextEditDialog()).setVisible(true);
	}
}