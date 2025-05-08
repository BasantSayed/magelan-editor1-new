package org.magelan.editor.extension;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.Action;

import org.magelan.commons.ui.Icons;
import org.magelan.core.entity.HexagonEntity;
import org.magelan.core.editor.DefaultDrawingEditor;
import org.magelan.core.editor.DrawingEditor;
import org.magelan.drawing.DrawingModel;
import org.magelan.drawing.Viewport;
import org.magelan.editor.AbstractEditorExtension;
import org.magelan.editor.AdjustableEditorExtension;

public class NewHexagon
    extends AbstractEditorExtension
    implements AdjustableEditorExtension, MouseListener, MouseMotionListener
{
  private DefaultDrawingEditor editor;
  private double startX, startY;

  public NewHexagon() {
    super();
    putValue(Action.NAME, "Hexagon");
    putValue(Action.SHORT_DESCRIPTION, "Draw a regular hexagon");
    putValue(Action.SMALL_ICON, Icons.getImageIcon("hexagon"));
    putValue(GROUP, "Draw");
  }

  @Override
  public void run(DrawingEditor e) {
    if (e instanceof DefaultDrawingEditor) {
      editor = (DefaultDrawingEditor)e;
      editor.listenMouseEvents(false);
      editor.addMouseListener(this);
      editor.addMouseMotionListener(this);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    DrawingModel dm = editor.getModel();
    Viewport vp = dm.getViewport();
    startX = (e.getX() - vp.getTransX()) / vp.getScaleX();
    startY = (e.getY() - vp.getTransY()) / vp.getScaleY();
    e.consume();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    DrawingModel dm = editor.getModel();
    Viewport vp = dm.getViewport();
    double endX = (e.getX() - vp.getTransX()) / vp.getScaleX();
    double endY = (e.getY() - vp.getTransY()) / vp.getScaleY();

    // compute the bounding box of the drag
    double minX = Math.min(startX, endX);
    double minY = Math.min(startY, endY);
    double w    = Math.abs(endX - startX);
    double h    = Math.abs(endY - startY);

    // create and add a HexagonEntity that always fits into that box
    Rectangle2D bounds = new Rectangle2D.Double(minX, minY, w, h);
    HexagonEntity hex = new HexagonEntity(bounds);
    dm.add(hex);

    // restore normal mouse handling
    editor.removeMouseListener(this);
    editor.removeMouseMotionListener(this);
    editor.listenMouseEvents(true);
    editor.repaint();
    e.consume();
  }

  // no‐ops
  @Override public void mouseDragged(MouseEvent e)  { }
  @Override public void mouseMoved(MouseEvent e)    { }
  @Override public void mouseClicked(MouseEvent e)  { }
  @Override public void mouseEntered(MouseEvent e)  { }
  @Override public void mouseExited(MouseEvent e)   { }
}
