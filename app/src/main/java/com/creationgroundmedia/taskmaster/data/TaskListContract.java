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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by George Cohn III on 7/4/16.
 * Basic Content Provider Contract
 */

public class TaskListContract {

    // Authority
    static final String CONTENT_AUTHORITY = "com.creationgroundmedia.taskmaster";
    //Base URI
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Paths
    static final String PATH_TASK = "task";
    static final String PATH_TASKS = "tasks";

    /* Inner class that defines the table contents of the products table */
    public static final class TaskListEntry implements BaseColumns {

        public static final Uri TASK_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASK).build();
        public static final Uri TASKLIST_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASKID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_DUEDATE = "duedate";

        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(TASK_URI, id);
        }

        public static Uri buildTaskUri(String id) {
            return buildTaskUri(Long.valueOf(id));
        }

         public static long getTaskRowIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}
