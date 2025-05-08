/* This File is part of the wayoda-project                                    */
/* Filename : MemoryToolTip.java                                              */
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JToolTip;

import org.wayoda.util.DrawString;

/**
 * This component displays the values from a MemeoryMonitor in a custom
 * ToolTip component. The MemoryToolTipComonent is a MemoryValueListener so it gets 
 * updates from the MemoryMonitor. In order tu use this class you need to do the following:<br>
 * 1. Create a new MemoryToolTipComponent.<br>
 * 2. Register the MemoryToolTipComponent with a MemoryMonitor.<br>
 * 3. Create the Component that should display our custom tooltip<br>
 * 4. For the Component you need to override the method <code>JToolTip JComponent.createToolTip()</code>
 * so that it returns the MemoryToolTipComponent created in 1.<br>
 * <strong>Important</strong> don't get misleaded by the name createToolTip(). Don't return
 * a new instance of MemeoryToolTipComponent every time the method is called. Always return the one
 * that was registered with the MemoryMonitor.<p>
 * Example<code><pre>
 *   MemoryMonitor mm=new MemoryMonitor(1,64);
 *   MemoryToolTipComponent mttc=new MemoryToolTipComponent();
 *   mm.addValueListener(mttc);
 *   JButton btn=new JButton("do Something!") {
 *       //Now override this method
 * 			 public JToolTip createToolTip() {
 *						return md; //NOT A NEW INSTANCE ALWAYS THE ONE REGISTERED
 *       }
 * 	 }; 
 * @author Eberhard Fahle
 * @version 1.0
 */
public class MemoryToolTipComponent extends JToolTip implements MemoryValueListener {
		/** The DrawString.Int for the Headline */
		private DrawString.Int headerLabel;
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

		/** We might want some margins for the text */
		private Insets margins=new Insets(4,4,4,4);

		public MemoryToolTipComponent() {
				super();
				//i18n of the display
				try {
						ResourceBundle rb=ResourceBundle.getBundle("org.wayoda.system.MemoryMonitorResources"); //$NON-NLS-1$
						headerLabel=new DrawString.Int(rb.getString("memorytooltip.label.header")); //$NON-NLS-1$
						jvmLabel=new DrawString.Int(rb.getString("memorytooltip.label.jvm")); //$NON-NLS-1$
						totalLabel=new DrawString.Int(rb.getString("memorytooltip.label.total")); //$NON-NLS-1$
						usedLabel=new DrawString.Int(rb.getString("memorytooltip.label.used")); //$NON-NLS-1$
						freeLabel=new DrawString.Int(rb.getString("memorytooltip.label.free")); //$NON-NLS-1$
				}
				catch(MissingResourceException mre) {
						//should never happen, but until this goes to the logger we ...
						System.out.println(mre);
				}
				setOpaque(false);
				//now let's see how big our component is
				measure();
		}

		/**
		 * Implementation of the MemoryValueListener-interface
		 * @param mve the event received from the MemoryMonitor
		 */
		public void update(MemoryValueEvent mve) {
				//convert the reported bytevalues to megabytes and then to Strings
				jvmValue.text=MemoryMonitor.byteToMegaByteString(mve.getJVMMemory());
				totalValue.text=MemoryMonitor.byteToMegaByteString(mve.getTotalMemory());
				usedValue.text=MemoryMonitor.byteToMegaByteString(mve.getUsedMemory());
				freeValue.text=MemoryMonitor.byteToMegaByteString(mve.getFreeMemory());
				repaint();
		}

