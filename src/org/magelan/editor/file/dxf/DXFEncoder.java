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
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.magelan.core.DefaultEntity;
import org.magelan.core.Entity;
import org.magelan.core.ExplodableEntity;
import org.magelan.core.entity.*;
import org.magelan.core.style.Layer;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.DrawingRenderer;

/**
 * An encoder to write <code>CoreModel</code> instances to DXF file.
 * Custom entities and entities that do not have a DXF equivalent 
 * will be exploded and an attempt will be made to encode the 
 * resulting entities.
 *
 * @version 1.1, 09/2006
 * @author  Assen Antov
 */
public class DXFEncoder implements DXFConst {
	
	public static void encode(DrawingModel dwg, DrawingRenderer renderer, OutputStream out) throws IOException {
		DXFOutputStream dxf = new DXFOutputStream(new BufferedOutputStream(out, 1048576));
		
		// initialise the stream
		dxf.code(999, "magelan.editor.file.dxf.DXFEncoder"); //$NON-NLS-1$
		dxf.code(999, IMPL_VERSION);
		dxf.code(999, COPYRIGHT);
		
		// open entities section
		dxf.code(0, "SECTION"); //$NON-NLS-1$
		dxf.code(2, "ENTITIES"); //$NON-NLS-1$
		
		// iterate the entities
		Iterator iter = dwg.getElements().iterator();
		while (iter.hasNext()) {
			encodeEntity((Entity) iter.next(), renderer, dxf);
		}
		
		// close the section
		dxf.code(0, "ENDSEC"); //$NON-NLS-1$
		
		// close the file
		dxf.code(0, "EOF"); //$NON-NLS-1$
		dxf.close();
	}
	
