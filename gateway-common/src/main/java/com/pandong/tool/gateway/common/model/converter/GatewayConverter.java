package com.pandong.tool.gateway.common.model.converter;

import com.pandong.tool.gateway.common.model.Gateway;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author pandong
 */
public class GatewayConverter implements Converter {


  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
    if (o == null || !(o instanceof Gateway)) {
      return;
    }
    Gateway gateway = (Gateway) o;
    writer.startNode(GatewayConverterName.NODE_NAME_SERVICES);
    if (gateway.getServices() != null && !gateway.getServices().isEmpty()) {
      gateway.getServices().forEach(service -> ConverterUtil.marshalService(writer, service));
    }
    writer.endNode();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    Gateway gateway = new Gateway();
    reader.moveDown();
    if (reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_SERVICES)) {
      while (reader.hasMoreChildren()) {
        reader.moveDown();
        gateway.addService(ConverterUtil.unmarshalService(reader));
        reader.moveUp();
      }
    }
    reader.moveUp();
    return gateway;
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass.equals(Gateway.class);
  }
}
