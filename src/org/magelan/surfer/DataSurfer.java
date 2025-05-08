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

import org.magelan.commons.ui.*;
import org.magelan.surfer.ui.*;
import org.magelan.surfer.resources.help.*;

import org.magelan.commons.ui.Icons;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * 
 * @version	1.1, 02/2004
 * @author	Assen Antov
 */
public class DataSurfer extends JFrame {
	
	public static final String NONAME = "[unnamed]";
	
	private JSplitPane split;
	private JPanel panel;
	private JTabbedPane tab;
	protected JToolBar main, toolbar;

	private static JPanel BLANK = new JPanel();


	public DataSurfer() {
		init();
	}
	
	private void init() {
		setBounds(0, 0, 800, 600);
		setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		SwingUtil.center(this);
		setTitle("Data Surfer");
		setIconImage(((ImageIcon) Icons.GLOBE_ICON).getImage());
		
		// file menu
		setJMenuBar(new JMenuBar());
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		getJMenuBar().add(file);
		
		// main toolbar
		main = new JToolBar("Main");
		main.setRollover(true);
		getContentPane().add(main, BorderLayout.NORTH);
		
		// add commands
		Action cNew = new NewCommand(this);
		file.add(createMenuItem(cNew));
		main.add(createToolBarButton(cNew));
		
		Action cOpen = new OpenCommand(this);
		file.add(createMenuItem(cOpen));
		main.add(createToolBarButton(cOpen));

		Action cClose = new CloseCommand(this);
		file.add(createMenuItem(cClose));
		main.add(createToolBarButton(cClose));
		
		file.addSeparator();

		Action cSave = new SaveCommand(this);
		file.add(createMenuItem(cSave));
		main.add(createToolBarButton(cSave));

		Action cSaveAll = new SaveAllCommand(this);
		file.add(createMenuItem(cSaveAll));

		file.addSeparator();
		
		Action cExit = new ExitCommand(this);
		file.add(createMenuItem(cExit));
		
		// split
		split = UIFactory.createSplitPane();
		//split.setOneTouchExpandable(true);
		getContentPane().add(split, BorderLayout.CENTER);
		
		// tabbed pane
		tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(250, 600));
		split.setLeftComponent(tab);
		
