/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.msrv;

import java.util.List;

/**
 * Created by eneim on 3/23/16.
 */
public interface Selectable {

  void toggleSelection(int pos);

  void clearSelections();

  // void selectAll();

  // void deselectAll();

  boolean isItemSelected(int pos);

  // void onActionItemClicked(ActionMode actionMode, MenuItem item);

  int getSelectedItemCount();

  List<Integer> getSelectedItems();
}
