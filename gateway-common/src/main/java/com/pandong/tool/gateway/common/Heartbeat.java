package com.pandong.tool.gateway.common;

import com.google.common.collect.Maps;
import com.pandong.tool.gateway.common.jmx.JmxObject;
import lombok.Data;

import java.util.Map;

@Data
public class Heartbeat {
  public static final String OBJECT_NAME_OPERATING_SYSTEM = "java.lang:type=OperatingSystem";
  public static final String[] ATTRIBUTE_NAMES_OPERATING_SYSTEM = new String[]{
          "CommittedVirtualMemorySize",
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
          "AvailableProcessors"};

  public static final String OBJECT_NAME_PS_OLD_GEN = "java.lang:name=PS Old Gen,type=MemoryPool";
  public static final String[] ATTRIBUTE_NAMES_PS_OLD_GEN = new String[]{
          "Name",
          "Type",
          "Valid",
          "CollectionUsage",
          "CollectionUsageThreshold",
          "CollectionUsageThresholdCount",
          "MemoryManagerNames",
          "PeakUsage",
          "Usage",
          "UsageThreshold",
          "UsageThresholdCount",
          "CollectionUsageThresholdExceeded",
          "CollectionUsageThresholdSupported",
          "UsageThresholdExceeded",
          "UsageThresholdSupported"
  };

  public static final String OBJECT_NAME_CLASS_LOADING = "java.lang:type=ClassLoading";
  public static final String[] ATTRIBUTE_NAMES_CLASS_LOADING = new String[]{
          "LoadedClassCount",
          "TotalLoadedClassCount",
          "UnloadedClassCount",
          "Verbose"
  };

  public static final String OBJECT_NAME_RUNTIME = "java.lang:type=Runtime";
  public static final String[] ATTRIBUTE_NAMES_RUNTIME = new String[]{
          "Name",
          "ClassPath",
          "StartTime",
          "InputArguments",
          "ManagementSpecVersion",
          "SpecName",
          "SpecVendor",
          "SpecVersion",
          "SystemProperties",
          "BootClassPath",
          "LibraryPath",
          "Uptime",
          "VmName",
          "VmVendor",
          "VmVersion",
          "BootClassPathSupported"
  };

  public static final String OBJECT_NAME_PS_SCAVENGE = "java.lang:name=PS Scavenge,type=GarbageCollector";
  public static final String[] ATTRIBUTE_NAMES_PS_SCAVENGE = new String[]{
          "LastGcInfo",
          "CollectionCount",
          "CollectionTime",
          "Name",
          "Valid",
          "MemoryPoolNames"
  };

  public static final String OBJECT_NAME_THREADING = "java.lang:type=Threading";
  public static final String[] ATTRIBUTE_NAMES_THREADING = new String[]{
          "ThreadAllocatedMemoryEnabled",
          "ThreadAllocatedMemorySupported",
          "AllThreadIds",
          "CurrentThreadCpuTime",
          "CurrentThreadUserTime",
          "ThreadCount",
          "TotalStartedThreadCount",
          "ThreadCpuTimeSupported",
          "ThreadContentionMonitoringEnabled",
          "ThreadCpuTimeEnabled",
          "DaemonThreadCount",
          "PeakThreadCount",
          "CurrentThreadCpuTimeSupported",
          "ObjectMonitorUsageSupported",
          "SynchronizerUsageSupported",
          "ThreadContentionMonitoringSupported"
  };

  public static final String OBJECT_NAME_CODE_CACHE_MANAGER ="java.lang:name=CodeCacheManager,type=MemoryManager";
  public static final String[] ATTRIBUTE_NAMES_CODE_CACHE_MANAGER = new String[]{
          "Name",
          "Valid",
          "MemoryPoolNames"
  };

  public static final String OBJECT_NAME_PS_EDEN_SPACE = "java.lang:name=PS Eden Space,type=MemoryPool";
  public static final String[] ATTRIBUTE_NAMES_PS_EDEN_SPACE = new String[]{
          "Name",
          "Type",
          "Valid",
          "CollectionUsage",
          "CollectionUsageThreshold",
          "CollectionUsageThresholdCount",
          "MemoryManagerNames",
          "PeakUsage",
          "Usage",
          "UsageThreshold",
          "UsageThresholdCount",
          "CollectionUsageThresholdExceeded",
          "CollectionUsageThresholdSupported",
          "UsageThresholdExceeded",
          "UsageThresholdSupported"
  };

  public static final String OBJECT_NAME_PS_SURVIVOR_SPACE = "java.lang:name=PS Survivor Space,type=MemoryPool";
  public static final String[] ATTRIBUTE_NAMES_PS_SURVIVOR_SPACE = new String[]{
          "Name",
          "Type",
          "Valid",
          "CollectionUsage",
          "CollectionUsageThreshold",
          "CollectionUsageThresholdCount",
          "MemoryManagerNames",
          "PeakUsage",
          "Usage",
          "UsageThreshold",
          "UsageThresholdCount",
          "CollectionUsageThresholdExceeded",
          "CollectionUsageThresholdSupported",
          "UsageThresholdExceeded",
          "UsageThresholdSupported"
  };

  public static final String OBJECT_NAME_PS_MARK_SWEEP = "java.lang:name=PS MarkSweep,type=GarbageCollector";
  public static final String[] ATTRIBUTE_NAMES_PS_MARK_SWEEP = new String[]{
          "LastGcInfo",
          "CollectionCount",
          "CollectionTime",
          "Name",
          "Valid",
          "MemoryPoolNames"
  };

  public static final String OBJECT_NAME_MEMORY = "java.lang:type=Memory";
  public static final String[] ATTRIBUTE_NAMES_MEMORY = new String[]{
          "Verbose",
          "HeapMemoryUsage",
          "NonHeapMemoryUsage",
          "ObjectPendingFinalizationCount"
  };

  public static final String[] OBJECT_NAMES = new String[]{
          OBJECT_NAME_PS_OLD_GEN,
          OBJECT_NAME_CLASS_LOADING,
          OBJECT_NAME_CODE_CACHE_MANAGER,
          OBJECT_NAME_MEMORY,
          OBJECT_NAME_OPERATING_SYSTEM,
          OBJECT_NAME_PS_EDEN_SPACE,
          OBJECT_NAME_PS_MARK_SWEEP,
          OBJECT_NAME_PS_SCAVENGE,
          OBJECT_NAME_PS_SURVIVOR_SPACE,
          OBJECT_NAME_RUNTIME,
          OBJECT_NAME_THREADING
  };

  private Map<String, JmxObject> heartbeatMap = Maps.newHashMap();

  public void add(String objectName, JmxObject object){
    heartbeatMap.put(objectName, object);
  }

  public JmxObject get(String objectName){
    return heartbeatMap.get(objectName);
  }
}
