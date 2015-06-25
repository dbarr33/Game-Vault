package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.GiantBombSearch;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ImageDownloadManager;
import com.apps.danielbarr.gamecollection.Uitilites.ImageDownloader;
import com.apps.danielbarr.gamecollection.Uitilites.InternetUtils;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantDialogListAdapter extends ArrayAdapter<GiantBombSearch> {

    private ArrayList<GiantBombSearch> giantBombSearches;
    private ArrayList<Bitmap> images;
    private ArrayList<Boolean> hasLoaded;
    private Activity activity;
    private ImageDownloadManager<Integer> imageDownloadManager;

    public GiantDialogListAdapter(Context context, ArrayList<GiantBombSearch> giantBombSearches, Activity activity) {
        super(context, 0, giantBombSearches);
        this.giantBombSearches = giantBombSearches;
        this.activity = activity;
        images = new ArrayList<>();
        hasLoaded = new ArrayList<>();
        imageDownloadManager = new ImageDownloadManager<Integer>();
        imageDownloadManager.setListener(new ImageDownloader.Listener<Integer>() {
            @Override
            public void onThumbNailDownloaded(Integer position, Bitmap thumbnail) {
                images.set(position, thumbnail);
                hasLoaded.set(position, true);
                notifyDataSetChanged();
            }
        });

        for(int i = 0; i < giantBombSearches.size(); i++) {
            images.add(null);
            hasLoaded.add(false);
            if (InternetUtils.isNetworkAvailable((activity))) {

                if (giantBombSearches.get(i).image != null) {
                    imageDownloadManager.queueThumbnail(i, giantBombSearches.get(i).getImage().getThumb_url());
            }
                else {
                    Bitmap defaultBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.box_art);
                    images.set(i, defaultBitmap);
                    hasLoaded.set(i, true);
                }
            }
            else {
                hasLoaded.set(i, false);
                Toast.makeText(activity, "You are not connected to the internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (activity).getLayoutInflater();
            convertView = inflater.inflate(R.layout.giant_dialog_list_item, null);
            viewHolder.gameName = (TextView) convertView.findViewById(R.id.giant_list_item_gameName);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.giant_list_item_gameImage);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.giant_list_item_progressBar);
            convertView.setTag(viewHolder);
        }


        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.gameName.setText(giantBombSearches.get(position).getName());
        viewHolder.imageView.setImageBitmap(images.get(position));


        if(!hasLoaded.get(position))
        {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            if(InternetUtils.isNetworkAvailable((activity))) {
                if (giantBombSearches.get(position).image != null) {
                    imageDownloadManager.queueThumbnail(position, giantBombSearches.get(position).getImage().getThumb_url());
                }else {
                    hasLoaded.set(position, true);
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }
            else
            {
                Toast.makeText(activity, "You are not connected to the internet!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
           viewHolder.progressBar.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void clearQueue(){
        imageDownloadManager.imageDownloader.clearQueue();
        imageDownloadManager.imageDownloader.quit();
    }


    private static class ViewHolder
    {
        private ImageView imageView;
        private TextView gameName;
        private ProgressBar progressBar;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }
}
