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
package org.magelan.editor.commands;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.editor.*;
import org.magelan.surfer.DataSurfer;
import org.magelan.surfer.DataTree;
import org.magelan.surfer.HelpSurfer;
import org.magelan.surfer.resources.help.HelpItem;

/**
 *
 * @author Assen Antov
 * @version 2.0, 02/2004
 */
public class Help extends EditorCommand {

	private static Lang lang = Lang.getLang(Editor.STRINGS);

	private DataSurfer help;

	public Help() {
		super();
		putValue(Action.NAME, lang.getString("common.help")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Help.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Help.descr.long")); //$NON-NLS-1$

		putValue(Action.SMALL_ICON, Icons.HELP_ICON);
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
		putValue(
			Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
	}

	public void run(org.magelan.core.editor.DrawingEditor e) {
		if (help == null) {
			help = new HelpSurfer();
			//help.setBounds(0, 0, 700, 500);
			SwingUtil.center(help);

			// data tree
			help.addTree("Help", org.magelan.surfer.DataTree.load("Help.xml")); //$NON-NLS-1$ //$NON-NLS-2$

			// extensions tree
			DefaultMutableTreeNode root = DataTree.createNode("Extensions");

			IExtensionsManager extMgr =
				Editor.getExtensionsManager();
			Iterator iter = extMgr.getExtensions().iterator();
			while (iter.hasNext()) {
				// collect extension information
				AbstractEditorExtension ext =
					(AbstractEditorExtension) iter.next();
				ClassManifest cmf = extMgr.getManifestFor(ext);
				
				// construct help item
				if (cmf != null) {
					HelpItem item =
						new HelpItem(
							(String) ext.getValue(AbstractEditorExtension.NAME),
							(String) ext.getValue(
								AbstractEditorExtension.SHORT_DESCRIPTION),
								cmf.getHelpPath());
					DefaultMutableTreeNode node = DataTree.createNode(item);
					root.add(node);
				}
			}

			// data tree
			DataTree et = new DataTree(root);
			help.addTree("Extensions", et);
		}

		SwingUtilities.updateComponentTreeUI(help);
		help.setVisible(true);
	}
}