	/**
	 * Encodes an entity to a DXFOutputStream.
	 * <p>
	 * @param e   entity to encode
	 * @param dxf stream to write the entity to
	 */
	public static void encodeEntity(Entity e, DrawingRenderer renderer, DXFOutputStream dxf) throws IOException {
		
		Layer l = e.getLayer();
		if (l != null) {
			if (l.isFrozen() || !l.isVisible()) return;
		}
		
		// LINE entity
		if (e instanceof LineEntity) {
			dxf.code(0, "LINE");    // type //$NON-NLS-1$
			encodeCommon(e, dxf);
			dxf.code(10, ((LineEntity) e).getX1());     // x1 coord
			dxf.code(20, ((LineEntity) e).getY1());     // y1 coord
			dxf.code(30, 0);        // z1 coord
			dxf.code(11, ((LineEntity) e).getX2());     // x2 coord
			dxf.code(21, ((LineEntity) e).getY2());     // y2 coord
			dxf.code(31, 0);        // z2 coord
			
			float th = ((DefaultEntity) e).getThickness();
			if (th != 0) dxf.code(39, th); // thickness
		}
		
		// CIRCLE entity
		else if (e instanceof CircleEntity) {
			dxf.code(0, "CIRCLE"); //$NON-NLS-1$
			encodeCommon(e, dxf);
			dxf.code(10, ((CircleEntity) e).getX());
			dxf.code(20, ((CircleEntity) e).getY());
			dxf.code(30, 0);
			dxf.code(40, ((CircleEntity) e).getRadius());
			
			float th = ((DefaultEntity) e).getThickness();
			if (th != 0) dxf.code(39, th); // thickness
		}
		
		// TEXT entity
		else if (e instanceof TextEntity) {
			dxf.code(0, "TEXT"); //$NON-NLS-1$
			encodeCommon(e, dxf);
			
			dxf.code(10, ((TextEntity) e).getX());     // x coord
			dxf.code(20, ((TextEntity) e).getY());     // y coord
			dxf.code(30, 0);        // z coord
			
			float th = ((DefaultEntity) e).getThickness();
			if (th != 0) dxf.code(39, th); // thickness
			
			dxf.code(40, ((TextEntity) e).getSize());  // height
			dxf.code(1, ((TextEntity) e).getText());     // the string itself
			dxf.code(50, -((TextEntity) e).getRotation());      // rotation
			//dxf.code(41, 1);      // width scale rel to X
			//dxf.code(51, 0);      // oblique
			//dxf.code(7, "STANDARD");  // text style name
			//dxf.code(71, 0);      // backwards or upside down
			//dxf.code(72, 0);      // horizontal justification
			//dxf.code(73, 0);      // vertical justification
		}
		
		else if (e instanceof LabelEntity) {
			dxf.code(0, "TEXT"); //$NON-NLS-1$
			encodeCommon(e, dxf);
			
			dxf.code(10, ((TextEntity) e).getX());     // x coord
			dxf.code(20, ((TextEntity) e).getY());     // y coord
			dxf.code(30, 0);        // z coord
			
			float th = ((DefaultEntity) e).getThickness();
			if (th != 0) dxf.code(39, th); // thickness
			
			dxf.code(40, ((TextEntity) e).getSize());  // height
			dxf.code(1, ((TextEntity) e).getText());     // the string itself
			dxf.code(50, -((LabelEntity) e).getAlignedAngle());      // rotation
			//dxf.code(41, 1);      // width scale rel to X
			//dxf.code(51, 0);      // oblique
			//dxf.code(7, "STANDARD");  // text style name
			//dxf.code(71, 0);      // backwards or upside down
			//dxf.code(72, 0);      // horizontal justification
			//dxf.code(73, 0);      // vertical justification
		}
		
		// PLINE entity
		else if (e instanceof PolyLineEntity) {
			dxf.code(0, "POLYLINE"); //$NON-NLS-1$
			encodeCommon(e, dxf);
			
			dxf.code(66, 1);
			dxf.code(10, 0);
			dxf.code(20, 0);
			dxf.code(30, 0);
			dxf.code(70, ((PolyLineEntity) e).isClosed()? 1 : 0);    // closed or not (1 or 0)
			
			PointEntity[] points = ((PolyLineEntity) e).getPoints();
			for (int i = 0; i < points.length; i++) {
				dxf.code(0, "VERTEX"); //$NON-NLS-1$
				dxf.code(10, points[i].getX());
				dxf.code(20, points[i].getY());
				dxf.code(30, 0);
				dxf.code(8, l == null? "0" : l.getName());  // layer name
			}
			
			dxf.code(0, "SEQEND"); //$NON-NLS-1$
		}
		
		/*
		 // SOLID entity - a filled rectangle
		  else if (e instance of LineEntity) {
		  int[] xPoints = new int[] { x, x + width, x + width, x };
		  int[] yPoints = new int[] { y, y, y+height, y+height };
		  
		  dxf.code(0, "SOLID");
		  dxf.codeCommon(e, dxf);
		  
		  dxf.code(10, this.x + x);
		  dxf.code(20, this.y + y);
		  dxf.code(30, 0);
		  
		  dxf.code(11, this.x + x+width);
		  dxf.code(21, this.y + y);
		  dxf.code(31, 0);
		  
		  dxf.code(12, this.x + x);
		  dxf.code(22, this.y + y+height);
		  dxf.code(32, 0);
		  
		  dxf.code(13, this.x + x+width);
		  dxf.code(23, this.y + y+height);
		  dxf.code(33, 0);
		  }
		  */
		
		/*
		 // ARC entity
		  else if (e instance of Arc) {
		  dxf.code(0, "ARC");
		  dxf.codeCommon();
		  
		  dxf.code(10, this.x + x + width/2);
		  dxf.code(20, this.y + y + height/2);
		  dxf.code(30, 0);
		  dxf.code(40, width/2);
		  dxf.code(50, startAngle);
		  dxf.code(51, startAngle+arcAngle);
		  }
		  */
		
		// HATCH entity
		/*
		else if (e instanceof HatchEntity) {
			dxf.code(0, "HATCH");
			encodeCommon(e, dxf);
			//dxf.code(70, 1); // solid fill
			//code(71, 1); // associative

			//Read the coordinates of the referred shape
			double[] x = new double[50];
			double[] y = new double[50];
			
			Shape shape = ((HatchEntity) e).getShape();
			
			// get the path iterator and iterate
			PathIterator iter = shape.getPathIterator(null, 1);
			double[] coords = new double[6];
			int i = 0;
			while (!iter.isDone()) {
				int seg = iter.currentSegment(coords);
				
				if (seg == PathIterator.SEG_CLOSE) {
					x[i] = x[0];
					y[i] = y[0];
					i++;
				}
				// all other segment types interpret as moveto
				else {
					if (i == x.length) {
						x = grow(x, 50);
						y = grow(y, 50);
					}
					
					x[i] = coords[0];
					y[i] = coords[1];
					i++;
				}
				
				iter.next();
			}
			
			x = shrink(x, i);
			y = shrink(y, i);
			
			
			//dxf.code(91, x.length); // number of boundary loops
			dxf.code(92, 2); // boundary type polyline
			
			// --> polyline
			dxf.code(72, 0); // no bulge
			dxf.code(73, 0); // is closed?
			dxf.code(93, x.length); // number of vertices
			
			for (i = 0; i < x.length; i++) {

				dxf.code(10, x[i]);
				dxf.code(20, y[i]);
			}

			// could be done by referencing the previous polygon
			// this is the better alternative, since the hatch will
			// be associative
			//dxf.code(97, 1); // 1 referenced boundary object
			//dxf.code(330, ""); // handle number of the polygon
		}
		
		*/
		
		/*
		 * Try to explode unrecognized entities and encode the resulting basic
		 * entities.
		 */
		else if (e instanceof ExplodableEntity) {
			Iterator iter = ((ExplodableEntity) e).explode();
			while (iter.hasNext()) {
				encodeEntity((Entity) iter.next(), renderer, dxf);
			}
		}
		
		/*
		 * For all entities that are not recognized and are not explodeable try
		 * to create a DXF entities from their 'as-rendered' shapes.
		 */
		else {
			Shape shape = renderer.getShape(e);
			if (shape != null) encodeShape(e, shape, renderer, dxf);
			
			/*
			 * Try to encode link volumes. Done using reflection to retain the
			 * independence of the editor from the transport modelling library.
			 */
			if (e.getClass().getName().equals("org.magelan.transport.entity.ModelLink") &&
				renderer.getClass().getName().equals("org.magelan.transport.editor.NetworkRenderer")) {
				
				try {
					Method m = renderer.getClass().getMethod("getVolumeShape", new Class[] { Object.class });
					Shape sh = (Shape) m.invoke(renderer, new Object[] { e });
					if (sh != null) encodeShape(e, sh, renderer, dxf);
				} catch (Throwable thr) {}
			}
		}
	}
	
