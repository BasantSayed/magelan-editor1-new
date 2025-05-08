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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import org.magelan.commons.Beans;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.ColorComboBox;
import org.magelan.commons.ui.DividerLabel;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.LabelledItemPanel;
import org.magelan.commons.ui.ListRenderer;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.CoreModel;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.DefaultEntity;
import org.magelan.core.EntityModifier;
import org.magelan.core.entity.LabelEntity;
import org.magelan.core.entity.LineEntity;
import org.magelan.core.entity.PointEntity;
import org.magelan.core.style.TextStyle;
import org.magelan.editor.Editor;


/**
 * 
 * @author Assen Antov
 * @version 2.1, 07/2006
 */
public class NewLabelDialog extends JDialog implements ActionListener {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	protected Collection entities;
	protected JLabel banner;
	protected LabelledItemPanel pane, pane2;
	protected CoreModel model;
	
	private JComboBox property, modifier, ent;
	private JButton okButton;
	private JButton cancelButton;
	
	protected JTabbedPane tab;
	
	private JTextField field, size, dx, dy, rotation, suf, pref;
	private ColorComboBox color;
	private JComboBox style;

	private LabelEntity templateLabel;
	
	
	/**
	 * If set to <code>false</code> all entities from the parameter list will
	 * be labelled and the labels added to the model. If set to
	 * <code>true</code> only one template label will be created.
	 */
	private boolean template;
	
	public NewLabelDialog() {
		this(new ArrayList(), new DefaultCoreModel(), null);
	}

	public NewLabelDialog(Collection entities, CoreModel model, java.awt.Frame parent) {
		super(parent, true);

		this.model = model;
		this.entities = entities;
		template = false;
		
		construct();
	}
	
	/**
	 * Create 1 temlate label only.
	 * 
	 * @param model
	 * @param parent
	 */
	public NewLabelDialog(CoreModel model, java.awt.Frame parent) {
		super(parent, true);

		this.model = model;
		this.entities = model.getElements();
		template = true;
		
		construct();
	}
	
	protected void construct() {

		// JFrame
		setBounds(0, 0, 300, 60);
		setTitle(lang.getString("NewLabelDialog.title")); //$NON-NLS-1$
		setResizable(true);
		getContentPane().setLayout(new BorderLayout(5, 5));

		//
		banner = new JLabel();
		UIFactory.configureBanner(banner, lang.getString("NewLabelDialog.banner"), null/*Icons.EDIT_TEXT_ICON*/); //$NON-NLS-1$
		banner.setPreferredSize(new Dimension(300, banner.getPreferredSize().height));
		banner.setMaximumSize(new Dimension(300, banner.getPreferredSize().height));
		getContentPane().add(banner, BorderLayout.NORTH);
		
		tab = new JTabbedPane();
		getContentPane().add(tab, BorderLayout.CENTER);
		
		pane = new LabelledItemPanel();
		
			DividerLabel div3 = new DividerLabel(lang.getString("NewLabel.LabelDialog.divider.general")); //$NON-NLS-1$
			pane.addDivider(div3);
			
			Vector v = new Vector();
			v.addAll(Beans.groupBeans(entities));
			
			ent = new JComboBox(v);
			ent.setRenderer(new Beans.BeanGroupRenderer());
			pane.addItem(lang.getString("NewLabel.LabelDialog.label.entities"), ent); //$NON-NLS-1$
			ent.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});

			modifier = new JComboBox();
			modifier.setRenderer(new ModifierListRenderer());
			pane.addItem(lang.getString("NewLabel.LabelDialog.label.modifier"), modifier); //$NON-NLS-1$

			property = new JComboBox();
			property.setRenderer(new PropertyListRenderer());
			pane.addItem(lang.getString("NewLabel.LabelDialog.label.property"), property); //$NON-NLS-1$
			
			DividerLabel div4 = new DividerLabel(lang.getString("NewLabel.LabelDialog.divider.geometry")); //$NON-NLS-1$
			pane.addDivider(div4);

			// dx coordinate
			dx = new JTextField();
			dx.setText("10.0"); //$NON-NLS-1$
			Dimension dim4 = new Dimension(80, dx.getPreferredSize().height);
			dx.setPreferredSize(dim4);
			dx.setMinimumSize(dim4);
			pane.addItem(lang.getString("NewLabelDialog.label.dx"), dx, false); //$NON-NLS-1$
			
			// dy coordinate
			dy = new JTextField();
			dy.setText("0"); //$NON-NLS-1$
			dy.setPreferredSize(dim4);
			dy.setMinimumSize(dim4);
			pane.addItem(lang.getString("NewLabelDialog.label.dy"), dy, false); //$NON-NLS-1$

			DividerLabel div5 = new DividerLabel(lang.getString("NewLabel.LabelDialog.divider.formatting")); //$NON-NLS-1$
			pane.addDivider(div5);
			
			// pref
			pref = new JTextField();
			pref.setText(""); //$NON-NLS-1$
			Dimension dim5 = new Dimension(100, pref.getPreferredSize().height);
			pref.setPreferredSize(dim5);
			pref.setMinimumSize(dim5);
			pane.addItem(lang.getString("NewLabelDialog.label.prefix"), pref, false); //$NON-NLS-1$
			
			// suf
			suf = new JTextField();
			suf.setText(""); //$NON-NLS-1$
			suf.setPreferredSize(dim5);
			suf.setMinimumSize(dim5);
			pane.addItem(lang.getString("NewLabelDialog.label.suffix"), suf, false); //$NON-NLS-1$
			
		tab.add(lang.getString("NewLabelDialog.tab.label"), pane); //$NON-NLS-1$
		
		pane2 = new LabelledItemPanel();
		
			DividerLabel div1 = new DividerLabel(lang.getString("NewLabelDialog.divider.general")); //$NON-NLS-1$
			pane2.addDivider(div1);
			
			// text style
			v = new Vector();
			v.add(null);
			v.addAll(model.getFeatures(TextStyle.class));
			
			style = new JComboBox(v);
			style.setRenderer(new TextStyleListRenderer());
