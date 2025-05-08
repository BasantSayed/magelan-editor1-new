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
package org.magelan.editor.util;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.AttributedCharacterIterator;

/**
 * A <code>Graphics</code> object capable of producing DXF file data.
 * <p>
 * @version 1.0, 03/2003
 * @author  Assen Antov
 */
public class DXFGraphics extends Graphics {
    
    /** Draw lines as filled polygons (solids) if true. */
    private boolean solid = false;
    
    /** Output stream to write the DXF data to. */
    private PrintStream out;
    private OutputStream str;
    
    /** Default color. */
    public static final int COLOR_BYLAYER = 256;
    
    public static final int COLOR_RED = 1;
    public static final int COLOR_YELLOW = 2;
    public static final int COLOR_GREEN = 3;
    public static final int COLOR_CYAN = 4;
    public static final int COLOR_BLUE = 5;
    public static final int COLOR_MAGENTA = 6;
    public static final int COLOR_WHITE = 7;
    public static final int COLOR_DARK_GRAY = 8;
    public static final int COLOR_BLACK = 250;
    public static final int COLOR_ORANGE = 21;
    
    /** Default linestyle. */
    public static final String BYLAYER = "BYLAYER"; //$NON-NLS-1$
    
    /** List of layers. */
    private List layers;
    
    /** Name of the current layer. */
    private String layer;
    
    
    /**
     * A layer class used for internal representation of 
     * DXF layers.
     */
    public class Layer {
      private String name;
      private int color;
      private String lineType;
      public Layer(String name, int color, String lineType) {
        if ((this.name = name) == null)
          throw new DXFEncodingException("layer name cannot be null"); //$NON-NLS-1$
        this.color = color;
        this.lineType = lineType;
      }
      public String getName() { return name; }
      public int getColor() { return color; }
      public String getLineType() { return lineType; }
    }
    
    
    /**
     * Default constructor. Dumps the DXF data to the 
     * <code>System.out</code> output stream.
     */
    public DXFGraphics() {
      this(null);
    }

    /**
     * Constructor by output stream.
     * <p>
     * @param out output stream to writre the DXF data to
     */
    public DXFGraphics(OutputStream str) {
//      if (str == null)
//        throw new DXFEncodingException("DXF output stream cannot be null");
      
      if (str != null) {
        if (str instanceof PrintStream) out = (PrintStream) str;
        else out = new PrintStream(str);
      }
    
      Layer l = new Layer("0", COLOR_WHITE, BYLAYER); //$NON-NLS-1$
      addLayer(l);
      setLayer(l);
      setColor(null);
      
      // initialise dxf
      code(999, "magelan.editor.utils.DXFGraphics"); //$NON-NLS-1$
      code(999, "Version 1.0"); //$NON-NLS-1$
      code(999, "Copyright 2003 (c) Assen Antov"); //$NON-NLS-1$
      code(0, "SECTION"); //$NON-NLS-1$
      code(2, "ENTITIES"); //$NON-NLS-1$
    }
    
    
    /**
     * Adds a layer to the list of layers.
     * <p>
     * @param layer adds the parameter layer
     */
    public void addLayer(Layer layer) {
      if (layers == null) layers = new ArrayList();
      layers.add(layer);
    }
    
    /**
     * Removes the parameter layer.
     * <p>
     * @param layer name of the layer to remove
     */
    public boolean removeLayer(String layer) {
      return removeLayer(findLayer(layer));
    }
    
    /**
     * Removes the parameter layer.
     * <p>
     * @param layer layer to remove
     */
    public boolean removeLayer(Layer layer) {
      if ("0".equals(layer.getName())) //$NON-NLS-1$
        throw new DXFEncodingException("cannot remove layer '0'"); //$NON-NLS-1$
      boolean b = layers.remove(layer);
      if (layers.isEmpty()) layers = null;
      return b;
    }
    
    /**
     * Sets the parameter layer to be the current layer.
     * <p>
     * @param name  layer name to become current
     */
    public void setLayer(String name) {
      if (findLayer(name) == null)
        throw new DXFEncodingException("undefined layer '"+name+"'"); //$NON-NLS-1$ //$NON-NLS-2$
      layer = ""+name; //$NON-NLS-1$
    }
    
    /**
     * Sets the parameter layer to be the current layer. Adds
     * the layer if not present.
     * <p>
     * @param name  layer to become current
     */
    public void setLayer(Layer layer) {
      if (layers != null && layers.contains(layer))
        addLayer(layer);
      this.layer = ""+layer.getName(); //$NON-NLS-1$
    }
    
    /**
     * Gets the current layer.
     * <p>
     * @return  the name of the current layer
     */
    public String getLayer() { return layer; }
    
    
    void setLayers(List layers) {
      this.layers = layers;
    }
    
    List getLayers() { return layers; }
    
    /**
     * Finds the layer with the name specified.
     * <p>
     * @param   name name of the layer to search for
     * @return  a layer found or <code>null</code>
     */
    public Layer findLayer(String name) {
      Iterator i = layers.iterator();
      while (i.hasNext()) {
        Layer l = (Layer) i.next();
        if (l.getName().equals(name))
          return l;
      }
      return null;
    }
    
    /**
     * Converts the parameter <code>Color</code> to DXF
     * integer color.
     * <p>
     * @param   color standard AWT color
     * @return  converted color
     */
    public int toDXFColor(Color color) {
      if (color == null) return COLOR_BYLAYER;
      if (color.equals(Color.red)) return COLOR_RED;
      else if (color.equals(Color.yellow)) return COLOR_YELLOW;
      else if (color.equals(Color.green)) return COLOR_GREEN;
      else if (color.equals(Color.cyan)) return COLOR_CYAN;
      else if (color.equals(Color.blue)) return COLOR_BLUE;
      else if (color.equals(Color.magenta)) return COLOR_MAGENTA;
      else if (color.equals(Color.white)) return COLOR_WHITE;
      else if (color.equals(Color.darkGray)) return COLOR_DARK_GRAY;
      else if (color.equals(Color.orange)) return COLOR_ORANGE;
      else if (color.equals(Color.black)) return COLOR_BLACK;
      else if ((color.getRed() <= 204 && color.getRed() >= 153)
        && (color.getGreen() <= 204 && color.getGreen() >= 153)
        && (color.getBlue() <= 255 && color.getBlue() >= 230)) return 153;
      else if (color.equals(new Color(204, 204, 204))) return 253;
      else if (color.equals(new Color(153, 153, 153))) return 252;
      else {
        int cc = color.getRGB()>0? color.getRGB() >> 16 : -color.getRGB() >> 16;
        if (cc == 102) return 153;
        return cc;
      }
    }
    
