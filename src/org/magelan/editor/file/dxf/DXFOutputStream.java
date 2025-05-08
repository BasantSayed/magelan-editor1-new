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
package org.magelan.editor.file.dxf;

import java.awt.Color;
import java.io.OutputStream;
import java.io.*;

/**
 * A DXF output stream.
 * 
 * @version 2.0, 08/2004
 * @author Assen Antov
 */
public class DXFOutputStream extends FilterOutputStream implements DXFConst {

	private boolean flip = true;
	private static final String LF = System.getProperty("line.separator"); //$NON-NLS-1$

	public DXFOutputStream(OutputStream str) throws IOException {
		super(str);

		// initialise dxf stream
		code(999, "magelan.editor.file.dxf.DXFOutputStream"); //$NON-NLS-1$
		code(999, IMPL_VERSION);
		code(999, COPYRIGHT);
	}

	/**
	 * Writes a pair of group code/double value to the stream.
	 * <p>
	 * <strong>Note: </strong> if the flip property is set, Y-coordinates (group
	 * codes from 20 to 29) will be negated.
	 * <p>
	 * <i>TODO: recognise all other group codes, that identify Y-coordinates.
	 * </i>
	 * <p>
	 * 
	 * @param code group code
	 * @param val value
	 */
	public void code(int code, double val) throws IOException {
		println("" + code); //$NON-NLS-1$
		if (flip && code >= 20 && code < 30)
			println("" + (-val)); //$NON-NLS-1$
		else
			println("" + val); //$NON-NLS-1$
	}

	/**
	 * Writes a pair of group code/integer value to the stream.
	 * <p>
	 * <strong>Note: </strong> if the flip property is set, Y-coordinates (group
	 * codes from 20 to 29) will be negated.
	 * <p>
	 * <i>TODO: recognise all other group codes, that identify Y-coordinates.
	 * </i>
	 * <p>
	 * 
	 * @param code group code
	 * @param val value
	 */
	public void code(int code, int val) throws IOException {
		println("" + code); //$NON-NLS-1$
		if (flip && code >= 20 && code < 30)
			println("" + (-val)); //$NON-NLS-1$
		else
			println("" + val); //$NON-NLS-1$
	}

	/**
	 * Writes a pair of group code/string value to the stream.
	 * <p>
	 * 
	 * @param code group code
	 * @param val value
	 */
	public void code(int code, String val) throws IOException {
		println("" + code); //$NON-NLS-1$
		println(val);
	}

	/**
	 * Utility method to convert the parameter <code>Color</code> to DXF
	 * integer color.
	 * <p>
	 * <i>TODO: improve color compatibility </i>
	 * <p>
	 * 
	 * @param color standard AWT color
	 * @return converted color
	 */
	public int toDXFColor(Color color) {
		if (color == null)
			return COLOR_BYLAYER;
		if (color.equals(Color.red))
			return COLOR_RED;
		else if (color.equals(Color.yellow))
			return COLOR_YELLOW;
		else if (color.equals(Color.green))
			return COLOR_GREEN;
		else if (color.equals(Color.cyan))
			return COLOR_CYAN;
		else if (color.equals(Color.blue))
			return COLOR_BLUE;
		else if (color.equals(Color.magenta))
			return COLOR_MAGENTA;
		else if (color.equals(Color.white))
			return COLOR_WHITE;
		else if (color.equals(Color.darkGray))
			return COLOR_DARK_GRAY;
		else if (color.equals(Color.orange))
			return COLOR_ORANGE;
		else if (color.equals(Color.black))
			return COLOR_BLACK;
		else if ((color.getRed() <= 204 && color.getRed() >= 153)
				&& (color.getGreen() <= 204 && color.getGreen() >= 153)
				&& (color.getBlue() <= 255 && color.getBlue() >= 230))
			return 153;
		else if (color.equals(new Color(204, 204, 204)))
			return 253;
		else if (color.equals(new Color(153, 153, 153)))
			return 252;
		else {
			int cc = color.getRGB() > 0 ? color.getRGB() >> 16 : -color.getRGB() >> 16;
			if (cc == 102)
				return 153;
			return cc;
		}
	}

	public void setFlip(boolean f) {
		flip = f;
	}

	public boolean getFlip() {
		return flip;
	}

	public void println(String s) throws IOException {
		if (s == null) {
			s = "null"; //$NON-NLS-1$
		}
		write(s.getBytes());
		write(LF.getBytes());
	}

}