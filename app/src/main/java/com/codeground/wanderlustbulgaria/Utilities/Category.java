package com.codeground.wanderlustbulgaria.Utilities;

public class Category {

    private String mName;
    private int mCount;
    private int mIcon;
    private String mIntentId;

    public Category(String mName, int mCount, int mIcon) {
        this.mName = mName;
        this.mCount = mCount;
        this.mIcon = mIcon;
        this.mIntentId = mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public String getIntentId() {
        return mIntentId;
    }
}