		// right panel
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		split.setRightComponent(panel);
		
	}
	
	/**
	 * Adds a <code>DataTree</code> to the tabbed pane on the left. The tree 
	 * to add will be enclosed in a <code>JScrollPane</code>.
	 * 
	 * @param	name	tree name
	 * @param	comp	tree to add
	 */
	public void addTree(String name, DataTree tree) {
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.addMouseListener(new TreeMouseAdapter(tree));
		tab.addTab(name, UIFactory.createScrollPane(tree));
	}
	
	/**
	 * 
	 */
	public void view(DefaultMutableTreeNode node) {
		Resource res = ResourceManager.getResourceFor(node.getUserObject());
		setMain(res.getViewerFor(node));
		addActions(res.getViewActionsFor(node));
	}
	
	/**
	 * 
	 */
	public void edit(DefaultMutableTreeNode node) {
		Resource res = ResourceManager.getResourceFor(node.getUserObject());
		setMain(res.getEditorFor(node));
		addActions(res.getEditActionsFor(node));
	}
	
	/**
	 * Sets the component on the right.
	 * 
	 * @param	comp	the component to add
	 */
	public void setMain(Component comp) {
		panel.removeAll();
		
		panel.add(createToolbar(), BorderLayout.NORTH);

		if (comp != null) {
			panel.add(comp, BorderLayout.CENTER);
		}
		else {
			panel.add(BLANK);
		}

		panel.revalidate();
		panel.repaint();
	}
	
	public void addActions(Action[] a) {
		if (a != null) {
			for (int i = 0; i < a.length; i++) {
				addAction(a[i]);
			}
		}
	}
	
	public void addAction(Action a) {
		if (!toolbar.isVisible()) {
			toolbar.setVisible(true);
		}
		toolbar.add(createToolBarButton(a));
	}
	
	/**
	 * Factory method to create a toolbar for editor actions.
	 */
	JToolBar createToolbar() {
		if (toolbar == null) {
			toolbar = new JToolBar("Tools");
			toolbar.setRollover(true);
		}
		else {
			toolbar.removeAll();
		}
		toolbar.setVisible(false);
		return toolbar;
	}
	
	/**
	 * Factory method to create a menu item from action.
	 * 
	 * @param	a	action
	 */
	JMenuItem createMenuItem(Action a) {
		JMenuItem item = new JMenuItem(a);
		if (a.getValue(Action.SMALL_ICON) == null) {
			item.setIcon(Icons.GAP_ICON_16);
		}
		item.setToolTipText(null);
		return item;
	}
	
	/**
	 * Factory method to create a toolbar button from action.
	 * 
	 * @param	a	action
	 */
	JButton createToolBarButton(Action a) {
		JButton b = new JButton(a);
		b.setText("");
		b.setMargin(new Insets(1, 1, 1, 1));
		b.setFocusPainted(false);
		return b;
	}
	
	private JPopupMenu popup;
	
	/**
	 * Factory method to create a popup menu for node.
	 * 
	 * @param	node	node to create the menu for
	 */
	JPopupMenu createPopupFor(DefaultMutableTreeNode node) {
		if (popup == null) {
			popup = new JPopupMenu();
		}

		popup.removeAll();
		
		popup.add(createMenuItem(new SaveCommand(this)));
		popup.addSeparator();
		popup.add(createMenuItem(new EditCommand(this, node)));
		popup.add(createMenuItem(new DeleteCommand(this, node)));

		Iterator iter = ResourceManager.getResources().iterator();
		while (iter.hasNext()) {
			Resource res = (Resource) iter.next();
			Action a = res.getCreateActionFor(node);
			if (a != null) {
				popup.add(createMenuItem(a));
			}
		}

		return popup;
	}
	
	public JTabbedPane getJTabbedPane() {
		return tab;
	}

	public void setToolsVisible(boolean v) {
		getJMenuBar().setVisible(v);
		main.setVisible(v);
	}

	public DataTree getSelectedTree() {
		int idx = tab.getSelectedIndex();
		if (idx != -1) {
			Component c = tab.getComponentAt(idx);
			if (c instanceof JScrollPane) {
				Component t = ((JScrollPane) c).getViewport().getView();
				if (t instanceof DataTree) {
					return (DataTree) t;
				}
			}
		}
		
		System.out.println("non-tree component found!");
		return null;
	}

	public void exit(int code) {
		System.exit(code);
	}

	/**
	 * 
	 */
	class TreeMouseAdapter extends MouseAdapter {

		JTree tree;

		TreeMouseAdapter(JTree tree) {
			this.tree = tree;
		}

		public void mousePressed(MouseEvent e) {
			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				if (e.getClickCount() == 1) {

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();

					if (e.getButton() == MouseEvent.BUTTON1) {
						view(node);
					}

					else if (e.getButton() == MouseEvent.BUTTON3) {
						JPopupMenu pm = createPopupFor(node);
						pm.show(tree, e.getX(), e.getY());
					}
				}
				else if (e.getClickCount() == 2) {
				}
			}
		}
	}


	public static void main(String[] args) {
		DataSurfer ds = new DataSurfer();
		//ds.setToolsVisible(false);
		
		DefaultMutableTreeNode root = DataTree.createNode("Root");
			
			DefaultMutableTreeNode node = DataTree.createNode("Class Path");
			root.add(node);
			
			node = DataTree.createNode("Notes");
			node.add(DataTree.createNode("To create new nodes press the right mouse button."));
			node.add(DataTree.createNode("This is a note!"));
			root.add(node);
			
			node = DataTree.createNode("Help");
			node.add(DataTree.createNode(new HelpItem("Contents", "Help contents", "C:\\eclipse\\notice.html")));
			node.add(DataTree.createNode(new HelpItem("Index", "Help index", "C:\\eclipse\\cpl-v10.html")));
			node.add(DataTree.createNode(new HelpItem("Assistance", "Invalid item", "dsfs")));
			root.add(node);
			
		// data tree
		DataTree dt = new DataTree(root);
		ds.addTree("Demo", dt);
		
		ds.setVisible(true);
	}
}
