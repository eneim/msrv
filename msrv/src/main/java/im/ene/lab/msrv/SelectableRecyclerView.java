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

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by eneim on 3/22/16.
 */
public class SelectableRecyclerView extends RecyclerView {

  public SelectableRecyclerView(Context context) {
    super(context);
  }

  public SelectableRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public SelectableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    im.ene.lab.msrv.Adapter.SavedState state = new im.ene.lab.msrv.Adapter.SavedState(superState);
    if (getAdapter() instanceof im.ene.lab.msrv.Adapter) {
      state.selectedItems = ((im.ene.lab.msrv.Adapter) getAdapter()).selectedItems;
    }

    return state;
  }

  @Override protected void onRestoreInstanceState(Parcelable state) {
    im.ene.lab.msrv.Adapter.SavedState savedState = (im.ene.lab.msrv.Adapter.SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());

    if (savedState.selectedItems != null) {
      if (getAdapter() != null && getAdapter() instanceof im.ene.lab.msrv.Adapter) {
        ((im.ene.lab.msrv.Adapter) getAdapter()).selectedItems = savedState.selectedItems;
        requestLayout();
      }
    }
  }
}
