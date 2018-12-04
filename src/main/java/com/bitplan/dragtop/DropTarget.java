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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginWrapper;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * the space on which to drop things
 * 
 * @author wf
 *
 */
public class DropTarget extends Pane {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.dragtop");
  public static boolean debug = false;

  public static Color DRAG_ENTERED_COLOR = Color.LIGHTGREEN;
  public static Color DRAG_DONE_COLOR = Color.LIGHTGRAY;
  public static Color DRAG_DROPPED_COLOR = Color.LIGHTGRAY;
  private DropTarget target;
  List<DragItem> dragItems = new ArrayList<DragItem>();

  /**
   * create a Drag Space
   */
  public DropTarget() {
    target = this;
    target.setOnDragDetected(onDragDetected);
    target.setOnDragOver(onDragOver);
    target.setOnDragEntered(onDragEntered);
    target.setOnDragExited(onDragExited);
    target.setOnDragDropped(onDragDropped);
    target.setOnDragDone(onDragDone);
  }

  EventHandler<MouseEvent> onDragDetected = new EventHandler<MouseEvent>() {
    public void handle(MouseEvent event) {
      /* drag was detected, start drag-and-drop gesture */
      if (debug) {
        LOGGER.log(Level.INFO, "onDragDetected");
      }

      /* allow any transfer mode */
      // Dragboard db = target.startDragAndDrop(TransferMode.ANY);

      /* put a string on dragboard */
      // ClipboardContent content = new ClipboardContent();
      // content.putString(target.getText());
      // db.setContent(content);

      event.consume();
    }
  };

  EventHandler<DragEvent> onDragOver = new EventHandler<DragEvent>() {
    public void handle(DragEvent event) {
      Dragboard db = event.getDragboard();
      /* data is dragged over the target */
      if (debug) {
        LOGGER.log(Level.INFO,
            String.format("onDragOver %s", db.getContentTypes()));
      }
      /*
       * accept it only if it is not dragged from the same node and if it has a
       * string data
       */
      if (event.getGestureSource() != target) {
        // && db.hasFiles()
        /* allow for both copying and moving, whatever user chooses */
        event.acceptTransferModes(TransferMode.COPY);
      } else {
        event.consume();
      }
    }
  };

  EventHandler<DragEvent> onDragEntered = new EventHandler<DragEvent>() {
    public void handle(DragEvent event) {
      /* the drag-and-drop gesture entered the target */
      if (debug) {
        LOGGER.log(Level.INFO, "onDragEntered");
      }
      /* show to the user that it is an actual gesture target */
      if (event.getGestureSource() != target) {
        target.setFill(DRAG_ENTERED_COLOR);
      } else {
        target.setFill(Color.LIGHTBLUE);
      }

      event.consume();
    }
  };

  EventHandler<DragEvent> onDragExited = new EventHandler<DragEvent>() {
    public void handle(DragEvent event) {
      /* mouse moved away, remove the graphical cues */
      target.setFill(DRAG_DONE_COLOR);
      event.consume();
    }
  };

  EventHandler<DragEvent> onDragDropped = new EventHandler<DragEvent>() {
    public void handle(DragEvent event) {
      /* data dropped */
      if (debug) {
        LOGGER.log(Level.INFO, "onDragDropped");
      }
      /* if there is file data on dragboard, read it and use it */
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles()) {
        success = true;
        for (File file : db.getFiles()) {
          DragItem dragItem = new DragItem(file);
          dragItems.add(dragItem);
          target.getChildren().add(dragItem);
          dragItem.setLayoutX(event.getX());
          dragItem.setLayoutY(event.getY());
          List<DropHandler> dropHandlers = pluginManager
              .getExtensions(DropHandler.class);
          if (dropHandlers.size() > 0)
            for (DropHandler dropHandler : dropHandlers) {
              dropHandler.getHandler().accept(dragItem);
            }
        }
        setFill(DRAG_DROPPED_COLOR);
      }
      /*
       * let the source know whether the string was successfully transferred and
       * used
       */
      event.setDropCompleted(success);

      event.consume();
    }
  };

  EventHandler<DragEvent> onDragDone = new EventHandler<DragEvent>() {
    public void handle(DragEvent event) {
      /* the drag-and-drop gesture ended */
      if (debug) {
        LOGGER.log(Level.INFO, "onDragDone");
      }
      /* if the data was successfully moved, clear it */
      if (event.getTransferMode() == TransferMode.MOVE) {
        // target.setText("");
      }
      target.setFill(DRAG_DONE_COLOR);
      event.consume();
    }
  };

  DefaultPluginManager pluginManager;

  /**
   * helper to get RGBCode from Color
   * https://stackoverflow.com/questions/17925318/how-to-get-hex-web-string-from-javafx-colorpicker-color
   * 
   * @param color
   * @return - the webhex
   */
  public static String toRGBCode(Color color) {
    return String.format("#%02X%02X%02X", (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
  }

  protected void setFill(Color color) {
    setStyle(String.format("-fx-background-color: %s;", toRGBCode(color)));
  }
  
  /**
   * activate the given plugins
   * 
   * @param plugins
   */
  public void activatePlugins(String plugins) {
    // https://github.com/pf4j/pf4j/issues/209
    pluginManager = new JarPluginManager(this.getClass().getClassLoader());

    if (plugins != null) {

      for (String plugin : plugins.split(",")) {
        Path pluginPath = Paths.get(plugin).toAbsolutePath().normalize();
        pluginManager.loadPlugin(pluginPath);
      }
    }
    pluginManager.startPlugins();
    showStartedPlugins();
    DropHandler workAroundHandler =  new DropHandler() {
      Consumer<DragItem> handler=null;
      @Override
      public Consumer<DragItem> getHandler() {
        return handler;
      }

      @Override
      public void setHandler(Consumer<DragItem> handler) {
        this.handler=handler;
      }
      
    };
    workAroundHandler.setHandler(null);
  }

  /**
   * show the started plugins
   */
  public void showStartedPlugins() {

    List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();

    for (PluginWrapper plugin : startedPlugins) {
      PluginDescriptor descriptor = plugin.getDescriptor();
      String pluginId = plugin.getDescriptor().getPluginId();
      String msg = String.format(
          "Extensions added by plugin id:'%s' version:'%s' %s:", pluginId,
          descriptor.getVersion(), descriptor.getPluginDescription());
      if (debug)
        LOGGER.log(Level.INFO, msg);
      Set<String> extensionClassNames = pluginManager
          .getExtensionClassNames(pluginId);
      for (String extension : extensionClassNames) {
        msg = "   " + extension;
        if (debug)
          LOGGER.log(Level.INFO, msg);
      }
    }
  }

}
