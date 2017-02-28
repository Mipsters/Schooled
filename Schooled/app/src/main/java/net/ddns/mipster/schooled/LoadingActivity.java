package net.ddns.mipster.schooled;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {

    GeneralDataTask generalDataTask;
    com.wang.avi.AVLoadingIndicatorView progressBar;
    com.wang.avi.AVLoadingIndicatorView progressBarOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loadOn);
        progressBarOff = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loadOff);

        progressBar.show();
        progressBarOff.show();
        progressBarOff.setVisibility(View.GONE);

        if(generalDataTask == null) {
            generalDataTask = new GeneralDataTask();
            generalDataTask.execute();
        }
    }

    class GeneralDataTask extends AsyncTask<Void,String,Void>{
        private ArrayList<AnnouncementItemData> announcementData;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBarOff.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            announcementData = parceAnnouncementData();

            if(announcementData == null)
                cancel(true);

            return null;
        }

        @Override
        protected void onCancelled() {
            Snackbar.make(findViewById(R.id.activity_loading), "Internet connection error",Snackbar.LENGTH_INDEFINITE)
                    .setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GeneralDataTask().execute();
                        }
                    }).show();

            progressBar.setVisibility(View.GONE);
            progressBarOff.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent mainActivity = new Intent(LoadingActivity.this, MainActivity.class);

            mainActivity.putExtra(SchooledApplication.ANNOUNCEMENT_DATA, announcementData);

            startActivity(mainActivity);
            finish();
        }
    }

    public static ArrayList<AnnouncementItemData> parceAnnouncementData(){
        ArrayList<AnnouncementItemData> announcementData = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect("http://www.handasaim.co.il/").get();
        } catch (java.io.IOException e) {
            return null;
        }
        if(doc != null) {
            Elements newsHeadlines = doc.select("marquee > table > tbody > tr > td");
            String[] data = newsHeadlines.html().split(String.format("(?=%1$s)", "<sup>"));
            String[] newData = new String[data.length - 1];

            System.arraycopy(data,1,newData,0,data.length - 1);

            for(String str : newData){
                Document document = Jsoup.parse(str);

                String dataStr = document.select("sup").html();
                String date = dataStr.substring(1,dataStr.length() - 1);

                dataStr = document.select("b").html();
                String title = dataStr;

                String url = document.select("a").attr("href").replace(" ","");

                document = Jsoup.parse(document.toString()
                        .replace(document.select("sup").toString(),"")
                        .replace(document.select("b").toString(),"")
                        .replace(document.select("a").toString(),""));

                String text = document.select("body").html()
                        .replace("<br>","\n").replaceAll("(?m)^[ \t]*\r?\n", "");

                announcementData.add(new AnnouncementItemData(title,date,text,url));
            }
        }
        return announcementData;
    }
}