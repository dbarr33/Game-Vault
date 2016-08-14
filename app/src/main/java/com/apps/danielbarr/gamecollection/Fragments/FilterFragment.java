package com.apps.danielbarr.gamecollection.Fragments;

import android.animation.Animator;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.ExpandableRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.FilterState;
import com.apps.danielbarr.gamecollection.Model.RealmDeveloper;
import com.apps.danielbarr.gamecollection.Model.RealmPublisher;
import com.apps.danielbarr.gamecollection.Model.SortType;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ListObjectBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class FilterFragment extends Fragment {

    public static final String NO_SELECTION = "No Selection";

    private ImageView filterToggle;
    private RadioButton alpha;
    private RadioButton date;
    private FilterState filterState;
    private View overLay;
    private View blockingView;
    private Button cancelFilter;
    private Button applyFilter;
    private FloatingActionButton addButton;
    private RecyclerView publisherRecyclerview;
    private RecyclerView developerRecyclerview;
    private TransitionDrawable td;

    private boolean toggled;
    private boolean isAnimating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterState = new FilterState();
        filterState.setSortType(SortType.DATE);
        toggled = false;
        isAnimating = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        alpha = (RadioButton) view.findViewById(R.id.alpha);
        date = (RadioButton) view.findViewById(R.id.date);
        filterToggle = (ImageView) getActivity().findViewById(R.id.filterButton);
        cancelFilter = (Button) view.findViewById(R.id.cancelFilter);
        applyFilter = (Button) view.findViewById(R.id.applyFilter);
        overLay = view.findViewById(R.id.overlay);
        blockingView = view.findViewById(R.id.blockingView);
        addButton = (FloatingActionButton)getActivity().findViewById(R.id.floatingActionButton);
        publisherRecyclerview = (RecyclerView)view.findViewById(R.id.publisherRecyclerView);
        developerRecyclerview = (RecyclerView)view.findViewById(R.id.developersRecyclerView);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupFilter();
        setupFilterToggle();
        setupTransitionDrawable();
        setupPublisherAdapter();
        setupDeveloperSpinner();
    }

    private void setupFilter() {
        alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterState.setSortType(SortType.ALPHA);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterState.setSortType(SortType.DATE);
            }
        });

    }

    private void setupFilterToggle(){
        getView().animate().setDuration(1).translationY(-1000);
        filterToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                animateView();
            }
        });

        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateView();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Main)getActivity()).applyFilter(filterState);
                animateView();
            }
        });
    }

    private void setupTransitionDrawable() {
        td = new TransitionDrawable( new Drawable[] {
                getResources().getDrawable(R.drawable.filled_filter),
                getResources().getDrawable(R.drawable.arrow_down)
        });
        td.setCrossFadeEnabled(true);
        filterToggle.setImageDrawable(td);
    }

    private void setupPublisherAdapter() {
        RealmList<RealmPublisher> publishers = RealmManager.getInstance().getAllPublishers();
        final ArrayList<String> publisherNames = ListObjectBuilder.createArrayList("Publisher", publishers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        publisherRecyclerview.setLayoutManager(linearLayoutManager);
        ExpandableRecyclerAdapter adapter = new ExpandableRecyclerAdapter(publisherNames, publisherRecyclerview, false);
        publisherRecyclerview.setAdapter(adapter);
        publisherRecyclerview.setVisibility(View.VISIBLE);
    }

    private void setupDeveloperSpinner() {
        RealmList<RealmDeveloper> developers = RealmManager.getInstance().getAllDevelopers();
        final ArrayList<String> names = ListObjectBuilder.createArrayList("Developers", developers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        developerRecyclerview.setLayoutManager(linearLayoutManager);
        ExpandableRecyclerAdapter adapter = new ExpandableRecyclerAdapter(names, developerRecyclerview, false);
        developerRecyclerview.setAdapter(adapter);
        developerRecyclerview.setVisibility(View.VISIBLE);
    }

    private void animateView() {
        float amountToMove;
        final int visibility;
        if(!isAnimating) {
            if(toggled) {
                td.reverseTransition(500);
                visibility = View.GONE;
                amountToMove = getView().getBottom() * -1;
                overLay.setVisibility(visibility);
                addButton.animate().scaleX(1);
                addButton.animate().scaleY(1);
            }
            else {
                setupPublisherAdapter();
                setupDeveloperSpinner();
                td.startTransition(500);
                visibility = View.VISIBLE;
                amountToMove = getActivity().findViewById(R.id.toolbar).getBottom();
                addButton.animate().scaleX(0);
                addButton.animate().scaleY(0);
            }

            getView().animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    overLay.setVisibility(visibility);
                    blockingView.setVisibility(visibility);
                    isAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).setDuration(500).translationY(amountToMove);
            toggled = !toggled;
        }
    }
}
