package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private GameView gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gm = App.getGameView();

        TextView platformText = findViewById(R.id.avenida_text);
        TextView schoolText   = findViewById(R.id.school_text);
        ProgressBar tramProgress  = findViewById(R.id.tram_progress);
        TextView    tramStatus    = findViewById(R.id.tram_status);
        ProgressBar trainProgress = findViewById(R.id.train_progress);
        TextView    trainStatus   = findViewById(R.id.train_status);
        Button      btnUpgrades   = findViewById(R.id.btn_upgrades);

        gm.avenidaStudents.observe(this, value -> {
            if (value != null) platformText.setText(String.valueOf(value));
        });

        gm.schoolStudents.observe(this, value -> {
            if (value != null) schoolText.setText(String.valueOf(value));
        });

        gm.tramProgress.observe(this, value -> {
            if (value != null) tramProgress.setProgress(value);
        });

        gm.tramStatus.observe(this, value -> {
            if (value != null) tramStatus.setText(value);
        });

        gm.trainProgress.observe(this, value -> {
            if (value != null) trainProgress.setProgress(value);
        });

        gm.trainStatus.observe(this, value -> {
            if (value != null) trainStatus.setText(value);
        });

        btnUpgrades.setOnClickListener(v ->
                startActivity(new Intent(this, UpgradeActivity.class)));
    }
}