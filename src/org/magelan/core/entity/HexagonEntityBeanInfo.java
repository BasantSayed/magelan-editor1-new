package org.magelan.core.entity;

import java.awt.Color;
import java.beans.*;
import java.util.ArrayList;
import java.util.List;

public class HexagonEntityBeanInfo extends SimpleBeanInfo {
  @Override
  public BeanInfo[] getAdditionalBeanInfo() {
    // pick up all the normal PolyLineEntity props, if you still want them
    try {
      return new BeanInfo[]{ Introspector.getBeanInfo(PolyLineEntity.class) };
    } catch (IntrospectionException e) {
      return null;
    }
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      List<PropertyDescriptor> list = new ArrayList<>();

      // only the Color, Layer and Thickness properties, for example:
      PropertyDescriptor color =
        new PropertyDescriptor("color",
                               HexagonEntity.class,
                               "getColor",
                               "setColor");
      color.setDisplayName("Color");
      color.setShortDescription("Fill & outline color");
      list.add(color);

      PropertyDescriptor layer =
        new PropertyDescriptor("layer", HexagonEntity.class);
      layer.setDisplayName("Layer");
      list.add(layer);

      PropertyDescriptor thickness =
        new PropertyDescriptor("thickness", HexagonEntity.class);
      thickness.setDisplayName("Stroke Thickness");
      list.add(thickness);

      return list.toArray(new PropertyDescriptor[0]);
    }
    catch (IntrospectionException ex) {
      ex.printStackTrace();
      return new PropertyDescriptor[0];
    }
  }
}
