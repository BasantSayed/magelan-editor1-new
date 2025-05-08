/*
 * The Magelan Project - Vector Graphics Library and Editor
 * Copyright (c) 2003-2006, Assen Antov and Larisa Feldman. All rights reserved.
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
package org.magelan.editor.commands;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.magelan.commons.Lang;
import org.magelan.core.Entity;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.style.Layer;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.Editor;

/**
 * 
 * @version	1.0, 07/2006
 * @author	Assen Antov
 */
public class Internal {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	public static class SendToBack extends AbstractAction {
		private Layer layer;
		
		public SendToBack(Layer l) {
			super();
			this.layer = l;

			putValue(Action.NAME, lang.getString("internal.sendtoback.name")); //$NON-NLS-1$
			//putValue(Action.SHORT_DESCRIPTION, lang.getString("internal.sendtoback.descr.short")); //$NON-NLS-1$
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK));
			
		}
		
		public void actionPerformed(ActionEvent e) {
			DrawingEditor editor = Editor.getEditorManager().getSelectedEditor();
			if (editor == null) return;
			DrawingModel model = editor.getModel();
			List elements = model.getElements();
			List sel = new LinkedList();
			
			Iterator iter = elements.iterator();
			while (iter.hasNext()) {
				Object el = iter.next();
				if (el instanceof Entity && layer.equals(((Entity) el).getLayer())) {
					sel.add(el);
				}
			}
			
			elements.removeAll(sel);
			elements.addAll(0, sel);
			editor.repaint();
		}
	}
	
	public static class BringToFront extends AbstractAction {
		private Layer layer;
		
		public BringToFront(Layer l) {
			super();
			this.layer = l;

			putValue(Action.NAME, lang.getString("internal.bringtofront.name")); //$NON-NLS-1$
			//putValue(Action.SHORT_DESCRIPTION, lang.getString("internal.bringtofront.descr.short")); //$NON-NLS-1$
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK));
			
		}
		
		public void actionPerformed(ActionEvent e) {
			DrawingEditor editor = Editor.getEditorManager().getSelectedEditor();
			if (editor == null) return;
			DrawingModel model = editor.getModel();
			List elements = model.getElements();
			List sel = new LinkedList();
			
			Iterator iter = elements.iterator();
			while (iter.hasNext()) {
				Object el = iter.next();
				if (el instanceof Entity && layer.equals(((Entity) el).getLayer())) {
					sel.add(el);
				}
			}
			
			elements.removeAll(sel);
			elements.addAll(elements.size()-1, sel);
			editor.repaint();
		}
	}
}
