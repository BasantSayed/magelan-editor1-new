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
package org.magelan.editor.extension;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.PathIterator;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.magelan.commons.ui.Icons;
import org.magelan.core.CoreModel;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.entity.PathEntity;
import org.magelan.core.entity.PointEntity;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.Viewport;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.ui.EditorPopupMenu;

/**
 * <p></p>
 *
 * @author Bernard Desprez
 * @version 1.0, 09/2003
 *
 * @see EditorPopupMenu
 */
public class NewPath extends AbstractEditorExtension
        implements MouseListener, MouseMotionListener {

        //~ Instance fields --------------------------------------------------------

// move 0 line 1 quad 2 cubic 3 close 4
        public  final byte SEG_MOVETO  = (byte) PathIterator.SEG_MOVETO;
        public  final byte SEG_LINETO  = (byte) PathIterator.SEG_LINETO;
        public  final byte SEG_QUADTO  = (byte) PathIterator.SEG_QUADTO;
        public  final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
        public  final byte SEG_CLOSE   = (byte) PathIterator.SEG_CLOSE;

        private final boolean dbg=false;
        private DrawingEditor editor;
        private int index=-1;
        private String name= "";
        private PathEntity pathEntity = null;
        private EditorPopupMenu PM;
        private CoreModel dwg = null;
        private Viewport viewport = null;
        private Frame f = null;
        private String[] icons = {
            "image",
            "pmove",
            "pinsert",
            "parc",
            "pcurve",
            "pclose",
            "porigin",
            "ppath",
            "preturn"
        };
        private PointEntity menu=null;
        private int indic = -1;  // current command
        private int nbPts = 0;

        private String[] messages={
           "Can not Insert inside a curve",
           "First insert a starting point",
           "Close already done",
           "Close what ?? "
        };

        //~ Constructors -----------------------------------------------------------

        /**
         * Creates a new NewPath object.
         */
        public NewPath() {
                super();

                putValue(Action.NAME, "Path");
                putValue(AbstractEditorExtension.VERSION, "1.0, 09/2003");
                putValue(AbstractEditorExtension.AUTHOR, "Bernard Desprez");
                putValue(Action.SHORT_DESCRIPTION, "Create new path");
                putValue(Action.LONG_DESCRIPTION, "Create New Path in Selected Editor");
                putValue(Action.SMALL_ICON,
                                 Icons.getImageIcon("pathE"));
                putValue(AbstractEditorExtension.GROUP, "Draw");
   if(dbg) System.out.println("NewPath constructor");

        }

        //~ Methods ----------------------------------------------------------------

        public void run(DrawingEditor e) {
           if(dbg) System.out.println("run "+e);

           if (e == null) return;
           editor = e;
           editor.listenMouseEvents(false);
           editor.getContainer().addMouseListener(this);
           editor.getContainer().addMouseMotionListener(this);
           dwg = (CoreModel) editor.getModel();

           if(PM == null) {
              Component compo = e.getEnclosingScrollPane();
              while(compo.getParent() != null) {
                 compo = compo.getParent();
              }

              f = (Frame)compo;
              PM = new EditorPopupMenu();
             JMenuItem item;
              for(int i =0; i <icons.length; i++ ) {
                 item = new JMenuItem(Icons.getImageIcon(icons[i]));
                 item.setText(icons[i]);
                 item.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent evt) {
                        handleEvent(evt);
                   }
                 } ); // end ActionListener
                 PM.add(item);
              } // for
             PM.pack();
           } //PM null
           // the user would like to modify a existing path
            Vector epaths = new Vector();
            java.util.List li = dwg.getElements();
            if(li != null) {
               Iterator iter = dwg.getElements().iterator();
               while(iter.hasNext()) {
                  Object obe =(Object) iter.next();
                  if(obe instanceof org.magelan.core.entity.PathEntity) {
                    epaths.addElement(obe);
                    //System.out.println("old "+((PathEntity)obe).getID());
                  }
               }
               if(epaths.size() != 0) {
                  (new PathSelection(epaths)).setVisible(true);
                  System.out.println("index "+index+"name "+name+"-");
               }
            }
            if(name == null) name="";
            if(index < 1) {
               if(name.equals("")) {
                 int i = epaths.size();
                 i++;
                 name = "path"+(new Integer(i)).toString();
                 System.out.println("new "+name+"-");
               }
            }

        }

        /**
         * @method mouseClicked
         *
         * @param e MouseEvent
         */

        public void mouseClicked(MouseEvent evt) {
                if(pathEntity != null) {
                   if(pathEntity.isMsg()) {  // clear message if any
                       pathEntity.setMsg(0);
                       return;
                   }
                }
                viewport = dwg.getViewport();
 if(dbg) System.out.println("\r\nmouseClicked "+dwg+"\r\n"+viewport);
                // event coordinates in drawing units
                double x = (evt.getX() - viewport.getTransX()) / viewport.getScaleX();
                double y = (evt.getY() - viewport.getTransY()) / viewport.getScaleY();
 if(dbg) System.out.println("x y "+evt.getX()+" "+evt.getY());
                if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
                  menu = new PointEntity(x,y);
                  showMenu(evt);
                  return;
                }
                if((evt.getModifiers() & Event.CTRL_MASK) != 0) {
                  if(pathEntity != null) pathEntity.setSelect();
                  return;
                }
                if((evt.getModifiers() & Event.ALT_MASK) != 0) {
                  System.out.println("alt mask");
                }
                if((evt.getModifiers() & Event.SHIFT_MASK) != 0) {
                if(dbg) System.out.println("shift click");
                  if(pathEntity != null) pathEntity.deleteCurp();
                  return;
                }

                if (evt.getClickCount() >= 2) {
  if(dbg) System.out.println("dble click received");
//                    remove(current);
                }

                if (pathEntity == null) {
                        pathEntity = new PathEntity(editor);
   System.out.println(" new path "+name);
                        //pathEntity.setID(name);
                        dwg.add(pathEntity);
                        pathEntity.addPoint(x,y); //MoveTo
                        pathEntity.addCommand(SEG_MOVETO);

                }
                else {
                   if(indic == -1) pathEntity.addLine(x,y);
                   if(indic == SEG_MOVETO){
                       pathEntity.addPoint(x,y);
                       pathEntity.addCommand(SEG_MOVETO);

                   }
                   if(indic > SEG_MOVETO) {
                      pathEntity.addPoint(x,y);
                      nbPts++;
                      if(dbg) System.out.println("indic "+indic);
                      switch(nbPts) {
                         case 2:
                           if(indic == SEG_QUADTO) {
                              pathEntity.addCommand(SEG_QUADTO);
                              indic=-1;
                              nbPts=0;
                           }
                           break;
                         case 3:
                           pathEntity.addCommand(SEG_CUBICTO);
                           indic=-1;
                           nbPts=0;
                           break;
                         default:
                           break;
                      } //switch
                   }
                }
                editor.repaint();

                evt.consume();
        }

        /**
         * @method mouseMoved
         *
         * @param e DOCME!
         */
        public void mouseMoved(MouseEvent e) {
                DrawingModel dwg = editor.getModel();
                Viewport viewport = dwg.getViewport();

                // event coordinates in drawing units
                double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
                double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

                if (pathEntity == null) {
                        editor.getContainer().setToolTipText("" + x + ": " + y);
                        return;
                }
                pathEntity.isPointed(x,y);
/*                if(pathEntity.isPointed(x, y)) {
                  editor.paint();
                  System.out.println("Pointed");
                } */

/*                if(redraw != 0) {
                  editor.paint();
                  redraw=0;
                 */
        }

        /**
         * @method mousePressed
         *
         * @param e DOCME!
         */
        public void mousePressed(MouseEvent e) {
        }

        /**                                                                      /**
         * @method mouseReleased
         *
         * @param e DOCME!
         */
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * @method mouseEntered
         *
         * @param e DOCME!
         */
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * @method mouseExited
         *
         * @param e DOCME!
         */
        public void mouseExited(MouseEvent e) {
        }

        /**
         * @method mouseDragged
         *
         * @param e DOCME!
         */
        public void mouseDragged(MouseEvent e) {
           if(pathEntity == null) return;
           if(!pathEntity.isSelect()) return;
           dwg = (CoreModel) editor.getModel();
           viewport = dwg.getViewport();
           double x = (e.getX() - viewport.getTransX()) / viewport.getScaleX();
           double y = (e.getY() - viewport.getTransY()) / viewport.getScaleY();

           pathEntity.setCurp(x,y);
//            System.out.println("mouseDragged");
        }


        /**
         *@method showMenu
         *
         * When the user click the right button a menu is shown
         * at the nearest place where the cursor is.
         *
         *@param MouseEvent
         */


        private void showMenu(MouseEvent e) {
if(dbg) System.out.println("showMenu ");
          int x = e.getX();
          int y = e.getY();
          Dimension r = PM.getPreferredSize();
          Dimension r1 = e.getComponent().getSize();
          int compWidth = r1.width;
          int compHeight = r1.height;
          if((x+r.width) > compWidth) x-= r.width;
          if((y+r.height) > compHeight) y -= r.height;
          PM.show((Component)e.getComponent(),x,y);


        }

        /**
         *@method handleEvent
         *
         *@param ActionEvent
         */

        public Object handleEvent(ActionEvent evt) {
//if(dbg) System.out.println("handle "+evt);
        JMenuItem src = (JMenuItem) evt.getSource();
        
        String icon = src.getText();
        int b =0;
        for(;b<icons.length;b++) {
          if(icons[b].equals(icon) ) break;
        }
if(dbg) System.out.println(b+" icon "+icon);
        if(pathEntity == null) {
          pathEntity = new PathEntity(editor);
         // pathEntity.setID(name);
          dwg.add(pathEntity);
        }

        if(b==0) {
           File model = getFile();
           if(model == null) return null;  // ignore
           pathEntity.setCalque(model);
           editor.repaint();
        }
        if(b== 1) {
           pathEntity.addPoint(menu);
           pathEntity.addCommand(SEG_MOVETO);
           editor.repaint();
        }
        if(b==3) indic=SEG_QUADTO;
        else if(b==4) indic=SEG_CUBICTO;
        else if(b==5)  pathEntity.addCommand(SEG_CLOSE);
        else if(b==8) {  //end
                 pathEntity.setCross(false);
                 pathEntity.setCenter(false);
                 pathEntity = null;
                 editor.getContainer().removeMouseListener(this);
                 editor.getContainer().removeMouseMotionListener(this);
                 editor.listenMouseEvents(true);
                 editor.repaint();
                 editor.getContainer().setToolTipText(null);

             }
/*               if( b == 5) { // set origin
                  pzx = px;
                  pzy = py;
                  paintOffscr();
                  repaint();
if(dbg) System.out.println("set origin "+pzx+" "+pzy);
                  return;
               }
if(dbg) System.out.println("action at "+px+" "+py);
               current = find(px*zoom,py*zoom);
if(dbg) System.out.println("action "+b+" current "+current+" curp "+curp+" curtype "+curtype);
//      System.out.println("action "+b+" current "+current+" curp "+curp+" curtype "+curtype);
// check insert point
               if((byte)curtype == SEG_QUADTO){
                  if(current == curp+1) {
                    message(19);
                    return;
                  }
               }
               else if((byte)curtype == SEG_CUBICTO){
                  if((current == curp+1) || (current == curp+2)){
                    message(19);
                    return;
                  }
               }
               if( b == 0) { // move to
if(dbg) System.out.println("moveto "+((px-pzx)*zoom)+" "+((py-pzy)*zoom));
               add((px)*zoom,(py)*zoom,new Byte(SEG_MOVETO));
                 paintOffscr();
                 repaint();
                 closeFlag = 0;
              }
              else if( b == 1) { // insert point
                 for(int i=0; i< squares.size(); i++) {
                    Byte tb = (Byte)squares.elementAt(i);
                    byte t  = tb.byteValue();
                    px -= pzx;
                    py -= pzy;
                    if(t == SEG_CLOSE) {
if(dbg) System.out.println("Insert "+i);
                       Point squar0 = (Point)squares.elementAt(i-1);
                       i++;
                       Integer I1 = (Integer)squares.elementAt(i);
                       int p = I1.intValue();
                       Point squar1 = (Point)squares.elementAt(p);
                        if(contains(squar0,squar1) ) {
                           squares.insertElementAt(new Point(px,py),i-1);
                           squares.insertElementAt(new Byte(SEG_LINETO),i-1);
// dump();
                          break;
                       }
                      continue;
                   }
                   else if( t == SEG_QUADTO) {
                      i+=2;
                      continue;
                   }
                   else if( t == SEG_CUBICTO) {
                      i+=3;
                      continue;
                   }
                   else if( t == SEG_MOVETO || t == SEG_LINETO) {
                       i++;
                       Point squart1 = (Point)squares.elementAt(i);
                       Byte tb2 = (Byte)squares.elementAt(i+1);
                       byte t2  = tb2.byteValue();
                       Point squart2 = null;
                       if( t2 != SEG_CLOSE ) {
                           squart2 = (Point)squares.elementAt(i+2);
                       }
                       else {
if(dbg)  System.out.println("error 481 "+t2);
                          continue;
                       }
                       if(contains(squart1,squart2) ) {
                          squares.insertElementAt(new Point(px,py),i+1);
                          squares.insertElementAt(new Byte(SEG_LINETO),i+1);
//dump();
                          break;
                       }
                    } // insert a point on a line
                } // End for
                paintOffscr();
                repaint();
             }
             else if( b == 2) { // insert an arc
                if(curtype == -1) {
                  pathEntity.setMsg(1);
                  return;
                }
                quad(1);
             }
             else if( b == 3) { // insert a curve
                if(curtype == -1) {
                  message(20);
                  return;
                }
                cubic(1);
             }
             else if( b == 4) { // close   not right???
                closePath();
             }
             else if( b == 6) { // new path
                squares = new Vector();
                paths.addElement(squares);
                add((px)*zoom,(py)*zoom,new Byte(SEG_MOVETO));
                paintOffscr();
                repaint();
                closeFlag = 0;
             }
             else if( b == 7) { // ignore
               trace = 1;
             }

*/
      return null;
      }
