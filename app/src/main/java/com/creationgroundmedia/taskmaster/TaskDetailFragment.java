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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.creationgroundmedia.taskmaster.data.TaskListContract;

/**
 * A fragment representing a single task detail screen.
 * This fragment is either contained in a {@link taskListActivity}
 * in two-pane mode (on tablets) or a {@link taskDetailActivity}
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

    private long mItemId;
    private View mView;
    private Loader<Cursor> mCursorLoader;
    private Context mContext;
    private String mDueDate;
    private String mName;
    private String mDescription;
    private String mPriority;
    private String mStatus = "New";
    private Spinner mPriorityView;
    private EditText mNameView;
    private EditText mDescriptionView;
    private DatePicker mDateView;
    private boolean mNewTask;

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
        Intent intent = new Intent(context, taskDetailActivity.class);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.task_detail, container, false);
        mNameView = (EditText) mView.findViewById(R.id.task_detail_name);
        mDescriptionView = (EditText) mView.findViewById(R.id.task_detail_description);
        mDateView = (DatePicker) mView.findViewById(R.id.task_detail_due_date);

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
                dismiss();
            }
        });
        Button cancelView = (Button) mView.findViewById(R.id.cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (!mNewTask) {
            mCursorLoader = getActivity().getSupportLoaderManager().initLoader(URL_LOADER, null, this);
        }
        return mView;
    }

    private void saveData() {
        mName = String.valueOf(mNameView.getText());
        mDescription = String.valueOf(mDescriptionView.getText());
        mPriority = mPriorityView.getSelectedItem().toString();
        mStatus = "0";
        mDueDate = String.format("%4d%02d%02d",
                mDateView.getYear(), mDateView.getMonth(), mDateView.getDayOfMonth());

        ContentValues values = new ContentValues();
        values.put(TaskListContract.TaskListEntry.COLUMN_DESCRIPTION, mDescription);
        values.put(TaskListContract.TaskListEntry.COLUMN_DUEDATE, mDueDate);
        values.put(TaskListContract.TaskListEntry.COLUMN_NAME, mName);
        values.put(TaskListContract.TaskListEntry.COLUMN_PRIORITY, mPriority);
        values.put(TaskListContract.TaskListEntry.COLUMN_STATUS, mStatus);
        if (mNewTask) {
            mContext.getContentResolver().insert(TaskListContract.TaskListEntry.TASKLIST_URI,
                    values);
        } else {
            values.put(TaskListContract.TaskListEntry._ID, mItemId);
            mContext.getContentResolver().update(TaskListContract.TaskListEntry.buildTaskUri(mItemId),
                    values,
                    null,
                    null);
        }
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
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

        mNameView.setText(mName);
        mDescriptionView.setText(mDescription);

        mDateView.init(getYear(mDueDate), getMonth(mDueDate), getDay(mDueDate), null);
        mStatus = "0";
    }

    private int getDay(@NonNull String date) {
        return Integer.parseInt(date.substring(6,8));
    }

    private int getMonth(@NonNull String date) {
        return Integer.parseInt(date.substring(4,6));
    }

    private int getYear(@NonNull String date) {
        return Integer.parseInt(date.substring(0,4));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        nothing to be done here
    }
}