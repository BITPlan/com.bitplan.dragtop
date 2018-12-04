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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * a draggable Item
 * 
 * @author wf
 *
 */
public class DragItem extends Label {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.dragtop");
  public static boolean debug = false;

  private Image image;

  private double dragSceneY;
  private double dragSceneX;
  double iconSize=64;
  Object item;

  public Object getItem() {
    return item;
  }

  public void setItem(Object item) {
    this.item = item;
  }

  /**
   * create a drag item for the given file
   * 
   * @param file
   */
  public DragItem(File file) {
    this.item=file;
    String filePath = file.getAbsolutePath();
    if (debug) {
      LOGGER.log(Level.INFO,"Dragitem for file "+filePath+" created");
    }
    image = FileIcon.getFileIcon(file);
    ImageView imageView = new ImageView();
    imageView.setImage(image);
    imageView.setScaleX(iconSize/image.getWidth());
    imageView.setScaleY(iconSize/image.getHeight());

    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    setGraphic(imageView);
    Tooltip tooltip = new Tooltip(filePath);
    setTooltip(tooltip);
    activateDrag();
  }

  private void activateDrag() {
    setOnMousePressed((t) -> {
      dragSceneX = t.getSceneX();
      dragSceneY = t.getSceneY();
      DragItem d = (DragItem) (t.getSource());
      d.toFront();
    });
    setOnMouseDragged((event) -> {
      DragItem d = (DragItem) (event.getSource());
      double offsetX = event.getSceneX() - dragSceneX;
      double offsetY = event.getSceneY() - dragSceneY;

      d.setLayoutX(d.getLayoutX() + offsetX);
      d.setLayoutY(d.getLayoutY() + offsetY);
      dragSceneX = event.getSceneX();
      dragSceneY = event.getSceneY();
    });

  }
}
