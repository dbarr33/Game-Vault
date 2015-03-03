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

import com.apps.danielbarr.gamecollection.Model.IgnResponse;
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
public class IgnDialogListAdapter extends ArrayAdapter<IgnResponse> {

    private Context context;
    private ArrayList<IgnResponse> ignResponses;
    private ArrayList<Bitmap> images;
    private ArrayList<Boolean> hasLoaded;
    private Activity activity;


    public IgnDialogListAdapter(Context context, ArrayList<IgnResponse> ignResponses, Activity activity ) {
        super(context, 0, ignResponses);
        this.context = context;
        this.ignResponses = ignResponses;
        this.activity = activity;
        images = new ArrayList<>();
        hasLoaded = new ArrayList<>();
        for(int i = 0; i < ignResponses.size(); i++)
        {
            images.add(null);
            hasLoaded.add(false);
            if (InternetUtils.isNetworkAvailable((activity))) {

                new DownloadAsyncTask(i).execute(ignResponses.get(i).getThumb());
            }
            else
            {
                hasLoaded.set(i, false);
                Toast.makeText(activity, "You are not connected to the internet!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();


        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.ign_dialog_list_item, null);
            viewHolder.gameName = (TextView) convertView.findViewById(R.id.ign_list_item_gameName);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.ign_list_item_gameImage);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.ign_list_item_progressBar);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.gameName.setText(ignResponses.get(position).getTitle());
        viewHolder.imageView.setImageBitmap(images.get(position));

        if(images.get(position) == null && !hasLoaded.get(position))
        {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            if(InternetUtils.isNetworkAvailable((activity)))
            {
                new DownloadAsyncTask(position).execute(ignResponses.get(position).getThumb());
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
                Bitmap defualtBitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.default_image);
                images.set(position, defualtBitmap);
                notifyDataSetChanged();
            }
            else {
                hasLoaded.set(position,true);
                images.set(position, result);
                notifyDataSetChanged();
            }
        }
    }
}
