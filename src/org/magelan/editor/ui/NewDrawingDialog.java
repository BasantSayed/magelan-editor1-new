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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.ListDataModel;
import org.magelan.commons.ui.ListRenderer;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.editor.DefaultDrawingTemplate;
import org.magelan.editor.DrawingTemplate;
import org.magelan.editor.Editor;


/**
 * Displays a template chooser dialog.
 *
 * @author Assen Antov
 * @version 2.0, 12/2003
 */
public class NewDrawingDialog extends JDialog implements ActionListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private java.util.List templates;
	private DrawingTemplate chosen;

	// variables
	private JLabel statusLine;
	private JButton cancelButton, okButton;
	private JPanel component12, component6, panel1;
	private JList list;
	private JScrollPane scroll;
	private JTabbedPane tab;
	
	//~ Constructors -----------------------------------------------------------

	public NewDrawingDialog() {
		this(new java.util.ArrayList(), null);
	}

	public NewDrawingDialog(java.util.List templates, java.awt.Frame parent) {
		super(parent, true);

		// JFrame
		setName("New"); //$NON-NLS-1$
		setBounds(361, 236, 378, 240);
		setTitle(lang.getString("NewDrawingDialog.newDrawing")); //$NON-NLS-1$
		setResizable(true);
		getContentPane().setLayout(new BorderLayout(3, 3));

		// JList
		list = new JList();
		list.setName("list"); //$NON-NLS-1$

		// JScrollPane
		scroll = new JScrollPane(list);
		scroll.setName("scroll"); //$NON-NLS-1$
		scroll.setPreferredSize(new Dimension(250, 200));

		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(250, 200));
		tab.addTab(lang.getString("NewDrawingDialog.new")/*, Icons.NEW_ICON*/, scroll); //$NON-NLS-1$
		tab.setMnemonicAt(0, KeyEvent.VK_N);
		
		getContentPane().add(tab, BorderLayout.CENTER);

		// JPanel
		component6 = new JPanel();
		component6.setName("component6"); //$NON-NLS-1$
		component6.setLayout(new GridBagLayout());
		getContentPane().add(component6, BorderLayout.EAST);
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.gridx   = 0;
		constraints.gridy   = 99;
		constraints.insets  = new Insets(0, 0, 0, 0);
		constraints.weighty = 1.0;
		constraints.fill    = GridBagConstraints.VERTICAL;
		component6.add(new javax.swing.JPanel(), constraints);
			
		// JButton
		constraints.gridx   = 0;
		constraints.gridy   = 1;
		constraints.insets  = new Insets(5, 5, 0, 5);
		constraints.weightx = 1.0;
		constraints.weighty = 0;
		constraints.anchor  = GridBagConstraints.NORTH;
		constraints.fill    = GridBagConstraints.HORIZONTAL;
		
		okButton = new JButton();
		okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
		okButton.setIcon(Icons.OK_ICON);
		okButton.setName("okButton"); //$NON-NLS-1$
		okButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
		component6.add(okButton, constraints);

		// JButton
		constraints.gridy   = 2;
		
		cancelButton = new JButton();
		cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
		cancelButton.setIcon(Icons.CANCEL_ICON);
		cancelButton.setName("cancelButton"); //$NON-NLS-1$
		cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
		component6.add(cancelButton, constraints);

		// JLabel
		statusLine = new JLabel();
		statusLine.setName("statusLine"); //$NON-NLS-1$

		statusLine.setBorder(BorderFactory.createLineBorder(statusLine.getBackground().darker()));
		Font f = statusLine.getFont();
		statusLine.setFont(new Font(f.getName(), Font.PLAIN, f.getSize())); //$NON-NLS-1$
		getContentPane().add(statusLine, BorderLayout.SOUTH);
		
		setModal(true);
		SwingUtil.center(this);

		this.templates = templates;
		
		// list setup
		list.setModel(new ListDataModel(templates));
		list.setCellRenderer(new TemplateRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = list.locationToIndex(e.getPoint());

					if (index != -1) {
						chosen = (DrawingTemplate) list.getSelectedValue();
					}

					setVisible(false);
					dispose();
				}
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				statusLine.setText(((DrawingTemplate) list.getSelectedValue()).getDescription());
			}
		});

		if (templates.size() > 0) {
			list.setSelectedIndex(0);
		}

		getRootPane().setDefaultButton(okButton);

		// set listeners
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Listens for action events.
	 * 
	 * <p></p>
	 *
	 * @param e an event
	 */
	public void actionPerformed(ActionEvent e) {
		String src = ((java.awt.Component) e.getSource()).getName();

		if ("okButton".equals(src)) { //$NON-NLS-1$
			if (list.getSelectedIndex() != -1) {
				chosen = (DrawingTemplate) list.getSelectedValue();
			}

			setVisible(false);
		}
		else if ("cancelButton".equals(src)) { //$NON-NLS-1$
			chosen = null;
			setVisible(false);
		}
	}

	public DrawingTemplate getTemplate() {
		return chosen;
	}

	/**
	 * Opens a new forms dialog.
	 * 
	 * <p></p>
	 *
	 * @param args array of parameter strings
	 */
	public static void main(String[] args) {
		java.util.List list = new ArrayList();
		list.add(new DefaultDrawingTemplate());
		NewDrawingDialog d = new NewDrawingDialog(list, null);
		d.setVisible(true);
	}

	//~ Inner Classes ----------------------------------------------------------
	/**
	 * A renderer for templates. Shows the template icon next to the template
	 * name and sets a tooltip text after the template's description.
	 */
	class TemplateRenderer extends ListRenderer {

		public TemplateRenderer () {
			super();
		}

		public Component getListCellRendererComponent(JList list, Object value, 
			int index, boolean isSelected, boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setText(((DrawingTemplate) value).getName());
			return this;
		}
		
		public Icon getIcon(Object value) {
			return ((DrawingTemplate) value).getIcon();
		}

		public String getToolTipText(Object value) {
			return ((DrawingTemplate) value).getDescription();
		}
	}
}