package com.xf.swipelayout.widget;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Created by X-FAN on 2016/12/2.
 */

public class SwipeLayout extends FrameLayout {

    public static final int CLOSE = 0;
    public static final int OPEN = 1;
    private int mState = CLOSE;
    private int mWidth;
    private int mHeight;
    private float mDownX;
    private float mDownY;

    private GestureDetectorCompat mGestureDetector;
    private SwipeListener mSwipeListener;
    private View mTopView;
    private View mBottomView;
    private ViewDragHelper mViewDragHelper;


    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mSwipeListener != null) {
                    mSwipeListener.onClickListener();
                }
                return super.onSingleTapUp(e);
            }
        });
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {//只对mTopView进行处理
                return child == mTopView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {//设置横向滑动的边界(left的值是mTopView左上角点的x坐标值)
                int newLeft;
                if (left <= -mBottomView.getMeasuredWidth()) {
                    newLeft = -mBottomView.getMeasuredWidth();
                } else if (left >= 0) {
                    newLeft = 0;
                } else {
                    newLeft = left;
                }
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {//因为不需要上下的滑动直接设置为0(top的值是mTopView左上角点的y坐标值)
                return 0;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {//手指松开时会回调该函数
                int right = mWidth - releasedChild.getRight();//mTopView右边界距离屏幕右边的距离
                int bottomWidth = mBottomView.getMeasuredWidth();
                if (right > bottomWidth * 9 / 10) {
                    scrollToLeftEdge();
                    return;
                }
                if (right <= bottomWidth / 10 && right > 0) {
                    scrollToRightEdge();
                    return;
                }
                if (xvel == 0) {//速度为0时单独处理
                    if (right >= bottomWidth / 2) {
                        scrollToLeftEdge();
                    } else if (right < bottomWidth / 2) {
                        scrollToRightEdge();
                    }
                    return;
                }
                if (xvel > 0) {//向右滑动后松手
                    scrollToRightEdge();
                } else {//向左滑动后松手
                    scrollToLeftEdge();
                }
            }
        });
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                if (mState == CLOSE) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = ev.getRawX() - mDownX;
                float distanceY = ev.getRawY() - mDownY;
                float angle;
                if (distanceX == 0) {
                    angle = 90;
                } else {
                    angle = (float) Math.toDegrees(Math.atan(Math.abs(distanceY / distanceX)));
                }
                if (angle < 45) {
                    return true;//拦截事件交给自己处理滑动
                }
                break;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        ViewParent viewParent = getParent();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = ev.getRawX() - mDownX;
                float distanceY = ev.getRawY() - mDownY;
                float angle;
                if (distanceX == 0) {
                    angle = 90;
                } else {
                    angle = (float) Math.toDegrees(Math.atan(Math.abs(distanceY / distanceX)));
                }
                if (angle < 45 && viewParent != null) {
                    viewParent.requestDisallowInterceptTouchEvent(true);//让父控件不要处理事件,交给自己处理
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (viewParent != null) {
                    viewParent.requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        mViewDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int measureHeight = mBottomView.getMeasuredHeight();
        int measureWidth = mBottomView.getMeasuredWidth();
        mBottomView.layout(mWidth - measureWidth, (mHeight - measureHeight) / 2, mWidth, mHeight + measureHeight / 2);//靠右边界垂直居中
        if (mState == OPEN) {
            mTopView.layout(-measureWidth, 0, mTopView.getMeasuredWidth() - measureWidth, mTopView.getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalStateException("only and should contain two child view");
        }
        View bottomView = getChildAt(0);
        if (!(bottomView instanceof ViewGroup)) {
            throw new IllegalStateException("sideslip menu should be contained by a viewgroup");
        }
        mBottomView = bottomView;
        mTopView = getChildAt(1);
    }


    //回滚到左边(只能在onViewReleased里使用该方法)
    private void scrollToLeftEdge() {
        mViewDragHelper.settleCapturedViewAt(-mBottomView.getMeasuredWidth(), 0);
        invalidate();
        mState = OPEN;
        if (mSwipeListener != null) {
            mSwipeListener.onOpenListener(this);
        }
    }

    //回滚到右边(只能在onViewReleased里使用该方法)
    private void scrollToRightEdge() {
        if (mState != CLOSE) {
            mViewDragHelper.settleCapturedViewAt(0, 0);
            invalidate();
            mState = CLOSE;
            if (mSwipeListener != null) {
                mSwipeListener.onCloseListener(this);
            }
        }
    }

    public void smoothClose() {
        mViewDragHelper.smoothSlideViewTo(mTopView, 0, 0);
        invalidate();
        mState = CLOSE;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
        invalidate();
    }

    public interface SwipeListener {
        void onOpenListener(SwipeLayout swipeLayout);

        void onCloseListener(SwipeLayout swipeLayout);

        void onClickListener();
    }

    public void setSwipeListener(SwipeListener mSwipeListener) {
        this.mSwipeListener = mSwipeListener;
    }


}