		/**
		 * We have to calculate the size of our component as it depends on the font and
		 * Strings. This must be done on every Font-change or Border-change.
		 */
		private void measure() {
				int w=0;			
				int h=0; 
				int offset=4; //The spacing between the label and the values
				//let's get the FontMetrics
				Font f=getFont();
				FontMetrics fm=getFontMetrics(f);
				//the maximum width of the values, this will work until memory size is below 10GB (in the JVM)
				DrawString.Int val=new DrawString.Int("00000.0 MB"); //$NON-NLS-1$
				val.calcSize(fm);
				//now lets see how wide our labels could grow 
				headerLabel.calcSize(fm);
				jvmLabel.calcSize(fm);
				totalLabel.calcSize(fm);
				usedLabel.calcSize(fm);
				freeLabel.calcSize(fm);
				jvmValue.calcSize(fm);
				totalValue.calcSize(fm);
				usedValue.calcSize(fm);
				freeValue.calcSize(fm);
				//now go through the labels to find the longest one
				int lw=jvmLabel.width;
				if(totalLabel.width>lw)
						lw=totalLabel.width;
				if(usedLabel.width>lw)
						lw=usedLabel.width;
				if(freeLabel.width>lw)
						lw=freeLabel.width;
				w=lw+offset+val.width;			//the labels width plus an offset to values plus width of the values
				h=fm.getAscent();
				headerLabel.setLocation(w/2,DrawString.HORIZONTAL_CENTER,
																0,DrawString.VERTICAL_ASCENT);
				h+=fm.getHeight();
				jvmLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
														 h,DrawString.VERTICAL_BASELINE);
				jvmValue.setLocation(lw+offset,DrawString.HORIZONTAL_LEFT,
														 h,DrawString.VERTICAL_BASELINE);
				h+=fm.getHeight();
				totalLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
														 h,DrawString.VERTICAL_BASELINE);
				totalValue.setLocation(lw+offset,DrawString.HORIZONTAL_LEFT,
														 h,DrawString.VERTICAL_BASELINE);
				h+=fm.getHeight();
				usedLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
														 h,DrawString.VERTICAL_BASELINE);
				usedValue.setLocation(lw+offset,DrawString.HORIZONTAL_LEFT,
														 h,DrawString.VERTICAL_BASELINE);
				h+=fm.getHeight();
				freeLabel.setLocation(lw,DrawString.HORIZONTAL_RIGHT,
														 h,DrawString.VERTICAL_BASELINE);
				freeValue.setLocation(lw+offset,DrawString.HORIZONTAL_LEFT,
														 h,DrawString.VERTICAL_BASELINE);
				Insets insets=getInsets();
				Dimension d=new Dimension(insets.left+margins.left+w+insets.right+margins.right,
																	insets.top+margins.top+h+insets.bottom+margins.bottom);
				setPreferredSize(d);
				invalidate();
		}

		/**
		 * That paints our new Tooltip
		 * @param g the Graphics-object we paint with
		 */
		public void paintComponent(Graphics g) {
				Graphics2D g2=(Graphics2D)g;
				AffineTransform oldat=(AffineTransform)g2.getTransform().clone();
				Insets insets=getInsets();
				//fill the window background
				g2.setColor(getBackground());
				g2.fillRect(0,0,getWidth(),getHeight());
				//Now a black 1 pixel border and a line below the header
				g2.setColor(getForeground());
				g2.drawRect(0,0,getWidth()-1,getHeight()-1);
				g2.drawLine(0,headerLabel.height+insets.top+margins.top,getWidth(),
										headerLabel.height+insets.top+margins.top);
				g2.translate(insets.left+margins.left,insets.top+margins.top);
				g2.setFont(getFont());
				g2.drawString(headerLabel.text,headerLabel.x,headerLabel.y);
				g2.drawString(jvmLabel.text,jvmLabel.x,jvmLabel.y);
				g2.drawString(totalLabel.text,totalLabel.x,totalLabel.y);
				g2.drawString(usedLabel.text,usedLabel.x,usedLabel.y);
				g2.drawString(freeLabel.text,freeLabel.x,freeLabel.y);
				g2.drawString(jvmValue.text,jvmValue.x,jvmValue.y);
				g2.drawString(totalValue.text,totalValue.x,totalValue.y);
				g2.drawString(usedValue.text,usedValue.x,usedValue.y);
				g2.drawString(freeValue.text,freeValue.x,freeValue.y);
				g2.setTransform(oldat);
		}

}
