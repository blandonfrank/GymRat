package com.jordanspell.gymrat.biz;

/**
 * Created by jorda_000 on 2/13/2015.
 */
public enum WorkoutSetTypeEnum {

    FiveXFive(1, "5x5", 5, 5),
    ThreeXEight(2, "3x8", 3, 8),
    ThreeXFive(3, "3x5", 3, 5),
    ThreeXThree(4, "3x3", 3, 3),
    OneXFive(5, "1x5", 1, 5),
    OneXThree(6, "1x3", 1, 3);

    private final int id;
    private final String name;
    private final int sets;
    private final int reps;

    WorkoutSetTypeEnum(int id, String name, int sets, int reps) {
        this.id = id;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
    }

    public static WorkoutSetTypeEnum getEnumByID(int id) {
        for(WorkoutSetTypeEnum e : values()) {
            if(e.id == (id)) return e;
        }
        return null;
    }

    public static WorkoutSetTypeEnum getEnumByName(String name) {
        for(WorkoutSetTypeEnum e : values()) {
            if(e.name.equals(name)) return e;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }
}