/*
        public String getName() {
                return "Binary CoreModel File Handler";
        }

        public String getTypeDescription() {
                return "Binary Coded Basic CoreModel";
        }

        public ImageIcon getIcon() {
                return Icons.DRAWING_ICON;
        }

        public FileFilter getFileFilter() {
    return new DefaultFileFilter("bdrawing", "Binary Coded Basic Drawings [*.bdrawing]");
        }

        public boolean isSupported(File f) {
                String ext = "";

                if (f != null) {
                        String filename = f.getName();
                        int i = filename.lastIndexOf('.');

                        if (i > 0 && i < filename.length() - 1) {
                                ext = filename.substring(i + 1).toLowerCase();
                        }
                }

                if ("bdrawing".equals(ext)) {
                        return true;
                } else {
                        return false;
                }
        }*/
        // TBD file selection

        public File getFile() {
           FileDialog fd = new FileDialog(f,"image",FileDialog.LOAD);
           fd.setVisible(true);
           return new File(fd.getDirectory(),fd.getFile());
        }

//**********************************************************************

        /**
         * The user must have the possibility to recall a previous or
         * loaded path for modifications
        */

   public class PathSelection extends JDialog implements ActionListener{
        //~ Instance fields --------------------------------------------------------
            JComboBox list;
            Vector epaths;
        //~ Constructors -----------------------------------------------------------
        public PathSelection(Vector epaths) {
           this.epaths = epaths;
           setTitle("new/select path");
           setModal(true);
           getContentPane().setLayout(new FlowLayout());
           list = new JComboBox();
           list.addItem("");
           Enumeration en = epaths.elements();
           while(en.hasMoreElements()) {
              PathEntity pe = (PathEntity)en.nextElement();
//              System.out.println("list "+pe.getID());
              list.addItem(pe);
           }
           list.setEditable(true);
           list.addActionListener(this);
           getContentPane().add(list);
           setSize(148,68);
           center(this);
        }

        //~ Methods ----------------------------------------------------------------
        /**
         * @method actionPerformed
         *
         * @parm ActionEvent
        */

        public void actionPerformed(ActionEvent e) {
          index = list.getSelectedIndex();
          index --;
          name =list.getSelectedItem().toString();
         // Enumeration en = epaths.elements();
          pathEntity = (PathEntity)epaths.elementAt(index);
          pathEntity.setCross(true);
          pathEntity.setCenter(true);
          this.dispose();
          editor.repaint();

        }

        /**
         * Centers the component on screen.
         *
         * <p></p>
         *
         * @param c the component to center
         */

        public void center(Component c) {
           Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
           c.setLocation(screenSize.width / 2 - c.getSize().width / 2,
                        screenSize.height / 2 - c.getSize().height / 2);
        }

   }
}
