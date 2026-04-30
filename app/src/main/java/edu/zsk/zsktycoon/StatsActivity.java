package edu.zsk.zsktycoon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        GameView gm = GameView.getInstance();

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());

        TextView totalPassengers = findViewById(R.id.total_transported);
        TextView linesOwned = findViewById(R.id.lines_owned);
        TextView teachers = findViewById(R.id.teachers_collected);
        TextView adsWatched = findViewById(R.id.total_ads_watched);
        TextView trainsBought = findViewById(R.id.total_trains_bought);
        TextView upgradesBought = findViewById(R.id.total_upgrades_bought);

        gm.totalPassengers.observe(this, val -> totalPassengers.setText(String.valueOf(val)));
        gm.ownedTrainsCount.observe(this, val -> linesOwned.setText(val + "/3"));
        gm.teachers.observe(this, val -> teachers.setText(String.valueOf(val)));
        gm.totalAdsWatched.observe(this, val -> adsWatched.setText(String.valueOf(val)));
        gm.totalTrainsBought.observe(this, val -> trainsBought.setText(String.valueOf(val)));
        gm.totalUpgradesBought.observe(this, val -> upgradesBought.setText(String.valueOf(val)));
    }
}