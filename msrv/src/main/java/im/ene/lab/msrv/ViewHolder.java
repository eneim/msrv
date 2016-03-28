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

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by eneim on 3/23/16.
 */
public abstract class ViewHolder extends RecyclerView.ViewHolder {

  public ViewHolder(View itemView) {
    super(itemView);
  }

  public void setOnViewClickListener(View.OnClickListener listener) {
    itemView.setOnClickListener(listener);
  }

  public void setOnViewLongClickListener(View.OnLongClickListener listener) {
    itemView.setOnLongClickListener(listener);
  }

  public abstract void bind(Object item);

  private Boolean selectState = null;

  public final void onSelectStateChanged(boolean selectState) {
    if (this.selectState == null || this.selectState != selectState) {
      this.selectState = selectState;
      onSelectState(selectState);
    }
  }

  protected void onSelectState(boolean activated) {
    itemView.setActivated(activated);
  }
}
