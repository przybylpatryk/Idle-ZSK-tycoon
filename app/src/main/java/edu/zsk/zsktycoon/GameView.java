package edu.zsk.zsktycoon;

import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.*;

import edu.zsk.zsktycoon.data.AppDatabase;
import edu.zsk.zsktycoon.data.GameStateEntity;

public class GameView {

    private static GameView instance;

    private AppDatabase db;

    public static GameView getInstance() {
        if (instance == null) instance = new GameView();
        return instance;
    }

    public MutableLiveData<Long> avenidaStudents = new MutableLiveData<>(0L);
    public MutableLiveData<Long> schoolStudents = new MutableLiveData<>(0L);
    public MutableLiveData<Long> teachers = new MutableLiveData<>(0L);
    public MutableLiveData<Integer> tramProgress = new MutableLiveData<>(0);
    public MutableLiveData<String> tramStatus = new MutableLiveData<>("Tramwaj czeka");
    public MutableLiveData<Boolean> tramActive = new MutableLiveData<>(false);
    private boolean tramOutward = true;
    public MutableLiveData<Integer> train1Progress = new MutableLiveData<>(0);
    public MutableLiveData<String> train1Status = new MutableLiveData<>("Linia Pobiedziska czeka");
    public MutableLiveData<Boolean> train1Active = new MutableLiveData<>(false);
    private boolean train1Outward = true;
    public MutableLiveData<Integer> train2Progress = new MutableLiveData<>(0);
    public MutableLiveData<String> train2Status = new MutableLiveData<>("Linia Puszczykowo (zablokowana)");
    public MutableLiveData<Boolean> train2Active = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> train2Owned = new MutableLiveData<>(false);
    private boolean train2Outward = true;
    public MutableLiveData<Integer> train3Progress = new MutableLiveData<>(0);
    public MutableLiveData<String> train3Status = new MutableLiveData<>("Linia Opalenica (zablokowana)");
    public MutableLiveData<Boolean> train3Active = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> train3Owned = new MutableLiveData<>(false);
    private boolean train3Outward = true;
    public MutableLiveData<Long> nextTrainCost = new MutableLiveData<>(50L);
    public MutableLiveData<Integer> ownedTrainsCount = new MutableLiveData<>(1);
    public MutableLiveData<Integer> tramCapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> tramSpeedLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train1CapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train1SpeedLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train2CapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train2SpeedLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train3CapacityLevel = new MutableLiveData<>(1);
    public MutableLiveData<Integer> train3SpeedLevel = new MutableLiveData<>(1);
    public MutableLiveData<String> activeTramModel = new MutableLiveData<>("Standardowy");
    public MutableLiveData<Set<String>> ownedTramModels = new MutableLiveData<>(new HashSet<>(Collections.singleton("Standardowy")));
    public MutableLiveData<String> activeTrain1Model = new MutableLiveData<>("standardowy");
    public MutableLiveData<Set<String>> ownedTrain1Models = new MutableLiveData<>(new HashSet<>(Collections.singleton("standardowy")));
    public MutableLiveData<String> activeTrain2Model = new MutableLiveData<>("standardowy");
    public MutableLiveData<Set<String>> ownedTrain2Models = new MutableLiveData<>(new HashSet<>(Collections.singleton("standardowy")));
    public MutableLiveData<String> activeTrain3Model = new MutableLiveData<>("standardowy");
    public MutableLiveData<Set<String>> ownedTrain3Models = new MutableLiveData<>(new HashSet<>(Collections.singleton("standardowy")));
    public MutableLiveData<Long> totalPassengers = new MutableLiveData<>(0L);
    public MutableLiveData<Long> totalAdsWatched = new MutableLiveData<>(0L);
    public MutableLiveData<Long> totalTrainsBought = new MutableLiveData<>(0L);
    public MutableLiveData<Long> totalUpgradesBought = new MutableLiveData<>(0L);

