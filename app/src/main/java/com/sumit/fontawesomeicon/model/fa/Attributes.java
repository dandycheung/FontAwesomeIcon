package com.sumit.fontawesomeicon.model.fa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Attributes {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("membership")
    @Expose
    private Membership membership;

    @SerializedName("styles")
    @Expose
    private List<String> styles = null;

    @SerializedName("unicode")
    @Expose
    private String unicode;

    @SerializedName("voted")
    @Expose
    private Boolean voted;

    private int iconColor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }
}
