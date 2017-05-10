package com.codeground.wanderlustbulgaria.Utilities;

public class Category{

    public enum Type {
        NEARBY(0),
        SPECIFIED(1),
        FROM_USERS(2);

        Type (int i)
        {
            this.type = i;
        }

        private int type;

        public int getNumericType()
        {
            return type;
        }
    }

    private String mName;
    private int mIcon;
    private Type mType;

    public Category(String name, int icon, Type type) {
        this.mName = name;
        this.mIcon = icon;
        this.mType = type;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public Type getType() {
        return mType;
    }
}
