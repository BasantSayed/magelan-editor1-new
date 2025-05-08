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
import org.magelan.editor.extension.NewHexagon;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.magelan.commons.Lang;
import org.magelan.commons.Prefs;
import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.OptionDialog;
import org.magelan.commons.ui.TabbedPane;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.markup.SelectionListener;
import org.magelan.editor.commands.*;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;
import org.magelan.editor.ui.*;
import org.magelan.editor.util.EditorUtil;

/**
 * An editor, capable of handling one drawing at a time.
 * 
 * @author larisa
 * @version 1.5, 08/2006
 */
public class SingleFrameEditorManager extends JFrame implements IEditorManager {

	private static Logger log = Logger.getLogger(SingleFrameEditorManager.class);
	private static Preferences prfs = Preferences.userNodeForPackage(SingleFrameEditorManager.class);
	private static Lang lang = Lang.getLang(Editor.STRINGS);
	
	/**
	 * Default frame title.
	 */
	public static final String TITLE = lang.getString("common.magelan"); //$NON-NLS-1$
	
	public static final String FILE = lang.getString("common.file"); //$NON-NLS-1$
	public static final String EDIT = lang.getString("common.edit"); //$NON-NLS-1$
	public static final String FEATURES = lang.getString("common.features"); //$NON-NLS-1$
	public static final String PREFERENCES = lang.getString("common.preferences"); //$NON-NLS-1$
	public static final String HELP = lang.getString("common.help"); //$NON-NLS-1$
	
	/** Number of recently opened files to show. */
	public static final int MAX_FILES_TO_SHOW = 5;
	
	private static EditorFrame BLANK;
	
	private EditorToolBar mainToolBar;
	private EditorToolBar extToolBar;
	private TabbedPane extTab;
	private JSplitPane split;
	private boolean initialized;
	private StatusBar statusBar;
	private EditorFrame editorFrame;
	private EditorMenu extMenu;	// extensions pull-down menu
	private org.magelan.commons.ui.log.Logger assistanceLog;
	
	/**
	 * A list of event listeners for this component.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Opened editor - in this case selected editor as well.
	 * A wrapper class, enabling the editors to 'talk' to
	 * <code>StatusBar</code> is stored here, rather than an 
	 * editor.
	 */
	private StatusSourceEditor openedEditor;

	/**
	 * Name of the drawing currently open.
	 */
	private String drawingName;
	
	/**
	 * Icon for the drawing currently open.
	 */
  	private ImageIcon drawingIcon;
  	
	/*
	 * Editor commands. Stored like this temporarily.
	 */
	private EditorCommand cNew;
	private EditorCommand cClose;
	private EditorCommand cOpen;
	private EditorCommand cSave;
	private EditorCommand cSaveAs;
	private EditorCommand cDelete;
	private EditorCommand cCut;
	private EditorCommand cCopy;
	private EditorCommand cPaste;
	private EditorCommand cSelectAll;
	private EditorCommand cDeselectAll;
	private EditorCommand cPrint;

	// file submenu, which is dynamically updated
	private EditorMenu file;
	

	/**
	 * Creates a new SingleFrameEditorManager object.
	 */
	public SingleFrameEditorManager() {
		this(TITLE);
	}
 
	/**
	 * Creates a new SingleFrameEditorManager object.
	 *
	 * @param title title of the editor frame
	 */
	public SingleFrameEditorManager(String title) {
		super(title);
		
		log.debug("<init>;"); //$NON-NLS-1$
		
		// set look and feel; bugfix 782818
		String laf = Preferences.userNodeForPackage(Editor.class).
			get("lookandfeel", javax.swing.UIManager.getSystemLookAndFeelClassName()); //$NON-NLS-1$
		EditorUtil.setLookAndFeel(laf);
		
		BLANK = new DefaultEditorFrame() {
			public Dimension getPreferredSize() {
				return new Dimension(450, 300);
			}
		};
	}


	public DrawingEditor getSelectedEditor() {
		return openedEditor == null? null : openedEditor.getEditor();
	}

	public DrawingEditor[] getEditors() {
		return openedEditor == null? new DrawingEditor[] {} : new DrawingEditor[] {openedEditor.getEditor()};
	}
	
	public void setSelectedEditor(DrawingEditor editor) {
	}

