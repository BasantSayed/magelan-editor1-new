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


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * <p></p>
 *
 * @author Bernard Desprez
 * @version 1.0, 08/2003
 */
public class PathMenu extends JPopupMenu implements ActionListener {
    private final boolean dbg=true;
    ImageIcon[] icons = {
      new ImageIcon("/org/magelan/images/pmove.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/pinsert.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/parc.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/pcurve.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/pclose.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/porigin.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/ppath.gif"), //$NON-NLS-1$
      new ImageIcon("/org/magelan/images/preturn.gif"), //$NON-NLS-1$
    };
    String[] Tips = {
        "move_to_point", //$NON-NLS-1$
        "insert_a_point", //$NON-NLS-1$
        "insert_an_arc", //$NON-NLS-1$
        "insert_a_curve", //$NON-NLS-1$
        "close_path", //$NON-NLS-1$
        "CoreModel_orign", //$NON-NLS-1$
        "New_Path", //$NON-NLS-1$
        "ignore_request" //$NON-NLS-1$
    };
    svgc.CallBackable handler;
    public Frame f;

    PathMenu(Frame f, svgc.CallBackable handler) {
        super();
        this.handler = handler;
        this.f =f;
        JMenuItem item;
        for(int i =0; i <icons.length; i++ ) {
           item = new JMenuItem(icons[i]);
           item.setToolTipText(Tips[i]);
           item.addActionListener(this);
           add(item);
        }
    }


    public int getHeight() {
        int h =0;
        for(int i =0; i <icons.length; i++ ) {
//          h +=(icons[i].getSize()).height;
        }
        return h;
    }

/*    public int getButton(JButton source) {
        for(int i=0; i <myButtons.length; i++) {
           if(myButtons[i] == source) return i;
        }
        return -1;
    } */

    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
    }

    /**
     *@method actionPerformed
     *@param ActionEvent
     */

    public void actionPerformed(ActionEvent evt) {

               Object source = evt.getSource();
//               int b = getButton((JButton)source);
//               handler.activateCallBack(b,evt);
    }
}

