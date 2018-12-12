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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bitplan.javafx.SampleApp;

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider;
import javafx.application.Platform;
import javafx.scene.image.Image;

/**
 * test the card handling
 * @author wf
 *
 */
public class TestCard {

  public static int SHOW_TIME = 4000;

  @BeforeClass
  public static void init() {
    SampleApp.toolkitInit();
    SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());
  }

  @Test
  public void testSVGImage() throws Exception {
    // https://www.svgrepo.com/svg/52741/bath-of-immersion
    String imageUrls[] = {
        // "https://www.svgrepo.com/show/52741/bath-of-immersion.svg",
        "http://wiki.bitplan.com/images/wiki/0/05/Bath.svg" };
    for (String imageUrl : imageUrls) {
      //InputStream imageData=new URL(imageUrl).openStream();
      Image svgImage = new Image(imageUrl);
      System.out.println(String.format("%s: %4.0f x %4.0f",imageUrl,svgImage.getWidth(),svgImage.getHeight()));
      // assertEquals(100, svgImage.getHeight(), 0.01);
    }
  }
  
  @Test
  public void testDownload() throws Exception {
    String imageUrls[] = {
        "http://wiki.bitplan.com/images/wiki/0/05/Bath.svg" };
    for (String imageUrl : imageUrls) {
      URI imageUri=new URI(imageUrl);
      String[] segments = imageUri.getPath().split("/");
      String name = segments.length > 0 ? segments[segments.length - 1] : imageUrl;
      File destination=File.createTempFile("image", name);
      FileUtils.copyURLToFile(imageUri.toURL(), destination);
    }
  }

  @Test
  public void testCard() throws Exception {
    // https://www.svgrepo.com/svg/25681/bath
    Graph graph = TinkerGraph.open();
    addMaterial(graph, "html", "Spa", "Bagni di Pisa",
        "https://www.bagnidipisa.com/en/spa-thermae/thermae-health/wellness-italy/39-0.html",
        "https://www.bagnidipisa.com/images/1543424695/t/design/logo.png",
        "http://wiki.bitplan.com/images/wiki/0/05/Bath.svg");
    addMaterial(graph, "html", "Person", "Queen Victoria",
        "https://en.wikipedia.org/wiki/Queen_Victoria",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Queen_Victoria_by_Bassano.jpg/220px-Queen_Victoria_by_Bassano.jpg",
        "http://wiki.bitplan.com/font-awesome/png/32x32/plain/user.png");
    addMaterial(graph, "html", "Person", "Maria Fahl",
        "http://www.bitplan.com/index.php/Maria_Fahl",
        "http://wiki.bitplan.com/images/wiki/thumb/9/9f/MariaFahl.png/200px-MariaFahl.png",
        "http://wiki.bitplan.com/font-awesome/png/32x32/plain/user.png");
    addMaterial(graph, "jar", "Tool", "PDFExtractor",
        "http://repo1.maven.org/maven2/com/bitplan/pdfextractor/com.bitplan.pdfextractor/0.0.1/com.bitplan.pdfextractor-0.0.1.jar",
        "",
        "http://wiki.bitplan.com/font-awesome/png/32x32/plain/wrench.png");
    
    //HBox box = new HBox();
    //box.setSpacing(10);
    //box.setFillHeight(false);
    DragTopApp sampleApp = new DragTopApp("","dragtoptest.xml");
    sampleApp.show();
    sampleApp.waitOpen();
    GraphTraversalSource g = graph.traversal();
    g.V().forEachRemaining(v -> {
      Platform.runLater(()->sampleApp.getDropTarget().addDragItem(new Card(v, sampleApp)));
    });
    Thread.sleep(SHOW_TIME);
    sampleApp.close();
  }
  
  @Test
  public void testCardCreate() throws IOException {
    String url="https://en.wikipedia.org/wiki/Cologne";
    Card card=Card.create(url, null);
    assertEquals(url,card.getUrl());
    File file=File.createTempFile("test", ".pdf");
    Card cardf=Card.create(file.toURI().toURL().toString(), null);
    assertNotNull(cardf);
    assertTrue(cardf.getItem() instanceof File);
    file.delete();
  }

  /**
   * add material
   * 
   * @param graph
   * @param fileType
   * @param label
   * @param name
   * @param url
   * @param previewUrl,
   * @param iconUrl
   */
  private void addMaterial(Graph graph, String fileType, String label,
      String name, String url, String previewUrl, String iconUrl) {
    Vertex mv = graph.addVertex(label);
    mv.property("type", "material");
    mv.property("fileType", fileType);
    mv.property("name", name);
    mv.property("url", url);
    mv.property("previewUrl", previewUrl);
    mv.property("iconUrl", iconUrl);
    if ("jar".equals(fileType)) {
      mv.property("tool",true);
    }
  }
}
