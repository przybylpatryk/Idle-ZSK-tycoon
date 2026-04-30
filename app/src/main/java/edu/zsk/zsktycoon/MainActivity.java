package edu.zsk.zsktycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameView gm = GameView.getInstance();

        TextView avenidaText = findViewById(R.id.avenida_text);
        TextView schoolText  = findViewById(R.id.school_text);
        TextView teacherText = findViewById(R.id.teacher_text);

        ImageView tramImage   = findViewById(R.id.tram_image);
        ImageView train1Image = findViewById(R.id.train1_image);
        ImageView train2Image = findViewById(R.id.train2_image);
        ImageView train3Image = findViewById(R.id.train3_image);

        FrameLayout tramContainer   = findViewById(R.id.tram_container);
        FrameLayout train1Container = findViewById(R.id.train1_container);
        FrameLayout train2Container = findViewById(R.id.train2_container);
        FrameLayout train3Container = findViewById(R.id.train3_container);

        TextView tramStatus   = findViewById(R.id.tram_status);
        TextView trainStatus1 = findViewById(R.id.train_status1);
        TextView trainStatus2 = findViewById(R.id.train_status2);
        TextView trainStatus3 = findViewById(R.id.train_status3);

        LinearLayout train2Card = findViewById(R.id.train2_card);
        LinearLayout train3Card = findViewById(R.id.train3_card);

        Button newTrainBtn     = findViewById(R.id.new_train_btn);
        Button tramUpgradeBtn  = findViewById(R.id.tram_upgrade_btn);
        Button train1UpgradeBtn = findViewById(R.id.train1_upgrade_btn);
        Button train2UpgradeBtn = findViewById(R.id.train2_upgrade_btn);
        Button train3UpgradeBtn = findViewById(R.id.train3_upgrade_btn);
        Button adButton        = findViewById(R.id.ad_button);

        TextView weatherEmoji       = findViewById(R.id.weather_emoji);
        TextView weatherDescription = findViewById(R.id.weather_description);
        TextView weatherMultiplier  = findViewById(R.id.weather_multiplier);

        gm.weatherEmoji.observe(this, weatherEmoji::setText);
        gm.weatherDescription.observe(this, weatherDescription::setText);
        gm.weatherMultiplier.observe(this, mul ->
                weatherMultiplier.setText("Mnożnik: ×" + String.format("%.1f", mul)));

        gm.avenidaStudents.observe(this, val -> avenidaText.setText(String.valueOf(val)));
        gm.schoolStudents.observe(this, val -> schoolText.setText(String.valueOf(val)));
        gm.teachers.observe(this, val -> teacherText.setText(String.valueOf(val)));

        tramImage.setImageResource(gm.getTramDrawableRes(gm.activeTramModel.getValue()));
        train1Image.setImageResource(gm.getTrainDrawableRes(gm.activeTrain1Model.getValue()));
        train2Image.setImageResource(gm.getTrainDrawableRes(gm.activeTrain2Model.getValue()));
        train3Image.setImageResource(gm.getTrainDrawableRes(gm.activeTrain3Model.getValue()));

        gm.tramProgress.observe(this, progress -> {
            if (tramContainer.getWidth() == 0) {
                tramContainer.post(() -> moveVehicle(tramImage, tramContainer, progress));
            } else {
                moveVehicle(tramImage, tramContainer, progress);
            }
        });
        gm.tramStatus.observe(this, tramStatus::setText);

        gm.train1Progress.observe(this, progress -> moveVehicle(train1Image, train1Container, progress));
        gm.train1Status.observe(this, trainStatus1::setText);

        gm.train2Progress.observe(this, progress -> moveVehicle(train2Image, train2Container, progress));
        gm.train2Status.observe(this, trainStatus2::setText);

        gm.train3Progress.observe(this, progress -> moveVehicle(train3Image, train3Container, progress));
        gm.train3Status.observe(this, trainStatus3::setText);

        gm.activeTramModel.observe(this, modelId ->
                tramImage.setImageResource(gm.getTramDrawableRes(modelId)));

        gm.activeTrain1Model.observe(this, modelId ->
                train1Image.setImageResource(gm.getTrainDrawableRes(modelId)));

        gm.activeTrain2Model.observe(this, modelId ->
                train2Image.setImageResource(gm.getTrainDrawableRes(modelId)));

        gm.activeTrain3Model.observe(this, modelId ->
                train3Image.setImageResource(gm.getTrainDrawableRes(modelId)));

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
        gm.nextTrainCost.observe(this, cost ->
                newTrainBtn.setText("Kup nową linię (" + cost + " uczniów)"));

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

        adButton.setOnClickListener(v ->
                startActivity(new Intent(this, AdActivity.class)));
    }

    private void moveVehicle(ImageView vehicle, ViewGroup container, int progress) {
        float maxX = container.getWidth() - vehicle.getWidth();
        float x = (progress / 100f) * maxX;
        vehicle.setTranslationX(x);
    }
}