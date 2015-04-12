package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.apps.danielbarr.gamecollection.Activities.EditGameActivity;
import com.apps.danielbarr.gamecollection.Adapter.GiantDialogListAdapter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.GiantBombSearch;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class GiantGamesFragment extends DialogFragment {

    private ArrayList<GiantBombSearch> giantBombSearches;
    private ListView gameList;
    private Button dialogButton;
    public static final String EXTRA_GIANTGAMES= "com.apps.danielbarr.gamecollection.giantgames";

    public static GiantGamesFragment newInstance(ArrayList<GiantBombSearch> giantBombSearches, String platform)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_GIANTGAMES, giantBombSearches);
        args.putString("platform",platform);

        GiantGamesFragment fragment = new GiantGamesFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_giant_dialog, container, false);
        getDialog().setTitle("Games Found:");
        getDialog().setCanceledOnTouchOutside(false);
        gameList = (ListView)view.findViewById(R.id.giant_dialog_gameList);
        dialogButton = (Button)view.findViewById(R.id.giant_dialog_cancelButton);

        giantBombSearches = (ArrayList<GiantBombSearch>)getArguments().getSerializable((EXTRA_GIANTGAMES));
        final GiantDialogListAdapter giantDialogListAdapter = new GiantDialogListAdapter(getActivity(), giantBombSearches, getActivity());
        gameList.setAdapter(giantDialogListAdapter);
        giantDialogListAdapter.notifyDataSetChanged();
        Log.e("ERROR", giantDialogListAdapter.getCount() + " num of games");

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, new Intent());
                dismiss();
            }
        });

        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getActivity(), EditGameActivity.class);
                i.putExtra("GiantBombResponse", giantDialogListAdapter.getItem(position));
                if(giantDialogListAdapter.getImages().get(position) != null) {
                    i.putExtra(EditGameFragment.EXTRA_SEARCH, giantDialogListAdapter.getImages().get(position));
                }
                else {
                    i.putExtra(EditGameFragment.EXTRA_SEARCH, BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.box_art));

                }
                i.putExtra(EditGameFragment.EXTRA_PLATFORM, getArguments().getString("platform"));
                getDialog().dismiss();
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getDialog() != null){
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, new Intent());
            getDialog().dismiss();
        }
    }
}
