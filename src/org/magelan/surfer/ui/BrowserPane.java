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
package org.magelan.surfer.ui;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * An HTML browser pane.
 * <p>
 * @version	1.0, 07/2001
 * @author		Assen Antov
 */
public class BrowserPane extends JPanel {
	
	private JEditorPane html;

	public static final String DEFAULT_URL = "file:index.html";
	public static final String URL_NOT_FOUND_P = "<html><font face=\"Arial\">Cannot find URL: ";
	public static final String URL_NOT_FOUND_S = "</font></html>";
	public static final String MALFORMED_URL_P = "<html><font face=\"Arial\">Malformed URL: ";
	public static final String MALFORMED_URL_S = "</font></html>";


	/**
	 * 
	 */
	public BrowserPane() {
		this(null);
	}
	
	/**
	 * 
	 */
	public BrowserPane(URL url) {
		super();
		
		html = new JEditorPane();
		setPage(url);
		html.setContentType("text/html");

		html.setEditable(false);
		html.addHyperlinkListener(
			new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						if (e instanceof HTMLFrameHyperlinkEvent) {
							((HTMLDocument) html.getDocument()).processHTMLFrameHyperlinkEvent(
								(HTMLFrameHyperlinkEvent) e);
						}
						else {
							setPage(e.getURL());
						}
					}
				}
			}
		);

		setLayout(new BorderLayout());
		
		JScrollPane scroll = new JScrollPane();
		add(scroll);
		
		JViewport vp = scroll.getViewport();
		vp.setView(html);
	}
	
	
	/**
	 * Sets the page to view.
	 * <p>
	 * @param		url	the URL to view
	 */
	public void setPage(String url) {
		URL url0 = null;
		try {
			url0 = new URL(url);
			setPage(url0);
		} catch (Exception e) {
			html.setText(MALFORMED_URL_P + url + MALFORMED_URL_S);
		}
	}
	
	/**
	 * Sets the page to view.
	 * <p>
	 * @param		url	the URL to view
	 */
	public void setPage(URL url) {
		try {
			html.setPage(url);
		} catch (Exception e) {
			html.setText(URL_NOT_FOUND_P + url.toString() + URL_NOT_FOUND_S);
		}
	}
	
	/**
	 */
	public String getPage() {
		return (html.getPage() == null)? "" : html.getPage().toString();
	}
	
	/**
	 */
	public JEditorPane getJEditorPane() {
		return html;
	}
	
	
	public static void main(String[] params) {
		
		BrowserPane browser = new BrowserPane();
		
		JFrame frame = new JFrame();
		frame.setBounds(300, 150, 400, 450);
		frame.setTitle("Browser");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(browser);
		frame.setVisible(true);
	}
}