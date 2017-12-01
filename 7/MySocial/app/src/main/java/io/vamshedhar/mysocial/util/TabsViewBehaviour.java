package io.vamshedhar.mysocial.util;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/19/17 7:19 PM.
 * vchinta1@uncc.edu
 */

public class TabsViewBehaviour  extends CoordinatorLayout.Behavior<TabLayout> {
    private int height;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, TabLayout child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, TabLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, TabLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyConsumed > 0) {
            slideUp(child);
        } else if (dyConsumed < 0) {
            slideDown(child);
        }
    }

    private void slideUp(TabLayout child) {
        child.clearAnimation();
        child.animate().translationY(-1 * height).setDuration(200);
    }

    private void slideDown(TabLayout child) {
        child.clearAnimation();
        child.animate().translationY(0).setDuration(200);
    }
}
