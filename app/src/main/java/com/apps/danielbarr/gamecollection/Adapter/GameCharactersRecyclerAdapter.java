package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Fragments.CharacterFragment;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.BuildGameCharacter;
import com.apps.danielbarr.gamecollection.Uitilites.FragmentController;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.RealmList;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */

public class GameCharactersRecyclerAdapter extends RecyclerView.Adapter<GameCharactersRecyclerAdapter.ListViewHolder> {

    private Activity activity;
    private ArrayList<RealmCharacter> realmCharacters;
    private HashMap<Integer, Integer> positions = new HashMap<Integer, Integer>();

    OnItemClickListener mItemClickListener;

    public GameCharactersRecyclerAdapter(final ArrayList<GameCharacter> gameCharacters, Activity activity)
    {
        setup(activity);

        for(int i = 0; i < gameCharacters.size(); i++) {
            realmCharacters.add(new RealmCharacter());
            realmCharacters.get(i).setName(gameCharacters.get(i).getName());
            realmCharacters.get(i).setID(gameCharacters.get(i).getId());
            positions.put(gameCharacters.get(i).getId(), i);
            BuildGameCharacter.getCharacterInfo(gameCharacters.get(i).getId(), gameCharacters.get(i).getName(), retroCallback, activity);
        }
    }

    public GameCharactersRecyclerAdapter(RealmList<RealmCharacter> characters, Activity activity) {
        setup(activity);
        for (int i = 0; i < characters.size(); i++) {
            realmCharacters.add(characters.get(i));
            Bitmap bmp = BitmapFactory.decodeByteArray(characters.get(i).getPhoto(), 0, characters.get(i).getPhoto().length);
            if (bmp == null) {
                positions.put(realmCharacters.get(i).getID(), i);
                BuildGameCharacter.getCharacterInfo(realmCharacters.get(i).getID(),realmCharacters.get(i).getName(), retroCallback, activity);
            }
        }
    }

    private void setup(Activity activity) {
        realmCharacters = new ArrayList<>();
        this.activity = activity;
        SetOnItemClickListener(characterTransition);

    }

    public ArrayList<RealmCharacter> getRecyclerObjects() {
        return realmCharacters;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(com.apps.danielbarr.gamecollection.R.layout.character_list_item, viewGroup, false);
        ListViewHolder listViewHolder =  new ListViewHolder(itemView);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder listViewHolder, int i) {
        listViewHolder.mCharacterImageView.setImageBitmap(null);
        String temp = realmCharacters.get(i).getName();
        if (temp != null) {
            listViewHolder.mName.setText(temp);
        }

        if(realmCharacters.get(i).getPhoto() != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(realmCharacters.get(i).getPhoto(), 0, realmCharacters.get(i).getPhoto().length);
            if (bmp != null) {
                listViewHolder.mCharacterImageView.setImageBitmap(bmp);
                listViewHolder.mProgressBar.setVisibility(View.GONE);
            } else {
                listViewHolder.mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return realmCharacters.size();
    }

    private OnItemClickListener characterTransition = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            if(realmCharacters.get(position).isPhotosLoaded()) {
                FragmentController fragmentController = new FragmentController(activity.getFragmentManager());
                fragmentController.hideFramentCommand(activity.getResources().getString(R.string.fragment_edit_game));
                fragmentController.addFragmentCommand(CharacterFragment.newInstance(realmCharacters.get(position)),
                        activity.getResources().getString(R.string.fragment_character));
            }
            else {
                Toast.makeText(activity.getApplicationContext(),  "Wait for " + realmCharacters.get(position).getName() + " to load",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private Callback<RealmCharacter> retroCallback = new Callback<RealmCharacter>() {
            @Override
            public void success(RealmCharacter realmCharacter, Response response) {
            if(positions.containsKey(realmCharacter.getID())) {
                realmCharacter.setPhotosLoaded(true);
                realmCharacters.set(positions.get(realmCharacter.getID()),realmCharacter);
                notifyDataSetChanged();
            }
        }
            @Override
            public void failure(RetrofitError error) {

        }
    };


    public  class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mName;
        protected ImageView mCharacterImageView;
        protected ProgressBar mProgressBar;

        public ListViewHolder(final View itemView) {
            super(itemView);

            itemView.setClickable(true);
            mName = (TextView) itemView.findViewById(R.id.list_character_name);
            mCharacterImageView = (ImageView) itemView.findViewById(R.id.list_character_image);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.list_character_progresBar);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

        public interface OnItemClickListener {
            public void onItemClick(View view , int position);
        }

        public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

}
