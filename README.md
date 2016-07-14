RatingBar
=========================

<img src="art/demo.png" width="275" align="right"/>

A lightweight RatingsBar widget that makes it easier to customize the look of the children ratings views than the default RatingsBar.

Usage
-----

- Include the selected state drawable `icn_rating_start_green` in your project.
- Include the unselected state drawable `icn_rating_start_grey` in your project.

- Include in layout:

```xml
<com.fuzzproductions.ratingbar.RatingBar
    android:id="@+id/rating_bar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerInParent="true"
    app:minStars="1"
    app:maxStars="7"
    app:starSize="20dp"
    app:starsSelected="3"
    app:starSpacing="10dp"
    />
```

Customizations
--------------

Set the height and width of the stars with `setStarSizeInDp(int dp)`. Omitting this method call will result in the use of
wrap_content.

Change the number of stars to choose from with `setStarCount(int count)`.

Change the minimum stars allowed to be selected `setMinStarCount(int minCount)`