    /**
     *
     */
    public OutputStream getOutputStream() { return str; }
    
    /**
     *
     */
    public void close() {
      code(0, "ENDSEC"); //$NON-NLS-1$
      code(0, "EOF"); //$NON-NLS-1$
      
      out.close();
    }
    
    
    private void code(int code, int val) {
      if (out == null) return;
      out.println(""+code); //$NON-NLS-1$
      if (code >= 20 && code < 30) out.println(""+(-val)); //$NON-NLS-1$
      else out.println(""+val); //$NON-NLS-1$
    }
    
    private void code(int code, String val) {
      if (out == null) return;
      out.println(""+code); //$NON-NLS-1$
      out.println(val);
    }
    
    private void codeCommon() {
      //code(5, getNextHandle()); // handle number
      code(6, BYLAYER);   // linetype
      code(8, getLayer());  // layer name
      
      // determine entity color
      if (toDXFColor(getColor()) != findLayer(getLayer()).getColor())
        code(62, toDXFColor(getColor()));
       
      //code(67, 0);        // model space
      //code(370, 1);       // lineweight (16-bit integer)
      //code(48, "1.0");    // linetype scale
      //code(60, 0);        // visible
    }
    
    /**
     * Creates a new <code>Graphics</code> object that is 
     * a copy of this <code>Graphics</code> object.
     * @return  a new graphics context that is a copy of 
     *          this graphics context.
     */
    public Graphics create() {
      DXFGraphics dxf = new DXFGraphics();
      dxf.setOut(getOut());
      dxf.setColor(getColor());
      dxf.setLayers(getLayers());
      dxf.setLayer(getLayer());
      dxf.translate(x, y);
      return dxf;
    }
  
  
    void setOut(PrintStream out) {
      this.out = out;
    }
    
    PrintStream getOut() { return out; }
    
    private int x = 0;
    private int y = 0;
    
    /**
     * Translates the origin of the graphics context to the point
     * (<i>x</i>,&nbsp;<i>y</i>) in the current coordinate system. 
     * Modifies this graphics context so that its new origin corresponds 
     * to the point (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's 
     * original coordinate system.  All coordinates used in subsequent 
     * rendering operations on this graphics context will be relative 
     * to this new origin.
     * @param  x   the <i>x</i> coordinate.
     * @param  y   the <i>y</i> coordinate.
     */
    public void translate(int x, int y) {
      this.x += x;
      this.y += y;
    }

  
    private Color color;
    
    /**
     * Gets this graphics context's current color.
     * @return    this graphics context's current color.
     * @see       java.awt.Color
     * @see       java.awt.Graphics#setColor(Color)
     */
    public Color getColor() { return color; }

    /**
     * Sets this graphics context's current color to the specified 
     * color. All subsequent graphics operations using this graphics 
     * context use this specified color. 
     * @param     c   the new rendering color.
     * @see       java.awt.Color
     * @see       java.awt.Graphics#getColor
     */
    public void setColor(Color c) { color = c; }

    /**
     * Sets the paint mode of this graphics context to overwrite the 
     * destination with this graphics context's current color. 
     * This sets the logical pixel operation function to the paint or
     * overwrite mode.  All subsequent rendering operations will
     * overwrite the destination with the current color. 
     */
    public void setPaintMode() {}

    /**
     * Sets the paint mode of this graphics context to alternate between 
     * this graphics context's current color and the new specified color. 
     * This specifies that logical pixel operations are performed in the 
     * XOR mode, which alternates pixels between the current color and 
     * a specified XOR color. 
     * <p>
     * When drawing operations are performed, pixels which are the 
     * current color are changed to the specified color, and vice versa. 
     * <p>
     * Pixels that are of colors other than those two colors are changed 
     * in an unpredictable but reversible manner; if the same figure is 
     * drawn twice, then all pixels are restored to their original values. 
     * @param     c1 the XOR alternation color
     */
    public void setXORMode(Color c1) {}

    private Font font = new Font("Arial", Font.PLAIN, 12); //$NON-NLS-1$
    
    /**
     * Gets the current font.
     * @return    this graphics context's current font.
     * @see       java.awt.Font
     * @see       java.awt.Graphics#setFont(Font)
     */
    public Font getFont() { return font; }

    /**
     * Sets this graphics context's font to the specified font. 
     * All subsequent text operations using this graphics context 
     * use this font. 
     * @param  font   the font.
     * @see     java.awt.Graphics#getFont
     * @see     java.awt.Graphics#drawString(java.lang.String, int, int)
     * @see     java.awt.Graphics#drawBytes(byte[], int, int, int, int)
     * @see     java.awt.Graphics#drawChars(char[], int, int, int, int)
    */
    public void setFont(Font font) { this.font = font; }

    /**
     * Gets the font metrics for the specified font.
     * @return    the font metrics for the specified font.
     * @param     f the specified font
     * @see       java.awt.Graphics#getFont
     * @see       java.awt.FontMetrics
     * @see       java.awt.Graphics#getFontMetrics()
     */
    public FontMetrics getFontMetrics(Font f) { 
      return new FontMetrics(f) {
         public int charWidth(int ch) { return 6; }
         public int charWidth(char ch) { return 6; }
         public int charsWidth(char[] ch, int st, int len) { return 6+(len-1)*7; }
      };
    }
  
