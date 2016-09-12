package com.apps.danielbarr.gamecollection.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.apps.danielbarr.gamecollection.R;
import com.apps.danielbarr.gamecollection.Uitilites.GameApplication;
import java.util.ArrayList;

/**
 * @author Daniel Barr (Fuzz)
 */
public class ExpandableRecyclerAdapter
    extends RecyclerView.Adapter<ExpandableRecyclerAdapter.ExpandableViewHolder> {

  private ArrayList<String> list;
  private Activity activity;
  private boolean headerMode;
  private int length;
  private int headerResource;
  private int cellResource;
  private int activeCell;

  public ExpandableRecyclerAdapter(ArrayList<String> list) {
    this.activity = GameApplication.getActivity();
    headerMode = true;
    this.list = list;
    this.length = 1;
    this.headerResource = -1;
    this.cellResource = -1;
    activeCell = 1;
  }

  public void setHeaderResource(int resource) {
    headerResource = resource;
    notifyDataSetChanged();
  }

  public void setList(ArrayList<String> list) {
    this.list = list;
    headerMode = true;
    length = 1;
  }

  public void setCellResource(int resource) {
    cellResource = resource;
    notifyDataSetChanged();
  }

  public void setActiveCell(int activeCell) {
    this.activeCell = activeCell;
    notifyDataSetChanged();
  }

  public String getSelectedItem() {
    return list.get(activeCell);
  }

  @Override
  public ExpandableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View itemView;
    if (i == 0) {
      if (headerResource == -1) {
        itemView = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.similar_game_header, viewGroup, false);
      } else {
        itemView =
            LayoutInflater.from(viewGroup.getContext()).inflate(headerResource, viewGroup, false);
      }
      return new HeaderListViewHolder(itemView);
    } else {
      if (headerResource == -1) {
        itemView = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.similar_game_item, viewGroup, false);
      } else {
        itemView =
            LayoutInflater.from(viewGroup.getContext()).inflate(cellResource, viewGroup, false);
      }
      return new ListViewHolder(itemView);
    }
  }

  @Override
  public void onBindViewHolder(final ExpandableViewHolder viewHolder, int i) {
    viewHolder.update(i);
  }

  @Override
  public int getItemCount() {
    return length;
  }

  public ArrayList<String> getList() {
    return list;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return 0;
    } else {
      return 1;
    }
  }

  public abstract class ExpandableViewHolder extends RecyclerView.ViewHolder {

    public ExpandableViewHolder(View itemView) {
      super(itemView);
    }

    public void update(int position) {

    }
  }

  public class HeaderListViewHolder extends ExpandableViewHolder {
    protected TextView mName;
    protected LinearLayout mHeaderLayout;
    protected ImageView mHeaderImage;

    public HeaderListViewHolder(View itemView) {
      super(itemView);
      mName = (TextView) itemView.findViewById(R.id.similar_game_header_textview);
      mHeaderLayout = (LinearLayout) itemView.findViewById(R.id.similar_game_hearderLayout);
      mHeaderImage = (ImageView) itemView.findViewById(R.id.plus_minus_icon);

      mHeaderLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (headerMode) {
            length = list.size();
            headerMode = false;
            notifyItemRangeInserted(1, list.size() - 1);
            mHeaderImage.setImageDrawable(
                activity.getResources().getDrawable(R.drawable.minus_icon));
          } else {
            length = 1;
            headerMode = true;
            notifyItemRangeRemoved(1, list.size() - 1);
            mHeaderImage.setImageDrawable(
                activity.getResources().getDrawable(R.drawable.plus_icon));
          }
        }
      });
    }

    @Override
    public void update(int position) {
      mName.setText(list.get(position));
    }
  }

  public class ListViewHolder extends ExpandableViewHolder {

    protected TextView mName;
    private RadioButton radioButton;

    public ListViewHolder(final View itemView) {
      super(itemView);
      mName = (TextView) itemView.findViewById(R.id.similar_game_name_textview);
      if (cellResource != -1) {
        radioButton = (RadioButton) itemView.findViewById(R.id.similar_game_name_textview);
      }
    }

    @Override
    public void update(final int position) {
      if (cellResource == -1) {
        mName.setText(list.get(position));
      } else {
        radioButton.setText(list.get(position));
        if (activeCell == position) {
          radioButton.setChecked(true);
        } else {
          radioButton.setChecked(false);
        }
        radioButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            int oldPosition = activeCell;
            activeCell = position;
            notifyItemChanged(oldPosition);
          }
        });
      }
    }
  }
}

