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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.List;
import org.magelan.core.entity.HexagonEntity;
import org.magelan.commons.ui.propertytable.TableItem;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import org.magelan.commons.ui.propertytable.GroupTableItem;
import org.magelan.commons.ui.propertytable.PropertyEditor;
import org.magelan.commons.ui.propertytable.PropertyTable;
import org.magelan.commons.ui.propertytable.TableItem;
import org.magelan.core.style.Layer;
import org.magelan.core.style.LineStyle;
import org.magelan.core.style.TextStyle;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;


/**
 * Shows a table of <code>CoreModel</code> properties.
 * 
 * @author Assen Antov
 * @version 1.0,  06/2002
 * @version 1.1,  01/2009
 */
public class DrawingPropertyTable extends PropertyTable {

	private DrawingModel dwg;

	// editors for properties of various types
	private PropertyEditor layerEditor;
	private PropertyEditor lsEditor;
	private PropertyEditor tsEditor;
	private PropertyEditor rendererEditor;
	private PropertyEditor magelanEditor;
	

	/**
	 * Creates a new DrawingPropertyTable object.
	 */
	public DrawingPropertyTable() {
		super();
	}

	/**
	 * Creates a new DrawingPropertyTable object.
	 *
	 * @param bean DOCME!
	 */
	public DrawingPropertyTable(Object bean) {
		super(bean);
	}

	/**
	 * Creates a new DrawingPropertyTable object.
	 *
	 * @param data DOCME!
	 */
	public DrawingPropertyTable(TableItem[] data) {
		super(data);
	}

	/**
	 * Creates a new DrawingPropertyTable object.
	 *
	 * @param data DOCME!
	 */
	public DrawingPropertyTable(java.util.List data) {
		super(data);
	}

	/**
	 * Creates a new DrawingPropertyTable object.
	 *
	 * @param group DOCME!
	 */
	public DrawingPropertyTable(GroupTableItem group) {
		super(group);
	}

	public TableCellEditor getCellEditor(int row, int column) {
		TableItem p = (TableItem) getItems().get(row);
	
	
		if (p.getType() == Layer.class) {
		
				JComboBox layerEditor0;

				if (dwg != null) {
					layerEditor0 = new JComboBox(new Vector(dwg.getFeatures(Layer.class)));
				} else {
					layerEditor0 = new JComboBox();
				}

				layerEditor = new PropertyEditor(layerEditor0);
		

			layerEditor.setBean(/*getBean()*/dwg);
			return layerEditor;
		}
		
		
		if (p.getType() == LineStyle.class) {
	
				JComboBox lsEditor0;
				if (dwg != null) {
					lsEditor0 = new JComboBox(new Vector(dwg.getFeatures(LineStyle.class)));
				} else {
					lsEditor0 = new JComboBox();
				}

				lsEditor0.setRenderer(new LineStyleCellRenderer());
				lsEditor = new PropertyEditor(lsEditor0);
		

			lsEditor.setBean(dwg/*getBean()*/);
			return lsEditor;
		}

		
		if (p.getType() == TextStyle.class) {
	
				JComboBox tsEditor0;

				if (dwg != null) {
					tsEditor0 = new JComboBox(new Vector(dwg.getFeatures(TextStyle.class)));
				} else {
					tsEditor0 = new JComboBox();
				}

				tsEditor = new PropertyEditor(tsEditor0);
	

			tsEditor.setBean(dwg);
			return tsEditor;
		}

		if (p.getType() == DrawingRenderer.class) {
		//	if (rendererEditor == null) {
				JComboBox rendererEditor0;

				if (dwg != null) {
					rendererEditor0 = new JComboBox(new Vector(dwg.getFeatures(DrawingRenderer.class)));
				} else {
					rendererEditor0 = new JComboBox();
				}

				rendererEditor = new PropertyEditor(rendererEditor0);
	

			rendererEditor.setBean(dwg);
			return rendererEditor;
		}

		
		if (p.getType().getName().startsWith("org.magelan.")) {
			String type = p.getType().getName();
			List<Object> f = new ArrayList<Object>();
			
			Iterator iter = dwg.getFeatures().iterator();
			while (iter.hasNext()) {
				Object cl = iter.next();
				if (type.equals(cl.getClass().getName())) f.add(cl);
			}

			JComboBox magelanEditor0;
			if (dwg != null) {
				magelanEditor0 = new JComboBox(new Vector(f));
			} else {
				magelanEditor0 = new JComboBox();
			}

			magelanEditor = new PropertyEditor(magelanEditor0);
			magelanEditor.setBean(dwg);
			return magelanEditor;
		}
		
		return super.getCellEditor(row, column);
	}




	public void setDrawing(DrawingModel dwg) {
		this.dwg = dwg;
		layerEditor = null;
		lsEditor = null;
	}

	public DrawingModel getDrawing() {
		return dwg;
	}
}