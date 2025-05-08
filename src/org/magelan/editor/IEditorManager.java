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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;

import org.magelan.commons.ui.log.Logger;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.markup.SelectionListener;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.EditorMenu;
import org.magelan.editor.ui.EditorToolBar;
import org.magelan.editor.ui.StatusBar;


/**
 * Editor user interface. Provides access to editor's components and defines 
 * methods to create, open, save drawings.
 * 
 * @author larisa
 * @author Assen Antov
 * @version 2.1, 01/2006
 */
public interface IEditorManager {

	/**
	 * Returns the selected editor. May return <code>null</code> if there is no
	 * selected or open editor. The drawing contained in the editor can be
	 * obrained using its {$link DrawingEditor#getModel} method.
	 *
	 * @return the currently selected editor or <code>null</code> if no such
	 */
	public DrawingEditor getSelectedEditor();
	
	/**
	 * Returns an array of the currently open editors. Can return an empty array
	 * if there are no editors open.
	 *
	 * @return the currently opened editors
	 */
	public DrawingEditor[] getEditors();
	

	/**
	 * Sets the selected editor. 
	 *
	 * @param editor editor to become selected
	 */
	public void setSelectedEditor(DrawingEditor editor);

	/**
	 * Closes the selected editor. This manager may display a dialog asking whether
	 * to save the editor or not.
	 * 
	 * @return <code>false</code> if the user has cancelled the operation; 
	 * 		<code>true</code> otherwise
	 */
	public boolean closeSelectedEditor();

	/**
	 * Closes the specified editor in multiframe editor managers. This manager may 
	 * display a dialog asking whether to save the editor or not.
	 *
	 * @param editor editor to be closed
	 * @return <code>false</code> if the user has cancelled the operation; 
	 * 		<code>true</code> otherwise
	 */
	public boolean closeEditor(DrawingEditor editor);

	/**
	 * Returns the status bar.
	 * 
	 * @return editor status bar
	 * @see StatusBar
	 */
	public StatusBar getStatusBar();
	
	/**
	 * Returns the name of the file that corresponds to the drawing contained
	 * by the parameter editor.
	 * 
	 * @param	ed	editor to lookup
	 * @return	drawing file name
	 */
	public String getFileNameFor(DrawingEditor ed);
	
	/**
	 * Returns the icon representing the drawing contained
	 * by the parameter editor.
	 * 
	 * @param	ed	editor to lookup
	 * @return	drawing icon
	 */
	public ImageIcon getIconFor(DrawingEditor ed);
	
	/**
	 * Sets the name of the file that corresponds to the drawing contained
	 * by the parameter editor.
	 * 
	 * @param	name	drawing file name
	 * @param	ed	editor instance
	 */
	public void setFileNameFor(String name, DrawingEditor ed);
	
	/**
	 * Returns the icon representing the drawing contained
	 * by the parameter editor.
	 * 
	 * @param	name	file name
	 * @param	ed	editor instance
	 */
	public void setIconFor(ImageIcon icon, DrawingEditor ed);
	
	/**
	 * Constructs and initializes the editor manager.
	 */
	public void initialize();
	
	/**
	 * Safely exits and hides the editor but <strong>does not destroy it</strong>.
	 * The editor can be shown again using the <code>getMainFrame().show()</code>
	 * construct. The editor cannot be re-initialized using 
	 * <code>initialize()</code> after <code>exit()</code>. 
	 * <p>
	 * This method provides the 'softest' exit - finalizes work with the editor 
	 * and hides it without destroying it. 
	 * </p>
	 * The user will be prompted to save any drawings open. At that point the user
	 * may cancel the operation. In this case the method will return 
	 * <code>false</code>.
	 *
	 * @return <code>false</code> if the user has cancelled the operation; 
	 * 		<code>true</code> otherwise
	 * @see	Editor.shutdown()
	 */
	public boolean exit();

	/**
	 * Opens a new editor. Obtains the default template from the available
	 * <code>ITemplatesManager</code> and opens its corresponding
	 * <code>DrawingEditor</code>.
	 *
	 * @see ITemplatesManager#getDefaultTemplate()
	 * @see DrawingTemplate
	 */
	public void openNewDefaultEditor();

	/**
	 * Opens a new editor. Typically the editor manager will display a dialog
	 * with a list of all available  <code>DrawingTemplate</code>s and open
	 * the editor for the template chosen by the user.
	 *
	 * @see DrawingTemplate
	 */
	public void openNewEditor();

	/**
	 * Opens the specified editor.
	 *
	 * @param editor editor to open
	 */
	public void openEditor(DrawingEditor editor);

	/**
	 * Reads the parameter file, wraps it in the proper <code>DrawingEditor</code>
	 * and returns it. If the <code>file</code> parameter is <code>null</code>
	 * the user will be asked to select a file to be read. The method will return 
	 * <code>null</code> on error. In this case an error notification dialog will be 
	 * displayed to the user. The drawing read will not be opened in the editor. The 
	 * caller should invoke the {@link #openEditor(DrawingEditor)} method, if the 
	 * drawing must be shown. This method is similar to
	 * {@link IFilesManager#decode(File)}.
	 * 
	 * @param file drawing file name to read 
	 * @return a <code>DrawingEditor</code> containing the drawing read or 
	 * 		<code>null</code> on failure
	 * @since 2.0
	 */
	public DrawingEditor readDrawing(String file);
	
