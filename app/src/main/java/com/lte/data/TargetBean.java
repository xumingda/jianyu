package com.lte.data;

/**
 * @createAuthor zfb
 * @createTime 2017/3/22${Time}
 * @describe ${TODO}
 */

public class TargetBean {

    /**
     * imsi : 460000560736203
     * delay : 11
     * sinr : 12
     * freq : 39098
     * bbu : 1
     * rsrp : 12
     */

    private String imsi;
    private String delay;
    private String freq;
    private String BBU;
    private float rsrp;
    private float sinr;
    private String pci;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private String time;
    private int    count;

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getBbu() {
        return BBU;
    }

    public void setBbu(String BBU) {
        this.BBU = BBU;
    }

    public Float getRsrp() {
        return rsrp;
    }

    public void setSinr(float sinr) {
        this.sinr = sinr;
    }

    public Float getSinr() {
        return sinr;
    }

    public void setRsrp(float rsrp) {
        this.rsrp = rsrp;
    }

    public String getPci() {
        return pci;
    }

    public void setPci(String pci) {
        this.pci = pci;
    }

    @Override
    public String toString() {
        return "TargetBean{" +
                "imsi='" + imsi + '\'' +
                ", delay='" + delay + '\'' +
                ", freq='" + freq + '\'' +
                ", BBU='" + BBU + '\'' +
                ", rsrp=" + rsrp +
                ", sinr=" + sinr +
                ", pci='" + pci + '\'' +
                ", time='" + time + '\'' +
                ", count=" + count +
                '}';
    }
}
