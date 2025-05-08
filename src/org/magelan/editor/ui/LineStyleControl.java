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

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.magelan.core.CoreModel;
import org.magelan.core.style.LineStyle;
import org.magelan.drawing.DrawingModel;


/**
 * A line style chooser class.
 * 
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.01, 04/2002
 * @version 2.0, 07/2003
 */
public class LineStyleControl extends JComboBox {
	//~ Instance fields --------------------------------------------------------

	/** CoreModel to read the styles from. */
	private DrawingModel dwg;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates the editor with a new default drawing.
	 */
	public LineStyleControl() {
		this(null);
	}

	/**
	 * Creates the customizer with a line style to edit.
	 * 
	 * <p></p>
	 *
	 * @param dwg a line style object
	 */
	public LineStyleControl(CoreModel dwg) {
		setRenderer(new LineStyleCellRenderer());
		setDrawing(dwg);
	}

	//~ Methods ----------------------------------------------------------------

	public void setDrawing(DrawingModel dwg) {
		this.dwg = dwg;

		if (dwg != null) {
			setModel(new DefaultComboBoxModel(new Vector(dwg.getFeatures(LineStyle.class))));
			setSelectedItem(dwg.getCurrent(LineStyle.class));
		}
		else {
			setModel(new DefaultComboBoxModel(new Vector()));
		}
		setEnabled(true);
	}

	public DrawingModel getDrawing() {
		return dwg;
	}
}