	public boolean closeSelectedEditor() {
		return closeEditor(getSelectedEditor());
	}

	public boolean closeEditor(DrawingEditor editor) {
		if (editor == null || !editor.equals(openedEditor.getEditor())) {
			return true;
		}
		
		// display save dialog
		int r = OptionDialog.showQuestionDialog(getMainFrame(), lang.getString("SingleFrameEditorManager.options.save")); //$NON-NLS-1$
		switch (r) {
		case 0:	// save
			String name = getFileNameFor(editor);
			if (name == null) {
				if (saveDrawingAs(editor, null) == null) {
					return false;
				}
			}
			else {
				saveDrawing(editor, name);
			}
			break;
		case 1:	// do not save
			break;
		case 2:	// cancel
			return false;
		}
		
		log.info("Closing editor"); //$NON-NLS-1$ //$NON-NLS-2$
		fireEditorFrameEvent(EditorFrameEvent.EDITOR_FRAME_CLOSING);

		if (openedEditor instanceof IStatusSource) {
			((IStatusSource) openedEditor).deConfigStatusListener(getStatusBar());
		}

		// remove the listeners from the editor
		removeListeners(editor);
		fireEditorFrameEvent(EditorFrameEvent.EDITOR_FRAME_CLOSED);
		removeListeners(editor);
		
		// close the editor and set default title
		openedEditor = null;
		
		editorFrame.clear();
		
		// AA: event firing code to be added
		if (cClose != null) {
			cClose.setEnabled(false);
		}
		if (cSave != null) {
			cSave.setEnabled(false);
		}
		if (cSaveAs != null) {
			cSaveAs.setEnabled(false);
		}

		drawingName = null;
		drawingIcon = null;
		
		getExtensionToolbar().setEnabled(false);
		getMainFrame().setTitle(""); //$NON-NLS-1$
		
		return true;
	}

