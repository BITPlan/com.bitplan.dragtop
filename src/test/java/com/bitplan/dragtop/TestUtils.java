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

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import javafx.scene.image.Image;

/**
 * test the file icon handling
 * 
 * @author wf
 *
 */
public class TestUtils {

  boolean debug = false;
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.dragtop");

  @Test
  public void testPath() {
    Path path = Paths.get("..").toAbsolutePath().normalize();
    File file = path.toFile();
    assertTrue(file.isDirectory());
    if (debug)
      LOGGER.log(Level.INFO, file.getAbsolutePath());
    assertFalse(file.getPath().contains(".."));
  }

  @Test
  public void testIcons() {
    String exts[] = { "pdf" };
    for (String ext : exts) {
      Image fileIcon = FileIcon.getFileIcon(ext);
      assertEquals(32, fileIcon.getHeight(), 0.01);
      assertEquals(32, fileIcon.getWidth(), 0.01);
    }
  }

}
