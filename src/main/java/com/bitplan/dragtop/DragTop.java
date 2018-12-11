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

import org.kohsuke.args4j.Option;

import com.bitplan.javafx.Main;

/**
 * the DragTop
 * 
 * @author wf
 *
 */
public class DragTop extends Main {

  @Option(name = "-p", aliases = {
      "--plugins" }, usage = "plugins\ncomma separated list of plugins to load")
  String plugins;

  @Option(name = "-g", aliases = {
      "--graph" }, usage = "graph\nthe graphml file for the dragtop graph to open")
  String graphPath="dragtop.xml";

  @Override
  public String getSupportEMail() {
    return "support@bitplan.com";
  }

  @Override
  public String getSupportEMailPreamble() {
    return "Dear DragTop user,";
  }

  @Override
  public void work() throws Exception {
    if (showVersion)
      this.showVersion();
    if (showHelp)
      this.showHelp();
    else {
      DragTopApp.testMode = testMode;
      DragTopApp.debug = debug;
      DragTopApp.toolkitInit();
      DragTopApp app = new DragTopApp(graphPath,plugins);

      app.show();
      app.waitOpen();
      app.waitClose();
    }
  }

  /**
   * entry point for application
   * 
   * @param args
   */
  public static void main(String[] args) {
    DragTop dragtop = new DragTop();
    dragtop.maininstance(args);
  }
}
