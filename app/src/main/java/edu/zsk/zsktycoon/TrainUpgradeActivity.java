package edu.zsk.zsktycoon;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import androidx.lifecycle.MutableLiveData;

public class TrainUpgradeActivity extends AppCompatActivity {
    private GameView gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_upgrade);
        gm = GameView.getInstance();

        int trainNumber = getIntent().getIntExtra("nbr", 0);

        TextView schoolCount = findViewById(R.id.school_count);
        TextView teacherCount = findViewById(R.id.teacher_count);
        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());

        gm.schoolStudents.observe(this, val -> schoolCount.setText("Uczniowie: " + val));
        gm.teachers.observe(this, val -> teacherCount.setText("Nauczyciele: " + val));

        setupTrainSection(1, findViewById(R.id.train1_section), gm.train1CapacityLevel, gm.train1SpeedLevel,
                gm.activeTrain1Model, gm.ownedTrain1Models, GameView.TRAIN_MODELS, "train1");
        setupTrainSection(2, findViewById(R.id.train2_section), gm.train2CapacityLevel, gm.train2SpeedLevel,
                gm.activeTrain2Model, gm.ownedTrain2Models, GameView.TRAIN_MODELS, "train2");
        setupTrainSection(3, findViewById(R.id.train3_section), gm.train3CapacityLevel, gm.train3SpeedLevel,
                gm.activeTrain3Model, gm.ownedTrain3Models, GameView.TRAIN_MODELS, "train3");

        findViewById(R.id.train1_section).setVisibility(trainNumber == 1 ? View.VISIBLE : View.GONE);
        findViewById(R.id.train2_wrapper).setVisibility(trainNumber == 2 ? View.VISIBLE : View.GONE);
        findViewById(R.id.train3_wrapper).setVisibility(trainNumber == 3 ? View.VISIBLE : View.GONE);
    }

    private void setupTrainSection(int num, View section, MutableLiveData<Integer> capLvlData,
                                   MutableLiveData<Integer> spdLvlData, MutableLiveData<String> activeModelData,
                                   MutableLiveData<Set<String>> ownedModelsData, List<VehicleModel> models,
                                   String vehicleTag) {
        TextView capLevel = section.findViewById(R.id.train_cap_level);
        Button capBtn = section.findViewById(R.id.train_cap_btn);
        TextView spdLevel = section.findViewById(R.id.train_spd_level);
        Button spdBtn = section.findViewById(R.id.train_spd_btn);
        TextView capCost = section.findViewById(R.id.train_cap_cost);
        TextView spdCost = section.findViewById(R.id.train_spd_cost);
        LinearLayout modelsContainer = section.findViewById(R.id.models_container);

        capLvlData.observe(this, lvl -> {
            String info = "Poziom " + lvl + "/5";
            capLevel.setText(info);
            capBtn.setEnabled(lvl < 5);
        });
        spdLvlData.observe(this, lvl -> {
            String info = "Poziom " + lvl + "/5";
            spdLevel.setText(info);
            spdBtn.setEnabled(lvl < 5);
        });
        capLvlData.observe(this, lvl -> {
            capCost.setText(gm.getUpgradeCost(lvl) + " uczniów");
            capBtn.setEnabled(lvl < 5);
        });
        spdLvlData.observe(this, lvl -> {
            spdCost.setText(gm.getUpgradeCost(lvl) + " uczniów");
            spdBtn.setEnabled(lvl < 5);
        });

        capBtn.setOnClickListener(v -> {
            if (vehicleTag.equals("train1")) gm.upgradeTrain1Capacity();
            else if (vehicleTag.equals("train2")) gm.upgradeTrain2Capacity();
            else gm.upgradeTrain3Capacity();
        });
        spdBtn.setOnClickListener(v -> {
            if (vehicleTag.equals("train1")) gm.upgradeTrain1Speed();
            else if (vehicleTag.equals("train2")) gm.upgradeTrain2Speed();
            else gm.upgradeTrain3Speed();
        });

        activeModelData.observe(this, active -> rebuildModels(modelsContainer, models, ownedModelsData.getValue(), active, vehicleTag));
        ownedModelsData.observe(this, owned -> rebuildModels(modelsContainer, models, owned, activeModelData.getValue(), vehicleTag));
    }

    private void rebuildModels(LinearLayout container, List<VehicleModel> models, Set<String> owned, String active, String vehicleTag) {
        container.removeAllViews();
        for (VehicleModel model : models) {
            View item = getLayoutInflater().inflate(R.layout.item_model, container, false);
            TextView name = item.findViewById(R.id.model_name);
            TextView desc = item.findViewById(R.id.model_desc);
            Button buyBtn = item.findViewById(R.id.model_buy);
            Button selectBtn = item.findViewById(R.id.model_select);

            name.setText(model.name);
            desc.setText(model.description + " (koszt: " + model.costTeachers + " naucz.)");

            boolean isOwned = owned != null && owned.contains(model.id);
            boolean isActive = model.id.equals(active);

            buyBtn.setVisibility(isOwned ? View.GONE : View.VISIBLE);
            buyBtn.setOnClickListener(v -> gm.buyModel(vehicleTag, model.id));

            selectBtn.setVisibility(isOwned ? View.VISIBLE : View.GONE);
            if (isOwned) {
                if (isActive) {
                    selectBtn.setText("Wybrano");
                    selectBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.selected_model)));
                    selectBtn.setEnabled(false);
                } else {
                    selectBtn.setText("Wybierz");
                    selectBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.accent)));
                    selectBtn.setEnabled(true);
                }
                selectBtn.setOnClickListener(v -> gm.setActiveModel(vehicleTag, model.id));
            }

            container.addView(item);
        }
    }
}