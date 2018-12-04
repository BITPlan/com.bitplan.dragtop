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

import com.bitplan.javafx.WaitableApp;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DragTopApp extends WaitableApp {
  private Scene scene;
  private String plugins;

  /**
   * construct me with the given plugins
   * 
   * @param plugins
   */
  public DragTopApp(String plugins) {
    this.plugins = plugins;
  }

  public Scene getScene() {
    return scene;
  }

  public void setScene(Scene scene) {
    this.scene = scene;
  }

  String title = "Drag & Drop Here";
  int screenPercent = 33;
  int divX = 3;
  int divY = 3;

  private DropTarget dropTarget;

  /**
   * setup the GUI
   * 
   * @param stage
   */
  private void setup(Stage stage) {
    stage.setTitle(title);
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, divX, divY);
    dropTarget = new DropTarget();
    setScene(
        new Scene(dropTarget, sceneBounds.getWidth(), sceneBounds.getHeight()));
    stage.setScene(getScene());
    scene.setFill(Color.LIGHTGRAY);

    stage.setX(super.getScreenWidth() - sceneBounds.getMinX());
    stage.setY(super.getScreenHeight() - sceneBounds.getMinY());
    stage.show();
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    setup(stage);
    DropTarget.debug = debug;
    dropTarget.activatePlugins(plugins);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
}
