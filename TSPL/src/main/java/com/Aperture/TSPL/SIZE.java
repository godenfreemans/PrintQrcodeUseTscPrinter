package com.Aperture.TSPL;

public class SIZE {
    private double mWidth, mHeight;
    private TSCSystem mTscSystem = TSCSystem.English;

    /**
     * @param Width     Label width
     * @param Height    Label length
     * @param tscSystem Length unit. See {@link TSCSystem}.
     */
    public SIZE(double Width, double Height, TSCSystem tscSystem) {
        this.mWidth = Width;
        this.mHeight = Height;
        this.mTscSystem = tscSystem;
    }

    /**
     * This command defines the label width and length.
     *
     * @param Width  Label width (inch)
     * @param Height Label length (inch)
     */
    public SIZE(double Width, double Height) {
        this.mWidth = Width;
        this.mHeight = Height;
    }

    /**
     * @return TSPL command.
     */
    public String getCOMMAND() {
        switch (mTscSystem) {
            case English:
                return "SIZE " + String.valueOf(mWidth) + ',' + String.valueOf(mHeight);
            case Metric:
                return "SIZE " + String.valueOf(mWidth) + " mm" + ',' + String.valueOf(mHeight) + " mm";
            default:
                return null;
        }
    }
}