    public static final List<VehicleModel> TRAM_MODELS = Arrays.asList(
            new VehicleModel("Standardowy", "Standardowy", "Brak bonusów", 1.0f, 1.0f, 0),
            new VehicleModel("10", "Linia 10", "+50% pojemności, -10% prędkości", 0.9f, 1.5f, 50),
            new VehicleModel("17", "Linia 17", "+30% pojemności i prędkości", 1.3f, 1.3f, 75),
            new VehicleModel("5", "Linia 5", "+60% prędkości i +60% pojemności", 1.6f, 1.6f, 100)
    );

    public static final List<VehicleModel> TRAIN_MODELS = Arrays.asList(
            new VehicleModel("standardowy", "Standardowy", "Podstawowy model pociągu", 1.0f, 1.0f, 0),
            new VehicleModel("KW", "Koleje Wielkopolskie", "+60% pojemności", 1.0f, 1.6f, 60),
            new VehicleModel("Polregio", "Polregio", "+50% prędkości", 1.5f, 1.0f, 90),
            new VehicleModel("Intercity", "Intercity", "+40% prędkości i +40% pojemności", 1.4f, 1.4f, 120)
    );

    public MutableLiveData<Float> weatherMultiplier = new MutableLiveData<>(1.0f);
    public MutableLiveData<String> weatherDescription = new MutableLiveData<>("Ładuje pogodę...");
    public MutableLiveData<String> weatherEmoji = new MutableLiveData<>("🌤️");

    private static final int MAX_UPGRADE_LEVEL = 5;
    private static final long UPGRADE_BASE_COST = 50;

    private Handler handler = new Handler(Looper.getMainLooper());
    private long tramPassengers = 0;

    private Runnable train1LoopRunnable, train2LoopRunnable, train3LoopRunnable, tramLoopRunnable;

    public void init(Context context) {
        db = AppDatabase.getInstance(context);
        loadState();
    }

    private void loadState() {
        if (db == null) return;
        GameStateEntity state = db.gameStateDao().getState();
        if (state == null) {
            saveState();
            return;
        }

        avenidaStudents.setValue(state.avenidaStudents);
        schoolStudents.setValue(state.schoolStudents);
        teachers.setValue(state.teachers);

        tramCapacityLevel.setValue(state.tramCapacityLevel);
        tramSpeedLevel.setValue(state.tramSpeedLevel);
        activeTramModel.setValue(state.activeTramModel);
        ownedTramModels.setValue(state.ownedTramModels);

        train1CapacityLevel.setValue(state.train1CapacityLevel);
        train1SpeedLevel.setValue(state.train1SpeedLevel);
        activeTrain1Model.setValue(state.activeTrain1Model);
        ownedTrain1Models.setValue(state.ownedTrain1Models);

        train2Owned.setValue(state.train2Owned);
        train2CapacityLevel.setValue(state.train2CapacityLevel);
        train2SpeedLevel.setValue(state.train2SpeedLevel);
        activeTrain2Model.setValue(state.activeTrain2Model);
        ownedTrain2Models.setValue(state.ownedTrain2Models);

        train3Owned.setValue(state.train3Owned);
        train3CapacityLevel.setValue(state.train3CapacityLevel);
        train3SpeedLevel.setValue(state.train3SpeedLevel);
        activeTrain3Model.setValue(state.activeTrain3Model);
        ownedTrain3Models.setValue(state.ownedTrain3Models);

        nextTrainCost.setValue(state.nextTrainCost);
        ownedTrainsCount.setValue(state.ownedTrainsCount);

        if (state.train2Owned) {
            train2Owned.setValue(true);
            train2Status.setValue("Linia Puszczykowo czeka");
            handler.postDelayed(this::startTrain2Ride, 2000);
        }
        if (state.train3Owned) {
            train3Owned.setValue(true);
            train3Status.setValue("Linia Opalenica czeka");
            handler.postDelayed(this::startTrain3Ride, 2000);
        }

        totalPassengers.setValue(state.totalPassengers);
        totalAdsWatched.setValue(state.totalAdsWatched);
        totalTrainsBought.setValue(state.totalTrainsBought);
        totalUpgradesBought.setValue(state.totalUpgradesBought);
    }

