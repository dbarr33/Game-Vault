package com.apps.danielbarr.gamecollection.Uitilites;

/**
 * Created by danielbarr on 7/4/15.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);
    void onItemMoved(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
