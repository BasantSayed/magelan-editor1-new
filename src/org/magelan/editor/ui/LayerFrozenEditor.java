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

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;

import org.magelan.commons.ui.Icons;
import org.magelan.core.style.Layer;


/**
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.0, 03/2002
 */
public class LayerFrozenEditor extends JToggleButton
	implements TableCellEditor {
	//~ Instance fields --------------------------------------------------------

	// 
	private Layer layer;
	protected EventListenerList listenerList = new EventListenerList();
	transient protected ChangeEvent changeEvent = null;
	PropertyChangeListener listener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e) {
			if (e.getPropertyName().equals("frozen")) { //$NON-NLS-1$
				LayerFrozenEditor.this.layer.removePropertyChangeListener(this);
				setValue((Boolean) e.getNewValue());
				LayerFrozenEditor.this.layer.addPropertyChangeListener(this);
			}
		}
	};


	//~ Constructors -----------------------------------------------------------

	/**
						*/
	public LayerFrozenEditor() {
		this(new Layer());
	}

	/**
						*/
	public LayerFrozenEditor(Layer layer) {
		super();

		setPreferredSize(new Dimension(20, 20));
		setText(""); //$NON-NLS-1$
		setName("frozenButton"); //$NON-NLS-1$
		setContentAreaFilled(false);
		setLayer(layer);
		addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setValue(new Boolean(isSelected()));
				}
			});
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		try {
			((Layer) layer).removePropertyChangeListener(listener);
		} catch (Exception e) { /* some error handling? */
		}
	}

	/**
	 * DOCME!
	 *
	 * @param listener DOCME!
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		try {
			((Layer) layer).addPropertyChangeListener(listener);
		} catch (Exception e) { /* some error handling? */
		}
	}

	/**
	 * DOCME!
	 *
	 * @param value DOCME!
	 */
	public void setValue(Object value) {
		boolean b = ((Boolean) value).booleanValue();
		setSelected(b);
		setIcon(b ? Icons.FROZEN_ICON : Icons.UNFROZEN_ICON);

		if (layer != null) {
			layer.setFrozen(b);
		}
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public Object getValue() {
		return new Boolean(layer.isFrozen());
	}



	/*
	                TableCellEditor
	*/
	public Component getTableCellEditorComponent(JTable table, Object value,
												 boolean isSelected, int row,
												 int column) {
		return this;
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public Object getCellEditorValue() {
		return new Boolean(isSelected());
	}

	/**
	 * DOCME!
	 *
	 * @param e DOCME!
	 *
	 * @return DOCME!
	 */
	public boolean isCellEditable(java.util.EventObject e) {
		return true;
	}

	/**
	 * DOCME!
	 *
	 * @param s DOCME!
	 *
	 * @return DOCME!
	 */
	public boolean shouldSelectCell(java.util.EventObject s) {
		return true;
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public boolean stopCellEditing() {
		fireEditingStopped();

		return true;
	}

	/**
	 * DOCME!
	 */
	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	/**
	 * DOCME!
	 *
	 * @param l DOCME!
	 */
	public void addCellEditorListener(CellEditorListener l) {
		listenerList.add(CellEditorListener.class, l);
	}

	/**
	 * DOCME!
	 *
	 * @param l DOCME!
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		listenerList.remove(CellEditorListener.class, l);
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public CellEditorListener[] getCellEditorListeners() {
		return (CellEditorListener[]) listenerList.getListeners(CellEditorListener.class);
	}

	/**
	 * DOCME!
	 */
	protected void fireEditingStopped() {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}

				((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
			}
		}
	}

	/**
	 * DOCME!
	 */
	protected void fireEditingCanceled() {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}

				((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
			}
		}
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public Layer getLayer() {
		return layer;
	}

	/**
	 * DOCME!
	 *
	 * @param layer DOCME!
	 */
	public void setLayer(Layer layer) {
		if (this.layer != null) {
			this.layer.removePropertyChangeListener(listener);
		}

		if (layer != null) {
			setSelected(layer.isFrozen());
			setIcon(layer.isFrozen() ? Icons.FROZEN_ICON : Icons.UNFROZEN_ICON);
			layer.addPropertyChangeListener(listener);
		}

		this.layer = layer;
	}
}