    public void saveState() {
        if (db == null) return;
        GameStateEntity state = new GameStateEntity();

        state.avenidaStudents = avenidaStudents.getValue() != null ? avenidaStudents.getValue() : 0L;
        state.schoolStudents = schoolStudents.getValue() != null ? schoolStudents.getValue() : 0L;
        state.teachers = teachers.getValue() != null ? teachers.getValue() : 0L;

        state.tramCapacityLevel = tramCapacityLevel.getValue() != null ? tramCapacityLevel.getValue() : 1;
        state.tramSpeedLevel = tramSpeedLevel.getValue() != null ? tramSpeedLevel.getValue() : 1;
        state.activeTramModel = activeTramModel.getValue();
        state.ownedTramModels = ownedTramModels.getValue();

        state.train1CapacityLevel = train1CapacityLevel.getValue() != null ? train1CapacityLevel.getValue() : 1;
        state.train1SpeedLevel = train1SpeedLevel.getValue() != null ? train1SpeedLevel.getValue() : 1;
        state.activeTrain1Model = activeTrain1Model.getValue();
        state.ownedTrain1Models = ownedTrain1Models.getValue();

        state.train2Owned = train2Owned.getValue() != null && train2Owned.getValue();
        state.train2CapacityLevel = train2CapacityLevel.getValue() != null ? train2CapacityLevel.getValue() : 1;
        state.train2SpeedLevel = train2SpeedLevel.getValue() != null ? train2SpeedLevel.getValue() : 1;
        state.activeTrain2Model = activeTrain2Model.getValue();
        state.ownedTrain2Models = ownedTrain2Models.getValue();

        state.train3Owned = train3Owned.getValue() != null && train3Owned.getValue();
        state.train3CapacityLevel = train3CapacityLevel.getValue() != null ? train3CapacityLevel.getValue() : 1;
        state.train3SpeedLevel = train3SpeedLevel.getValue() != null ? train3SpeedLevel.getValue() : 1;
        state.activeTrain3Model = activeTrain3Model.getValue();
        state.ownedTrain3Models = ownedTrain3Models.getValue();

        state.nextTrainCost = nextTrainCost.getValue() != null ? nextTrainCost.getValue() : 50L;
        state.ownedTrainsCount = ownedTrainsCount.getValue() != null ? ownedTrainsCount.getValue() : 1;

        state.totalPassengers = totalPassengers.getValue() != null ? totalPassengers.getValue() : 0L;
        state.totalAdsWatched = totalAdsWatched.getValue() != null ? totalAdsWatched.getValue() : 0L;
        state.totalTrainsBought = totalTrainsBought.getValue() != null ? totalTrainsBought.getValue() : 0L;
        state.totalUpgradesBought = totalUpgradesBought.getValue() != null ? totalUpgradesBought.getValue() : 0L;

        state.lastUpdateTime = System.currentTimeMillis();

        db.gameStateDao().insert(state);
    }

    private GameView() {
        handler.postDelayed(this::startTrain1Ride, 2000);
        handler.postDelayed(this::startTramRide, 2000);
        refreshWeather();
    }

    private void refreshWeather() {
        WeatherService.fetchWeatherMultiplier((multiplier, description, emoji) -> {
            weatherMultiplier.setValue(multiplier);
            weatherDescription.setValue(description);
            weatherEmoji.setValue(emoji);
        });
        handler.postDelayed(this::refreshWeather, 10 * 60 * 1000);
    }

    public int getTramDrawableRes(String modelId) {
        if (modelId == null) return R.drawable.ic_tram_standard;
        switch (modelId) {
            case "10": return R.drawable.ic_tram_10;
            case "17": return R.drawable.ic_tram_17;
            case "5":  return R.drawable.ic_tram_5;
            default:   return R.drawable.ic_tram_standard;
        }
    }

