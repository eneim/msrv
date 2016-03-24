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

package im.ene.lab.msrv.sample.viewholder;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import im.ene.lab.msrv.ViewHolder;
import im.ene.lab.msrv.sample.R;
import im.ene.lab.msrv.sample.data.SimpleItem;

/**
 * Created by eneim on 3/23/16.
 */
public class SimpleViewHolder extends ViewHolder {

  public static final int LAYOUT_RES = R.layout.layout_simple_item;

  TextView textView;

  public SimpleViewHolder(View itemView) {
    super(itemView);
    textView = (TextView) itemView.findViewById(R.id.text);
  }

  @Override public void bind(Object item) {
    if (item instanceof SimpleItem) {
      textView.setText(((SimpleItem) item).content);
    }
  }

  @Override public void onSelectState(boolean activated) {
    Log.d(TAG, getAdapterPosition() + " | " + "isActivated = [" + activated + "]");
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      itemView.setSelected(activated);
    } else {
      itemView.setActivated(activated);
    }
  }

  private static final String TAG = "SimpleViewHolder";
}
