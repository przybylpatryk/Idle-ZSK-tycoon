package edu.zsk.zsktycoon;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class TramUpgradeActivity extends AppCompatActivity {
    private GameView gm;
    private LinearLayout modelsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tram_upgrade);
        gm = GameView.getInstance();

        TextView schoolCount = findViewById(R.id.school_count);
        TextView teacherCount = findViewById(R.id.teacher_count);
        TextView capLevel = findViewById(R.id.tram_cap_level);
        Button capBtn = findViewById(R.id.tram_cap_btn);
        TextView spdLevel = findViewById(R.id.tram_spd_level);
        Button spdBtn = findViewById(R.id.tram_spd_btn);
        modelsContainer = findViewById(R.id.models_container);
        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());

        gm.schoolStudents.observe(this, val -> schoolCount.setText("Uczniowie: " + val));
        gm.teachers.observe(this, val -> teacherCount.setText("Nauczyciele: " + val));

        gm.tramCapacityLevel.observe(this, lvl -> {
            capLevel.setText("Poziom " + lvl + "/5 (poj. " + gm.getTramCapacity() + ")");
            capBtn.setEnabled(lvl < 5);
        });
        gm.tramSpeedLevel.observe(this, lvl -> {
            spdLevel.setText("Poziom " + lvl + "/5 (krok " + gm.getTramStepMs() + "ms)");
            spdBtn.setEnabled(lvl < 5);
        });

        capBtn.setOnClickListener(v -> gm.upgradeTramCapacity());
        spdBtn.setOnClickListener(v -> gm.upgradeTramSpeed());

        buildModelList();
        gm.ownedTramModels.observe(this, set -> buildModelList());
        gm.activeTramModel.observe(this, active -> buildModelList());
    }

    private void buildModelList() {
        modelsContainer.removeAllViews();
        Set<String> owned = gm.ownedTramModels.getValue();
        String active = gm.activeTramModel.getValue();
        for (VehicleModel model : GameView.TRAM_MODELS) {
            View item = getLayoutInflater().inflate(R.layout.item_model, modelsContainer, false);
            TextView name = item.findViewById(R.id.model_name);
            TextView desc = item.findViewById(R.id.model_desc);
            Button buyBtn = item.findViewById(R.id.model_buy);
            Button selectBtn = item.findViewById(R.id.model_select);

            name.setText(model.name);
            desc.setText(model.description + " (koszt: " + model.costTeachers + " naucz.)");

            boolean isOwned = owned != null && owned.contains(model.id);
            boolean isActive = model.id.equals(active);

            buyBtn.setVisibility(isOwned ? View.GONE : View.VISIBLE);
            buyBtn.setOnClickListener(v -> gm.buyModel("tram", model.id));
            selectBtn.setVisibility(isOwned ? View.VISIBLE : View.GONE);
            selectBtn.setEnabled(!isActive);
            selectBtn.setOnClickListener(v -> gm.setActiveModel("tram", model.id));

            modelsContainer.addView(item);
        }
    }
}