    public int getTrainDrawableRes(String modelId) {
        if (modelId == null) return R.drawable.ic_train_standard;
        switch (modelId) {
            case "KW":        return R.drawable.ic_train_kw;
            case "Polregio":  return R.drawable.ic_train_polregio;
            case "Intercity": return R.drawable.ic_train_intercity;
            default:          return R.drawable.ic_train_standard;
        }
    }

    public int getTramCapacity() {
        Integer lvl = tramCapacityLevel.getValue();
        return 20 + (lvl != null ? (lvl - 1) * 10 : 0);
    }

    public int getTramStepMs() {
        Integer lvl = tramSpeedLevel.getValue();
        return Math.max(80, 400 - (lvl != null ? (lvl - 1) * 40 : 0));
    }

    public int getTrain1Passengers() {
        Integer lvl = train1CapacityLevel.getValue();
        return 4 + (lvl != null ? (lvl - 1) * 2 : 0);
    }

    public int getTrain1StepMs() {
        Integer lvl = train1SpeedLevel.getValue();
        return Math.max(100, 500 - (lvl != null ? (lvl - 1) * 50 : 0));
    }

    public int getTrain2Passengers() {
        Integer lvl = train2CapacityLevel.getValue();
        return 7 + (lvl != null ? (lvl - 1) * 3 : 0);
    }

    public int getTrain2StepMs() {
        Integer lvl = train2SpeedLevel.getValue();
        return Math.max(140, 700 - (lvl != null ? (lvl - 1) * 70 : 0));
    }

    public int getTrain3Passengers() {
        Integer lvl = train3CapacityLevel.getValue();
        return 11 + (lvl != null ? (lvl - 1) * 4 : 0);
    }

    public int getTrain3StepMs() {
        Integer lvl = train3SpeedLevel.getValue();
        return Math.max(200, 1000 - (lvl != null ? (lvl - 1) * 100 : 0));
    }

    public long getUpgradeCost(int currentLevel) {
        return UPGRADE_BASE_COST * currentLevel * 2;
    }

    private VehicleModel getModelById(String vehicle, String modelId) {
        List<VehicleModel> list = vehicle.equals("tram") ? TRAM_MODELS : TRAIN_MODELS;
        for (VehicleModel m : list) if (m.id.equals(modelId)) return m;
        return null;
    }

    private Set<String> getOwnedSet(String vehicle) {
        switch (vehicle) {
            case "tram":   return ownedTramModels.getValue();
            case "train1": return ownedTrain1Models.getValue();
            case "train2": return ownedTrain2Models.getValue();
            case "train3": return ownedTrain3Models.getValue();
        }
        return null;
    }

    public int getEffectiveTramCapacity() {
        int base = getTramCapacity();
        VehicleModel model = getModelById("tram", activeTramModel.getValue());
        if (model != null) base = (int)(base * model.capacityMul);
        return base;
    }

    public int getEffectiveTrain1Passengers() {
        int base = getTrain1Passengers();
        VehicleModel model = getModelById("train1", activeTrain1Model.getValue());
        if (model != null) base = (int)(base * model.capacityMul);
        return base;
    }

    public int getEffectiveTrain2Passengers() {
        int base = getTrain2Passengers();
        VehicleModel model = getModelById("train2", activeTrain2Model.getValue());
        if (model != null) base = (int)(base * model.capacityMul);
        return base;
    }

    public int getEffectiveTrain3Passengers() {
        int base = getTrain3Passengers();
        VehicleModel model = getModelById("train3", activeTrain3Model.getValue());
        if (model != null) base = (int)(base * model.capacityMul);
        return base;
    }

