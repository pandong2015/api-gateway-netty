package com.pandong.tool.gateway.common.model.converter;

import com.pandong.common.units.StringUtils;
import com.pandong.tool.gateway.common.jmx.JmxObject;
import com.pandong.tool.gateway.common.model.Heartbeat;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author pandong
 */
public class HeartbeatConverter implements Converter {


  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
    if (o == null || !(o instanceof Heartbeat)) {
      return;
    }
    Heartbeat heartbeat = (Heartbeat) o;
    writer.startNode(GatewayConverterName.NODE_NAME_HEARTBEAT);
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_TIMESTAMP, String.valueOf(heartbeat.getTimestamp()));
    heartbeat.getNames().forEach(name -> {
      writer.startNode(GatewayConverterName.NODE_NAME_NODE);
      writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_NAME, name);
      JmxObject jmxObject = heartbeat.get(name);
      if (jmxObject != null) {
        writer.startNode(jmxObject.getName());
        jmxObject.getAttributeNames().forEach(attrName -> {
          writer.startNode(attrName);
          writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_VALUE, jmxObject.get(attrName));
          writer.endNode();
        });
        writer.endNode();
      }
      writer.endNode();
    });
    writer.endNode();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    Heartbeat heartbeat = new Heartbeat();
    reader.moveDown();
    if (!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_TIMESTAMP))) {
      heartbeat.setTimestamp(Long.parseLong(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_TIMESTAMP)));
    }
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      switch (reader.getNodeName()) {
        case GatewayConverterName.NODE_NAME_NODE:
          String name = reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_NAME);
          reader.moveDown();
          JmxObject jmxObject = new JmxObject();
          jmxObject.setName(reader.getNodeName());
          while (reader.hasMoreChildren()) {
            reader.moveDown();
            jmxObject.add(reader.getNodeName(), reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_VALUE));
            reader.moveUp();
          }
          heartbeat.add(name, jmxObject);
          reader.moveUp();
          break;
      }
      reader.moveUp();
    }
    reader.moveUp();
    return heartbeat;
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass.equals(Heartbeat.class);
  }
}
