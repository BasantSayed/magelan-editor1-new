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
package org.magelan.editor.extension;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 *
 * @author Assen Antov
 * @version 1.0, 06/2002
 */
public class NewAttribute extends NewAttributeLoader implements ActionListener {

	private Object bean;


	/**
	 * Creates a new NewAttribute object.
	 *
	 * @param bean DOCME!
	 */
	public NewAttribute(Object bean) {
		this(bean, null);
	}

	/**
	 * Creates a new NewAttribute object.
	 *
	 * @param bean DOCME!
	 * @param parent DOCME!
	 */
	public NewAttribute(Object bean, Frame parent) {
		super(parent);
		setModal(true);
		center(this);

		this.bean = bean;

		getRootPane().setDefaultButton(okButton);

		// set listeners
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		// set tooltips
		//		okButton.setToolTipText("Creates a new form and saves the User list of forms");
		//		cancelButton.setToolTipText("Don't apply any changes");
	}

	//~ Methods ----------------------------------------------------------------

	/**
	 * Listens for action events.
	 * 
	 * <p></p>
	 *
	 * @param e an event
	 */
	public void actionPerformed(ActionEvent e) {
		String src = ((java.awt.Component) e.getSource()).getName();

		if ("okButton".equals(src)) { //$NON-NLS-1$
			Object val;
			if (type.getSelectedItem().equals("String")) { //$NON-NLS-1$
				val = value.getText();
			} else if (type.getSelectedItem().equals("Integer")) { //$NON-NLS-1$
				val = new Integer(value.getText());
			} else if (type.getSelectedItem().equals("Real")) { //$NON-NLS-1$
				val = new Double(value.getText());
			} else if (type.getSelectedItem().equals("Boolean")) { //$NON-NLS-1$
				val = new Boolean(value.getText());
			} else {
				val = null;
			}

			if (!"".equals(name.getText())) { //$NON-NLS-1$
	//			bean.putValue(name.getText(), val);
			}

			setVisible(false);
			dispose();
		} else if ("cancelButton".equals(src)) { //$NON-NLS-1$
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Centers the component on screen.
	 * 
	 * <p></p>
	 *
	 * @param c the component to center
	 */
	public void center(Component c) {
		java.awt.Dimension screenSize = Toolkit.getDefaultToolkit()
											   .getScreenSize();
		c.setLocation(screenSize.width / 2 - c.getSize().width / 2,
					  screenSize.height / 2 - c.getSize().height / 2);
	}

	/**
	 * Opens a new forms dialog.
	 * 
	 * <p></p>
	 *
	 * @param args array of parameter strings
	 */
	public static void main(String[] args) {
		//(new NewAttribute(new LineEntity())).setVisible(true);
	}
}