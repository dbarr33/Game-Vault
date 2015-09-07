package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Adapter.GiantDialogListAdapter;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.GiantBombSearch;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.AddFragmentCommand;
import com.apps.danielbarr.gamecollection.Uitilites.HideFragmentCommand;

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


                if(giantDialogListAdapter.getImages().get(position) != null) {
                    giantDialogListAdapter.clearQueue();
                    getDialog().dismiss();
                    EditGameFragment editGameFragment = EditGameFragment.newInstance(getArguments().getString("platform"),
                            giantDialogListAdapter.getItem(position));

                    HideFragmentCommand hideFragmentCommand = new HideFragmentCommand(getActivity(), GameListFragment.class.getName());
                    hideFragmentCommand.execute();
                    AddFragmentCommand addFragmentCommand = new AddFragmentCommand(editGameFragment, getActivity());
                    addFragmentCommand.execute();
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Wait for " + giantDialogListAdapter.getItem(position).getName() + " to load",
                            Toast.LENGTH_SHORT).show();
                }
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
