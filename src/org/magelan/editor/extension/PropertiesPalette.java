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
package org.magelan.editor.extension;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;

import org.magelan.commons.Beans;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.commons.ui.UIFactory;
import org.magelan.commons.ui.propertytable.ExpandableTableItem;
import org.magelan.core.DefaultEntity;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.markup.SelectionEvent;
import org.magelan.drawing.markup.SelectionListener;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.DrawingPropertyTable;


/**
 * A property modifier palette extension class.
 *
 * @author Assen Antov
 * @version 2.0, 08/2004
 */
public class PropertiesPalette extends AbstractEditorExtension
	implements EditorFrameListener, SelectionListener {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private JPanel control;
	private JComboBox combo;
	private DrawingPropertyTable table;
	private DrawingEditor editor;
	private JToolBar toolbar;

	/**
	 * Creates a new PropertiesPalette object.
	 */
	public PropertiesPalette() {
		super();

		putValue(Action.NAME, "Properties"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "2.0, 08/2004"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("PropertiesPalette.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("PropertiesPalette.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.PROPERTIES_ICON);
 		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}


	public Component getComponent() {
		if (control == null) {
			control = new JPanel();
			control.setLayout(new BorderLayout());
			control.setPreferredSize(new Dimension(200, 350));
			control.setMinimumSize(new Dimension(200, 350));
			
			JPanel north = new JPanel();
			north.setLayout(new BorderLayout());
			
			/*
			toolbar = new JToolBar();
			toolbar.setBorderPainted(false);
			toolbar.setFloatable(false);
			toolbar.setRollover(true);
			toolbar.setBorder(new LineBorder(toolbar.getBackground().darker()));
			
			north.add(toolbar, BorderLayout.NORTH);
			toolbar.add(UIFactory.createButton(new CollapseAllCommand()));
			*/
			
			// entities combo box
			combo = new JComboBox();
			combo.setRenderer(new Beans.BeanGroupRenderer());
			combo.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
			north.add(combo, BorderLayout.CENTER);
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comboUpdate();
				}
			});
			
			control.add(north, BorderLayout.NORTH);
			
			// property table 
			table = new DrawingPropertyTable();
			JScrollPane scroll = UIFactory.createScrollPane(table);
			control.add(scroll, BorderLayout.CENTER);
			
			SwingUtil.setEnabled(control, false);
			
			Editor.getEditorManager().addEditorFrameListener(this);
			Editor.getEditorManager().addSelectionListener(this);
		}

		return control;
	}

	protected void comboUpdate() {
		Beans.BeanGroup selected = (Beans.BeanGroup) combo.getSelectedItem();
		table.setBeans(selected.getBeans(), selected.getGroupClass());
	}
	
	public void update() {
		if (editor == null) return;
		java.util.List sel = editor.getSelection();
		if (sel == null) return;
		
		if (sel.size() == 0) {
			combo.setModel(new DefaultComboBoxModel());
			table.setBean(editor.getModel());
		} else {
			Vector v = new Vector();
			Collection groups = Beans.groupBeans(sel);
			if (groups.size() > 1) {
				v.add(Beans.getGroup(sel, DefaultEntity.class));
			}
			v.addAll(groups);
			combo.setModel(new DefaultComboBoxModel(v));
			
			Beans.BeanGroup first = (Beans.BeanGroup) v.get(0);
			table.setBeans(first.getBeans(), first.getGroupClass());
		}
	}
	
	public void run(DrawingEditor e) {
	}

	public void selectionChange(SelectionEvent e) {
		update();
	}


	/*
	 * EditorFrameListener
	 */
	
	public void editorFrameActivated(EditorFrameEvent e) {
		DrawingEditor editor = Editor.getEditorManager().getSelectedEditor();
		table.setDrawing((org.magelan.core.CoreModel) editor.getModel());
		this.editor = editor;

		update();

		SwingUtil.setEnabled(control, true);
	}

	private static final DefaultComboBoxModel EMPTY_MODEL = new DefaultComboBoxModel();
	private static final ArrayList emptyList;
	static {
		emptyList = new ArrayList();
		emptyList.add(null);
	}
	
	public void editorFrameClosed(EditorFrameEvent e) {
		editor = null;
		combo.setModel(EMPTY_MODEL);
		table.setBean(null);
		table.setDrawing(null);
		SwingUtil.setEnabled(control, false);
	}

	public void editorFrameOpened(EditorFrameEvent e) {
	}

	public void editorFrameClosing(EditorFrameEvent e) {
	}

	public void editorFrameIconified(EditorFrameEvent e) {
		SwingUtil.setEnabled(control, false);
	}

	public void editorFrameDeiconified(EditorFrameEvent e) {
		SwingUtil.setEnabled(control, true);
	}

	public void editorFrameDeactivated(EditorFrameEvent e) {
		editor = null;
		combo.setModel(EMPTY_MODEL);
		table.setBean(null);
		table.setDrawing(null);
		SwingUtil.setEnabled(control, false);
	}
	
	/**
	 * CollapseAll command.
	 */
	private class CollapseAllCommand extends AbstractAction {
		public CollapseAllCommand() {
			super();
			putValue(Action.NAME, "Collapse Groups");
			putValue(Action.SMALL_ICON, Icons.COLLAPSE_ICON);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		}
		public void actionPerformed(ActionEvent e) {
			Iterator items = table.getItems().iterator();
			while (items.hasNext()) {
				Object item = items.next();
				if (item instanceof ExpandableTableItem) {
					((ExpandableTableItem) item).setExpanded(false);
					table.revalidate();
				}
			}
		}
	}
}