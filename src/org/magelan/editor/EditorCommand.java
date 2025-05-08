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
package org.magelan.editor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.OptionDialog;
import org.magelan.core.editor.DrawingEditor;

/**
 * Abstract Command that can be used in menus and toolbars. 
 * 
 * @version	2.0, 08/2003
 * @author	larisa
 */
public abstract class EditorCommand extends AbstractAction {

	public static String COMPATIBLE = "compatible"; //$NON-NLS-1$
	public static String AVAILABLE = "available"; //$NON-NLS-1$
	public static String AVAILABLE_ALWAYS = "always"; //$NON-NLS-1$
	public static String AVAILABLE_ALONE = "alone"; //$NON-NLS-1$
	
	private static Logger log = Logger.getLogger(AbstractEditorExtension.class);
	private static Lang lang = Lang.getLang(Editor.STRINGS);

	/**
	 * Construct command.
	 */
	public EditorCommand() {
		putValue(COMPATIBLE, null);
		putValue(AVAILABLE, AVAILABLE_ALWAYS);
	}

	/**
	 * Redirects an action to the command - specific execute implementation.
	 * The execute implementation is run on a new thread, rather than on the
	 * current event dispatch thread.
	 *
	 * @param	e	the action event to handle
	 */
	public void actionPerformed(ActionEvent e) {
		IExtensionsManager em = Editor.getExtensionsManager();
		
		String av = (String) getValue(AVAILABLE);
		if (av == null || av.equals(AVAILABLE_ALONE)) {
			if (em.isBusy()) {
				OptionDialog.showInfoDialog(Editor.getEditorManager().getMainFrame(), lang.getString("EditorCommand.msg.1")); //$NON-NLS-1$
				return;
			}
		}
		
		if (getValue(COMPATIBLE) != null) {
			DrawingEditor de = Editor.getEditorManager().getSelectedEditor();
			
			if (de == null) {
				OptionDialog.showInfoDialog(Editor.getEditorManager().getMainFrame(), lang.getString("EditorCommand.msg.2")); //$NON-NLS-1$
				return;
			}
			
			if (!((Class) getValue(COMPATIBLE)).isInstance(de.getModel())) {
				OptionDialog.showInfoDialog(Editor.getEditorManager().getMainFrame(), lang.getString("EditorCommand.msg.3")); //$NON-NLS-1$
				return;
			}
		}
		
		log.info("Executing: " + getClass().getName()); //$NON-NLS-1$
		
		(new Thread(new Runnable() {
			public void run() {
				try{
					IExtensionsManager em = Editor.getExtensionsManager();
					synchronized (em) {
						em.setBusy(true);
					}
					
					EditorCommand.this.setEnabled(false);
					EditorCommand.this.run(Editor.getEditorManager().getSelectedEditor());
					EditorCommand.this.setEnabled(true);
					
					synchronized (em) {
						em.setBusy(false);
					}
				} catch (Throwable ex) {
					IExtensionsManager em = Editor.getExtensionsManager();
					synchronized (em) {
						em.setBusy(false);
					}
					EditorCommand.this.setEnabled(true);
					log.error("unexpected error executing editor command", ex); //$NON-NLS-1$
					org.magelan.commons.ui.ErrorDialog.showErrorDialog(
							lang.getString("EditorCommand.error") + getClass().getName(), ex); //$NON-NLS-1$
				}
			}
		})).start();
	}

	/**
	 * Command specific execute.
	 *
	 * @param e host drawing editor
	 */
	public abstract void run(DrawingEditor e);

	/**
	 * Returns a custom GUI component to represent this command. Most commands
	 * need only to be represented by buttons and menu items but some may need
	 * to provide extended functionality via a custom component. Such commands
	 * will not be listed in the menus but in toolbars or elsewere, depending 
	 * on their type.
	 * 
	 * @return	a custom command component
	 * @see	DefaultExtensionManager
	 */
	public java.awt.Component getComponent() {
		return null;
	}
}