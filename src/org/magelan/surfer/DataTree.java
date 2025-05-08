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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.magelan.commons.ui.Icons;

/**
 * A tree capable of hosting various types of data. Rendering information for 
 * the tree nodes is obtained from <code>Resource</code> descriptors. All 
 * resource descriptors must be added to the <code>ResourceManager</code> in
 * order to be recognized by the renderer.
 * 
 * @version	1.1, 02/2004
 * @author	Assen Antov
 * 
 * @see	Resource
 * @see	ResourceManager
 */
public class DataTree extends JTree {

	
	/**
	 * Constructor for DataTree.
	 */
	public DataTree() {
		super();
		init();
	}

	/**
	 * Constructor for DataTree.
	 * 
	 * @param	node	root tree node
	 */
	public DataTree(TreeNode node) {
		super(node);
		init();
	}
	
	/**
	 * Constructor for DataTree.
	 * 
	 * @param	model	tree model
	 */
	public DataTree(TreeModel model) {
		super(model);
		init();
	}

	/**
	 * Initializes the tree, which is to setup renderer and editir.
	 */
	private void init() {
		TreeCellRenderer renderer = new TreeCellRenderer();
		setCellRenderer(renderer);
		setCellEditor(new DefaultTreeCellEditor(this, renderer));
	}

	/**
	 * Renderer for the <code>Data Tree</code>. The standard leaf icon will 
	 * be substituted with the <code>ImageUtil.NEW_ICON</code>
	 * 
	 * @version	1.0, 07/2003
	 * @author	Assen Antov
	 * 
	 * @see	Resource
	 */
	public class TreeCellRenderer extends DefaultTreeCellRenderer {
		
		public TreeCellRenderer() {
			setLeafIcon(Icons.NEW_ICON);
		}
		
		public java.awt.Component getTreeCellRendererComponent(JTree tree, 
					Object value,
					boolean sel,
					boolean expanded,
					boolean leaf, int row,
					boolean hasFocus) {
			
			setToolTipText(null);
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			if (value instanceof DefaultMutableTreeNode) {
				
				Object val = ((DefaultMutableTreeNode) value).getUserObject();
				if (val != null) {
					
					Resource res = ResourceManager.getResourceFor(val);
					if (res == null) {
						setIcon(Icons.ERROR_ICON);
						setText(val.toString());
						setToolTipText("Resource " + val.getClass().getName() + " not supported!"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else {
						if (leaf) {
							if (res.getIconFor(val) != null) {
								setIcon(res.getIconFor(val));
							}
						}
						setText(res.getTextFor(val));
						setToolTipText(res.getToolTipFor(val));
					}
				}
			}
			
			else {
				setText(value.toString());
			}
			
			return this;
		}
	}
	
	/**
	 * Creates a node for the tree.
	 * 
	 * @param	value	object to be set as node value
	 * @return	a <code>MutableTreeNode</code>
	 */
	public static DefaultMutableTreeNode createNode(Object value) {
		return new DefaultMutableTreeNode(value);
	}
	
	/**
	 * Saves the tree to a file.
	 *
	 * @param	t	tree to save
	 * @param	fileName	name of the file to write the tree to
	 */
	public static void save(DataTree t, String fileName) {
		try {
			XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName), 1048576));
			encoder.writeObject(t.getModel().getRoot());
			encoder.flush();
			encoder.close();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
	
	/**
	 * Reads a tree from a file.
	 *
	 * @param	fileName	name of the file to read the tree from
	 * @return	loaded tree
	 */
	public static DataTree load(String fileName) {
		DefaultMutableTreeNode root = null;
		try {
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName), 1048576));
			root = (DefaultMutableTreeNode) decoder.readObject();
			decoder.close();
		} catch (Throwable th) {
			th.printStackTrace();
		}
		return new DataTree(root);
	}
}
