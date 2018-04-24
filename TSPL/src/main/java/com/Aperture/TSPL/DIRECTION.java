package com.Aperture.TSPL;

public class DIRECTION {


    private Integer mN;


    private Integer mM = 0;


    /**
     * @param N Direct of things
     *          <p>0: dowm</p>
     *          <p>1: up</p>
     */
    public DIRECTION(Integer N) {
        this.mN = N;
    }

    /**
     * @param N Direct of label
     *          <p>0: down</p>
     *          <p>1: up</p>
     * @param M Mirror label
     *          <p>0: normal</p>
     *          <p>1: mirror</p>
     */
    public DIRECTION(Integer N, Integer M) {
        this.mN = N;
        this.mM = M;
    }

    public String getCOMMAND() {
        return "DIRECTION " + mN + ',' + mM;
    }


}
