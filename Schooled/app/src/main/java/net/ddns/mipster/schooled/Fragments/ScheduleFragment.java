package net.ddns.mipster.schooled.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.mipster.schooled.SQLiteHelper;
import net.ddns.mipster.schooled.activities.LoadingActivity;
import net.ddns.mipster.schooled.classes.AnnouncementItemData;
import net.ddns.mipster.schooled.classes.NoteData;
import net.ddns.mipster.schooled.R;
import net.ddns.mipster.schooled.adapters.ScheduleListAdapter;
import net.ddns.mipster.schooled.SchooledApplication;
import net.ddns.mipster.schooled.classes.Tuple;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class ScheduleFragment extends Fragment {

    private TextView day, error;
    private Spinner spinner;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private Cursor excelData;
    private Switch aSwitch;
    private ImageView share;
    private String[] classes;
    private String[] classesData;
    private String dayVal;
    private SharedPreferences sharedPref;

    public  ScheduleFragment(){}

    public static ScheduleFragment newInstance(String[] classes, String day) {
        Bundle args = new Bundle();

        args.putStringArray(SchooledApplication.CLASSES_DATA, classes);
        args.putString(SchooledApplication.DAY_DATA, day);

        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scheduale, container, false);

        day = (TextView) rootView.findViewById(R.id.day);
        error = (TextView) rootView.findViewById(R.id.error);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        listView = (ListView) rootView.findViewById(R.id.listView);
        aSwitch = (Switch) rootView.findViewById(R.id.switch1);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.appBar);
        share = (ImageView) rootView.findViewById(R.id.share);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        excelData = SchooledApplication.SQL_DATA.getAllData(SQLiteHelper.Tables.SCHEDULE);
        classes = getArguments().getStringArray(SchooledApplication.CLASSES_DATA);
        dayVal = getArguments().getString(SchooledApplication.DAY_DATA);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);


        if(excelData == null || excelData.getCount() == 0 || classes == null) {
            appBarLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);

            error.setVisibility(View.VISIBLE);
            error.setText("אין מערכת");

            return;
        }


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item_rtl, (CharSequence[]) classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        aSwitch.setChecked(sharedPref.getBoolean(SchooledApplication.SWITCH_DATA, false));

        day.setText("יום " + dayVal);

        spinner.setSelection(sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 1) - 1);

        excelData.moveToFirst();
        classesData = new String[excelData.getCount()];

        for(int j = 0; j < classesData.length; j++, excelData.moveToNext())
            classesData[j] = excelData.getString(spinner.getSelectedItemPosition());


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //deleted = deleteExtra(excelData[i + 1]);
                excelData.moveToFirst();
                classesData = new String[excelData.getCount()];

                for(int j = 0; j < classesData.length; j++, excelData.moveToNext())
                    classesData[j] = excelData.getString(i);


                ScheduleListAdapter scheduleListAdapter =
                        new ScheduleListAdapter(getContext(), classesData, aSwitch.isChecked());
                listView.setAdapter(scheduleListAdapter);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SchooledApplication.SCHEDULE_DATA, i + 1);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        swipeRefreshLayout.setColorSchemeColors(
                Color.RED,
                Color.GREEN,
                Color.BLUE
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //new GeneralDataTask().execute();
                startActivity(new Intent(getContext(), LoadingActivity.class));
                getActivity().finish();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getContext(), classesData, aSwitch.isChecked());
                listView.setAdapter(scheduleListAdapter);

                sharedPref.edit().putBoolean(SchooledApplication.SWITCH_DATA,aSwitch.isChecked()).apply();
            }
        });

        /*
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, scheduleText());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        */
    }
