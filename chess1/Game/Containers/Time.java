package org.example.chess1.Game.Containers;

import javafx.animation.AnimationTimer;
import org.example.chess1.Game.Containers.Enum.GameTypes;

import java.io.Serializable;

public class Time implements Serializable {
    private long currentTime;
    private final int finalTime, addTime;

    private transient AnimationTimer timer;

    private boolean running, stopped;

    public GameTypes type;

    public Time(int sec, int add, GameTypes type){
        this.type = type;
        addTime = add;
        finalTime = sec;
        running = false;
        stopped = false;
        currentTime = 0;
        session(sec);
    }
    public Time (Time time){
        currentTime = 0;
        addTime = time.addTime;
        finalTime = time.finalTime;
        type = time.type;
        running = false;
        stopped = false;
        session(finalTime);
    }

    private void session(int sec) {
        timer = new AnimationTimer() {
            private long startTime = -1;
            @Override
            public void handle(long l) {
                if (startTime == -1){
                    startTime = l;
                }

                if (running) {
                    currentTime = l - startTime;
                } else {
                    startTime = l - currentTime;
                }

                if (currentTime/1_000_000_000 >= sec && sec > 0){
                    Time.this.stop();
                }
            }
        };
        timer.start();
    }

    //Actions with Timer
    public void pause(){
        running = false;
        currentTime-=(addTime* 1_000_000_000L);
    }
    public void start(){ running = true; }
    public void stop(){
        running = false;
        stopped = true;
        timer.stop();
    }

    public boolean isStopped() { return stopped; }

    @Override public String toString(){
        if (finalTime == -1){
            return "";
        }
        int sec = (int) (finalTime - currentTime/1_000_000_000);
        int Min = sec/60;
        int Sec = sec%60;

        String str = "";
        if (Min < 10){
            str+=STR."0\{Min}";
        } else {
            str+=Min;
        }
        str+=":";
        if (Sec < 10){
            str+= STR."0\{Sec}";
        } else{
            str+=Sec;
        }
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Time time)) return false;

        if (finalTime != time.finalTime) return false;
        return addTime == time.addTime;
    }

    @Override
    public int hashCode() {
        int result = finalTime;
        result = 31 * result + addTime;
        return result;
    }
}
