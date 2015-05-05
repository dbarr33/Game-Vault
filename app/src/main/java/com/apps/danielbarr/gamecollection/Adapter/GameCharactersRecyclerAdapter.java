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
import com.apps.danielbarr.gamecollection.Model.GameCharacters;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Character;
import com.apps.danielbarr.gamecollection.Model.RecyclerObject;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.BuildGameCharacter;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * @author Daniel Barr (Fuzz)
 */

public class GameCharactersRecyclerAdapter extends RecyclerView.Adapter<GameCharactersRecyclerAdapter.ListViewHolder> {

    private ArrayList<Character> characters;
    private Activity activity;
    private ArrayList<Bitmap> gameImageView;
    private ArrayList<GameCharacters> gameCharacterses;
    private ArrayList<RecyclerObject> recyclerObjects;
    OnItemClickListener mItemClickListener;


    public GameCharactersRecyclerAdapter(ArrayList<com.apps.danielbarr.gamecollection.Model.GiantBomb.Character> characters, Activity activity)
    {
        this.characters = characters;
        gameImageView = new ArrayList<>();
        gameCharacterses = new ArrayList<>();
        recyclerObjects = new ArrayList<>();

        for(int i = 0; i < characters.size(); i++) {
            recyclerObjects.add(new RecyclerObject());
            recyclerObjects.get(i).setPhotosLoaded(false);
            gameImageView.add(null);
            gameCharacterses.add(new GameCharacters());
            gameCharacterses.get(i).setName(characters.get(i).getName());
            gameCharacterses.get(i).setID(characters.get(i).getId());
            new BuildGameCharacter(activity, characters.get(i), this, i);
        }
        this.activity = activity;
        SetOnItemClickListener(characterTransition);
    }

    public GameCharactersRecyclerAdapter(RealmList<GameCharacters> characters, Activity activity) {
        this.characters = new ArrayList<>();
        gameImageView = new ArrayList<>();
        gameCharacterses = new ArrayList<>();
        recyclerObjects = new ArrayList<>();

        for (int i = 0; i < characters.size(); i++) {
            Character temp = new Character();
            gameCharacterses.add(characters.get(i));
            recyclerObjects.add(new RecyclerObject());
            recyclerObjects.get(i).setPhotosLoaded(false);
            temp.setName(characters.get(i).getName());
            this.characters.add(temp);
            gameImageView.add(null);
            Bitmap bmp = BitmapFactory.decodeByteArray(characters.get(i).getPhoto(), 0, characters.get(i).getPhoto().length);
            if (bmp != null) {
                gameImageView.set(i, bmp);
            }
            else {
                new BuildGameCharacter(activity, characters.get(i), this, i);
            }
        }
        this.activity = activity;
        SetOnItemClickListener(characterTransition);
    }

    public void setCharactersAtPosition(int position, GameCharacters gameCharacters) {
        if(position < gameCharacterses.size()) {
            gameCharacterses.set(position, gameCharacters);
            recyclerObjects.get(position).setPhotosLoaded(true);
            recyclerObjects.get(position).setPhoto(gameCharacters.getPhoto());
            recyclerObjects.get(position).setDescription(gameCharacters.getDescription());
           // if(gameCharacters.getEnemies() != null) {
           //     recyclerObjects.get(position).setEnemies(gameCharacters.getEnemies());
           // }
            Bitmap bmp = BitmapFactory.decodeByteArray(gameCharacters.getPhoto(), 0, gameCharacters.getPhoto().length);
            if (bmp != null) {
                gameImageView.set(position, bmp);
                notifyDataSetChanged();
            }
        }
    }

    public ArrayList<RecyclerObject> getRecyclerObjects() {
        return recyclerObjects;
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
        listViewHolder.position = i;
        String holder = characters.get(i).getName();
        if (!holder.matches("")) {
            listViewHolder.mName.setText(holder);
        }

        if(gameImageView.get(i) != null) {
            listViewHolder.mCharacterImageView.setImageBitmap(gameImageView.get(i));
            listViewHolder.mProgressBar.setVisibility(View.GONE);
        }
        else {
            listViewHolder.mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public ArrayList<GameCharacters> getGameCharacterses() {
        return gameCharacterses;
    }

    OnItemClickListener characterTransition = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            if(gameCharacterses.get(position).getDescription() != null) {
                CharacterFragment characterFragment = CharacterFragment.newInstance(gameCharacterses.get(position));
                activity.getFragmentManager().beginTransaction().hide(activity.getFragmentManager().
                        findFragmentByTag(activity.getResources().getString(R.string.fragment_edit_game))).commit();
                activity.getFragmentManager().beginTransaction().add(R.id.content_frame, characterFragment, activity.getResources().getString(R.string.fragment_character))
                        .addToBackStack(null).commit();
            }
            else {
                Toast.makeText(activity.getApplicationContext(),  "Wait for " + gameCharacterses.get(position).getName() + " to load",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    public  class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mName;
        protected ImageView mCharacterImageView;
        protected ProgressBar mProgressBar;
        protected int position;

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
