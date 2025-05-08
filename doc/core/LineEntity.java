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
package magelan.core.entity;

import java.awt.*;
import java.awt.geom.*;

import org.magelan.core.EntityModifier;

import magelan.core.*;

/**
 * A demo line entity. Almost the same as the actual 
 * <code>LineEntity</code> but a bit shortened.
 * 
 * @author Assen Antov
 * @version demo 1.2, 10/2004
 */
public class LineEntity extends DefaultEntity {
	
	/*
	 * Coordinates of the line points.
	 */
	private double x1, y1, x2, y2;
	
	/** Array of all entity modifiers. */
	private EntityModifier[] modifiers;
	
	
	/**
	 * Creates a new LineEntity object.
	 */
	public LineEntity() {
		this(0, 0, 1, 1, null);
	}
	
	/**
	 * Creates a new LineEntity object.
	 * 
	 * @param x1 X-coordinate of the first point
	 * @param y1 Y-coordinate of the first point
	 * @param x2 X-coordinate of the second point
	 * @param y2 Y-coordinate of the second point
	 */
	public LineEntity(double x1, double y1, double x2, double y2) {
		this(x1, y1, x2, y2, null);
	}
	
	/**
	 * Returns a shape. Note that for efficiency with single-threaded implementations 
	 * this could also be a flyweight (shared) shape instance.
	 * 
	 * @return  a shape, representing the entity
	 */
	public Shape getShape() {
		return new Line2D.Double(x1, y1, x2, y2);
	}
	
	public EntityModifier[] getModifiers() {
		if (modifiers == null) {
			modifiers = new EntityModifier[] {
				/*
				 * First point modifier.
				 */
				new EntityModifier() {
					public void modify(double x, double y) {
						LineEntity.this.x1 = x;
						LineEntity.this.y1 = y;
					}
					public Entity getEntity() { return LineEntity.this; }
					public double getX() { return LineEntity.this.x1; }
					public double getY() { return LineEntity.this.y1; }
					public int getSnapPointType() { return ENDPOINT; }
					public int getSnapPointFunction() { return STRETCH; }
				},
				
				/*
				 * Middle point modifier.
				 */
				new EntityModifier() {
					public void modify(double x, double y) {
						double xx = getX();
						double yy = getY();
						LineEntity.this.x1 += x - xx;
						LineEntity.this.y1 += y - yy;
						LineEntity.this.x2 += x - xx;
						LineEntity.this.y2 += y - yy;
					}
					public Entity getEntity() { return LineEntity.this; }
					public double getX() {
						return (LineEntity.this.x1 + LineEntity.this.x2) / 2;
					}
					public double getY() {
						return (LineEntity.this.y1 + LineEntity.this.y2) / 2;
					}
					public int getSnapPointType() { return MIDPOINT; }
					public int getSnapPointFunction() { return MOVE; }
				},
				
				/*
				 * Second point modifier.
				 */
				new EntityModifier() {
						public void modify(double x, double y) {
							LineEntity.this.x2 = x;
							LineEntity.this.y2 = y;
						}
						public Entity getEntity() { return LineEntity.this; }
						public double getX() { return LineEntity.this.x2; }
						public double getY() { return LineEntity.this.y2; }
						public int getSnapPointType() { return ENDPOINT; }
						public int getSnapPointFunction() { return STRETCH; }
					}
			};
		}
		
		return modifiers; 
	}
	
	/**
	 * Paints the line. Note that as the graphics context is
	 * fully configured with the proper color, transform, linestyle,
	 * etc. all the entity actually needs to do is paint its shape.
	 *
	 * @param g graphics context to paint to 
	 * @param d drawing to retreive rendering information from
	 */
	public void paint(Graphics2D g, DrawingModel d) {
		g.draw(getShape());
	}
	
	public Object clone() {
		EntityModifier[] m = modifiers;
		modifiers = null;	// modifiers should not be cloned!
		Object cl = super.clone();
		modifiers = m;
		return cl;
	}

	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			LineEntity e = (LineEntity) obj;
			if (x1 != e.getX1() || y1 != e.getY1() || 
				x2 != e.getX2() || y2 != e.getY2()) {
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	public double getX1() { return this.x1; }
	
	public void setX1(double x1) {
		firePropertyChange("x1", new Double(this.x1), new Double(x1));
		this.x1 = x1;
	}
	
	public double getY1() { return this.y1; }
	
	public void setY1(double y1) {
		firePropertyChange("y1", new Double(this.y1), new Double(y1));
		this.y1 = y1;
	}
	
	public double getX2() { return this.x2; }
	
	public void setX2(double x2) {
		firePropertyChange("x2", new Double(this.x2), new Double(x2));
		this.x2 = x2;
	}
	
	public double getY2() { return this.y2; }
	
	public void setY2(double y2) {
		firePropertyChange("y2", new Double(this.y2), new Double(y2));
		this.y2 = y2;
	}
}