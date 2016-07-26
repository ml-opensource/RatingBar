package com.fuzzproductions.ratingbarexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.fuzzproductions.ratingbar.RatingBar;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.justsometext);

        RatingBar bar = (RatingBar) findViewById(R.id.rating_bar);
        if(bar != null) {
            bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(textView != null) {
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
        }
    }
}
