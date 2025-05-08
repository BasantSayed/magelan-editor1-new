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
package org.magelan.editor.commands;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.UIFactory;
import org.magelan.commons.ui.log.LogItem;
import org.magelan.commons.ui.log.LogView;
import org.magelan.commons.ui.log.Logger;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;


/**
 * Active assistance palette.
 *
 * @author Assen Antov
 * @version 1.0, 01/2006
 */
public class AssistancePalette extends AbstractEditorExtension {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private String[] fields = new String[] {LogItem.KEY_LEVEL, LogItem.KEY_MSG/*, LogItem.KEY_SRC*/};
	private Logger l;
	
	private JPanel control;
	private JTable table;
	private DrawingEditor editor;
	private JToolBar toolbar;


	public AssistancePalette() {
		super();

		putValue(Action.NAME, "Assistance"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, 01/2006"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.AUTHOR, "Assen Antov"); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("AssistancePalette.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("AssistancePalette.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.HELP_ICON);
 		putValue(AbstractEditorExtension.GROUP, lang.getString("common.help")); //$NON-NLS-1$
	}


	public Component getComponent() {
		if (control == null) {
			control = new JPanel();
			control.setLayout(new BorderLayout());
			control.setPreferredSize(new Dimension(200, 350));
			control.setMinimumSize(new Dimension(200, 350));
			
			JPanel north = new JPanel();
			north.setLayout(new BorderLayout());
		
			toolbar = new JToolBar();
			toolbar.setBorderPainted(false);
			toolbar.setFloatable(false);
			toolbar.setRollover(true);
			toolbar.setBorder(new LineBorder(toolbar.getBackground().darker()));
			
			toolbar.add(UIFactory.createButton(new DeleteAllCommand()));
			north.add(toolbar, BorderLayout.CENTER);
			control.add(north, BorderLayout.NORTH);
			
			// assistance table 
			l = Editor.getEditorManager().getAssistanceLog();
			table = new LogView.Table(l, fields);
			JScrollPane scroll = UIFactory.createScrollPane(table);
			control.add(scroll, BorderLayout.CENTER);
		}

		return control;
	}

	
	public void run(DrawingEditor e) {
	}

	/**
	 * DeleteAll command.
	 */
	private class DeleteAllCommand extends AbstractAction {
		public DeleteAllCommand() {
			super();
			putValue(Action.NAME, lang.getString("AssistancePalette.deleteAll")); //$NON-NLS-1$
			putValue(Action.SMALL_ICON, Icons.DELETE_ICON);
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}
		public void actionPerformed(ActionEvent e) {
			l.clear();
		}
	}
}