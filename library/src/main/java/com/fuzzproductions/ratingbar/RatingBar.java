package com.fuzzproductions.ratingbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * A rating bar that allows changes to the "stars". Currently does not support partial stars
 *
 * @author Piotr Leja (FUZZ)
 */
public class RatingBar extends View implements View.OnTouchListener {
    @SuppressWarnings("unused")
    private static final String TAG = "RatingBar";
    protected static final int DEFAULT_FILLED_DRAWABLE = R.drawable.icn_rating_start_green;
    protected static final int DEFAULT_EMPTY_DRAWABLE = R.drawable.icn_rating_start_grey;
    private int mMaxCount = 5;
    private float currentlySelected;
    private int minSelected = 0;
    private int starSize = 0;
    private boolean isIndicator = false;

    @DrawableRes
    private int filledDrawable;
    @DrawableRes
    private int emptyDrawable;

    private Drawable baseDrawable;
    private ClipDrawable overlayDrawable;

    /**
     * Amount of space between consecutive rating stars - default 5 dp.
     */
    protected int margin;

    private OnRatingBarChangeListener mRatingBarListener = null;

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
     *
     * @param attributeSet where we pull attributes from
     */
    protected void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
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
        baseDrawable = getResources().getDrawable(emptyDrawable);
        if (baseDrawable != null) {
            baseDrawable.setBounds(0, 0, starSize, starSize);
        }

        Drawable d = getResources().getDrawable(filledDrawable);
        if (d != null) {
            d.setBounds(0, 0, starSize, starSize);
            overlayDrawable = new ClipDrawable(d, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            overlayDrawable.setBounds(d.getBounds());
        }
        setOnTouchListener(null);

    }

    private void setDefaultDrawables() {
        setFilledDrawable(DEFAULT_FILLED_DRAWABLE);
        setEmptyDrawable(DEFAULT_EMPTY_DRAWABLE);
    }


    private void setRating(float pos, boolean fromUser) {
        currentlySelected = pos;
        if (currentlySelected < minSelected) {
            currentlySelected = minSelected;
        } else if (currentlySelected > mMaxCount) {
            currentlySelected = mMaxCount;
        }
        if (mRatingBarListener != null) {
            mRatingBarListener.onRatingChanged(this, pos, fromUser);
        }
        postInvalidate();

    }

    public void setRating(float rating) {
        setRating(rating, false);
    }

    public float getRating() {
        return currentlySelected;
    }

    public void setMax(int count) {
        mMaxCount = count;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    public int getMax() {
        return this.mMaxCount;
    }

    public void setMinStarCount(int minStarCount) {
        minSelected = minStarCount;
        postInvalidate();
    }

    public int getMinStarCount() {
        return minSelected;
    }

    private int getDefaultSpacing() {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                getResources().getDisplayMetrics()
        );
    }

    public void setStarSizeInDp(int size) {
        starSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size,
                getResources().getDisplayMetrics()
        );
        if(baseDrawable != null){
            baseDrawable.setBounds(0, 0, starSize, starSize);
        }
        if(overlayDrawable != null){
            overlayDrawable.setBounds(0, 0, starSize, starSize);
        }
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * Changes the current filled drawable to the one passed in via the
     * {@code filledDrawable}.
     */
    public void setFilledDrawable(@DrawableRes int filledDrawable) {
        if(this.filledDrawable != filledDrawable) {
            this.filledDrawable = filledDrawable;
            overlayDrawable = new ClipDrawable(
                    getResources().getDrawable(filledDrawable),
                    Gravity.LEFT,
                    ClipDrawable.HORIZONTAL
            );
            overlayDrawable.setBounds(0, 0, starSize, starSize);
            postInvalidate();
        }
    }

    /**
     * Changes the current empty drawable to the one passed in via the
     * {@code emptyDrawable}.
     */
    public void setEmptyDrawable(@DrawableRes int emptyDrawable) {
        if(this.emptyDrawable != emptyDrawable) {
            this.emptyDrawable = emptyDrawable;
            baseDrawable = new ClipDrawable(
                    getResources().getDrawable(this.emptyDrawable),
                    Gravity.LEFT,
                    ClipDrawable.HORIZONTAL
            );
            baseDrawable.setBounds(0, 0, starSize, starSize);
        }
    }

    public void setIsIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        //We don't allow other touch listeners here
        super.setOnTouchListener(this);
    }

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        this.mRatingBarListener = listener;
    }

    public OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return mRatingBarListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Basically do not allow user to update this stuff is indicator only
        if (isIndicator)
            return true;

        float x = (int) event.getX();

        int selectedAmount = 0;

        if (x >= 0 && x <= getWidth()) {
            int xPerStar = margin * 2 + starSize;
            if (x < xPerStar * .25f) {
                selectedAmount = 0;
            } else {
                selectedAmount = (int) ((x - xPerStar * .25) / xPerStar + 1);
            }
        }
        if (x < 0) {
            selectedAmount = 0;

        } else if (x > getWidth()) {
            selectedAmount = mMaxCount;

        }

        setRating(selectedAmount, true);


        return true;
    }

    public interface OnRatingBarChangeListener {
        void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser);
        //Possibly add a previously selected and currently selected part, but later.
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Currently we don't care about wrap_content, and other stuff
        int height = margin * 2 + starSize;
        int width = height * mMaxCount;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float movedX = 0;
        canvas.translate(0, margin);

        for (int i = 0; i < mMaxCount; i++) {
            canvas.translate(margin, 0);
            movedX += margin;


            if (baseDrawable != null) {
                baseDrawable.draw(canvas);
            }
            if (overlayDrawable != null) {
                if (i + 1 <= currentlySelected) {
                    overlayDrawable.setLevel(10000);
                    overlayDrawable.draw(canvas);
                } else {
                    overlayDrawable.setLevel(0);
                }
            }
            canvas.translate(starSize, 0f);
            movedX += starSize;

            canvas.translate(margin, 0);
            movedX += margin;
        }

        canvas.translate(movedX * -1, margin * -1);

    }
}
