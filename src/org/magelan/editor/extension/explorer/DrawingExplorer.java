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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.magelan.commons.Beans;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.*;
import org.magelan.commons.ui.tree.TreeCellRenderer;
import org.magelan.core.CoreModel;
import org.magelan.core.db.DataSource;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.renderer.CompoundRenderer;
import org.magelan.core.renderer.Decorator;
import org.magelan.core.style.Layer;
import org.magelan.core.style.LineStyle;
import org.magelan.core.style.TextStyle;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.IFeaturesManager;
import org.magelan.editor.commands.Internal;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.DrawingPropertyTable;


/**
 *
 * @author Assen Antov
 * @version 2.1, 07/2006
 */
public class DrawingExplorer extends AbstractEditorExtension implements EditorFrameListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Logger log = Logger.getLogger(DrawingExplorer.class);
	
	protected DrawingEditor editor;
	protected DrawingModel model;
	
	private JPanel control;
	private JScrollPane treeScroll;
	private JScrollPane propsScroll;
	private JSplitPane split;
	private DrawingPropertyTable props;
	private JTree tree;
	private JToolBar toolbar;
	private JButton createButton;
	
	private JToggleButton toggle;

	/**
	 * Creates a new Drawing Explorer.
	 */
	public DrawingExplorer() {
		super();

		putValue(Action.NAME, "Explorer"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.3, 03/2005"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.TREE_ICON);
 		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}


	public Component getComponent() {
		return getComponent(true);
	}
	
	public Component getComponent(boolean modifiable) {
		if (control == null) {
			control = new JPanel();
			control.setLayout(new BorderLayout());
			
			// create toolbar
			if (modifiable) {
				toolbar = new JToolBar();
				
				createButton = new MenuButton(new CreateCommand(), getItems());
				toolbar.add(createButton);
				toolbar.add(UIFactory.createButton(new CurrentCommand()));
				//toolbar.add(UIFactory.createButton(new CopyCommand()));
				toolbar.add(UIFactory.createButton(new DeleteCommand()));
				
				toolbar.addSeparator();
				
				toggle = UIFactory.createToggleButton(new PropsCommand());
				toggle.setSelected(true);
				toolbar.add(toggle);
				toolbar.add(UIFactory.createButton(new RefreshCommand()));
				
				setToolBar(toolbar);
			}
			
			// create tree
			tree = new JTree();
			tree.setBorder(new EmptyBorder(3, 3, 3, 3));
			tree.setCellRenderer(new TreeRenderer());
			tree.setModel(null);
			treeScroll = UIFactory.createScrollPane(tree);
			//treeScroll.setBorder(new LineBorder(treeScroll.getBackground().darker()));
			if (modifiable) {
				tree.addMouseListener(new TreeMouseAdapter());
			}
			tree.addTreeSelectionListener(new SelectionListener());
			
			// property editor
			props = new DrawingPropertyTable();
			props.setMinimumSize(new Dimension(200, 70));
			propsScroll = UIFactory.createScrollPane(props);
			
			// create split pane
			split = SplitPane.createStrippedSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				treeScroll,
				propsScroll);
			split.setDividerLocation(250);
			
			control.add(split, BorderLayout.CENTER);
			
			control.setPreferredSize(new Dimension(200, 350));
			control.setMinimumSize(new Dimension(200, 350));
			
			Editor.getEditorManager().addEditorFrameListener(this);
		}

		return control;
	}

	public void run(DrawingEditor e) {
	}


	protected void getTree(DrawingModel model, DefaultMutableTreeNode root) {
		List features = model.getFeatures(Object.class);
		
		Map groups = new HashMap();
		
		Iterator iter = features.iterator();
		while (iter.hasNext()) {
			Object feature = iter.next();
			
			String name = getName(feature);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) groups.get(name);
			if (node == null) {
				node = createNode(name);
				groups.put(name, node);
			}
			DefaultMutableTreeNode currentNode = createNode(feature);
			node.add(currentNode);
			
			// a sub-tree to show decorators of compound renderers
			if (feature instanceof CompoundRenderer) {
				CompoundRenderer cr = (CompoundRenderer) feature;
				Decorator[] d = cr.getDecorators();
				if (d != null) {
					for (int i = 0; i < d.length; i++) {
						//String decName = d[i].toString();
						currentNode.add(createNode(d[i]));
					}
				}
			}
			
			/*
			 * Don't want to make the Editor dependable on the transport
			 * modelling suite, so the following is done using reflection.
			 */
			else if (feature.getClass().getName().equals("org.magelan.transport.editor.XRef")) {
				ClassLoader loader = Editor.getInstance().getClassLoader();
				Class cl = null;
				try {
					cl = loader.loadClass("org.magelan.transport.editor.XRef");
				} catch (ClassNotFoundException ex2) {
					log.warn("Cannot load class \"org.magelan.transport.editor.XRef\"", ex2); //$NON-NLS-1$ 
				}
				if (cl != null) {
					PropertyDescriptor pd = Beans.getDescriptorFor("model", cl);
					if (pd != null) {
						Method rm = pd.getReadMethod();
						
						DrawingModel xref = null;
						try {
							xref = (DrawingModel) rm.invoke(feature, null);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						if (xref != null) getTree(xref, currentNode);
					}
				}
			}
		}
		
		iter = groups.values().iterator();
		while (iter.hasNext()) {
			root.add((DefaultMutableTreeNode) iter.next());
		}
	}

	protected TreeModel getTreeModel(DrawingEditor editor) {
		if (editor == null) return null;
		return getTreeModel(editor.getModel());
	}
	
	protected TreeModel getTreeModel(DrawingModel model) {
		DefaultMutableTreeNode root = createNode(model);
		getTree(model, root);
		return new DefaultTreeModel(root);
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
	
	protected String getName(Object obj) {
		if (obj instanceof CoreModel) {
			String name = (String) ((CoreModel) obj).getValue(CoreModel.KEY_NAME);
			return (name != null)? name : lang.getString("DrawingExplorer.leaf.model"); //$NON-NLS-1$
		}
		else if (obj instanceof Layer) {
			return lang.getString("DrawingExplorer.leaf.layers"); //$NON-NLS-1$
		}
		else if (obj instanceof LineStyle) {
			return lang.getString("DrawingExplorer.leaf.lineStyles"); //$NON-NLS-1$
		}
		else if (obj instanceof TextStyle) {
			return lang.getString("DrawingExplorer.leaf.textStyles"); //$NON-NLS-1$
		}
		else if (obj instanceof DrawingRenderer) {
			return lang.getString("DrawingExplorer.leaf.renderers"); //$NON-NLS-1$
		}
		else if (obj instanceof DataSource) {
			return lang.getString("DrawingExplorer.leaf.dataSources"); //$NON-NLS-1$
		}
		
		else {
			String name = obj.getClass().getName();
			int idx = name.lastIndexOf('.');
			if (idx != -1) {
				name = name.substring(idx+1);
			}
			return name;
		}
	}

	protected Object[] getItems() {
		IFeaturesManager fm = Editor.getFeaturesManager();
		Iterator iter = fm.getFeatures().iterator();
		Object[] items = new Object[fm.getFeatures().size()]; 
		int i = 0;
		while (iter.hasNext()) {
			String feature = (String) iter.next();
			Action a = null;
			try {
				a = new CreateFeature(feature);
			} catch (RuntimeException re) {}
			if (a != null) {
				items[i++] = a; 
			}
		}
		return items;
	}

	private class SelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
			if (node instanceof DefaultMutableTreeNode) {
				Object obj = ((DefaultMutableTreeNode) node).getUserObject();
				if (obj instanceof String) {
					obj = null;
				}
				
				props.setBean(obj);
				props.revalidate();
				props.repaint();
			}
		}
	}
	
	private class TreeMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				if (e.getClickCount() == 1) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
					if (e.getButton() == MouseEvent.BUTTON3) {
						tree.clearSelection();
						tree.addSelectionPath(selPath);
						JPopupMenu pm = createPopupFor(node);
						if (pm == null) return;
						pm.show(tree, e.getX(), e.getY());
					}
				}
				else if (e.getClickCount() == 2) {
				}
			}
		}
	}

	public DefaultMutableTreeNode getSelectedNode() {
		TreePath p = tree.getSelectionPath();
		if (p == null) return null;
		return (DefaultMutableTreeNode) p.getLastPathComponent();
	}

	private JPopupMenu popup;
	
	/**
	 * Factory method to create a popup menu for node.
	 * 
	 * @param	node	node to create the menu for
	 */
	JPopupMenu createPopupFor(DefaultMutableTreeNode node) {
		Object obj = node.getUserObject();
		if (obj instanceof String) return null;
		
		popup = new JPopupMenu();
		
		popup.add(createMenuItem(new CurrentCommand()));
		//popup.addSeparator();
		//popup.add(createMenuItem(new CopyCommand()));
		popup.add(createMenuItem(new DeleteCommand()));

		if (obj instanceof Layer) {
			popup.addSeparator();
			popup.add(createMenuItem(new Internal.BringToFront((Layer) obj)));
			popup.add(createMenuItem(new Internal.SendToBack((Layer) obj)));
		}
		
		return popup;
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


	protected void refresh(DrawingModel model) {
		this.model = model;
		
		tree.setModel(getTreeModel(model));
		tree.setEnabled(true);
		
		props.setDrawing(model);
		props.setBean(null);
		props.setEnabled(true);
	}
	
	protected void refresh(DrawingEditor editor) {
		this.editor = editor;
		tree.setModel(getTreeModel(editor));
		tree.setEnabled(true);
		
		if (editor == null) {
			props.setDrawing(null);
		}
		else {
			props.setDrawing(editor.getModel());
			model = editor.getModel();
		}
		props.setBean(null);
		props.setEnabled(true);
	}


	/*
	 * EditorFrameListener
	 */
	
	public void editorFrameActivated(EditorFrameEvent e) {
		editor = Editor.getEditorManager().getSelectedEditor();
		refresh(editor);
	}


	public void editorFrameClosed(EditorFrameEvent e) {
		editor = null;
		
		tree.setModel(null);
		tree.setEnabled(false);
		
		props.setBean(null);
		props.setEnabled(false);
	}

	public void editorFrameOpened(EditorFrameEvent e) {
	}

	public void editorFrameClosing(EditorFrameEvent e) {
	}

	public void editorFrameIconified(EditorFrameEvent e) {
		tree.setEnabled(false);
		props.setEnabled(false);
	}

	public void editorFrameDeiconified(EditorFrameEvent e) {
		tree.setEnabled(true);
		props.setEnabled(true);
	}

	public void editorFrameDeactivated(EditorFrameEvent e) {
		tree.setModel(null);
		tree.setEnabled(false);
		
		props.setBean(null);
		props.setEnabled(false);
	}


	/**
	 * Toggle properties command.
	 */
	private class PropsCommand extends AbstractAction {
		
		public PropsCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("DrawingExplorer.PropsCommand.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.PropsCommand.descr.short")); //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.PropsCommand.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.PROPERTIES_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		}

		private int oldLocation = 0;
		
		public void actionPerformed(ActionEvent e) {
			if (!toggle.isSelected()) {
				oldLocation = split.getDividerLocation();
				control.remove(split);
				control.add(treeScroll, BorderLayout.CENTER);
				control.revalidate();
			}
			else {
				control.remove(treeScroll);
				split.setTopComponent(treeScroll);
				split.setBottomComponent(propsScroll);
				split.setDividerLocation(oldLocation);
				control.add(split, BorderLayout.CENTER);
				control.revalidate();
			}
		}
	}
	
	/**
	 * Refresh view command.
	 */
	private class RefreshCommand extends AbstractAction {
		
		public RefreshCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("DrawingExplorer.RefreshCommand.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.RefreshCommand.descr.short")); //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.RefreshCommand.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.REFRESH_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		}
		
		public void actionPerformed(ActionEvent e) {
			/*
			if (editor != null) refresh(editor);
			else refresh(model);
			*/
			refresh(model);
		}
	}
	
	/**
	 * Set current command.
	 */
	private class CurrentCommand extends AbstractAction {
		
		public CurrentCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("DrawingExplorer.MakeCurrent.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.MakeCurrent.descr.short"));  //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.MakeCurrent.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.CURRENT_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}
		
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode node = getSelectedNode();
			if (node == null) return;
			Object feature = node.getUserObject();
			if (feature instanceof String) return;
			
			//DrawingModel model = editor.getModel();
			try {
				model.setCurrent(feature);
			} catch (Throwable t) {
				ErrorDialog.showErrorDialog(lang.getString("DrawingExplorer.error.current") + feature, t); //$NON-NLS-1$
				log.warn(lang.getString("DrawingExplorer.error.current") + feature, t); //$NON-NLS-1$
			}
			tree.repaint();
			//refresh(editor);
		}
	}
	
	/**
	 * Copy command.
	 */
	private class CopyCommand extends AbstractAction {
		
		public CopyCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("DrawingExplorer.Copy.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.Copy.descr.short"));  //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.Copy.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.COPY_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}
		
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	/**
	 * Delete command.
	 */
	private class DeleteCommand extends AbstractAction {
		
		public DeleteCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("DrawingExplorer.Delete.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.Delete.descr.short"));  //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.Delete.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}
		
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode node = getSelectedNode();
			if (node == null) return;
			Object feature = node.getUserObject();
			if (feature instanceof String) return;
			
			//DrawingModel model = editor.getModel();
			try {
				model.removeFeature(feature);
			} catch (Throwable t) {
				ErrorDialog.showErrorDialog(lang.getString("DrawingExplorer.error.delete") + feature, t); //$NON-NLS-1$
				log.warn(lang.getString("DrawingExplorer.error.delete") + feature, t); //$NON-NLS-1$
			}
			//((DefaultMutableTreeNode) node.getParent()).remove(node);
			refresh(model);
		}
	}
	
	/**
	 * Create command.
	 */
	private class CreateCommand extends AbstractAction {
		
		public CreateCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("DrawingExplorer.Create.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("DrawingExplorer.Create.descr.short"));  //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("DrawingExplorer.Create.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.CREATE_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}
		
		public void actionPerformed(ActionEvent e) {
			((MenuButton) createButton).setItems(getItems());
		}
	}
	
	
	private class CreateFeature extends AbstractAction {
		static final String FEATURE = "feature";  //$NON-NLS-1$
		private Class cl = null;
		private BeanInfo bi = null;
		
		public CreateFeature(String feature) {
			super();
			
			// get feature info
			try {
				cl = Class.forName(feature);
			} 
			catch (ClassNotFoundException e) {}
			if (cl != null) {
				try {
					bi = Introspector.getBeanInfo(cl);
				} catch (IntrospectionException ie) {}
			}
			
			// fail if cannot obtain it
			if (cl == null || bi == null) {
				throw new RuntimeException();
			}
			
			// get representing image
			Image image = bi.getIcon(BeanInfo.ICON_COLOR_16x16);
			Icon icon;
			if (image != null) {
				icon = new ImageIcon(image);
			}
			else {
				icon = Icons.GAP_ICON_16;
			}
			
			putValue(Action.NAME, bi.getBeanDescriptor().getDisplayName());
			putValue(Action.SHORT_DESCRIPTION, bi.getBeanDescriptor().getShortDescription()); 
			putValue(Action.LONG_DESCRIPTION, bi.getBeanDescriptor().getShortDescription());
			putValue(Action.SMALL_ICON, icon);
			putValue(FEATURE, feature);
		}
		
		public void actionPerformed(ActionEvent e) {
			// create a new feature
			Object f = null;
			try {
				f = cl.newInstance();
			} catch (Throwable t) {
				log.warn(lang.getString("DrawingExplorer.error.instantiate") + getValue(FEATURE), t); //$NON-NLS-1$
				ErrorDialog.showErrorDialog(lang.getString("DrawingExplorer.error.instantiate") + getValue(FEATURE), t); //$NON-NLS-1$
			}
			if (f == null) return;
			
			// add it to the selected editor
			DrawingModel model = editor.getModel();
			try {
				model.addFeature(f);
			} catch (Throwable t) {
				ErrorDialog.showErrorDialog(lang.getString("DrawingExplorer.error.add") + getValue(FEATURE), t); //$NON-NLS-1$
				log.warn(lang.getString("DrawingExplorer.error.add") + getValue(FEATURE), t); //$NON-NLS-1$
			}
			refresh(editor);
		}
	}
	
	/**
	 * Makes the current features bold. 
	 * 
	 * @version 1.0, 2005-3-5
	 * @author Assen Antov
	 */
	private class TreeRenderer extends TreeCellRenderer {
		public java.awt.Component getTreeCellRendererComponent(JTree tree, 
					Object value,
					boolean sel,
					boolean expanded,
					boolean leaf, int row,
					boolean hasFocus) {
			
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			if (value instanceof DefaultMutableTreeNode) {
				Object val = ((DefaultMutableTreeNode) value).getUserObject();
				if (val != null) {
					
					DrawingModel model;
					if (editor != null) model = editor.getModel();
					else model = DrawingExplorer.this.model;
					
					Object current = model.getCurrent(val.getClass());
					Font font = getFont();
					if (current == val) {
						setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
					}
					else {
						setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
					}
				}
			}
			
			return this;
		}
	}
	
	public JSplitPane getSplitPane() {
		return split;
	}
	
	public void setToolBar(JToolBar toolbar) {
		this.toolbar = toolbar;
		
		toolbar.setBorderPainted(false);
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		toolbar.setBorder(new LineBorder(toolbar.getBackground().darker()));
		
		control.add(toolbar, BorderLayout.NORTH);
	}
}