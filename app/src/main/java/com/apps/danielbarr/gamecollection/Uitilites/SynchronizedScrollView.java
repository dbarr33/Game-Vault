package com.apps.danielbarr.gamecollection.Uitilites;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SynchronizedScrollView extends ScrollView {

    private View mAnchorView;
    private View mSyncView;
    private float position;

    public SynchronizedScrollView(Context context) {
        super(context);
    }

    public SynchronizedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public SynchronizedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Attach the appropriate child view to monitor during scrolling
     * as the anchoring space for the floating view.  This view MUST
     * be an existing child.
     *
     * @param v View to manage as the anchoring space
     */
    public void setAnchorView(View v) {
        mAnchorView = v;
       // syncViews();
    }

    /**
     * Attach the appropriate child view to managed during scrolling
     * as the floating view.  This view MUST be an existing child.
     *
     * @param v View to manage as the floating view
     */
    public void setSynchronizedView(View v) {
        mSyncView = v;
       // syncViews();
    }

    //Position the views together
    private void syncViews() {
        if(mAnchorView == null || mSyncView == null) {
            return;
        }

        //Distance between the anchor view and the header view
        int distance = mAnchorView.getBottom() - mSyncView.getTop();
        mSyncView.offsetTopAndBottom(distance);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        position =  mSyncView.getTop() - (mSyncView.getTop() - mSyncView.getBottom()) ;

        //Calling this here attaches the views together if they were added
        // before layout finished
       // syncViews();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mAnchorView == null || mSyncView == null) {
            return;
        }

        float temp = position - t;
        temp = temp/ position;

        mSyncView.setAlpha(temp);

        //Distance between the anchor view and the scroll position
        int matchDistance = mSyncView.getTop() - getScrollY();
        //Distance between scroll position and sync view
        int offset = getScrollY() - mSyncView.getTop();
        //Check if anchor is scrolled off screen

        if(matchDistance < 0) {
            mAnchorView.setVisibility(VISIBLE);
            mSyncView.setVisibility(GONE);
        } else {
            mAnchorView.setVisibility(GONE);
            mSyncView.setVisibility(VISIBLE);
        }
    }
}