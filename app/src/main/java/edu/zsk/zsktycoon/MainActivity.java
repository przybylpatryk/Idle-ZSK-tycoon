package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gm = GameView.getInstance();

        TextView avenidaText = findViewById(R.id.avenida_text);
        TextView schoolText = findViewById(R.id.school_text);
        TextView teacherText = findViewById(R.id.teacher_text);
        ProgressBar tramProgress = findViewById(R.id.tram_progress);
        TextView tramStatus = findViewById(R.id.tram_status);
        ProgressBar trainProgress1 = findViewById(R.id.train_progress);
        TextView trainStatus1 = findViewById(R.id.train_status);
        ProgressBar trainProgress2 = findViewById(R.id.train_progress2);
        TextView trainStatus2 = findViewById(R.id.train_status2);
        ProgressBar trainProgress3 = findViewById(R.id.train_progress3);
        TextView trainStatus3 = findViewById(R.id.train_status3);

        Button newTrainBtn = findViewById(R.id.new_train_btn);
        Button tramUpgradeBtn = findViewById(R.id.tram_upgrade_btn);
        Button trainUpgradeBtn = findViewById(R.id.train_upgrade_btn);

        gm.avenidaStudents.observe(this, val -> avenidaText.setText(String.valueOf(val)));
        gm.schoolStudents.observe(this, val -> schoolText.setText(String.valueOf(val)));
        gm.teachers.observe(this, val -> teacherText.setText("Nauczyciele: " + val));

        gm.tramProgress.observe(this, tramProgress::setProgress);
        gm.tramStatus.observe(this, tramStatus::setText);

        gm.train1Progress.observe(this, trainProgress1::setProgress);
        gm.train1Status.observe(this, trainStatus1::setText);
        gm.train2Progress.observe(this, trainProgress2::setProgress);
        gm.train2Status.observe(this, trainStatus2::setText);
        gm.train3Progress.observe(this, trainProgress3::setProgress);
        gm.train3Status.observe(this, trainStatus3::setText);

        gm.train2Owned.observe(this, owned -> {
            int vis = owned ? View.VISIBLE : View.GONE;
            trainProgress2.setVisibility(vis);
            trainStatus2.setVisibility(vis);
        });
        gm.train3Owned.observe(this, owned -> {
            int vis = owned ? View.VISIBLE : View.GONE;
            trainProgress3.setVisibility(vis);
            trainStatus3.setVisibility(vis);
        });

        gm.nextTrainCost.observe(this, cost ->
                newTrainBtn.setText("Kup nową linię (" + cost + " uczniów)"));
        gm.ownedTrainsCount.observe(this, cnt -> {
            newTrainBtn.setEnabled(cnt < 3);
            if (cnt >= 3) newTrainBtn.setText("Wszystkie linie kupione");
        });
        newTrainBtn.setOnClickListener(v -> gm.buyNewTrain());

        tramUpgradeBtn.setOnClickListener(v ->
                startActivity(new Intent(this, TramUpgradeActivity.class)));
        trainUpgradeBtn.setOnClickListener(v ->
                startActivity(new Intent(this, TrainUpgradeActivity.class)));
    }
}