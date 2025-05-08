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
package org.magelan.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import org.magelan.core.style.Layer;
import org.magelan.editor.Editor;


/**
 *
 * @author Assen Antov
 * @version 1.1, 07/2006
 */
public class LayerManagerPanel extends JPanel {

	private static Lang lang = Lang.getLang(Editor.STRINGS);

	private CoreModel dwg;
	private List layers;
	private boolean sort = false;
	
	private LayerListRenderer renderer;
	private JList layerList;
	private JToggleButton toggle, sorted;
	private JToolBar toolbar;
	private JSplitPane split;
	private JScrollPane layerScroll, propsScroll;
	private DrawingPropertyTable props;


	public LayerManagerPanel(CoreModel model) {
		this();
		setModel(model);
	}

	public LayerManagerPanel() {
		setLayout(new BorderLayout());
		
		toolbar = new JToolBar();
		toolbar.setBorderPainted(false);
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		toolbar.setBorder(new LineBorder(toolbar.getBackground().darker()));
		
		toolbar.add(UIFactory.createButton(new NewCommand()));
		toolbar.add(UIFactory.createButton(new DeleteCommand()));
		toolbar.addSeparator();
		
		toolbar.add(UIFactory.createButton(new ShowCommand()));
		toolbar.add(UIFactory.createButton(new HideCommand()));
		toolbar.addSeparator();
		
		
		sorted = UIFactory.createToggleButton(new SortCommand());
		sorted.setSelected(false);
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
		renderer = new LayerListRenderer(null);
		layerList = new JList();
		layerList.setCellRenderer(renderer);
		layerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		layerList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = layerList.locationToIndex(e.getPoint());

					if (index != -1) {
						dwg.setCurrent((Layer) layerList.getSelectedValue());
					}
				}
			}
		});
		layerList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				props.setBean(layerList.getSelectedValue());
				props.revalidate();
				props.repaint();
			}
		});
		layerScroll = new JScrollPane(layerList); //UIFactory.createScrollPane(layerList);
		
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
			layers = new ArrayList();
		}
		
		else {
			layers = dwg.getFeatures(Layer.class);
			if (!sort) {
				Collections.sort(layers, new Comparator() {
					public int compare(Object a, Object b) {
						return ((Layer) a).getName().compareTo(((Layer) b).getName());
					}
				});
			}
		}
		
		layerList.setModel(new ListDataModel(layers));
	}

	/**
	 * Sets the model to obtain layers from.
	 * 
	 * @param model model to analize
	 */
	public void setModel(CoreModel model) {
		dwg = model;
		renderer = new LayerListRenderer(model);
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
		layerList.setEnabled(enabled);
		props.setEnabled(enabled);
	}

	/**
	 * Toggle properties command.
	 */
	private class PropsCommand extends AbstractAction {
		
		public PropsCommand() {
			super();
			
			putValue(Action.NAME, lang.getString("LayerManagerPanel.PropsCommand.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("LayerManagerPanel.PropsCommand.descr.short")); //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("LayerManagerPanel.PropsCommand.descr.long")); //$NON-NLS-1$
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
			
			putValue(Action.NAME, lang.getString("LayerManagerPanel.SortCommand.name")); //$NON-NLS-1$
			putValue(Action.SHORT_DESCRIPTION, lang.getString("LayerManagerPanel.SortCommand.descr.short")); //$NON-NLS-1$
			putValue(Action.LONG_DESCRIPTION, lang.getString("LayerManagerPanel.SortCommand.descr.long")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.SORT_ICON);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}

		public void actionPerformed(ActionEvent e) {
			sort = !sorted.isSelected();
			refresh();
		}
	}

	/**
	 * New layer command.
	 */
	private class NewCommand extends AbstractAction {
		public NewCommand() {
			super();
			putValue(Action.NAME, lang.getString("LayerManagerPanel.NewCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.NEW_LAYER_ICON);
//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}
		public void actionPerformed(ActionEvent e) {
			Layer src = (Layer) layerList.getSelectedValue();
			if (src == null) return;
			Layer copy = (Layer) src.clone();
			copy.setName(lang.getString("LayerManagerPanel.newLayerPrefix") + src.getName()); //$NON-NLS-1$
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
			putValue(Action.NAME, lang.getString("LayerManagerPanel.DeleteCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}
		public void actionPerformed(ActionEvent e) {
			int[] idx = layerList.getSelectedIndices();
			for (int i = 0; i < idx.length; i++) {
				try {
					dwg.removeFeature(layers.get(idx[i]));
				} catch (Throwable ex) {}
			}
			refresh();
		}
	}

	/**
	 * Make visible command.
	 */
	private class ShowCommand extends AbstractAction {
		public ShowCommand() {
			super();
			putValue(Action.NAME, lang.getString("LayerManagerPanel.ShowCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.VISIBLE_ICON);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
		}
		public void actionPerformed(ActionEvent e) {
			int[] idx = layerList.getSelectedIndices();
			for (int i = 0; i < idx.length; i++) {
				((Layer) layers.get(idx[i])).setVisible(true);
			}
			layerList.repaint();
		}
	}
	
	/**
	 * Make invisible command.
	 */
	private class HideCommand extends AbstractAction {
		public HideCommand() {
			super();
			putValue(Action.NAME, lang.getString("LayerManagerPanel.HideCommand.name")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.INVISIBLE_ICON);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		}
		public void actionPerformed(ActionEvent e) {
			int[] idx = layerList.getSelectedIndices();
			for (int i = 0; i < idx.length; i++) {
				((Layer) layers.get(idx[i])).setVisible(false);
			}
			layerList.repaint();
		}
	}
}