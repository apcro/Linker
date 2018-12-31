package com.alienpants.linker.data;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Android
 * Created by cro on 30/12/2018.
 */

@Entity
public class LevelData {

    @Id
    long id;

    int size;
    int num;
    String layout;
    Boolean locked;
    int score;
    int bestScore;

    public LevelData() {
    }

    public LevelData(int size, int num) {
        this.size = size;
        this.num = num;
        this.locked = true;
        this.score = 0;
        this.bestScore = 0;
    }

    public int getNum() {
        return num;
    }

    public int getSize() {
        return size;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public void unlock() {
        this.locked = false;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }
}
