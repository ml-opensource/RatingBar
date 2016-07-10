RatingBar
=========================

A simple RatingsBar that obtains most of its value proposition from being easier to customize the look of the star views 
than the default RatingsBar.

Usage
-----

- Have the selected state drawable `icn_rating_start_green` in your project.
- Have the unselected state drawable `icn_rating_start_grey` in your project.

- Include in layout:

```xml
<com.fuzzproductions.ratingbar.RatingBar
  android:id="@+id/ratingBar"
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"/>
```

Customizations
--------------

Set the height and width of the stars with `setStarSizeInDp(int dp)`. Omitting this method call will result in the use of
wrap_content.

Change the number of stars to choose from with `setStarCount(int count)`.


