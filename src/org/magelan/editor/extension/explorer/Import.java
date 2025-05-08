/*
 * The Magelan Project - Vector Graphics Library and Editor
 * Copyright (c) 2003-2006, Assen Antov and Larisa Feldman. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.magelan.commons.Beans;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.ErrorDialog;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.Entity;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.style.Layer;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.IEditorManager;


/**
 *
 * @author Assen Antov
 * @version 1.0, 07/2006
 */
public class Import extends AbstractEditorExtension {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private static Logger log = Logger.getLogger(Import.class);
	
	private DrawingEditor editor;
	private DrawingModel src, dest;
	private JCheckBox check;
	
	public Import() {
		super();

		putValue(Action.NAME, "Import"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 07/2006"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Import.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Import.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.IMPORT_ICON);
 		putValue(AbstractEditorExtension.GROUP, lang.getString("common.tools")); //$NON-NLS-1$
	}


	
	public void run(DrawingEditor e) {
		if (e == null) return;
		
		// open the drawing to import from
		src = open();
		if (src == null) return;
		
		dest = e.getModel();
		
		createDialog(src, dest, Editor.getEditorManager().getMainFrame()).setVisible(true);
	}

	public DrawingModel open() {
		IEditorManager m = Editor.getEditorManager();
		JFrame f = m.getMainFrame();
		JFileChooser fileChooser = Editor.getFilesManager().getFileChooser();
		int returnVal = fileChooser.showOpenDialog(f);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			DrawingEditor ed;
			ed = Editor.getFilesManager().decode(fileChooser.getSelectedFile());
			
			if (ed == null) return null;
			return ed.getModel();
		}
		return null;
	}
	
	
	private JDialog createDialog(DrawingModel src, DrawingModel dest, JFrame parent) {
		JDialog d = new JDialog(parent);
		d.setModal(true);
		d.setLayout(new BorderLayout());
		d.setTitle("Import");
		
		JLabel banner = UIFactory.createBanner("Import features from drawing"); //, null, Icons.IMPORT_BAN_ICON);
		d.add(banner, BorderLayout.NORTH);

		DrawingExplorer e1 = new DrawingExplorer();
		Component left = e1.getComponent(false);
		e1.refresh(src);
		
		DrawingExplorer e2 = new DrawingExplorer();
		Component right = e2.getComponent();
		e2.refresh(dest);

		JToolBar toolbar = new JToolBar();
		
			Action a = new OpenCommand(e1);
			UIFactory.addKeyMapping(d.getRootPane(), a);
			toolbar.add(UIFactory.createButton(a));
			
			a = new ImportCommand(e1, e2, src, dest);
			UIFactory.addKeyMapping(d.getRootPane(), a);
			toolbar.add(UIFactory.createButton(a));
		
		e1.setToolBar(toolbar);
		
		JSplitPane pane = UIFactory.createSplitPane();
		pane.setLeftComponent(left);
		pane.setRightComponent(right);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		panel.add(pane, BorderLayout.CENTER);
		
		check = new JCheckBox("Import layer entities");
		check.setSelected(true);
		panel.add(check, BorderLayout.SOUTH);
		
		d.add(panel, BorderLayout.CENTER);
		
		Action oka = new OKCommand(d);
		JButton okButton = new JButton(oka);
		
		JPanel buttons = new JPanel();
		buttons.setBorder(UIFactory.createTopBorder());
		buttons.add(okButton);
		d.getRootPane().setDefaultButton(okButton);
		UIFactory.addKeyMapping(d.getRootPane(), oka);
		d.add(buttons, BorderLayout.SOUTH);
		
		int width = 500; int height = 550;
		d.setSize(width, height);
		d.setLocationRelativeTo(parent);
		
		pane.setDividerLocation((int) (0.5 * width));
		e1.getSplitPane().setDividerLocation((int) (0.4 * height));
		e2.getSplitPane().setDividerLocation((int) (0.4 * height));
		
		return d;
	}
	
	
	/**
	 * Copy command.
	 */
	private class ImportCommand extends AbstractAction {
		private DrawingExplorer exp, exp2;
		private DrawingModel src, dest;
		
		ImportCommand(DrawingExplorer exp, DrawingExplorer exp2, DrawingModel src, DrawingModel dest) {
			super();
			this.exp = exp;
			this.exp2 = exp2;
			this.src = src;
			this.dest = dest;
			
			putValue(Action.NAME, lang.getString("Import.Import.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("Import.Import.descr.short"));  //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.IMPORT_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		}
		
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode node = exp.getSelectedNode();
			if (node == null) return;
			
			try {
				Object obj = node.getUserObject();
				if (obj instanceof String || obj instanceof DrawingModel) return;
				
				if (obj instanceof Layer && check.isSelected()) {
					try {
						dest.addFeature(obj);
					} catch (Throwable t) {
						// such layer already exists
					}
					
					List elements = src.getElements();
					List sel = new LinkedList();
					
					Iterator iter = elements.iterator();
					while (iter.hasNext()) {
						Object el = iter.next();
						if (el instanceof Entity && ((Layer) obj).getName().equals(((Entity) el).getLayer().getName())) {
							((Entity) el).setExternal(false); // internalize entities
							sel.add(el);
						}
					}
					
					dest.add(sel);
					exp2.refresh(dest);
					return;
				}
				
				/*
				 * Don't want to make the Editor dependable on the transport
				 * modelling suite, so the following is done using reflection.
				 */
				else if (obj.getClass().getName().equals("org.magelan.transport.editor.XRef")) {
					ClassLoader loader = Editor.getInstance().getClassLoader();
					Class cl = null;
					try {
						cl = loader.loadClass("org.magelan.transport.editor.XRef");
					} catch (ClassNotFoundException ex2) {
						log.warn("Cannot load class \"org.magelan.transport.editor.XRef\"", ex2); //$NON-NLS-1$ 
					}
					if (cl != null) {
						PropertyDescriptor pd = Beans.getDescriptorFor("parent", cl);
						if (pd != null) {
							Method wm = pd.getWriteMethod();
							
							try {
								wm.invoke(obj, new Object[] { dest });
							} catch (IllegalArgumentException ex) {
								ex.printStackTrace();
							} catch (IllegalAccessException ex) {
								ex.printStackTrace();
							} catch (InvocationTargetException ex) {
								ex.printStackTrace();
							}
						}
					}
				}
				
				// add feature and refresh
				dest.addFeature(obj);
				exp2.refresh(dest);
			} catch (Throwable t) {
				ErrorDialog.showErrorDialog("Error importing feature", t);
			}
		}
	}
	
	private class OpenCommand extends AbstractAction {
		private DrawingExplorer exp;
		
		OpenCommand(DrawingExplorer exp) {
			super();
			this.exp = exp;
			
			putValue(Action.NAME, lang.getString("OpenDrawing.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("OpenDrawing.descr.short")); //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("OpenDrawing.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.OPEN_ICON);
			putValue(Action.ACCELERATOR_KEY,
					 KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		}

		public void actionPerformed(ActionEvent e) {
			DrawingModel model = open();
			if (model == null) return;
			
			src = model;
			exp.refresh(model);
		}
	}
	
	/**
	 * OK command.
	 */
	private class OKCommand extends AbstractAction {
		private JDialog d;
		OKCommand(JDialog d) {
			super();
			this.d = d;
			putValue(Action.NAME, lang.getString("common.close")); //$NON-NLS-1$
			//putValue(Action.SMALL_ICON, Icons.CANCEL_ICON);
//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
		}
		public void actionPerformed(ActionEvent e) {
			d.setVisible(false);
			d.dispose();
		}
	}
}