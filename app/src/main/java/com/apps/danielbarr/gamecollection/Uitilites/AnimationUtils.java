package com.apps.danielbarr.gamecollection.Uitilites;

import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.UNSPECIFIED;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * @author Daniel Barr (Fuzz)
 */
public class AnimationUtils {
    static Interpolator easeInOutQuart = PathInterpolatorCompat.create(0.77f, 0f, 0.175f, 1f);

    public static Animation expand(final View view) {
        int widthSpec = makeMeasureSpec(((View) view.getParent()).getWidth(), EXACTLY);
        int wrapSpec = makeMeasureSpec(0, UNSPECIFIED);
        view.measure(widthSpec, wrapSpec);
        final int targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0 so use 1 instead.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);

                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setInterpolator(easeInOutQuart);
        animation.setDuration(computeDurationFromHeight(view));
        view.startAnimation(animation);

        return animation;
    }

    public static Animation collapse(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setInterpolator(easeInOutQuart);
        int durationMillis = computeDurationFromHeight(view);
        a.setDuration(durationMillis);
        view.startAnimation(a);
        return a;
    }

    private static int computeDurationFromHeight(View view) {
        return 400;
        // 1dp/ms * multiplier
        //return (int) (view.getMeasuredHeight() / view.getContext().getResources().getDisplayMetrics().density);
    }
}
