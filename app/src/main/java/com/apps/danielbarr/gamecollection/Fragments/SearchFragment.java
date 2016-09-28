package com.apps.danielbarr.gamecollection.Fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.apps.danielbarr.gamecollection.Model.GiantBomb.Search.SearchResponse;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ApiHandler;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
    private boolean isBackgrounded = false;
    private ApiHandler apiHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        apiHandler = ApiHandler.getInstance();

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchTextView = (TextView)view.findViewById(R.id.searchTextField);
        searchButton = (Button)view.findViewById(R.id.searchButton);
        searchButton.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               performSearch();
            }
        });
        return view;
    }

    public void performSearch(){
        String searchText  = searchTextView.getText().toString();
        searchText.toLowerCase();
        GameApplication application = (GameApplication) getActivity().getApplication();
        Tracker mTracker = application.mTracker;
        mTracker.setScreenName("Game List");
        mTracker.send(new HitBuilders.EventBuilder().setAction("Search Performed")
                .setCategory("Search")
                .setLabel(searchText)
                .build());
        if (searchTextView.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "The game name must not be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            apiHandler.getSearchGiantBomb(searchTextView.getText().toString(), new Callback<SearchResponse>() {
                @Override
                public void success(SearchResponse SearchResponse, retrofit.client.Response response) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
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
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackgrounded = true;
        apiHandler.dismissDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        isBackgrounded = false;
    }
}