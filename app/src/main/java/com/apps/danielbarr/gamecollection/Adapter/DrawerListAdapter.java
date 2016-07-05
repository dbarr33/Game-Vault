package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ItemTouchHelperAdapter;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 7/4/15.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.DrawerView> implements ItemTouchHelperAdapter{

    private RealmResults<DrawerItem> list;
    private Activity activity;
    private final OnStartDragListner onStartDragListner;
    private int activatedPosition;
    private boolean displaySavePlatformCell;

    public interface OnDrawerClickListener {
        void onClick(int position);
    }
    private final OnDrawerClickListener onDrawerClickListener;
    public DrawerListAdapter(RealmResults<DrawerItem> list, Activity activity,
                             OnStartDragListner onStartDragListner, OnDrawerClickListener onDrawerClickListener) {
        this.list = list;
        this.activity = activity;
        this.onStartDragListner = onStartDragListner;
        this.onDrawerClickListener = onDrawerClickListener;
        this.activatedPosition = 0;
        this.displaySavePlatformCell = false;
    }

    public interface OnStartDragListner{
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    @Override
    public DrawerView onCreateViewHolder(ViewGroup parent, int viewType) {
        DrawerView viewHolder;
        if(viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item, parent, false);
            viewHolder = new DrawerView(itemView);
        }
        else if(viewType == 1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_platform_cell, parent, false);
            viewHolder = new AddPlatformCell(itemView);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_platform_cell, parent, false);
            viewHolder = new SavePlatformCell(itemView);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == list.size()) {
            if(displaySavePlatformCell) {
                return 2;
            }
            else {
                return 1;
            }
        }
        else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(final DrawerView holder, final int position) {

        if(position != activatedPosition){
            holder.itemView.setActivated(false);
        }
        else{
            holder.itemView.setActivated(true);
        }

        holder.update();

    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        Realm realm = RealmManager.getInstance().getRealm();
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

        public void update() {
            imageView.setImageDrawable(activity.getResources().getDrawable(list.get(getAdapterPosition()).getIconID()));
            textView.setText(list.get(getAdapterPosition()).getName());

            dragImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        onStartDragListner.onStartDrag(DrawerView.this);
                    }

                    return false;
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onStartDragListner.onStartDrag(DrawerView.this);
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDrawerClickListener.onClick(getAdapterPosition());
                    activatedPosition = getAdapterPosition();
                    itemView.setActivated(true);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class AddPlatformCell extends DrawerView  {

        public AddPlatformCell(View itemView) {
            super(itemView);

        }

        @Override
        public void update() {
            itemView.setOnDragListener(null);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displaySavePlatformCell = true;
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    public class SavePlatformCell extends DrawerView  {
        private ImageView saveButton;
        private ImageView cancelButton;
        private EditText platformTextView;

        public SavePlatformCell(View itemView) {
            super(itemView);

            saveButton = (ImageView)itemView.findViewById(R.id.save);
            cancelButton = (ImageView)itemView.findViewById(R.id.cancel);
            platformTextView = (EditText)itemView.findViewById(R.id.platformName);

        }

        @Override
        public void update() {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetView();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetView();

                }
            });

        }

        private void resetView() {
            displaySavePlatformCell = false;
            notifyItemChanged(getAdapterPosition());
            platformTextView.setText("");
        }
    }
}
