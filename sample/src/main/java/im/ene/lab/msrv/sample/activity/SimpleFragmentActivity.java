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

package im.ene.lab.msrv.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import im.ene.lab.msrv.ErabiAdapter;
import im.ene.lab.msrv.ErabiRecyclerView;
import im.ene.lab.msrv.ViewHolder;
import im.ene.lab.msrv.sample.R;
import im.ene.lab.msrv.sample.adapter.SimpleErabiAdapter;
import im.ene.lab.msrv.sample.widget.DividerItemDecoration;

/**
 * Created by eneim on 3/23/16.
 */
public class SimpleFragmentActivity extends AppCompatActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .replace(android.R.id.content, SimpleFragment.newInstance())
          .commit();
    }
  }

  public static class SimpleFragment extends Fragment {

    public static SimpleFragment newInstance() {
      return new SimpleFragment();
    }

    ErabiRecyclerView recyclerView;

    SimpleErabiAdapter adapter;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      recyclerView = (ErabiRecyclerView) view.findViewById(R.id.recycler_view);
      recyclerView.setLayoutManager(
          new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      recyclerView.addItemDecoration(
          new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    private static final String TAG = "SimpleFragment";

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      adapter = new SimpleErabiAdapter();
      recyclerView.setMultiChoiceModeListener(new ErabiAdapter.MultiChoiceModeListener() {
        @Override public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
            boolean checked) {
          Log.d(TAG, "onItemCheckedStateChanged() called with: "
              + "mode = ["
              + mode
              + "], position = ["
              + position
              + "], id = ["
              + id
              + "], checked = ["
              + checked
              + "]");
        }

        @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
          mode.getMenuInflater().inflate(R.menu.menu_main, menu);
          return true;
        }

        @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
          return false;
        }

        @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
          return false;
        }

        @Override public void onDestroyActionMode(ActionMode mode) {

        }
      });
      // adapter.setItemClickListener(clickListener);
      // adapter.setItemSelectedListener(selectedListener);
      recyclerView.setAdapter(adapter);
      adapter.setOnItemClickListener(new ErabiAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(ErabiAdapter adapter, ViewHolder viewHolder, View view, int pos,
            long id) {
          Log.d(TAG, "onItemClick() called with: "
              + "adapter = ["
              + adapter
              + "], viewHolder = ["
              + viewHolder
              + "], view = ["
              + view
              + "], pos = ["
              + pos
              + "], id = ["
              + id
              + "]");
        }
      });
    }
  }
}
