package com.fuzzproductions.ratingbarexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fuzzproductions.ratingbar.RatingBar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "ExampleActivity";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.justsometext);

        final RatingBar barWithoutSelect = (RatingBar) findViewById(R.id.rating_bar_without_select);
        final RatingBar barWithSelect = (RatingBar) findViewById(R.id.rating_bar_with_select);
        bindRatingBar(barWithoutSelect);
        bindRatingBar(barWithSelect);

        View b = findViewById(R.id.random_starSize);
        if (b != null) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Random r = new Random(System.currentTimeMillis());
                    int size = r.nextInt(100);
                    setStarSizeInDp(barWithoutSelect, size);
                    setStarSizeInDp(barWithSelect, size);

                }
            });
        }

        b = findViewById(R.id.random_starCount);
        if (b != null) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Random r = new Random(System.currentTimeMillis());
                    int count = r.nextInt(10);
                    setStarCount(barWithoutSelect, count);
                    setStarCount(barWithSelect, count);

                }
            });
        }


    }

    private void setStarCount(RatingBar bar, int count) {
        if (bar != null) {
            bar.setMax(count);
        }
    }

    private void setStarSizeInDp(RatingBar bar, int size) {
        if (bar != null) {
            bar.setStarSizeInDp(size);
        }
    }

    private void bindRatingBar(RatingBar bar) {
        if (bar != null) {
            bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (textView != null) {
                        textView.setText(
                                String.format(
                                        "Currently selected: %s isFromUser: %s",
                                        rating,
                                        fromUser
                                )

                        );
                    }
                }
            });
            // If you want to see for yourself whether click events are passed through,
            // try out below lines of code.
//            bar.setIsIndicator(true);
//            ((ViewGroup) bar.getParent()).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Clicked background");
//                }
//            });

        }
    }
}
