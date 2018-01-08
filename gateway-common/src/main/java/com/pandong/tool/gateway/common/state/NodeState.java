package com.pandong.tool.gateway.common.state;

import com.pandong.tool.gateway.common.Node;
import lombok.Data;

/**
 * @author pandong
 */
@Data
public class NodeState {
  public enum State{
    NORMAL,WARN,FAIL
  }
  private Node node;
  private State state;
}
