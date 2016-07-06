package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.DrawerItem;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ItemTouchHelperAdapter;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by danielbarr on 7/4/15.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.DrawerView> implements ItemTouchHelperAdapter{

    private List<DrawerItem> list;
    private Activity activity;
    private final OnStartDragListner onStartDragListner;
    private int activatedPosition;
    private boolean displaySavePlatformCell;
    private boolean editingMode;
    private HashMap<Integer, Boolean> editingMap;
    private String editingTransitionString;
    private RealmManager realmManager;

    public interface OnDrawerClickListener {
        void onClick(int position);
    }
    private final OnDrawerClickListener onDrawerClickListener;
    public DrawerListAdapter(Activity activity,
                             OnStartDragListner onStartDragListner, OnDrawerClickListener onDrawerClickListener) {
        this.activity = activity;
        this.realmManager = RealmManager.getInstance();
        this.onStartDragListner = onStartDragListner;
        this.onDrawerClickListener = onDrawerClickListener;
        this.activatedPosition = 1;
        this.displaySavePlatformCell = false;
        this.editingMode = false;
        this.editingTransitionString = "";
        this.editingMap = new HashMap<>();
        setupDrawerList();
        for(int i = 0; i < list.size() + 2; i++) {
            editingMap.put(i, false);
        }
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
        else if(viewType == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_platform_cell, parent, false);
            viewHolder = new SavePlatformCell(itemView);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_platform_cell, parent, false);
            viewHolder = new EditPlatform(itemView);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 3;
        }
        else if(position == list.size() + 1) {

            if(displaySavePlatformCell) {
                return 2;
            }
            else {
                return 1;
            }
        }
        else if(editingMap.get(position)) {
            return 2;
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
        return list.size() + 2;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        final int offset = 1;
        Realm realm = RealmManager.getInstance().getRealm();
        realm.beginTransaction();

        RealmResults<DrawerItem> results = realm.allObjects(DrawerItem.class);
        realm.copyToRealm(list);

        String tempName = list.get(fromPosition - offset).getName();
        int tempID = list.get(fromPosition - offset).getIconID();
        results.get(fromPosition - offset).setIconID(list.get(toPosition - offset).getIconID());
        results.get(fromPosition - offset).setName(list.get(toPosition - offset).getName());
        results.get(toPosition - offset).setIconID(tempID);
        results.get(toPosition - offset).setName(tempName);
        realm.commitTransaction();

        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemMoved(int from, int to) {


    }

    public List<DrawerItem> getDrawerList(){
        return list;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class DrawerView extends RecyclerView.ViewHolder  {

        protected ImageView imageView;
        protected TextView textView;
        protected LinearLayout dragLayout;
        protected ImageView editImage;
        protected ImageView dragImage;

        public DrawerView(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(R.id.drawer_item_platformName);
            imageView = (ImageView)itemView.findViewById(R.id.drawer_icon);
            dragLayout = (LinearLayout)itemView.findViewById(R.id.drawer_item_dragIcon);
            editImage = (ImageView)itemView.findViewById(R.id.editButton);
            dragImage = (ImageView)itemView.findViewById(R.id.dragImage);
        }

        public void update() {
            final int position = getAdapterPosition() - 1;
            if(editingMode) {
                editImage.setVisibility(View.VISIBLE);
                dragImage.setVisibility(View.GONE);
            }
            else {
                editImage.setVisibility(View.GONE);
                dragImage.setVisibility(View.VISIBLE);
            }
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editingMap.put(getAdapterPosition(), true);
                    notifyItemChanged(getAdapterPosition());
                    editingTransitionString = textView.getText().toString();
                }
            });
            imageView.setImageDrawable(activity.getResources().getDrawable(list.get(position).getIconID()));
            textView.setText(list.get(position).getName());

            dragLayout.setOnTouchListener(new View.OnTouchListener() {
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
                    onDrawerClickListener.onClick(getAdapterPosition() - 1);
                    activatedPosition = getAdapterPosition();
                    itemView.setActivated(true);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class EditPlatform extends DrawerView  {
        Button editButton;
        public EditPlatform(View itemView) {
            super(itemView);
            editButton = (Button)itemView.findViewById(R.id.edit);

        }

        @Override
        public void update() {

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editingMode = !editingMode;
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
            if(getAdapterPosition() + 1 != getItemCount()) {
                platformTextView.setText(editingTransitionString);
            }
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition() + 1 == getItemCount()) {
                        DrawerItem drawerItem = new DrawerItem();
                        drawerItem.setName(platformTextView.getText().toString());
                        drawerItem.setPosition(getAdapterPosition() - 1);
                        drawerItem.setIconID(R.drawable.joystick);
                        editingMap.put(getItemCount() + 1, false);
                        realmManager.savePlatform(drawerItem);
                        displaySavePlatformCell = false;
                    }
                    else {
                        realmManager.updatePlatform(list.get(getAdapterPosition() - 1), platformTextView.getText().toString());
                    }
                    setupDrawerList();
                    resetView();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition() + 1 != getItemCount()) {
                        realmManager.deletePlatform(list.get(getAdapterPosition() - 1));
                        editingMap.put(getAdapterPosition(), false);
                        itemRemoved();
                        notifyItemRemoved(getAdapterPosition());
                    }
                    else {
                        resetView();
                    }

                }
            });

        }

        private void resetView() {
            if(getAdapterPosition() + 1 != getItemCount()) {
                editingMap.put(getAdapterPosition(), false);
            }else {
                displaySavePlatformCell = false;
            }
            notifyItemChanged(getAdapterPosition());
            platformTextView.setText("");
        }
    }

    private void itemRemoved() {
        for (int i = 0; i < list.size(); i++) {
            if(editingMap.get(i)) {
                editingMap.put(i - 1, editingMap.get(i));
            }
        }
        setupDrawerList();
    }
    private void setupDrawerList() {
        list = realmManager.getAllPlatforms();
    }
}