	public StatusBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new StatusBar();
		}

		return statusBar;
	}

	/**
	 * Opens new default editor. This method should check if another editor is
	 * opened and prompt the user for save.
	 */
	public void openNewDefaultEditor() {
		if (openedEditor != null && !closeSelectedEditor()) {
			return;
		}

		DrawingTemplate t = Editor.getTemplatesManager().getDefaultTemplate();
		DrawingEditor ed = t.getEditor();
		ed.setModel(t.getDrawing());
		openEditor(ed);
	}

	public void openNewEditor() {
		if (openedEditor != null && !closeSelectedEditor()) {
			return;
		}
		
		// function as openNewDefaultEditor() if only one template
		if (Editor.getTemplatesManager().getTemplates().size() < 2) {
			DrawingTemplate t = Editor.getTemplatesManager().getDefaultTemplate();
			DrawingEditor ed = t.getEditor();
			ed.setModel(t.getDrawing());
			openEditor(ed);
		}
		
		// display a new drawing dialog
		else {
			// always construct the dialog
			NewDrawingDialog dialog = new NewDrawingDialog(Editor.getTemplatesManager().getTemplates(), getMainFrame());
			dialog.setVisible(true);
			DrawingTemplate template = dialog.getTemplate();
			if (template != null) {
				DrawingEditor ed = template.getEditor();
				ed.setModel(template.getDrawing());
				openEditor(ed);
			}
		}
	}

	public void openEditor(DrawingEditor editor) {
		if (openedEditor != null && !closeSelectedEditor()) {
			return;
		}
		
		updateFileMenu(file);
		
		log.info("Opening new editor"); //$NON-NLS-1$
		drawingName = null;
		drawingIcon = null;
		
		EditorUtil.configure(editor);
		
		//editor.getContainer().setBackground(Color.black);
		editor.getContainer().setVisible(true);
		editor.getContainer().setPreferredSize(new Dimension(1000, 1000));	// FIXME

		addListeners(editor);

		//FIXME check for save
		//FIXME deconfig status bar
		
		editorFrame.setDrawingEditor(editor);
		openedEditor = new StatusSourceEditor(editor);

		if (openedEditor instanceof IStatusSource) {
			((IStatusSource) openedEditor).configStatusListener(getStatusBar());
		}

		if (cClose != null) {
			cClose.setEnabled(true);
		}
		if (cSave != null) {
			cSave.setEnabled(true);
		}
		if (cSaveAs != null) {
			cSaveAs.setEnabled(true);
		}

		getExtensionToolbar().setEnabled(true);
		fireEditorFrameEvent(EditorFrameEvent.EDITOR_FRAME_OPENED);
		fireEditorFrameEvent(EditorFrameEvent.EDITOR_FRAME_ACTIVATED);
		
		//editor.paint();
	}

	
	public DrawingEditor readDrawing(String name) {
		File file = null;
		if (name == null) {
			JFileChooser fileChooser = Editor.getFilesManager().getFileChooser();
			int returnVal = fileChooser.showOpenDialog(getMainFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
			}
		} else {
			file = new File(name);
		}
		
		return Editor.getFilesManager().decode(file);
	}
	
	public int saveDrawing(DrawingEditor editor, String name) {
		return Editor.getFilesManager().encode(editor.getModel(), editor.getRenderer(), new File(name));
	}
	
	public Object saveDrawingAs(DrawingEditor editor, String name) {
		File file = null;
		JFileChooser fileChooser = Editor.getFilesManager().getFileChooser();
		if (name != null) {
			fileChooser.setSelectedFile(new File(name));
		}
		int returnVal;
		boolean repeat = false;
		do {
			returnVal = fileChooser.showSaveDialog(getMainFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				repeat = false;
				if (file.exists()) {
					int opt = OptionDialog.showQuestionDialog(getMainFrame(), lang.getString("SingleFrameEditorManager.options.overwrite") + file.getName()); //$NON-NLS-1$
					if (opt == 1) {
						repeat = true;
					} 
					else if (opt == 2) {
						return null;
					} 
				}
			}
			else {
				repeat = false;
			}
		} while (repeat);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			int res = Editor.getFilesManager().encode(editor.getModel(), editor.getRenderer(), file);
			if (res == IFilesManager.SUCCESS) {
				return file.getAbsolutePath();
			}
			return new Integer(res);
		}
		else {
			return null;
		}
	}
	
	public JFrame getMainFrame() {
		return this;
	}

	public EditorToolBar getEditorToolbar() {
		if (mainToolBar == null) {
			mainToolBar = new EditorToolBar(prfs.getInt("toolbar.editor.mode", EditorToolBar.TOOLBAR)); //$NON-NLS-1$
		}

		return mainToolBar;
	}

	public EditorToolBar getExtensionToolbar() {
		if (extToolBar == null) {
			extToolBar = new EditorToolBar(prfs.getInt("toolbar.extension.mode", EditorToolBar.SPLIT/*TABBED*/)); //$NON-NLS-1$
		}

		return extToolBar;
	}
	
	public JTabbedPane getExtensionTab() {
		if (extTab == null) {
			extTab = (TabbedPane) UIFactory.createTabbedPane();
			
			split = UIFactory.createSplitPane();
			split.setLeftComponent(((TabbedPane.Panel) extTab).getPanel());
			
			if (editorFrame != null) {
				getContentPane().remove((Component) editorFrame);
				split.setRightComponent((Component) editorFrame);
			}
			else {
				split.setRightComponent((Component) BLANK);
			}
			
			
			getContentPane().add(split, BorderLayout.CENTER);
		}

		return extTab;
	}
	
	public EditorMenu getExtensionMenu() {
		if (extMenu == null) {
			extMenu = new EditorMenu(lang.getString("common.extensions")); //$NON-NLS-1$
			extMenu.setMnemonic(KeyEvent.VK_X);
		}
			
		return extMenu;
	}
	
	public void initialize() {
		log.debug("initialize();"); //$NON-NLS-1$
		
		if (initialized) {
			log.error("initialize() called more than once"); //$NON-NLS-1$
			throw new IllegalStateException("initialize() called more than once"); //$NON-NLS-1$
		}

		JPanel toolbars = new JPanel();
		toolbars.setLayout(new BorderLayout());
		toolbars.add(getEditorToolbar(), BorderLayout.NORTH);
		toolbars.add(getExtensionToolbar(), BorderLayout.SOUTH);
		getContentPane().add(toolbars, BorderLayout.NORTH);
			
		editorFrame = BLANK;
		if (split == null) {
			getContentPane().add((Component) editorFrame, BorderLayout.CENTER);
		}
		getContentPane().add(getStatusBar(), BorderLayout.SOUTH);

		// create editor commands
		cNew = new NewDrawing();
		cOpen = new OpenDrawing();
		cSave = new SaveDrawing();
		cSave.setEnabled(false);
		cSaveAs = new SaveDrawingAs();
		cSaveAs.setEnabled(false);
		cClose = new CloseDrawing();
		cClose.setEnabled(false);
		cDelete = new Delete();
		cCut = new Cut();
		cCopy = new Copy();
		cPaste = new Paste();
		cSelectAll = new SelectAll();
		cDeselectAll = new DeselectAll();
		cPrint = new Print();
		
		// create the editor toolbar
		mainToolBar = getEditorToolbar();
		mainToolBar.add(FILE, cNew);
		mainToolBar.add(FILE, cOpen);
		mainToolBar.add(FILE, cSave);
		mainToolBar.addSeparator(new Dimension(4, 16));
		mainToolBar.add(FILE, cPrint);
		
		/*
		mainToolBar.add(EDIT, cCut);
		mainToolBar.add(EDIT, cCopy);
		mainToolBar.add(EDIT, cPaste);
		*/
		
		// create file submenu
		file = new EditorMenu(FILE);
		file.setMnemonic(KeyEvent.VK_F);
		
		// add file commands
		updateFileMenu(file);

		// create file submenu
		EditorMenu edit = new EditorMenu(EDIT);
		edit.setMnemonic(KeyEvent.VK_E);
		
		// add edit commands
		/*
		edit.add(UIFactory.createMenuItem(cCut));
		edit.add(UIFactory.createMenuItem(cCopy));
		edit.add(UIFactory.createMenuItem(cPaste));
		edit.addSeparator();
		*/
		edit.add(UIFactory.createMenuItem(cDelete));
		edit.add(UIFactory.createMenuItem(cSelectAll));
		edit.add(UIFactory.createMenuItem(cDeselectAll));
		
		// add extensions manager
		getExtensionMenu().addSeparator();
		getExtensionMenu().add(UIFactory.createMenuItem(new ManageExtensions()));
		
		// create features submenu
		EditorMenu feat = new EditorMenu(FEATURES);
		feat.setMnemonic(KeyEvent.VK_A);
		
		// add features commands
		feat.add(UIFactory.createMenuItem(new FeaturesManager()));
		
		// create prefs submenu
		EditorMenu prefs = new EditorMenu(PREFERENCES);
		prefs.setMnemonic(KeyEvent.VK_P);
		
		// add prefs commands
		prefs.add(UIFactory.createMenuItem(new EditWorkspace()));
		prefs.add(UIFactory.createMenuItem(new LFManager()));
		prefs.add(UIFactory.createMenuItem(new EditClasspath()));
		
		// create help submenu
		EditorMenu help = new EditorMenu(HELP);
		help.setMnemonic(KeyEvent.VK_H);
		
		// add help commands
		help.add(UIFactory.createMenuItem(new Help()));
		help.add(UIFactory.createMenuItem(new Tips()));
		help.addSeparator();
		help.add(UIFactory.createMenuItem(new About()));
		
		// add a menu bar
		EditorMenuBar menu = new EditorMenuBar();
		menu.add(file);
		menu.add(edit);
		menu.add(getExtensionMenu());
		menu.add(feat);
		menu.add(prefs);
		menu.add(help);
		setJMenuBar(menu);

		getExtensionToolbar().setEnabled(false);

		// register status bar
		menu.setStatusBar(getStatusBar());
		getEditorToolbar().configStatusListener(getStatusBar());
		getExtensionToolbar().configStatusListener(getStatusBar());

		// active assistance
		Editor.getExtensionsManager().add(new AssistancePalette());
		Editor.getExtensionsManager().add(new NewHexagon());

		// set Magelan icon
		setIconImage(((ImageIcon) Icons.MAGELAN_ICON).getImage()); //$NON-NLS-1$

		// set look and feel; bugfix 782818
//		String laf = prfs.get("lookandfeel", javax.swing.UIManager.getSystemLookAndFeelClassName()); //$NON-NLS-1$
//		SwingUtil.setLookAndFeel(laf);

		//pack and center
		if (!Prefs.readBounds(this, "mainframe", this.getClass()) ||  //$NON-NLS-1$
				getWidth() == 0 || getHeight() == 0) {
			pack();
			org.magelan.commons.ui.SwingUtil.center(this);
		}
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				log.info("Exitting the editor..."); //$NON-NLS-1$
				if (Editor.getEditorManager().exit()) {
					Editor.getInstance().shutdown();
					System.exit(0);
				}
			}
		});
	}

	private void updateFileMenu(JMenu file) {
		file.removeAll();
		
		file.add(UIFactory.createMenuItem(cNew));
		file.add(UIFactory.createMenuItem(cOpen));
		file.add(UIFactory.createMenuItem(cClose));
		file.addSeparator();
		file.add(UIFactory.createMenuItem(cSave));
		file.add(UIFactory.createMenuItem(cSaveAs));
		file.addSeparator();
		file.add(UIFactory.createMenuItem(cPrint));
		file.addSeparator();
		
		String recent = prfs.get("recentfiles", null); //$NON-NLS-1$
		//System.out.println(recent);
		if (recent != null) {
			java.util.List recentList = ClassManifest.getClassPath(recent);
			int i = 1;
			Iterator iter = recentList.iterator();
			while (iter.hasNext() /*&& i < MAX_FILES_TO_SHOW*/) {
				file.add(new OpenAction((String) iter.next(), i++));  //$NON-NLS-1$ //$NON-NLS-2$
			}
			file.addSeparator();
		}
		
		file.add(UIFactory.createMenuItem(new Exit()));
		//if (getJMenuBar() != null) getJMenuBar().revalidate();
	}
	
	public boolean exit() {
		log.debug("exit();"); //$NON-NLS-1$
		
		// save the bounds of the main frame
		Prefs.storeBounds(this, "mainframe", this.getClass()); //$NON-NLS-1$
		
		boolean res = closeSelectedEditor();
		if (res) {
			getMainFrame().setVisible(false);
		}
		return res;
	}
	
	
	/*
	 * Frame/Window equivalent
	 */

	public void addEditorFrameListener(EditorFrameListener l) {
		listenerList.add(EditorFrameListener.class, l);
	}

	public void removeEditorFrameListener(EditorFrameListener l) {
		listenerList.remove(EditorFrameListener.class, l);
	}

	public EditorFrameListener[] getEditorFrameListeners() {
		return (EditorFrameListener[]) listenerList.getListeners(EditorFrameListener.class);
	}

	/**
	 * Fires an editor frame event.
	 *
	 * @param id the type of the event being fired; one of the following:
	 * 		  <ul><li><code>EditorFrameEvent.EDITOR_FRAME_OPENED</code>
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_CLOSING</code>
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_CLOSED</code>
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_ICONIFIED</code>
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_DEICONIFIED</code>
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_ACTIVATED</code>
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_DEACTIVATED</code> </ul>
	 * 		  If the event type is not one of the above, nothing happens.
	 */
	protected void fireEditorFrameEvent(int id) {
		Object[] listeners = listenerList.getListenerList();
		EditorFrameEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EditorFrameListener.class) {
				if (e == null) {
          e = new EditorFrameEvent(openedEditor.getEditor(), id);

				}

				switch (e.getID()) {
					case EditorFrameEvent.EDITOR_FRAME_OPENED:
						((EditorFrameListener) listeners[i + 1]).editorFrameOpened(e);
						break;
					case EditorFrameEvent.EDITOR_FRAME_CLOSING:
						((EditorFrameListener) listeners[i + 1]).editorFrameClosing(e);
						break;
					case EditorFrameEvent.EDITOR_FRAME_CLOSED:
						((EditorFrameListener) listeners[i + 1]).editorFrameClosed(e);
						break;
					case EditorFrameEvent.EDITOR_FRAME_ICONIFIED:
						((EditorFrameListener) listeners[i + 1]).editorFrameIconified(e);
						break;
					case EditorFrameEvent.EDITOR_FRAME_DEICONIFIED:
						((EditorFrameListener) listeners[i + 1]).editorFrameDeiconified(e);
						break;
					case EditorFrameEvent.EDITOR_FRAME_ACTIVATED:
						((EditorFrameListener) listeners[i + 1]).editorFrameActivated(e);
						break;
					case EditorFrameEvent.EDITOR_FRAME_DEACTIVATED:
						((EditorFrameListener) listeners[i + 1]).editorFrameDeactivated(e);
						break;
					default:
						break;
				}
			}
		}
	}

	public void addSelectionListener(SelectionListener l) {
		// add to listener list and then attach to openedEditor if any
		listenerList.add(SelectionListener.class, l);
		if (openedEditor != null) {
			openedEditor.getEditor().addSelectionListener(l);
		}
	}

	public void removeSelectionListener(SelectionListener l) {
		// remove from listeners list and from opened editor if any
		listenerList.remove(SelectionListener.class, l);
		if (openedEditor != null) {
			openedEditor.getEditor().removeSelectionListener(l);
		}
	}

	public SelectionListener[] getSelectionListeners() {
		return (SelectionListener[]) listenerList.getListeners(SelectionListener.class);
	}


	/**
	 * Removes selection and other listeners from editor
	 *
	 * @param	e	editor to remove the listeners from
	 */
	private void removeListeners(DrawingEditor e) {
		SelectionListener[] selList = getSelectionListeners();
		for (int i = 0; i < selList.length; i++) {
			e.removeSelectionListener(selList[i]);
		}
	}

	/**
	 * Adds selection and other listeners to editor
	 *
	 * @param	e	editor to add listeners to
	 */
	private void addListeners(DrawingEditor e) {
		SelectionListener[] selList = getSelectionListeners();
		for (int i = 0; i < selList.length; i++) {
			e.addSelectionListener(selList[i]);
		}
	}
	
	
	/**
	 * Overrides JFrame's <code>setTitle</code> method for 
	 * additional functionality. The title set to the frame will be
	 * <code>TITLE + " - [" + title + "]"</code>.
	 * 
	 * @param  title  the new frame title
	 */
	public void setTitle(String title) {
		if (title == null || "".equals(title)) { //$NON-NLS-1$
			super.setTitle(TITLE);
		}
		else {
			super.setTitle(TITLE + " - [" + title + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	
	public String getFileNameFor(DrawingEditor ed) {
		if (ed == null || !getSelectedEditor().equals(ed)) {
			return null;
		}
		
		return drawingName;
	}

	public ImageIcon getIconFor(DrawingEditor ed) {
		if (ed == null || !getSelectedEditor().equals(ed)) {
			return null;
		}
		
		return drawingIcon;
	}
	
	public void setFileNameFor(String name, DrawingEditor ed) {
		if (ed == null || !getSelectedEditor().equals(ed)) {
			return;
		}
		
		drawingName = name;
		editorFrame.setFrameName(name);
	}
	
	public void setIconFor(ImageIcon icon, DrawingEditor ed) {
		if (ed == null || !getSelectedEditor().equals(ed)) {
			return;
		}
		
		drawingIcon = icon;
		editorFrame.setFrameIcon(icon);
	}
	
	public org.magelan.commons.ui.log.Logger getAssistanceLog() {
		if (assistanceLog == null) {
			assistanceLog = new org.magelan.commons.ui.log.Logger.Default();
		}
		return assistanceLog;
	}
	
	private class OpenAction extends AbstractAction {
		private String file;
		private int idx;
		private File f;
		OpenAction(String file, int idx) {
			this.file = file;
			this.idx = idx;
			f = new File(file);
			putValue(Action.NAME, "" + idx + " [" + f.getName() + "]");
			putValue(Action.LONG_DESCRIPTION, f.getAbsolutePath());
			putValue(Action.MNEMONIC_KEY, new Integer(0x30+idx)); // KeyEvent.VK_[0 to 9]
		}
		public void actionPerformed(ActionEvent e) {
			// first try to close the selected editor
			if (openedEditor != null && !closeSelectedEditor()) {
				return;
			}
			
			DrawingEditor ed;
			ed = Editor.getFilesManager().decode(f);

			if (ed == null) {
				return;
			}

			// open the editor and set proper editor frame title
			getMainFrame().setTitle(file);
			openEditor(ed);
			setFileNameFor(f.getAbsolutePath(), ed);
			DrawingFileHandler fh = Editor.getFilesManager().getHandlerFor(f);
			if (fh != null) {
				setIconFor(fh.getIcon(), ed);
			}
		}
	}
}