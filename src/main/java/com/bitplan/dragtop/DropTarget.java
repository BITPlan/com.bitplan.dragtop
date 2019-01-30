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

import java.util.List;

public interface DropTarget {

  /**
   * add the given card for the given url
   * 
   */
  void addDragItem(String url);

  /**
   * add the given dragItem
   * @param card
   */
  void addDragItem(Card card);
  
  List<DragItem> getDragItems();
  
  
  /**
   * set the progress in percent
   * @param progress (0.00 to 1.00) 
   */
  void setProgress(double progress);

}