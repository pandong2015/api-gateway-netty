package com.pandong.tool.gateway.common;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pandong
 */
@Slf4j
public class GatewayClientConverter implements Converter {
  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {
    if (o == null || !(o instanceof GatewayClient)) {
      return;
    }
    GatewayClient client = (GatewayClient) o;
    writer.startNode(GatewayConverterName.NODE_NAME_SERVICES);
    ConverterUtil.marshalNode(writer, client.getService());
    writer.endNode();
    writer.startNode(GatewayConverterName.NODE_NAME_GATEWAY);
    ConverterUtil.marshalNode(writer, client.getGateway());
    writer.endNode();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
    GatewayClient client = new GatewayClient();
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      if (reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_SERVICE)) {
        reader.moveDown();
        client.setService(ConverterUtil.unmarshalNode(reader));
        reader.moveUp();
      }
      if (reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_GATEWAY)) {
        reader.moveDown();
        client.setGateway(ConverterUtil.unmarshalNode(reader));
        reader.moveUp();
      }
      reader.moveUp();
    }
    return client;
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass.equals(GatewayClient.class);
  }
}
