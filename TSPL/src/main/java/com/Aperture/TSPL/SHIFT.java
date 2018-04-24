package com.Aperture.TSPL;

public class SHIFT {

    private double mX = 0;
    private double mY;

    /**
     * @param X Shift on x direction
     * @param Y Shift on x direction
     */
    public SHIFT(double X, double Y) {
        this.mX = X;
        this.mY = Y;
    }

    /**
     * @param Y Shift on x direction
     */
    public SHIFT(double Y) {
        this.mY = Y;
    }

    public String getCOMMAND() {
        return "SHIFT " + mX + ',' + mY;
    }
}
