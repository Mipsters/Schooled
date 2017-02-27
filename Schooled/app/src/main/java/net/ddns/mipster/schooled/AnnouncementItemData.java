package net.ddns.mipster.schooled;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Chen on 27/02/2017.
 */

public class AnnouncementItemData implements Serializable {
    private String title, text, date, url;

    public AnnouncementItemData(String title, String date) {
        this.title = title;
        this.date = date;
        text = "";
        url = "";
    }

    public AnnouncementItemData(String title, String date, String text) {
        this.title = title;
        this.date = date;
        this.text = text;
        url = "";
    }

    public AnnouncementItemData(String title, String date, String text, String url) {
        this.title = title;
        this.date = date;
        this.text = text;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}