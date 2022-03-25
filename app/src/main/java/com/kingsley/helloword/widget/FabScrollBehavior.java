package com.kingsley.helloword.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FabScrollBehavior extends FloatingActionButton.Behavior {

    private int mOffsetY;
    List<FloatingActionButton> subActionButtons = new ArrayList<>();
    FloatingActionButton mainActionButton;
    int goneCount;

    public void setMainActionButton(FloatingActionButton mainActionButton) {
        this.mainActionButton = mainActionButton;
    }

    public void addSubActionButton(FloatingActionButton button) {
        subActionButtons.add(button);
    }

    private void setSubActionButtonsVisibility(int visibility) {
        for (FloatingActionButton btn : subActionButtons) {
            if (View.VISIBLE == visibility){
                btn.show();
            } else {
                btn.hide();
            }
        }
    }

    public FabScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        setSubActionButtonsVisibility(View.GONE);
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        mOffsetY += dy;
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        int bottomMargin = layoutParams.bottomMargin;

        int childHeight = child.getHeight() + bottomMargin;
        if (mOffsetY == 0 || mOffsetY == childHeight) {
            return;
        }
        mOffsetY = Math.min(mOffsetY, childHeight);
        mOffsetY = Math.max(mOffsetY, 0);
        child.setTranslationY(mOffsetY);
    }
}
