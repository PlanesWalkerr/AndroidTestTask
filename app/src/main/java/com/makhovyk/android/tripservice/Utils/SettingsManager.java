package com.makhovyk.android.tripservice.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by misha on 3/27/18.
 */

public class SettingsManager {
    public static final String REALM = "realm";
    public static final String SQLITE = "sqlite";
    private static final String PREFER_NAME = "DB";
    private static final String DBMS = "DBMS";

    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;

    public SettingsManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getDBMS() {
        return sharedPreferences.getString(DBMS, null);
    }

    public void setDBMS(String dbms) {
        editor.putString(DBMS, dbms);
        editor.commit();
    }

    public void setDefault() {
        editor.putString(DBMS, SQLITE);
        editor.commit();
    }

    public boolean isEmpty() {
        return getDBMS() == null;
    }
}
