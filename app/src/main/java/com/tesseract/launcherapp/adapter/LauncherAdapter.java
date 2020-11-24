package com.tesseract.launcherapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tesseract.launcherapp.R;
import com.tesseract.launcherapp.model.Apps;

import java.util.ArrayList;
import java.util.List;

public class LauncherAdapter extends RecyclerView.Adapter<LauncherAdapter.ViewHolder> {
    public List<Apps> appsList;
    Context mContext;
    public LauncherAdapter(Context context, List<Apps> appsList) {

        this.mContext = context;
        this.appsList = appsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView, txtPackageName, txtActivityName, txtVersionName, txtVersionCode;
        public ImageView img;

        //This is the subclass ViewHolder which simply
        //'holds the views' for us to show on each cell_row
        public ViewHolder(View itemView) {
            super(itemView);

            //Finds the views from our cell_row.xmlw.xml
            img = (ImageView) itemView.findViewById(R.id.imgAppIcon);

            textView = (TextView) itemView.findViewById(R.id.txtTitle);
            txtPackageName = (TextView) itemView.findViewById(R.id.txtPackageName);
            txtActivityName = (TextView) itemView.findViewById(R.id.txtActivityName);
            txtVersionName = (TextView) itemView.findViewById(R.id.txtVersionName);
            txtVersionCode = (TextView) itemView.findViewById(R.id.txtVersionCode);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick (View v) {
            int pos = getAdapterPosition();
            Context context = v.getContext();

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appsList.get(pos).packageName);
            context.startActivity(launchIntent);
            Toast.makeText(v.getContext(), appsList.get(pos).title, Toast.LENGTH_LONG).show();

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //This is what adds the code we've written in here to our target view

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LauncherAdapter.ViewHolder viewHolder, int i) {

        String appLabel = appsList.get(i).title;
        String appPackage = appsList.get(i).packageName;
        String activity = appsList.get(i).activity;
        String versionName = appsList.get(i).versionName;
        int versionCode = appsList.get(i).versionCode;


        Drawable appIcon = appsList.get(i).icon;
        TextView textView = viewHolder.textView;
        textView.setText(appLabel);

        viewHolder.txtPackageName.setText(appPackage);
        viewHolder.txtActivityName.setText(activity);
        viewHolder.txtVersionName.setText(versionName);
        viewHolder.txtVersionName.setText(versionName);
        viewHolder.txtVersionCode.setText("" + versionCode);

        ImageView imageView = viewHolder.img;
        imageView.setImageDrawable(appIcon);
    }

    @Override
    public int getItemCount() {

        return appsList.size();
    }

    public void setFilter(List<Apps> countryModels){

        Log.d("Test", "setFilter");
        appsList = new ArrayList<Apps>();
        appsList.addAll(countryModels);
        notifyDataSetChanged();
    }
}