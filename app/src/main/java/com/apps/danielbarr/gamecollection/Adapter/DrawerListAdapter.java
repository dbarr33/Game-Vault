package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Image;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ItemTouchHelperAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 7/4/15.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.DrawerView> implements ItemTouchHelperAdapter{

    private RealmResults<DrawerItem> list;
    private Activity activity;
    private final OnStartDragListner onStartDragListner;
    private int activatedPosition;

    public interface OnDrawerClickListener {
        public void onClick(int position);
    }
    private final OnDrawerClickListener onDrawerClickListener;
    public DrawerListAdapter(RealmResults<DrawerItem> list, Activity activity,
                             OnStartDragListner onStartDragListner, OnDrawerClickListener onDrawerClickListener) {
        this.list = list;
        this.activity = activity;
        this.onStartDragListner = onStartDragListner;
        this.onDrawerClickListener = onDrawerClickListener;
        this.activatedPosition = 0;
    }

    public interface OnStartDragListner{
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public DrawerView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item, parent, false);
        return new DrawerView(itemView);
    }

    @Override
    public void onBindViewHolder(final DrawerView holder, final int position) {

        if(position != activatedPosition){
            holder.itemView.setActivated(false);
        }
        else{
            holder.itemView.setActivated(true);
        }

        holder.imageView.setImageDrawable(activity.getResources().getDrawable(list.get(position).getIconID()));
        holder.textView.setText(list.get(position).getName());

        holder.dragImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListner.onStartDrag(holder);
                }

                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrawerClickListener.onClick(holder.getAdapterPosition());
                activatedPosition = holder.getAdapterPosition();
                holder.itemView.setActivated(true);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        Realm realm = Realm.getInstance(activity);
        realm.beginTransaction();

        RealmResults<DrawerItem> results = realm.allObjects(DrawerItem.class);
        realm.copyToRealm(list);

        String tempName = list.get(fromPosition).getName();
        int tempID = list.get(fromPosition).getIconID();
        results.get(fromPosition).setIconID(list.get(toPosition).getIconID());
        results.get(fromPosition).setName(list.get(toPosition).getName());
        results.get(toPosition).setIconID(tempID);
        results.get(toPosition).setName(tempName);
        realm.commitTransaction();

        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemMoved(int from, int to) {


    }

    public RealmResults<DrawerItem> getDrawerList(){
        return list;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class DrawerView extends RecyclerView.ViewHolder  {

        protected ImageView imageView;
        protected TextView textView;
        protected LinearLayout dragImage;
        public DrawerView(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(R.id.drawer_item_platformName);
            imageView = (ImageView)itemView.findViewById(R.id.drawer_icon);
            dragImage = (LinearLayout)itemView.findViewById(R.id.drawer_item_dragIcon);
        }
    }
}
