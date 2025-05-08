/*
 * The Magelan Project - Vector Graphics Library and Editor
 * Copyright (c) 2003-2005, Assen Antov and Larisa Feldman. All rights reserved.
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.Customizer;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.magelan.commons.Beans;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.*;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.DefaultEntity;
import org.magelan.core.db.DataSource;
import org.magelan.core.db.DelimitedFileDataSource;
import org.magelan.core.entity.*;
import org.magelan.core.renderer.ColorDecorator;
import org.magelan.core.renderer.Decorator;
import org.magelan.core.renderer.PropertyRangeMapper;
import org.magelan.core.renderer.RangeMapper;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.Editor;
import org.magelan.editor.IEditorManager;


/**
 * magelan-editor
 * 
 * @version 1.0, 02/2005
 * @author Assen Antov
 */
public class DecoratorCustomizer extends LabelledItemPanel implements Customizer {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private ColorDecorator decorator;
	private DrawingModel model;
	private Class[] classes = new Class[] { 
		DefaultEntity.class, LineEntity.class, PolyLineEntity.class, PointEntity.class,
		CircleEntity.class, HatchEntity.class, InsertEntity.class, LabelEntity.class,
		TextEntity.class, ViewEntity.class
	};
	//private RangeMapper mapper;
	
	private JComboBox dsCombo, applyCombo, propsCombo;
	private JSpinner intField;
	private JTable rangeTable;
	
	public DecoratorCustomizer() {
		super();
		setPreferredSize(new Dimension(200, 400));
		
		// try to obtain the available data sources
		List dataSources = null;
		if (Editor.getInstance() != null) {
			IEditorManager em = Editor.getEditorManager();
			model = em.getSelectedEditor().getModel();
			dataSources = new ArrayList();
			dataSources.add(lang.getString("DecoratorCustomizer.builtin")); //$NON-NLS-1$
			dataSources.addAll(model.getFeatures(DataSource.class));
		}
		
		// for tests
		else {
			dataSources = new ArrayList();
			dataSources.add(lang.getString("DecoratorCustomizer.builtin")); //$NON-NLS-1$
			dataSources.add(new DelimitedFileDataSource("Test", "test.txt")); //$NON-NLS-1$ //$NON-NLS-2$
			model = new DefaultCoreModel();
			model.add(new LineEntity(0, 0, 100, 0));
			model.add(new LineEntity(0, 0, 200, 0));
			model.add(new LineEntity(0, 0, 300, 0));
			model.add(new LineEntity(0, 0, 500, 0));
			decorator = new ColorDecorator();
		}
		
		/*
		 * General
		 */
		addDivider(new DividerLabel(lang.getString("DecoratorCustomizer.divider.general"))); //$NON-NLS-1$
		
		// apply to which entities
//		applyCombo = new JComboBox(classes);
		applyCombo = new JComboBox(Beans.getClasses(model.getElements()));
		applyCombo.setRenderer(new Beans.ClassListRenderer());
		applyCombo.addActionListener(apChange);
		addItem(lang.getString("DecoratorCustomizer.label.apply"), applyCombo); //$NON-NLS-1$
		
		// add the available data sources
		dsCombo = new JComboBox(dataSources.toArray());
		dsCombo.setRenderer(new Beans.BeanListRenderer());
		dsCombo.addActionListener(dsChange);
		addItem(lang.getString("DecoratorCustomizer.label.ds"), dsCombo); //$NON-NLS-1$
		
		// add the available data sources
		propsCombo = new JComboBox();
		propsCombo.addActionListener(propChange);
		addItem(lang.getString("DecoratorCustomizer.label.property"), propsCombo); //$NON-NLS-1$
		
		/*
		 * Range
		 */
		addDivider(new DividerLabel(lang.getString("DecoratorCustomizer.divider.range"))); //$NON-NLS-1$
		
		// number of ranges
		intField = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
		addItem(lang.getString("DecoratorCustomizer.label.intervals"), intField, false); //$NON-NLS-1$
		
		/*
		mapper = new PropertyRangeMapper();
		mapper.setMapping(
				new Double[] { new Double(0), new Double(10), new Double(50), new Double(100), new Double(200), new Double(500), new Double(1000) },
				ColorDecorator.getRedPalette(7)
			);
		*/
		
		rangeTable = new JTable();
		rangeTable.setDefaultRenderer(Color.class, new ColorRenderer());
		//rangeTable.setModel(new RangeModel(decorator.getRangeMapper()));
		rangeTable.setDefaultEditor(Color.class, new ColorEditor());
		JScrollPane scroll = new JScrollPane(rangeTable);
		scroll.setPreferredSize(new Dimension(200, 200));
		scroll.setMinimumSize(new Dimension(200, 150));
		//scroll.setMaximumSize(new Dimension(200, 200));
		addDivider(scroll);
	}

