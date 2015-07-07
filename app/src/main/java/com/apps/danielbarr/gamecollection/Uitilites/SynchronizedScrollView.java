package com.apps.danielbarr.gamecollection.Uitilites;

import android.animation.Animator;
import android.app.Activity;
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

    private View mSyncView;
    private float position = new Float(0.0);
    private Button toTheTopButton;
    private boolean animating = false;
    private Toolbar toolbar;
    private String toolbarTitle;
    private TextView toolbarTextView;
    private float alphaValue;

    public SynchronizedScrollView(Context context) {
        super(context);
    }

    public SynchronizedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        alphaValue = 1;
    }

    public SynchronizedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        alphaValue = 0;
    }

    public void init(Activity activity, View view){
        toolbar = (Toolbar)activity.findViewById(R.id.editToolbar);
        toolbarTextView = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toTheTopButton = (Button)view.findViewById(R.id.backToTheTopButton);
        mSyncView = view.findViewById(R.id.sync);

        toTheTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoothScrollTo(0, 0);
                toTheTopButton.setVisibility(View.GONE);
            }
        });
    }

    public void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(mSyncView == null) {
            return;
        }
        position =  mSyncView.getTop() - (mSyncView.getTop() - mSyncView.getBottom()) ;
    }

    @Override
    protected void onScrollChanged(int l, final int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mSyncView == null) {
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
        alphaValue = temp/ position;
        mSyncView.setAlpha(alphaValue);
        toolbar.setAlpha(1 - alphaValue);
        int matchDistance = mSyncView.getTop() - getScrollY();

        if(matchDistance < 0) {
            toolbarTextView.setText(toolbarTitle);
        } else {
            toolbarTextView.setText("");
        }
    }

    public void setViewAlpha() {
        if(mSyncView != null) {
            mSyncView.setAlpha(alphaValue);
        }

        if(toolbar != null) {
            toolbar.setAlpha(1 - alphaValue);
        }
    }
}