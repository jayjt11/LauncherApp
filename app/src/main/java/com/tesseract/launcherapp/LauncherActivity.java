package com.tesseract.launcherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tesseract.launcherapp.adapter.LauncherAdapter;
import com.tesseract.launcherapp.model.Apps;
import com.tesseract.launcherapp.util.SimpleDividerItemDecoration;
import com.tesseract.launcherapp.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LauncherActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    LauncherAdapter launcherAdapter;
    private List<Apps> appsList;
    SearchView searchListView;
    TextView txtNoRecords;
    RecyclerView recyclerLauncher;

    List<Apps> filteredAppsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        searchListView = (SearchView) findViewById(R.id.searchView);

        searchListView.setQueryHint("Search Apps");
        searchListView.setOnQueryTextListener(this);

        txtNoRecords = (TextView) findViewById(R.id.txtNoRecords);
        recyclerLauncher = (RecyclerView) findViewById(R.id.recyclerLauncher);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        registerReceiver(myBroadcastReceiverInstallUninstall,intentFilter);

        new myThread().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (myBroadcastReceiverInstallUninstall != null) {
            unregisterReceiver(myBroadcastReceiverInstallUninstall);
        }
    }

    private BroadcastReceiver myBroadcastReceiverInstallUninstall = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                String packageName = intent.getDataString ();
                System.out.println ("installed:" + packageName + "package name of the program");
                Toast.makeText(LauncherActivity.this, "installed:" + packageName + "package name of the program", Toast.LENGTH_LONG).show();
            }
            if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                String packageName = intent.getDataString ();
                System.out.println ("uninstall:" + packageName + "package name of the program");
                Toast.makeText(LauncherActivity.this, "uninstall:" + packageName + "package name of the program", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.d("Test", "onQueryTextChange");

        filteredAppsList = new ArrayList<>();
        filteredAppsList = filter(appsList, newText);
        if (filteredAppsList.isEmpty()) {
            recyclerLauncher.setVisibility(View.GONE);
            txtNoRecords.setVisibility(View.VISIBLE);
        }
        else {
          //  isFiltered = true;
            recyclerLauncher.setVisibility(View.VISIBLE);
            txtNoRecords.setVisibility(View.GONE);
            launcherAdapter.setFilter(filteredAppsList);
        }
        return true;
    }

    // filter data

    private List<Apps> filter(List<Apps> apps, String query) {

        Log.d("Test", "filter");

        query = query.toLowerCase();
        filteredAppsList = new ArrayList<>();
        for (Apps app : apps) {
            String title = app.title.toLowerCase();
            if (title.contains(query)) {
                filteredAppsList.add(app);
            }
        }
        return filteredAppsList;
    }

    public class myThread extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Util.showLoader(LauncherActivity.this);
        }

        @Override
        protected String doInBackground(Void... Params) {

            PackageManager pm = getPackageManager();
            appsList = new ArrayList<>();

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> allApps = pm.queryIntentActivities(i, 0);
            for(ResolveInfo ri:allApps) {
                Apps app = new Apps();
                app.icon = ri.activityInfo.loadIcon(pm);
                app.title = ri.loadLabel(pm).toString();
                app.packageName = ri.activityInfo.packageName;
                app.activity = ri.activityInfo.name;

                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(ri.activityInfo.packageName, 0);
                    app.versionName = packageInfo.versionName;
                    app.versionCode = packageInfo.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appsList.add(app);
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            updateStuff();
        }
    }

    // update stuff
    public void updateStuff() {

        Collections.sort(appsList, new Comparator<Apps>() {
            @Override
            public int compare(Apps lhs, Apps rhs) {
                return lhs.title.compareTo(rhs.title);
            }
        });
        Util.hideLoader();
        launcherAdapter = new LauncherAdapter(this, appsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerLauncher.setLayoutManager(mLayoutManager);
        recyclerLauncher.addItemDecoration(new SimpleDividerItemDecoration(this));

        recyclerLauncher.setAdapter(launcherAdapter);
     //   radapter.notifyItemInserted(radapter.getItemCount()-1);
    }
}
