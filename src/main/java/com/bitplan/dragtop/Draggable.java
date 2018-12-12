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

import javafx.scene.Node;

/**
 * draggable items implement this interface
 * @author wf
 *
 */
public interface Draggable {

  /**
   * make me draggable
   */
  public default void activateDrag(Node node) {
    node.setOnMousePressed((t) -> {
      node.getProperties().put("dragSceneX",t.getSceneX());
      node.getProperties().put("dragSceneY",t.getSceneY());
      Node d = (Node) (t.getSource());
      d.toFront();
    });
    node.setOnMouseDragged((event) -> {
      
      Node d = (Node) (event.getSource());
      double offsetX = event.getSceneX() - (double)node.getProperties().get("dragSceneX");
      double offsetY = event.getSceneY() - (double)node.getProperties().get("dragSceneY");

      d.setLayoutX(d.getLayoutX() + offsetX);
      d.setLayoutY(d.getLayoutY() + offsetY);
      node.getProperties().put("dragSceneX",event.getSceneX());
      node.getProperties().put("dragSceneY",event.getSceneY());
    });

  }
}