	public static void encodeShape(Entity e, Shape shape, DrawingRenderer renderer, DXFOutputStream dxf) throws IOException {
		// start poly line declaration
		dxf.code(0, "POLYLINE"); //$NON-NLS-1$
		encodeCommon(e, dxf);
		
		dxf.code(66, 1);
		dxf.code(10, 0);
		dxf.code(20, 0);
		dxf.code(30, 0);
		//dxf.code(70, ((PolyLineEntity) e).isClosed()? 1 : 0);    // closed or not (1 or 0)
		dxf.code(70, 0);    // closed or not (1 or 0)
		
		double fx = 0, fy = 0;
		Layer l = ((Entity) e).getLayer();
		
		// get the path iterator
		PathIterator iter = shape.getPathIterator(null, 1);
		double[] coords = new double[6];
			
		int i = 0;
		while (!iter.isDone()) {
			int seg = iter.currentSegment(coords);
			
			if (seg == PathIterator.SEG_CLOSE) {
				dxf.code(0, "VERTEX"); //$NON-NLS-1$
				dxf.code(10, fx);
				dxf.code(20, fy);
				dxf.code(30, 0);
				dxf.code(8, l == null? "0" : l.getName());  // layer name
			}
			else {
				if (i == 0) {
					fx = coords[0];
					fy = coords[1];
				}
				dxf.code(0, "VERTEX"); //$NON-NLS-1$
				dxf.code(10, coords[0]);
				dxf.code(20, coords[1]);
				dxf.code(30, 0);
				dxf.code(8, l == null? "0" : l.getName());  // layer name
				i++;
			}
				
			iter.next();
		}
		
		dxf.code(0, "SEQEND"); //$NON-NLS-1$
	}
	
	/**
	 * Encodes codes common for all entities to a DXFOutputStream.
	 * <p>
	 * @param e   entity to encode
	 * @param dxf stream to write the entity to
	 */
	public static void encodeCommon(Entity e, DXFOutputStream dxf) throws IOException {
		//dxf.code(5, ); // handle number
		dxf.code(6, BYLAYER);       // linetype
		
		Layer l = e.getLayer();
		dxf.code(8, l == null? "0" : l.getName());  // layer name
		
		// determine entity color
		Color color = e.getColor();
		if (color == null) { /*code(62, COLOR_BYLAYER);*/ }
		else
			dxf.code(62, dxf.toDXFColor(color));
		
		// these may be omitted
		//dxf.code(67, 0);        // model space
		//dxf.code(370, 1);       // lineweight (16-bit integer)
		//dxf.code(48, 1.0);      // linetype scale
		//dxf.code(60, 0);        // visible
	}
	
	/**
	 * Creates a new array with more elements and copies the content
	 * of the source array in it. 
	 *  
	 * @param a source array
	 * @param more number of additional elements
	 * @return an expanded array
	 */
	private static double[] grow(double[] a, int more) {
		double[] res = new double[a.length + more];
		System.arraycopy(a, 0, res, 0, a.length);
		return res;
	}

	/**
	 * Shrinks the parameter array to the desired size.
	 * 
	 * @param a array to shrink
	 * @param size size of the new array
	 * @return a shrinked array
	 */
	private static double[] shrink(double[] a, int size) {
		double[] res = new double[size];
		System.arraycopy(a, 0, res, 0, size);
		return res;
	}
}
