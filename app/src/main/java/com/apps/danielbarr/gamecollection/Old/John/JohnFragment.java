package com.apps.danielbarr.gamecollection.Old.John;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class JohnFragment extends Fragment {

    private Button yes;
    private Button no;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_john, container, false);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.GONE);


        yes = (Button)view.findViewById(R.id.yesButton);
        no = (Button)view.findViewById(R.id.noButton);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Correct John is Gay", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "FAG!!!", Toast.LENGTH_SHORT).show();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Wrong Please Try again", Toast.LENGTH_SHORT).show();
            }
        });

        return view;


    }
}
