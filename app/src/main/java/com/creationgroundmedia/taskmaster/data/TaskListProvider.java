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

/**
 * Created by George Cohn III on 9/24/16.
 * Basic content provider for Tasks and Task lists
 */

package com.creationgroundmedia.taskmaster.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import static com.creationgroundmedia.taskmaster.data.TaskListContract.TaskListEntry.getTaskRowIdFromUri;

public class TaskListProvider extends ContentProvider {
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private static final String LOG_CAT = TaskListProvider.class.getSimpleName();
    private static final String SELECTION_BY_ID_STRING = TaskListContract.TaskListEntry._ID + " = ";
    private TaskListDbHelper mOpenHelper;

    static final int TASKS = 100;
    static final int TASK = 101;

    public TaskListProvider() {
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TaskListContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TaskListContract.PATH_TASKS, TASKS);
        matcher.addURI(authority, TaskListContract.PATH_TASK + "/#", TASK);

        return matcher;
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int retVal = -1;

        switch (mUriMatcher.match(uri)) {
            case TASKS: {
                retVal = mOpenHelper.getWritableDatabase()
                        .delete(TaskListContract.TaskListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TASK: {
                retVal = mOpenHelper.getWritableDatabase()
                        .delete(TaskListContract.TaskListEntry.TABLE_NAME,
                                SELECTION_BY_ID_STRING + getTaskRowIdFromUri(uri),
                                null);
                /*
                 * if we delete a single row,
                 * listeners on the whole list will have to be notified too.
                 * This doesn't happen automatically because
                 * the list item URI isn't derived from the list URI
                 */
                if (retVal > 0) {
                    getContext().getContentResolver()
                            .notifyChange(TaskListContract.TaskListEntry.TASKLIST_URI, null);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
        if (retVal > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return retVal;
    }

    @Override
    public String getType(Uri uri) {

        switch (mUriMatcher.match(uri)) {
            case TASK:
                return TaskListContract.TaskListEntry.CONTENT_ITEM_TYPE;
            case TASKS:
                return TaskListContract.TaskListEntry.CONTENT_TYPE;
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = uri;

        switch (mUriMatcher.match(uri)) {
            case TASK:
            case TASKS: {
                long _id = mOpenHelper.getWritableDatabase()
                        .insert(TaskListContract.TaskListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TaskListContract.TaskListEntry.buildTaskUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TaskListDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
//        Log.d(LOG_CAT, "query URI: " + uri);
        switch (mUriMatcher.match(uri)) {
            case TASK: {
                // A single row is queried for details for a single task
                cursor = mOpenHelper.getReadableDatabase().query(
                        TaskListContract.TaskListEntry.TABLE_NAME,
                        projection,
                        SELECTION_BY_ID_STRING + TaskListContract.TaskListEntry.getTaskRowIdFromUri(uri),
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TASKS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TaskListContract.TaskListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
//                Log.d(LOG_CAT, "count: " + cursor.getCount());
                break;
            }

            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        switch (mUriMatcher.match(uri)) {
            case TASK: {
                count = mOpenHelper.getWritableDatabase()
                        .update(TaskListContract.TaskListEntry.TABLE_NAME,
                                values,
                                " _ID = " + TaskListContract.TaskListEntry.getTaskRowIdFromUri(uri)
                                + (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""),
                                selectionArgs);
                /*
                 * if we update a single row,
                 * listeners on the whole list will have to be notified too.
                 * This doesn't happen automatically because
                 * the list item URI isn't derived from the list URI
                 */
                getContext().getContentResolver()
                        .notifyChange(TaskListContract.TaskListEntry.TASKLIST_URI, null);
                break;
            }
            case TASKS: {
                count = mOpenHelper.getWritableDatabase()
                        .update(TaskListContract.TaskListEntry.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}