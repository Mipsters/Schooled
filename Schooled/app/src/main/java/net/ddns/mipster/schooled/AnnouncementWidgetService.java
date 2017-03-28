package net.ddns.mipster.schooled;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by Chen on 18/03/2017.
 */

public class AnnouncementWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ViewsFactory(getApplicationContext());
    }

    class ViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        private Cursor cursor;
        private Context context;

        public ViewsFactory(Context context){
            this.context = context;
            this.cursor = SchooledApplication.data.getAllData(SQLiteHelper.ANNOUNCEMENT_TABLE);
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(context.getPackageName(),
                    R.layout.announcement_list_item);

            cursor.moveToPosition(position);

            row.setTextViewText(R.id.title, cursor.getString(0));
            row.setTextViewText(R.id.text, cursor.getString(1));
            row.setTextViewText(R.id.date, cursor.getString(2));

            return row;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void onCreate() {}

        @Override
        public void onDataSetChanged() {}

        @Override
        public void onDestroy() {}

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }
    }
}
