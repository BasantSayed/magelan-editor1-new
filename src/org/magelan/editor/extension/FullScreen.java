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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.magelan.commons.Lang;
import org.magelan.commons.ui.Icons;
import org.magelan.core.*;
import org.magelan.drawing.*;
import org.magelan.core.editor.DefaultDrawingEditor;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;
import org.magelan.editor.Editor;
import org.magelan.editor.ui.EditorPopupMenu;
import svgc.CallBackable;

/**
 *
 * @author Bernard Desprez
 * @version 1.0, June 2004
 */
public class FullScreen
	extends AbstractEditorExtension
	implements AdjustableEditorExtension, MouseListener, MouseMotionListener, CallBackable {

	private static Lang lang = Lang.getLang(Editor.STRINGS);
	private int x;
	private int y;
	private int Ox, Oy;
	private int panx = 0, pany = 0;

	private boolean inpan = false;
	private boolean drag = false;

	private DefaultDrawingEditor editor;
	private JFrame frame;
	private FscrPopupMenu PM;
	private FullScreenPane FP;

	/**
	     * Creates a new FullScreen object.
	 */
	public FullScreen() {
		super();

		putValue(Action.NAME, "FullScreen"); //$NON-NLS-1$
		putValue(AbstractEditorExtension.VERSION, "1.0, June 20042");
		putValue(AbstractEditorExtension.AUTHOR, "Bernard Desprez");
		putValue(Action.SHORT_DESCRIPTION, lang.getString("Show_full_screen"));
		putValue(
			Action.LONG_DESCRIPTION,
			lang.getString("Show_drawing_in_full_screen_mode"));
		putValue(Action.SMALL_ICON, Icons.getImageIcon("FSCR"));
		putValue(AbstractEditorExtension.GROUP, lang.getString("Navigate_7"));
	}

	/**
	     * @method run
	 *
	     * @param e DrawingEditor
	 */
	public void run(DrawingEditor e) {
		if (e instanceof DefaultDrawingEditor) {
			editor = (DefaultDrawingEditor) e;
			editor.listenMouseEvents(false);
			editor.addMouseListener(this);
			editor.addMouseMotionListener(this);
		}
		if (PM == null)
			PM = new FscrPopupMenu((CallBackable) this);
		frame = new JFrame("Magelan FullScreen mode");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				return;
			}
		});
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();

		frame.setSize(screenSize.width, screenSize.height);
		frame.getContentPane().setLayout(new BorderLayout());
		FP = new FullScreenPane();
		frame.getContentPane().add(FP);
		frame.show();
	}

	/**
	     * @method mouseClicked
	 *
	     * @param e MouseEvent
	 */

	public void mouseClicked(MouseEvent e) {

		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			if (inpan) {
				panx = x;
				pany = y;
				x = 0;
				y = 0;
				inpan = false;
				drag = false;
			}
			//                  PM.setPopupSize(30,45*5); // noway to reduce it
			PM.show((Component) e.getComponent(), e.getX(), e.getY());
			return;
		}
		if (inpan) {
			x = e.getX();
			Ox = x;
			y = e.getY();
			Oy = y;
			drag = false;
			return;
		}
		editor.mouseClicked(updateEvent(e));
		e.consume();
		FP.repaint();
	}

	public void mouseMoved(MouseEvent e) {
		if (!inpan)
			editor.mouseMoved(updateEvent(e));
		e.consume();
		FP.repaint();

	}

	public void mousePressed(MouseEvent e) {
		if (inpan) {
			if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
				x = e.getX();
				Ox = x;
				y = e.getY();
				Oy = y;
			}
		} else
			editor.mousePressed(updateEvent(e));
		e.consume();
		FP.repaint();

	}

	public void mouseReleased(MouseEvent e) {
		if (!inpan) {
			editor.mouseReleased(updateEvent(e));
		} else {
			panx = x;
			pany = y;
		}
		e.consume();
		FP.repaint();

	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
		if (inpan) {
			drag = true;
			x = Ox - e.getX();
			x += panx;
			y = Oy - e.getY();
			y += pany;
		} else
			editor.mouseDragged(updateEvent(e));
		e.consume();
		FP.repaint();

	}

	/**
	 *@method updateEvent it will look like comming from the scroll pane
	 *
	 *@parm original MouseEvent
	 */

	private MouseEvent updateEvent(MouseEvent e) {
		Insets ins = frame.getInsets();
		e.translatePoint(-ins.left, -ins.top);
		return e;
	}

	/**
	  *  @method activateCallBack Called by various event
	  *  can not be synchronized
	  *  @parm  routine number and an object
	  */

	public Object activateCallBack(int rtne, Object obj) {
		if (rtne == 5) {
			editor.removeMouseListener(this);
			editor.removeMouseMotionListener(this);
			editor.listenMouseEvents(true);

			PM.setVisible(false);
			frame.setVisible(false);
		}
		// We can try actionperformed find the right extension then call run
		// too much mess for a couple of instructions
		else if (rtne == 0 || rtne == 1) {
			DrawingModel dwg = editor.getModel();
			Viewport vp = dwg.getViewport();
			if (rtne == 0) {
				vp.setScaleX(vp.getScaleX() * 2);
				vp.setScaleY(vp.getScaleY() * 2);

			} else if (rtne == 1) {
				vp.setScaleX(vp.getScaleX() / 2);
				vp.setScaleY(vp.getScaleY() / 2);
			}
			FP.repaint();
		} else if (rtne == 2) { //realtime
		} else if (rtne == 3) { //pan
			inpan = true;
		} else if (rtne == 4) { //original view
			inpan = false;
			drag = false;
			x = 0;
			Ox = 0;
			panx = 0;
			y = 0;
			Oy = 0;
			pany = 0;
		}

		return null;
	}

	/*
	 * internal class to handle painting
	 */

	public class FullScreenPane extends JPanel {

		public FullScreenPane() {
			super();
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.white); // one day get user choice
			Dimension d = getSize();
			g2.fill(
				new java.awt.Rectangle(
					0,
					0,
					(int) d.getWidth(),
					(int) d.getHeight()));
			AffineTransform atx = new AffineTransform();
			if (inpan && drag) {
				/*        Drawing dwg = editor.getDrawing();// don't work
				VPort vp = dwg.getViewport();
				        vp.transformX(x);
				        vp.transformY(y);
				
				  */
				atx.translate(x, y);
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} else {
				atx.translate(panx, pany);
				setCursor(Cursor.getDefaultCursor());
			}
			g2.setTransform(atx);
			DrawingModel dwg = editor.getModel();
			editor.getRenderer().paint(dwg, g2);
			drawHandles(g2);
			// with the following we only get the visible part of the
			// scroll Pane so only a part of the markers
			//               g.drawImage(editor.getBuffer(), 0, 0, this);

		}

	}

	/**
	 *@method drawHandles
	 */

	private void drawHandles(Graphics g) {
		List selection = editor.getSelection();
		Iterator iter = selection.iterator();
		g.setPaintMode();
		g.setColor((Color) editor.getValue(DrawingEditor.KEY_HANDLE_COLOR));
		Viewport vp = editor.getModel().getViewport();
		int size =
			((Integer) editor.getValue(DrawingEditor.KEY_HANDLE_SIZE))
				.intValue();

		while (iter.hasNext()) {
			Entity entity = (Entity) iter.next();
			EntityModifier[] modifiers = entity.getModifiers();
			for (int i = 0; i < modifiers.length; i++) {
				double x = modifiers[i].getX();
				double y = modifiers[i].getY();

				g.drawRect(
					vp.transformX(x) - size / 2,
					vp.transformY(y) - size / 2,
					size,
					size);
			}

		}
	}

	/**
	* FscrPopupMenu
	*
	* @author Bernard Desprez
	* @version 1.0 June 2004
	*/
	public class FscrPopupMenu extends EditorPopupMenu {

		private final boolean dbg = true;
		private CallBackable cb;

		private String[] icons =
			{ "zoom_in", "zoom_out", "zoom_r", "pan", "Original", "preturn" };
		private String[] tips =
			{
				lang.getString("Zoom_In_4"),
				lang.getString("Zoom_out_4"),
				lang.getString("Zoom_realtime_4"),
				lang.getString("Pan_4"),
				lang.getString("Original_view"),
				lang.getString("end_drawing")};
		private JMenuItem[] items = new JMenuItem[icons.length];

		/**
		     * Creates a new FscrPopupMenu
		 */

		public FscrPopupMenu(CallBackable cb) {
			this.cb = cb;
			for (int i = 0; i < icons.length; i++) {
				items[i] = new JMenuItem(Icons.getImageIcon(icons[i]));
				items[i].setText(tips[i]);
				items[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						handleEvent(evt);
					}
				}); // end ActionListener
				add(items[i]);
			} // for
			//              setPopupSize(62,25*icons.length);
			pack();

		}

		/**
		 *@method handleEvent
		 *
		 *@param ActionEvent
		 */

		public void handleEvent(ActionEvent evt) {
			int b = 0;
			//if(dbg) System.out.println("handle "+evt);
			for (; b < items.length; b++) {
				if (evt.getSource() == items[b])
					break;
			}
			//if(dbg) System.out.println(b+" icon "+icons[b]);
			cb.activateCallBack(b, evt);
		}
	}
}
