package edu.zsk.zsktycoon;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gm = new ViewModelProvider(this).get(GameView.class);

        TextView platformText = findViewById(R.id.avenida_text);
        TextView schoolText = findViewById(R.id.school_text);
        ProgressBar tramProgress = findViewById(R.id.tram_progress);
        TextView tramStatus = findViewById(R.id.tram_status);
        ProgressBar trainProgress = findViewById(R.id.train_progress);
        TextView trainStatus = findViewById(R.id.train_status);

        gm.avenidaStudents.observe(this, value -> {
            platformText.setText(String.valueOf(value));
        });

        gm.schoolStudents.observe(this, value -> {
            schoolText.setText(String.valueOf(value));
        });

        gm.tramProgress.observe(this, progress -> {
            tramProgress.setProgress(progress);
        });

        gm.tramStatus.observe(this, status -> {
            tramStatus.setText(status);
        });

        gm.trainProgress.observe(this, progress -> {
            trainProgress.setProgress(progress);
        });

        gm.trainStatus.observe(this, status -> {
            trainStatus.setText(status);
        });
    }
}