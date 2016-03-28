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

package im.ene.lab.msrv.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.lab.msrv.ErabiAdapter;
import im.ene.lab.msrv.sample.data.SimpleItem;
import im.ene.lab.msrv.sample.viewholder.SimpleViewHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 3/23/16.
 */
public class SimpleErabiAdapter extends ErabiAdapter<SimpleViewHolder> {

  private static List<Integer> DATA = new ArrayList<>();

  static {
    for (int i = 0; i < 100; i++) {
      DATA.add(i);
    }
  }

  @Override public void onBindViewHolder(SimpleViewHolder holder, int position) {
    super.onBindViewHolder(holder, position);
    holder.bind(getItem(position));
  }

  public Object getItem(int position) {
    return new SimpleItem("Item number: " + DATA.get(position));
  }

  @Override protected SimpleViewHolder createViewHolderInternal(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(SimpleViewHolder.LAYOUT_RES, parent, false);
    return new SimpleViewHolder(view);
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override public int getItemCount() {
    return DATA.size();
  }
}
