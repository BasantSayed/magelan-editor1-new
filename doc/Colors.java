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
package org.magelan.editor.extension;

import org.magelan.core.*;
import org.magelan.core.editor.*;
import org.magelan.core.utils.*;
import org.magelan.editor.*;

import java.awt.*;
import java.util.*;
import javax.swing.*;


/**
 * Change the current color if no selection, or the color of the  entities
 * selected. Almost the same as the actual Colors extension
 * <p>
 * @author Assen Antov
 * @version 1.0 demo, 07/2003
 */
public class Colors extends AbstractEditorExtension {

	/**
	 * Constructs the extension.
	 */
	public Colors() {
		super();
		putValue(Action.NAME, "Colors");
		putValue(AbstractEditorExtension.VERSION, "1.0 demo, 07/2003");
		putValue(AbstractEditorExtension.AUTHOR, "aantov@users.sourceforge.net");
		putValue(Action.SHORT_DESCRIPTION, "Change Color");
		putValue(Action.LONG_DESCRIPTION, "Change the color of the selected entities");
		putValue(Action.SMALL_ICON, org.magelan.editor.utils.Icons.COLORS_ICON);
		putValue(AbstractEditorExtension.GROUP, "Modify");
	}

	/**
	 * Main method of the extension.
	 * <p>
	 * @param	e	currently selected editor
	 */
	public void run(DrawingEditor e) {
		// check if there is an editor selected for safety
		if (e == null) return;
		
		// obtain the drawing being edited
		Drawing dwg = e.getDrawing();
		
		// get the selected entities
		java.util.List selection = e.getSelection();

		// choose new color
		Color color = JColorChooser.showDialog(e, "Choose Current Color", dwg.getColor());

		// if no selection change current drawing color
		if (selection.size() == 0) {
			dwg.setColor(color);
		}
		
		// if selected entitites, change their colors
		else {
			Iterator elements = selection.iterator();
			while (elements.hasNext())
				((Entity) elements.next()).setColor(color);
		}
		
		// repaint the drawing
		e.paint();
	}
}