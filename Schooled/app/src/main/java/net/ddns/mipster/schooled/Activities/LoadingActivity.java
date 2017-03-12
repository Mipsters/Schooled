package net.ddns.mipster.schooled.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    private final String WORK = "began work";

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

        if(!getIntent().getBooleanExtra(WORK, false)) {
            new GeneralDataTask().execute();
            getIntent().putExtra(WORK, true);
        }
    }

    class GeneralDataTask extends AsyncTask<Void,Object,Void>{
        private ArrayList<AnnouncementItemData> announcementData;
        private Tuple<String[][], ArrayList<NoteData>> excelData;
        private String[] classes;
        private String error;
        private TextView stat;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBarOff.setVisibility(View.GONE);

            stat = (TextView) findViewById(R.id.stat);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress("Receiving Announcements");

            announcementData = parseAnnouncementData();

            if(announcementData == null) {
                cancel(true);
                error = "Internet connection error";
                return null;
            }

            publishProgress("Downloading schedule Excel file");

            excelData = updateSchedule(announcementData);

            publishProgress("Setting things up");

            classes = new String[excelData.getItem1().length - 1];

            for(int i = 0; i < classes.length; i++)
                classes[i] = excelData.getItem1()[i + 1][SchooledApplication.FIRST_LINE];

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

            Snackbar.make(findViewById(R.id.activity_loading), error, Snackbar.LENGTH_INDEFINITE)
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
            mainActivity.putExtra(SchooledApplication.SCHEDULE_DATA, excelData.getItem1());
            mainActivity.putExtra(SchooledApplication.NOTE_DATA, excelData.getItem2());
            mainActivity.putExtra(SchooledApplication.CLASSES_DATA, classes);

            startActivity(mainActivity);
            finish();
        }

        public Tuple<String[][], ArrayList<NoteData>> updateSchedule(ArrayList<AnnouncementItemData> announcementData){
            for(AnnouncementItemData id : announcementData)
                if(id.getUrl().contains("s3-eu-west-1.amazonaws.com/schooly/handasaim/news") &&
                        (id.getUrl().contains(".xls") || id.getUrl().contains(".xlsx"))){
                    String end = id.getUrl().contains(".xls") ? ".xls" : ".xlsx";
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
                        if (!getBaseContext().getFileStreamPath("schedule(" + date.replace("/", "-") + ")" + end).exists())
                            downloadExcel(id, end);
                        else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17)
                            downloadExcel(id, end);

                        return goThroughExcel(id, end);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
                }
            return null;
        }

        private void downloadExcel(AnnouncementItemData itemData, String end) throws IOException {
            String date = itemData.getDate().replace("/","-");
            URL url = new URL(itemData.getUrl());
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = openFileOutput("schedule(" + date + ")" + end, Context.MODE_PRIVATE);

            byte data[] = new byte[32];
            int count;

            while ((count = input.read(data)) != -1)
                output.write(data, 0, count);

            output.flush();
            output.close();
            input.close();
        }

        private Tuple<String[][], ArrayList<NoteData>> goThroughExcel(AnnouncementItemData itemData, String end) throws IOException {
            String[][] excelData;
            String date = itemData.getDate().replace("/","-");

            ///////////////////////////////////////////////////////
            //InputStream excelFile = getAssets().open("TestH.xls");
            //end = "xls";
            ///////////////////////////////////////////////////////

            InputStream excelFile = openFileInput("schedule(" + date + ")" + end);

            boolean isX = end.contains("xlsx");

            Workbook workbook;
            if(isX)
                workbook = new XSSFWorkbook(excelFile);
            else
                workbook = new HSSFWorkbook(excelFile);


            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            int rowsCount = sheet.getPhysicalNumberOfRows();

            int maxRows = 0;
            for (int r = 0; r < rowsCount; r++)
                if(sheet.getRow(r).getPhysicalNumberOfCells() > maxRows)
                    maxRows = sheet.getRow(r).getPhysicalNumberOfCells();


            excelData = new String[maxRows][rowsCount];

            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                for (int c = 0; c < row.getPhysicalNumberOfCells(); c++)
                    excelData[c][r] = getCellAsString(row, c, formulaEvaluator);
            }


            ArrayList<NoteData> noteData = new ArrayList<>();

            List children = isX ?
                    ((XSSFSheet) sheet).getDrawingPatriarch().getShapes() :
                    ((HSSFSheet) sheet).getDrawingPatriarch().getChildren();

            Iterator it = children.iterator();

            while (it.hasNext()) {
                if(isX) {
                    XSSFSimpleShape shape = (XSSFSimpleShape) it.next();
                    XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();
                    String str = shape.getText();
                    noteData.add(new NoteData(anchor.getCol1(), anchor.getRow1(),
                                 anchor.getCol2(), anchor.getRow2(), str));
                } else {
                    HSSFSimpleShape shape = (HSSFSimpleShape) it.next();
                    HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                    HSSFRichTextString richString = shape.getString();
                    String str = richString.getString();
                    noteData.add(new NoteData(anchor.getCol1(), anchor.getRow1(),
                            anchor.getCol2(), anchor.getRow2(), str));
                }
            }

            return new Tuple<>(excelData, noteData);
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

            announcementData.add(new AnnouncementItemData(title,date,text,url));
        }
        return announcementData;
    }
}