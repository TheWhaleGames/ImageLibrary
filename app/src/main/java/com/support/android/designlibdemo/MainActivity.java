/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.support.android.designlibdemo;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity implements OnTaskCompleted{
    private DrawerLayout mDrawerLayout;
    private GetWebtoonTask getWebtoonTask;
    private String json_webtoon;
    public static List<String> imgUrlList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fresco.initialize(this);

        imgUrlList = new ArrayList<String>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        GetWebtoonTask getWebtoonTask = new GetWebtoonTask(MainActivity.this);
        getWebtoonTask.execute();
        Log.d("Log_d", "MainActivity onCreate");
    }

    @Override
    public void OnTaskCompleted() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class GetWebtoonTask extends AsyncTask<String, Void, Void> {
        private OnTaskCompleted taskCompleted;
        String url = "http://dev.api.battlecomics.co.kr/lulu/v1/webtoons";

        public GetWebtoonTask(OnTaskCompleted activityContext){
            this.taskCompleted = activityContext;
        }

        @Override
        protected Void doInBackground(String... params) {
            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder().url(url).build();
            try {
                Response response =okHttpClient.newCall(request).execute();
                json_webtoon = response.body().string();

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ObjectMapper mapper = new ObjectMapper();
            try{
                JsonNode obj = mapper.readTree(json_webtoon);
                JsonNode obj_changed = obj.get("data").get("webtoons");

                for(int index=0; index< obj_changed.size(); index++){
                    JsonNode item = obj_changed.get(index);
                    String imgURL = item.get("thumbnail").toString();
                    imgUrlList.add(imgURL);

                }

                taskCompleted.OnTaskCompleted();
            }catch (Exception e){

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_night_mode_system:
                setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case R.id.menu_night_mode_day:
                setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.menu_night_mode_night:
                setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.menu_night_mode_auto:
                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        CheeseListFragment glideFragment = new CheeseListFragment();
        CheeseListFragment picassoFragment = new CheeseListFragment();
        //CheeseListFragment frescoFragment = new CheeseListFragment();

        glideFragment.setConstructor("glide");
        picassoFragment.setConstructor("picasso");
        //frescoFragment.setConstructor("fresco");

        adapter.addFragment(glideFragment, "Glide/Picasso/Fresco 비교");
        adapter.addFragment(picassoFragment, "Fresco gif");
       // adapter.addFragment(frescoFragment, "Fresco");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public Task<List<ParseObject>> findAsync(Object query) {
        return Task.forResult(null).onSuccess(new Continuation<Object, List<ParseObject>>() {
            @Override
            public List<ParseObject> then(Task<Object> task) throws Exception {
                return null;
            }
        }, Task.BACKGROUND_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Log_d", "MainActivity onResume");
    }
    @Override
    public void onPause() {
        super.onPause();

        Log.d("Log_d", "MainActivity onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Log_d", "MainActivity onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Log_d", "MainActivity onDestroy");
    }
}
