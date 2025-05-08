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
package org.magelan.surfer;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.tree.DefaultMutableTreeNode;

import org.magelan.commons.ui.Icons;
import org.magelan.surfer.resources.help.HelpItem;

/**
 * 
 * @version	2.0, 02/2004
 * @author	Assen Antov
 */
public class HelpSurfer extends DataSurfer {
	
	public HelpSurfer() {
		super();
		
		setToolsVisible(true);
		setTitle("Help");
		setIconImage(((ImageIcon) Icons.HELP_ICON).getImage());
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		getJMenuBar().setVisible(false); // hide menu
		getContentPane().remove(main); // remove toolbar
	}
	
	public static void main(String[] args) {
		HelpSurfer hs = new HelpSurfer();
		
		DefaultMutableTreeNode root = DataTree.createNode("Welcome!");
			
			root.add(DataTree.createNode(new HelpItem("Contents", "Help contents", "C:\\eclipse\\notice.html")));
			root.add(DataTree.createNode(new HelpItem("Index", "Help index", "C:\\eclipse\\cpl-v10.html")));
			
		// data tree
		DataTree dt = new DataTree(root);
		hs.addTree("Explorer", dt);
		
		hs.setVisible(true);
	}
}
