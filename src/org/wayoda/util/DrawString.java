/* This File is part of the wayoda-project                                    */
/* Filename : DrawString.java                                                 */
/* Copyright (C) <2002>  <Eberhard Fahle>
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA  */

package org.wayoda.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 * The DrawString class is a container for a String and its desired rendering location(x,y)
 * and dimension(w,h). 
 * <p>
 * <strong> This class is nothing more than a hack yet ! </strong>
 * <p>
 * This class is only the abstract superclass for all objects that store a DrawString. 
 * The actual storage representation of the coordinates is left to the subclass. 
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public abstract class DrawString {
		/** 
		 * Constant that decribes how setting the y-coordiante of the text is to be interpreted:
		 * The y-coordinate is to be interpreted as the baseline of the Font.
		 */
		public static final int VERTICAL_BASELINE=0;
		/** 
		 * Constant that decribes how setting the y-coordiante of the text is to be interpreted:
		 * The y-coordinate is to be interpreted as the top of the Font (leading and ascent need
		 * to be added to locate the baseline at the desired position).
		 */
		public static final int VERTICAL_TOP=1;
		/** 
		 * Constant that decribes how setting the y-coordiante of the text is to be interpreted:
		 * The y-coordinate is to be interpreted as the top of the Font exluding the leading (ascent needs
		 * to be added to locate the baseline at the desired position).
		 */
		public static final int VERTICAL_ASCENT=2;
		/** 
		 * Constant that decribes how setting the y-coordiante of the text is to be interpreted:
		 * The y-coordinate is to be interpreted as the center of the String (We need to add 
		 * <code>ascent-(height/2)</code> to locate the baseline at the desired position).
		 */
		public static final int VERTICAL_CENTER=3;
		/** 
		 * Constant that decribes how setting the x-coordiante of the text is to be interpreted:
		 * The x-coordinate is to be interpreted as the position where the text starts.
		 */
		public static final int HORIZONTAL_LEFT=4;
		/** 
		 * Constant that decribes how setting the x-coordiante of the text is to be interpreted:
		 * The x-coordinate is to be interpreted as the position where the text ends.
		 */
		public static final int HORIZONTAL_RIGHT=5;
		/** 
		 * Constant that decribes how setting the x-coordiante of the text is to be interpreted:
		 * The x-coordinate is to be interpreted as the center of the text.
		 */
		public static final int HORIZONTAL_CENTER=6;

		/** The text that is to be rendered */
		public String text;
		
		/** 
		 * Creates a new DrawString.
		 * @param text the String to be rendered. If text is null we store the 
		 * empty String here, So at least we don't get a NullPointerException
		 * when this text is drawn.
		 */
		public DrawString(String text) {
				if(text==null)
						text=""; //$NON-NLS-1$
				this.text=text;
		}

		/**
		 * Caluculates the width and height of the rendered String based on the 
		 * supplied FontMetrics. 
		 * @param fm the FontMetrics-object that is to be used for calculating
		 * the width and height of the text when rendered.
		 * @throws IllegalArgumentException if fm==null
		 */
		public abstract void calcSize(FontMetrics fm);

		/**
		 * Caluculates the width and height of the rendered String based on the 
		 * supplied Font and a FontRenderContext. 
		 * @param f the Font to be used when calculating the width and height
		 * of the text when rendered.
		 * @param frc the FontRenderContext in which rendering is done.
		 * @throws IllegalArgumentException if f==null or frc==null
		 */
		public abstract void calcSize(Font f, FontRenderContext frc);

		/**
		 * Sets the loaction of the rendered TextEntity.
		 * @param x the x-coordinate of the text when rendered
		 * @param xalign A constant that defines how that x-coordinate is to be interpreted<br>
		 * This is one of the constants:<br><code>
		 * VERTICAL_BASELINE, VERTICAL_TOP, VERTICAL_ASCENT, VERTICAL_CENTER</code><br>
		 * @param y the y-coordinate of the text when rendered
		 * @param yalign A constant that defines how that x-coordinate is to be interpreted<br>
		 * This is one of the constants:<br><code>
		 * HORIZONTAL_LEFT, HORIZONTAL_RIGHT, HORIZONTAL_CENTER</code>
		 * @throws IllegalArgumentException if xalign is not one of the constants
		 * defined for horizontal alignment.
		 * @throws IllegalArgumentException if yalign is not one of the constants
		 * defined for vertical alignment.
		 * @throws IllegalStateException if the metrics of the font have not been 
		 * determined yet. (i.e. calcSize has not been called before).
		 */
		public abstract void setLocation(int x, int xalign, int y, int yalign);		

		/********************************************************************************/
		/* Nested Classes from here on                                                  */
		/********************************************************************************/

		/**
		 * An implementation of DrawString that uses int-values for the loacation,
		 * with and height.
		 */
		public static class Int extends DrawString {
				/** The x-coordinate for the rendered text */
				public int x; 
				/** The y-coordinate for the rendered text */
				public int y; 
				/** The width of the rendered text */
				public int width=-1; 
				/** The height of the rendered text THIS IS THE BOUNDING BOX NOT THE HEIGHT OF THE FONT */
				public int height=-1;
				/** This is the height thats calculated as ascent+descent+leading */
				public int fontHeight=-1;
				/** The ascent of the font on which the size was calculated */
				public int ascent=-1;
				/** The descent of the font on which the size was calculated */
				public int descent=-1;
				/** The leading of the font on which the size was calculated */
				public int leading=-1;

						
				/** 
				 * Creates a new DrawString.Int.
				 * @param text the String to be rendered. If text is null we store the 
				 * empty String here, So at least we don't get a NullPointerException
				 * when this text is drawn.
				 */
				public Int(String text) {
						super(text);
				}
				
				/**
				 * Caluculates the width and height of the rendered String based on the 
				 * supplied FontMetrics. 
				 * @param fm the FontMetrics-object that is to be used for calculating
				 * the width and height of the text when rendered.
				 * @throws IllegalArgumentException if fm==null
				 */
				public void calcSize(FontMetrics fm) {
						if(fm==null)
								throw new IllegalArgumentException("FontMetrics is null"); //$NON-NLS-1$
						width=fm.stringWidth(text);
						height=fm.getHeight();
						fontHeight=height;	//The same for fontmetrics
						ascent=fm.getAscent();
						descent=fm.getDescent();
						leading=fm.getLeading();
				}

				/**
				 * Caluculates the width and height of the rendered String based on the 
				 * supplied Font and a FontRenderContext. As a DrawString.Int works on
				 * int locations and sizes, and LineMetrics and StringBounds in this class
				 * return floats, every value frcational value returned from the getStringBounds()
				 * and LineMetrics is rounded by Math.ceil(). 
				 * @param f the Font to be used when calculating the width and height
				 * of the text when rendered.
				 * @param frc the FontRenderContext in which rendering is done.
				 * @throws IllegalArgumentException if f==null or frc==null
				 */
				public void calcSize(Font f, FontRenderContext frc) {
						if(f==null || frc==null)
								throw new IllegalArgumentException("Font or FontRenderContext null"); //$NON-NLS-1$
						TextLayout tl=new TextLayout(text,f,frc);
						Rectangle2D.Float r=(Rectangle2D.Float)tl.getBounds();
						width=(int)Math.ceil(r.width);
						height=(int)Math.ceil(r.height);
						ascent=(int)Math.ceil(tl.getAscent());
						descent=(int)Math.ceil(tl.getDescent());
						leading=(int)Math.ceil(tl.getLeading());
						fontHeight=ascent+descent+leading; //different from the bounds returned by TextLayout
				}

				/**
				 * Sets the loaction of the rendered TextEntity.
				 * @param x the x-coordinate of the text when rendered
				 * @param xalign A constant that defines how that x-coordinate is to be interpreted<br>
				 * This is one of the constants:<br><code>
				 * VERTICAL_BASELINE, VERTICAL_TOP, VERTICAL_ASCENT, VERTICAL_CENTER</code><br>
				 * @param y the y-coordinate of the text when rendered
				 * @param yalign A constant that defines how that x-coordinate is to be interpreted<br>
				 * This is one of the constants:<br><code>
				 * HORIZONTAL_LEFT, HORIZONTAL_RIGHT, HORIZONTAL_CENTER</code>
				 * @throws IllegalArgumentException if xalign is not one of the constants
				 * defined for horizontal alignment.
				 * @throws IllegalArgumentException if yalign is not one of the constants
				 * defined for vertical alignment.
				 * @throws IllegalStateException if the metrics of the font have not been 
				 * determined yet. (i.e. calcSize has not been called before).
				 */
				public void setLocation(int x, int xalign, int y, int yalign) {
						if(height==-1)
								throw new IllegalStateException("FontMetrics not set"+text); //$NON-NLS-1$
						if(!(xalign==HORIZONTAL_LEFT
								 || xalign==HORIZONTAL_RIGHT
								 || xalign==HORIZONTAL_CENTER))
								throw new IllegalArgumentException("Unknown Constant for horizontal alignment"); //$NON-NLS-1$
						if(!(yalign==VERTICAL_BASELINE
								 || yalign==VERTICAL_TOP
								 || yalign==VERTICAL_ASCENT
								 || yalign==VERTICAL_CENTER))
								throw new IllegalArgumentException("Unknown Constant for vertical alignment"); //$NON-NLS-1$
						if(xalign==HORIZONTAL_LEFT)
								this.x=x;
 						else if(xalign==HORIZONTAL_RIGHT)
								this.x=x-width;
 						else if(xalign==HORIZONTAL_CENTER)
								this.x=x-width/2;
						if(yalign==VERTICAL_BASELINE)
								this.y=y;
 						else if(yalign==VERTICAL_TOP) {
								if(height<ascent+descent)	{
										//not sure where the baseline is so we paint the bounding box
										this.y=y+height+leading;
								}
								else {
										//the height matches ascent+decent use the baseline
										this.y=+ascent+leading;
								}
						}
 						else if(yalign==VERTICAL_ASCENT) {
								if(height<ascent+descent)	{
										//not sure where the baseline is so we paint the bounding box
										this.y=y+height;
								}
								else {
										//the height matches ascent+decent use the baseline
										this.y=+ascent;
								}
						}
 						else if(yalign==VERTICAL_CENTER) {
								if(height<ascent+descent)	{
										//not sure where the baseline is so we paint the bounding box
										this.y=y+height-(height/2);
								}
								else {
										//the height matches ascent+decent use the baseline
										this.y=y+ascent-((ascent+descent)/2);
								}
						}
				}

		}
}				
				
