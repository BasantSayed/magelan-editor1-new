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
package org.magelan.editor.file.svg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.magelan.core.CoreModel;
import org.magelan.core.DefaultEntity;
import org.magelan.core.Entity;
import org.magelan.core.ExplodableEntity;
import org.magelan.core.entity.CircleEntity;
import org.magelan.core.entity.LineEntity;
import org.magelan.core.entity.PathEntity;
import org.magelan.core.entity.PointEntity;
import org.magelan.core.entity.PolyLineEntity;
import org.magelan.core.entity.TextEntity;
import org.magelan.core.style.Layer;
import org.magelan.core.style.LineStyle;
import org.magelan.core.style.TextStyle;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;

/**
 * An encoder to write <code>DrawingModel</code> instances to SVG format.
 *
 * @version 1.2, 02/2005
 * @author  Bernard Desprez
 * @author  Assen Antov
 */
public class SVGEncoder {

	private static Logger log = Logger.getLogger(SVGEncoder.class);
	
	public static final byte SEG_MOVETO  = (byte) PathIterator.SEG_MOVETO;
	public static final byte SEG_LINETO  = (byte) PathIterator.SEG_LINETO;
	public static final byte SEG_QUADTO  = (byte) PathIterator.SEG_QUADTO;
	public static final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
	public static final byte SEG_CLOSE   = (byte) PathIterator.SEG_CLOSE;

	public static final String NAME_0 = "O";
	
	public static boolean whiteToBlack = true;
	
