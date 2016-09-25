package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.GiantBombSearch;
import com.apps.danielbarr.gamecollection.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantDialogListAdapter extends ArrayAdapter<GiantBombSearch> {

    private ArrayList<GiantBombSearch> giantBombSearches;
    private Activity activity;

    public GiantDialogListAdapter(Context context, ArrayList<GiantBombSearch> giantBombSearches, Activity activity) {
        super(context, 0, giantBombSearches);
        this.giantBombSearches = giantBombSearches;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder temp = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (activity).getLayoutInflater();
            convertView = inflater.inflate(R.layout.giant_dialog_list_item, null);
            temp.gameName = (TextView) convertView.findViewById(R.id.giant_list_item_gameName);
            temp.imageView = (ImageView) convertView.findViewById(R.id.giant_list_item_gameImage);
            temp.progressBar = (ProgressBar) convertView.findViewById(R.id.giant_list_item_progressBar);
            convertView.setTag(temp);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.gameName.setText(giantBombSearches.get(position).getName());
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        if(giantBombSearches.get(position).getImage() != null) {
            Glide.with(activity)
                    .load(giantBombSearches.get(position).getImage().getThumb_url())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.imageView);
        }
        return convertView;
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView gameName;
        private ProgressBar progressBar;
    }
}
