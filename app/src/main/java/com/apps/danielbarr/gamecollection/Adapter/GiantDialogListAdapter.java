package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.GiantBombSearch;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.InternetUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantDialogListAdapter extends ArrayAdapter<GiantBombSearch> {

    private Context context;
    private ArrayList<GiantBombSearch> giantBombSearches;
    private ArrayList<Bitmap> images;
    private ArrayList<Boolean> hasLoaded;
    private Activity activity;

    public GiantDialogListAdapter(Context context, ArrayList<GiantBombSearch> giantBombSearches, Activity activity) {
        super(context, 0, giantBombSearches);
        this.context = context;
        this.giantBombSearches = giantBombSearches;
        this.activity = activity;
        images = new ArrayList<>();
        hasLoaded = new ArrayList<>();
        for(int i = 0; i < giantBombSearches.size(); i++) {
            images.add(null);
            hasLoaded.add(false);
            if (InternetUtils.isNetworkAvailable((activity))) {

                if (giantBombSearches.get(i).image != null) {
                    new DownloadAsyncTask(i).execute(giantBombSearches.get(i).getImage().getThumb_url());
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

    public ArrayList<Boolean> getHasLoaded() {
        return hasLoaded;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
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
                    new DownloadAsyncTask(position).execute(giantBombSearches.get(position).getImage().getThumb_url());
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

    private static class ViewHolder
    {
        private ImageView imageView;
        private TextView gameName;
        private ProgressBar progressBar;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void setImages(ArrayList<Bitmap> images) {
        this.images = images;
    }

    private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private WeakReference weakReference;

        public DownloadAsyncTask(int position)
        {
            weakReference = new WeakReference(position);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String URL = params[0];
            Bitmap bitmap = null;
            try {
                URL imageURL = new URL(URL);

                BufferedInputStream bis = new BufferedInputStream(imageURL.openStream(), 10240);
                bitmap = BitmapFactory.decodeStream(bis);
                bis.close();

            } catch (MalformedURLException e) {
                Log.e("error", "Downloading Image Failed");

            }
            catch (IOException e)
            {
                Log.e("error", "Downloading Image Failed");
            }

            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            int position = (int)weakReference.get();

            if (result == null) {
                hasLoaded.set(position,true);
                Bitmap defaultBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.box_art);
                images.set(position, defaultBitmap);
                notifyDataSetChanged();
            }
            else {
                hasLoaded.set(position,true);
                images.set(position, result);
                notifyDataSetChanged();
            }
            weakReference.clear();
        }
    }
}
