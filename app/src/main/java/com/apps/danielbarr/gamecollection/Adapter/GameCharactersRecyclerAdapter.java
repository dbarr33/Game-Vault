package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.apps.danielbarr.gamecollection.Fragments.CharacterFragment;
import com.apps.danielbarr.gamecollection.Fragments.EditGameFragment;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Game.GameCharacter;
import com.apps.danielbarr.gamecollection.Model.RealmCharacter;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.AddFragmentCommand;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.apps.danielbarr.gamecollection.Uitilites.GameCharacterBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.HideFragmentCommand;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Daniel Barr (Fuzz)
 */

public class GameCharactersRecyclerAdapter extends RecyclerView.Adapter<GameCharactersRecyclerAdapter.ListViewHolder> {

    private Activity activity;
    private RealmList<RealmCharacter> realmCharacters;
    private HashMap<Integer, Integer> positions = new HashMap<Integer, Integer>();

    OnItemClickListener mItemClickListener;

    public GameCharactersRecyclerAdapter(final ArrayList<GameCharacter> gameCharacters)
    {
        setup();
        for(int i = 0; i < gameCharacters.size(); i++) {
            realmCharacters.add(new RealmCharacter());
            realmCharacters.get(i).setName(gameCharacters.get(i).getName());
            realmCharacters.get(i).setID(gameCharacters.get(i).getId());
            positions.put(gameCharacters.get(i).getId(), i);
            GameCharacterBuilder.getCharacterInfo(gameCharacters.get(i).getId(), gameCharacters.get(i).getName(), retroCallback);
        }
    }

    public GameCharactersRecyclerAdapter(RealmList<RealmCharacter> characters) {
        setup();
        for (int i = 0; i < characters.size(); i++) {
            realmCharacters.add(characters.get(i));
            if (realmCharacters.get(i).getImageURL() == null) {
                positions.put(realmCharacters.get(i).getID(), i);
                GameCharacterBuilder.getCharacterInfo(realmCharacters.get(i).getID(), realmCharacters.get(i).getName(), retroCallback);
            }
        }
    }

    private void setup() {
        this.realmCharacters = new RealmList<>();
        this.activity = GameApplication.getActivity();
        SetOnItemClickListener(characterTransition);

    }

    public RealmList<RealmCharacter> getRecyclerObjects() {
        return realmCharacters;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(com.apps.danielbarr.gamecollection.R.layout.character_list_item, viewGroup, false);
        ListViewHolder listViewHolder =  new ListViewHolder(itemView);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder listViewHolder, int i) {
        listViewHolder.mCharacterImageView.setImageBitmap(null);
        String temp = realmCharacters.get(i).getName();
        if (temp != null) {
            listViewHolder.mName.setText(temp);
        }

        if(realmCharacters.get(i).getImageURL() != null) {
            Glide.with(listViewHolder.mCharacterImageView.getContext())
                    .load(realmCharacters.get(i).getImageURL())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            listViewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            listViewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(listViewHolder.mCharacterImageView);
        }
        else {
            Glide.with(listViewHolder.mCharacterImageView.getContext())
                    .load(realmCharacters.get(i).getPhoto())
                    .into(listViewHolder.mCharacterImageView);
            listViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return realmCharacters.size();
    }

    private OnItemClickListener characterTransition = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            if(realmCharacters.get(position).getImageURL() != null || realmCharacters.get(position).getPhoto() != null) {
                HideFragmentCommand hideFragmentCommand = new HideFragmentCommand(activity, EditGameFragment.class.getName());
                hideFragmentCommand.execute();
                AddFragmentCommand addFragmentCommand = new AddFragmentCommand(CharacterFragment.newInstance(realmCharacters.get(position)), activity);
                addFragmentCommand.execute();
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
        protected ProgressBar progressBar;

        public ListViewHolder(final View itemView) {
            super(itemView);

            itemView.setClickable(true);
            mName = (TextView) itemView.findViewById(R.id.list_character_name);
            mCharacterImageView = (ImageView) itemView.findViewById(R.id.list_character_image);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
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
            void onItemClick(View view , int position);
        }

        public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }

}