    public int getEffectiveTramStepMs() {
        int step = getTramStepMs();
        VehicleModel model = getModelById("tram", activeTramModel.getValue());
        if (model != null) step = (int)(step / model.speedMul);
        Float wm = weatherMultiplier.getValue();
        return Math.max(80, (int)(step * (wm != null ? wm : 1.0f)));
    }

    public int getEffectiveTrain1StepMs() {
        int step = getTrain1StepMs();
        VehicleModel model = getModelById("train1", activeTrain1Model.getValue());
        if (model != null) step = (int)(step / model.speedMul);
        Float wm = weatherMultiplier.getValue();
        return Math.max(100, (int)(step * (wm != null ? wm : 1.0f)));
    }

    public int getEffectiveTrain2StepMs() {
        int step = getTrain2StepMs();
        VehicleModel model = getModelById("train2", activeTrain2Model.getValue());
        if (model != null) step = (int)(step / model.speedMul);
        Float wm = weatherMultiplier.getValue();
        return Math.max(140, (int)(step * (wm != null ? wm : 1.0f)));
    }

    public int getEffectiveTrain3StepMs() {
        int step = getTrain3StepMs();
        VehicleModel model = getModelById("train3", activeTrain3Model.getValue());
        if (model != null) step = (int)(step / model.speedMul);
        Float wm = weatherMultiplier.getValue();
        return Math.max(200, (int)(step * (wm != null ? wm : 1.0f)));
    }

    public boolean upgradeTramCapacity()   { return doUpgrade(tramCapacityLevel); }
    public boolean upgradeTramSpeed()      { return doUpgrade(tramSpeedLevel); }
    public boolean upgradeTrain1Capacity() { return doUpgrade(train1CapacityLevel); }
    public boolean upgradeTrain1Speed()    { return doUpgrade(train1SpeedLevel); }
    public boolean upgradeTrain2Capacity() { return doUpgrade(train2CapacityLevel); }
    public boolean upgradeTrain2Speed()    { return doUpgrade(train2SpeedLevel); }
    public boolean upgradeTrain3Capacity() { return doUpgrade(train3CapacityLevel); }
    public boolean upgradeTrain3Speed()    { return doUpgrade(train3SpeedLevel); }

    private boolean doUpgrade(MutableLiveData<Integer> levelData) {
        Integer lvl = levelData.getValue();
        Long school = schoolStudents.getValue();
        if (lvl == null || school == null || lvl >= MAX_UPGRADE_LEVEL) return false;
        long cost = getUpgradeCost(lvl);
        if (school < cost) return false;
        schoolStudents.setValue(school - cost);
        levelData.setValue(lvl + 1);

        Long currentUpgrades = totalUpgradesBought.getValue();
        if (currentUpgrades != null) totalUpgradesBought.setValue(currentUpgrades + 1);
        requestSave();
        return true;
    }

    public boolean buyModel(String vehicle, String modelId) {
        VehicleModel model = getModelById(vehicle, modelId);
        if (model == null) return false;
        Long t = teachers.getValue();
        if (t == null || t < model.costTeachers) return false;
        Set<String> owned = getOwnedSet(vehicle);
        if (owned == null || owned.contains(modelId)) return false;
        teachers.setValue(t - model.costTeachers);
        owned.add(modelId);
        switch (vehicle) {
            case "tram":   ownedTramModels.setValue(new HashSet<>(owned));   break;
            case "train1": ownedTrain1Models.setValue(new HashSet<>(owned)); break;
            case "train2": ownedTrain2Models.setValue(new HashSet<>(owned)); break;
            case "train3": ownedTrain3Models.setValue(new HashSet<>(owned)); break;
        }
        requestSave();
        return true;
    }

    public void setActiveModel(String vehicle, String modelId) {
        Set<String> owned = getOwnedSet(vehicle);
        if (owned == null || !owned.contains(modelId)) return;
        switch (vehicle) {
            case "tram":   activeTramModel.setValue(modelId);   break;
            case "train1": activeTrain1Model.setValue(modelId); break;
            case "train2": activeTrain2Model.setValue(modelId); break;
            case "train3": activeTrain3Model.setValue(modelId); break;
        }
    }

