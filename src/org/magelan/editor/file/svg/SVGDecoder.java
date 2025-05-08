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

import java.awt.Frame;
import java.awt.geom.PathIterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.magelan.core.CoreModel;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.entity.PathEntity;
import org.magelan.core.entity.PathMsg;
import org.magelan.core.entity.PointEntity;
import org.magelan.editor.Editor;

/**
 * Reads a SVG file into a <code>CoreModel</code> instance.
 * 
 * <p></p>
 *
 * @author Bernard  Desprez
 * @version 1.0, sept/2003
 */
public class SVGDecoder {
       final boolean dbg = true;
//   final boolean dbg = false;
         boolean first = true;
	
	/**
         * For the time beeing this class method will handle only the path
         * element all the others will be ignored. Perhaps latter
         * we will code more  or by using Xerces we will go further...
	 * <p>
         * @param in  input stream to read SVG data from
	 * @param d   drawing to add entities to
	 * <p>
	 * @throws  IOException 
	 */
      Frame f= null;
      PathEntity paths = null;
      double lX=0.0F;
      double lY=0.0F;
// move 0 line 1 quad 2 cubic 3 close 4
    public   final byte SEG_MOVETO  = (byte) PathIterator.SEG_MOVETO;
    public   final byte SEG_LINETO  = (byte) PathIterator.SEG_LINETO;
    public   final byte SEG_QUADTO  = (byte) PathIterator.SEG_QUADTO;
    public   final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
    public   final byte SEG_CLOSE   = (byte) PathIterator.SEG_CLOSE;
       

        /**
         * @method decode
         *
         */

        public   void decode(InputStream inst, CoreModel d, DrawingEditor editor)
         throws IOException {
        
if(dbg) System.out.println("reading SVG "); //$NON-NLS-1$
       f = Editor.getEditorManager().getMainFrame();

       String path = ""; //$NON-NLS-1$
       try {
       BufferedReader in = new BufferedReader(new InputStreamReader(inst));
       paths = new PathEntity(editor);
       paths.setCross(false);
       paths.setCenter(false);
       d.add(paths);
       String st = null;
       for(;;) {
          st = in.readLine();

          if(st == null) break;
          st = st.trim();
          if(!st.startsWith("<path")) continue; //$NON-NLS-1$

          for(;;) {
             int i =st.indexOf("/>"); //$NON-NLS-1$
             if(i != -1) {
               i = st.lastIndexOf("\""); //$NON-NLS-1$
               int p = st.indexOf("d="); //$NON-NLS-1$
               if(p == -1 ) {
                   (new PathMsg(f)).showMessage(5,true);
                   return; // error
               }
               path =st.substring(p+3,i);
               handleP(path);
               break;
             }
             String st1 = in.readLine();
             if(st1 == null) break;
             st1 = st1.trim();
             st  += st1;
          } // till />
       } // end forever
       in.close();
       } catch(Exception e) {
           System.err.println(e);
       }
    } // end read

     /**
      * @method handleP
      *
      */

     private  void handleP(String path) {
if(dbg) System.out.println("handlePath "+path); //$NON-NLS-1$
        String previous= ""; // or insult by javac //$NON-NLS-1$
        String commands = " ,Mm-LlCcZzSsHhVvQqTtAa"; //$NON-NLS-1$
        StringTokenizer st = new StringTokenizer(path, commands , true);
        int sign  = 1;
        while (st.hasMoreTokens()) {
          String tok = st.nextToken();
          tok = tok.trim();
          if(tok.equals("")) continue; //$NON-NLS-1$
   if(dbg)  System.out.println(tok);
          if(tok.equals("M") || tok.equals("m") || tok.equals("L")  || tok.equals("l") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
          || tok.equals("H") || tok.equals("h") || tok.equals("V")  || tok.equals("v") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
          || tok.equals("C") || tok.equals("c") || tok.equals("S") || tok.equals("s") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
          || tok.equals("Q") || tok.equals("q") || tok.equals("T") || tok.equals("t") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
          || tok.equals("A") || tok.equals("a") ){ //$NON-NLS-1$ //$NON-NLS-2$
             processToken(tok,null,st,sign);
             previous = tok;
             sign = 1;
          }
          else if(tok.equals("Z") || tok.equals("z")) { //$NON-NLS-1$ //$NON-NLS-2$
          }   // a revoir
          else if(tok.equals(",")) { //$NON-NLS-1$
          }
          else if(tok.equals("-")) { //$NON-NLS-1$
             sign = -1;
          }
          else {  // numeric or invalid format
             // after M or m default is L or l
             if(previous.equals("M")) previous = "L"; //$NON-NLS-1$ //$NON-NLS-2$
             else if(previous.equals("m")) previous = "l"; //$NON-NLS-1$ //$NON-NLS-2$
if(dbg) System.out.println("token "+tok+" previous "+previous); //$NON-NLS-1$ //$NON-NLS-2$
             if(tok != null) processToken(previous,tok,st,sign);
             sign = 1;
          }
        } // end while

    } // end handlepath

