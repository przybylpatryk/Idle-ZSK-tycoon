package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;

public class MainActivity extends AppCompatActivity {

    private ClipDrawable tramClip;
    private ClipDrawable train1Clip;
    private ClipDrawable train2Clip;
    private ClipDrawable train3Clip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gm = GameView.getInstance();

        TextView avenidaText = findViewById(R.id.avenida_text);
        TextView schoolText = findViewById(R.id.school_text);
        TextView teacherText = findViewById(R.id.teacher_text);


        ImageView tramImage = findViewById(R.id.tram_image);
        ImageView train1Image = findViewById(R.id.train1_image);
        ImageView train2Image = findViewById(R.id.train2_image);
        ImageView train3Image = findViewById(R.id.train3_image);

        TextView tramStatus = findViewById(R.id.tram_status);
        TextView trainStatus1 = findViewById(R.id.train_status1);
        TextView trainStatus2 = findViewById(R.id.train_status2);
        TextView trainStatus3 = findViewById(R.id.train_status3);

        LinearLayout train2Card = findViewById(R.id.train2_card);
        LinearLayout train3Card = findViewById(R.id.train3_card);

        Button newTrainBtn = findViewById(R.id.new_train_btn);
        Button tramUpgradeBtn = findViewById(R.id.tram_upgrade_btn);
        Button train1UpgradeBtn = findViewById(R.id.train1_upgrade_btn);
        Button train2UpgradeBtn = findViewById(R.id.train2_upgrade_btn);
        Button train3UpgradeBtn = findViewById(R.id.train3_upgrade_btn);


        tramClip = (ClipDrawable) tramImage.getDrawable();
        train1Clip = (ClipDrawable) train1Image.getDrawable();
        train2Clip = (ClipDrawable) train2Image.getDrawable();
        train3Clip = (ClipDrawable) train3Image.getDrawable();


        gm.avenidaStudents.observe(this, val -> avenidaText.setText(String.valueOf(val)));
        gm.schoolStudents.observe(this, val -> schoolText.setText(String.valueOf(val)));
        gm.teachers.observe(this, val -> teacherText.setText("Nauczyciele: " + val));


        gm.tramProgress.observe(this, val -> {
            if (tramClip != null) {
                int level = val * 100;  // 0-100 -> 0-10000
                tramClip.setLevel(level);
            }
        });
        gm.tramStatus.observe(this, tramStatus::setText);


        gm.train1Progress.observe(this, val -> {
            if (train1Clip != null) {
                train1Clip.setLevel(val * 100);
            }
        });
        gm.train1Status.observe(this, trainStatus1::setText);


        gm.train2Progress.observe(this, val -> {
            if (train2Clip != null) {
                train2Clip.setLevel(val * 100);
            }
        });
        gm.train2Status.observe(this, trainStatus2::setText);


        gm.train3Progress.observe(this, val -> {
            if (train3Clip != null) {
                train3Clip.setLevel(val * 100);
            }
        });
        gm.train3Status.observe(this, trainStatus3::setText);


        gm.train2Owned.observe(this, owned ->
                train2Card.setVisibility(owned ? View.VISIBLE : View.GONE));
        gm.train3Owned.observe(this, owned ->
                train3Card.setVisibility(owned ? View.VISIBLE : View.GONE));


        gm.ownedTrainsCount.observe(this, cnt -> {
            if (cnt >= 3) {
                newTrainBtn.setVisibility(View.GONE);
            } else {
                newTrainBtn.setVisibility(View.VISIBLE);
                newTrainBtn.setEnabled(true);
            }
        });
        gm.nextTrainCost.observe(this, cost -> {
                newTrainBtn.setText("Kup nową linię (" + cost + " uczniów)");
        });

        newTrainBtn.setOnClickListener(v -> gm.buyNewTrain());

        tramUpgradeBtn.setOnClickListener(v ->
                startActivity(new Intent(this, TramUpgradeActivity.class)));

        train1UpgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainUpgradeActivity.class);
            intent.putExtra("nbr", 1);
            startActivity(intent);
        });

        train2UpgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainUpgradeActivity.class);
            intent.putExtra("nbr", 2);
            startActivity(intent);
        });

        train3UpgradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainUpgradeActivity.class);
            intent.putExtra("nbr", 3);
            startActivity(intent);
        });
    }
}