    private Rectangle clip = new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    
    /**
     * Returns the bounding rectangle of the current clipping area.
     * This method refers to the user clip, which is independent of the
     * clipping associated with device bounds and window visibility.  
     * If no clip has previously been set, or if the clip has been 
     * cleared using <code>setClip(null)</code>, this method returns
     * <code>null</code>.
     * The coordinates in the rectangle are relative to the coordinate
     * system origin of this graphics context.
     * @return      the bounding rectangle of the current clipping area,
     *              or <code>null</code> if no clip is set.
     * @see         java.awt.Graphics#getClip
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public Rectangle getClipBounds() { return clip; }

    /** 
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.  If there is no 
     * current clipping area, either because the clip has never been 
     * set, or the clip has been cleared using <code>setClip(null)</code>, 
     * the specified rectangle becomes the new clip.
     * This method sets the user clip, which is independent of the
     * clipping associated with device bounds and window visibility.  
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use any of the setClip methods.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     * @see #setClip(Shape)
     */
    public void clipRect(int x, int y, int width, int height) {
    }

    /**
     * Sets the current clip to the rectangle specified by the given
     * coordinates.  This method sets the user clip, which is 
     * independent of the clipping associated with device bounds
     * and window visibility.  
     * Rendering operations have no effect outside of the clipping area.
     * @param       x the <i>x</i> coordinate of the new clip rectangle.
     * @param       y the <i>y</i> coordinate of the new clip rectangle.
     * @param       width the width of the new clip rectangle.
     * @param       height the height of the new clip rectangle.
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(Shape)
     * @see     java.awt.Graphics#getClip
     * @since       JDK1.1
     */
    public void setClip(int x, int y, int width, int height) {
      clip = new Rectangle(x, y, width, height);
    }

    /**
     * Gets the current clipping area.
     * This method returns the user clip, which is independent of the
     * clipping associated with device bounds and window visibility.
     * If no clip has previously been set, or if the clip has been 
     * cleared using <code>setClip(null)</code>, this method returns 
     * <code>null</code>.
     * @return      a <code>Shape</code> object representing the 
     *              current clipping area, or <code>null</code> if
     *              no clip is set.
     * @see         java.awt.Graphics#getClipBounds
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @see         java.awt.Graphics#setClip(Shape)
     * @since       JDK1.1
     */
    public Shape getClip() { return clip; }

    /**
     * Sets the current clipping area to an arbitrary clip shape.
     * Not all objects that implement the <code>Shape</code> 
     * interface can be used to set the clip.  The only 
     * <code>Shape</code> objects that are guaranteed to be 
     * supported are <code>Shape</code> objects that are
     * obtained via the <code>getClip</code> method and via 
     * <code>Rectangle</code> objects.  This method sets the
     * user clip, which is independent of the clipping associated
     * with device bounds and window visibility.
     * @param clip the <code>Shape</code> to use to set the clip
     * @see         java.awt.Graphics#getClip()
     * @see         java.awt.Graphics#clipRect
     * @see         java.awt.Graphics#setClip(int, int, int, int)
     * @since       JDK1.1
     */
    public void setClip(Shape clip) {}

    /**
     * Copies an area of the component by a distance specified by 
     * <code>dx</code> and <code>dy</code>. From the point specified
     * by <code>x</code> and <code>y</code>, this method
     * copies downwards and to the right.  To copy an area of the 
     * component to the left or upwards, specify a negative value for 
     * <code>dx</code> or <code>dy</code>.
     * If a portion of the source rectangle lies outside the bounds 
     * of the component, or is obscured by another window or component, 
     * <code>copyArea</code> will be unable to copy the associated
     * pixels. The area that is omitted can be refreshed by calling 
     * the component's <code>paint</code> method.
     * @param       x the <i>x</i> coordinate of the source rectangle.
     * @param       y the <i>y</i> coordinate of the source rectangle.
     * @param       width the width of the source rectangle.
     * @param       height the height of the source rectangle.
     * @param       dx the horizontal distance to copy the pixels.
     * @param       dy the vertical distance to copy the pixels.
     */
    public void copyArea(int x, int y, int width, int height,
          int dx, int dy) {}

    /** 
     * Draws a line, using the current color, between the points 
     * <code>(x1,&nbsp;y1)</code> and <code>(x2,&nbsp;y2)</code> 
     * in this graphics context's coordinate system. 
     * @param   x1  the first point's <i>x</i> coordinate.
     * @param   y1  the first point's <i>y</i> coordinate.
     * @param   x2  the second point's <i>x</i> coordinate.
     * @param   y2  the second point's <i>y</i> coordinate.
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
      if (solid) {
        int d = 1;
        int e = 0;
    
        int[] x = new int[4];
        int[] y = new int[4];
        
        float dX = x1 - x2;
        float dY = y1 - y2;
        
        float l = (float) Math.sqrt(dX*dX + dY*dY);
        
        float sa = 1;
        float ca = 1;
          
        if (l >= 0.01) {
          sa = dY/l;
          ca = dX/l;
        }
        
        x[0] = (int) ((float)x1 + ((float)d/2 - e)*sa);
        y[0] = (int) ((float)y1 - ((float)d/2 - e)*ca);
        
        x[1] = (int) ((float)x1 + (-(float)d/2 - e)*sa);
        y[1] = (int) ((float)y1 - (-(float)d/2 - e)*ca);
        
        x[2] = (int) ((float)x2 + (-(float)d/2 - e)*sa);
        y[2] = (int) ((float)y2 - (-(float)d/2 - e)*ca);
        
        x[3] = (int) ((float)x2 + ((float)d/2 - e)*sa);
        y[3] = (int) ((float)y2 - ((float)d/2 - e)*ca);
        
        fillPolygon(x, y, 4);
      }
      
      else {
        code(0, "LINE");    // type //$NON-NLS-1$
        codeCommon();
        code(10, x+x1);     // x1 coord
        code(20, y+y1);     // y1 coord
        code(30, 0);        // z1 coord
        code(11, x+x2);     // x2 coord
        code(21, y+y2);     // y2 coord
        code(31, 0);        // z2 coord
      }
    }

    /** 
     * Fills the specified rectangle. 
     * The left and right edges of the rectangle are at 
     * <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. 
     * The top and bottom edges are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. 
     * The resulting rectangle covers an area 
     * <code>width</code> pixels wide by 
     * <code>height</code> pixels tall.
     * The rectangle is filled using the graphics context's current color. 
     * @param         x   the <i>x</i> coordinate 
     *                         of the rectangle to be filled.
     * @param         y   the <i>y</i> coordinate 
     *                         of the rectangle to be filled.
     * @param         width   the width of the rectangle to be filled.
     * @param         height   the height of the rectangle to be filled.
     * @see           java.awt.Graphics#clearRect
     * @see           java.awt.Graphics#drawRect
     */
    public void fillRect(int x, int y, int width, int height) {
      if ((width < 0) || (height < 0)) return;
      
      int[] xPoints = new int[] { x, x + width, x + width, x };
      int[] yPoints = new int[] { y, y, y+height, y+height };
      
      code(0, "SOLID"); //$NON-NLS-1$
      codeCommon();
      
      code(10, this.x + x);
      code(20, this.y + y);
      code(30, 0);
      
      code(11, this.x + x+width);
      code(21, this.y + y);
      code(31, 0);
      
      code(12, this.x + x);
      code(22, this.y + y+height);
      code(32, 0);
      
      code(13, this.x + x+width);
      code(23, this.y + y+height);
      code(33, 0);
    }

