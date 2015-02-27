package com.apps.danielbarr.gamecollection.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.apps.danielbarr.gamecollection.Activities.EditGameActivity;
import com.apps.danielbarr.gamecollection.Adapter.IgnDialogListAdapter;
import com.apps.danielbarr.gamecollection.Model.IgnResponse;
import com.apps.danielbarr.gamecollection.R;

import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class IgnGamesFragment extends DialogFragment {

    private ArrayList<IgnResponse> ignResponses;
    private ListView gameList;
    private Button dialogButton;
    public static final String EXTRA_IGNGAMES= "com.apps.danielbarr.gamecollection.igngames";

    public static IgnGamesFragment newInstance(ArrayList<IgnResponse> ignResponses)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IGNGAMES,ignResponses);

        IgnGamesFragment fragment = new IgnGamesFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_ign_dialog, container, false);
        getDialog().setTitle("Found Games");
        getDialog().setCanceledOnTouchOutside(false);
        gameList = (ListView)view.findViewById(R.id.ign_dialog_gameList);
        dialogButton = (Button)view.findViewById(R.id.ign_dialog_cancelButton);

        ignResponses = (ArrayList<IgnResponse>)getArguments().getSerializable((EXTRA_IGNGAMES));

        final IgnDialogListAdapter ignDialogListAdapter = new IgnDialogListAdapter(getActivity(),ignResponses, getActivity());
        gameList.setAdapter(ignDialogListAdapter);

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
                ((EditGameActivity) getActivity()).setIgnResponse(ignDialogListAdapter.getItem(position));

                if(!ignResponses.get(position).thumb.toString().trim().matches("")) {
                    ((EditGameActivity) getActivity()).setGameImage((ImageView) view.findViewById(R.id.ign_list_item_gameImage));
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                getDialog().dismiss();
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
