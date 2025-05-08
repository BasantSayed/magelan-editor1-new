/* This File is part of the wayoda-project                                    */
/* Filename : Screen.java                                                     */
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

/**
 * A collection of utilities dealing with the way components are
 * located and rendered on the screen.
 * <p>
 * This class provides some static methods for easy window-placement,
 * and a FontRenderContext that can be used to measure the visual size 
 * of Strings when there is no Graphics2D-object available.
 * <p>
 * @author Eberhard Fahle
 * @version 0.1
 */
public final class Screen {
		/** A FontRenderContext that can be used to measure Strings */
		private static FontRenderContext frc=null;
		
		/** 
		 * The constructor is private, 
		 * because we don't want any instances of this class 
		 */
		private Screen() {
		}
		
		/**
		 * This method returns the FontRenderContext
		 * for the default Screen-device.
		 * @return FontRenderContext the FontRenderContext used for the screen-device
		 */
		private static void createFontRenderContext() {
				GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice gd=ge.getDefaultScreenDevice();
				GraphicsConfiguration gc=gd.getDefaultConfiguration();
				BufferedImage bi=gc.createCompatibleImage(1,1);				//we need at least one pixel
				Graphics2D g2=bi.createGraphics();
				frc=g2.getFontRenderContext();
		}

		/**
		 * Gets the FontRenderContext for the default screen-device.
		 * The FontRenderContext returned here, is used to measure
		 * the visual size of Strings that are painted by a component
		 * that is not realized yet. 
		 * The FontRenderContext is mapped to the defaultScreenDevice 
		 * of the JVM. The FontRenderContext is only to be used for 
		 * components that are painted onto the screen. 
		 * For performance reasons we return a reference
		 * to a static FontRenderContext-object that is created in this class.
		 * Take care never to set the object returned here to null.
		 * Otherwise we always have to create a new one when this method 
		 * is called.  So just use the FontRenderContext for obtaining the 
		 * metrics of a font and than leave it alone.
		 * @return FontRenderContext a FontRenderContext for the 
		 * default screen.
		 */
		public static synchronized FontRenderContext getDefaultFontRenderContext() {
				if(frc==null)
						createFontRenderContext();
				return frc;
		}

		/**
		 * Gets the dimension of the screen from the defaultToolkit.
		 * @return Dimension the with and height of the screen
		 * we paint on.
		 */
		public static Dimension getScreenSize() {
				return Toolkit.getDefaultToolkit().getScreenSize();
		}

		/**
		 * Gets the coordinates where the upper lefthand corner
		 * of a Window should be placed, if you want it to show
		 * centered on the current screen. If the window
		 * is bigger than the current screen, we return a location 
		 * that ensures that the top-left corner of the Window is always visible.
		 * @param w the window that is to be placed. This window
		 * must already have its final size (pack() called already)
		 * in order to get correct values.
		 * @return Point the location that can be used by the method
		 * Window.setLocation(Point)
		 * @throws IllegalArgumentException if the window is null
		 */
		public static Point getScreenCenterLocation(Window w) {
				if(w==null)
						throw new IllegalArgumentException("window is null"); //$NON-NLS-1$
				Dimension screen=getScreenSize();
				Dimension win=w.getSize();
				int x=(screen.width-win.width)/2;
				if(x<0)
						x=0;				//ensure left side is on screen
				int y=(screen.height-win.height)/2;
				if(y<0)
						y=0;				//ensure top side is on screen
				return new Point(x,y);
		}

		/**
		 * Gets the coordinates where the upper lefthand corner
		 * of a Window should be placed, if you want it to show
		 * centered on a parent window. If that would place our window
		 * partly off-screen, we return a location
		 * that ensures that the child-window is fully visible. 
		 * @param child the window that is to be placed. This window
		 * must already have its final size (pack() called already)
		 * in order to get correct values.
		 * @param parent the window where our child-window is to be centered on.
		 * @return Point the location that can be used by the method
		 * Window.setLocation(Point)
		 * @throws IllegalArgumentException if either the child- or
		 * parent-window is null
		 */
		public static Point getWindowCenterLocation(Window child, Window parent) {
				if(child==null)
						throw new IllegalArgumentException("child-window is null"); //$NON-NLS-1$
				if(parent==null)
						throw new IllegalArgumentException("parent-window is null"); //$NON-NLS-1$
				Dimension screen=getScreenSize();
				Dimension cd=child.getSize();
				Dimension pd=parent.getSize();
				Point ploc=parent.getLocationOnScreen();
				Point cloc=new Point(0,0);
				if(cd.width<=pd.width) 
						cloc.x=ploc.x+((pd.width-cd.width)/2);
				else
						cloc.x=ploc.x-((cd.width-pd.width)/2);
				if(cd.height<=pd.height) 
						cloc.y=ploc.y+((pd.height-cd.height)/2);
				else
						cloc.y=ploc.y-((cd.height-pd.height)/2);
				if(cloc.x+cd.width > screen.width)
						cloc.x=screen.width-cd.width;
				if(cloc.y+cd.height > screen.height)
						cloc.y=screen.height-cd.height;
				if(cloc.x<0)
						cloc.x=0;
				if(cloc.y<0)
						cloc.y=0;
				return cloc;
		}
		
}















