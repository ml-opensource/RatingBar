package com.fuzzproductions.ratingbarexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.fuzzproductions.ratingbar.RatingBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.justsometext);

        RatingBar bar = (RatingBar) findViewById(R.id.rating_bar);
        if(bar != null) {
            bar.setRatingBarListener(new RatingBar.RatingBarListener() {
                @Override
                public void onChangeSelectedStar(RatingBar ratingBar, int previousSelected, int currentlySelected) {
                    if(textView != null){
                        textView.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "previously selected: %d currentlySelected %d",
                                        previousSelected,
                                        currentlySelected
                                )
                        );
                    }
                }
            });
        }
    }
}
