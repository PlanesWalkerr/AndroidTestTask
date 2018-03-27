package com.makhovyk.android.tripservice.Model;

import android.content.Context;

/**
 * Created by misha on 3/26/18.
 */

public class HelperFactory {

    public static Helper geHelper(Context context, String option){
        if(option.equals("sqlite")){
            return new DBHelper(context);
        }else if(option.equals("realm")){
            return new RealmDBHelper(context);
        }
        return null;
    }
}