    /** 
     * Draws the outline of the specified rectangle. 
     * The left and right edges of the rectangle are at 
     * <code>x</code> and <code>x&nbsp;+&nbsp;width</code>. 
     * The top and bottom edges are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>. 
     * The rectangle is drawn using the graphics context's current color.
     * @param         x   the <i>x</i> coordinate 
     *                         of the rectangle to be drawn.
     * @param         y   the <i>y</i> coordinate 
     *                         of the rectangle to be drawn.
     * @param         width   the width of the rectangle to be drawn.
     * @param         height   the height of the rectangle to be drawn.
     * @see          java.awt.Graphics#fillRect
     * @see          java.awt.Graphics#clearRect
     */
    public void drawRect(int x, int y, int width, int height) {
      if ((width < 0) || (height < 0)) return;
      
      int[] xPoints = new int[] { x, x + width, x + width, x };
      int[] yPoints = new int[] { y, y, y+height, y+height };
      
      codePLine(xPoints, yPoints, 4, true);
    }
    
    /** 
     * Clears the specified rectangle by filling it with the background
     * color of the current drawing surface. This operation does not 
     * use the current paint mode. 
     * <p>
     * Beginning with Java&nbsp;1.1, the background color 
     * of offscreen images may be system dependent. Applications should 
     * use <code>setColor</code> followed by <code>fillRect</code> to 
     * ensure that an offscreen image is cleared to a specific color. 
     * @param       x the <i>x</i> coordinate of the rectangle to clear.
     * @param       y the <i>y</i> coordinate of the rectangle to clear.
     * @param       width the width of the rectangle to clear.
     * @param       height the height of the rectangle to clear.
     * @see         java.awt.Graphics#fillRect(int, int, int, int)
     * @see         java.awt.Graphics#drawRect
     * @see         java.awt.Graphics#setColor(java.awt.Color)
     * @see         java.awt.Graphics#setPaintMode
     * @see         java.awt.Graphics#setXORMode(java.awt.Color)
     */
    public void clearRect(int x, int y, int width, int height) {}

    /** 
     * Draws an outlined round-cornered rectangle using this graphics 
     * context's current color. The left and right edges of the rectangle 
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>, 
     * respectively. The top and bottom edges of the rectangle are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>. 
     * @param      x the <i>x</i> coordinate of the rectangle to be drawn.
     * @param      y the <i>y</i> coordinate of the rectangle to be drawn.
     * @param      width the width of the rectangle to be drawn.
     * @param      height the height of the rectangle to be drawn.
     * @param      arcWidth the horizontal diameter of the arc 
     *                    at the four corners.
     * @param      arcHeight the vertical diameter of the arc 
     *                    at the four corners.
     * @see        java.awt.Graphics#fillRoundRect
     */
    public void drawRoundRect(int x, int y, int width, int height,
               int arcWidth, int arcHeight) {}

    /** 
     * Fills the specified rounded corner rectangle with the current color.
     * The left and right edges of the rectangle 
     * are at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>, 
     * respectively. The top and bottom edges of the rectangle are at 
     * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. 
     * @param       x the <i>x</i> coordinate of the rectangle to be filled.
     * @param       y the <i>y</i> coordinate of the rectangle to be filled.
     * @param       width the width of the rectangle to be filled.
     * @param       height the height of the rectangle to be filled.
     * @param       arcWidth the horizontal diameter 
     *                     of the arc at the four corners.
     * @param       arcHeight the vertical diameter 
     *                     of the arc at the four corners.
     * @see         java.awt.Graphics#drawRoundRect
     */
    public void fillRoundRect(int x, int y, int width, int height,
               int arcWidth, int arcHeight) {
      drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }


    /**
     * Paints a 3-D highlighted rectangle filled with the current color.
     * The edges of the rectangle will be highlighted so that it appears
     * as if the edges were beveled and lit from the upper left corner.
     * The colors used for the highlighting effect will be determined from
     * the current color.
     * @param       x the <i>x</i> coordinate of the rectangle to be filled.
     * @param       y the <i>y</i> coordinate of the rectangle to be filled.
     * @param       width the width of the rectangle to be filled.
     * @param       height the height of the rectangle to be filled.
     * @param       raised a boolean value that determines whether the 
     *                      rectangle appears to be raised above the surface 
     *                      or etched into the surface.
     * @see         java.awt.Graphics#draw3DRect
     */
    public void draw3DRect(int x, int y, int width, int height,
         boolean raised) {
  Color c = getColor();
  Color brighter = c.brighter();
  Color darker = c.darker();

  setColor(raised ? brighter : darker);
  //drawLine(x, y, x, y + height);
  fillRect(x, y, 1, height + 1);
  //drawLine(x + 1, y, x + width - 1, y);
  fillRect(x + 1, y, width - 1, 1);
  setColor(raised ? darker : brighter);
  //drawLine(x + 1, y + height, x + width, y + height);
  fillRect(x + 1, y + height, width, 1);
  //drawLine(x + width, y, x + width, y + height - 1);
  fillRect(x + width, y, 1, height);
    }    

