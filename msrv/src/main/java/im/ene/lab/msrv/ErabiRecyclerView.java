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
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by eneim on 3/28/16.
 */
public class ErabiRecyclerView extends RecyclerView {

  private ErabiAdapter.MultiChoiceModeListener multiChoiceModeListener;
  private ErabiAdapter.AdapterParams adapterParams = new ErabiAdapter.AdapterParams();

  public ErabiRecyclerView(Context context) {
    this(context, null);
  }

  public ErabiRecyclerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ErabiRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ErabiRecyclerView);

    try {
      adapterParams.choiceMode =
          a.getInt(R.styleable.ErabiRecyclerView_erabi_mode, ErabiAdapter.CHOICE_MODE_NONE);
      adapterParams.selector = a.getDrawable(R.styleable.ErabiRecyclerView_erabi_drawable);
    } finally {
      a.recycle();
    }
  }

  public void setMultiChoiceModeListener(
      ErabiAdapter.MultiChoiceModeListener multiChoiceModeListener) {
    this.multiChoiceModeListener = multiChoiceModeListener;
    if (getAdapter() != null && getAdapter() instanceof ErabiAdapter) {
      ((ErabiAdapter) getAdapter()).setMultiChoiceModeListener(multiChoiceModeListener);
    }
  }

  @Override public void setAdapter(Adapter adapter) {
    if (adapter instanceof ErabiAdapter) {
      ((ErabiAdapter) adapter).setAdapterParams(adapterParams);

      if (multiChoiceModeListener != null) {
        ((ErabiAdapter) adapter).setMultiChoiceModeListener(multiChoiceModeListener);
      }
    }
    super.setAdapter(adapter);
  }

  static class ErabiSavedState extends BaseSavedState {

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     */
    public ErabiSavedState(Parcel source) {
      super(source);
    }

    /**
     * Constructor called by derived classes when creating their SavedState objects
     *
     * @param superState The state of the superclass of this view
     */
    public ErabiSavedState(Parcelable superState) {
      super(superState);
    }

    public static final Parcelable.Creator<ErabiSavedState> CREATOR =

        ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<ErabiSavedState>() {
          @Override public ErabiSavedState createFromParcel(Parcel in, ClassLoader loader) {
            return new ErabiSavedState(in);
          }

          @Override public ErabiSavedState[] newArray(int size) {
            return new ErabiSavedState[size];
          }
        });
  }
}
