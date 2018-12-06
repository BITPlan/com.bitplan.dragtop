/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.dragtop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.dragtop;

import java.util.function.Consumer;

import org.pf4j.ExtensionPoint;

/**
 * handle drop events
 * @author wf
 *
 */
public interface DropHandler extends ExtensionPoint {
  /**
   * get the handler
   * @return the handler
   */
  public Consumer<DragItem> getHandler();
  /**
   * set the handler
   * @param handler
   */
  public void setHandler( Consumer<DragItem> handler);
  /**
   * is the dropHandler state on or off?
   * @return the state - true if on
   */
  public boolean isOn();
  /**
   * set the dropHandler state to on or off
   * @param pOn
   */
  public void setOn(boolean pOn);
}
