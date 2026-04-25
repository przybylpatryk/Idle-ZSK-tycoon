package edu.zsk.zsktycoon;

public class VehicleModel {
    public String id;
    public String name;
    public String description;
    public float speedMul;
    public float capacityMul;
    public long costTeachers;

    public VehicleModel(String id, String name, String description, float speedMul, float capacityMul, long costTeachers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.speedMul = speedMul;
        this.capacityMul = capacityMul;
        this.costTeachers = costTeachers;
    }
}