package net.ddns.mipster.schooled;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

//startActivity(new Intent(LoadingActivity.this, MainActivity.class));

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new GeneralDataTask().execute();
    }

    class GeneralDataTask extends AsyncTask<Void,Integer,Void>{
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            finish();
        }
    }
}
