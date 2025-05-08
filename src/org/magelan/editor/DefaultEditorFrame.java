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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import org.magelan.commons.ui.Icons;
import org.magelan.commons.ui.MenuButton;
import org.magelan.commons.ui.SimpleInternalFrame;
import org.magelan.commons.ui.UIFactory;
import org.magelan.core.CoreModel;
import org.magelan.core.Derivate;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.core.variant.DefaultVariant;
import org.magelan.core.variant.Variant;
import org.magelan.core.variant.VariantRenderer;
import org.magelan.drawing.DrawingModel;
import org.magelan.editor.event.EditorFrameEvent;
import org.magelan.editor.event.EditorFrameListener;


/**
 * The default implementation of <code>EditorFrame</code>.
 *
 * @author Assen Antov
 * @version 3.0, 05/2006
 */
public class DefaultEditorFrame extends SimpleInternalFrame implements EditorFrame {

	private static org.magelan.commons.Lang lang = org.magelan.commons.Lang.getLang(Editor.STRINGS);

	private DrawingEditor editor;
	private CoreModel parent;
	//private JTabbedPane tab;
	private JScrollPane scroll;
	
	private Action closeAction, layoutsAction;
	private JButton closeButton, layoutsButton;

	private String name;


	/**
	 * Creates a new editor frame with no editor. 
	 */
	public DefaultEditorFrame() {
		this(null, null, null);
	}

	/**
	 * Creates a new editor frame, using the parameter editor. 
	 * 
	 * @param ed DrawingEditor
	 */
	public DefaultEditorFrame(DrawingEditor ed) {
		this(ed, null, null);
	}

	/**
	 * Creates a new editor frame, using the parameter editor. 
	 * 
	 * @param ed DrawingEditor
	 * @param name frame name
	 * @param icon frame icon
	 */
	public DefaultEditorFrame(DrawingEditor ed, String name, Icon icon) {
		super(" "); //$NON-NLS-1$
		
		setDrawingEditor(ed);
		setFrameName(name);
	}


	public DrawingEditor getDrawingEditor() {
		return editor;
	}

	public void setDrawingEditor(DrawingEditor ed) {
		DrawingEditor oldEditor = editor;
		editor = ed;
		
		if (ed != null) {

			parent = (CoreModel) ed.getModel();
			
			scroll = UIFactory.createScrollPane(null);
			scroll.setAutoscrolls(true);
			scroll.setViewportView(editor.getContainer());
			/*
			tab = UIFactory.createTabbedPane(); //new JTabbedPane();
			tab.addTab("Model", scroll);
			tab.setTabPlacement(JTabbedPane.BOTTOM);
			setContent(tab);
			*/
			setContent(scroll);
			
			/*
			 * Create toolbar
			 */
			if (super.getToolBar() == null) {
				JToolBar toolbar = new JToolBar();
				toolbar.setRollover(true);
				setToolBar(toolbar);
			}
			else {
				super.getToolBar().removeAll();
			}
			
			/*
			 * Add buttons
			 */
			
		//	if (layoutsButton == null) {
				layoutsAction = new LayoutsAction();
				layoutsButton = new MenuButton(layoutsAction, getActionsFor(parent)); //UIFactory.createButton(layoutsAction);
		//	}
			super.getToolBar().add(layoutsButton);
			
			if (closeButton == null) {
				closeAction = new CloseAction();
				closeButton = UIFactory.createButton(closeAction);
			}
			super.getToolBar().add(closeButton);
		}
		else {
			setContent(new JPanel());
		}
		firePropertyChange(EVENT_EDITOR, oldEditor, ed);
	}

	private Object[] getActionsFor(DrawingModel model) {
		List actions = new ArrayList();
		
		actions.add(new NewVariant());
		actions.add(null);
		actions.add(new DerivateSwitch(parent));
		
		Iterator iter = model.getFeatures(Derivate.class).iterator();
		int i = 1;
		while (iter.hasNext()) {
			Derivate el = (Derivate) iter.next();
			actions.add(new DerivateSwitch(el));
			
			/*
			Derivate el = (Derivate) iter.next();
			String name = (String) el.getValue(CoreModel.KEY_NAME);
			if (name == null) {
				name = "Derivate " + i++;
			}
			
			
			/*
			scroll = UIFactory.createScrollPane(null);
			scroll.setAutoscrolls(true);
			scroll.setViewportView((new DefaultDrawingEditor((CoreModel) el)).getContainer());
			
			tab.addTab(name, scroll);
			*/
		}
		
		return actions.toArray();
	}
	
