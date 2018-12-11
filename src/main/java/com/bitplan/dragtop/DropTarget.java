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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginWrapper;

import com.bitplan.gui.Linker;
import com.bitplan.rythm.GraphRythmContext;
import com.sun.javafx.geom.Point2D;

import javafx.event.EventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * the space on which to drop things
 * 
 * @author wf
 *
 */
public class DropTarget extends BorderPane {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.dragtop");
  public static boolean debug = false;

  public static Color DRAG_ENTERED_COLOR = Color.LIGHTGREEN;
  public static Color DRAG_DONE_COLOR = Color.LIGHTGRAY;
  public static Color DRAG_DROPPED_COLOR = Color.LIGHTGRAY;
  private DropTarget target;
  List<DragItem> dragItems = new ArrayList<DragItem>();
  private Linker linker;
  private Point2D currentPos;
  Map<String, Card> toolMap = new HashMap<String, Card>();

  Graph graph;

  /**
   * create an space where things can be dropped
   * 
   * @param linker
   */
  public DropTarget(Linker linker) {
    target = this;
    this.linker = linker;
    this.graph = TinkerGraph.open();
    // enable the drag events
    enableDrag();

    // enable paste
    enablePaste();

    // initialize the drop position for elements that are not "dropped" via the
    // mouse
    // but added via the API
    currentPos = new Point2D(Card.marginX, Card.marginY);
  }

  public void enablePaste() {
    super.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (e.isShortcutDown() && e.getCode() == KeyCode.V) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        URI clipURI = null;
        try {
          if (clipboard.hasString()) {
            String clip = clipboard.getString();
            clipURI = new URI(clip);
          }
          if (clipboard.hasUrl()) {
            clipURI = new URI(clipboard.getUrl());
          }
          if (clipURI != null) {
            Card card = new Card(clipURI, linker);
            this.addDragItem(card);
          }
        } catch (URISyntaxException e1) {
          LOGGER.log(Level.WARNING, e1.getMessage(), e1);
        }
      }
    });
  }

  /**
   * enable the drag events
   */
  public void enableDrag() {
    // enable dragEvents
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

  /**
   * add a dragItem at the given x and y position
   * 
   * @param dragItem
   * @param x
   *          - the x position
   * @param y
   *          - the y position
   */
  public void addDragItem(DragItem dragItem, Point2D pos) {
    if (dragItem.getItem() instanceof File) {
      File file = (File) dragItem.getItem();
      Vertex fileVertex = graph.addVertex("dragtop.File");
      try {
        fileVertex.property("url", file.toURI().toURL().toString());
      } catch (MalformedURLException e1) {
        LOGGER.log(Level.WARNING, String.format("could not get url for file %s error: %s", file.getPath(),e1.getMessage()),e1);
      }
      String ext = FileIcon.getFileExt(file.getName());
      switch (ext) {
      case "jar":
        String pluginId = pluginManager.loadPlugin(file.toPath());
        if (dragItem instanceof Card) {
          Card card = (Card) dragItem;
          card.pluginId = pluginId;
          toolMap.put(pluginId, card);
        }
        break;
      case "rythm":
        GraphRythmContext rythmContext = GraphRythmContext.getInstance();
        Object item = dragItem.getItem();
        if (item instanceof File) {
          File templateFile = (File) item;
          Map<String, Object> rootMap = new HashMap<String, Object>();
          try {
            String html = rythmContext.render(templateFile, rootMap);
            File htmlFile = File.createTempFile("rythm", ".html");
            FileUtils.writeStringToFile(htmlFile, html, "UTF-8");
            linker.browse(htmlFile.toURI().toURL().toString());
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
          }
        }
        break;
      }
    }
    dragItems.add(dragItem);
    target.getChildren().add(dragItem.getNode());
    dragItem.setLayoutX(pos.x);
    dragItem.setLayoutY(pos.y);
    pos.x += dragItem.getWidth() + Card.marginX;
    if (pos.x + dragItem.getWidth() > DropTarget.this.getWidth()) {
      pos.x = Card.marginX;
      pos.y += dragItem.getHeight() + Card.marginY;
    }
    fireTools(dragItem);
  }

  /**
   * add a file as a drag item
   * 
   * @param file
   * @param pos
   */
  public Card addDragFile(File file, Point2D pos) {
    Card card = new Card(file, linker);
    addDragItem(card, pos);
    return card;
  }

  /**
   * add the given card at the currenPosition
   * 
   * @param card
   */
  public void addDragItem(Card card) {
    addDragItem(card, currentPos);
  }

  /**
   * fire the tools that are active
   * 
   * @param dragItem
   */
  public void fireTools(DragItem dragItem) {
    for (Card card : toolMap.values()) {
      if (card.isToolOn()) {
        List<DropHandler> dropHandlers = pluginManager
            .getExtensions(DropHandler.class, card.pluginId);
        if (dropHandlers.size() > 0) {
          for (DropHandler dropHandler : dropHandlers) {
            dropHandler.getHandler().accept(dragItem);
          }
        }
      }
    }
  }

  EventHandler<DragEvent> onDragDropped = new EventHandler<DragEvent>() {
    public void handle(DragEvent event) {
      /* data dropped */
      if (debug) {
        LOGGER.log(Level.INFO, "onDragDropped");
      }
      /* if there is file data on dragboard, read it and use it */
      Dragboard db = event.getDragboard();
      boolean success = false;
      currentPos = new Point2D((float) event.getX(), (float) event.getY());
      if (db.hasFiles()) {
        success = true;
        for (File file : db.getFiles()) {
          addDragFile(file, currentPos);
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
        if (!plugin.isEmpty()) {
          Path pluginPath = Paths.get(plugin.trim()).toAbsolutePath()
              .normalize();
          File pluginJar = pluginPath.toFile();
          this.addDragFile(pluginJar, currentPos);
        }
      }
    }
    pluginManager.startPlugins();
    registerStartedPlugins();
    for (Card card : toolMap.values()) {
      card.startTool();
    }
  }

  /**
   * register the started plugins
   */
  public void registerStartedPlugins() {

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

  /**
   * load the graph from the given graphMl file
   * 
   * @param graphMl
   * @throws IOException
   */
  public void loadGraph(File graphMl) throws IOException {
    if (graphMl.exists()) {
      this.graph.io(IoCore.graphml()).readGraph(graphMl.getPath());
      GraphTraversalSource g = graph.traversal();
      g.V().hasLabel("dragtop.File").forEachRemaining(fileVertex->{
        try {
          URI uri=new URI(fileVertex.property("url").value().toString());
          Card card = new Card(uri, linker);
          this.addDragItem(card);
        } catch (NoSuchElementException | URISyntaxException e) {
          LOGGER.log(Level.WARNING,e.getMessage());
        }
      });
    }
  }

  /**
   * save the given graph
   * 
   * @param graphMl
   * @throws IOException
   */
  public void saveGraph(File graphMl) throws IOException {
    this.graph.io(IoCore.graphml()).writeGraph(graphMl.getPath());
  }

  public void clear() {
    this.graph=TinkerGraph.open();
    for (DragItem dragItem:dragItems) {
      target.getChildren().remove(dragItem.getNode());
    }
    this.dragItems.clear();
  }

}
