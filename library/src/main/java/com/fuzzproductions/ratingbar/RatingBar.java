package com.fuzzproductions.ratingbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A rating bar that allows changes to the "stars". Currently does not support partial stars
 *
 * @author Piotr Leja (FUZZ)
 */
public class RatingBar extends LinearLayout implements View.OnTouchListener {
    @SuppressWarnings("unused")
    private static final String TAG = "RatingBar";
    protected static final int DEFAULT_FILLED_DRAWABLE = R.drawable.icn_rating_start_green;
    protected static final int DEFAULT_EMPTY_DRAWABLE = R.drawable.icn_rating_start_grey;
    private int mMaxCount = 5;
    private int currentlySelected;
    private int minSelected = 0;
    private int starSize = 0;
    @DrawableRes
    private int filledDrawable;
    @DrawableRes
    private int emptyDrawable;

    /**
     * Amount of space between consecutive rating stars - default 5 dp.
     */
    protected int margin;

    public RatingBar(Context context) {
        super(context);
        init(null);
    }

    public RatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * Initialize attributes obtained when inflating
     * @param attributeSet where we pull attributes from
     */
    protected void init(AttributeSet attributeSet){
        if(attributeSet != null){
            TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.RatingBar);
            filledDrawable = a.getResourceId(R.styleable.RatingBar_filledDrawable, DEFAULT_FILLED_DRAWABLE);
            emptyDrawable = a.getResourceId(R.styleable.RatingBar_emptyDrawable, DEFAULT_EMPTY_DRAWABLE);
            starSize = a.getDimensionPixelSize(R.styleable.RatingBar_starSize, 0); // TODO: change default value
            mMaxCount = a.getInt(R.styleable.RatingBar_maxStars, 5); // you usually go 1-5 stars when rating
            minSelected = a.getInt(R.styleable.RatingBar_minStars, 0);
            margin = a.getDimensionPixelSize(R.styleable.RatingBar_starSpacing, getDefaultSpacing());
            currentlySelected = a.getInt(R.styleable.RatingBar_starsSelected, minSelected);
            a.recycle();
        } else {
            setDefaultDrawables();
        }

        setOnTouchListener(null);
        updateChildViews();

    }

    private void setDefaultDrawables() {
        filledDrawable = DEFAULT_FILLED_DRAWABLE;
        emptyDrawable = DEFAULT_EMPTY_DRAWABLE;
    }

    public void setCurrentlySelectedPosition(int pos) {
        currentlySelected = pos;
        updateChildViews();

    }

    public int getCurrentlySelectedPosition() {
        return currentlySelected;
    }

    public void setStarCount(int count) {
        mMaxCount = count;
        updateChildViews();
    }

    public void setMinStarCount(int minStarCount) {
        minSelected = minStarCount;
    }

    public int getMinStarCount() {
        return minSelected;
    }

    private int getDefaultSpacing(){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                getResources().getDisplayMetrics()
        );
    }

    public void setStarSizeInDp(int size) {
        starSize = (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size,
                getResources().getDisplayMetrics()
        );
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            params.width = starSize;
            params.height = starSize;
            v.postInvalidate();
        }
    }

    private void updateChildViews() {

        int childCount = getChildCount();
        if (mMaxCount < childCount) {
            while (getChildCount() > mMaxCount) {
                removeViewAt(getChildCount() - 1);
            }
        } else if (mMaxCount > childCount) {
            while (getChildCount() < mMaxCount) {
                generateAndAddChildViewAndParams();
            }
        }

        if (currentlySelected < minSelected)
            currentlySelected = minSelected;

        for (int i = 0; i < getChildCount(); i++) {
            final ImageView v = (ImageView) getChildAt(i);
            //TODO: make below more dynamic... i guess
            if (i < currentlySelected) {
                v.setImageResource(filledDrawable);
            } else {
                v.setImageResource(emptyDrawable);
            }

            v.postInvalidate();
        }

    }

    /**
     * Changes the current filled drawable to the one passed in via the
     * {@code filledDrawable}. */
    public void setFilledDrawable(@DrawableRes int filledDrawable) {
        this.filledDrawable = filledDrawable;
        updateChildViews();
    }

    /**
     * Changes the current empty drawable to the one passed in via the
     * {@code emptyDrawable}. */
    public void setEmptyDrawable(@DrawableRes int emptyDrawable) {
        this.emptyDrawable = emptyDrawable;
        updateChildViews();
    }

    protected void generateAndAddChildViewAndParams() {
        ImageView view = new ImageView(getContext());
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams params = new LayoutParams(
                starSize == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : starSize,
                starSize == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : starSize
        );
        if (margin < 0) {
            margin = getDefaultSpacing();
        }
        params.setMargins(margin, margin, margin, margin);
        params.gravity = Gravity.CENTER;
        view.setPadding(0, 0, 0, 0);
        addView(view, params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        //We don't allow other touch listeners here
        super.setOnTouchListener(this);
    }

    private Rect hitRectCheck = new Rect();

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        for (int i = 0; i < getChildCount(); i++) {
            View v2 = getChildAt(i);
            v2.getHitRect(hitRectCheck);
            boolean b = hitRectCheck.contains((int) event.getX(), (int) event.getY());
            if (b) {
                if (i == 0 && minSelected == 0) {

                    float hitOnView = event.getX() - hitRectCheck.left;
                    if (hitOnView / hitRectCheck.width() <= .25f) {
                        currentlySelected = 0;
                    } else {
                        currentlySelected = 1;
                    }

                } else {
                    currentlySelected = i + 1;
                }
                updateChildViews();
            }
        }

        return true;
    }
}
