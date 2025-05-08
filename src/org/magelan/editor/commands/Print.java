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

import java.awt.JobAttributes;
import java.awt.PageAttributes;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.ui.ErrorDialog;
import org.magelan.commons.ui.Icons;
import org.magelan.editor.Editor;
import org.magelan.editor.EditorCommand;
import org.magelan.editor.IEditorManager;

/**
 *
 * @author Assen Antov
 * @version 1.0, 08/2006
 */
public class Print extends EditorCommand {
	
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	private static Logger log = Logger.getLogger(Print.class);
	
	/**
	 * Creates a new Exit object.
	 */
	public Print() {
		super();
		putValue(Action.NAME, lang.getString("common.print")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Print.descr.short")); //$NON-NLS-1$
		putValue(Action.LONG_DESCRIPTION, lang.getString("Print.descr.long")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Icons.PRINT_ICON);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
	}

	public void run(org.magelan.core.editor.DrawingEditor e) {
		IEditorManager em = Editor.getEditorManager();
		
		JobAttributes ja = new JobAttributes();
		ja.setMaxPage(1);
		
		PageAttributes pa = new PageAttributes();
		pa.setColor(PageAttributes.ColorType.COLOR);
		pa.setPrintQuality(5);
		pa.setMedia(PageAttributes.MediaType.A4);
		
		PageFormat pf = new PageFormat();
		
		PrinterJob pj = PrinterJob.getPrinterJob();

		/*
		PrintRequestAttributeSet as = new PrintRequestAttributeSet();
		as.add(ja);
		as.add(pa);
		*/
		
		if (pj.printDialog()) {
			try {
				pj.setPrintable(em.getSelectedEditor(), pf);
				pj.print();
			} catch (PrinterException e1) {
				ErrorDialog.showErrorDialog("Error printing", e1);
			}
		}
	}
}