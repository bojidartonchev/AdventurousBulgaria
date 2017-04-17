package com.codeground.wanderlustbulgaria.Utilities;

import com.codeground.wanderlustbulgaria.Utilities.Adapters.CategoriesAdapter;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.LocalParseLocation;

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
    private CategoriesAdapter mAdapter;

    public Category(String name, int icon, Type type, CategoriesAdapter adapter) {
        this.mName = name;
        this.mIcon = icon;
        this.mType = type;
        this.mAdapter = adapter;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getCount() {
        return LocalParseLocation.getCategoryCount(mName);
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