    /**
     * Paints a 3-D highlighted rectangle filled with the current color.
     * The edges of the rectangle are highlighted so that it appears
     * as if the edges were beveled and lit from the upper left corner.
     * The colors used for the highlighting effect and for filling are
     * determined from the current <code>Color</code>.  This method uses
     * the current <code>Color</code> exclusively and ignores the current 
     * <code>Paint</code>.
     * @param x,&nbsp;y the coordinates of the rectangle to be filled.
     * @param       width the width of the rectangle to be filled.
     * @param       height the height of the rectangle to be filled.
     * @param       raised a boolean value that determines whether the 
     *                      rectangle appears to be raised above the surface 
     *                      or etched into the surface.
     * @see         java.awt.Graphics#draw3DRect
     */
    public void fill3DRect(int x, int y, int width, int height,
         boolean raised) {
  Color c = getColor();
  Color brighter = c.brighter();
  Color darker = c.darker();

  if (!raised) {
      setColor(darker);
  } else {
      setColor(c);
  }
  fillRect(x+1, y+1, width-2, height-2);
  setColor(raised ? brighter : darker);
  //drawLine(x, y, x, y + height - 1);
  fillRect(x, y, 1, height);
  //drawLine(x + 1, y, x + width - 2, y);
  fillRect(x + 1, y, width - 2, 1);
  setColor(raised ? darker : brighter);
  //drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
  fillRect(x + 1, y + height - 1, width - 1, 1);
  //drawLine(x + width - 1, y, x + width - 1, y + height - 2);
  fillRect(x + width - 1, y, 1, height - 1);
    }    

    /** 
     * Draws the outline of an oval.
     * The result is a circle or ellipse that fits within the 
     * rectangle specified by the <code>x</code>, <code>y</code>, 
     * <code>width</code>, and <code>height</code> arguments. 
     * <p> 
     * The oval covers an area that is 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide 
     * and <code>height&nbsp;+&nbsp;1</code> pixels tall. 
     * @param       x the <i>x</i> coordinate of the upper left 
     *                     corner of the oval to be drawn.
     * @param       y the <i>y</i> coordinate of the upper left 
     *                     corner of the oval to be drawn.
     * @param       width the width of the oval to be drawn.
     * @param       height the height of the oval to be drawn.
     * @see         java.awt.Graphics#fillOval
     */
    public void drawOval(int x, int y, int width, int height) {
      // code a circle
      if (width == height) {
        code(0, "CIRCLE"); //$NON-NLS-1$
        codeCommon();
        code(10, this.x + x + width/2);
        code(20, this.y + y + height/2);
        code(30, 0);
        code(40, width/2);
      }
      
      // code an ellipse DXF2000
      else {
        code(0, "ELLIPSE"); //$NON-NLS-1$
        codeCommon();
        code(10, this.x + x + width/2);
        code(20, this.y + y + height/2);
        code(30, 0);
        
        if (width > height) {
          code(11, this.x + x + width);
          code(21, this.y + y + width/2);
          code(31, 0);
          code(40, ""+width/height); //$NON-NLS-1$
        }
        else {
          code(11, this.x + x + width/2);
          code(21, this.y + y + height);
          code(31, 0);
          code(40, ""+height/width); //$NON-NLS-1$
        }
        
        code(41, 0);            // start param
        code(42, ""+2*Math.PI); // end param //$NON-NLS-1$
      }
    }

    /** 
     * Fills an oval bounded by the specified rectangle with the
     * current color.
     * @param       x the <i>x</i> coordinate of the upper left corner 
     *                     of the oval to be filled.
     * @param       y the <i>y</i> coordinate of the upper left corner 
     *                     of the oval to be filled.
     * @param       width the width of the oval to be filled.
     * @param       height the height of the oval to be filled.
     * @see         java.awt.Graphics#drawOval
     */
    public void fillOval(int x, int y, int width, int height) {}

    /**
     * Draws the outline of a circular or elliptical arc 
     * covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends  
     * for <code>arcAngle</code> degrees, using the current color.
     * Angles are interpreted such that 0&nbsp;degrees 
     * is at the 3&nbsp;o'clock position. 
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin 
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the 
     * <code>width</code> and <code>height</code> arguments. 
     * <p>
     * The resulting arc covers an area 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * <p>
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the <i>x</i> coordinate of the 
     *                    upper-left corner of the arc to be drawn.
     * @param        y the <i>y</i>  coordinate of the 
     *                    upper-left corner of the arc to be drawn.
     * @param        width the width of the arc to be drawn.
     * @param        height the height of the arc to be drawn.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc, 
     *                    relative to the start angle.
     * @see         java.awt.Graphics#fillArc
     */
    public void drawArc(int x, int y, int width, int height,
         int startAngle, int arcAngle) {
      
      if (height == width) {
        code(0, "ARC"); //$NON-NLS-1$
        codeCommon();
        
        code(10, this.x + x + width/2);
        code(20, this.y + y + height/2);
        code(30, 0);
        code(40, width/2);
        code(50, startAngle);
        code(51, startAngle+arcAngle);
      }
    }

    /** 
     * Fills a circular or elliptical arc covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends  
     * for <code>arcAngle</code> degrees.
     * Angles are interpreted such that 0&nbsp;degrees 
     * is at the 3&nbsp;o'clock position. 
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin 
     * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the 
     * <code>width</code> and <code>height</code> arguments. 
     * <p>
     * The resulting arc covers an area 
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * <p>
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds.
     * @param        x the <i>x</i> coordinate of the 
     *                    upper-left corner of the arc to be filled.
     * @param        y the <i>y</i>  coordinate of the 
     *                    upper-left corner of the arc to be filled.
     * @param        width the width of the arc to be filled.
     * @param        height the height of the arc to be filled.
     * @param        startAngle the beginning angle.
     * @param        arcAngle the angular extent of the arc, 
     *                    relative to the start angle.
     * @see         java.awt.Graphics#drawArc
     */
    public void fillArc(int x, int y, int width, int height,
         int startAngle, int arcAngle) {
      drawArc(x, y, width, height, startAngle, arcAngle);
      /*
      code(0, "HATCH");
      codeCommon();
      code(70, 1);        // solid fill
      //code(71, 1);      // associative
      code(91, 1);        // number of boundary loops
      
      code(92, 0);      // boundary type 
      code(93, 2);      // number of edges
      
      code(72, 3);      // edge type (arc)
      code(10, this.x + x+width/2);
      code(20, this.y + y+height/2);
      code(11, this.x + width);
      code(21, this.y + height);
      code(40, height/width);
      code(50, startAngle);
      code(51, startAngle+arcAngle);
      code(73, 0);      // is counterclockwise?
      
      code(72, 1);      // edge type (line)
      code(10, this.x + x);
      code(20, this.y + y);
      code(11, this.x + x+width);
      code(21, this.y + y+height);
      */
    }

