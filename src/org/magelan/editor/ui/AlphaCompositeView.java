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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.magelan.commons.ui.Icons;
import org.magelan.core.style.Layer;


/**
 * A class to view layer alpha composite settings.
 * 
 * <p></p>
 *
 * @author Assen Antov
 * @version 1.0, 04/2002
 */
public class AlphaCompositeView extends JPanel implements PropertyChangeListener {
	//~ Static fields/initializers ---------------------------------------------

	private static boolean showMeter = false;
	private static final int THICKNESS = 1;

	//~ Instance fields --------------------------------------------------------

	private Layer layer;
	private ImageIcon icon = (ImageIcon) Icons.EYE_ICON;

	//~ Constructors -----------------------------------------------------------

	/**
	 * Creates a new AlphaCompositeView object.
	 */
	public AlphaCompositeView() {
		this(new Layer());
	}

	/**
	 * Creates a new AlphaCompositeView object.
	 *
	 * @param layer DOCME!
	 */
	public AlphaCompositeView(Layer layer) {
		Dimension size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
		setLayer(layer);
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * DOCME!
	 *
	 * @param layer DOCME!
	 */
	public void setLayer(Layer layer) {
		if (layer == null) {
			return;
		}

		if (this.layer != null) {
			this.layer.removePropertyChangeListener(this);
		}

		layer.addPropertyChangeListener("alphaComposite", this); //$NON-NLS-1$
		this.layer = layer;
		setToolTipText("" + (int) (layer.getAlphaComposite().getAlpha() * 100) + //$NON-NLS-1$
					   "%"); //$NON-NLS-1$
		firePropertyChange("layer", this.layer, layer); //$NON-NLS-1$
		repaint();
	}

	/**
	 * DOCME!
	 *
	 * @return DOCME!
	 */
	public Layer getLayer() {
		return layer;
	}

	/**
	 * DOCME!
	 *
	 * @param g DOCME!
	 */
	public void paint(Graphics g) {
		if (g == null) return;
		
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;

		if (g2 != null) {
			if (showMeter) {
				g2.setPaintMode();

				int delta = (int) (layer.getAlphaComposite().getAlpha() * getHeight());
				g2.setColor(Color.black);
				g2.fillRect(getWidth() - THICKNESS, 0, THICKNESS,
							getHeight() - delta);
				g2.setColor(Color.green);
				g2.fillRect(getWidth() - THICKNESS, getHeight() - delta,
							THICKNESS, delta);
			}

			if (layer != null) {
				g2.setComposite(layer.getAlphaComposite());
			}

			if (!isEnabled()) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
														   0.3f));
			}

			int posx = (getPreferredSize().width - icon.getIconWidth()) / 2;
			int posy = (getPreferredSize().height - icon.getIconHeight()) / 2;
			g2.drawImage(icon.getImage(), posx, posy, this);
		}
	}

	/**
	 * DOCME!
	 *
	 * @param e DOCME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		//	firePropertyChange("", null, null);
		//	setLayer(layer);
		paint(getGraphics());
	}
}