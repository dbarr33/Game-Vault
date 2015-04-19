package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class RelevantGameRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<String> list;
    private ArrayList<String> backupList;
    private Activity activity;
    private boolean headerMode;
    private RecyclerView recyclerView;
    private ArrayList<String> gameList;
    private int maxSize;

    public RelevantGameRecyclerAdapter(ArrayList<String> list, Activity activity, RecyclerView recyclerView, int maxSize)
    {
        this.backupList = list;
        gameList = list;
        this.activity = activity;
        headerMode = true;
        this.list = new ArrayList<>();
        this.list.add(backupList.get(0));
        this.recyclerView = recyclerView;
        this.maxSize = maxSize;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        if(i == 0) {
            itemView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.similar_game_header, viewGroup, false);
            itemView.setOnClickListener(collapseList);
            return new HeaderListViewHolder(itemView);
        }else
        {
            itemView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.similar_game_item, viewGroup, false);
            return  new ListViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof HeaderListViewHolder) {
            ((HeaderListViewHolder)viewHolder).mHeader.setText(list.get(0));
        }
        else {
            ((ListViewHolder)viewHolder).mName.setText(list.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<String> getGameList() {
        return gameList;
    }

    public void setGameList(ArrayList<String> gameList) {
        this.gameList = gameList;
    }

    private View.OnClickListener collapseList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(headerMode) {
                headerMode = false;
                ArrayList<String> temp = new ArrayList<>();
                list = backupList;
                recyclerView.setMinimumHeight(maxSize);
                notifyItemRangeChanged(0, list.size());
                ((ImageView)v.findViewById(R.id.plus_minus_icon)).setImageDrawable(activity.getResources().getDrawable(R.drawable.minus_icon));

            }
            else {
                headerMode = true;
                ArrayList<String> temp = new ArrayList<>();
                notifyItemRangeChanged(0, 1);
                recyclerView.setMinimumHeight(v.getMeasuredHeight());
                ((ImageView)v.findViewById(R.id.plus_minus_icon)).setImageDrawable(activity.getResources().getDrawable(R.drawable.plus_icon));

            }
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public class HeaderListViewHolder extends RecyclerView.ViewHolder {
        protected TextView mHeader;
        protected LinearLayout mHeaderLayout;

        public HeaderListViewHolder(View itemView) {
            super(itemView);
            mHeader = (TextView)itemView.findViewById(R.id.similar_game_header_textview);
            mHeaderLayout = (LinearLayout)itemView.findViewById(R.id.similar_game_hearderLayout);
        }
    }
    public class ListViewHolder extends RecyclerView.ViewHolder{

        protected TextView mName;

        public ListViewHolder(final View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.similar_game_name_textview);
        }
    }
}