	/**
	 * Saves the drawing wrapped in the parameter editor to a file with the given
	 * name. The method is similar to invoking 
	 * {@link IFilesManager#encode(DrawingModel, DrawingRenderer, File)}. The user 
	 * will <em>not</em> be prompted, if a file with the same name already exists.
	 *  
	 * @param editor a <code>DrawingEditor</code> containing the drawing to save 
	 * @param file name to save the drawing under
	 * @return {@link IFilesManager#SUCCESS}, {@link IFilesManager#ERROR_WRITING} or
	 * 		{@link IFilesManager#NOT_SUPPORTED}
	 * @sicne 2.0
	 */
	public int saveDrawing(DrawingEditor editor, String file);
	
	/**
	 * Saves the drawing wrapped in the parameter editor to a file. The method will 
	 * display a dialog asking for a file name. If the <code>file</code> parameter 
	 * is not <code>null</code>, the name contained will be recommended to the user. 
	 * The user will be prompted, if a file with the name chosen already exists. 
	 * The method is similar to invoking 
	 * {@link IFilesManager#encode(DrawingModel, DrawingRenderer, File)}.
	 *  
	 * @param editor a <code>DrawingEditor</code> containing the drawing to save 
	 * @param file recommended name to save the drawing under; may be <code>null</code>
	 * @return a <code>String</code> with the file name the drawing was saved under 
	 * 		on success; or {@link IFilesManager#ERROR_WRITING} or {@link IFilesManager#NOT_SUPPORTED},
	 * 		wrapped as an <code>Integer</code>; <code>null</code> if the user cancelled
	 * 		the operation
	 * @sicne 2.0
	 */
	public Object saveDrawingAs(DrawingEditor editor, String file);
	
	/**
	 * Editor's main frame.
	 *
	 * @return the main frame of the editor manager
	 */
	public JFrame getMainFrame();

	/**
	 * Editor's main menu bar. 
	 *
	 * @return the pull-down menu bar of the editor manager
	 */
	public JMenuBar getJMenuBar();

	/**
	 * Editor's toolbar.
	 *
	 * @return the toolbar containing editor's commands
	 *
	 * @see EditorCommand
	 * @see	EditorToolBar
	 */
	public EditorToolBar getEditorToolbar();

	/**
	 * The extensions toolbar.
	 *
	 * @return the toolbar containing editor's extensions
	 *
	 * @see AbstractEditorExtension
	 * @see	EditorToolBar
	 */
	public EditorToolBar getExtensionToolbar();
	
	/**
	 * The extensions tabbed pane. If an extension provides a custom GUI
	 * component via their {@link EditorCommand#getComponent()} method 
	 * the <code>IExtensionsManager</code> will check the class of the 
	 * component and may decide to place it here.
	 *
	 * @return the toolbar containing editor's extensions
	 *
	 * @see AbstractEditorExtension
	 * @see	DefaultExtensionManager
	 */
	public JTabbedPane getExtensionTab();
	
	/**
	 * @return	editor extensions pull-down menu
	 */
	public EditorMenu getExtensionMenu();

	
	/*
	 * Frame/Window equivalents
	 */
	
	/**
	 * Adds the specified listener to receive editor frame events.
	 *
	 * @param l the editor frame listener
	 */
	public void addEditorFrameListener(EditorFrameListener l);

	/**
	 * Removes the specified editor frame listener so that it no longer
	 * receives editor frame events.
	 *
	 * @param l the editor frame listener
	 */
	public void removeEditorFrameListener(EditorFrameListener l);

	/**
	 * Returns an array of all the <code>EditorFrameListener</code>s that
	 * are added to the opened {@link EditorFrame}s and to the
	 * frames that will be opened.
	 *
	 * @return all of the <code>EditorFrameListener</code>s added or an empty
	 * 		   array if no listeners have been added
	 *
	 * @see #addEditorFrameListener
	 * @see	EditorFrameListener
	 */
	public EditorFrameListener[] getEditorFrameListeners();
	
	
	/*
	 * Selection listeners
	 */
	
	/**
	 * Adds the specified listener to receive drawing selection events.
	 *
	 * @param	l	listener to register
	 */
	public void addSelectionListener(SelectionListener l);

	/**
	 * Removes the specified editor seletction listener, so that it no longer
	 * receives editor selection events.
	 *
	 * @param l	listener to unregister
	 */
	public void removeSelectionListener(SelectionListener l);
	
	/**
	 * Returns an array of all the <code>SelectionListener</code>s that
	 * are added to the opened {@link EditorFrame}s and to the
	 * frames that will be opened.
	 *
	 * @return	all of the <code>SelectionListener</code>s added or an empty
	 * 					array if no listeners have been added
	 *
	 * @see	#addSelectionListener
	 * @see	SelectionListener
	 */
	public SelectionListener[] getSelectionListeners();
	
	/**
	 * Returns a logger instance for active assistance messages. All components
	 * and extensions may use this logger to register events and messages that
	 * are to be seen by the user.
	 * 
	 * @return a logger for active assistance messages
	 * @since 2.1
	 */
	public Logger getAssistanceLog();
}