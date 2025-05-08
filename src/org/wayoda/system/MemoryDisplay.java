/* This File is part of the wayoda-project                                    */
/* Filename : MemoryDisplay.java                                              */
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

package org.wayoda.system;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;
import javax.swing.*;
import javax.swing.border.*;

import org.wayoda.util.DrawString;
import org.wayoda.util.Screen;

import org.wayoda.value.ArrayQueue;

/**
 * Displays the current memory usage of the JVM in a grid and numerical.
 * <p>
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemoryDisplay extends JComponent implements MemoryValueListener {
		/** 
		 * Constant for the way the values are painted : Draw a single lines from 
		 * one value to the next.
		 */
		public static final int DRAW_LINE=0;
		/** 
		 * Constant for the way the values are painted : Draw filled regions from
		 * the bottom of the display to the values.
		 */
		public static final int DRAW_FILLED=1;
		/** The drawing-style of the comonent */
		private int drawStyle=DRAW_FILLED;
		/** The Stroke to be used for the DRAW_LINE style */
		private BasicStroke lbs=new BasicStroke(1.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
		/** The Stroke to be used for the DRAW_Filled style */
		private BasicStroke fbs=new BasicStroke(1.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER);
		/** The Stroke to be used for the grid */
		private BasicStroke gbs=new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
		/** The RenderingHints for this component (we want anitaliasing=on) */
		private RenderingHints rhints;
		/** The color for clearing the background */
		private Color bgColor=new Color(0xFF,0xFF,0xCC);
		/** The color for the grid */
		private Color gridColor=new Color(0x66,0x66,0x66);
		/** The color for the used memory */
		private Color usedColor=new Color(0xFF,0xFF,0x00,0xBB);
		/** The color for the free memory */
		private Color freeColor=new Color(0x00,0xFF,0x00,0xBB);
 		/** The color for the total memory */
		private Color totalColor=new Color(0x00,0x00,0xAA,0xBB);
		/** The color for text */
		private Color textColor=Color.black;
		/** The color for values */
		private Color valueColor=Color.blue;
		/** The Queue containing the y2 coordinates that reflect the used memory usage */
		private ArrayQueue.Float usedQ;
		/** The Queue containing the y2 coordinates that reflect the free memory */
		private ArrayQueue.Float freeQ;
		/** The Queue containing the y2 coordinates that reflect the total memory usage */
		private ArrayQueue.Float totalQ;
		/** The current number of bytes that is the maximum displayed in the grid (start with 4MB)*/
		private long maxGridMemory=0x400000;
		/** The time in milliseconds the last update was received , -1 because we need a first value on this */
		private long lastUpdateTime=-1;
		
		/** The offset from the gridvalue labels to the grid */
		private int gridXOffset;
		/** The vertical gridline distance */
		private float gridX=16.0f;
		/** The vertical gridline distance */
		private float gridY=8.0f;
		/** the width of the display in pixel */
		private float displayWidth=256;
		/** the height of the display in pixel */
		private float displayHeight=64;
		
		/** An all purpose Line2D.Float we use for drawing */
		private Line2D.Float line=new Line2D.Float();

		/** Some margins we want around our component */
		private Insets margins=new Insets(4,4,4,4);
		
		/** The label for the explaining the total color */
		private DrawString.Int totalColorLabel;
		/** The label for the explaining the used color */
		private DrawString.Int usedColorLabel;
		/** The label for the explaining the free color */
		private DrawString.Int freeColorLabel;
		/** The label for the delay between updates */
		private DrawString.Int delayLabel;
		/** The value for the delay between updates */
		private DrawString.Int delayValue=new DrawString.Int("--.--- s"); //$NON-NLS-1$

		/** The label for the value for the maximum memory the JVM will use */
		private DrawString.Int jvmLabel;
		/** The label for the value for the allocated object-memory */
		private DrawString.Int totalLabel;
		/** The label for the value for the object-memory already used */
		private DrawString.Int usedLabel;
		/** The label for the value for the object-memory not used yet*/
		private DrawString.Int freeLabel;
		/** The DrawString.Int that contains the value for the maximum memory the JVM will use */
		private DrawString.Int jvmValue=new DrawString.Int("?????.? MB"); //$NON-NLS-1$
		/** The DrawString.Int that contains the value for the allocated object-memory */
		private DrawString.Int totalValue=new DrawString.Int("?????.? MB"); //$NON-NLS-1$
		/** The DrawString.Int that contains the value for the object-memory already used */
		private DrawString.Int usedValue=new DrawString.Int("?????.? MB"); //$NON-NLS-1$
		/** The DrawString.Int that contains the value for the object-memory not used yet*/
		private DrawString.Int freeValue=new DrawString.Int("?????.? MB"); //$NON-NLS-1$

		/** The label to the left of the Display that shows the maximum value in the grid */
		private DrawString.Int maxGridValue;
		/** The label to the left of the Display that shows the medium value in the grid */
		private DrawString.Int mediumGridValue;
		/** The label to the left of the Display that shows the minium value in the grid */
		private DrawString.Int minGridValue;

		/** The rectangle we paint for our color information total graph */
		private Rectangle totalColorInfo=new Rectangle(12,12);
		/** The rectangle we paint for our color information used graph */
		private Rectangle usedColorInfo=new Rectangle(12,12);
		/** The rectangle we paint for our color information free graph */
		private Rectangle freeColorInfo=new Rectangle(12,12);
		/** The line we paint below the display and color info to seperate it from values */
		private Line2D.Float sepline=new Line2D.Float();

		private FontRenderContext frc=Screen.getDefaultFontRenderContext();

		/**
		 * Creates a new MemoryDisplay
		 */
		public MemoryDisplay() {
				//i18n of the display
				try {
						ResourceBundle rb=ResourceBundle.getBundle("org.wayoda.system.MemoryMonitorResources"); //$NON-NLS-1$
						jvmLabel=new DrawString.Int(rb.getString("memorydisplay.label.jvm")); //$NON-NLS-1$
						totalLabel=new DrawString.Int(rb.getString("memorydisplay.label.total")); //$NON-NLS-1$
						usedLabel=new DrawString.Int(rb.getString("memorydisplay.label.used")); //$NON-NLS-1$
						freeLabel=new DrawString.Int(rb.getString("memorydisplay.label.free")); //$NON-NLS-1$
						totalColorLabel=new DrawString.Int(rb.getString("memorydisplay.label.totalcolor")); //$NON-NLS-1$
						usedColorLabel=new DrawString.Int(rb.getString("memorydisplay.label.usedcolor")); //$NON-NLS-1$
						freeColorLabel=new DrawString.Int(rb.getString("memorydisplay.label.freecolor")); //$NON-NLS-1$
						delayLabel=new DrawString.Int(rb.getString("memorydisplay.label.delay")); //$NON-NLS-1$
				}
				catch(MissingResourceException mre) {
						//should never happen, but until this goes to the logger we ...
						System.out.println(mre);
				}
				maxGridValue=new DrawString.Int(MemoryMonitor.byteToMegaByteString(maxGridMemory));
				mediumGridValue=new DrawString.Int(MemoryMonitor.byteToMegaByteString(maxGridMemory/2));
				minGridValue=new DrawString.Int(MemoryMonitor.byteToMegaByteString(0));
				usedQ=new ArrayQueue.Float((int)displayWidth-1);
				freeQ=new ArrayQueue.Float((int)displayWidth-1);
				totalQ=new ArrayQueue.Float((int)displayWidth-1);
				rhints=new RenderingHints(RenderingHints.KEY_ANTIALIASING,
																	RenderingHints.VALUE_ANTIALIAS_ON);
				rhints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
									 RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				setFont(new Font("mono",Font.PLAIN,12)); //$NON-NLS-1$
				setOpaque(true);
				measure();			//see how big we are
		}

		/**
		 * Measure to overall size of our Component.
		 */
		private void measure() {
				int valueOffset=4;			//an offset between labels and values
				//let's get the FontMetrics
				Font f=getFont();
				//				FontMetrics fm=getFontMetrics(f);
				//we need to know how wide the gridvalues can be
				DrawString.Int tmp=new DrawString.Int("0000.0 MB"); //$NON-NLS-1$
				tmp.calcSize(f,frc);	
				gridXOffset=tmp.width+valueOffset;
				int h=(int)displayHeight;		//that height is needed for the display
				int w=(int)displayWidth+gridXOffset;		//that width is neededed for the display
				maxGridValue.calcSize(f,frc);
				mediumGridValue.calcSize(f,frc);
				minGridValue.calcSize(f,frc);
				maxGridValue.setLocation(tmp.width,DrawString.HORIZONTAL_RIGHT,
																 0,DrawString.VERTICAL_ASCENT);
				mediumGridValue.setLocation(tmp.width,DrawString.HORIZONTAL_RIGHT,
																 h/2,DrawString.VERTICAL_CENTER);
				minGridValue.setLocation(tmp.width,DrawString.HORIZONTAL_RIGHT,
																 h,DrawString.VERTICAL_BASELINE);
				h+=margins.bottom;			//add some room below the grid
				totalColorLabel.calcSize(f,frc);
				if(totalColorLabel.ascent<totalColorInfo.height)
						h+=totalColorInfo.height;
				else
						h+=totalColorLabel.ascent;
				totalColorInfo.x=0;
				totalColorInfo.y=h-totalColorInfo.height;
				totalColorLabel.setLocation(totalColorInfo.width+valueOffset,DrawString.HORIZONTAL_LEFT,
																		h,DrawString.VERTICAL_BASELINE);
				h+=totalColorLabel.descent+totalColorLabel.leading;
				usedColorLabel.calcSize(f,frc);
				if(usedColorLabel.ascent<usedColorInfo.height)
						h+=usedColorInfo.height;
				else
						h+=usedColorLabel.ascent;
				usedColorInfo.x=0;
				usedColorInfo.y=h-usedColorInfo.height;
				usedColorLabel.setLocation(usedColorInfo.width+valueOffset,DrawString.HORIZONTAL_LEFT,
																		h,DrawString.VERTICAL_BASELINE);
				freeColorLabel.calcSize(f,frc);
				freeColorInfo.x=usedColorLabel.x+usedColorLabel.width+valueOffset;
				freeColorInfo.y=h-freeColorInfo.height;
				freeColorLabel.setLocation(freeColorInfo.x+freeColorInfo.width+valueOffset,DrawString.HORIZONTAL_LEFT,
																		h,DrawString.VERTICAL_BASELINE);
				//add some room for the line painted between the grid and values
				h+=usedColorLabel.descent+usedColorLabel.leading;				
				delayLabel.calcSize(f,frc);
				h+=delayLabel.leading+delayLabel.ascent;				//text for values starts here
				jvmLabel.calcSize(f,frc);
				totalLabel.calcSize(f,frc);
				usedLabel.calcSize(f,frc);
				freeLabel.calcSize(f,frc);
				//we need to initialize with dummy values,otherwise we are unable to call setLocation()
				delayValue.calcSize(f,frc);
				jvmValue.calcSize(f,frc);
				totalValue.calcSize(f,frc);
				usedValue.calcSize(f,frc);
				freeValue.calcSize(f,frc);

				//this is strange but faster than using if-clauses
				int lw=Math.max(delayLabel.width,
												Math.max(jvmLabel.width,
																 Math.max(totalLabel.width,
																					Math.max(usedLabel.width,freeLabel.width))));
				lw+=gridXOffset;
				delayLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
															 h,DrawString.VERTICAL_BASELINE);
				h+=delayLabel.fontHeight;
				jvmLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
														 h,DrawString.VERTICAL_BASELINE);
				h+=jvmLabel.fontHeight;
				totalLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
															 h,DrawString.VERTICAL_BASELINE);
				h+=totalLabel.fontHeight;
				usedLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
															 h,DrawString.VERTICAL_BASELINE);
				h+=usedLabel.fontHeight;
				freeLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
															 h,DrawString.VERTICAL_BASELINE);
				lw+=valueOffset;
				delayValue.setLocation(lw,DrawString.HORIZONTAL_LEFT,
															 delayLabel.y,DrawString.VERTICAL_BASELINE);
				jvmValue.setLocation(lw,DrawString.HORIZONTAL_LEFT,
														 jvmLabel.y,DrawString.VERTICAL_BASELINE);
				totalValue.setLocation(lw,DrawString.HORIZONTAL_LEFT,
															 totalLabel.y,DrawString.VERTICAL_BASELINE);
				usedValue.setLocation(lw,DrawString.HORIZONTAL_LEFT,
															usedLabel.y,DrawString.VERTICAL_BASELINE);
				freeValue.setLocation(lw,DrawString.HORIZONTAL_LEFT,
															freeLabel.y,DrawString.VERTICAL_BASELINE);
				h+=freeLabel.descent;
				lw+=tmp.width;
				if(lw>w)				//the labels don't fit under the grid
						w=lw;
				
				Insets insets=getInsets();			//maybe we have a border set ?
				Dimension d=new Dimension(insets.left+margins.left+w+insets.right+margins.right,
																	insets.top+margins.top+h+insets.bottom+margins.bottom);
 				setSize(d);
				setPreferredSize(d);
 				setMaximumSize(d);
 				setMinimumSize(d);
 				invalidate();
		}

		/**
		 * If a  Border is set for this component we need to measure it again
		 * @param border the new Border for this component
		 */
		public void setBorder(Border border) {
				super.setBorder(border);
				measure();			//recalculate our component-size
		}

		/**
		 * This is what our MemoryMonitor tells us.
		 * @param mve the MemoryValueEvent reported by the MemoryMonitor
		 * we are registered with.
		 */
		 public void update(MemoryValueEvent mve) {
				 //this limits the value so the graph is painted 1 pixel above the bottom of the grid
				 float factor=displayHeight-1.0f;
				 //calculate the delay between updates
				 long nowTime=System.currentTimeMillis();
				 if(lastUpdateTime!=-1) {				//Thats at least the second call
						 long delay=nowTime-lastUpdateTime;
						 delayValue.text=formatDelay(delay);
						 lastUpdateTime=nowTime;
				 }
				 else
						 lastUpdateTime=nowTime;
				 //caluculate the painting-values from the memory usage
				 if(mve.getTotalMemory()>maxGridMemory) {
						 //we need to recalculate all values because the maximum for the grid is too small now
						 maxGridMemory*=2;	//we grow by factor 2
						 //every value currently in the queues therefore need to be divded by 2.0
						 float val;
						 float []tmp=totalQ.toArray();
						 totalQ.clear();
 						 for(int i=tmp.length-1;i>=0;i--) {
								 //now append beginning at end (faster than prepend)
								 val=factor-((factor-tmp[i])/2);
								 totalQ.append(val);
						 }
						 tmp=usedQ.toArray();
						 usedQ.clear();
						 for(int i=0;i<tmp.length;i++) {
								 val=factor-((factor-tmp[i])/2);
								 usedQ.append(val);
						 }
						 tmp=freeQ.toArray();
						 freeQ.clear();
						 for(int i=0;i<tmp.length;i++) {
								 val=factor-((factor-tmp[i])/2);
								 freeQ.append(val);
						 }
						 maxGridValue.text=MemoryMonitor.byteToMegaByteString(maxGridMemory);
						 mediumGridValue.text=MemoryMonitor.byteToMegaByteString(maxGridMemory/2);
				 }
				 float mm=(float)maxGridMemory;
				 float tm=(float)mve.getTotalMemory();
				 totalQ.append(factor-((factor*tm)/mm));
				 float um=(float)mve.getUsedMemory();
				 usedQ.append(factor-((factor*um)/mm));
				 float fm=(float)mve.getFreeMemory();
				 freeQ.append(factor-((factor*fm)/mm));

				 //set the values for the labels
				 jvmValue.text=MemoryMonitor.byteToMegaByteString(mve.getJVMMemory());
				 totalValue.text=MemoryMonitor.byteToMegaByteString(mve.getTotalMemory());
				 usedValue.text=MemoryMonitor.byteToMegaByteString(mve.getUsedMemory());
				 freeValue.text=MemoryMonitor.byteToMegaByteString(mve.getFreeMemory());
				 repaint();
		 }
		
		/**
		 * Formats the delay fro events into a String.
		 * @param delay the delay between updates from the MemoryMonitor
		 * @return String a formatted String for the delay.
		 */
		private String formatDelay(long delay) {
				StringBuffer sb=new StringBuffer();
				if(delay < 1000)				//print ms 
						sb.append(delay+" ms"); //$NON-NLS-1$
				else if(delay <60000) { //print ss.ms up to one minute
						sb.append(delay/1000);
						sb.append("."); //$NON-NLS-1$
						long ms=delay%1000;
						if(ms<10) 
								sb.append("00"); //$NON-NLS-1$
						else if(ms<100)
								sb.append(0);
						sb.append(ms);
						sb.append(" s"); //$NON-NLS-1$
				} else if (delay< 3600000) {
						//we're longo the minutes now, only  minutes and seconds now 
						sb.append(delay/60000);
						sb.append(":"); //$NON-NLS-1$
						long s=(delay%60000)/1000;
						if(s<10) 
								sb.append("0"); //$NON-NLS-1$
						sb.append(s);
						sb.append(" mm:ss"); //$NON-NLS-1$
				}
				else {
						//I'll give up on hours they can add up beyond 24, I don't care
						long h=(delay/3600000);
						long m=(delay%3600000)/60000;
						long s=((delay%3600000)%60000)/1000; 
						sb.append(h);
						sb.append(":"); //$NON-NLS-1$
						if(m<10) 
								sb.append("0"); //$NON-NLS-1$
						sb.append(m);
						sb.append(":"); //$NON-NLS-1$
						if(s<10) 
								sb.append("0"); //$NON-NLS-1$
						sb.append(s);
						sb.append(" hh:mm:ss"); //$NON-NLS-1$
				}
				return sb.toString();
		}
				
		
		/**
		 * This paints our component.
		 * @param Graphics g the Graphics object we paint with.
		 */
		public void paintComponent(Graphics g) {
				Graphics2D g2=(Graphics2D)g;
 				g2.setRenderingHints(rhints);
				AffineTransform oldat=(AffineTransform)g2.getTransform().clone();
				//gets our font
				g2.setFont(getFont());
				//first fill our background
				g2.setColor(bgColor);
				g2.fillRect(0,0,getWidth(),getHeight());
				Insets insets=getInsets();
				//translate by border insets
				g2.translate(insets.left,insets.top);
				paintGrid(g2);	//paint the grid
				paintLabels(g2);
				g2.setTransform(oldat);
		}

		/**
		 * This paints the grid and the labels for the colorinfo. 
		 * @param Graphics2D g2 the Graphics object we paint with.
		 */
		public void paintGrid(Graphics2D g2) {
				g2.setStroke(gbs);
				g2.setColor(gridColor);
				//we paint the line to offset the display from values across the window
				sepline.x1=0;
				sepline.x2=getWidth();
				//thats the vertical position of the line (under the usedColorLabel adding margins at top
				sepline.y1=margins.top+usedColorLabel.y+usedColorLabel.descent+usedColorLabel.leading;		
				sepline.y2=sepline.y1;
				g2.draw(sepline);
				//now translate by margins insets and paint the labels beside the grid
 				g2.translate(margins.left,margins.top);
				g2.drawString(maxGridValue.text,maxGridValue.x,maxGridValue.y);
				g2.drawString(mediumGridValue.text,mediumGridValue.x,mediumGridValue.y);
				g2.drawString(minGridValue.text,minGridValue.x,minGridValue.y);
 				g2.translate(gridXOffset,0);
				float y=0.0f;
				line.x1=0.0f;
				line.x2=displayWidth;
				while(y<=displayHeight) {
						line.y1=y;
						line.y2=y;
						g2.draw(line);
						y+=gridY;
				}
				float x=0.0f;
				line.y1=0.0f;
				line.y2=displayHeight;
				while(x<=displayWidth) {
						line.x1=x;
						line.x2=x;
						g2.draw(line);
						x+=gridX;
				}
				g2.setStroke(gbs);
				g2.setColor(totalColor);
				g2.fill(totalColorInfo);
				g2.setColor(usedColor);
				g2.fill(usedColorInfo);
				g2.setColor(freeColor);
				g2.fill(freeColorInfo);
				g2.setColor(gridColor);
				g2.draw(totalColorInfo);
				g2.draw(usedColorInfo);
				g2.draw(freeColorInfo);
				g2.setColor(textColor);
				g2.drawString(totalColorLabel.text,totalColorLabel.x,totalColorLabel.y);
				g2.drawString(usedColorLabel.text,usedColorLabel.x,usedColorLabel.y);
				g2.drawString(freeColorLabel.text,freeColorLabel.x,freeColorLabel.y);
				paintTotal(g2); //paint the total memory usage
				paintUsed(g2); //paint the used memory usage
				paintFree(g2); //paint the free memory
				//back to normal translation (insets that is);
 				g2.translate(-(margins.left+gridXOffset),-margins.top);

		}

		/**
		 * This paints the total memory usage to our component.
		 * @param Graphics2D g2 the Graphics object we paint with.
		 */
		public void paintTotal(Graphics2D g2) {
				g2.setColor(totalColor);
				float [] v=totalQ.toArray();
				if(v.length<2)
						return;
				float x=displayWidth-v.length;
				if(drawStyle==DRAW_FILLED) {
						g2.setStroke(fbs);
						line.x1=x;
						line.x2=x;
						line.y1=displayHeight-1.0f;
						for(int i=0;i<v.length;i++) {
								line.x1=x;
								line.x2=x;
								line.y2=v[i];
								g2.draw(line);
								x+=1;
						}
				}
				else if(drawStyle==DRAW_LINE) {
						g2.setStroke(lbs);
						for(int i=0;i<v.length-1;i++) {
								line.x1=x;
								x+=1;
								line.x2=x;
								line.y1=v[i];
								line.y2=v[i+1];
								g2.draw(line);
						}
				}
		}
				
		/**
		 * This paints the used memory usage to our component.
		 * @param Graphics2D g2 the Graphics object we paint with.
		 */
		public void paintUsed(Graphics2D g2) {
				g2.setColor(usedColor);
				float [] v=usedQ.toArray();
				if(v.length<2)
						return;
				float x=displayWidth-v.length;
				if(drawStyle==DRAW_FILLED) {
						g2.setStroke(fbs);
						line.x1=x;
						line.x2=x;
						line.y1=displayHeight-1.0f;
						for(int i=0;i<v.length;i++) {
								line.x1=x;
								line.x2=x;
								line.y2=v[i];
								g2.draw(line);
								x+=1;
						}
				}
				else if(drawStyle==DRAW_LINE) {
						g2.setStroke(lbs);
						for(int i=0;i<v.length-1;i++) {
								line.x1=x;
								x+=1;
								line.x2=x;
								line.y1=v[i];
								line.y2=v[i+1];
								g2.draw(line);
						}
				}
		}				

		/**
		 * This paints the free memory usage to our component.
		 * @param Graphics2D g2 the Graphics object we paint with.
		 */
		public void paintFree(Graphics2D g2) {
				g2.setColor(freeColor);
				float [] v=freeQ.toArray();
				if(v.length<2)
						return;
				float x=displayWidth-v.length;
				if(drawStyle==DRAW_FILLED) {
						g2.setStroke(fbs);
						line.x1=x;
						line.x2=x;
						line.y1=displayHeight-1.0f;
						for(int i=0;i<v.length;i++) {
								line.x1=x;
								line.x2=x;
								line.y2=v[i];
								g2.draw(line);
								x+=1;
						}
				}
				else if(drawStyle==DRAW_LINE) {
						g2.setStroke(lbs);
						for(int i=0;i<v.length-1;i++) {
								line.x1=x;
								x+=1;
								line.x2=x;
								line.y1=v[i];
								line.y2=v[i+1];
								g2.draw(line);
						}
				}
		}				

		/**
		 * This paints the labels to our component.
		 * @param Graphics2D g2 the Graphics object we paint with.
		 */
		public void paintLabels(Graphics2D g2) {
				g2.translate(margins.left,margins.top);
				g2.setColor(textColor);
 				g2.drawString(delayLabel.text,delayLabel.x,delayLabel.y);
 				g2.drawString(jvmLabel.text,jvmLabel.x,jvmLabel.y);
 				g2.drawString(totalLabel.text,totalLabel.x,totalLabel.y);
 				g2.drawString(usedLabel.text,usedLabel.x,usedLabel.y);
 				g2.drawString(freeLabel.text,freeLabel.x,freeLabel.y);
				g2.setColor(valueColor);
				g2.drawString(delayValue.text,delayValue.x,delayValue.y);
				g2.drawString(jvmValue.text,jvmValue.x,jvmValue.y);
				g2.drawString(totalValue.text,totalValue.x,totalValue.y);
				g2.drawString(usedValue.text,usedValue.x,usedValue.y);
				g2.drawString(freeValue.text,freeValue.x,freeValue.y);
				g2.translate(-margins.left,-margins.top);
		}

}
