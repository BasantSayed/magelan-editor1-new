/*
*  The Magelan Project - Java Vector Graphics Editor Library
*  Copyright (C) 2003 Assen Antov and Larisa Feldman
*
*  This library is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This library is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*  See the GNU Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public
*  License along with this library; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-
*  1307  USA
*
*  For detailed information on this project please visit:
*  http://magelan.sourceforge.net/
*
*  Bug fixes should be submitted here:
*  https://sourceforge.net/tracker/?func=add&group_id=73971&atid=539550
*
*  Suggestions and comments should be sent to:
*  aantov@users.sourceforge.net
*  larisa@users.sourceforge.net
*/
package org.wayoda.magelan;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.magelan.editor.*;


/**
 * I need a togglebutton
 *
 * @author eberhard
 */
public class EditorToolBarToggleButton extends JToggleButton {
	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates a new ToolBarToggleButton object.
	 *
	 * @param command EditorCommand
	 */
	public EditorToolBarToggleButton(EditorCommand command) {
		super(command);
		setText(""); //$NON-NLS-1$

		setMargin(new Insets(1, 1, 1, 1));
		setFocusPainted(false);
		setRolloverEnabled(true);
		setBorderPainted(false);
		
		/*
		 * Rollover if added to a panel.
		 */
		addMouseListener(new MouseAdapter(){
		    public void mouseEntered(MouseEvent e) {
			    if (isRolloverEnabled() && isEnabled()) {
			    	setBorderPainted(true);
			    }
		    }
    		public void mouseExited(MouseEvent e) {
    			if (isRolloverEnabled() && isEnabled()) {
					setBorderPainted(false);
				}
			 }	
			public void mouseClicked(MouseEvent e) {
			}
		});
	}
	
	public void setEnabled(boolean b) {
		setBorderPainted(false);
		super.setEnabled(b);
	}
}
