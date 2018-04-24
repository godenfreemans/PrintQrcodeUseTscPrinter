package com.Aperture.TSPL;

public class QRCODE {

    private double mX;
    private double mY;
    private char mEccLevel;
    private double mCellWidth;
    private char mMode;
    private int mRotation;
    private String mModule;
    private String mMask;
    private String mData;


    public QRCODE(double x, double mY, char eccLevel, double cellWidth, char mode, int rotation, String module, String mask, String Data) {
        this.mX = x;
        this.mY = mY;
        this.mEccLevel = eccLevel;
        this.mCellWidth = cellWidth;
        this.mMode = mode;
        this.mRotation = rotation;
        this.mModule = module;
        this.mMask = mask;
        this.mData = Data;
    }

    public String getCOMMAND() {
        return "QRCODE " + mX + "," + mY + "," + mEccLevel + "," + mCellWidth + "," + mMode + "," + mRotation + "," + mModule + "," + mMask + ",\"" + mData + '\"';
    }

}