	/**
	 * Encodes a <code>DrawingModel</code> to SVG. 
	 */
	public static void encode(DrawingModel dwg, DrawingRenderer renderer, OutputStream out) throws IOException {
		PrintWriter prt = new PrintWriter(out, true);
		
		prt.println("<?xml " +  //$NON-NLS-1$
			"version=\"1.0\" " + //$NON-NLS-1$
			//"encoding=\"" + System.getProperty("file.encoding")+ "\" " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ // FIXME encoding
			"standalone=\"yes\"" + //$NON-NLS-1$
			"?>" ); //$NON-NLS-1$

		// write drawing extents
		Rectangle2D ext = renderer.getExtents(dwg);
		prt.println("<svg " + //$NON-NLS-1$
			"width=\"" + (int) (ext.getX() + ext.getWidth()) + "\" " + //$NON-NLS-1$ //$NON-NLS-2$
			"height=\"" + (int) (ext.getY() + ext.getHeight()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			">"); //$NON-NLS-1$
		
		// write title
		String s = (String) dwg.getValue(CoreModel.KEY_NAME);
		if (s != null) {
			prt.println("<title>" + s + "</title>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		// write comments
		s = (String) dwg.getValue(CoreModel.KEY_AUTHOR);
		if (s != null) {
			prt.println("<!-- Created by: " + s + " -->"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		prt.println("<!-- Created using: Magelan " + org.magelan.editor.Editor.VERSION + " -->"); //$NON-NLS-1$ //$NON-NLS-2$
		prt.println("<!-- Created on: " + new java.util.Date() + " -->"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// write layers as CSS style definitions
		prt.println("<defs><style type=\"text/css\">"); //$NON-NLS-1$
		
		List lst = dwg.getFeatures(Layer.class);
		Iterator iter = lst.iterator();
		while (iter.hasNext()) {
			Layer l = (Layer) iter.next();
			
			String ln = l.getName();
			if (ln.equals("0")) ln = NAME_0;
			prt.print("." + ln + " {"); //$NON-NLS-1$ //$NON-NLS-2$
			
			// color
			prt.print("stroke: " + getColor(l.getColor()) + "; ");
			
			// thickness
			if (l.getThickness() != 0) {
				prt.print("stroke-width: " + l.getThickness() + "; "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			// linestyle
			LineStyle ls = l.getLineStyle();
			Stroke stroke = ls.getStroke();
			if (stroke instanceof BasicStroke) {
				float[] array = ((BasicStroke) stroke).getDashArray();
				if (array != null) {
					String dash = dashArrayToSVG(array);
					prt.print("stroke-dasharray: " + dash + "; "); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {
				log.debug("could not encode linestyle: " + ls); //$NON-NLS-1$
			}
			
			// textstyle
			TextStyle ts = l.getTextStyle();
			prt.print("font-family: " + ts.getFont().getFontName() + "; "); //$NON-NLS-1$ //$NON-NLS-2$
			prt.print("font-size: " + ts.getSize() + "; "); //$NON-NLS-1$ //$NON-NLS-2$
			int fs = ts.getStyle();
			if (fs == 1 || fs == 3) {
				prt.print("font-weight: bold; "); //$NON-NLS-1$
			}
			if (fs == 2) {
				prt.print("font-style: italic; "); //$NON-NLS-1$
			}
			
			prt.println("}"); //$NON-NLS-1$
		}
		
		prt.println("</style></defs>"); //$NON-NLS-1$
		
		// write all entities
		try {
			iter = dwg.getElements().iterator();
			while (iter.hasNext()) {
				Entity e = (Entity) iter.next();
				encodeEntity(e, prt);
			}
		} catch (Throwable ex) {
			log.error("unexpected error", ex); //$NON-NLS-1$
		}
		
		// close the file
		prt.println("</svg>"); //$NON-NLS-1$
		prt.flush();
		prt.close();
	}

	
	private static void encodeEntity(Entity e, PrintWriter prt) {
		if (e instanceof LineEntity) {
			encodeLine((LineEntity) e, prt);
		}
		else if (e instanceof CircleEntity) {
			encodeCircle((CircleEntity) e, prt);
		}
		else if (e instanceof PolyLineEntity) {
			encodePolyLine((PolyLineEntity) e, prt);
		}
		else if (e instanceof TextEntity) {
			encodeText((TextEntity) e, prt);
		}
		else if (e instanceof PathEntity) {
			encodePath((PathEntity) e, prt);
		}
		else if (e instanceof ExplodableEntity) {
			Iterator iter = ((ExplodableEntity) e).explode();
			while (iter.hasNext()) {
				encodeEntity(e, prt);
			}
		}
		else {
			log.debug("unsupported entity: " + e.getClass().getName() + ", " + e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
		
	private static void encodePath(PathEntity pe, PrintWriter prt) {
		if (pe.points.size() < 2) return;
		
		prt.println("<path"); //$NON-NLS-1$
		encodeStyle(pe, prt);
		prt.println(" d=\""); //$NON-NLS-1$
		
		String line = " "; //$NON-NLS-1$
		
		int type;
		int p = 0;
		double cx, cy;
		double lx = 0, ly = 0;
		boolean first = true;
		
		for (int j = 0; j < pe.cmds.size(); j++) {
			type = ((Byte) pe.cmds.elementAt(j)).intValue();

			PointEntity pt = (PointEntity) pe.points.elementAt(p++);
			cx = pt.getX();
			cy = pt.getY();

			switch(type) {
				case SEG_MOVETO:   
					if (first) {
						line += "M "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$
						first = false;
					}
					else {
                           cx -=lx;cy -=ly;
                           line += "m "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$
                         }
                         lx = cx;ly = cy;

                        break;
                      case SEG_LINETO:  
//  System.out.println("lineTo "+pt);
                        cx -=lx;cy -=ly;
                        line += "l "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$
//  System.out.println(line);
                        lx = pt.getX();ly = pt.getY();
                        break;
                      case SEG_QUADTO: 
                        cx -= lx; cy -= ly; 
                        line += "q "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$

                        pt = (PointEntity)pe.points.elementAt(p++);
                        cx = pt.getX();cy = pt.getY();
                        cx -= lx; cy -= ly;
                        line += " "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$
                        lx = pt.getX();ly = pt.getY();
                        break;
                      case SEG_CUBICTO: 
//System.out.println(p);
                        cx -= lx; cy -= ly;
                        line += "c "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$

                        pt = (PointEntity)pe.points.elementAt(p++);
//System.out.println(p);
                        cx = pt.getX();cy = pt.getY();
                        cx -= lx; cy -= ly;
                        line += " "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$

                        pt = (PointEntity)pe.points.elementAt(p++);
//System.out.println(p);
                        cx = pt.getX();cy = pt.getY();
                        cx -= lx; cy -= ly;
                        line += " "+doubleString(cx)+","+doubleString(cy); //$NON-NLS-1$ //$NON-NLS-2$
                        lx = pt.getX();ly = pt.getY();
                        break;
                      case SEG_CLOSE: 
                        line +="z"; //$NON-NLS-1$
                        break;
                  } // end switch
                  if(line.length()>75) {
                     prt.println(line);
                     line =" "; //$NON-NLS-1$
                  }
            } //more cmds
                prt.println(line);
                prt.println("\"/> "); //$NON-NLS-1$

	} // end encodePath


	/**
	 * encodeCircle
	 * &lt;circle cx="65" cy="245" r="2" />
	 */
	private static void encodeCircle(CircleEntity ce, PrintWriter prt) {
		prt.print("<circle"); //$NON-NLS-1$
		encodeStyle(ce, prt);
		prt.println(
			" cx=\"" + doubleString(ce.getX()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			" cy=\"" + doubleString(ce.getY()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			" r=\"" + doubleString(ce.getRadius()) + "\"" +  //$NON-NLS-1$ //$NON-NLS-2$
			"/>"); //$NON-NLS-1$
	}

	/**
	 * encodeLine
	 * format: &lt;line x1="325" y1="150" x2="375" y2="50" />
	 */
	private static void encodeLine(LineEntity le, PrintWriter prt) {
		prt.print("<line"); //$NON-NLS-1$
		encodeStyle(le, prt);
		prt.println(
			" x1=\"" + doubleString(le.getX1()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			" y1=\"" + doubleString(le.getY1()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			" x2=\"" + doubleString(le.getX2()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			" y2=\"" + doubleString(le.getY2()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			"/>"); //$NON-NLS-1$
	}

	/**
	 * encodePolyline
	 * &lt;polyline points="50,250,75,350,100,250,125,350,150,250,175,350" />
	 */
	private static void encodePolyLine(PolyLineEntity pe, PrintWriter prt) {
		if (pe.isClosed()) {
			prt.print("<polygon"); //$NON-NLS-1$
		}
		else {
			prt.print("<polyline"); //$NON-NLS-1$
		}
		encodeStyle(pe, prt);
		prt.print(" points=\""); //$NON-NLS-1$

		String line = ""; //$NON-NLS-1$
		PointEntity[] points = pe.getPoints();
		for (int i = 0; i < points.length; i++) {
				line += doubleString(points[i].getX())+","+doubleString(points[i].getY())+" "; //$NON-NLS-1$ //$NON-NLS-2$
			if (line.length() > 75) {
				prt.println(line);
				line=""; //$NON-NLS-1$
			}
		}
		prt.print(line);
		prt.println("\"/>"); //$NON-NLS-1$
	}

	/**
	 * encodeText
	 * &lt;text x="25" y="75" stroke="red">Magelan</text>
	 * encode the basic way no style(but stroke color) no child no ...etc
	 */
	private static void encodeText(TextEntity te, PrintWriter prt) {
		prt.print("<text"); //$NON-NLS-1$
		encodeStyle(te, prt);
		prt.print(
			" x=\"" + doubleString(te.getX()) + "\"" + //$NON-NLS-1$ //$NON-NLS-2$
			" y=\"" + doubleString(te.getY()) + "\""); //$NON-NLS-1$ //$NON-NLS-2$

		double siz = te.getSize();
		if (siz != 12) {
			prt.print(" font-size=\"" + doubleString(siz) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		double kx = te.getShearingX();
		double ky = te.getShearingY();
		double rot = te.getRotation();
		if (rot != 0 || kx != 0 || ky!=0) {
			prt.print(" transform=\""); //$NON-NLS-1$
			if (rot != 0)
				prt.print(" rotate("+doubleString(rot)+")"); //$NON-NLS-1$ //$NON-NLS-2$
			if (kx != 0)
				prt.print(" skewX("+doubleString(kx)+")"); //$NON-NLS-1$ //$NON-NLS-2$
			if (ky != 0)
				prt.print(" skewY("+doubleString(ky)+")"); //$NON-NLS-1$ //$NON-NLS-2$
			prt.print("\""); //$NON-NLS-1$
		}
		prt.print(">"); //$NON-NLS-1$
		prt.print(te.getText());
		prt.println("</text>"); //$NON-NLS-1$
	}

  /**
   * @method encodeStyle
   */
   private static void encodeStyle(Entity e, PrintWriter prt) {
		String line = ""; //$NON-NLS-1$
		
		// identifier
		String id = "" + ((DefaultEntity) e).getID(); //$NON-NLS-1$
		if (id != null) {
			line += " id=\""+id+"\""; //$NON-NLS-1$ //$NON-NLS-2$
		}

		String ln = e.getLayer().getName();
		if (ln.equals("0")) ln = NAME_0;
		
		// layer reference
		line += " class=\"" + ln + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		
		// stroke color
		Color color = e.getColor();
		if (color == null) {
			color = e.getLayer().getColor();	// repeat color, although defined by the layer
		}
		line += " stroke=\""+getColor(color)+"\""; //$NON-NLS-1$ //$NON-NLS-2$
		
		// text entity specifics:
		// has to override layer settings
		if (e instanceof TextEntity) {
			line +=" fill=\"" + getColor(color) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			line +=" stroke-width=\"0\""; //$NON-NLS-1$
			line +=" stroke-dasharray=\"\""; //$NON-NLS-1$
		}
		else {
			line += " fill=\"none\""; //$NON-NLS-1$
		
			// stroke-width
			float th = ((DefaultEntity) e).getThickness();
			if (th != 0) {
				line += " stroke-width=\""+doubleString((double)th)+"\""; //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			// stroke dasharray
			LineStyle ls = ((DefaultEntity) e).getLineStyle();
			if (ls != null) {
				float[] array = ((BasicStroke) ls.getStroke()).getDashArray();
				
				if (array != null) {
					String dash = dashArrayToSVG(array);
					line += " stroke-dasharray=\""+dash+"\""; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		prt.print(line);
   }

  /**
   *@method getColor
   *@return a string either color name or the hexadecimal representation
   *
   * @todo
   * 	Use the utility method with the same function.
   */

   private static String getColor(Color color) {
     String col = ""; //$NON-NLS-1$
     if(color.equals(Color.white)) {
     	if (whiteToBlack)
     		col ="black"; //$NON-NLS-1$
     	else
     		col ="white"; //$NON-NLS-1$
     }
     else if(color.equals(Color.lightGray)) col ="lightGray"; //$NON-NLS-1$
     else if(color.equals(Color.gray)) col ="gray"; //$NON-NLS-1$
     else if(color.equals(Color.darkGray)) col ="darkGray"; //$NON-NLS-1$
     else if(color.equals(Color.black)) col ="black"; //$NON-NLS-1$
     else if(color.equals(Color.red)) col ="red"; //$NON-NLS-1$
     else if(color.equals(Color.pink)) col ="pink"; //$NON-NLS-1$
     else if(color.equals(Color.orange)) col ="orange"; //$NON-NLS-1$
     else if(color.equals(Color.yellow)) col ="yellow"; //$NON-NLS-1$
     else if(color.equals(Color.green)) col ="green"; //$NON-NLS-1$
     else if(color.equals(Color.magenta)) col ="magenta"; //$NON-NLS-1$
     else if(color.equals(Color.cyan)) col ="cyan"; //$NON-NLS-1$
     else if(color.equals(Color.blue)) col ="blue"; //$NON-NLS-1$
     else {
     col = "#"; //$NON-NLS-1$
     col += addColor(Integer.toHexString(color.getRed()));
     col += addColor(Integer.toHexString(color.getGreen()));
     col += addColor(Integer.toHexString(color.getBlue()));
     }
     return col;

   }// end getColor

   private static String addColor(String str) {
     String str1 = str.toUpperCase();
     if(str.length() == 1) str = "0"+str1; //$NON-NLS-1$
     return str;
   }

    /**
     * @method dashArrayToSVG
     *
     * @param dashArray float array to convert to a string
     */

    private static String dashArrayToSVG(float dashArray[]){
        StringBuffer dashArrayBuf = new StringBuffer();
        if (dashArray.length > 0)
            dashArrayBuf.append(doubleString(dashArray[0]));

        for(int i=1; i<dashArray.length; i++){
            dashArrayBuf.append(" "); //$NON-NLS-1$
            dashArrayBuf.append(doubleString(dashArray[i]));
        }

        return dashArrayBuf.toString();
    }

    /**
     * @method joinToSVG
     * @param lineJoin join style
     */

    private static String joinToSVG(int lineJoin){
        switch(lineJoin){
        case BasicStroke.JOIN_BEVEL:
            return "bevel"; //$NON-NLS-1$
        case BasicStroke.JOIN_ROUND:
            return "round"; //$NON-NLS-1$
        case BasicStroke.JOIN_MITER:
        default:
            return "miter"; //$NON-NLS-1$
        }
    }

    /**
     * @method endCapToSVG
     *
     * @param endCap cap style
     */

    private static String endCapToSVG(int endCap){
        switch(endCap){
        case BasicStroke.CAP_BUTT:
            return "butt"; //$NON-NLS-1$
        case BasicStroke.CAP_ROUND:
            return "round"; //$NON-NLS-1$
        default:
        case BasicStroke.CAP_SQUARE:
            return "square"; //$NON-NLS-1$
        }
    }

    /**
     * @method doubleString
     *
     * @return the double value formated as an int if there
     *         is no fractional part. This avoids the extra
     *         ".0" that a standard convertion gives.
     */
	public static String doubleString(double value){
		if(((int)value) == value)
			return Integer.toString((int)value);
		else
			return Double.toString(value);
	}
}