    private void rollTeacher() {
        if (Math.random() < 0.1) {
            Long t = teachers.getValue();
            if (t != null) teachers.setValue(t + 1);
        }
    }

    public void buyNewTrain() {
        Long school = schoolStudents.getValue();
        Long cost = nextTrainCost.getValue();
        Integer owned = ownedTrainsCount.getValue();
        if (school == null || cost == null || owned == null || school < cost || owned >= 3) return;
        schoolStudents.setValue(school - cost);
        ownedTrainsCount.setValue(owned + 1);
        if (owned == 1) {
            train2Owned.setValue(true);
            train2Status.setValue("Linia Puszczykowo czeka");
            handler.postDelayed(this::startTrain2Ride, 2000);
        } else if (owned == 2) {
            train3Owned.setValue(true);
            train3Status.setValue("Linia Opalenica czeka");
            handler.postDelayed(this::startTrain3Ride, 2000);
        }
        nextTrainCost.setValue((long)(cost * 2.5));

        Long bought = totalTrainsBought.getValue();
        if (bought != null) totalTrainsBought.setValue(bought + 1);
        requestSave();
    }

    private void startTramRide() {
        if (Boolean.TRUE.equals(tramActive.getValue())) return;
        Long onPlatform = avenidaStudents.getValue();
        if (onPlatform == null || onPlatform == 0) {
            handler.postDelayed(this::startTramRide, 2000);
            return;
        }
        int capacity = getEffectiveTramCapacity();
        long willTake = Math.min(onPlatform, capacity);
        tramPassengers = willTake;
        avenidaStudents.setValue(onPlatform - willTake);
        tramActive.setValue(true);
        tramOutward = true;
        tramStatus.setValue("Tramwaj jedzie...");
        tramProgress.setValue(0);
        final int stepMs = getEffectiveTramStepMs();
        handler.postDelayed(new Runnable() {
            int progress = 0;
            @Override public void run() {
                if (tramOutward) {
                    progress += 10;
                    if (progress > 100) progress = 100;
                    tramProgress.setValue(progress);
                    if (progress >= 100) {
                        Long inSchool = schoolStudents.getValue();
                        if (inSchool != null) schoolStudents.setValue(inSchool + tramPassengers);
                        long pass = tramPassengers;
                        if (pass > 0) {
                            Long total = totalPassengers.getValue();
                            if (total != null) totalPassengers.setValue(total + pass);
                        }
                        tramPassengers = 0;
                        tramOutward = false;
                        tramStatus.setValue("Tramwaj wraca...");
                        handler.postDelayed(this, stepMs);
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                } else {
                    progress -= 10;
                    if (progress < 0) progress = 0;
                    tramProgress.setValue(progress);
                    if (progress <= 0) {
                        finishTramRide();
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                }
            }
        }, stepMs);
    }

    private void finishTramRide() {
        tramActive.setValue(false);
        tramStatus.setValue("Tramwaj czeka");
        tramProgress.setValue(0);
        rollTeacher();
        Long val = totalPassengers.getValue();
        if (val != null) totalPassengers.setValue(val + tramPassengers);
        handler.postDelayed(this::startTramRide, 2000);
    }

    private void startTrain1Ride() {
        if (Boolean.TRUE.equals(train1Active.getValue())) return;
        train1Active.setValue(true);
        train1Outward = true;
        int passengers = getEffectiveTrain1Passengers();
        int stepMs = getEffectiveTrain1StepMs();
        train1Status.setValue("Linia Pobiedziska jedzie...");
        train1Progress.setValue(0);
        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override public void run() {
                if (train1Outward) {
                    prog += 10;
                    if (prog > 100) prog = 100;
                    train1Progress.setValue(prog);
                    if (prog >= 100) {
                        train1Outward = false;
                        train1Status.setValue("Linia Pobiedziska wraca...");
                        handler.postDelayed(this, stepMs);
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                } else {
                    prog -= 10;
                    if (prog < 0) prog = 0;
                    train1Progress.setValue(prog);
                    if (prog <= 0) {
                        Long cur = avenidaStudents.getValue();
                        if (cur != null) avenidaStudents.setValue(cur + passengers);
                        finishTrain1Ride();
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                }
            }
        }, stepMs);
    }

    private void finishTrain1Ride() {
        train1Active.setValue(false);
        train1Status.setValue("Linia Pobiedziska czeka");
        train1Progress.setValue(0);
        rollTeacher();
        handler.postDelayed(this::startTrain1Ride, 2000);
    }

    private void startTrain2Ride() {
        if (Boolean.TRUE.equals(train2Active.getValue())) return;
        train2Active.setValue(true);
        train2Outward = true;
        int passengers = getEffectiveTrain2Passengers();
        int stepMs = getEffectiveTrain2StepMs();
        train2Status.setValue("Linia Puszczykowo jedzie...");
        train2Progress.setValue(0);
        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override public void run() {
                if (train2Outward) {
                    prog += 10;
                    if (prog > 100) prog = 100;
                    train2Progress.setValue(prog);
                    if (prog >= 100) {
                        train2Outward = false;
                        train2Status.setValue("Linia Puszczykowo wraca...");
                        handler.postDelayed(this, stepMs);
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                } else {
                    prog -= 10;
                    if (prog < 0) prog = 0;
                    train2Progress.setValue(prog);
                    if (prog <= 0) {
                        Long cur = avenidaStudents.getValue();
                        if (cur != null) avenidaStudents.setValue(cur + passengers);
                        finishTrain2Ride();
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                }
            }
        }, stepMs);
    }

    private void finishTrain2Ride() {
        train2Active.setValue(false);
        train2Status.setValue("Linia Puszczykowo czeka");
        train2Progress.setValue(0);
        rollTeacher();
        handler.postDelayed(this::startTrain2Ride, 3000);
    }


    private void startTrain3Ride() {
        if (Boolean.TRUE.equals(train3Active.getValue())) return;
        train3Active.setValue(true);
        train3Outward = true;
        int passengers = getEffectiveTrain3Passengers();
        int stepMs = getEffectiveTrain3StepMs();
        train3Status.setValue("Linia Opalenica jedzie...");
        train3Progress.setValue(0);
        handler.postDelayed(new Runnable() {
            int prog = 0;
            @Override public void run() {
                if (train3Outward) {
                    prog += 10;
                    if (prog > 100) prog = 100;
                    train3Progress.setValue(prog);
                    if (prog >= 100) {
                        train3Outward = false;
                        train3Status.setValue("Linia Opalenica wraca...");
                        handler.postDelayed(this, stepMs);
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                } else {
                    prog -= 10;
                    if (prog < 0) prog = 0;
                    train3Progress.setValue(prog);
                    if (prog <= 0) {
                        Long cur = avenidaStudents.getValue();
                        if (cur != null) avenidaStudents.setValue(cur + passengers);
                        finishTrain3Ride();
                    } else {
                        handler.postDelayed(this, stepMs);
                    }
                }
            }
        }, stepMs);
    }

    private void finishTrain3Ride() {
        train3Active.setValue(false);
        train3Status.setValue("Linia Opalenica czeka");
        train3Progress.setValue(0);
        rollTeacher();
        handler.postDelayed(this::startTrain3Ride, 4000);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        handler.removeCallbacksAndMessages(null);
    }

    private Handler saveHandler = new Handler(Looper.getMainLooper());
    private boolean saveScheduled = false;

    public void requestSave() {
        if (!saveScheduled) {
            saveScheduled = true;
            saveHandler.postDelayed(() -> {
                saveState();
                saveScheduled = false;
            }, 5000);
        }
    }
}