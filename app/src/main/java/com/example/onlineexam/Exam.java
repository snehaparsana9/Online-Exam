package com.example.onlineexam;

public class Exam {
    private int id;
    private String name;
    private int duration; // in minutes

    public Exam() {}

    public Exam(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    @Override
    public String toString() {
        return name + " (" + duration + " min)";
    }
}