    /** 
     * Draws a sequence of connected lines defined by 
     * arrays of <i>x</i> and <i>y</i> coordinates. 
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * The figure is not closed if the first point 
     * differs from the last point.
     * @param       xPoints an array of <i>x</i> points
     * @param       yPoints an array of <i>y</i> points
     * @param       nPoints the total number of points
     * @see         java.awt.Graphics#drawPolygon(int[], int[], int)
     * @since       JDK1.1
     */
    public void drawPolyline(int xPoints[], int yPoints[],
              int nPoints) {
      codePLine(xPoints, yPoints, nPoints, false);
    }

    /** 
     * Draws a closed polygon defined by 
     * arrays of <i>x</i> and <i>y</i> coordinates. 
     * Each pair of (<i>x</i>,&nbsp;<i>y</i>) coordinates defines a point.
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line 
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code> 
     * line segments are line segments from 
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code> 
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.  
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * @param        xPoints   a an array of <code>x</code> coordinates.
     * @param        yPoints   a an array of <code>y</code> coordinates.
     * @param        nPoints   a the total number of points.
     * @see          java.awt.Graphics#fillPolygon
     * @see          java.awt.Graphics#drawPolyline
     */
    public void drawPolygon(int xPoints[], int yPoints[],
             int nPoints) {
      codePLine(xPoints, yPoints, nPoints, true);
    }


    private void codePLine(int xPoints[], int yPoints[],
              int nPoints, boolean closed) {
      code(0, "POLYLINE"); //$NON-NLS-1$
      codeCommon();
      
      code(66, 1);
      code(10, 0);
      code(20, 0);
      code(30, 0);
      code(70, closed? 1 : 0);    // closed or not
      
      for(int i = 0; i < nPoints; i++) {
        code(0, "VERTEX"); //$NON-NLS-1$
        code(8, getLayer());  // layer name
        code(10, x + xPoints[i]);
        code(20, y + yPoints[i]);
        code(30, 0);
      }
      
      code(0, "SEQEND"); //$NON-NLS-1$
      code(8, getLayer());
    }
    
