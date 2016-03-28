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

import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by eneim on 3/26/16.
 */
public final class SelectorHelper {

  private static final Map<ViewHolder, StateListDrawable> sCache = new WeakHashMap<>();

  private AbsListView test;
  private RecyclerView.ItemDecoration test3;
}