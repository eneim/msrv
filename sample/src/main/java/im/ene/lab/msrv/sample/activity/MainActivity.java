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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import im.ene.lab.msrv.Adapter;
import im.ene.lab.msrv.SelectableRecyclerView;
import im.ene.lab.msrv.ViewHolder;
import im.ene.lab.msrv.sample.R;
import im.ene.lab.msrv.sample.adapter.SimpleAdapter;
import im.ene.lab.msrv.sample.widget.DividerItemDecoration;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  SelectableRecyclerView recyclerView;

  SimpleAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    recyclerView = (SelectableRecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    recyclerView.addItemDecoration(
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
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
    @Override public void onItemSelected(Adapter adapter, ViewHolder viewHolder, View view, int pos,
        long id) {
      Toast.makeText(MainActivity.this, "Selected: " + pos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onInitSelectMode(Adapter adapter, ViewHolder viewHolder, View view, int pos,
        long id) {
      startSupportActionMode(callback);
      return true;
    }
  };

  private Adapter.OnItemClickListener clickListener = new Adapter.OnItemClickListener() {
    @Override
    public void onItemClick(Adapter adapter, ViewHolder viewHolder, View view, int pos, long id) {
      Toast.makeText(MainActivity.this, "Clicked: " + pos, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(Adapter adapter, ViewHolder viewHolder, View view, int pos,
        long id) {
      Toast.makeText(MainActivity.this, "Long clicked: " + pos, Toast.LENGTH_SHORT).show();
      return true;
    }
  };
}
