/* This File is part of the wayoda-project                                    */
/* Filename : MemMonitor.java                                                 */
/* Copyright (C) <2003>  <Eberhard Fahle>
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA  */

package org.wayoda.magelan;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JToolTip;
import javax.swing.UIManager;

import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.event.EditorFrameEvent;
import org.wayoda.resources.MemoryMonitorIcon;
import org.wayoda.system.MemoryDisplay;
import org.wayoda.system.MemoryMonitor;
import org.wayoda.system.MemoryToolTipComponent;

/**
 * A MemoryMonitor implemented as an AbstractEditorExtension.
 * <p>
 * This is intended as an extenions to be used by magelan. 
 * It adds a (toggle-)button to the toolbar that opens 
 * a window where the current memory usage is displayed graphically.
 * The button in the toolbar also displays a special tooltip-component
 * where numerical values for the memory-usage are painted. 
 * <p>
 * @author Eberhard Fahle
 * @version 0.0
 */
public class MemMonitor extends AbstractEditorExtension {
		/** The monitor for the memory */
		private MemoryMonitor mm;
		/** tooltip on the button */
		private MemoryToolTipComponent mttc;
		/** The component shown in the dialog */
		private MemoryDisplay md;
		/** window for the graph */
		private JDialog memDisplayDialog;
		/** This will be the title of the graph-window */
		private String memDisplayDialogTitle;
		/** Special button for EditorToolBar */
		private EditorToolBarToggleButton tb;

		/**
		 * Creates the new Extension.
		 */
		public MemMonitor() {
				super();
				//i18n the command
				try {
						//config this for magelan
						ResourceBundle rb=ResourceBundle.getBundle("org.wayoda.magelan.MemMonitorResources"); //$NON-NLS-1$
						putValue(Action.NAME,rb.getString("action.name")); //$NON-NLS-1$
						putValue(AbstractEditorExtension.VERSION,rb.getString("action.version")); //$NON-NLS-1$
						putValue(AbstractEditorExtension.AUTHOR,rb.getString("action.author")); //$NON-NLS-1$
						putValue(Action.SHORT_DESCRIPTION,rb.getString("action.description.short")); //$NON-NLS-1$
						putValue(Action.LONG_DESCRIPTION,rb.getString("action.description.long")); //$NON-NLS-1$
						putValue(Action.SMALL_ICON, new MemoryMonitorIcon());
						putValue(AbstractEditorExtension.GROUP,rb.getString("action.group")); //$NON-NLS-1$
						//putValue(AbstractEditorExtension.ENABLE_POLICY,AbstractEditorExtension.ENABLE_ALWAYS);
						//read a title for the dialog and remember it until needed
						rb=ResourceBundle.getBundle("org.wayoda.system.MemoryMonitorResources"); //$NON-NLS-1$
						memDisplayDialogTitle=rb.getString("memorydisplay.label.header"); //$NON-NLS-1$
				}
				catch(MissingResourceException mre) {
						//should never happen, but until this goes to the logger we ...
						System.out.println(mre);
				}
				mttc=new MemoryToolTipComponent();
				mm=new MemoryMonitor(1,64);
				mm.addValueListener(mttc);
				//as there is no ToolBarToggleButton in magelan I implemented one
				tb=new EditorToolBarToggleButton(this) {
								public JToolTip createToolTip() {
										//we need to update the colors for the tooltip 
										//depending on the enabled state of our button
										//safe for LookAndFeelChanges too!
										if(isEnabled()) {
												mttc.setForeground(UIManager.getColor("ToolTip.foreground")); //$NON-NLS-1$
												mttc.setBackground(UIManager.getColor("ToolTip.background")); //$NON-NLS-1$
										}
										else {
												mttc.setForeground(UIManager.getColor("ToolTip.foregroundInactive")); //$NON-NLS-1$
												mttc.setBackground(UIManager.getColor("ToolTip.backgroundInactive")); //$NON-NLS-1$
										}
										//return our custom tooltip-component
										return mttc;
								}
								
						}; 
				tb.addItemListener(new ItemListener() {
								public void itemStateChanged(ItemEvent ie) {
										if(ie.getStateChange()==ItemEvent.SELECTED) {
												//here we could do something like runCommand()...
										}
										else if (ie.getStateChange()==ItemEvent.DESELECTED) {
												//... and this would be stopCommand()
										}
								}
						});
				Editor.getEditorManager().addEditorFrameListener(new EditorFrameAdapter() {
								public void editorFrameClosing(EditorFrameEvent efe) {
										if(memDisplayDialog!=null) {
												memDisplayDialog.setVisible(false);
												memDisplayDialog.dispose();
												memDisplayDialog=null;	//dispose seems not enough to release the display ?
										}			
										if(mm!=null) {
												mm.removeValueListener(md);
												md=null;
										}
										tb.setSelected(false);
								}
						});
		}
		
		/**
		 * This method gets called every time our ToggleButton changes it state.
		 * As a hack I have a look wether the button is selected or not, but I rather
		 * want something like runCommand()/stopCommand() to be called automaticly.
		 * @param	e	currently selected editor (I don't need one, as I'm not interested in drawings here )
		 */
		public void run(DrawingEditor e) {
				// We don't manipulate any drawings so this next line is commented out from example
				//if (e == null) return;
				if(memDisplayDialog==null) {	//there is no dialog yet
						md=null;
						memDisplayDialog=new JDialog();
						memDisplayDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
						memDisplayDialog.setTitle("Magelan : "+memDisplayDialogTitle); //$NON-NLS-1$
						md=new MemoryDisplay();
						mm.addValueListener(md);
						memDisplayDialog.getContentPane().add(md);
						memDisplayDialog.pack();
						memDisplayDialog.setLocationRelativeTo(tb);
						//in case the user closes the window we deselect our ToggleButton
						memDisplayDialog.addWindowListener(new WindowAdapter() {
										public void windowClosing(WindowEvent we) {
												tb.setSelected(false);
										}
								});
				}
				memDisplayDialog.setVisible(tb.isSelected());
		}

		/**
		 * Returns the ToolBarToggleButton so it can be added to the 
		 * toolbars.
		 * @return Component the Component I want to be added to the Toolbars
		 */
		public Component getComponent() {
				return tb;
		}

}



