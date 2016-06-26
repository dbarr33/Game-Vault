package com.apps.danielbarr.gamecollection.Fragments;

import android.animation.Animator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Model.FilterState;
import com.apps.danielbarr.gamecollection.Model.SortType;
import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class FilterFragment extends Fragment {

    private ImageView filterToggle;
    private RadioButton alpha;
    private Button publisher;
    private FilterState filterState;

    private Button cancelFilter;
    private Button applyFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterState = new FilterState();
        filterState.setSortType(SortType.ALPHA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        alpha = (RadioButton) view.findViewById(R.id.alpha);
        publisher = (Button) view.findViewById(R.id.publisherButton);
        filterToggle = (ImageView) getActivity().findViewById(R.id.filterButton);
        cancelFilter = (Button) view.findViewById(R.id.cancelFilter);
        applyFilter = (Button) view.findViewById(R.id.applyFilter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupFilter();
        setupFilterToggle();
    }

    private void setupFilter() {
        alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterState.setSortType(SortType.ALPHA);
            }
        });
        publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterState.setSelectedPublisher("Nintendo");
            }
        });
    }

    private void setupFilterToggle(){
        getView().animate().setDuration(1).translationY(-1000);
        filterToggle.setOnClickListener(new View.OnClickListener() {
            private View overLay = getView().findViewById(R.id.overlay);
            private View blockingView = getView().findViewById(R.id.blockingView);
            boolean toggled = false;
            @Override
            public void onClick(final View v) {
                float amountToMove;
                final int visibility;
                if(toggled) {
                    visibility = View.GONE;
                    amountToMove = getView().getBottom() * -1;
                    overLay.setVisibility(visibility);
                }
                else {
                    visibility = View.VISIBLE;
                    amountToMove = getActivity().findViewById(R.id.toolbar).getBottom();
                }
                getView().animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        overLay.setVisibility(visibility);
                        blockingView.setVisibility(visibility);
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
        });

        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Main)getActivity()).applyFilter(filterState);
            }
        });

    }
}
