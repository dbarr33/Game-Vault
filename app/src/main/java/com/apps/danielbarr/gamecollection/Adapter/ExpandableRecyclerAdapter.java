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
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.PictureUtils;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ExpandableViewHolder> {

    private ArrayList<String> list;
    private Activity activity;
    private boolean headerMode;
    private RecyclerView recyclerView;
    private int length;
    private boolean longTextMode;

    public ExpandableRecyclerAdapter(ArrayList<String> list, RecyclerView recyclerView, Boolean longTextMode) {
        this.activity = GameApplication.getActivity();
        headerMode = true;
        this.list = new ArrayList<>();
        this.list = list;
        this.recyclerView = recyclerView;
        this.length = 1;
        this.longTextMode = longTextMode;
    }

    @Override
    public ExpandableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        if(i == 0) {
            itemView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.similar_game_header, viewGroup, false);
            return new HeaderListViewHolder(itemView);
        }else {
            itemView  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.similar_game_item, viewGroup, false);
            return  new ListViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final ExpandableViewHolder viewHolder, int i) {
        viewHolder.update(i);
    }

    @Override
    public int getItemCount() {
        return length;
    }

    public ArrayList<String> getList() {
        return list;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public abstract class ExpandableViewHolder extends RecyclerView.ViewHolder{

        public ExpandableViewHolder(View itemView) {
            super(itemView);
        }

        public void update(int position){

        }
    }

    public class HeaderListViewHolder extends ExpandableViewHolder {
        protected TextView mName;
        protected LinearLayout mHeaderLayout;
        protected ImageView mHeaderImage;

        public HeaderListViewHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.similar_game_header_textview);
            mHeaderLayout = (LinearLayout)itemView.findViewById(R.id.similar_game_hearderLayout);
            mHeaderImage = (ImageView)itemView.findViewById(R.id.plus_minus_icon);

            mHeaderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (headerMode) {
                        if (longTextMode) {
                            int width = widthOfText(activity, list.get(1), v);
                            length = width / v.getMeasuredHeight();
                            recyclerView.getLayoutParams().height = v.getMeasuredHeight() * (width / v.getMeasuredWidth());

                        } else {
                            length = list.size();
                            recyclerView.getLayoutParams().height = v.getMeasuredHeight() * list.size();
                        }
                        length = list.size();
                        headerMode = false;
                        notifyItemRangeInserted(1, list.size() - 1);
                        mHeaderImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.minus_icon));

                    } else {
                        length = 1;
                        headerMode = true;
                        notifyItemRangeRemoved(1, list.size() - 1);
                        recyclerView.getLayoutParams().height = v.getMeasuredHeight();
                        mHeaderImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.plus_icon));
                    }

                }
            });
        }

        @Override
        public void update(int position) {
            mName.setText(list.get(position));
        }
    }
    public class ListViewHolder extends ExpandableViewHolder{

        protected TextView mName;

        public ListViewHolder(final View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.similar_game_name_textview);
        }

        @Override
        public void update(int position) {
            mName.setText(list.get(position));
        }
    }

    public int widthOfText(Activity activity, String text, View v){
        Rect bounds = new Rect();
        bounds.set(v.getHeight(), v.getWidth(), v.getHeight(), v.getWidth());
        Paint paint = new Paint();
        paint.setTextSize(PictureUtils.dpTOPX(14, activity));
        paint.getTextBounds(text, 0, text.length(), bounds);

        return (int) Math.ceil( bounds.width());
    }
}

