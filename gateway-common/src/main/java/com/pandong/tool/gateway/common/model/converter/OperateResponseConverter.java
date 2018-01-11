package com.pandong.tool.gateway.common.model.converter;

import com.pandong.common.units.StringUtils;
import com.pandong.tool.gateway.common.model.GatewayProto;
import com.pandong.tool.gateway.common.model.OperateResonse;
import com.pandong.tool.gateway.common.model.converter.GatewayConverterName;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author pandong
 */
public class OperateResponseConverter implements Converter {
  @Override
  public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext marshallingContext) {
    if (o == null || !(o instanceof OperateResonse)) {
      return;
    }
    OperateResonse resonse = (OperateResonse) o;
    writer.startNode(GatewayConverterName.NODE_NAME_RESPONSE);
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_OPERATE, resonse.getType().name());
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_STATUS, String.valueOf(resonse.getStatus()));
    writer.addAttribute(GatewayConverterName.ATTRIBUT_NAME_MESSAGE, resonse.getMessage());
    writer.endNode();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
    OperateResonse resonse = null;

    reader.moveDown();
    if(reader.getNodeName().equalsIgnoreCase(GatewayConverterName.NODE_NAME_RESPONSE)){
      String msg = reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_MESSAGE);
      int status = 0;
      if(!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_STATUS))) {
        status = Integer.parseInt(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_STATUS));
      }
      GatewayProto.OperationType type = null;
      if(!StringUtils.isNull(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_OPERATE))) {
        type = (GatewayProto.OperationType.valueOf(reader.getAttribute(GatewayConverterName.ATTRIBUT_NAME_STATUS)));
      }
      resonse = OperateResonse.builder().message(msg).status(status).type(type).build();
    }
    reader.moveUp();
    return resonse;
  }

  @Override
  public boolean canConvert(Class aClass) {
    return aClass.equals(OperateResonse.class);
  }
}
