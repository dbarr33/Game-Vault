package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<String> list;
    private ArrayList<String> backupList;
    private Activity activity;
    private boolean headerMode;
    private RecyclerView recyclerView;
    private ArrayList<String> gameList;

    public ExpandableRecyclerAdapter(ArrayList<String> list, Activity activity, RecyclerView recyclerView)
    {
        this.backupList = list;
        gameList = list;
        this.activity = activity;
        headerMode = true;
        this.list = new ArrayList<>();
        this.list.add(backupList.get(0));
        this.recyclerView = recyclerView;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
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
            ArrayList<String> temp = new ArrayList<>();
            temp = list;
            list = backupList;
            backupList = temp;

            if(headerMode) {
                headerMode = false;
                if(list.get(0) == "Description") {
                    Rect bounds = new Rect();
                    bounds.set(v.getHeight(), v.getWidth(), v.getHeight(), v.getWidth());
                    Paint paint = new Paint();
                    paint.setTextSize(PictureUtils.dpTOPX(12, activity));
                    paint.getTextBounds(list.get(1), 0, list.get(1).length(), bounds);

                    int width = (int) Math.ceil( bounds.width());
                    recyclerView.getLayoutParams().height = v.getMeasuredHeight() * (width / v.getMeasuredWidth());

                }
                else {
                    recyclerView.getLayoutParams().height = v.getMeasuredHeight() * list.size();
                }
                notifyItemRangeInserted(1, list.size() -1);
                ((ImageView)v.findViewById(R.id.plus_minus_icon)).setImageDrawable(activity.getResources().getDrawable(R.drawable.minus_icon));

            }
            else {
                headerMode = true;
                notifyItemRangeRemoved(1, list.size() -1);
                recyclerView.getLayoutParams().height = v.getMeasuredHeight();
                ((ImageView)v.findViewById(R.id.plus_minus_icon)).setImageDrawable(activity.getResources().getDrawable(R.drawable.plus_icon));

            }
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

