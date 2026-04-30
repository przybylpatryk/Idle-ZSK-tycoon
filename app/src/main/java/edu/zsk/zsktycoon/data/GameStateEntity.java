package edu.zsk.zsktycoon.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "game_state")
public class GameStateEntity {
    @PrimaryKey
    public int id = 1;
    public long avenidaStudents = 0L;
    public long schoolStudents = 0L;
    public long teachers = 0L;
    public int tramCapacityLevel = 1;
    public int tramSpeedLevel = 1;
    public String activeTramModel = "Standardowy";
    @TypeConverters(Converters.class)
    public Set<String> ownedTramModels = new HashSet<>(Collections.singleton("Standardowy"));
    public int train1CapacityLevel = 1;
    public int train1SpeedLevel = 1;
    public String activeTrain1Model = "standardowy";
    @TypeConverters(Converters.class)
    public Set<String> ownedTrain1Models = new HashSet<>(Collections.singleton("standardowy"));
    public boolean train2Owned = false;
    public int train2CapacityLevel = 1;
    public int train2SpeedLevel = 1;
    public String activeTrain2Model = "standardowy";
    @TypeConverters(Converters.class)
    public Set<String> ownedTrain2Models = new HashSet<>(Collections.singleton("standardowy"));
    public boolean train3Owned = false;
    public int train3CapacityLevel = 1;
    public int train3SpeedLevel = 1;
    public String activeTrain3Model = "standardowy";
    @TypeConverters(Converters.class)
    public Set<String> ownedTrain3Models = new HashSet<>(Collections.singleton("standardowy"));
    public long nextTrainCost = 50L;
    public int ownedTrainsCount = 1;
    public long lastUpdateTime = System.currentTimeMillis();
    public long totalPassengers = 0L;
    public long totalAdsWatched = 0L;
    public long totalTrainsBought = 0L;
    public long totalUpgradesBought = 0L;
}