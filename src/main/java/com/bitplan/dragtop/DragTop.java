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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.pf4j.DefaultPluginManager;

import com.bitplan.javafx.WaitableApp;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * the DragTop
 * 
 * @author wf
 *
 */
public class DragTop extends WaitableApp {
  private static String[] args;

  @Option(name = "-p", aliases = {
      "--plugins" }, usage = "plugins\ncomma separated list of plugins to load")
  String plugins;
  protected CmdLineParser parser;

  private Scene scene;

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

  private DefaultPluginManager pluginManager;

  /**
   * parse the given Arguments
   * 
   * @param args
   * @throws CmdLineException
   */
  public void parseArguments(String[] args) throws CmdLineException {
    parser = new CmdLineParser(this);
    parser.parseArgument(args);
  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    setup(stage);
    try {
      parseArguments(args);
      activatePlugins();
    } catch (CmdLineException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * activate the plugins requested on the command line
   */
  public void activatePlugins() {
    pluginManager = new DefaultPluginManager();

    if (plugins != null) {

      for (String plugin : plugins.split(",")) {
        Path pluginPath = Paths.get(plugin).toAbsolutePath().normalize();
        pluginManager.loadPlugin(pluginPath);
      }
    }
    pluginManager.startPlugins();
  }

  private void setup(Stage stage) {
    stage.setTitle(title);
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, divX, divY);
    DropTarget region = new DropTarget();
    setScene(
        new Scene(region, sceneBounds.getWidth(), sceneBounds.getHeight()));
    stage.setScene(getScene());
    scene.setFill(Color.LIGHTGRAY);

    stage.setX(super.getScreenWidth() - sceneBounds.getMinX());
    stage.setY(super.getScreenHeight() - sceneBounds.getMinY());
    stage.show();
  }

  /**
   * entry point for application
   * 
   * @param args
   */
  public static void main(String[] args) {
    DragTop.args = args;
    Application.launch(args);
  }
}
