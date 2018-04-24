package com.Aperture.TSPL;

public class TEXT {

    //TEXT x,y, " font ",rotation,x-multiplication,y-multiplication,[alignment,] " content "
    private Integer mX;
    private Integer mY;
    private String mFont;
    private Integer mRotation;
    private Integer mXMultiplication, mYMultiplication;
    private Integer mAlignment = null;
    private String mContent;


    public TEXT(int x, int y, String font, int rotation, int x_multiplication, int y_multiplication, String content) {
        mX = x;
        mY = y;
        mFont = font;
        this.mRotation = rotation;
        this.mXMultiplication = x_multiplication;
        this.mYMultiplication = y_multiplication;
        this.mContent = content;
    }

    public TEXT(int x, int y, String font, int rotation, int x_multiplication, int y_multiplication, int alignment, String content) {
        mX = x;
        mY = y;
        mFont = font;
        this.mRotation = rotation;
        this.mXMultiplication = x_multiplication;
        this.mYMultiplication = y_multiplication;
        this.mAlignment = alignment;
        this.mContent = content;
    }

    public String getCOMMAND() {
        if (mAlignment == null) {
            return "TEXT " +
                    mX +
                    mY +
                    " \"" + mFont + "\"" +
                    mRotation +
                    mXMultiplication +
                    mYMultiplication +
                    "\"" + mContent + "\"";
        }
        return "TEXT " +
                mX + ',' +
                mY + ',' +
                " \"" + mFont + "\"" + ',' +
                mRotation + ',' +
                mXMultiplication + ',' +
                mYMultiplication + ',' +
                mAlignment + ',' +
                "\"" + mContent + "\"";
    }
}