	public String getFrameName() {
		return name;
	}

	public void setFrameName(String name) {
		firePropertyChange(EVENT_NAME, this.name, name);
		this.name = name;
		
		super.setTitle(createTitle(name));
	}

	/**
	 * Determines the proper title of the contained drawing for the given name.
	 * 
	 * @param name name to use in the title
	 * @return proper frame title
	 */
	protected String createTitle(String name) {
		String title;
		
		if (name == null) {
			title = lang.getString("DefaultEditorFrame.title"); //$NON-NLS-1$
		} 
		else {
			title = "[" + name + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (editor != null && editor.getModel() != null) {

			DrawingTemplate templ = Editor.
				getTemplatesManager().getTemplateFor(editor.getModel());
			if (templ != null) {
				title += " (" + templ.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		return title;
	}
	
	public void setFrameIcon(Icon icon) {
		Icon oldIcon = super.getFrameIcon();
		super.setFrameIcon(icon);
		firePropertyChange(EVENT_ICON, oldIcon, icon);
	}

	public JToolBar getToolBar() {
		return super.getToolBar();
	}

	public void clear() {
		setDrawingEditor(null);
		setFrameName(null); // " " //$NON-NLS-1$
		setFrameIcon(null);
		getToolBar().removeAll();
	}

	public void addEditorFrameListener(EditorFrameListener l) { // remind: sync ??
		listenerList.add(EditorFrameListener.class, l);

		// remind: needed?
		enableEvents(0); // turn on the newEventsOnly flag in Component.
	}

	public void removeEditorFrameListener(EditorFrameListener l) { // remind: sync??
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
	 * 		  <li><code>EditorFrameEvent.EDITOR_FRAME_DEACTIVATED</code>
	 * 		  </ul> If the event type is not one of the above, nothing
	 * 		  happens.
	 */
	protected void fireEditorFrameEvent(int id) {
		Object[] listeners = listenerList.getListenerList();
		EditorFrameEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EditorFrameListener.class) {
				if (e == null) {
					//we do assume here that editor is not null
					e = new EditorFrameEvent(editor, id);
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

	private class CloseAction extends AbstractAction {
		
		CloseAction() {
			putValue(NAME, lang.getString("common.close")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.CLOSE_VIEW_ICON);
		}
		
		public void actionPerformed(ActionEvent e) {
			Editor.getEditorManager().closeEditor(getDrawingEditor());
		}
	}
	
	private class LayoutsAction extends AbstractAction {
		
		LayoutsAction() {
			putValue(NAME, lang.getString("DefaultEditorFrame.name")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.LAYOUT_ICON);
		}
		
		public void actionPerformed(ActionEvent e) {
			//Editor.getInstance().getManager().closeEditor(getDrawingEditor());
		}
	}
	
	private class NewVariant extends AbstractAction {
		
		NewVariant() {
			putValue(NAME, "New variant");
			putValue(SMALL_ICON, Icons.LAYOUT_ICON);
		}
		
		public void actionPerformed(ActionEvent e) {
			Variant v = new DefaultVariant(parent);
			v.update(parent);
			
			VariantRenderer vr = new VariantRenderer();
			((CoreModel) v).addFeature(vr);
			((CoreModel) v).setCurrent(vr);
			
			parent.addFeature(v);
		}
	}
	
	private class DerivateSwitch extends AbstractAction {
		
		private DrawingModel model;
		
		DerivateSwitch(DrawingModel model) {
			this.model = model;
			putValue(NAME, (String) model.getValue(CoreModel.KEY_NAME));
			//putValue(SMALL_ICON, Icons.LAYOUT_ICON);
		}
		
		public void actionPerformed(ActionEvent e) {
			editor.setModel(model);
		}
	}
}