/*
    private String scheduleText(){
        String text = "";

        int len = excelData[1 + spinner.getSelectedItemPosition()].length - 1;

        for(; excelData[1 + spinner.getSelectedItemPosition()][len] == null || excelData[1 + spinner.getSelectedItemPosition()][len].isEmpty(); len--);

        text += "המערכת ל" + day.getText() + ", כיתה " + classes[spinner.getSelectedItemPosition()] + "\n";

        for (int i = 2; i < len; i++) {
            String time = Integer.toString(i - 1 - (deleted.getItem2() ? 1 : 0)) + ". ";
            String classText = excelData[1 + spinner.getSelectedItemPosition()][i].replaceAll("(?:\\n)+", ", ");
            text += time + (classText.isEmpty() ? "אין שיעור" : classText) + '\n';
        }

        String time = Integer.toString(len - 1 - (deleted.getItem2() ? 1 : 0)) + ". ";
        String classText = excelData[1 + spinner.getSelectedItemPosition()][len].replaceAll("(?:\\n)+", ", ");
        text += time + (classText.isEmpty() ? "אין שיעור" : classText);

        return text;
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();

        sharedPref.edit().putBoolean(SchooledApplication.SWITCH_DATA,aSwitch.isChecked()).apply();
    }

    private Tuple<String[], Boolean> deleteExtra(String[] schedule){
        boolean isPre = false;
        int len;

        if(!schedule[1 + SchooledApplication.FIRST_LINE].isEmpty())
            isPre = true;

        for (len = 0; len < schedule.length; len++) {
            if(schedule[schedule.length - len - 1] != null)
                if (!schedule[schedule.length - len - 1].isEmpty())
                    break;
        }

        len += isPre ? 1 : 2;

        return new Tuple<>(Arrays.copyOfRange(schedule, SchooledApplication.FIRST_LINE + (isPre ? 1 : 2), /*FIRST_LINE +*/ (isPre ? 1 : 2) + schedule.length - len), isPre);
    }
/*
    class GeneralDataTask extends AsyncTask<Void,Object,Void> {
        private ArrayList<AnnouncementItemData> announcementData;
        private Tuple<String[][], ArrayList<NoteData>> excelData;

        @Override
        protected Void doInBackground(Void... voids) {
            announcementData = LoadingActivity.parseAnnouncementData();

            if(announcementData == null) {
                cancel(true);
                return null;
            }

            excelData = updateSchedule(announcementData);

            if(excelData.getItem1()[0][1].isEmpty())
                SchooledApplication.FIRST_LINE = 1;
            else
                SchooledApplication.FIRST_LINE = 0;

            return null;
        }

        @Override
        protected void onCancelled() {
            Snackbar.make(getView(),
                    "Internet connection error",Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            deleted = deleteExtra(excelData.getItem1()[sharedPref.getInt(SchooledApplication.SCHEDULE_DATA, 1)]);

            ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getContext(), deleted.getItem1(), deleted.getItem2(), aSwitch.isChecked());
            listView.setAdapter(scheduleListAdapter);

            swipeRefreshLayout.setRefreshing(false);
        }

        private Tuple<String[][], ArrayList<NoteData>> updateSchedule(ArrayList<AnnouncementItemData> announcementData){
            for(AnnouncementItemData id : announcementData)
                if(id.getUrl().contains("s3-eu-west-1.amazonaws.com/schooly/handasaim/news") &&
                        id.getTitle().contains("מערכת שעות") && (id.getUrl().contains(".xls") || id.getUrl().contains(".xlsx"))){
                    boolean isX = id.getUrl().contains(".xlsx");
                    String date = id.getDate();

                    try {
                        if (!getContext().getApplicationContext().getFileStreamPath("schedule(" + date.replace("/", "-") + ")" + (isX ? ".xlsx" : ".xls")).exists())
                            downloadExcel(id, isX);
                        else if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 17)
                            downloadExcel(id, isX);

                        return goThroughExcel(id, isX);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
                }
            return null;
        }

        private void downloadExcel(AnnouncementItemData itemData, boolean isX) throws IOException {
            String date = itemData.getDate().replace("/","-");
            URL url = new URL(itemData.getUrl());
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = getContext().getApplicationContext().openFileOutput("schedule(" + date + ")" + (isX ? ".xlsx" : ".xls"), Context.MODE_PRIVATE);

            byte SQL_DATA[] = new byte[32];
            int count;

            while ((count = input.read(SQL_DATA)) != -1)
                output.write(SQL_DATA, 0, count);

            output.close();
            input.close();
        }

        private Tuple<String[][], ArrayList<NoteData>> goThroughExcel(AnnouncementItemData itemData, boolean isX) throws IOException {
            String[][] excelData;
            String date = itemData.getDate().replace("/","-");

            InputStream excelFile = getContext().getApplicationContext().openFileInput("schedule(" + date + ")" + (isX ? ".xlsx" : ".xls"));

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
        String[] SQL_DATA = newsHeadlines.html().split(String.format("(?=%1$s)", "<sup>"));
        String[] newData = new String[SQL_DATA.length - 1];

        System.arraycopy(SQL_DATA,1,newData,0,SQL_DATA.length - 1);

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
    */
}