//			style.setSelectedItem(model.getCurrent(TextStyle.class));
			style.setSelectedItem(null);
//			Dimension dim3 = new Dimension(75, style.getPreferredSize().height);
//			style.setPreferredSize(dim3);
//			style.setMinimumSize(dim3);
			pane2.addItem(lang.getString("NewLabelDialog.label.style"), style, false); //$NON-NLS-1$
			
			// entity color
			color = new ColorComboBox();
			color.setSelectedItem(null);
			//Dimension dim3 = new Dimension(75, color.getPreferredSize().height);
			//color.setPreferredSize(dim3);
			//color.setMinimumSize(dim3);
			pane2.addItem(lang.getString("NewLabelDialog.label.color"), color, false); //$NON-NLS-1$
			
			DividerLabel div2 = new DividerLabel(lang.getString("NewLabelDialog.divider.geom")); //$NON-NLS-1$
			pane2.addDivider(div2);
			
//			 text size
			size = new JTextField();
			size.setText("12.0"); //$NON-NLS-1$
			Dimension dim2 = new Dimension(50, size.getPreferredSize().height);
			size.setPreferredSize(dim2);
			size.setMinimumSize(dim2);
			pane2.addItem(lang.getString("NewLabelDialog.label.size"), size, false); //$NON-NLS-1$
			
			// rotation
			rotation = new JTextField();
			rotation.setText("0.0"); //$NON-NLS-1$
			Dimension dim6 = new Dimension(50, rotation.getPreferredSize().height);
			rotation.setPreferredSize(dim6);
			rotation.setMinimumSize(dim6);
			pane2.addItem(lang.getString("NewLabelDialog.label.rotation"), rotation, false); //$NON-NLS-1$
			
		tab.add(lang.getString("NewLabelDialog.tab.style"), pane2); //$NON-NLS-1$
		
		//
		JPanel p2 = new JPanel();
		
			// JButton
			okButton = new JButton();
			okButton.setText(lang.getString("common.ok")); //$NON-NLS-1$
			okButton.setIcon(Icons.OK_ICON);
			okButton.setName("okButton"); //$NON-NLS-1$
			p2.add(okButton);
	
			// JButton
			cancelButton = new JButton();
			cancelButton.setText(lang.getString("common.cancel")); //$NON-NLS-1$
			cancelButton.setIcon(Icons.CANCEL_ICON);
			cancelButton.setName("cancelButton"); //$NON-NLS-1$
			p2.add(cancelButton);

		getContentPane().add(p2, BorderLayout.SOUTH);

		update();
		pack();
		org.magelan.commons.ui.SwingUtil.center(this);

		getRootPane().setDefaultButton(okButton);

		// set listeners
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}


	protected void update() {
		Beans.BeanGroup selected = (Beans.BeanGroup) ent.getSelectedItem();
		if (selected == null) return;
		
		modifier.setModel(new DefaultComboBoxModel(
			((DefaultEntity) selected.getBeans().iterator().next()).getModifiers())
		);

		BeanInfo bi = null;
		try {
			bi = Introspector.getBeanInfo(selected.getGroupClass());
		} catch (IntrospectionException ie) {}
		PropertyDescriptor[] pd = bi.getPropertyDescriptors();
			
		Vector properties = new Vector(pd.length);
		for (int i = 0; i < pd.length; i++) {
			if (!pd[i].isHidden()) {
				properties.add(pd[i]);
			}
		}
		
		property.setModel(new DefaultComboBoxModel(properties));
	}
	
	protected void apply() {
		if (template) {
			Beans.BeanGroup selected = (Beans.BeanGroup) ent.getSelectedItem();
			selected.getGroupClass();
			Iterator iter = selected.getBeans().iterator();
			DefaultEntity entity = (DefaultEntity) iter.next();
							
			int idx = modifier.getSelectedIndex();
			idx = idx > entity.getModifiers().length - 1? entity.getModifiers().length - 1 : idx;
			EntityModifier mod = entity.getModifiers()[idx];
			String prop = ((PropertyDescriptor) property.getSelectedItem()).getName();
	
			LabelEntity l = new LabelEntity(entity, idx, prop);

			l.setDX(Double.parseDouble(dx.getText()));
			l.setDY(Double.parseDouble(dy.getText()));
			l.setPrefix(pref.getText());
			l.setSuffix(suf.getText());
			
			l.setTextStyle((TextStyle) style.getSelectedItem());
			l.setColor((Color) color.getSelectedItem());
			l.setSize(Float.parseFloat(size.getText()));
			l.setRotation(Float.parseFloat(rotation.getText()));
			
			templateLabel = l;
			return;
		}
		
		Beans.BeanGroup selected = (Beans.BeanGroup) ent.getSelectedItem();
		Iterator iter = selected.getBeans().iterator();
		while (iter.hasNext()) {
			DefaultEntity entity = (DefaultEntity) iter.next();
			
			int idx = modifier.getSelectedIndex();
			idx = idx > entity.getModifiers().length - 1? entity.getModifiers().length - 1 : idx;
			EntityModifier mod = entity.getModifiers()[idx];
			String prop = ((PropertyDescriptor) property.getSelectedItem()).getName();
			
			LabelEntity l = new LabelEntity(entity, idx, prop);
			
			l.setDX(Double.parseDouble(dx.getText()));
			l.setDY(Double.parseDouble(dy.getText()));
			l.setPrefix(pref.getText());
			l.setSuffix(suf.getText());
			
			l.setTextStyle((TextStyle) style.getSelectedItem());
			l.setColor((Color) color.getSelectedItem());
			l.setSize(Float.parseFloat(size.getText()));
			l.setRotation(Float.parseFloat(rotation.getText()));
			
			model.add(l);
		}
	}
		
	/**
	 * Listens for action events.
	 *
	 * @param e an event
	 */
	public void actionPerformed(ActionEvent e) {
		String src = ((java.awt.Component) e.getSource()).getName();

		if ("okButton".equals(src)) { //$NON-NLS-1$
			apply();
			setVisible(false);
			dispose();
		}
		else if ("cancelButton".equals(src)) { //$NON-NLS-1$
			setVisible(false);
			dispose();
		}
	}


	private class PropertyListRenderer extends ListRenderer {
		
		private PropertyDescriptor prop;
		
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

			prop = (PropertyDescriptor) value;
			
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setText(prop.getName());
			return comp;
		}
		
		public Icon getIcon(Object value) {
			return Icons.PROPERTY_ICON;
		}

		public String getToolTipText(Object value) {
			return prop.getDisplayName() + ": " + prop.getShortDescription(); //$NON-NLS-1$
		}
	}

	private class ModifierListRenderer extends ListRenderer {
		
		private EntityModifier mod;
		
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

			mod = (EntityModifier) value;
			
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			String text = lang.getString("NewLabelDialog.mod.NONE"); //$NON-NLS-1$
			switch (mod.getSnapPointType()) {
				case EntityModifier.CENTER: 
					text = lang.getString("NewLabelDialog.mod.CENTER"); //$NON-NLS-1$
					break;
				case EntityModifier.ENDPOINT: 
					text = lang.getString("NewLabelDialog.mod.ENDPOINT"); //$NON-NLS-1$
					break;
				case EntityModifier.MIDPOINT: 
					text = lang.getString("NewLabelDialog.mod.MIDPOINT"); //$NON-NLS-1$
					break;
				case EntityModifier.QUADRANT: 
					text = lang.getString("NewLabelDialog.mod.QUADRANT"); //$NON-NLS-1$
			}
			
			setText(text);
			return comp;
		}
		
		public Icon getIcon(Object value) {
			/*
			switch (mod.getSnapPointType()) {
				case EntityModifier.CENTER: 
					return Icons.M_CENTER_ICON;
				case EntityModifier.ENDPOINT: 
					return Icons.M_ENDPOINT_ICON;
				case EntityModifier.MIDPOINT: 
					return Icons.M_MIDPOINT_ICON;
				case EntityModifier.QUADRANT: 
					return Icons.M_QUADRANT_ICON;
			}
			*/
			return Icons.GAP_ICON_16;
		}

		public String getToolTipText(Object value) {
			return null;
		}
	}


	public LabelEntity getTemplate() {
		return templateLabel;
	}

	
	/**
	 * Opens a new dialog.
	 *
	 * @param args array of parameter strings
	 */
	public static void main(String[] args) {
		List lst = new ArrayList();
		lst.add(new LineEntity());
		lst.add(new LineEntity());
		lst.add(new LineEntity());
		lst.add(new PointEntity());
		lst.add(new PointEntity());
		lst.add(new PointEntity());
		
		(new NewLabelDialog(lst, new DefaultCoreModel(), null)).setVisible(true);
	}
}