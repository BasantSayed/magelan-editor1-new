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
package org.magelan.editor.extension.explorer;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.magelan.commons.ui.Icons;
import org.magelan.core.style.TextStyle;
import org.magelan.surfer.Resource;

/**
 * 
 * @version	1.0, 08/2004
 * @author	Assen Antov
 */
public class TextStyleResource implements Resource {
	
	private static TextStyleResource dr;
	
	private TextStyleResource() {}
	
	/**
	 * Instantiate the resource descriptor.
	 * 
	 * @return	resource descriptor
	 */
	public static TextStyleResource getInstance() {
		if (dr == null) {
			dr = new TextStyleResource();
		}
		return dr;
	}
	
	public boolean isSupported(Object obj) {
		return obj instanceof TextStyle;
	}

	public String getTextFor(Object obj) {
		return ((TextStyle) obj).getName();
	}
	
	public boolean isValid(Object obj) {
		return true;
	}
	
	public String getToolTipFor(Object obj) {
		return ((TextStyle) obj).getName();
	}
	
	public Icon getIconFor(Object obj) {
		return Icons.TEXT_STYLE_ICON;
	}
	/*
	private JTextPane txt;
	private Action[] actions;
	private Action create;
	*/
	public Component getViewerFor(DefaultMutableTreeNode node) {
	/*	// do not show a viewer for non-leaf nodes
		if (!node.isLeaf()) {
			return null;
		}
		
		Component editor = getEditorFor(node);
		txt.setEditable(false);*/
		return null;
	}
	
	public Component getEditorFor(DefaultMutableTreeNode node) {
	/*	if (txt == null) {
			txt = new JTextPane();
		}
		
		txt.setEditable(true);
		txt.setText((String) node.getUserObject());
	*/	
		return null; //new JScrollPane(txt);
	}
	
	public Action[] getEditActionsFor(DefaultMutableTreeNode node) {
	/*	if (actions == null) {
			actions = new Action[] { new UpdateCommand() };
		}
		
		((UpdateCommand) actions[0]).setNode(node);
		return actions;
		*/
		return null;
	}
	
	public Action[] getViewActionsFor(DefaultMutableTreeNode node) {
		return null;
	}
	
	public Action getCreateActionFor(DefaultMutableTreeNode node) {
		/*if (create == null) {
			create = new CreateCommand();
		}
		
		((CreateCommand) create).setNode(node);
		return create;
		*/
		return null;
	}
	

//	/**
//	 * Update command.
//	 */
//	class UpdateCommand extends AbstractAction {
//		
//		private DefaultMutableTreeNode node;
//		
//		public UpdateCommand() {
//			super();
//			
//			putValue(Action.NAME, "Update");
//			putValue(Action.SHORT_DESCRIPTION, "Update item value");
//			putValue(Action.LONG_DESCRIPTION, "Update item value");
//			putValue(Action.SMALL_ICON, Icons.SAVE_ICON);
//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
//			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
//		}
//
//		public void setNode(DefaultMutableTreeNode node) {
//			this.node = node;
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			node.setUserObject(txt.getText());
//		}
//	}
//
//	/**
//	 * Create command.
//	 */
//	class CreateCommand extends AbstractAction {
//		
//		private DefaultMutableTreeNode node;
//		
//		public CreateCommand() {
//			super();
//			
//			putValue(Action.NAME, "Create Note");
//			putValue(Action.SHORT_DESCRIPTION, "Create a new note item");
//			putValue(Action.LONG_DESCRIPTION, "Create a new note item");
//			putValue(Action.SMALL_ICON, Icons.NOTE_ICON);
//			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
//			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
//		}
//
//		public void setNode(DefaultMutableTreeNode node) {
//			this.node = node;
//		}
//		
//		public void actionPerformed(ActionEvent e) {
//			node.add(DataTree.createNode("New Note"));
//		}
//	}
}

