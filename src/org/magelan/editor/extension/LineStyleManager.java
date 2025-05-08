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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.style.Layer;
import org.magelan.core.style.LineStyle;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;


/**
 *
 * @author Assen Antov
 * @version 1.0
 */
public class LineStyleManager extends AbstractEditorExtension {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	DrawingModel dwg;
	List lineStyles;
	LineStyleTable table;
	LineStyleDialog dialog;

	public LineStyleManager() {
		super();

		putValue(Action.NAME, "LineStyleManager"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 03/2002"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("LineStyleManager.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("LineStyleManager.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.LINESTYLE_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}

	
	public void run(DrawingEditor e) {
		dwg = e.getModel();
		lineStyles = dwg.getFeatures(LineStyle.class);
		
		if (dialog == null) {
			dialog = new LineStyleDialog();
		}
		dialog.setVisible(true);
	}


	class LineStyleTableModel extends AbstractTableModel {
		public int getColumnCount() {
			return 3;
		}
		public int getRowCount() {
			return LineStyleManager.this.lineStyles.size();
		}
		public Object getValueAt(int row, int col) {
			return LineStyleManager.this.lineStyles.get(row);
		}
		public String getColumnName(int column) {
			switch (column) {
				//	case 0: return "On/Off";
				case 0:
					return lang.getString("LineStyleTable.name"); //$NON-NLS-1$
		
				case 1:
					return lang.getString("LineStyleTable.scale"); //$NON-NLS-1$
		
				case 2:
					return lang.getString("LineStyleTable.preview"); //$NON-NLS-1$
			}
		
			return ""; // Shouldn't happen! //$NON-NLS-1$
		}
		public boolean isCellEditable(int row, int col) {
			return (col == 0);
		}
		public void setValueAt(Object aValue, int row, int column) {
		}
	}

	class LineStyleTable extends JTable {

		ManagerRenderer renderer = new ManagerRenderer();


		public TableCellRenderer getCellRenderer(int row, int column) {
			return renderer;
		}
	}

	/**
	 * Custom table cell renderer.
	 */
	class ManagerRenderer extends DefaultTableCellRenderer {

		public ManagerRenderer() {
			super();
		}

		public Component getTableCellRendererComponent(JTable table,
													   Object value,
													   boolean isSelected,
													   boolean hasFocus,
													   int row, int column) {
			if (isSelected) {
				super.setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				super.setForeground(table.getForeground());
				super.setBackground(table.getBackground());
			}

			//setFont(table.getFont());
			if (hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); //$NON-NLS-1$

				if (table.isCellEditable(row, column)) {
					super.setForeground(UIManager.getColor("Table.focusCellForeground")); //$NON-NLS-1$
					super.setBackground(UIManager.getColor("Table.focusCellBackground")); //$NON-NLS-1$
				}
			} else {
				setBorder(noFocusBorder);
			}

			// CUSTOM CODE
      LineStyle ls = (LineStyle) lineStyles.get(row);
			setIcon(null);
			setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

			switch (column) {
				case 0:
					setText(ls.getName());

					if (ls.getName().equals(((Layer) dwg.getCurrent(Layer.class)).getName())) {
						setFont(new Font("Dialog", Font.BOLD, 12)); //$NON-NLS-1$
					}

					break;

				case 1:
					setText("" + ls.getScale()); //$NON-NLS-1$

					break;

				case 2:
					setText(null);
					setIcon(new LineStylePreview(ls, this));

					break;
			}

			setToolTipText(getText());

			// ---- begin optimization to avoid painting background ----
			Color back = getBackground();
			boolean colorMatch = (back != null) &&
								 (back.equals(table.getBackground())) &&
								 table.isOpaque();
			setOpaque(!colorMatch);

			// ---- end optimization to aviod painting background ----
			return this;
		}
	}

	/**
	
		*/
	private class LineStylePreview implements Icon {
		//~ Instance fields ----------------------------------------------------

		int offset = 0;
		Color color;
		LineStyle ls;
		int thickness;
		JLabel l;

		//~ Constructors -------------------------------------------------------

		public LineStylePreview(LineStyle ls, JLabel l) {
			this(ls, 1, Color.black, l);
		}

		public LineStylePreview(LineStyle ls, int thickness, JLabel l) {
			this(ls, thickness, Color.black, l);
		}

		public LineStylePreview(LineStyle ls, int thickness, Color color,
								JLabel l) {
			this.ls = ls;
			this.color = color;
			this.thickness = thickness;
			this.l = l;
		}

		//~ Methods ------------------------------------------------------------

		public void paintIcon(Component c, Graphics g, int x, int y) {
			offset = y + l.getHeight() / 2;
			g.setColor(color);
		//	ls.paint(g, x + 5, offset, x + l.getWidth() - 5, offset, thickness,
			//		 0);
		}

		public int getIconWidth() {
			return l.getWidth() - 10;
		}

		public int getIconHeight() {
			return l.getHeight();
		}
	}
	
	class LineStyleDialog extends LineStyleManagerLoader implements ActionListener{

		public LineStyleDialog() {
			super();
			center(this);
			getRootPane().setDefaultButton(okButton);
			panel1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(),
																 lang.getString("Line_Styles_17"))); //$NON-NLS-1$

			//  setIconImage((new ImageIcon("magelan/images/linestyle.gif")).getImage());
			//this.dwg = dwg;
			//lineStyles = dwg.getLineStyles();
			// javax.swing.JTable
			table = new LineStyleTable();

			//table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			scroll.setViewportView(table);

			//table.addActionListener(this);
			setButton.addActionListener(this);
			removeButton.addActionListener(this);
			helpButton.addActionListener(this);
			okButton.addActionListener(this);

			// set the table model
		
			TableModel dataModel = new LineStyleTableModel();

			table.setModel(dataModel);
		}

		//~ Methods ----------------------------------------------------------------

		/**
		 * Listens for <code>ActionEvent</code>s.
		 * 
		 * <p></p>
		 *
		 * @param e action event
		 */
		public void actionPerformed(java.awt.event.ActionEvent e) {
			String src = ((java.awt.Component) e.getSource()).getName();

			if ("setButton".equals(src)) { //$NON-NLS-1$
				if (table.getSelectedRow() != -1) {
			dwg.setCurrent((LineStyle) lineStyles.get(table.getSelectedRow()));
					table.repaint();
				}
			}

			if ("removeButton".equals(src)) { //$NON-NLS-1$
			}

			if ("helpButton".equals(src)) { //$NON-NLS-1$
			}

			if ("okButton".equals(src)) { //$NON-NLS-1$
				setVisible(false);
			}
		}

		/**
		 * Centers the component on screen.
		 * 
		 * <p></p>
		 *
		 * @param c the component to center
		 */
		public void center(Component c) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			c.setLocation(screenSize.width / 2 - c.getSize().width / 2,
						  screenSize.height / 2 - c.getSize().height / 2);
		}

	}	
	
}