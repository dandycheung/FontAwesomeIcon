package com.sumit.fontawesomeicon.model.fa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Membership {
    @SerializedName("free")
    @Expose
    private List<String> free = null;

    @SerializedName("pro")
    @Expose
    private List<String> pro = null;

    public List<String> getFree() {
        return free;
    }

    public void setFree(List<String> free) {
        this.free = free;
    }

    public List<String> getPro() {
        return pro;
    }

    public void setPro(List<String> pro) {
        this.pro = pro;
    }
}
