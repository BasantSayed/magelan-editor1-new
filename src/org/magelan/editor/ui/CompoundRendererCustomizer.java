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

import java.awt.Dimension;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.magelan.commons.Beans;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.DividerLabel;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.LabelledItemPanel;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.CoreModel;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.Entity;
import org.magelan.core.entity.LabelEntity;
import org.magelan.core.renderer.ColorDecorator;
import org.magelan.core.renderer.CompoundRenderer;
import org.magelan.core.renderer.Decorator;
import org.magelan.core.renderer.LabelDecorator;
import org.magelan.editor.Editor;
import org.magelan.editor.ui.DecoratorCustomizer.DecoratorCustomizerDialog;


/**
 *
 * @author Assen Antov
 * @version 1.0, 09/2005
 */
public class CompoundRendererCustomizer extends LabelledItemPanel {

	private static Lang lang = Lang.getLang(Editor.STRINGS);

	private CoreModel dwg;
	private List renderers;
	
	private JComboBox renderersCombo;
	private JToolBar toolbar;
	private JList decoratorList;
	private JScrollPane decoratorScroll;


	public CompoundRendererCustomizer(CoreModel model) {
		this();
		setModel(model);
	}

	public CompoundRendererCustomizer() {

		// renderer selection
		addDivider(new DividerLabel(lang.getString("CompoundRendererCustomizer.divider.choose"))); //$NON-NLS-1$
		renderersCombo = new JComboBox();
		renderersCombo.setRenderer(new Beans.BeanListRenderer());
		renderersCombo.setEditable(false);
		renderersCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// update list
				CompoundRenderer rend = (CompoundRenderer) renderersCombo.getSelectedItem();
				if (rend == null) return;
				Decorator[] dec = rend.getDecorators();
				decoratorList.setModel(new ArrayListDataModel(dec));
			}
		});
		addItem(renderersCombo);

		addDivider(new DividerLabel(lang.getString("CompoundRendererCustomizer.divider.decorators"))); //$NON-NLS-1$
		
		// a toolbar
		toolbar = new JToolBar();
		toolbar.setBorderPainted(false);
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		toolbar.setBorder(new LineBorder(toolbar.getBackground().darker()));
		toolbar.add(UIFactory.createButton(new NewColorDecoratorCommand()));
		toolbar.add(UIFactory.createButton(new NewLabelDecoratorCommand()));
		toolbar.add(UIFactory.createButton(new DeleteCommand()));
		toolbar.add(UIFactory.createButton(new EditCommand()));
		addItem(toolbar);

		// decorator selection
		decoratorList = new JList();
		decoratorList.setCellRenderer(new Beans.BeanListRenderer());
		decoratorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		decoratorList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = decoratorList.locationToIndex(e.getPoint());

					if (index != -1) {
						editDecorator();
					}
				}
			}
		});
		decoratorScroll = UIFactory.createScrollPane(decoratorList);
		decoratorScroll.setPreferredSize(new Dimension(250, 150));
		decoratorScroll.setMinimumSize(new Dimension(250, 150));
		addItem(decoratorScroll);
	}


	private class ArrayListDataModel extends AbstractListModel {
		private Object[] data;
		private ArrayListDataModel(Object[] data) {
			this.data = data;
		}
        public int getSize() { return data == null? 0 : data.length; }
        public Object getElementAt(int i) { return data == null? null : data[i]; }
    }
	
	
	private void refresh() {
		if (dwg == null) return;
		renderers = dwg.getFeatures(CompoundRenderer.class);
		int idx = -1;
		if (renderersCombo.getModel().getSize() > 0) {
			idx = renderersCombo.getSelectedIndex();
		}
		renderersCombo.setModel(new DefaultComboBoxModel(new Vector(renderers))); // :(
		try {
			if (idx != -1) {
				renderersCombo.setSelectedIndex(idx);
			}
		} catch (IllegalArgumentException iae) {}
	}

	public void setModel(CoreModel model) {
		dwg = model;
		refresh();
		if (renderersCombo.getModel().getSize() > 0) {
			renderersCombo.setSelectedIndex(0);
		}
	}

	private void editDecorator() {
		int idx = decoratorList.getSelectedIndex();
		CompoundRenderer cr = (CompoundRenderer) renderersCombo.getSelectedItem();
		if (cr == null || idx == -1) return;

		if (cr.getDecorators()[idx] instanceof ColorDecorator) {
			DecoratorCustomizerDialog dlg = new DecoratorCustomizer.DecoratorCustomizerDialog(null);
			dlg.setObject(cr.getDecorators()[idx]);
			dlg.setVisible(true);
			refresh();
		}
		else if (cr.getDecorators()[idx] instanceof LabelDecorator) {
			NewLabelDialog dlg = new NewLabelDialog(dwg, Editor.getEditorManager().getMainFrame());
			dlg.setVisible(true);
			refresh();
			
			LabelEntity label = dlg.getTemplate();
			if (label != null) {
				LabelDecorator ld = (LabelDecorator) cr.getDecorators()[idx];
				ld.setLabel(dlg.getTemplate());
				ld.setDecClass(dlg.getTemplate().getParent().getClass());
			}
		}
	}
	

	private class NewColorDecoratorCommand extends AbstractAction {
		public NewColorDecoratorCommand() {
			super();
			putValue(Action.NAME, lang.getString("CompoundRendererCustomizer.NewCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.CREATE_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}
		public void actionPerformed(ActionEvent e) {
			CompoundRenderer cr = (CompoundRenderer) renderersCombo.getSelectedItem();
			if (cr == null) return;
			Decorator dec = new ColorDecorator();
			cr.add(dec);	// TODO: other types of decorators
			refresh();
			decoratorList.setSelectedValue(dec, true);
			editDecorator();
		}
	}

	private class NewLabelDecoratorCommand extends AbstractAction {
		public NewLabelDecoratorCommand() {
			super();
			putValue(Action.NAME, "New Label Decorator"); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.LABEL_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}
		public void actionPerformed(ActionEvent e) {
			CompoundRenderer cr = (CompoundRenderer) renderersCombo.getSelectedItem();
			if (cr == null) return;
			Decorator dec = new LabelDecorator();
			cr.add(dec);	// TODO: other types of decorators
			refresh();
			decoratorList.setSelectedValue(dec, true);
			editDecorator();
		}
	}
	
	/**
	 * Delete layer command.
	 */
	private class DeleteCommand extends AbstractAction {
		public DeleteCommand() {
			super();
			putValue(Action.NAME, lang.getString("CompoundRendererCustomizer.DeleteCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}
		public void actionPerformed(ActionEvent e) {
			int[] idx = decoratorList.getSelectedIndices();
			CompoundRenderer cr = (CompoundRenderer) renderersCombo.getSelectedItem();
			if (cr == null) return;
			for (int i = 0; i < idx.length; i++) {
				//try {
					cr.remove((Decorator) decoratorList.getModel().getElementAt(idx[i]));
				//} catch (Throwable ex) {}
			}
			refresh();
		}
	}

	private class EditCommand extends AbstractAction {
		public EditCommand() {
			super();
			putValue(Action.NAME, lang.getString("CompoundRendererCustomizer.EditCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.EDIT_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		}
		public void actionPerformed(ActionEvent e) {
			editDecorator();
		}
	}
	
	public static void main(String[] args) {
		CoreModel model = new DefaultCoreModel();
		CompoundRenderer r = new CompoundRenderer();
		r.add(new ColorDecorator());
		r.add(new ColorDecorator(Entity.class));
		model.addFeature(r);
		
		CompoundRendererCustomizer cust = new CompoundRendererCustomizer();
		cust.setModel(model);
		
		JDialog d = new JDialog();
	//	d.setSize(new Dimension(250, 150));
		d.add(cust);
		d.pack();
		d.show();
	}
}