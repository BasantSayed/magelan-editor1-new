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
package org.magelan.surfer.resources;

import org.magelan.commons.ui.*;
import org.magelan.surfer.*;
import org.magelan.surfer.ui.*;
import org.magelan.surfer.resources.help.HelpItem;
import java.awt.event.*;
import java.io.*;
import java.awt.*;
import java.net.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * 
 * 
 * @version	2.0, 02/2004
 * @author	Assen Antov
 */
public class HelpResource implements Resource {
	
	private static HelpResource hr;
	private BrowserPane browser;
	private LabelledItemPanel fields;
	private JPanel viewer, editor;
	private JLabel banner;
	private JTextField name, descr, fileName, iconName;
	
	
	private HelpResource() {}
	
	/**
	 * Instantiate the resource descriptor.
	 * 
	 * @return	resource descriptor
	 */
	public static HelpResource getInstance() {
		if (hr == null) {
			hr = new HelpResource();
		}
		return hr;
	}
	
	public boolean isSupported(Object obj) {
		return obj instanceof HelpItem;
	}

	public String getTextFor(Object obj) {
		return ((HelpItem) obj).getName();
	}
	
	public boolean isValid(Object obj) {
		File ref = new File(((HelpItem) obj).getFileName());
		if (ref == null) {
			return false;
		}
		if (ref instanceof File) {
			return ((File) ref).exists();
		}
		return true;
	}
	
	public String getToolTipFor(Object obj) {
		if (!isValid(obj)) {
			return "Could not locate help resource \"" + ((HelpItem) obj).getFileName() + "\"!";
		}
		return ((HelpItem) obj).getDescription();
	}
	
	public Icon getIconFor(Object obj) {
		if (!isValid(obj)) {
			return Icons.ERROR_ICON;
		}
		return Icons.NEW_ICON;
	}
	
	private JTextPane txt;
	private Action[] actions;
	private Action create;
	
	
	public Component getViewerFor(DefaultMutableTreeNode node) {
		if (!isValid(node.getUserObject())) {
			return null;
		}
		
		HelpItem item = (HelpItem) node.getUserObject();
		
		URL url = null;
		File ref = new File(item.getFileName());
		try {
			url = ((File) ref).toURL();
		} catch (Exception e) {
			return null;
		}
		
		if (viewer == null) {
			viewer = new JPanel();
			viewer.setLayout(new BorderLayout());
			
			//banner = new JLabel();
			//viewer.add(banner, BorderLayout.NORTH);
			
			browser = new BrowserPane(url);
			viewer.add(browser, BorderLayout.CENTER);
		}
		
		/*
		UIFactory.configureBanner(
			banner, 
			item.getDescription(),
			new ImageIcon(item.getIconName())
		);
		banner.setBorder(new javax.swing.border.EmptyBorder(2, 3, 2, 0));
		*/
		browser.setPage(url);
		
		return viewer;
	}


	public Component getEditorFor(DefaultMutableTreeNode node) {
		if (editor == null) {
			editor = new JPanel();
			editor.setLayout(new BorderLayout());
			
			fields = new LabelledItemPanel();
			
			name = new JTextField();
			fields.addItem("Name:", name);
			
			descr = new JTextField();
			fields.addItem("Description:", descr);
			
			fileName = new JTextField();
			fields.addItem("Help File Name:", fileName);
			
			iconName = new JTextField();
			fields.addItem("Icon Name:", iconName);
			
			editor.add(fields, BorderLayout.NORTH);
			
			txt = new JTextPane();
			editor.add(new JScrollPane(txt), BorderLayout.CENTER);
		}
		
		String nl = System.getProperty("line.separator");
		
		HelpItem item = (HelpItem) node.getUserObject();
		
		name.setText(item.getName());
		descr.setText(item.getDescription());
		fileName.setText(item.getFileName());
		iconName.setText(item.getIconName());
		
		txt.setEditable(true);
		String text = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(item.getFileName())));
			String s;
			while ((s=in.readLine()) != null) {
				text += s + nl;
			}
		} 
		catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		txt.setText(text);
		
		return editor;
	}
	
	public Action[] getEditActionsFor(DefaultMutableTreeNode node) {
		if (actions == null) {
			actions = new Action[] { new UpdateCommand() };
		}
		
		((UpdateCommand) actions[0]).setNode(node);
		return actions;
	}
	
	public Action[] getViewActionsFor(DefaultMutableTreeNode node) {
		return null;
	}
	
	public Action getCreateActionFor(DefaultMutableTreeNode node) {
		if (create == null) {
			create = new CreateCommand();
		}
		
		((CreateCommand) create).setNode(node);
		return create;
	}
	
	/**
	 * Update command.
	 */
	class UpdateCommand extends AbstractAction {
		
		private DefaultMutableTreeNode node;
		
		public UpdateCommand() {
			super();
			
			putValue(Action.NAME, "Update");
			putValue(Action.SHORT_DESCRIPTION, "Update item value");
			putValue(Action.LONG_DESCRIPTION, "Update item value");
			putValue(Action.SMALL_ICON, Icons.SAVE_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
		}

		public void setNode(DefaultMutableTreeNode node) {
			this.node = node;
		}
		
		public void actionPerformed(ActionEvent e) {
			HelpItem item = (HelpItem) node.getUserObject();
			
			item.setName(name.getText());
			item.setDescription(descr.getText());
			item.setFileName(fileName.getText());
			item.setIconName(iconName.getText());
			
			File file = new File(item.getFileName());
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(txt.getText());
				out.flush();
				out.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * Create command.
	 */
	class CreateCommand extends AbstractAction {
		
		private DefaultMutableTreeNode node;
		
		public CreateCommand() {
			super();
			
			putValue(Action.NAME, "Create Help Item");
			putValue(Action.SHORT_DESCRIPTION, "Create a new help item");
			putValue(Action.LONG_DESCRIPTION, "Create a new help item");
			putValue(Action.SMALL_ICON, Icons.HELP_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
		}

		public void setNode(DefaultMutableTreeNode node) {
			this.node = node;
		}
		
		public void actionPerformed(ActionEvent e) {
			node.add(DataTree.createNode(
				new HelpItem("Help Item", "A new help item", "")
			));
		}
	}
}