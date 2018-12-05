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
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.controlsfx.control.ToggleSwitch;

import com.bitplan.gui.Linker;
import com.bitplan.javafx.Link;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Card extends AnchorPane implements DragItem {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.dragtop");

  public boolean debug = false;

  double iconSize = 32.0;
  double previewSize = 64.0;
  double marginY = 6.0;
  double cardWidth = 297.0 * 2 / 3;
  double cardHeight = 210.0 * 2 / 3;

  private ToggleSwitch toggle;

  Vertex vertex;

  boolean tool = false;

  private String url;
  private Link link;
  private String name;
  private String iconUrl;

  private String fileType;
  private Image fileTypeImage;
  ImageView fileTypeImageView;

  private Image iconImage;
  ImageView iconImageView;

  private String previewUrl;

  private Image previewImage;
  ImageView previewImageView;
  private Linker linker;
  Object item;

  /**
   * create a card for the given vertex
   * 
   * @param v
   *          - the vertex
   */
  public Card(Vertex v, Linker linker) {
    this.linker = linker;
    this.vertex = v;

    this.fileType = v.property("fileType").value().toString();
    this.name = v.property("name").value().toString();
    this.iconUrl = v.property("iconUrl").value().toString();
    this.previewUrl = v.property("previewUrl").value().toString();
    this.url = v.property("url").value().toString();

    setup();
  }

  /**
   * create card
   * 
   * @param file
   * @param linker
   * @throws Exception
   */
  public Card(File file, Linker linker) {
    this.linker = linker;
    this.item = file;
    String filePath = file.getAbsolutePath();
    if (debug) {
      LOGGER.log(Level.INFO, "Dragitem for file " + filePath + " created");
    }
    fileType = FileIcon.getFileExt(filePath);
    name = file.getName();
    try {
      url = file.toURI().toURL().toString();
    } catch (MalformedURLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    setup();
  }

  /**
   * 
   */
  public void setup() {
    this.setPrefHeight(cardHeight);
    this.setPrefWidth(cardWidth);
    this.setStyle("-fx-background-color: #fcfcf8;");
    setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
        CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    this.setWidth(cardWidth);
    this.setHeight(cardHeight);

    fileTypeImage = FileIcon.getFileIcon(fileType);
    fileTypeImageView = addImageView(fileTypeImage,
        this.getPrefWidth() - iconSize, 0, iconSize);
    if (iconUrl != null) {
      iconImage = new Image(iconUrl, iconSize, iconSize, false, false);
      iconImageView = addImageView(iconImage,
          this.getPrefWidth() - iconSize * 2, 0, iconSize);
    }
    if (previewUrl != null) {
      previewImage = new Image(previewUrl, previewSize, previewSize, false,
          false);
      previewImageView = addImageView(previewImage, 0, 0, previewSize);
    }
    link = new Link(url, name, linker);
    link.setLayoutY(previewSize + marginY);
    this.getChildren().add(link);

    if (tool) {
      toggle = new ToggleSwitch();
      this.getChildren().add(toggle);
      toggle.setLayoutX(this.getPrefWidth() - iconSize * 2.0);
      toggle.setLayoutY(iconSize);
    }
    this.activateDrag(this);
  }

  /**
   * add an imageView of the given image at the given x and y position
   * 
   * @param image
   * @param x
   * @param y
   * @param size
   *          - the size to scale to
   * @return the imageView
   */
  public ImageView addImageView(Image image, double x, double y, double size) {
    ImageView imageView = new ImageView();

    imageView.setFitHeight(size);
    imageView.setFitWidth(size);
    imageView.setPreserveRatio(true);
    imageView.setImage(image);

    imageView.setLayoutX(x);
    imageView.setLayoutY(y+marginY);
    this.getChildren().add(imageView);
    return imageView;
  }

  @Override
  public Object getItem() {
    return item;
  }

}
