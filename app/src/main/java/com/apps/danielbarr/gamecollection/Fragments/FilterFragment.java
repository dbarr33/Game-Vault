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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import com.apps.danielbarr.gamecollection.Activities.Main;
import com.apps.danielbarr.gamecollection.Adapter.ExpandableRecyclerAdapter;
import com.apps.danielbarr.gamecollection.Model.FilterState;
import com.apps.danielbarr.gamecollection.Model.RealmDeveloper;
import com.apps.danielbarr.gamecollection.Model.RealmPublisher;
import com.apps.danielbarr.gamecollection.Model.SortType;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.ListObjectBuilder;
import com.apps.danielbarr.gamecollection.Uitilites.RealmManager;
import io.realm.RealmList;
import java.util.ArrayList;

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
  private LinearLayout filterLayout;
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
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_filter, container, false);
    alpha = (RadioButton) view.findViewById(R.id.alpha);
    date = (RadioButton) view.findViewById(R.id.date);
    filterToggle = (ImageView) getActivity().findViewById(R.id.filterButton);
    cancelFilter = (Button) view.findViewById(R.id.cancelFilter);
    applyFilter = (Button) view.findViewById(R.id.applyFilter);
    overLay = view.findViewById(R.id.overlay);
    blockingView = view.findViewById(R.id.blockingView);
    addButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
    publisherRecyclerview = (RecyclerView) view.findViewById(R.id.publisherRecyclerView);
    developerRecyclerview = (RecyclerView) view.findViewById(R.id.developersRecyclerView);
    filterLayout = (LinearLayout) view.findViewById(R.id.filterLayout);

    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    setupFilter();
    setupFilterToggle();
    setupTransitionDrawable();
    setupPublisherAdapter();
    setupDeveloperAdapter();
    setupFilterLayout();
  }

  private void setupFilterLayout() {
    if (publisherRecyclerview.getVisibility() == View.GONE
        && developerRecyclerview.getVisibility() == View.GONE) {
      filterLayout.setVisibility(View.GONE);
    } else {
      filterLayout.setVisibility(View.VISIBLE);
    }
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

  private void setupFilterToggle() {
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
        resetFilter();
      }
    });

    applyFilter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        filterApplied();
        animateView();
      }
    });
  }

  private void filterApplied() {
    String developer =
        ((ExpandableRecyclerAdapter) developerRecyclerview.getAdapter()).getSelectedItem();
    if (!developer.matches(NO_SELECTION)) {
      filterState.setSelectedDeveloper(developer);
    } else {
      filterState.setSelectedDeveloper(null);
    }

    String publisher =
        ((ExpandableRecyclerAdapter) publisherRecyclerview.getAdapter()).getSelectedItem();
    if (!publisher.matches(NO_SELECTION)) {
      filterState.setSelectedPublisher(publisher);
    } else {
      filterState.setSelectedPublisher(null);
    }

    ((Main) getActivity()).applyFilter(filterState);
  }

  private void setupTransitionDrawable() {
    td = new TransitionDrawable(new Drawable[] {
        getResources().getDrawable(R.drawable.filled_filter),
        getResources().getDrawable(R.drawable.x_mark2)
    });
    td.setCrossFadeEnabled(true);
    filterToggle.setImageDrawable(td);
  }

  private void setupPublisherAdapter() {
    RealmList<RealmPublisher> publishers = RealmManager.getInstance().getAllPublishers();
    final ArrayList<String> publisherNames =
        ListObjectBuilder.createArrayList("Publisher", publishers);
    setupAdapter(publisherRecyclerview, publisherNames);
    if (publishers.size() < 1) {
      publisherRecyclerview.setVisibility(View.GONE);
    } else {
      publisherRecyclerview.setVisibility(View.VISIBLE);
    }
  }

  private void setupDeveloperAdapter() {
    RealmList<RealmDeveloper> developers = RealmManager.getInstance().getAllDevelopers();
    final ArrayList<String> names = ListObjectBuilder.createArrayList("Developers", developers);
    setupAdapter(developerRecyclerview, names);
    if (developers.size() < 1) {
      developerRecyclerview.setVisibility(View.GONE);
    } else {
      developerRecyclerview.setVisibility(View.VISIBLE);
    }
  }

  private void setupAdapter(RecyclerView recyclerView, ArrayList<String> values) {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(linearLayoutManager);
    values.add(1, NO_SELECTION);
    if (recyclerView.getAdapter() == null) {
      ExpandableRecyclerAdapter adapter = new ExpandableRecyclerAdapter(values);
      adapter.setHeaderResource(R.layout.cell_filter_header);
      adapter.setCellResource(R.layout.cell_filter_item);
      recyclerView.setAdapter(adapter);
    } else {
      int selectedCell = 1;
      if (values.contains(filterState.getSelectedDeveloper())) {
        selectedCell = values.indexOf(filterState.getSelectedDeveloper());
      } else if (values.contains(filterState.getSelectedPublisher())) {
        selectedCell = values.indexOf(filterState.getSelectedPublisher());
      }
      ((ExpandableRecyclerAdapter) recyclerView.getAdapter()).setList(values);
      ((ExpandableRecyclerAdapter) recyclerView.getAdapter()).setActiveCell(selectedCell);
    }
  }

  private void animateView() {
    float amountToMove;
    final int visibility;
    if (!isAnimating) {
      if (toggled) {
        td.reverseTransition(500);
        visibility = View.GONE;
        amountToMove = getView().getBottom() * -1;
        overLay.setVisibility(visibility);
        addButton.animate().scaleX(1);
        addButton.animate().scaleY(1);
      } else {
        setupPublisherAdapter();
        setupDeveloperAdapter();
        setupFilterLayout();
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
          getView().setVisibility(View.VISIBLE);
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

  private void resetFilter() {
    filterState = new FilterState();
    filterState.setSortType(SortType.DATE);
    alpha.setChecked(false);
    date.setChecked(true);
    ((ExpandableRecyclerAdapter) developerRecyclerview.getAdapter()).setActiveCell(1);
    ((ExpandableRecyclerAdapter) publisherRecyclerview.getAdapter()).setActiveCell(1);
    filterApplied();
  }
}
