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
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.controlsfx.control.ToggleSwitch;

import com.bitplan.gui.Linker;
import com.bitplan.javafx.Link;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * a dragItem represented as a card
 * 
 * @author wf
 *
 */
public class Card extends AnchorPane implements DragItem {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.dragtop");

  public boolean debug = false;

  double iconSize = 32.0;
  double previewSize = 64.0;
  static float marginX = 6.0f;
  static float marginY = 6.0f;

  double cardWidth = 204;
  double cardHeight = 144;

  private ToggleSwitch toggle;

  Vertex vertex;

  boolean tool = false;
  boolean toolOn = false;

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

  public String pluginId;

  public boolean isToolOn() {
    return toolOn;
  }

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
    tool = v.property("tool").isPresent();

    setup();
  }
  
  /**
   * set up a card from a given uri
   * @param uri
   * @param linker
   */
  public Card(URI uri,Linker linker) {
     this.fileType="html";
     this.linker=linker;
     this.url=uri.toString();
     String[] segments = uri.getPath().split("/");
     this.name=segments.length>0?segments[segments.length-1]:url;
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
    switch (fileType) {
    case "jar":
    case "rythm":
      tool = true;
      break;
    }
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
    this.setHeight(cardHeight);
    this.setWidth(cardWidth);
    setupTool(tool);
    setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
        new CornerRadii(10), BorderWidths.DEFAULT)));

    fileTypeImage = FileIcon.getFileIcon(fileType);
    fileTypeImageView = addImageView(fileTypeImage,
        this.getPrefWidth() - iconSize, 0, iconSize);
    if (iconUrl != null) {
      iconImage = new Image(iconUrl, iconSize, iconSize, false, false);
      iconImageView = addImageView(iconImage,
          this.getPrefWidth() - iconSize * 2, 0, iconSize);
    }
    if (previewUrl != null && !previewUrl.isEmpty()) {
      previewImage = new Image(previewUrl, previewSize, previewSize, false,
          false);
      previewImageView = addImageView(previewImage, marginX, 0, previewSize);
    }
    link = new Link(url, name, linker);
    // How to remove hyperlink border in JavaFX?
    // https://stackoverflow.com/a/40592738/1497139
    link.setBorder(Border.EMPTY);
    link.setPadding(new Insets(4, 0, 4, 0));
    link.setLayoutX(marginX);
    link.setLayoutY(previewSize + marginY);

    this.getChildren().add(link);

    if (tool) {
      // https://stackoverflow.com/a/32603027/1497139
      toggle = new ToggleSwitch();
      toolOn = true;
      toggle.setSelected(toolOn);

      this.getChildren().add(toggle);
      toggle.setLayoutX(this.getPrefWidth() - iconSize * 2.0);
      toggle.setLayoutY(iconSize + marginY * 2);
      toggle.selectedProperty().addListener((o, old, newValue) -> {
        toolOn = newValue;
        updateTool(newValue);
      });
    }
    updateTool(tool ? toggle.isSelected() : false);

    this.activateDrag(this);
  }

  /**
   * setup the tool
   * 
   * @param tool
   */
  private void setupTool(boolean tool) {
    if (tool) {
      this.setPrefHeight(cardHeight);
      this.setPrefWidth(cardHeight);
      this.setStyle("-fx-background-color: #f0f8ff;");
    } else {
      this.setPrefHeight(cardHeight);
      this.setPrefWidth(cardWidth);
      this.setStyle("-fx-background-color: #fcfcf8;-fx-background-radius:10");
    }
  }

  public void startTool() {
    Platform.runLater(() -> toggle.setSelected(true));
  }

  /**
   * update the view if the tool swith changes
   * 
   * @param toolOn
   */
  private void updateTool(boolean toolOn) {
    setupTool(toolOn); // show me as a tool or not
    double xOfs = cardWidth - cardHeight;
    if (toolOn) {
      this.setTranslateX(xOfs);
      toggle.setTranslateX(0);
      if (iconImageView != null)
        this.iconImageView.setTranslateX(0);
      this.fileTypeImageView.setTranslateX(0);
      // this.getTransforms().add(new Rotate(45,Rotate.X_AXIS));
    } else {
      this.setTranslateX(0);
      if (toggle != null) {
        toggle.setTranslateX(xOfs);
        if (iconImageView != null)
          this.iconImageView.setTranslateX(xOfs);
        this.fileTypeImageView.setTranslateX(xOfs);
      }
    }
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

    // AnchorPane.setTopAnchor(imageView, y+marginY);
    // AnchorPane.setRightAnchor(imageView, x);
    imageView.setLayoutX(x);
    imageView.setLayoutY(y + marginY);
    this.getChildren().add(imageView);
    return imageView;
  }

  @Override
  public Object getItem() {
    return item;
  }

}
