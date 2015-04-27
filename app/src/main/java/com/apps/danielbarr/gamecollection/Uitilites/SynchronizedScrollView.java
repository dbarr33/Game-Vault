package com.apps.danielbarr.gamecollection.Uitilites;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apps.danielbarr.gamecollection.R;

/**
 * @author Daniel Barr (Fuzz)
 */
public class SynchronizedScrollView extends ScrollView {

    private View mAnchorView;
    private View mSyncView;
    private float position = new Float(0.0);
    private Button toTheTopButton;
    private boolean animating = false;
    private Toolbar toolbar;
    private String toolbarTitle;
    private TextView toolbarTextView;

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

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        toolbarTextView = (TextView)toolbar.findViewById(R.id.toolbar_title);
    }

    public void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    public Button getToTheTopButton() {
        return toTheTopButton;
    }

    public void setToTheTopButton(Button toTheTopButton) {
        this.toTheTopButton = toTheTopButton;
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
        if(mAnchorView == null || mSyncView == null) {
            return;
        }
        position =  mSyncView.getTop() - (mSyncView.getTop() - mSyncView.getBottom()) ;

        //Calling this here attaches the views together if they were added
        // before layout finished
       // syncViews();
    }

    @Override
    protected void onScrollChanged(int l, final int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mAnchorView == null || mSyncView == null) {
            return;
        }

        if(t + 50 < oldt) {
            if(toTheTopButton.getVisibility() != GONE && !animating) {
                toTheTopButton.animate().translationY(-200).setListener( new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toTheTopButton.setVisibility(GONE);
                        animating = false;

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }
        else if(getScrollY() > 3000 && t > oldt) {
            if(toTheTopButton.getVisibility() != VISIBLE) {
                toTheTopButton.animate().translationY(300).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animating = true;
                        toTheTopButton.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animating = false;

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }


        float temp = position - t;
        temp = temp/ position;

        mSyncView.setAlpha(temp);
        toolbar.setAlpha(1 - temp);

        //Distance between the anchor view and the scroll position
        int matchDistance = mSyncView.getTop() - getScrollY();
        //Distance between scroll position and sync view
        int offset = getScrollY() - mSyncView.getTop();
        //Check if anchor is scrolled off screen

        if(matchDistance < 0) {
            toolbarTextView.setText(toolbarTitle);
        } else {

            toolbarTextView.setText("");
        }
    }
}