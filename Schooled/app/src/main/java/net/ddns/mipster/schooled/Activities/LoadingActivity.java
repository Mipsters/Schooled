package net.ddns.mipster.schooled.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.ddns.mipster.schooled.SQLiteHelper;
import net.ddns.mipster.schooled.classes.AnnouncementItemData;
import net.ddns.mipster.schooled.classes.NoteData;
import net.ddns.mipster.schooled.classes.Tuple;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.SchooledApplication;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class LoadingActivity extends AppCompatActivity {

    private final static String WORK = "net.ddns.mipster.schooled.activities.BEGAN_WORK";

    com.wang.avi.AVLoadingIndicatorView progressBar;
    com.wang.avi.AVLoadingIndicatorView progressBarOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loadOn);
        progressBarOff = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loadOff);

        progressBarOff.setVisibility(View.GONE);

        if(!getIntent().getBooleanExtra(WORK, false)) {
            new GeneralDataTask().execute();
            getIntent().putExtra(WORK, true);
        }
    }

    class GeneralDataTask extends AsyncTask<Void,Object,Void>{
        private TextView stat;
        private String day;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBarOff.setVisibility(View.GONE);

            stat = (TextView) findViewById(R.id.stat);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress("Receiving Announcements");

            parseAnnouncementData();

            publishProgress("Downloading schedule Excel file");

            day = updateSchedule();

            publishProgress("Starting app");

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            stat.setText(values[0] + "...");
        }

        @Override
        protected void onCancelled() {
            stat.setText("");

            Snackbar.make(findViewById(R.id.activity_loading), "a problem occurred", Snackbar.LENGTH_INDEFINITE)
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
            /*
            Intent intent = new Intent(getApplicationContext(),AnnouncementWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), AnnouncementWidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);
            */
            Intent mainActivity = new Intent(LoadingActivity.this, MainActivity.class);

            mainActivity.putExtra(SchooledApplication.DAY_DATA, day);

            startActivity(mainActivity);
            finish();
        }

        private String updateSchedule(){
            Cursor c = SchooledApplication.SQL_DATA.getAllData(SQLiteHelper.Tables.ANNOUNCEMENT);
            while(c.moveToNext()) {
                AnnouncementItemData id = new AnnouncementItemData(c.getString(0),c.getString(2),c.getString(1),c.getString(3)) ;
                if (id.getUrl().contains("s3-eu-west-1.amazonaws.com/schooly/handasaim/news") &&
                        id.getTitle().contains("מערכת שעות") && (id.getUrl().contains(".xls") ||
                        id.getUrl().contains(".xlsx"))
                        /////////////////////////////////////////
                        //TODO: remove this part
                        // || true
                        /////////////////////////////////////////
                        ) {
                    boolean isX = id.getUrl().contains(".xlsx");
                    String date = id.getDate();

                    /*
                    String[] dateInfo = date.split("/");
                    GregorianCalendar dateGreg = new GregorianCalendar(2000 + Integer.parseInt(dateInfo[2]),
                            Integer.parseInt(dateInfo[1]) - 1, Integer.parseInt(dateInfo[0]));
                    dateGreg.add(Calendar.DAY_OF_MONTH, 1);
                    String finDate = new SimpleDateFormat("dd/MM/yy").format(dateGreg.getTime());

                    dateGreg.add(Calendar.DAY_OF_MONTH, 1);
                    String finDate2 = new SimpleDateFormat("dd/MM/yy").format(dateGreg.getTime());

                    String nowDate = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());

                    //nowDate.equals(date) || nowDate.equals(finDate) || id.getDate().equals(finDate2)

                    //getBaseContext().getFileStreamPath("schedule(" + date + ").xls").exists()
                    */


                    try {
                        if (!getApplicationContext().getFileStreamPath("schedule(" + date.replace("/", "-") + ")" + (isX ? ".xlsx" : ".xls")).exists()) {
                            Log.i("updateSchedule", "file did not exist");
                            downloadExcel(id, isX);
                        } else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17) {
                            Log.i("updateSchedule", "the time is after five pm");
                            downloadExcel(id, isX);
                        } else
                            Log.i("updateSchedule", "used existing file");

                        return goThroughExcel(id, isX);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            return null;
        }

        private void downloadExcel(AnnouncementItemData itemData, boolean isX) throws IOException {
            String date = itemData.getDate().replace("/","-");
            URL url = new URL(itemData.getUrl());
            URLConnection connection = url.openConnection();
            connection.connect();


            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = getApplicationContext().openFileOutput("schedule(" + date + ")" + (isX ? ".xlsx" : ".xls"), Context.MODE_PRIVATE);

            byte data[] = new byte[32];
            int count;

            while ((count = input.read(data)) != -1)
                output.write(data, 0, count);

            output.close();
            input.close();
        }

        private String goThroughExcel(AnnouncementItemData itemData, boolean isX) throws IOException {
            String date = itemData.getDate().replace("/","-");

            SchooledApplication.SQL_DATA.resetNote();

            //TODO: remember to roll this back
            ///////////////////////////////////////////////////////
            //InputStream excelFile = getAssets().open("TestH.xls");
            //isX = false;
            ///////////////////////////////////////////////////////

            InputStream excelFile = getApplicationContext().openFileInput("schedule(" + date + ")" + (isX ? ".xlsx" : ".xls"));


            Workbook workbook;
            if(isX)
                workbook = new XSSFWorkbook(excelFile);
            else
                workbook = new HSSFWorkbook(excelFile);


            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            String day = getCellAsString(sheet.getRow(0), 0, formulaEvaluator);

            if(getCellAsString(sheet.getRow(0), 1, formulaEvaluator).isEmpty())
                SchooledApplication.FIRST_LINE = 1;

            int rowsCount = sheet.getPhysicalNumberOfRows();
            int maxCols = 0;

            for (int r = 0; r < rowsCount; r++)
                if(sheet.getRow(r).getPhysicalNumberOfCells() > maxCols)
                    maxCols = sheet.getRow(r).getPhysicalNumberOfCells();

            for(int i = 1; i < maxCols; i++)
                SchooledApplication.SQL_DATA.insertDataClass(getCellAsString(sheet.getRow(SchooledApplication.FIRST_LINE),i,formulaEvaluator));

            SchooledApplication.SQL_DATA.createScheduleTable(maxCols - 1);

            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                String[] cells = new String[maxCols - 1];

                for (int c = 0; c < row.getPhysicalNumberOfCells(); c++)
                    if(c != 0 && r > SchooledApplication.FIRST_LINE)
                        cells[c - 1] = getCellAsString(row, c, formulaEvaluator);

                if(r > SchooledApplication.FIRST_LINE)
                    if(!(r == rowsCount - 1 && allNull(cells)))
                        SchooledApplication.SQL_DATA.insertDataSchedule(cells);
            }


            if((isX ?
                    ((XSSFSheet) sheet).getDrawingPatriarch() :
                    ((HSSFSheet) sheet).getDrawingPatriarch())
                    != null) {

                List children = isX ?
                        ((XSSFSheet) sheet).getDrawingPatriarch().getShapes() :
                        ((HSSFSheet) sheet).getDrawingPatriarch().getChildren();

                Iterator it = children.iterator();

                while (it.hasNext()) {
                    if (isX) {
                        XSSFSimpleShape shape = (XSSFSimpleShape) it.next();
                        XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();
                        String str = shape.getText();
                        SchooledApplication.SQL_DATA.insertDataNote(anchor.getCol1(), anchor.getRow1(),
                                anchor.getCol2(), anchor.getRow2(), str);
                    } else {
                        HSSFSimpleShape shape = (HSSFSimpleShape) it.next();
                        HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                        HSSFRichTextString richString = shape.getString();
                        String str = richString.getString();
                        SchooledApplication.SQL_DATA.insertDataNote(anchor.getCol1(), anchor.getRow1(),
                                anchor.getCol2(), anchor.getRow2(), str);
                    }
                }
            }
            return day;
        }

        private boolean allNull(String[] data){
            for(String str : data)
                if(str != null)
                    return false;
            return true;
        }

        private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
            String value = "";
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            if(cellValue != null)
                switch (cellValue.getCellType()) {
                    case Cell.CELL_TYPE_BOOLEAN:
                        value = Boolean.toString(cellValue.getBooleanValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        double numericValue = cellValue.getNumberValue();
                        if(HSSFDateUtil.isCellDateFormatted(cell)) {
                            double date = cellValue.getNumberValue();
                            SimpleDateFormat formatter =
                                    new SimpleDateFormat("dd/MM/yy");
                            value = formatter.format(HSSFDateUtil.getJavaDate(date));
                        } else {
                            value = Double.toString(numericValue);
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value = cellValue.getStringValue();
                        break;
                    default:
                        value = cellValue.toString();
                }
            return value;
        }
    }


    @Nullable
    public static ArrayList<AnnouncementItemData> parseAnnouncementData(){
        ArrayList<AnnouncementItemData> announcementData = new ArrayList<>();
        Document doc;

        SchooledApplication.SQL_DATA.resetAnnouncement();

        try {
            doc = Jsoup.connect("http://www.handasaim.co.il/").get();
        } catch (java.io.IOException e) {
            return null;
        }

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

            announcementData.add(new AnnouncementItemData(title, date, text, url));
            SchooledApplication.SQL_DATA.insertDataAnnouncement(title, text, date, url);
        }
        return announcementData;
    }
}