package com.pandong.tool.gateway.client.test;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

public class ManagementFactoryTest {
  public static void main(String[] args) throws Exception {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

//    allObject(mbs);

    printObject(mbs, "java.lang:type=OperatingSystem", "CommittedVirtualMemorySize",
            "FreePhysicalMemorySize",
            "FreeSwapSpaceSize",
            "ProcessCpuLoad",
            "ProcessCpuTime",
            "SystemCpuLoad",
            "TotalPhysicalMemorySize",
            "TotalSwapSpaceSize",
            "Name",
            "Version",
            "Arch",
            "SystemLoadAverage",
            "AvailableProcessors",
            "ObjectName");

  }

  private static void printObject(MBeanServer mbs, String name, String...attributeNames) throws Exception{
    ObjectName objectName = ObjectName.getInstance(name);
    AttributeList list = mbs.getAttributes(objectName, attributeNames);
    list.stream().map(o -> (Attribute)o).forEach(attribute -> System.out.println(attribute.getName()+" -- "+attribute.getValue()));
  }

  private static void allObject(MBeanServer mbs) throws Exception{
    Set MBeanset = mbs.queryMBeans(null, null);
    Iterator MBeansetIterator = MBeanset.iterator();
    while (MBeansetIterator.hasNext()) {
      ObjectInstance objectInstance = (ObjectInstance) MBeansetIterator
              .next();
      ObjectName objectName = objectInstance.getObjectName();
      MBeanInfo objectInfo = mbs.getMBeanInfo(objectName);
      System.out.println(objectName.getCanonicalName());
      for (int i = 0; i < objectInfo.getAttributes().length; i++) {
        System.out.println("\t\t"+objectInfo.getAttributes()[i].getName());
      }
//      System.out.println();
    }
  }
}
