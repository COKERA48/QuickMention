package com.CSC481Project.ashley.quickmentiontest;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ashley on 4/2/2018.
 */

class QMContract {
    private QMContract() {}

    static final String CONTENT_AUTHORITY = "com.CSC481Project.ashley.quickmentiontest";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_VEHICLE1 = "task-path";
    static final String PATH_VEHICLE2 = "category-path";
    static final String PATH_VEHICLE3 = "template-path";

    static final class TaskEntry implements BaseColumns {

        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEHICLE1);

        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE1;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE1;

        static final String TABLE_NAME = "tasks";

        final static String _ID1 = BaseColumns._ID;

        static final String KEY_NAME = "task_name";
        static final String KEY_START_DATE = "start_date";
        static final String KEY_START_TIME = "start_time";
        static final String KEY_END_DATE = "end_date";
        static final String KEY_END_TIME = "end_time";
        static final String KEY_REPEATS = "repeats";
        static final String KEY_NOTES = "notes";
        static final String KEY_TIMESTAMP = "timestamp";

    }

    static final class CategoryEntry implements BaseColumns {
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEHICLE2);
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE2;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE2;

        final static String TABLE_NAME = "categories";

        final static String _ID2 = BaseColumns._ID;
        static final String KEY_NAME = "category_name";
        static final String KEY_ICON = "icon";
    }

    static final class TemplateEntry implements BaseColumns {
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEHICLE3);
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE3;

        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE3;

        final static String TABLE_NAME = "templates";

        final static String _ID3 = BaseColumns._ID;
        static final String KEY_NAME = "template_name";
        static final String KEY_REPEATS = "repeats";
        static final String KEY_TEMP_CAT = "template_category";
    }


}