package net.ddns.mipster.schooled;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new GeneralDataTask().execute();
    }

    class GeneralDataTask extends AsyncTask<Void,String,Void>{
        private ProgressBar progressBar;
        private ArrayList<AnnouncementItemData> scheduleData;

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            scheduleData = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document doc;
            try {
                doc = Jsoup.connect("http://www.handasaim.co.il/").get();
            } catch (java.io.IOException e) {
                cancel(true);
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

                    scheduleData.add(new AnnouncementItemData(title,date,text,url));
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Snackbar.make(findViewById(R.id.activity_loading), "Internet connection failed",Snackbar.LENGTH_INDEFINITE)
                    .setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GeneralDataTask().execute();
                        }
                    }).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent mainActivity = new Intent(LoadingActivity.this, MainActivity.class);

            mainActivity.putExtra(SchooledApplication.ANNOUNCEMENT_DATA, scheduleData);

            startActivity(mainActivity);
            finish();
        }
    }
}