     /**
      * @method processToken
      */

     private  void processToken(String tok,String tokN,StringTokenizer st ,int sign) {
          int cb;
  if(dbg) System.out.println(tok+"="+tokN+"-"+sign); //$NON-NLS-1$ //$NON-NLS-2$
          if(sign == -1 && tokN != null) tokN = "-"+tokN; //$NON-NLS-1$
          sign = 1;
          PointEntity LastP = null;
          PointEntity BLastP= null;
          if(!first)  {
             LastP = paths.getLast();
          }
          double x=0;
          double y=0;
          if(first && tok.equals("m") ){ //$NON-NLS-1$
           tok = "M"; //$NON-NLS-1$
          }
          if(tok.equals("M") || tok.equals("m") ) {   //(x y) + //$NON-NLS-1$ //$NON-NLS-2$
             if(first) {
                try {
                   if(tokN != null) {
                      x = ( new Float(tokN)).doubleValue();
                      y = ( new Float(getNextToken(st))).doubleValue();
                   }
                   else {
                      x = ( new Float(getNextToken(st))).doubleValue();
                      y = ( new Float(getNextToken(st))).doubleValue();
                   }
                } catch (NumberFormatException e) {
                     System.err.println(e);
                     System.exit(0);
                }
                paths.addPoint(x,y);
                paths.addCommand(SEG_MOVETO);
                lX =x;
                lY =y;
                first = false;
             }
             else {
                paths.addCommand(SEG_MOVETO);
                if(tok.equals("M")) { //$NON-NLS-1$
                  if(tokN != null) setPoint(tokN,getNextToken(st),false,true);
                  else setPoint(getNextToken(st),getNextToken(st),false,true);
                }
                else {
                  if(tokN != null) setPoint(tokN,getNextToken(st),true,true);
                  else setPoint(getNextToken(st),getNextToken(st),true,true);
                }
                
             }
          }
          else if(tok.equals("L") ) { // (x y)+ //$NON-NLS-1$
             paths.addCommand(SEG_LINETO);
             if(tokN != null) setPoint(tokN,getNextToken(st),false,true);
             else setPoint(getNextToken(st),getNextToken(st),false,true);
          }
          else if(tok.equals("l")) { // (x y)+ //$NON-NLS-1$
             paths.addCommand(SEG_LINETO);
             if(tokN != null) setPoint(tokN,getNextToken(st),true,true);
             else setPoint(getNextToken(st),getNextToken(st),true,true);
          }
          else if(tok.equals("H")|| tok.equals("h")) { // x+ //$NON-NLS-1$ //$NON-NLS-2$
             x = nextValue(tokN, st);
             if(x!= 0) paths.addCommand(SEG_LINETO);
             if(tok.equals("H")) lX=x; //$NON-NLS-1$
             else lX +=x;
             paths.addPoint(lX,LastP.getY());
          }
          else if(tok.equals("V")|| tok.equals("v")) { // y+ //$NON-NLS-1$ //$NON-NLS-2$
             y = nextValue(tokN, st);
             if(y != 0) paths.addCommand(SEG_LINETO);
             if(tok.equals("V")) lY=y; //$NON-NLS-1$
             else lY +=y;
             paths.addPoint(LastP.getX(),lY);

          }

          // Cc (curveto)  (x1 y1 x2 y2 x y)+
          else if(tok.equals("C") || tok.equals("c")) { //$NON-NLS-1$ //$NON-NLS-2$
             boolean rel = true;
             paths.addCommand(SEG_CUBICTO);
              if(tok.equals("C")) rel = false; //$NON-NLS-1$
             //x1 y1
             if(tokN != null) setPoint(tokN,getNextToken(st),rel,false);
             else setPoint(getNextToken(st),getNextToken(st),rel,false);
             //x2 y2
             setPoint(getNextToken(st),getNextToken(st),rel,false);
             //x  y
             setPoint(getNextToken(st),getNextToken(st),rel,true);

          }
/*
          //Ss (shorthand/smooth curveto)  (x2 y2 x y)+
          else if(tok.equals("S") || tok.equals("s")) {
              if(tok.equals("S"))
                 cb = SVGCode.SCURVETO|SVGCode.ABSOLUTE;
              else
                 cb = SVGCode.SCURVETO|SVGCode.RELATIVE;
             //x2
             if(tokN != null)
                svgc.outValue(getNextToken(st),cb);
             else
                svgc.outValue(getNextToken(st),cb);
             //y2
             svgc.outValue(getNextToken(st),cb);
             //x
             svgc.outValue(getNextToken(st),cb);
             //y
             svgc.outValue(getNextToken(st),cb);

          }
          else if(tok.equals("A") || tok.equals("a")) {
          // Aa (elliptical arc) (rx ry x-axis-rotation large-arc-flag sweep-flag x y)+
              if(tok.equals("A"))
                 cb = SVGCode.ELLIPARC|SVGCode.ABSOLUTE;
              else
                 cb = SVGCode.ELLIPARC|SVGCode.RELATIVE;
             //rx
             if(tokN != null)
                svgc.outValue(tokN,cb);
             else
                svgc.outValue(getNextToken(st),cb);
             //ry
             svgc.outValue(getNextToken(st),cb);
             // x-axis-rotation
             svgc.outValue(getNextToken(st),cb);
             // large-arc-flag
             String flag = getNextToken(st);
             if(flag.equals("0")) svgc.out(SVGCode.ATTR_FALSE);
             else svgc.out(SVGCode.ATTR_TRUE);
             // sweep-flag
             flag = getNextToken(st);
             if(flag.equals("0")) svgc.out(SVGCode.ATTR_FALSE);
             else svgc.out(SVGCode.ATTR_TRUE);
             //x
             svgc.outValue(getNextToken(st),cb);
             //y
             svgc.outValue(getNextToken(st),cb);
          }
          else if(tok.equals("Q") || tok.equals("q")) {
          // Qq (quadratic bezier curveto)  (x1 y1 x y)+
              if(tok.equals("Q"))
                 cb = SVGCode.QBCURVETO|SVGCode.ABSOLUTE;
              else
                 cb = SVGCode.QBCURVETO|SVGCode.RELATIVE;
             //x1
             if(tokN != null)
                svgc.outValue(tokN,cb);
             else
                svgc.outValue(getNextToken(st),cb);
             //y1
             svgc.outValue(getNextToken(st),cb);
             //x
             svgc.outValue(getNextToken(st),cb);
             //y
             svgc.outValue(getNextToken(st),cb);
          }
          // Tt (shorthand/smooth quadric bezier curve) (x y)+
          else if(tok.equals("T") || tok.equals("t")) {
              if(tok.equals("Q"))
                 cb = SVGCode.SQBCURVETO|SVGCode.ABSOLUTE;
              else
                 cb = SVGCode.SQBCURVETO|SVGCode.RELATIVE;
               //x
             if(tokN != null)
                svgc.outValue(tokN,cb);
             else
               svgc.outValue(getNextToken(st),cb);
               //y
               svgc.outValue(getNextToken(st),cb);

          }*/
          else {
        System.out.println("-"+tok+"- pathdata not yet coded !!!"); //$NON-NLS-1$ //$NON-NLS-2$
          }
     }

