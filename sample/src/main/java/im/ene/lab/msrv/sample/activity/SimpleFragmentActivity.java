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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import im.ene.lab.msrv.Adapter;
import im.ene.lab.msrv.SelectableRecyclerView;
import im.ene.lab.msrv.ViewHolder;
import im.ene.lab.msrv.sample.R;
import im.ene.lab.msrv.sample.adapter.SimpleAdapter;
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

    SelectableRecyclerView recyclerView;

    SimpleAdapter adapter;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
      return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      recyclerView = (SelectableRecyclerView) view.findViewById(R.id.recycler_view);
      recyclerView.setLayoutManager(
          new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      recyclerView.addItemDecoration(
          new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      adapter = new SimpleAdapter();
      adapter.setItemClickListener(clickListener);
      adapter.setItemSelectedListener(selectedListener);
      recyclerView.setAdapter(adapter);
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
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
        adapter.clearSelections();
      }
    };

    private Adapter.OnItemSelectedListener selectedListener = new Adapter.OnItemSelectedListener() {
      @Override
      public void onItemSelected(Adapter adapter, ViewHolder viewHolder, View view, int pos,
          long id) {
        Toast.makeText(getContext(), "Selected: " + pos, Toast.LENGTH_SHORT).show();
      }

      @Override
      public boolean onInitSelectMode(Adapter adapter, ViewHolder viewHolder, View view, int pos,
          long id) {
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
          ((AppCompatActivity) getActivity()).startSupportActionMode(callback);
        }
        return true;
      }
    };

    private Adapter.OnItemClickListener clickListener = new Adapter.OnItemClickListener() {
      @Override
      public void onItemClick(Adapter adapter, ViewHolder viewHolder, View view, int pos, long id) {
        startActivity(new Intent(getContext(), DetailActivity.class));
      }

      @Override
      public boolean onItemLongClick(Adapter adapter, ViewHolder viewHolder, View view, int pos,
          long id) {
        Toast.makeText(getContext(), "Long clicked: " + pos, Toast.LENGTH_SHORT).show();
        return true;
      }
    };
  }
}
