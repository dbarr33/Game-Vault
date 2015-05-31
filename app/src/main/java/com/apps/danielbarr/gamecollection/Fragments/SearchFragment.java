package com.apps.danielbarr.gamecollection.Fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.danielbarr.gamecollection.Model.GiantBomb.SearchResponse;
import com.apps.danielbarr.gamecollection.Old.John.JohnFragment;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.GiantBombRestClient;
import com.apps.danielbarr.gamecollection.Uitilites.InternetUtils;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SearchFragment extends DialogFragment {

    private static final int REQUEST_CHOICE = 0;
    public static final String EXTRA_PASS_PLATFORM = "com.apps.danielbarr.gamecollection.passplatform";

    private TextView searchTextView;
    private Button searchButton;
    private ProgressDialog mDialog;
    private boolean isBackgrounded = false;

    public SearchFragment() {
        mDialog = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        getDialog().setTitle("Search Online Database");
        searchTextView = (TextView)view.findViewById(R.id.searchTextField);
        searchButton = (Button)view.findViewById(R.id.searchButton);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText  = searchTextView.getText().toString();
                searchText.toLowerCase();

                if (searchTextView.getText().toString().equals("")) {

                    Toast.makeText(getActivity().getApplicationContext(), "The game name must not be empty", Toast.LENGTH_LONG).show();

                }
                else if(searchText.equals("is john gay")){
                    getFragmentManager().beginTransaction().hide(getFragmentManager()
                            .findFragmentByTag(getResources().getString(R.string.fragment_game_list))).commit();
                    getFragmentManager().beginTransaction().add(R.id.content_frame, new JohnFragment(), "John").addToBackStack(null).commit();

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
                    dismiss();
                }
                else if (InternetUtils.isNetworkAvailable(getActivity())) {
                    mDialog = ProgressDialog.show(getActivity(), "Loading", "Wait while loading...");
                    GiantBombRestClient.get().getSearchGiantBomb(GiantBombRestClient.key, GiantBombRestClient.json,
                            searchTextView.getText().toString(), "game", 30, new Callback<SearchResponse>() {
                                @Override
                                public void success(SearchResponse SearchResponse, retrofit.client.Response response) {
                                    mDialog.dismiss();
                                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

                                    android.app.FragmentManager fm = getActivity().getFragmentManager();
                                    GiantGamesFragment dialog = GiantGamesFragment.newInstance(SearchResponse.getResults(), getArguments().getString(EXTRA_PASS_PLATFORM));
                                    dialog.setTargetFragment(SearchFragment.this, REQUEST_CHOICE);
                                    if (!isBackgrounded) {
                                        dialog.show(fm, "TAG");
                                        getDialog().dismiss();

                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    mDialog.dismiss();
                                    if (!InternetUtils.isNetworkAvailable(getActivity())) {
                                        AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                                        dialog.show();
                                    }
                                    Log.e("Giant", error.getMessage());
                                }
                            });
                }
                else {
                    AlertDialog.Builder dialog = InternetUtils.buildDialog(getActivity());
                    dialog.show();
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackgrounded = true;
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isBackgrounded = false;
    }
}