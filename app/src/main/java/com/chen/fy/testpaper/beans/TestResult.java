package com.chen.fy.testpaper.beans;

/**
 * 试纸检测结果信息
 */
public class TestResult {

    private int Crooked;
    private int Brief;
    private int Stain;
    private int Burrs;
    private int Sign;
    private int Fold;
    private int ALL;

    public int getCrooked() {
        return Crooked;
    }

    public void setCrooked(int crooked) {
        Crooked = crooked;
    }

    public int getBrief() {
        return Brief;
    }

    public void setBrief(int brief) {
        Brief = brief;
    }

    public int getStain() {
        return Stain;
    }

    public void setStain(int stain) {
        Stain = stain;
    }

    public int getBurrs() {
        return Burrs;
    }

    public void setBurrs(int burrs) {
        Burrs = burrs;
    }

    public int getSign() {
        return Sign;
    }

    public void setSign(int sign) {
        Sign = sign;
    }

    public int getFold() {
        return Fold;
    }

    public void setFold(int fold) {
        Fold = fold;
    }

    public int getALL() {
        return ALL;
    }

    public void setALL(int ALL) {
        this.ALL = ALL;
    }
}
