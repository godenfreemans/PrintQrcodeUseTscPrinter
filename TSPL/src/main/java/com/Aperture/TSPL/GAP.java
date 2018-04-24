package com.Aperture.TSPL;

public class GAP {
    private final double mGap;
    private final double mOffset;
    private TSCSystem mTscSystem = TSCSystem.English;

    /**
     * Defines the gap distance between two labels.
     *
     * @param mGap      Label GAP
     * @param mOffset   Gap offset
     * @param tscSystem Length unit. See {@link TSCSystem}.
     */
    public GAP(double mGap, double mOffset, TSCSystem tscSystem) {
        this.mGap = mGap;
        this.mOffset = mOffset;
        this.mTscSystem = tscSystem;
    }

    /**
     * Defines the gap distance between two labels.
     *
     * @param mGap    Label GAP (inch)
     * @param mOffset Gap offset (inch)
     */
    public GAP(double mGap, double mOffset) {
        this.mGap = mGap;
        this.mOffset = mOffset;
    }

    /**
     * Defines the gap distance between two labels.
     *
     * @param mGap Label GAP (inch)
     */
    public GAP(double mGap) {
        this.mGap = mGap;
        this.mOffset = 0;
    }

    /**
     * @return TSPL command.
     */
    public String getCOMMAND() {
        switch (mTscSystem) {
            case English:
                return "SIZE " + String.valueOf(mGap) + ',' + String.valueOf(mOffset);
            case Metric:
                return "SIZE " + String.valueOf(mGap) + " mm" + ',' + String.valueOf(mOffset) + " mm";
            default:
                return null;
        }
    }
}
