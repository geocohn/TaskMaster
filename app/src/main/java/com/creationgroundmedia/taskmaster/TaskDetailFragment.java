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

package com.creationgroundmedia.taskmaster;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.creationgroundmedia.taskmaster.data.TaskListContract;

/**
 * A fragment representing a single task detail screen.
 * This fragment is either contained in a {@link TaskListActivity}
 * in two-pane mode (on tablets) or a {@link TaskDetailActivity}
 * on handsets.
 */
public class TaskDetailFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private static final int URL_LOADER = 2;

    private static final String[] PROJECTION = new String[] {
            TaskListContract.TaskListEntry._ID,
            TaskListContract.TaskListEntry.COLUMN_DESCRIPTION,
            TaskListContract.TaskListEntry.COLUMN_DUEDATE,
            TaskListContract.TaskListEntry.COLUMN_NAME,
            TaskListContract.TaskListEntry.COLUMN_PRIORITY,
            TaskListContract.TaskListEntry.COLUMN_STATUS
    };
    // The following must agree with the PROJECTION above
    private static final int ID = 0;
    private static final int DESCRIPTION = 1;
    private static final int DUEDATE = 2;
    private static final int NAME = 3;
    private static final int PRIORITY = 4;
    private static final int STATUS = 5;

    private String[] mPriorities;

    private long mItemId;
    private View mView;
    private Loader<Cursor> mCursorLoader;
    private Context mContext;
    private String mDueDate;
    private String mName;
    private String mDescription;
    private String mPriority;
    private Spinner mPriorityView;
    private EditText mNameView;
    private EditText mDescriptionView;
    private DatePicker mDateView;
    private boolean mNewTask;
    private boolean mDone = false;
    private CheckBox mCompletedView;
    private boolean mBackFromRotation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskDetailFragment() {
    }

    public static TaskDetailFragment newInstance(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(TaskDetailFragment.ARG_ITEM_ID, id);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public static void launchInstance(Context context, String id) {
        Intent intent = new Intent(context, TaskDetailActivity.class);
        intent.putExtra(TaskDetailFragment.ARG_ITEM_ID, id);
        context.startActivity(intent);
    }

    public static void launchDialog(Context context) {
        newInstance("")
                .show(((FragmentActivity)(context)).getSupportFragmentManager(), "new task");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBackFromRotation = savedInstanceState != null;
        if (mBackFromRotation) {
            mDescription = savedInstanceState.getString(TaskListContract.TaskListEntry.COLUMN_DESCRIPTION);
            mDueDate = savedInstanceState.getString(TaskListContract.TaskListEntry.COLUMN_DUEDATE);
            mName = savedInstanceState.getString(TaskListContract.TaskListEntry.COLUMN_NAME);
            mPriority = savedInstanceState.getString(TaskListContract.TaskListEntry.COLUMN_PRIORITY);
            mDone = savedInstanceState.getString(TaskListContract.TaskListEntry.COLUMN_STATUS) != "0";
        }

        /**
         * If we have an argument that's not null and not an empty string,
         * then we're looking to do an update, otherwise we're creating a new item
         */
        mNewTask = true;
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String itemIdString = getArguments().getString(ARG_ITEM_ID);
            mNewTask = itemIdString == null || itemIdString.isEmpty();
            if (!mNewTask) {
                mItemId = Long.parseLong(itemIdString);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TaskListContract.TaskListEntry.COLUMN_DESCRIPTION, mDescription);
        outState.putString(TaskListContract.TaskListEntry.COLUMN_DUEDATE, mDueDate);
        outState.putString(TaskListContract.TaskListEntry.COLUMN_NAME, mName);
        outState.putString(TaskListContract.TaskListEntry.COLUMN_PRIORITY, mPriority);
        outState.putString(TaskListContract.TaskListEntry.COLUMN_STATUS, mDone? "1" : "0");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.task_detail, container, false);
        mNameView = (EditText) mView.findViewById(R.id.task_detail_name);
        mDescriptionView = (EditText) mView.findViewById(R.id.task_detail_description);
        mDateView = (DatePicker) mView.findViewById(R.id.task_detail_due_date);
        mCompletedView = (CheckBox) mView.findViewById(R.id.task_detail_status);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(mContext,
                R.array.priority, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mPriorityView = (Spinner) mView.findViewById(R.id.task_detail_priority);
        mPriorityView.setAdapter(spinnerAdapter);
        mPriorityView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPriority = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button saveView = (Button) mView.findViewById(R.id.save);
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                if (mNewTask) {
                    dismiss();
                }
            }
        });
        if (!mBackFromRotation && !mNewTask) {
            mCursorLoader = getActivity().getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        }
        return mView;
    }

    private void saveData() {
        mName = String.valueOf(mNameView.getText());
        mDescription = String.valueOf(mDescriptionView.getText());
        mPriority = mPriorityView.getSelectedItem().toString();
        mDueDate = String.format("%4d%02d%02d",
                mDateView.getYear(), mDateView.getMonth() + 1, mDateView.getDayOfMonth());

        ContentValues values = new ContentValues();
        values.put(TaskListContract.TaskListEntry.COLUMN_DESCRIPTION, mDescription);
        values.put(TaskListContract.TaskListEntry.COLUMN_DUEDATE, mDueDate);
        values.put(TaskListContract.TaskListEntry.COLUMN_NAME, mName);
        values.put(TaskListContract.TaskListEntry.COLUMN_PRIORITY, mPriority);
        values.put(TaskListContract.TaskListEntry.COLUMN_STATUS, mDone? "1" : "0");
        if (mNewTask) {
            mContext.getContentResolver().insert(TaskListContract.TaskListEntry.TASKLIST_URI,
                    values);
        } else {
            mContext.getContentResolver().update(TaskListContract.TaskListEntry.buildTaskUri(mItemId),
                    values,
                    null,
                    null);
        }
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        mPriorities = getResources().getStringArray(R.array.priority);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        mContext,                                   // context
                        TaskListContract.TaskListEntry.buildTaskUri(mItemId),  // Table to query
                        PROJECTION,                             // Projection to return
                        null,
                        null,                                   // No selection arguments
                        null                                    // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        data.moveToFirst();
        mName = data.getString(NAME);
        mDueDate = data.getString(DUEDATE);
        mDescription = data.getString(DESCRIPTION);
        mDone = data.getString(STATUS).compareTo("0") != 0;
        mCompletedView.setChecked(mDone);
        mCompletedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDone = isChecked;
            }
        });
        mPriority = data.getString(PRIORITY);

        mNameView.setText(mName);
        mDescriptionView.setText(mDescription);
        mPriorityView.setSelection(priorityPosition(mPriority));

        mDateView.init(getYear(mDueDate), getMonth(mDueDate), getDay(mDueDate), null);
    }

    private int priorityPosition(String priority) {
        int i = 0;
        for (String str : mPriorities) {
            if (priority.compareTo(str) == 0) {
                return i;
            }
            i++;
        }
        return 0;
    }

    private int getDay(@NonNull String date) {
        return Integer.parseInt(date.substring(6,8));
    }

    private int getMonth(@NonNull String date) {
        return Integer.parseInt(date.substring(4,6)) - 1;
    }

    private int getYear(@NonNull String date) {
        return Integer.parseInt(date.substring(0,4));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        nothing to be mDone here
    }
}
