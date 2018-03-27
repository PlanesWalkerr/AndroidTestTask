package com.makhovyk.android.tripservice.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by misha on 3/27/18.
 */

public class FileLogger {

    private static final String PATH = "sdcard/Tripslog.dat";


    public static int logInFile(String tag, String msg, Context context) {

        File file = new File(PATH);
        boolean res = false;
        int result = 0;
        try {
            if (!file.exists()) {
                res = file.createNewFile();

            }
            String timeLog = new SimpleDateFormat("dd.MM.yy hh:mm:ss").format(new Date());
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append(timeLog).append(" (").append(tag).append(")\t").append(msg).append("\n");
            bw.close();
            result = 1;
        } catch (IOException e) {
            //Log.e("sss", String.valueOf(res));
            e.printStackTrace();
        }
        return result;
    }
}