     /**
      * @method getNextToken
      *
      */

     private String getNextToken(StringTokenizer st) {
        String sign = ""; //$NON-NLS-1$
        while(st.hasMoreTokens()) {
           String tok = st.nextToken();
           if( tok.equals("-")) { //$NON-NLS-1$
             sign = "-"; //$NON-NLS-1$
           }
           else if(!tok.equals(" ") && !tok.equals(",") && !tok.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  if(dbg) System.out.println("nextToken _"+tok+"_"+sign); //$NON-NLS-1$ //$NON-NLS-2$
              return sign+tok;
           }
        }
        return null;
     }

     /**
      * @method nextValue
      *
      */

     private double nextValue(String tokN,StringTokenizer st) {
        double val=0;
        String test = null;
        try {
         if(tokN != null) {
             val = (new Float(tokN)).doubleValue();
         }
         else {
             test = getNextToken(st);
             val = (new Float(test)).doubleValue();
         }
       } catch(Exception e) {
           System.out.println("nextValue "+tokN+" "+test); //$NON-NLS-1$ //$NON-NLS-2$
           System.out.println(e);
           
         }
       return val;
     }

     /**
      * @method setPoint
      *
      * @parm boolean type true if relative false if absolute.
      * @parm boolean last true if last point of a curve.
      */

     private   void setPoint(String x,String y,boolean type,boolean last) {
if(dbg) System.out.println("setPoint "+x+" "+y+"type "+type+" "+lX+" "+lY); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            float X = 0.0F;
            float Y = 0.0F;

            try {
              X =(new Float(x)).floatValue();
              Y =(new Float(y)).floatValue();
              if(type) {
                 X += lX;
                 Y += lY;
              }
         } catch (NumberFormatException e) {
             System.err.println(e);
             (new PathMsg(f)).showMessage(5,true);
         }
if(dbg) System.out.println("Point "+X+" "+Y); //$NON-NLS-1$ //$NON-NLS-2$
         paths.addPoint((double)X,(double)Y);
         if(last) {
            lX = X;
            lY = Y;
if(dbg) System.out.println("new lX "+lX+"new lY "+lY); //$NON-NLS-1$ //$NON-NLS-2$

         }
	 
        } 
}
