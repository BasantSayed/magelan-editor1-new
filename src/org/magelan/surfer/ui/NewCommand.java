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
package org.magelan.surfer.ui;

import org.magelan.commons.ui.*;
import org.magelan.surfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;


/**
 * 
 * <p>
 * @author Assen Antov
 * @version 1.0, 07/2003
 */
public class NewCommand extends AbstractAction {

	private DataSurfer ds;

	public NewCommand(DataSurfer ds) {
		super();
		this.ds = ds;
		putValue(Action.NAME, "New");
		putValue(Action.SHORT_DESCRIPTION, "New tree");
		putValue(Action.LONG_DESCRIPTION, "Creates a new data tree");
		putValue(Action.SMALL_ICON, Icons.NEW_ICON);
		putValue(Action.ACCELERATOR_KEY,
				 KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
	}


	public void actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode root = DataTree.createNode("Root");
		DataTree dt = new DataTree(root);
		ds.addTree(DataSurfer.NONAME, dt);
		ds.setMain(null);
		
		JTabbedPane tab = ds.getJTabbedPane();
		tab.setSelectedIndex(tab.getTabCount()-1);
	}
}