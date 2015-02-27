package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.R;

import java.util.List;

public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {

    Context context;
    List<DrawerItem> drawerItemList;
    int layoutResID;

    public CustomDrawerAdapter(Context context, int layoutResourceID,
                               List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            convertView = inflater.inflate(R.layout.drawer_list_item, null);
        }

        DrawerItem item = getItem(position);

        TextView gameNameTextView = (TextView)convertView.findViewById(R.id.drawer_item_platformName);
        gameNameTextView.setText(item.getName());

        ImageView gameIconImageView = (ImageView)convertView.findViewById(R.id.drawer_icon);
        gameIconImageView.setImageDrawable(convertView.getResources().getDrawable(item.getIconID()));

        return convertView;
    }
}