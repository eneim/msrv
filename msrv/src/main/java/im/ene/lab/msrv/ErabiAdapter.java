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

import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by eneim on 3/26/16.
 */
public abstract class ErabiAdapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

  private AppCompatActivity mParent;

  /**
   * Normal list that does not indicate choices
   */
  public static final int CHOICE_MODE_NONE = 0;

  /**
   * The list allows up to one choice
   */
  public static final int CHOICE_MODE_SINGLE = 1;

  /**
   * The list allows multiple choices
   */
  @Deprecated public static final int CHOICE_MODE_MULTIPLE = 2;

  /**
   * The list allows multiple choices in a modal selection mode
   */
  public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

  /**
   * Controls if/how the user may choose/check items in the list
   */
  int mChoiceMode = CHOICE_MODE_NONE;

  /**
   * Controls CHOICE_MODE_MULTIPLE_MODAL. null when inactive.
   */
  ActionMode mChoiceActionMode;

  /**
   * Wrapper for the multiple choice mode callback; AbsListView needs to perform
   * a few extra actions around what application code does.
   */
  MultiChoiceModeWrapper mMultiChoiceModeCallback;

  /**
   * Running count of how many items are currently checked
   */
  int mCheckedItemCount;

  /**
   * Running state of which positions are currently checked
   */
  SparseBooleanArray mCheckStates;

  /**
   * Running state of which IDs are currently checked.
   * If there is a value for a given key, the checked state for that ID is true
   * and the value holds the last known position in the adapter for that id.
   */
  LongSparseArray<Integer> mCheckedIdStates;

  /**
   * A MultiChoiceModeListener receives events for {@link #CHOICE_MODE_MULTIPLE_MODAL}.
   * It acts as the {@link ActionMode.Callback} for the selection mode and also receives
   * {@link #onItemCheckedStateChanged(ActionMode, int, long, boolean)} events when the user
   * selects and deselects list items.
   */
  public interface MultiChoiceModeListener extends ActionMode.Callback {
    /**
     * Called when an item is checked or unchecked during selection mode.
     *
     * @param mode The {@link ActionMode} providing the selection mode
     * @param position Adapter position of the item that was checked or unchecked
     * @param id Adapter ID of the item that was checked or unchecked
     * @param checked <code>true</code> if the item is now checked, <code>false</code>
     * if the item is now unchecked.
     */
    void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked);
  }

  class MultiChoiceModeWrapper implements MultiChoiceModeListener {
    private MultiChoiceModeListener mWrapped;

    public void setWrapped(MultiChoiceModeListener wrapped) {
      mWrapped = wrapped;
    }

    public boolean hasWrappedCallback() {
      return mWrapped != null;
    }

    @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      if (mWrapped.onCreateActionMode(mode, menu)) {
        // Initialize checked graphic state?
        setLongClickable(false);
        return true;
      }
      return false;
    }

    @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return mWrapped.onPrepareActionMode(mode, menu);
    }

    @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      return mWrapped.onActionItemClicked(mode, item);
    }

    @Override public void onDestroyActionMode(ActionMode mode) {
      mWrapped.onDestroyActionMode(mode);
      mChoiceActionMode = null;

      // Ending selection mode means deselecting everything.
      clearChoices();
      notifyDataSetChanged();

      setLongClickable(true);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
      mWrapped.onItemCheckedStateChanged(mode, position, id, checked);

      // If there are no items selected we no longer need the selection mode.
      if (getCheckedItemCount() == 0) {
        mode.finish();
      }
    }
  }

  /**
   * Set a {@link MultiChoiceModeListener} that will manage the lifecycle of the
   * selection {@link android.view.ActionMode}. Only used when the choice mode is set to
   * {@link #CHOICE_MODE_MULTIPLE_MODAL}.
   *
   * @param listener Listener that will manage the selection mode
   * @see #setChoiceMode(int)
   */
  void setMultiChoiceModeListener(@NonNull MultiChoiceModeListener listener) {
    if (mMultiChoiceModeCallback == null) {
      mMultiChoiceModeCallback = new MultiChoiceModeWrapper();
    }
    mMultiChoiceModeCallback.setWrapped(listener);
  }

  /**
   * Returns the number of items currently selected. This will only be valid
   * if the choice mode is not {@link #CHOICE_MODE_NONE} (default).
   *
   * <p>To determine the specific items that are currently selected, use one of
   * the <code>getChecked*</code> methods.
   *
   * @return The number of items currently selected
   * @see #getCheckedItemPosition()
   * @see #getCheckedItemPositions()
   * @see #getCheckedItemIds()
   */
  public int getCheckedItemCount() {
    return mCheckedItemCount;
  }

  /**
   * Returns the currently checked item. The result is only valid if the choice
   * mode has been set to {@link #CHOICE_MODE_SINGLE}.
   *
   * @return The position of the currently checked item or
   * {@link RecyclerView#NO_POSITION} if nothing is selected
   * @see #setChoiceMode(int)
   */
  public int getCheckedItemPosition() {
    if (mChoiceMode == CHOICE_MODE_SINGLE && mCheckStates != null && mCheckStates.size() == 1) {
      return mCheckStates.keyAt(0);
    }

    return RecyclerView.NO_POSITION;
  }

  /**
   * Returns the set of checked items in the list. The result is only valid if
   * the choice mode has not been set to {@link #CHOICE_MODE_NONE}.
   *
   * @return A SparseBooleanArray which will return true for each call to
   * get(int position) where position is a checked position in the
   * list and false otherwise, or <code>null</code> if the choice
   * mode is set to {@link #CHOICE_MODE_NONE}.
   */
  public SparseBooleanArray getCheckedItemPositions() {
    if (mChoiceMode != CHOICE_MODE_NONE) {
      return mCheckStates;
    }
    return null;
  }

  /**
   * Returns the set of checked items ids. The result is only valid if the
   * choice mode has not been set to {@link #CHOICE_MODE_NONE} and the adapter
   * has stable IDs. ({@link #hasStableIds()} == {@code true})
   *
   * @return A new array which contains the id of each checked item in the
   * list.
   */
  public long[] getCheckedItemIds() {
    if (mChoiceMode == CHOICE_MODE_NONE || mCheckedIdStates == null) {
      return new long[0];
    }

    final LongSparseArray<Integer> idStates = mCheckedIdStates;
    final int count = idStates.size();
    final long[] ids = new long[count];

    for (int i = 0; i < count; i++) {
      ids[i] = idStates.keyAt(i);
    }

    return ids;
  }

  /**
   * Clear any choices previously set
   */
  public void clearChoices() {
    if (mCheckStates != null) {
      mCheckStates.clear();
    }
    if (mCheckedIdStates != null) {
      mCheckedIdStates.clear();
    }
    mCheckedItemCount = 0;
  }

  /**
   * @return The current choice mode
   * @see #setChoiceMode(int)
   */
  public int getChoiceMode() {
    return mChoiceMode;
  }

  /**
   * Defines the choice behavior for the List. By default, Lists do not have any choice behavior
   * ({@link #CHOICE_MODE_NONE}). By setting the choiceMode to {@link #CHOICE_MODE_SINGLE}, the
   * List allows up to one item to  be in a chosen state. By setting the choiceMode to
   * {@link #CHOICE_MODE_MULTIPLE}, the list allows any number of items to be chosen.
   *
   * @param choiceMode One of {@link #CHOICE_MODE_NONE}, {@link #CHOICE_MODE_SINGLE}, or
   * {@link #CHOICE_MODE_MULTIPLE}
   */
  public void setChoiceMode(int choiceMode) {
    mChoiceMode = choiceMode;
    if (mChoiceActionMode != null) {
      mChoiceActionMode.finish();
      mChoiceActionMode = null;
    }
    if (mChoiceMode != CHOICE_MODE_NONE) {
      if (mCheckStates == null) {
        mCheckStates = new SparseBooleanArray(0);
      }
      if (mCheckedIdStates == null && hasStableIds()) {
        mCheckedIdStates = new LongSparseArray<>(0);
      }
      // Modal multi-choice mode only has choices when the mode is active. Clear them.
      if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
        clearChoices();
        setLongClickable(true);
      }
    }
  }

  private boolean isLongClickEnabled = false;

  private void setLongClickable(boolean enable) {
    isLongClickEnabled = enable;
  }

  public interface OnItemLongClickListener {

    boolean onItemLongClick(ErabiAdapter adapter, ViewHolder viewHolder, View view, int pos,
        long id);
  }

  public interface OnItemClickListener {

    void onItemClick(ErabiAdapter adapter, ViewHolder viewHolder, View view, int pos, long id);
  }

  private OnItemLongClickListener mOnItemLongClickListener;
  private OnItemClickListener mOnItemClickListener;

  public void setOnItemClickListener(OnItemClickListener listener) {
    mOnItemClickListener = listener;
  }

  public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    mOnItemLongClickListener = listener;
  }

  /**
   * Sets the checked state of the specified position. The is only valid if
   * the choice mode has been set to {@link #CHOICE_MODE_SINGLE} or
   * {@link #CHOICE_MODE_MULTIPLE}.
   *
   * @param position The item whose checked state is to be checked
   * @param value The new checked state for the item
   */
  public void setItemChecked(int position, boolean value) {
    if (mChoiceMode == CHOICE_MODE_NONE) {
      return;
    }

    // Start selection mode if needed. We don't need to if we're unchecking something.
    if (value && mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL && mChoiceActionMode == null) {
      if (mMultiChoiceModeCallback == null || !mMultiChoiceModeCallback.hasWrappedCallback()) {
        throw new IllegalStateException("AbsListView: attempted to start selection mode " +
            "for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was " +
            "supplied. Call setMultiChoiceModeListener to set a callback.");
      }

      if (mParent != null) {
        mChoiceActionMode = mParent.startSupportActionMode(mMultiChoiceModeCallback);
      }
    }

    if (mChoiceMode == CHOICE_MODE_MULTIPLE || mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
      boolean oldValue = mCheckStates.get(position);
      mCheckStates.put(position, value);
      if (mCheckedIdStates != null && hasStableIds()) {
        if (value) {
          mCheckedIdStates.put(getItemId(position), position);
        } else {
          mCheckedIdStates.delete(getItemId(position));
        }
      }
      if (oldValue != value) {
        if (value) {
          mCheckedItemCount++;
        } else {
          mCheckedItemCount--;
        }
      }
      if (mChoiceActionMode != null) {
        final long id = getItemId(position);
        mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, position, id, value);
      }
    } else {
      boolean updateIds = mCheckedIdStates != null && hasStableIds();
      // Clear all values if we're checking something, or unchecking the currently
      // selected item
      if (value || isItemChecked(position)) {
        mCheckStates.clear();
        if (updateIds) {
          mCheckedIdStates.clear();
        }
      }
      // this may end up selecting the value we just cleared but this way
      // we ensure length of mCheckStates is 1, a fact getCheckedItemPosition relies on
      if (value) {
        mCheckStates.put(position, true);
        if (updateIds) {
          mCheckedIdStates.put(getItemId(position), position);
        }
        mCheckedItemCount = 1;
      } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
        mCheckedItemCount = 0;
      }
    }

    notifyDataSetChanged();
  }

  /**
   * Returns the checked state of the specified position. The result is only
   * valid if the choice mode has been set to {@link #CHOICE_MODE_SINGLE}
   * or {@link #CHOICE_MODE_MULTIPLE}.
   *
   * @param position The item whose checked state to return
   * @return The item's checked state or <code>false</code> if choice mode
   * is invalid
   * @see #setChoiceMode(int)
   */
  public boolean isItemChecked(int position) {
    if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
      return mCheckStates.get(position);
    }

    return false;
  }

  @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    mParent = (AppCompatActivity) recyclerView.getContext();
  }

  @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    mParent = null;
  }

  @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    final VH vh = createViewHolderInternal(parent, viewType);
    vh.setOnViewLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        if (!isLongClickEnabled) {
          return false;
        }

        int pos = vh.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
          return false;
        }

        // CHOICE_MODE_MULTIPLE_MODAL takes over long press.
        if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL) {
          if (mChoiceActionMode == null && (mParent != null
              && (mChoiceActionMode = mParent.startSupportActionMode(mMultiChoiceModeCallback))
              != null)) {
            setItemChecked(pos, true);
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            notifyItemChanged(pos);
          }
          return true;
        }

        boolean handled = false;
        if (mOnItemLongClickListener != null) {
          handled = mOnItemLongClickListener.onItemLongClick(ErabiAdapter.this, vh, v, pos,
              getItemId(pos));
        }

        if (handled) {
          v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
      }
    });

    vh.setOnViewClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int pos = vh.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
          return;
        }

        performItemClick(vh, v, pos, getItemId(pos));
      }
    });
    return vh;
  }

  /**
   * Adapt from {@link AbsListView#performItemClick(View, int, long)}
   */
  private boolean performItemClick(ViewHolder viewHolder, View view, int position, long id) {
    boolean handled = false;
    boolean dispatchItemClick = true;

    if (mChoiceMode != CHOICE_MODE_NONE) {
      handled = true;
      boolean checkedStateChanged = false;

      if (mChoiceMode == CHOICE_MODE_MULTIPLE || (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL
          && mChoiceActionMode != null)) {
        boolean checked = !mCheckStates.get(position, false);
        mCheckStates.put(position, checked);
        if (mCheckedIdStates != null && hasStableIds()) {
          if (checked) {
            mCheckedIdStates.put(getItemId(position), position);
          } else {
            mCheckedIdStates.delete(getItemId(position));
          }
        }
        if (checked) {
          mCheckedItemCount++;
        } else {
          mCheckedItemCount--;
        }
        if (mChoiceActionMode != null) {
          mMultiChoiceModeCallback.onItemCheckedStateChanged(mChoiceActionMode, position, id,
              checked);
          dispatchItemClick = false;
        }
        checkedStateChanged = true;
      } else if (mChoiceMode == CHOICE_MODE_SINGLE) {
        boolean checked = !mCheckStates.get(position, false);
        if (checked) {  // false --> true
          mCheckStates.clear();
          mCheckStates.put(position, true);
          if (mCheckedIdStates != null && hasStableIds()) {
            mCheckedIdStates.clear();
            mCheckedIdStates.put(getItemId(position), position);
          }
          mCheckedItemCount = 1;
        } else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)) {
          mCheckedItemCount = 0;
        }
        checkedStateChanged = true;
      }

      if (checkedStateChanged) {
        notifyDataSetChanged();
      }
    }

    if (dispatchItemClick) {
      handled |= performSuperItemClick(viewHolder, view, position, id);
    }

    return handled;
  }

  /**
   * Adapt from {@link AdapterView#performItemClick(View, int, long)}
   */
  private boolean performSuperItemClick(ViewHolder viewHolder, View view, int position, long id) {
    final boolean result;
    if (mOnItemClickListener != null) {
      view.playSoundEffect(SoundEffectConstants.CLICK);
      mOnItemClickListener.onItemClick(this, viewHolder, view, position, id);
      result = true;
    } else {
      result = false;
    }

    if (view != null) {
      view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
    }
    return result;
  }

  @CallSuper @Override public void onBindViewHolder(VH holder, int position) {
    holder.onSelectStateChanged(isItemChecked(position));
  }

  protected abstract VH createViewHolderInternal(ViewGroup parent, int viewType);

  void setAdapterParams(AdapterParams adapterParams) {
    // change choice mode
    setChoiceMode(adapterParams.choiceMode);
    // TODO setup Selector drawable too
  }

  @IntDef({
      CHOICE_MODE_NONE, CHOICE_MODE_SINGLE, CHOICE_MODE_MULTIPLE, CHOICE_MODE_MULTIPLE_MODAL
  }) @Retention(RetentionPolicy.SOURCE) public @interface ChoiceMode {
  }

  static class AdapterParams {

    @ChoiceMode int choiceMode;

    Drawable selector;
  }
}
