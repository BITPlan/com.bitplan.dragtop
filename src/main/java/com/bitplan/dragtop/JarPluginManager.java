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

import org.pf4j.DefaultPluginManager;
import org.pf4j.JarPluginLoader;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;

/**
 * see https://github.com/pf4j/pf4j/issues/249 see
 * https://pf4j.org/doc/class-loading.html
 * 
 * @author wf
 *
 */
public class JarPluginManager extends DefaultPluginManager {
  public static class ParentClassLoaderJarPluginLoader extends JarPluginLoader {
    static ClassLoader parentClassLoader;

    /**
     * 
     * @param pluginManager
     */
    public ParentClassLoaderJarPluginLoader(PluginManager pluginManager) {
      super(pluginManager);
    }

    static PluginClassLoader pluginClassLoader;

    @Override
    public ClassLoader loadPlugin(Path pluginPath,
        PluginDescriptor pluginDescriptor) {
      if (pluginClassLoader == null) {
        boolean parentFirst=true;
        pluginClassLoader = new PluginClassLoader(pluginManager,
            pluginDescriptor, parentClassLoader,parentFirst);
      }
      pluginClassLoader.addFile(pluginPath.toFile());

      return pluginClassLoader;
    }
  }

  /**
   * construct me with the given classloader
   * @param classLoader
   */
  public JarPluginManager(ClassLoader classLoader) {
    ParentClassLoaderJarPluginLoader.parentClassLoader=classLoader;
    //System.setProperty("pf4j.mode", RuntimeMode.DEPLOYMENT.toString());
    //System.setProperty("pf4j.mode", RuntimeMode.DEVELOPMENT.toString());
  }

  @Override
  protected PluginLoader createPluginLoader() {
    // load only jar plugins
    return new ParentClassLoaderJarPluginLoader(this);
  }

  @Override
  protected PluginDescriptorFinder createPluginDescriptorFinder() {
    // read plugin descriptor from jar's manifest
    return new ManifestPluginDescriptorFinder();
  }

}