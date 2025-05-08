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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import org.magelan.commons.ui.Icons;
import org.magelan.surfer.DataTree;
import org.magelan.surfer.Resource;

/**
 * This resource descriptor is used for viewing and editing 
 * <code>String</code>s.
 * 
 * @version	1.2, 02/2004
 * @author	Assen Antov
 */
public class StringResource implements Resource {
	
	private static StringResource sr;
	
	private StringResource() {}
	
	/**
	 * Instantiate the resource descriptor.
	 * 
	 * @return	resource descriptor
	 */
	public static StringResource getInstance() {
		if (sr == null) {
			sr = new StringResource();
		}
		return sr;
	}
	
	public boolean isSupported(Object obj) {
		return obj instanceof String;
	}

	public String getTextFor(Object obj) {
		if (20 > ((String) obj).length()) {
			return (String) obj;
		}
		return ((String) obj).substring(0, 20) + "...";
	}
	
	public boolean isValid(Object obj) {
		return true;
	}
	
	public String getToolTipFor(Object obj) {
		return (String) obj;
	}
	
	public Icon getIconFor(Object obj) {
		return Icons.NOTE_ICON;
	}
	
	private JTextPane txt;
	private Action[] actions;
	private Action create;
	
	public Component getViewerFor(DefaultMutableTreeNode node) {
		// do not show a viewer for non-leaf nodes
		if (!node.isLeaf()) {
			return null;
		}
		
		Component editor = getEditorFor(node);
		txt.setEditable(false);
		return editor;
	}
	
	public Component getEditorFor(DefaultMutableTreeNode node) {
		if (txt == null) {
			txt = new JTextPane();
		}
		
		txt.setEditable(true);
		txt.setText((String) node.getUserObject());
		
		return new JScrollPane(txt);
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
			node.setUserObject(txt.getText());
		}
	}

	/**
	 * Create command.
	 */
	class CreateCommand extends AbstractAction {
		
		private DefaultMutableTreeNode node;
		
		public CreateCommand() {
			super();
			
			putValue(Action.NAME, "Create Note");
			putValue(Action.SHORT_DESCRIPTION, "Create a new note item");
			putValue(Action.LONG_DESCRIPTION, "Create a new note item");
			putValue(Action.SMALL_ICON, Icons.NOTE_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}

		public void setNode(DefaultMutableTreeNode node) {
			this.node = node;
		}
		
		public void actionPerformed(ActionEvent e) {
			node.add(DataTree.createNode("New Note"));
		}
	}
}

