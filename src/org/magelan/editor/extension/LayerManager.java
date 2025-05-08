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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.SwingUtil;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.CoreModel;
import org.magelan.core.DefaultCoreModel;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.ui.LayerManagerPanel;


/**
 *
 * @author Assen Antov
 * @version 2.0, 05/2004
 */
public class LayerManager extends AbstractEditorExtension {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private LayerManagerDialog dialog;


	/**
	 * Creates a new LayerManager object.
	 */
	public LayerManager() {
		putValue(Action.NAME, "Layer Control"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "2.0, 05/2004"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("LayerManager.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("LayerManager.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.LAYERS_ICON);
		putValue(AbstractEditorExtension.GROUP, lang.getString("common.modify")); //$NON-NLS-1$
	}


	public void run(DrawingEditor e) {
		if (dialog == null) {
			dialog = new LayerManagerDialog(Editor.getEditorManager().getMainFrame());
		}
		
		dialog.setModel((CoreModel) e.getModel());
		
		SwingUtilities.updateComponentTreeUI(dialog);
		dialog.setVisible(true);
		e.repaint();
	}



	private class LayerManagerDialog extends JDialog {

		private LayerManagerPanel layerPanel;
		private JButton okButton;
		
		private LayerManagerDialog(Frame parent) {
			super(parent);
			construct();
		}
		
		private void construct() {
			setTitle(lang.getString("LayerManager.title")); //$NON-NLS-1$
			getContentPane().setLayout(new BorderLayout());
			setModal(true);
			
			/*
			 * Setup a banner
			 */
			JLabel title = UIFactory.createBanner(lang.getString("LayerManager.banner"), Icons.LAYERS_ICON); //$NON-NLS-1$
			getContentPane().add(title, BorderLayout.NORTH);

			/*
			 * Layer view panel
			 */
			layerPanel = new LayerManagerPanel();
			getContentPane().add(layerPanel, BorderLayout.CENTER);
			
			/*
			 * Setup button panel
			 */
			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			okButton = new JButton(new OKCommand());
			buttons.add(okButton);
			
			getContentPane().add(buttons, BorderLayout.SOUTH);
			
			getRootPane().setDefaultButton(okButton);
			//pack();
			setBounds(0, 0, 430, 350);
			SwingUtil.center(this);
		}
		
		private void setModel(CoreModel model) {
			layerPanel.setModel(model);
		}
		
		/**
		 * OK command.
		 */
		private class OKCommand extends AbstractAction {
			public OKCommand() {
				super();
				putValue(Action.NAME, lang.getString("common.close")); //$NON-NLS-1$
				putValue(Action.SMALL_ICON, Icons.OK_ICON);
	//			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
				putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			}
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}
	
	}


	public static void main(String[] args) {
		LayerManagerDialog f = new LayerManager().new LayerManagerDialog(null);
		f.setModel(new DefaultCoreModel());
		f.setVisible(true);
	}
}