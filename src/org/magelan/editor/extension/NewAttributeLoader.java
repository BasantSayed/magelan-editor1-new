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
package org.magelan.editor.extension;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.editor.Editor;


/**
 *
 * @author Assen Antov
 * @version 1.0, 06/2002
 *
 * @since
 */
public class NewAttributeLoader extends JDialog {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	// variables
	public JLabel statusLine;
	public JButton cancelButton;
	public JButton okButton;
	public JPanel component12;
	public JButton removeButton;
	public JButton modifyButton;
	public JButton addButton;
	public JPanel component6;
	public JList list;
	public JScrollPane scroll;
	public JPanel panel1;
	JTextField name;
	JComboBox type;
	JTextField value;


	/**
	 * Creates a new NewAttributeLoader object.
	 */
	public NewAttributeLoader() {
		this(null);
	}

	/**
	 */
	public NewAttributeLoader(Frame parent) {
		super(parent, true);

		// JFrame
		setName("NewAttribute"); //$NON-NLS-1$
		setBounds(361, 236, 378, 150);
		setTitle(lang.getString("NewAttribute.title")); //$NON-NLS-1$
		setResizable(true);
		getContentPane().setLayout(new BorderLayout(3, 3));

		panel1 = new JPanel();
		panel1.setPreferredSize(new Dimension(250, 100));
		panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel1.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),
														  lang.getString("NewAttribute.banner"))); //$NON-NLS-1$
		getContentPane().add(panel1, BorderLayout.CENTER);

		JLabel l = new JLabel(lang.getString("common.name")); //$NON-NLS-1$
		l.setPreferredSize(new Dimension(80, l.getPreferredSize().height));
		panel1.add(l);

		name = new JTextField();
		name.setPreferredSize(new Dimension(150, name.getPreferredSize().height));
		panel1.add(name);

		l = new JLabel(lang.getString("common.type")); //$NON-NLS-1$
		l.setPreferredSize(new Dimension(80, l.getPreferredSize().height));
		panel1.add(l);

		java.util.Vector types = new java.util.Vector(4);
		types.add("String"); //$NON-NLS-1$
		types.add("Integer"); //$NON-NLS-1$
		types.add("Real"); //$NON-NLS-1$
		types.add("Boolean"); //$NON-NLS-1$

		type = new JComboBox(types);
		type.setPreferredSize(new Dimension(90, name.getPreferredSize().height));
		panel1.add(type);

		l = new JLabel(lang.getString("common.value")); //$NON-NLS-1$
		l.setPreferredSize(new Dimension(80, l.getPreferredSize().height));
		panel1.add(l);

		value = new JTextField();
		value.setPreferredSize(new Dimension(150,
											 value.getPreferredSize().height));
		panel1.add(value);

		// JPanel
		component6 = new JPanel();
		component6.setName("component6"); //$NON-NLS-1$
		component6.setPreferredSize(new Dimension(100, 200));
		getContentPane().add(component6, BorderLayout.EAST);

		/*
		        // JButton
		        addButton = new JButton();
		        addButton.setText("Add");
		        addButton.setMargin(new Insets(2, 14, 2, 14));
		        addButton.setName("addButton");
		        addButton.setPreferredSize(new Dimension(81, 27));
		        component6.add(addButton);

		        // JButton
		        modifyButton = new JButton();
		        modifyButton.setText("Modify");
		        modifyButton.setMargin(new Insets(2, 14, 2, 14));
		        modifyButton.setName("modifyButton");
		        modifyButton.setPreferredSize(new Dimension(81, 27));
		        component6.add(modifyButton);

		        // JButton
		        removeButton = new JButton();
		        removeButton.setText("Remove");
		        removeButton.setMargin(new Insets(2, 0, 2, 0));
		removeButton.setIcon(new ImageIcon("magelan/images/delete.gif"));
		        removeButton.setName("removeButton");
		        removeButton.setPreferredSize(new Dimension(81, 27));
		        component6.add(removeButton);

		        // JPanel
		        component12 = new JPanel();
		        component12.setName("component12");
		        component12.setPreferredSize(new Dimension(50, 15));
		        component6.add(component12);
		*/
		// JButton
		okButton = new JButton();
		okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
		okButton.setIcon(Icons.OK_ICON);
		okButton.setName("okButton"); //$NON-NLS-1$
		component6.add(okButton);

		// JButton
		cancelButton = new JButton();
		cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
		cancelButton.setIcon(Icons.CANCEL_ICON);
		cancelButton.setName("cancelButton"); //$NON-NLS-1$
		component6.add(cancelButton);

		/*
		// JLabel
		statusLine = new JLabel();
		statusLine.setText(" ");
		statusLine.setName("statusLine");
		//statusLine.setPreferredSize(new Dimension(360, 27));
		statusLine.setBorder(new border.EtchedBorder());
		statusLine.setFont(new Font("Arial", Font.PLAIN, 12));
		getContentPane().add(statusLine, BorderLayout.SOUTH);
		*/
	}
}