package org.magelan.core.entity;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class HexagonEntity extends PolyLineEntity {

    public HexagonEntity(Rectangle2D bounds) {
        super();
        setClosed(true);
        setBounds2D(bounds);
    }

    public Color getStrokeColor() {
        return getColor();
      }
      public void setStrokeColor(Color c) {
        setColor(c);
      }
    
//      public Color getColor() {
//        return getLineColor();
//      }
//      public void setColor(Color c) {
//        setLineColor(c);
//      }

    public void setBounds2D(Rectangle2D b) {
        double cx = b.getCenterX(), cy = b.getCenterY();
        double rx = b.getWidth()  / 2.0, ry = b.getHeight() / 2.0;
        double[] xs = new double[6], ys = new double[6];
        for (int i = 0; i < 6; i++) {
            double ang = Math.toRadians(60 * i - 30);
            xs[i] = cx + rx * Math.cos(ang);
            ys[i] = cy + ry * Math.sin(ang);
        }
        setPoints(xs, ys);
    }
}
