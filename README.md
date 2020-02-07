RatingBar [![Slack Status](https://fuzz-opensource.herokuapp.com/badge.svg)](https://fuzz-opensource.herokuapp.com/)
=========================

<img src="art/demo.png" width="275" align="right"/>

A lightweight RatingsBar widget that makes it easier to customize the look of the children ratings views than the default RatingsBar.

**NOTE: This currently only allows use of stock RatingBar functions but not underlying AbsSeekBar functions**
**NOTE2: Min SDK allowed is SDK 9 - used support library only allows this**

Usage
-----
**Gradle:**

```xml
maven { url "https://www.jitpack.io" }
```
Add to dependencies
```
compile 'com.github.fuzz-productions:RatingBar:1.0.5'
```

- Include the selected state drawable `icn_rating_start_green` in your project.
- Include the unselected state drawable `icn_rating_start_grey` in your project.

- Include in layout:

```xml
<com.fuzzproductions.ratingbar.RatingBar
    android:id="@+id/rating_bar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    app:minAllowedStars="1"
    app:numStars="7"
    app:starSize="20dp"
    app:rating="3"
    app:starMargin="10dp"
    app:isIndicator="false"
    app:emptyDrawable="@drawable/icn_rating_star_green"
    app:filledDrawable="@drawable/icn_rating_star_grey"
    />
```

Customizations
--------------

Set the height and width of the stars with `setStarSizeInDp(int dp)`. Omitting this method call will result in the use of
default 30dp value.

Change the number of stars to choose from with `setMax(int count)` corresponds to .

Change the minimum stars allowed to be selected `setMinimumSelectionAllowed(int minCount)`
