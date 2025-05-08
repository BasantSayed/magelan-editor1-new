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
package org.magelan.editor.ui;

import org.magelan.editor.event.*;

import java.awt.*;

import java.util.*;

import javax.swing.*;


/**
 * StatusBar component. This component allways have a section (panel) for User
 * Hints. It also can be configured to contain other sections.
 */
public class StatusBar extends JPanel implements StatusChangedListener {
	//~ Static fields/initializers ---------------------------------------------

	private static final String HINT_PANEL = "hint"; //$NON-NLS-1$

	//~ Instance fields --------------------------------------------------------

	private HashMap panels;
	private String defaultPanel;
	private GridBagLayout gbl;
	private GridBagConstraints gbc;

	//~ Constructors -----------------------------------------------------------

	/**
	 * SatusBar constructor comment.
	 */
	public StatusBar() {
		super();
		panels = new HashMap();

		initLayout();

		defaultPanel = HINT_PANEL;

		HintDisplayPanel hintPanel = createNewHintDispalyPanel(HINT_PANEL, 0);
		addHintPanel(hintPanel);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Initialize and set layout
	 */
	private void initLayout() {
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		setLayout(gbl);
	}

	/**
	 * Hides/displays Hint Display manels
	 *
	 * @param hintPanel DOCME!
	 * @param visible DOCME!
	 */
	public void setHintDisplayVisible(HintDisplayPanel hintPanel,
									  boolean visible) {
		HintDisplayPanel panel = (HintDisplayPanel) panels.get(hintPanel.getName());
		panel.setVisible(visible);
	}

	/**
	 * Gets the preferred size of this component.
	 *
	 * @return A dimension object indicating this component's preferred size.
	 */
	public Dimension getPreferredSize() {
		Dimension ret = super.getPreferredSize();
		ret.height = 20;

		return ret;
	}

	/**
	 * Notified that new status has to be displayed
	 *
	 * @param event
	 */
	public void statusChanged(StatusChangedEvent event) {
		String target = event.getStatusDestination();

		if (target == null || target.trim().length() == 0) {
			target = defaultPanel;
		}

		HintDisplayPanel panel = (HintDisplayPanel) panels.get(target);
		panel.setStatus(event.getStatusString());
	}

	/**
	 * Creates  new HintDisplaypanel and adds it to the StatusBar The panel is
	 * allways added to the end.
	 *
	 * @param name the name of hint panel
	 * @param width preferred width of the new hint Panel. If width is 0 -  the
	 * 		  panel will be stretched by the layout to fill in the gaps.
	 *
	 * @return new HintDisplayPanel that was added to the end.
	 *
	 * @throws IllegalArgumentException in name is empty or null
	 */
	public static HintDisplayPanel createNewHintDispalyPanel(String name,
															 int width) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Name can not be null or empty."); //$NON-NLS-1$
		}

		HintDisplayPanel hintPanel = new HintDisplayPanel();
		hintPanel.setName(name);
		hintPanel.setPreferredWidth(width);

		return hintPanel;
	}

	/**
	 * Adds a new visible HintDisplayPanel to the status bar
	 *
	 * @param hintPanel DOCME!
	 */
	public void addHintPanel(HintDisplayPanel hintPanel) {
		gbc.weightx = (hintPanel.getPreferredWidth() == 0) ? 1 : 0;

		gbl.setConstraints(hintPanel, gbc);
		panels.put(hintPanel.getName(), hintPanel);
		add(hintPanel);
		revalidate();
		repaint();
	}

	/**
	 * Removes HintDisplayPanel from the status bar
	 *
	 * @param hintPanel DOCME!
	 */
	public void removeHintPanel(HintDisplayPanel hintPanel) {
		panels.remove(hintPanel.getName());
		remove(hintPanel);
		revalidate();
		repaint();
	}
}