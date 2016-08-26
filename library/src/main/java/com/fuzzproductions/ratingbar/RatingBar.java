package com.fuzzproductions.ratingbar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * A rating bar that allows customization to the star element and more
 *
 * @author Piotr Leja (FUZZ)(@raigex)
 */
public class RatingBar extends View {
    @SuppressWarnings("unused")
    private static final String TAG = "RatingBar";
    protected static final int DEFAULT_FILLED_DRAWABLE = R.drawable.icn_rating_start_green;
    protected static final int DEFAULT_EMPTY_DRAWABLE = R.drawable.icn_rating_start_grey;
    private int mMaxCount = 5;
    private float mRating;
    private int mMinSelectionAllowed = 0;
    private int mStarSize = 0;
    private boolean isIndicator = false;
    private float mStepSize = 1;

    @DrawableRes
    private int filledDrawable;
    @DrawableRes
    private int emptyDrawable;

    private Drawable baseDrawable;
    private ClipDrawable overlayDrawable;

    /**
     * Amount of space between consecutive rating stars - default 5 dp.
     */
    private int mMargin;

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
            mStarSize = a.getDimensionPixelSize(R.styleable.RatingBar_starSize, getPixelValueForDP(20));
            mMaxCount = a.getInt(R.styleable.RatingBar_numStars, 5); // you usually go 1-5 stars when rating
            mMinSelectionAllowed = a.getInt(R.styleable.RatingBar_minAllowedStars, 0);
            mMargin = a.getDimensionPixelSize(R.styleable.RatingBar_starSpacing, getPixelValueForDP(5));
            mRating = a.getFloat(R.styleable.RatingBar_rating, mMinSelectionAllowed);
            isIndicator = a.getBoolean(R.styleable.RatingBar_isIndicator, false);
            mStepSize = a.getFloat(R.styleable.RatingBar_stepSize, 1);
            a.recycle();
        } else {
            setDefaultDrawables();
        }
        super.setOnTouchListener(mTouchListener);
        setEmptyDrawable(emptyDrawable);
        setFilledDrawable(filledDrawable);
    }

    private void setDefaultDrawables() {
        setFilledDrawable(DEFAULT_FILLED_DRAWABLE);
        setEmptyDrawable(DEFAULT_EMPTY_DRAWABLE);
    }


    private void setRating(float newRating, boolean fromUser) {
        float mod = newRating % mStepSize;

        // patch up precision issue where this calculation results in a remainder that incorrectly subtracts off the rating.
        if (mod < mStepSize) {
            mod = 0;
        }
        mRating = newRating - mod;
        if (mRating < mMinSelectionAllowed) {
            mRating = mMinSelectionAllowed;
        } else if (mRating > mMaxCount) {
            mRating = mMaxCount;
        }
        if (mRatingBarListener != null) {
            mRatingBarListener.onRatingChanged(this, mRating, fromUser);
        }
        postInvalidate();

    }

    /**
     * Sets the current rating, if a rating is set that is not an interval of step size
     * (e.g. 1.2 if stepSize is .5) then we round down to nearest step size
     *
     * @param rating the rating to be set must be positive or 0
     */
    public void setRating(float rating) {
        setRating(rating, false);
    }

    /**
     * @return Returns the current rating
     */
    public float getRating() {
        return mRating;
    }

    /**
     * Sets the stars count
     *
     * @param count amount of stars to draw
     */
    public void setMax(int count) {
        mMaxCount = count;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * @return Return star count
     */
    public int getMax() {
        return this.mMaxCount;
    }

    /**
     * Sets the minimum allowable stars, so the user cannot swipe to 0 if rating must be at least 1
     *
     * @param minStarCount the minimum amount of stars that have to be selected
     */
    public void setMinimumSelectionAllowed(int minStarCount) {
        mMinSelectionAllowed = minStarCount;
        postInvalidate();
    }

    /**
     * @return the current min of stars allowed
     */
    public int getMinimumSelectionAllowed() {
        return mMinSelectionAllowed;
    }


    /**
     * Sets the stars margins, this is unconnected to starSize, it is a type of padding around each star
     *
     * @param marginInDp the dp size you wish to use
     */
    public void setStarMarginsInDP(int marginInDp) {
        setStarMargins(getPixelValueForDP(marginInDp));
    }

    /**
     * Sets the star margins in PIXELS
     *
     * @param margins margins in Pixels
     */
    public void setStarMargins(int margins) {
        this.mMargin = margins;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * @return returns current margins in pixels
     */
    public int getMargin() {
        return this.mMargin;
    }

    private int getPixelValueForDP(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    /**
     * Sets the square box for star size
     *
     * @param size the dp version of size
     */
    public void setStarSizeInDp(int size) {
        setStarSize(getPixelValueForDP(size));
    }

    /**
     * Sets the square box for star size
     *
     * @param size pixels for 1 side of square box
     */
    public void setStarSize(int size) {
        mStarSize = size;
        if (baseDrawable != null) {
            baseDrawable.setBounds(0, 0, mStarSize, mStarSize);
        }
        if (overlayDrawable != null) {
            overlayDrawable.setBounds(0, 0, mStarSize, mStarSize);
        }
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @SuppressLint("RtlHardcoded")
    private void createFilledClipDrawable(@NonNull Drawable d) {
        overlayDrawable = new ClipDrawable(
                d,
                Gravity.LEFT,
                ClipDrawable.HORIZONTAL
        );
        overlayDrawable.setBounds(0, 0, mStarSize, mStarSize);
    }

    /**
     * Changes the current filled drawable to the one passed in via the
     * {@code filledDrawable} drawable.
     */
    public void setFilledDrawable(Drawable filledDrawable) {
        if (overlayDrawable == null) {
            if (filledDrawable != null) {
                createFilledClipDrawable(filledDrawable);
            }
        } else {
            if (filledDrawable == null) {
                overlayDrawable = null;
            } else {
                createFilledClipDrawable(filledDrawable);
            }
        }
        postInvalidate();
    }

    /**
     * Changes the current filled drawable to the one passed in via the
     * {@code filledDrawable} resource.
     */
    public void setFilledDrawable(@DrawableRes int filledDrawable) {
        Drawable newVersion;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newVersion = getResources().getDrawable(filledDrawable, null);
        } else {
            newVersion = getResources().getDrawable(filledDrawable);
        }
        setFilledDrawable(newVersion);
    }

    /**
     * Changes the current empty drawable to the one passed in via the
     * {@code emptyDrawable} drawable.
     */
    public void setEmptyDrawable(Drawable emptyDrawable) {
        this.baseDrawable = emptyDrawable;
        baseDrawable.setBounds(0, 0, mStarSize, mStarSize);
        postInvalidate();
    }

    /**
     * Changes the current empty drawable to the one passed in via the
     * {@code emptyDrawable} resource.
     */
    public void setEmptyDrawable(@DrawableRes int emptyDrawable) {
        this.emptyDrawable = emptyDrawable;
        Drawable d;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            d = getResources().getDrawable(emptyDrawable, null);
        } else {
            d = getResources().getDrawable(emptyDrawable);
        }
        setEmptyDrawable(d);

    }

    /**
     * Set weather this rating bar is user touch modyfiable
     *
     * @param isIndicator if true user cannot change with touch, if false user can change with touch
     */
    public void setIsIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
    }

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        this.mRatingBarListener = listener;
    }

    public OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return mRatingBarListener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Currently we don't care about wrap_content, and other stuff
        int height = mMargin * 2 + mStarSize;
        int width = height * mMaxCount;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float movedX = 0;
        canvas.translate(0, mMargin);
        float remaining = mRating;
        for (int i = 0; i < mMaxCount; i++) {
            canvas.translate(mMargin, 0);
            movedX += mMargin;

            if (baseDrawable != null) {
                baseDrawable.draw(canvas);
            }
            if (overlayDrawable != null) {
                if (remaining >= 1) {
                    overlayDrawable.setLevel(10000);
                    overlayDrawable.draw(canvas);
                } else if (remaining > 0) {
                    overlayDrawable.setLevel((int) (remaining * 10000));
                    overlayDrawable.draw(canvas);
                } else {
                    overlayDrawable.setLevel(0);
                }
                remaining -= 1;
            }
            canvas.translate(mStarSize, 0f);
            movedX += mStarSize;

            canvas.translate(mMargin, 0);
            movedX += mMargin;
        }

        canvas.translate(movedX * -1, mMargin * -1);

    }

    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Basically do not allow user to update this stuff is indicator only
            if (isIndicator) {
                return true;
            }

            float x = (int) event.getX();

            float selectedAmount = 0;

            if (x >= 0 && x <= getWidth()) {
                int xPerStar = mMargin * 2 + mStarSize;
                if (x < xPerStar * .25f) {
                    selectedAmount = 0;
                } else {

                    if (mStepSize <= 0) {
                        mStepSize = 0.1f;
                    }

                    selectedAmount = (((x - xPerStar) / xPerStar) + 1);
                    float remainder = selectedAmount % mStepSize;
                    selectedAmount = selectedAmount - remainder;
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
    };

    //Interfaces

    public interface OnRatingBarChangeListener {
        void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser);
        //Possibly add a previously selected and currently selected part, but later.
    }

}
