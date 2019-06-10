package com.wtz.tools.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.wtz.tools.R;

public class ActionBarActivity extends AppCompatActivity {

    private static final String TAG = "ActionBarActivity";
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_bar);

        initActionBar();
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle("标题");
        toolbar.setSubtitle("副标题");
        toolbar.setLogo(R.mipmap.ic_launcher_round);

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.keyboard_backspace);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(ActionBarActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu_items, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        mSearchView = (SearchView) searchItem.getActionView();

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "test share content");
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);

        return true;
    }
}
