package com.Aperture.TSPL;

public class BARCODE {

    // BARCODE X,Y, "code type",height,human readable,rotation,narrow,wide,[alignment,] "content "

    private Integer mX, mY;
    private String mCodeType;
    private Integer mHeight;
    private Integer mHumanReadable;
    private Integer mRoation;
    private Integer mNarrow;
    private Integer mWide;
    private Integer mAlignment = 0;
    private String mContent;

    public BARCODE(Integer x, Integer y, String codeType, Integer height, Integer humanReadable, Integer roation, Integer narrow, Integer wide, String content) {
        mX = x;
        mY = y;
        mCodeType = codeType;
        mHeight = height;
        mHumanReadable = humanReadable;
        mRoation = roation;
        mNarrow = narrow;
        mWide = wide;
        mContent = content;
    }

    public BARCODE(Integer x, Integer y, String codeType, Integer height, Integer humanReadable, Integer roation, Integer narrow, Integer wide, Integer alignment, String content) {
        mX = x;
        mY = y;
        mCodeType = codeType;
        mHeight = height;
        mHumanReadable = humanReadable;
        mRoation = roation;
        mNarrow = narrow;
        mWide = wide;
        mAlignment = alignment;
        mContent = content;
    }

    public String getCOMMAND() {
        return String
                .format("BARCODE %d,%d,\"%s\",%d,%d,%d,%d,%d,\"%s\"", mX, mY, mCodeType, mHeight, mHumanReadable, mRoation, mNarrow, mWide, mContent);
    }

}
