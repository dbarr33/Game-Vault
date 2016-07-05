package com.apps.danielbarr.gamecollection.Uitilites;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.apps.danielbarr.gamecollection.Adapter.DrawerListAdapter;

/**
 * Created by danielbarr on 7/4/15.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;

    public  SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if(viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;

    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        adapter.onItemMoved(fromPos, toPos);
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }



    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //((DrawerListAdapter.DrawerView)viewHolder).onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder.getAdapterPosition() + 1 == ((DrawerListAdapter)adapter).getItemCount()) {
            return makeMovementFlags(0, 0);

        }
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


}
