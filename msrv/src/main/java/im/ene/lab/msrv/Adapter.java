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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 3/23/16.
 */
public abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH>
    implements Selectable {

  SparseBooleanArray selectedItems;

  private OnItemClickListener itemClickListener;
  private OnItemSelectedListener itemSelectedListener;

  public Adapter() {
    super();
    selectedItems = new SparseBooleanArray();
    setHasStableIds(true);  // force stable Id for selection
  }

  @Override public void onBindViewHolder(VH holder, int position) {
    holder.bind(getItem(position));
    holder.onSelectStateChanged(selectedItems.get(position, false));
  }

  public void setItemClickListener(OnItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
    this.itemSelectedListener = itemSelectedListener;
  }

  @Override public long getItemId(int position) {
    return position;
  }

  public abstract Object getItem(int position);

  public void clearSelection(int position) {
    selectedItems.delete(position);
    notifyItemChanged(position);
  }

  public void clearSelections(Integer... items) {
    for (Integer item : items) {
      selectedItems.delete(item);
      notifyItemChanged(item);
    }
  }

  @Override public void onViewAttachedToWindow(VH holder) {
    super.onViewAttachedToWindow(holder);
    holder.onSelectStateChanged(selectedItems.get(holder.getAdapterPosition(), false));
  }

  protected abstract VH onCreateViewHolderInternal(ViewGroup parent, int type);

  @Override public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
    final VH vh = onCreateViewHolderInternal(parent, viewType);
    vh.setOnVHLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        int pos = vh.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
          return false;
        }

        if (itemClickListener == null && itemSelectedListener == null) {
          return false;
        }

        if (!isSelectable(pos) || itemSelectedListener == null) {
          return itemClickListener != null && //
              itemClickListener.onItemLongClick(Adapter.this, vh, v, pos, getItemId(pos));
        } else {
          if (isInSelectMode()) { // Is selecting, so continue selection
            toggle(pos);
            onSelection(vh, v, pos);
            return true;
          }

          isInSelectMode = itemSelectedListener != null && //
              itemSelectedListener.onInitSelectMode(Adapter.this, vh, v, pos, getItemId(pos));
          if (isInSelectMode) { // select mode initialized, so toggle clicked item
            toggle(pos);
            onSelection(vh, v, pos);
          }
          return isInSelectMode;
        }
      }
    });

    vh.setOnVHClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int pos = vh.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
          return;
        }

        if (isInSelectMode && isSelectable(pos)) { // In select mode, so do selection stuff
          toggle(pos);
          onSelection(vh, v, pos);
        } else {  // Not int select mode, do normal stuff
          if (itemClickListener != null) {
            itemClickListener.onItemClick(Adapter.this, vh, v, pos, getItemId(pos));
          }
        }
      }
    });

    return vh;
  }

  private void onSelection(ViewHolder vh, View v, int pos) {
    if (isItemSelected(pos) && itemSelectedListener != null) {
      itemSelectedListener.onItemSelected(Adapter.this, vh, v, pos, getItemId(pos));
    }
  }

  private boolean isInSelectMode;

  public boolean isInSelectMode() {
    return isInSelectMode;
  }

  @Override public boolean isItemSelected(int pos) {
    return selectedItems.get(pos, false);
  }

  @Override public void toggle(int pos) {
    if (selectedItems.get(pos, false)) {
      selectedItems.delete(pos);
    } else {
      selectedItems.put(pos, true);
    }
    notifyItemChanged(pos);
  }

  @Override public void clearSelections() {
    isInSelectMode = false;
    selectedItems.clear();
    notifyDataSetChanged();
  }

  @Override public void selectAll() {
    for (int i = 0; i < getItemCount(); i++) {
      if (isSelectable(i)) {
        selectedItems.put(i, true);
      }
    }
    notifyDataSetChanged();
  }

  @Override public void selectNone() {
    for (int i = 0; i < getItemCount(); i++) {
      if (isSelectable(i)) {
        selectedItems.put(i, false);
      }
    }
    notifyDataSetChanged();
  }

  @Override public boolean isSelectable(int pos) {
    return false;
  }

  @Override public int getSelectedItemCount() {
    return selectedItems.size();
  }

  @Override public List<Integer> getSelectedItems() {
    List<Integer> items = new ArrayList<>(selectedItems.size());
    for (int i = 0; i < selectedItems.size(); i++) {
      items.add(selectedItems.keyAt(i));
    }
    return items;
  }

  static class SavedState extends View.BaseSavedState {

    SparseBooleanArray selectedItems;

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     */
    public SavedState(Parcel source) {
      super(source);
      selectedItems = source.readSparseBooleanArray();
    }

    /**
     * Constructor called by derived classes when creating their SavedState objects
     *
     * @param superState The state of the superclass of this view
     */
    public SavedState(Parcelable superState) {
      super(superState);
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeSparseBooleanArray(selectedItems);
    }

    public static final Parcelable.Creator<SavedState> CREATOR =

        ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
          @Override public SavedState createFromParcel(Parcel in, ClassLoader loader) {
            return new SavedState(in);
          }

          @Override public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        });
  }

  /**
   * Listen to item's click event.
   */
  public interface OnItemClickListener {

    void onItemClick(Adapter adapter, ViewHolder viewHolder, View view, int pos, long id);

    boolean onItemLongClick(Adapter adapter, ViewHolder viewHolder, View view, int pos, long id);
  }

  /**
   * Control item's selection event. For example: init selection mode, notify Item selected...
   */
  public interface OnItemSelectedListener {

    void onItemSelected(Adapter adapter, ViewHolder viewHolder, View view, int pos, long id);

    boolean onInitSelectMode(Adapter adapter, ViewHolder viewHolder, View view, int pos, long id);
  }
}
