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
import java.io.IOException;

import org.controlsfx.control.StatusBar;

import com.bitplan.javafx.WaitableApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * the Java FX application for the drag top
 * 
 * @author wf
 *
 */
public class DragTopApp extends WaitableApp {
  private Scene scene;
  private String plugins;
  private String graphPath;

  /**
   * construct me with the given plugins
   * 
   * @param graphPath
   * @param plugins
   */
  public DragTopApp(String graphPath, String plugins) {
    this.graphPath = graphPath;
    this.plugins = plugins;
    dropTarget = new DropTarget(this);
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

  public DropTarget getDropTarget() {
    return dropTarget;
  }

  private StatusBar statusBar;
  private MenuBar menu;
  private File graphMl;

  /**
   * setup the GUI
   * 
   * @param stage
   */
  private void setup(Stage stage) {
    Rectangle2D sceneBounds = super.getSceneBounds(screenPercent, divX, divY);

    menu = createMenu();
    dropTarget.setTop(menu);
    statusBar = new StatusBar();
    dropTarget.setBottom(statusBar);
    setScene(
        new Scene(dropTarget, sceneBounds.getWidth(), sceneBounds.getHeight()));
    stage.setScene(getScene());
    scene.setFill(Color.LIGHTGRAY);

    stage.setX(super.getScreenWidth() - sceneBounds.getMinX());
    stage.setY(super.getScreenHeight() - sceneBounds.getMinY());
    stage.show();
    try {
      graphMl = load(graphPath, true);
    } catch (IOException e) {
      handle(e);
    }
  }

  /**
   * load my graph from the given graph path
   * 
   * @param pGraphPath
   * @param withClear
   * @return the loaded graphMl File
   * @throws IOException
   */
  public File load(String pGraphPath, boolean withClear) throws IOException {
    File lGraphMl;
    if (!pGraphPath.contains("/"))
      lGraphMl = new File(this.getAppRoot(), pGraphPath);
    else
      lGraphMl = new File(pGraphPath);
    if (withClear)
      dropTarget.clear();
    dropTarget.loadGraph(lGraphMl);
    updateTitle(pGraphPath);
    return lGraphMl;
  }

  public void updateTitle(String filePath) {
    stage.setTitle(filePath + ":" + title);
  }

  /**
   * get the application root directory
   * 
   * @return the application root directory
   */
  public File getAppRoot() {
    File home = new File(System.getProperty("user.home"));
    File appRoot = new File(home, ".dragtop");
    if (!appRoot.exists())
      appRoot.mkdirs();
    return appRoot;
  }

  /**
   * create a Menu
   * 
   * @return - the menu
   */
  public MenuBar createMenu() {
    // @Todo - use com.bitplan.javafx tools for menu creation

    MenuItem newMenuItem = new MenuItem("New");
    MenuItem openMenuItem = new MenuItem("Open");
    MenuItem importMenuItem = new MenuItem("Import");
    MenuItem saveMenuItem = new MenuItem("Save");
    MenuItem saveAsMenuItem = new MenuItem("Save as");
    MenuItem quitMenuItem = new MenuItem("Quit");

    final Menu menu = new Menu("File");
    menu.getItems().add(newMenuItem);
    menu.getItems().add(openMenuItem);
    menu.getItems().add(importMenuItem);
    menu.getItems().add(saveMenuItem);
    menu.getItems().add(saveAsMenuItem);
    menu.getItems().add(quitMenuItem);

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(menu);

    newMenuItem.setOnAction(e -> clear());

    quitMenuItem.setOnAction(e -> {
      try {
        dropTarget.saveGraph(graphMl);
      } catch (IOException e1) {
        handle(e1);
      }
      Platform.exit();
    });

    openMenuItem.setOnAction(e -> openFile(true));
    importMenuItem.setOnAction(e -> openFile(false));

    saveAsMenuItem.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Save dragtop xml file");
      fileChooser.setInitialDirectory(graphMl.getParentFile());
      fileChooser.setInitialFileName(graphMl.getName());
      File savedFile = fileChooser.showSaveDialog(stage);
      if (savedFile != null) {
        try {
          dropTarget.saveGraph(savedFile);
          graphMl = savedFile;
          updateTitle(savedFile.getName());
          statusBar.setText("File saved: " + savedFile.toString());
        } catch (IOException e1) {
          handle(e1);
        }
      } else {
        statusBar.setText("File save cancelled.");
      }
    });

    saveMenuItem.setOnAction(e -> {
      try {
        dropTarget.saveGraph(graphMl);
      } catch (IOException e1) {
        handle(e1);
      }
    });
    return menuBar;
  }

  protected void clear() {
    // save current state
    // TODO - should we interactively ask for this
    // only in case of modifications?
    try {
      dropTarget.saveGraph(graphMl);
    } catch (IOException e1) {
      handle(e1);
    }
    // and clear
    dropTarget.clear();
  }

  /**
   * open a new file
   * 
   * @param withClear
   *          - if true overwrite if false import
   */
  protected void openFile(boolean withClear) {
    {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Select dragtop xml file");
      fileChooser.setInitialDirectory(getAppRoot());
      fileChooser.getExtensionFilters()
          .addAll(new ExtensionFilter("XML Files", "*.xml"));
      File selectedFile = fileChooser.showOpenDialog(null);

      if (selectedFile != null) {
        statusBar.setText("File selected: " + selectedFile.getName());
        graphPath = selectedFile.getPath();
        try {
          graphMl = load(graphPath, withClear);
        } catch (IOException e1) {
          handle(e1);
        }
      } else {
        statusBar.setText("File selection cancelled.");
      }
    }
  }

  private void handle(Throwable th) {
    statusBar.setText(
        String.format("error %s:%s", th.getClass().getName(), th.getMessage()));

  }

  @Override
  public void start(Stage stage) {
    super.start(stage);
    setup(stage);
    DropTarget.debug = debug;
    dropTarget.activatePlugins(plugins);
  }

  /**
   * direct start of DragTop (only for test / debugging - use DragTop.main
   * instead)
   * 
   * @param args
   */
  public static void main(String[] args) {
    Application.launch(args);
  }
}
