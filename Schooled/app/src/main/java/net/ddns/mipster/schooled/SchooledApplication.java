package net.ddns.mipster.schooled;

import android.app.Application;


/**
 * Created by Chen on 25/02/2017.
 */

public class SchooledApplication extends Application {

    public static final String ANNOUNCEMENT_DATA = "net.ddns.mipster.schooled.announcementData";
    public static final String SCHEDULE_DATA = "net.ddns.mipster.schooled.scheduleData";

    public SchooledApplication(){}

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
