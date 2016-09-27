/*
 * Copyright 2016 George Cohn III
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.creationgroundmedia.taskmaster.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.creationgroundmedia.taskmaster.data.TaskListContract.TaskListEntry;

/**
 * Created by George Cohn III on 9/24/16.
 * Build the SQLite database
 */

public class TaskListDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "tasklist.db";

    public TaskListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + TaskListEntry.TABLE_NAME + " (" +
                TaskListEntry.COLUMN_TASKID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TaskListEntry.COLUMN_NAME + " TEXT, " +
                TaskListEntry.COLUMN_DESCRIPTION + " TEXT, " +
                TaskListEntry.COLUMN_STATUS + " TEXT, " +
                TaskListEntry.COLUMN_PRIORITY + " TEXT, " +
                TaskListEntry.COLUMN_DUEDATE + " TEXT );";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}