	class RangeModel extends AbstractTableModel {
		
		RangeMapper mapper;
		
		RangeModel(RangeMapper mapper) {
			this.mapper = mapper;
		}
		
		public Class getColumnClass(int columnIndex) {
			if (columnIndex == 0) return Color.class;
			if (columnIndex == 1) return Double.class;
			if (columnIndex == 2) return String.class;
			return null;
		}

		public int getColumnCount() {
			return 3;
		}

		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) return lang.getString("DecoratorCustomizer.col.decoration"); //$NON-NLS-1$
			if (columnIndex == 1) return lang.getString("DecoratorCustomizer.col.interval"); //$NON-NLS-1$
			if (columnIndex == 2) return lang.getString("DecoratorCustomizer.col.label"); //$NON-NLS-1$
			return null;
		}

		public int getRowCount() {
			return mapper.getValues().length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return mapper.getDecorations()[rowIndex];
			}
			if (columnIndex == 1) {
				return mapper.getValues()[rowIndex];
			}
			if (columnIndex == 2) {
				return mapper.getLabel(rowIndex);
			}
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			//if (columnIndex == 1 || columnIndex == 2) return true;
			return true;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 1) {
				Comparable val = null;
				if (aValue instanceof Double) val = (Comparable) aValue;
				else if (aValue instanceof String) val = new Double(Double.parseDouble((String) aValue));
				mapper.getValues()[rowIndex] = val;
			}
			if (columnIndex == 2) {
				mapper.setLabel(rowIndex, (String) aValue);
			}
			if (columnIndex == 0) {
				mapper.getDecorations()[rowIndex] = (Color) aValue;
			}
		}
	}

	private ActionListener apChange = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object sel = applyCombo.getSelectedItem();
			if (sel == null) return;
			decorator.setDecClass((Class) sel);
		}
	};
	
	private ActionListener dsChange = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object sel = dsCombo.getSelectedItem();
			if (sel == null) return;
			
			if (sel instanceof String) {
				Class cl = (Class) applyCombo.getSelectedItem();
				PropertyDescriptor[] pd = Beans.getVisibleDescriptors(cl);
				propsCombo.setRenderer(new Beans.PropertyListRenderer());
				propsCombo.setModel(new DefaultComboBoxModel(pd));

				if (model != null) {
					RangeMapper mapper = decorator.getRangeMapper(); 
					if (mapper instanceof PropertyRangeMapper) {
						String prop = ((PropertyDescriptor) propsCombo.getSelectedItem()).getName();
						Color[] decor = ColorDecorator.getRedPalette(((Number) intField.getValue()).intValue());
						((PropertyRangeMapper) mapper).classify(model.getElements(), cl, prop, decor);
						rangeTable.setModel(new RangeModel(mapper));
					}
				}
			}
			if (sel instanceof DataSource) {
				try {
					String[] fields = ((DataSource) sel).getFields();
					propsCombo.setRenderer(new DefaultListCellRenderer());
					propsCombo.setModel(new DefaultComboBoxModel(fields));
				} catch (Throwable t) {
					ErrorDialog.showErrorDialog(lang.getString("DecoratorCustomizer.error.connect"), t); //$NON-NLS-1$
					dsCombo.setSelectedIndex(0); // select built-in
				}
			}
		}
	};
	
	private ActionListener propChange = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object sel = propsCombo.getSelectedItem();
			if (sel == null) return;
			
			if (sel instanceof PropertyDescriptor) {
				Class cl = (Class) applyCombo.getSelectedItem();

				if (model != null) {
					RangeMapper mapper = decorator.getRangeMapper(); 
					if (mapper instanceof PropertyRangeMapper) {
						String prop = ((PropertyDescriptor) propsCombo.getSelectedItem()).getName();
						Color[] decor = ColorDecorator.getRedPalette(((Number) intField.getValue()).intValue());
						((PropertyRangeMapper) mapper).classify(model.getElements(), cl, prop, decor);
						System.out.println("---"); //$NON-NLS-1$
						rangeTable.setModel(new RangeModel(mapper));
					}
				}
			}
		}
	};
	
	/**
	 * @see java.beans.Customizer#setObject(java.lang.Object)
	 */
	public void setObject(Object bean) {
		decorator = (ColorDecorator) bean;
		applyCombo.setSelectedItem(decorator.getDecClass());
		rangeTable.setModel(new RangeModel(decorator.getRangeMapper()));
	}
	
	
	public class ColorRenderer extends DefaultTableCellRenderer {
		
		private Icons.ColorIcon colorIcon = new Icons.ColorIcon();

		public ColorRenderer() {
			super();
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected,
													   boolean hasFocus, int row,
													   int column) {
			// Fix back- and foreground
			if (isSelected) {
				super.setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
				fore = table.getSelectionForeground();
			} else {
				super.setForeground(table.getForeground());
				super.setBackground(table.getBackground());
				fore = table.getForeground();
			}

			// Set up font to be same as table font
			setFont(table.getFont());

			// Set border
			if (hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); //$NON-NLS-1$
			} else {
				setBorder(noFocusBorder);
			}

			Color val = (Color) value;

			// Adjust color icon
			colorIcon.setValue(val);
			colorIcon.setRowHeight(table.getRowHeight());
			setIcon(colorIcon);
			setDisabledIcon(colorIcon);

			String textVal = org.magelan.commons.ui.SwingUtil.color2String(val);
			setValue(textVal);
			setToolTipText(textVal);

			setIconTextGap(1);

			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) &&
								 (back.equals(table.getBackground())) &&
								 table.isOpaque();
			setOpaque(!colorMatch);

			// ---- end optimization to aviod painting background ----
			return this;
		}
		Color fore;
	}
	
	public class ColorEditor extends DefaultCellEditor {
		ColorEditor() {
			super(new ColorComboBox());
		}
	}
	
	/**
	 * 
	 * magelan-editor
	 * 
	 * @version 1.0, 2005-9-24
	 * @author Assen Antov
	 */
	public static class DecoratorCustomizerDialog extends JDialog {

		private DecoratorCustomizer panel;
		private JButton closeButton;
		
		public DecoratorCustomizerDialog(Frame parent) {
			super(parent);
			construct();
		}
		
		private void construct() {
			setTitle(lang.getString("DecoratorCustomizer.title")); //$NON-NLS-1$
			getContentPane().setLayout(new BorderLayout());
			setModal(true);
			
			/*
			 * Setup a banner
			 */
			JLabel title = UIFactory.createBanner(lang.getString("DecoratorCustomizer.banner")); //$NON-NLS-1$
			getContentPane().add(title, BorderLayout.NORTH);

			/*
			 * Styles view panel
			 */
			panel = new DecoratorCustomizer();
			getContentPane().add(panel, BorderLayout.CENTER);
			
			/*
			 * Setup button panel
			 */
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
			buttons.setBorder(UIFactory.createTopBorder());
			
			closeButton = new JButton(new CloseCommand());
			buttons.add(closeButton);
			
			getContentPane().add(buttons, BorderLayout.SOUTH);
			
			getRootPane().setDefaultButton(closeButton);
			pack();
			setBounds(0, 0, 280, 410);
			SwingUtil.center(this);
		}
		
		public void setObject(Decorator dec) {
			panel.setObject(dec);
		}
		
		private class CloseCommand extends AbstractAction {
			public CloseCommand() {
				super();
				putValue(Action.NAME, lang.getString("common.close")); //$NON-NLS-1$
				putValue(Action.SMALL_ICON, Icons.OK_ICON);
	//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
				putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			}
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}
	
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		DecoratorCustomizer dc = new DecoratorCustomizer();
		f.add(dc);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(new Dimension(250, 400));
		f.setVisible(true);
	}
}
