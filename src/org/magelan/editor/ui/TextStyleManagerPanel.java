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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.ListDataModel;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.CoreModel;
import org.magelan.core.style.TextStyle;
import org.magelan.editor.Editor;


/**
 *
 * @author Assen Antov
 * @version 1.0, 08/2004
 */
public class TextStyleManagerPanel extends JPanel {

	private static Lang lang = Lang.getLang(Editor.STRINGS);

	private CoreModel dwg;
	private List styles;
	private boolean sort = false;
	
	private JList stylesList;
	private JToggleButton toggle, sorted;
	private JToolBar toolbar;
	private JSplitPane split;
	private JScrollPane layerScroll, propsScroll;
	private DrawingPropertyTable props;


	public TextStyleManagerPanel(CoreModel model) {
		this();
		setModel(model);
	}

	public TextStyleManagerPanel() {
		setLayout(new BorderLayout());
		
		toolbar = new JToolBar();
		toolbar.setBorderPainted(false);
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		toolbar.setBorder(new LineBorder(toolbar.getBackground().darker()));
		
		toolbar.add(UIFactory.createButton(new NewCommand()));
		toolbar.add(UIFactory.createButton(new DeleteCommand()));
		toolbar.addSeparator();
		
		sorted = UIFactory.createToggleButton(new SortCommand());
		sorted.setSelected(sort);
		toolbar.add(sorted);
		
		toggle = UIFactory.createToggleButton(new PropsCommand());
		toggle.setSelected(true);
		toolbar.add(toggle);
		add(toolbar, BorderLayout.NORTH);
		
		// property editor
		props = new DrawingPropertyTable();
		props.setMinimumSize(new Dimension(200, 70));
		propsScroll = UIFactory.createScrollPane(props);
		
		// create layer list
		stylesList = new JList();
		stylesList.setCellRenderer(new TextStyleListRenderer());
		stylesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		stylesList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = stylesList.locationToIndex(e.getPoint());

					if (index != -1) {
						dwg.setCurrent((TextStyle) stylesList.getSelectedValue());
					}
				}
			}
		});
		stylesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				props.setBean(stylesList.getSelectedValue());
				props.revalidate();
				props.repaint();
			}
		});
		layerScroll = new JScrollPane(stylesList); //UIFactory.createScrollPane(layerList);
		
		// create split pane
		split = UIFactory.createSplitPane(
			//new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			layerScroll,
			propsScroll);
		split.setDividerLocation(200);
		add(split, BorderLayout.CENTER);
	}
	
	private void refresh() {
		if (dwg == null) {
			styles = new ArrayList();
		}
		
		else {
			styles = dwg.getFeatures(TextStyle.class);
			if (sort) {
				Collections.sort(styles, new Comparator() {
					public int compare(Object a, Object b) {
						return ((TextStyle) a).getName().compareTo(((TextStyle) b).getName());
					}
				});
			}
		}
		
		stylesList.setModel(new ListDataModel(styles));
	}

	/**
	 * Sets the model to obtain styles from.
	 * 
	 * @param model model to analize
	 */
	public void setModel(CoreModel model) {
		dwg = model;
		refresh();
		props.setDrawing(model);
	}

	/**
	 * Sets the split pane orientation.
	 * 
	 * @param orient split orientation
	 */
	public void setSplitOrientation(int orient) {
		split.setOrientation(orient);
		revalidate();
	}

	public void setEnabled(boolean enabled) {
		stylesList.setEnabled(enabled);
		props.setEnabled(enabled);
	}

	/**
	 * Toggle properties command.
	 */
	private class PropsCommand extends AbstractAction {
		
		public PropsCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("TextStyleManagerPanel.PropsCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.PROPERTIES_ICON);
			//putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		}

		private int oldLocation = 0;
		
		public void actionPerformed(ActionEvent e) {
			if (!toggle.isSelected()) {
				oldLocation = split.getDividerLocation();
				remove(split);
				add(layerScroll, BorderLayout.CENTER);
				revalidate();
				repaint();
			}
			else {
				remove(layerScroll);
				split.setTopComponent(layerScroll);
				split.setBottomComponent(propsScroll);
				split.setDividerLocation(oldLocation);
				add(split, BorderLayout.CENTER);
				revalidate();
				repaint();
			}
		}
	}

	/**
	 * Sort command.
	 */
	private class SortCommand extends AbstractAction {
		public SortCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("TextStyleManagerPanel.SortCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.SORT_ICON);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}

		public void actionPerformed(ActionEvent e) {
			sort = sorted.isSelected();
			refresh();
		}
	}

	/**
	 * New layer command.
	 */
	private class NewCommand extends AbstractAction {
		public NewCommand() {
			super();
			putValue(Action.NAME, lang.getString("TextStyleManagerPanel.NewCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.NEW_TEXT_STYLE_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}
		public void actionPerformed(ActionEvent e) {
			TextStyle src = (TextStyle) stylesList.getSelectedValue();
			if (src == null) return;
			TextStyle copy = (TextStyle) src.clone();
			copy.setName(lang.getString("TextStyleManagerPanel.newStylePrefix") + src.getName()); //$NON-NLS-1$
			try {
				dwg.addFeature(copy);
			} catch (Throwable ex) {}
			refresh();
		}
	}

	/**
	 * Delete layer command.
	 */
	private class DeleteCommand extends AbstractAction {
		public DeleteCommand() {
			super();
			putValue(Action.NAME, lang.getString("TextStyleManagerPanel.DeleteCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}
		public void actionPerformed(ActionEvent e) {
			int[] idx = stylesList.getSelectedIndices();
			for (int i = 0; i < idx.length; i++) {
				try {
					dwg.removeFeature(styles.get(idx[i]));
				} catch (Throwable ex) {}
			}
			refresh();
		}
	}
}