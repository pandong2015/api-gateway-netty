package com.pandong.tool.gateway.client.test;

import javax.management.*;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ManagementFactoryTest {
  public static void main(String[] args) throws Exception {
//    Heartbeat heartbeat = Heartbeat.build();
//    String xml = Global.object2Xml(heartbeat);
//    System.out.println(xml);
//    heartbeat = Global.string2Obj(xml, new Heartbeat());
//
//    System.out.println(heartbeat);

    Files.list(Paths.get("/")).forEach(path -> {
      System.out.println(path);
    });
    File[] roots = File.listRoots();//获取磁盘分区列表
    for (File file : roots) {
      System.out.println(file.getPath()+"信息如下:");
      System.out.println("空闲未使用 = " + file.getFreeSpace()/1024/1024/1024+"G");//空闲空间
      System.out.println("已经使用 = " + file.getUsableSpace()/1024/1024/1024+"G");//可用空间
      System.out.println("总容量 = " + file.getTotalSpace()/1024/1024/1024+"G");//总空间
      System.out.println();
    }


//    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//    printObject(mbs, Heartbeat.OBJECT_NAME_RUNTIME, "SystemProperties");
//    allObject(mbs);

//    Heartbeat.OBJECT_ATTRIBUTS_HEARTBEAT_MAPPING.entrySet().stream().forEach(entry -> {
//      try {
//        printObject(mbs, entry.getKey(), entry.getValue());
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    });

  }

  private static void printObject(MBeanServer mbs, String name, String... attributeNames) throws Exception {
    ObjectName objectName = ObjectName.getInstance(name);
    AttributeList list = mbs.getAttributes(objectName, attributeNames);
    list.stream().map(o -> (Attribute) o)
            .filter(attribute -> attribute!=null)
            .forEach(attribute -> ptintValue(attribute.getName(), attribute.getValue()));
  }

  private static void allObject(MBeanServer mbs) throws Exception {
    Set MBeanset = mbs.queryMBeans(null, null);
    Iterator MBeansetIterator = MBeanset.iterator();
    while (MBeansetIterator.hasNext()) {
      ObjectInstance objectInstance = (ObjectInstance) MBeansetIterator
              .next();
      ObjectName objectName = objectInstance.getObjectName();
      MBeanInfo objectInfo = mbs.getMBeanInfo(objectName);
      System.out.println(objectName.getCanonicalName());
      for (int i = 0; i < objectInfo.getAttributes().length; i++) {
        System.out.println("\t\t" + objectInfo.getAttributes()[i].getName());
      }
//      System.out.println();
    }
  }

  public static void ptintValue(String name, CompositeDataSupport compositeDataSupport){
    compositeDataSupport.getCompositeType().keySet().stream().forEach(name1->{
      System.out.print(name+"-"+name1);
      System.out.print(" : ");
      System.out.print(compositeDataSupport.getCompositeType().getType(name1).getTypeName());
      System.out.print( " : ");
      System.out.println(compositeDataSupport.get(name1));
    });
  }

  public static void ptintValue(String name, TabularDataSupport tabularDataSupport){
    tabularDataSupport.entrySet().forEach(entry->{
      System.out.println(((List)entry.getKey()).get(0));
//      ptintValue(name, (CompositeDataSupport)entry.getValue());
    });
  }

  public static void ptintValue(String value){
    System.out.println(value);
  }

  public static void ptintValue(String name, Object obj){
    if(obj instanceof CompositeDataSupport) {
      ptintValue(name, (CompositeDataSupport) obj);
    }else if(obj instanceof TabularDataSupport){
      ptintValue(name, (TabularDataSupport) obj);
    }else {
      System.out.println(name+" : "+obj.getClass().getName());
    }
  }
}