    /** 
     * Draws the outline of a polygon defined by the specified 
     * <code>Polygon</code> object. 
     * @param        p the polygon to draw.
     * @see          java.awt.Graphics#fillPolygon
     * @see          java.awt.Graphics#drawPolyline
     */
    public void drawPolygon(Polygon p) {
      drawPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Fills a closed polygon defined by 
     * arrays of <i>x</i> and <i>y</i> coordinates. 
     * <p>
     * This method draws the polygon defined by <code>nPoint</code> line 
     * segments, where the first <code>nPoint&nbsp;-&nbsp;1</code> 
     * line segments are line segments from 
     * <code>(xPoints[i&nbsp;-&nbsp;1],&nbsp;yPoints[i&nbsp;-&nbsp;1])</code> 
     * to <code>(xPoints[i],&nbsp;yPoints[i])</code>, for 
     * 1&nbsp;&le;&nbsp;<i>i</i>&nbsp;&le;&nbsp;<code>nPoints</code>.  
     * The figure is automatically closed by drawing a line connecting
     * the final point to the first point, if those points are different.
     * <p>
     * The area inside the polygon is defined using an 
     * even-odd fill rule, also known as the alternating rule.
     * @param        xPoints   a an array of <code>x</code> coordinates.
     * @param        yPoints   a an array of <code>y</code> coordinates.
     * @param        nPoints   a the total number of points.
     * @see          java.awt.Graphics#drawPolygon(int[], int[], int)
     */
    public void fillPolygon(int xPoints[], int yPoints[],
             int nPoints) {
      if (nPoints == 4) {
        code(0, "SOLID"); //$NON-NLS-1$
        codeCommon();
        
        code(10, this.x + xPoints[0]);
        code(20, this.y + yPoints[0]);
        code(30, 0);
        
        code(11, this.x + xPoints[1]);
        code(21, this.y + yPoints[1]);
        code(31, 0);
        
        code(12, this.x + xPoints[3]);
        code(22, this.y + yPoints[3]);
        code(32, 0);
        
        code(13, this.x + xPoints[2]);
        code(23, this.y + yPoints[2]);
        code(33, 0);
      }
      else drawPolygon(xPoints, yPoints, nPoints);
      
      /*
      code(0, "HATCH");
      codeCommon();
      code(70, 1);        // solid fill
      //code(71, 1);        // associative
      code(91, nPoints);  // number of boundary loops
      
      for (int i = 0; i < nPoints; i++) {
        code(92, 0);      // boundary type
        code(93, 1);      // number of edges
        code(72, 1);      // edge type (line)
        code(10, x + xPoints[i]);
        code(20, y + yPoints[i]);
        code(11, x + xPoints[i]);
        code(21, y + yPoints[i]);
        
        // could be done by referencing the previous polygon
        // this is the better alternative, since the hatch will
        // be associative
        /*
        code(97, 1);      // 1 referenced boundary object
        code(330, "");    // handle number of the polygon
        
        
      }
      */
    }

    /** 
     * Fills the polygon defined by the specified Polygon object with
     * the graphics context's current color. 
     * <p>
     * The area inside the polygon is defined using an 
     * even-odd fill rule, also known as the alternating rule.
     * @param        p the polygon to fill.
     * @see          java.awt.Graphics#drawPolygon(int[], int[], int)
     */
    public void fillPolygon(Polygon p) {
      fillPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Draws the text given by the specified string, using this 
     * graphics context's current font and color. The baseline of the 
     * leftmost character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system. 
     * @param       str      the string to be drawn.
     * @param       x        the <i>x</i> coordinate.
     * @param       y        the <i>y</i> coordinate.
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawChars
     */
    public void drawString(String str, int x, int y) {
      code(0, "TEXT"); //$NON-NLS-1$
      codeCommon();
      code(10, this.x+x);     // x coord
      code(20, this.y+y);     // y coord
      code(30, 0);        // z coord
      //code(39, 0);      // thickness
      code(40, getFont().getSize());  // height
      code(1, str);     // the string itself
      //code(50, 0);      // rotation
      //code(41, 1);      // width scale rel to X
      //code(51, 0);      // oblique
      //code(7, "STANDARD");  // text style name
      //code(71, 0);      // backwards or upside down
      //code(72, 0);      // horizontal justification
      //code(73, 0);      // vertical justification
    }

    /** 
     * Draws the text given by the specified iterator, using this 
     * graphics context's current color. The iterator has to specify a font
     * for each character. The baseline of the 
     * leftmost character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system. 
     * @param       iterator the iterator whose text is to be drawn
     * @param       x        the <i>x</i> coordinate.
     * @param       y        the <i>y</i> coordinate.
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawChars
     */
   public void drawString(AttributedCharacterIterator iterator,
                                    int x, int y) {}

    /** 
     * Draws the text given by the specified character array, using this 
     * graphics context's current font and color. The baseline of the 
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system. 
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the <i>x</i> coordinate of the baseline of the text
     * @param y the <i>y</i> coordinate of the baseline of the text
     * @see         java.awt.Graphics#drawBytes
     * @see         java.awt.Graphics#drawString
     */
    public void drawChars(char data[], int offset, int length, int x, int y) {
      drawString(new String(data, offset, length), x, y);
    }

    /** 
     * Draws the text given by the specified byte array, using this 
     * graphics context's current font and color. The baseline of the 
     * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this 
     * graphics context's coordinate system.
     * @param data the data to be drawn
     * @param offset the start offset in the data
     * @param length the number of bytes that are drawn
     * @param x the <i>x</i> coordinate of the baseline of the text
     * @param y the <i>y</i> coordinate of the baseline of the text
     * @see         java.awt.Graphics#drawChars
     * @see         java.awt.Graphics#drawString
     */
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
      drawString(new String(data, 0, offset, length), x, y);
    }

    /** 
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at 
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate 
     * space. Transparent pixels in the image do not affect whatever 
     * pixels are already there. 
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered 
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * @param    img the specified image to be drawn.
     * @param    x   the <i>x</i> coordinate.
     * @param    y   the <i>y</i> coordinate.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @return   <code>true</code> if the image is completely loaded;
     *           <code>false</code> otherwise.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y, 
              ImageObserver observer) { return true; }

    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this 
     * graphics context's coordinate space, and is scaled if 
     * necessary. Transparent pixels do not affect whatever pixels
     * are already there. 
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the image observer by calling its <code>imageUpdate</code> method.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    width  the width of the rectangle.
     * @param    height the height of the rectangle.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @return   <code>true</code> if the current output representation
     *           is complete; <code>false</code> otherwise.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
              int width, int height, 
              ImageObserver observer) { return true; }
    
    /** 
     * Draws as much of the specified image as is currently available.
     * The image is drawn with its top-left corner at 
     * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate 
     * space.  Transparent pixels are drawn in the specified
     * background color.
     * <p> 
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * complete image has not yet been loaded, and it has not been dithered 
     * and converted for the current output device.
     * <p>
     * If the image has not yet been completely loaded, then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * @param    img    the specified image to be drawn.
     * @param    x      the <i>x</i> coordinate.
     * @param    y      the <i>y</i> coordinate.
     * @param    bgcolor the background color to paint under the
     *                         non-opaque portions of the image.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @return   <code>true</code> if the image is completely loaded;
     *           <code>false</code> otherwise.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y, 
              Color bgcolor,
              ImageObserver observer) { return true; }

    /**
     * Draws as much of the specified image as has already been scaled
     * to fit inside the specified rectangle.
     * <p>
     * The image is drawn inside the specified rectangle of this 
     * graphics context's coordinate space, and is scaled if 
     * necessary. Transparent pixels are drawn in the specified
     * background color. 
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * entire image has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * <p>
     * A scaled version of an image will not necessarily be
     * available immediately just because an unscaled version of the
     * image has been constructed for this output device.  Each size of
     * the image may be cached separately and generated from the original
     * data in a separate image production sequence.
     * @param    img       the specified image to be drawn.
     * @param    x         the <i>x</i> coordinate.
     * @param    y         the <i>y</i> coordinate.
     * @param    width     the width of the rectangle.
     * @param    height    the height of the rectangle.
     * @param    bgcolor   the background color to paint under the
     *                         non-opaque portions of the image.
     * @param    observer    object to be notified as more of 
     *                          the image is converted.
     * @return   <code>true</code> if the current output representation
     *           is complete; <code>false</code> otherwise.
     * @see      java.awt.Image
     * @see      java.awt.image.ImageObserver
     * @see      java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean drawImage(Image img, int x, int y,
              int width, int height, 
              Color bgcolor,
              ImageObserver observer) { return true; }
    
    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface. Transparent pixels 
     * do not affect whatever pixels are already there.
     * <p>
     * This method returns immediately in all cases, even if the
     * image area to be drawn has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * <p>
     * This method always uses the unscaled version of the image
     * to render the scaled rectangle and performs the required
     * scaling on the fly. It does not use a cached, scaled version
     * of the image for this operation. Scaling of the image from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle, and the second source coordinate is
     * mapped to the second destination coordinate. The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param       img the specified image to be drawn
     * @param       dx1 the <i>x</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dy1 the <i>y</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dx2 the <i>x</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       dy2 the <i>y</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       sx1 the <i>x</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sy1 the <i>y</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sx2 the <i>x</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       sy2 the <i>y</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       observer object to be notified as more of the image is
     *                    scaled and converted.
     * @return   <code>true</code> if the current output representation
     *           is complete; <code>false</code> otherwise.
     * @see         java.awt.Image
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since       JDK1.1
     */
    public boolean drawImage(Image img,
              int dx1, int dy1, int dx2, int dy2,
              int sx1, int sy1, int sx2, int sy2,
              ImageObserver observer) { return true; }

    /**
     * Draws as much of the specified area of the specified image as is
     * currently available, scaling it on the fly to fit inside the
     * specified area of the destination drawable surface. 
     * <p>
     * Transparent pixels are drawn in the specified background color. 
     * This operation is equivalent to filling a rectangle of the
     * width and height of the specified image with the given color and then
     * drawing the image on top of it, but possibly more efficient.
     * <p>
     * This method returns immediately in all cases, even if the
     * image area to be drawn has not yet been scaled, dithered, and converted
     * for the current output device.
     * If the current output representation is not yet complete then
     * <code>drawImage</code> returns <code>false</code>. As more of
     * the image becomes available, the process that draws the image notifies 
     * the specified image observer.
     * <p>
     * This method always uses the unscaled version of the image
     * to render the scaled rectangle and performs the required
     * scaling on the fly. It does not use a cached, scaled version
     * of the image for this operation. Scaling of the image from source
     * to destination is performed such that the first coordinate
     * of the source rectangle is mapped to the first coordinate of
     * the destination rectangle, and the second source coordinate is
     * mapped to the second destination coordinate. The subimage is
     * scaled and flipped as needed to preserve those mappings.
     * @param       img the specified image to be drawn
     * @param       dx1 the <i>x</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dy1 the <i>y</i> coordinate of the first corner of the
     *                    destination rectangle.
     * @param       dx2 the <i>x</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       dy2 the <i>y</i> coordinate of the second corner of the
     *                    destination rectangle.
     * @param       sx1 the <i>x</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sy1 the <i>y</i> coordinate of the first corner of the
     *                    source rectangle.
     * @param       sx2 the <i>x</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       sy2 the <i>y</i> coordinate of the second corner of the
     *                    source rectangle.
     * @param       bgcolor the background color to paint under the
     *                    non-opaque portions of the image.
     * @param       observer object to be notified as more of the image is
     *                    scaled and converted.
     * @return   <code>true</code> if the current output representation
     *           is complete; <code>false</code> otherwise.
     * @see         java.awt.Image
     * @see         java.awt.image.ImageObserver
     * @see         java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
     * @since       JDK1.1
     */
    public boolean drawImage(Image img,
              int dx1, int dy1, int dx2, int dy2,
              int sx1, int sy1, int sx2, int sy2,
              Color bgcolor,
              ImageObserver observer) { return true; }

    /**
     * Disposes of this graphics context and releases 
     * any system resources that it is using. 
     * A <code>Graphics</code> object cannot be used after 
     * <code>dispose</code>has been called.
     * <p>
     * When a Java program runs, a large number of <code>Graphics</code>
     * objects can be created within a short time frame.
     * Although the finalization process of the garbage collector 
     * also disposes of the same system resources, it is preferable 
     * to manually free the associated resources by calling this
     * method rather than to rely on a finalization process which 
     * may not run to completion for a long period of time.
     * <p>
     * Graphics objects which are provided as arguments to the 
     * <code>paint</code> and <code>update</code> methods 
     * of components are automatically released by the system when 
     * those methods return. For efficiency, programmers should
     * call <code>dispose</code> when finished using
     * a <code>Graphics</code> object only if it was created 
     * directly from a component or another <code>Graphics</code> object.
     * @see         java.awt.Graphics#finalize
     * @see         java.awt.Component#paint
     * @see         java.awt.Component#update
     * @see         java.awt.Component#getGraphics
     * @see         java.awt.Graphics#create
     */
    public void dispose() {}
    
    
    public static void main(String[] args) {
      
      OutputStream str = null;
      
      try {
        str = new java.io.BufferedOutputStream(
          new java.io.FileOutputStream("x.dxf")); //$NON-NLS-1$
      } catch (Exception e) { return; }
        
      DXFGraphics g = new DXFGraphics(str);
      /*
      g.drawRect(30, 30, 40, 50);
      g.fillPolygon(
        new int[] { 13, 50, 78, 45 },
        new int[] { 20, 70, 65, 30 },
        4
      );
      g.setColor(new Color(160, 50, 70));
      */
      /*
      g.drawPolyline(
        new int[] { 13, 50, 78, 45 },
        new int[] { 20, 70, 65, 30 },
        4
      );
      */
      /*
      g.drawString("Heyy!", 40, 40);
      g.drawLine(10, 20, 40, 50);
      g.drawOval(20, 20, 40, 40);
      g.setColor(Color.blue);
      g.fill3DRect(10, 10, 40, 40, true);
      g.setColor(Color.gray);
      g.draw3DRect(90, 90, 40, 40, true);
      g.drawString("Zdrawei", 50, 50);
      
      g.drawArc(100, 100, 100, 100, 0, 45);
      */
      javax.swing.JFrame p = new javax.swing.JFrame();
      p.setBounds(10, 10, 250, 200);
      javax.swing.JSlider sl = new javax.swing.JSlider(0, 100);
      sl.setPreferredSize(new java.awt.Dimension(100, 50));
      sl.setMajorTickSpacing(20);
      sl.setMinorTickSpacing(10);
      sl.setPaintTicks(true);
      sl.setPaintLabels(true);
      sl.setVisible(true);
      p.getContentPane().setLayout(new java.awt.FlowLayout());
      
      p.getContentPane().add(sl);
      
      javax.swing.JButton b = new javax.swing.JButton("Button"); //$NON-NLS-1$
      p.getContentPane().add(b);
      p.setVisible(true);
      
      //javax.swing.JColorChooser.showDialog(p, "", null);
      synchronized (g) {
        sl.paintAll(g);
        g.translate(100, 100);
        b.paintAll(g);
      }
      
      long start = System.currentTimeMillis();
      //while (System.currentTimeMillis() < start + 100000) {}
      g.close();
    }
}
