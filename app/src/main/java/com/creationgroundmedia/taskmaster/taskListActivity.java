package com.creationgroundmedia.taskmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creationgroundmedia.taskmaster.data.TaskListContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An activity representing a list of tasks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link taskDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class taskListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int URL_LOADER = 1;
    private static final String SELECTED_POSITION = "selectedPosition";

    private static final String[] PROJECTION = new String[] {
            TaskListContract.TaskListEntry._ID,
            TaskListContract.TaskListEntry.COLUMN_DUEDATE,
            TaskListContract.TaskListEntry.COLUMN_NAME,
            TaskListContract.TaskListEntry.COLUMN_PRIORITY,
            TaskListContract.TaskListEntry.COLUMN_STATUS
    };
    // The following must agree with the PROJECTION above
    private static final int ID = 0;
    private static final int DATE = 1;
    private static final int NAME = 2;
    private static final int PRIORITY = 3;
    private static final int STATUS = 4;

    private String[] mPriorities;
    private int[] mPriorityColors;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Context mContext;
    private int mSelectedPosition;
    private Loader<Cursor> mCursorLoader;
    private Uri mSearchUri = TaskListContract.TaskListEntry.TASKLIST_URI;

    private RecyclerView mRecyclerView;
    private int mLastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (savedInstanceState != null) {
            mSelectedPosition = savedInstanceState.getInt(SELECTED_POSITION);
        }

        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.mipmap.ic_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDetailFragment.launchDialog(mContext);
//                TaskDetailFragment.launchInstance(mContext, "");
            }
        });

        mPriorities = getResources().getStringArray(R.array.priority);
        mPriorityColors = getResources().getIntArray(R.array.priority_colors);

        mCursorLoader = getSupportLoaderManager().initLoader(URL_LOADER, null, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.task_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

        if (findViewById(R.id.task_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        SimpleTaskListCursorRecyclerViewAdapter taskAdapter =
                new SimpleTaskListCursorRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(taskAdapter);
        ItemTouchHelper.Callback callback = new TaskTouchHelper(taskAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        this,                                   // context
                        mSearchUri,  // Table to query
                        PROJECTION,                             // Projection to return
                        null,
                        null,                                   // No selection arguments
                        TaskListContract.TaskListEntry.COLUMN_DUEDATE + " ASC"
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        SimpleTaskListCursorRecyclerViewAdapter adapter =
                (SimpleTaskListCursorRecyclerViewAdapter) mRecyclerView.getAdapter();
        adapter.changeCursor(data);
        mRecyclerView.smoothScrollToPosition(mSelectedPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SimpleTaskListCursorRecyclerViewAdapter)(mRecyclerView.getAdapter())).changeCursor(null);
    }

    public class TaskTouchHelper extends ItemTouchHelper.SimpleCallback {
        private SimpleTaskListCursorRecyclerViewAdapter mTaskAdapter;

        public TaskTouchHelper(SimpleTaskListCursorRecyclerViewAdapter taskAdapter){
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.mTaskAdapter = taskAdapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //TODO: Not implemented here
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //Remove item
            long itemId = Long.parseLong(((SimpleTaskListCursorRecyclerViewAdapter.ViewHolder) viewHolder).mId);
            mTaskAdapter.delete(itemId);
        }
    }
    public class SimpleTaskListCursorRecyclerViewAdapter
            extends CursorRecyclerViewAdapter<SimpleTaskListCursorRecyclerViewAdapter.ViewHolder> {

        SimpleTaskListCursorRecyclerViewAdapter(Context context, Cursor productCursor) {
            super(context, productCursor);
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
            // set up the animation per the xml files
            int position = cursor.getPosition();
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    (position > mLastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;

            holder.mId = cursor.getString(ID);
            holder.mTaskName.setText(cursor.getString(NAME));
            holder.mPriority = priorityPosition(cursor.getString(PRIORITY));
            holder.mTaskCard.setCardBackgroundColor(mPriorityColors[holder.mPriority]);

            holder.mDueDate.setText(formattedDateString(cursor.getString(DATE)));
            holder.mDone = cursor.getString(STATUS).compareTo("0") != 0;
            strikeThrough(holder.mTaskName, holder.mDone);
            holder.mTaskCompleted.setChecked(holder.mDone);
            holder.mTaskCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    holder.mDone = isChecked;
                    strikeThrough(holder.mTaskName, isChecked);
                    saveRow(holder.mId, isChecked);
                }
            });
            holder.mTaskContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.task_detail_container,
                                        TaskDetailFragment.newInstance(holder.mId))
                                .commit();
                    } else {
                        TaskDetailFragment.launchInstance(v.getContext(), holder.mId);
                    }
                }
            });
        }

        private void strikeThrough(TextView textView, boolean strike) {
            if (strike) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            // Prevent problems when fast scrolling due to
            // the view being reused while the animation is happening
            holder.itemView.clearAnimation();
            super.onViewDetachedFromWindow(holder);
        }

        public void delete(long itemId) {
            getContentResolver().delete(
                    TaskListContract.TaskListEntry.buildTaskUri(itemId),
                    null,
                    null);
            getSupportLoaderManager().restartLoader(URL_LOADER, null, taskListActivity.this);
        }

        public void saveRow(String itemId, boolean done) {
            ContentValues values = new ContentValues();
            values.put(TaskListContract.TaskListEntry.COLUMN_STATUS, done? "1" : "0");
            getContentResolver().update(
                    TaskListContract.TaskListEntry.buildTaskUri(itemId),
                    values,
                    null,
                    null);
//            getSupportLoaderManager().restartLoader(URL_LOADER, null, taskListActivity.this);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final CheckBox mTaskCompleted;
            public final LinearLayout mTaskContainer;
            public final TextView mTaskName;
            public final TextView mDueDate;
            private final CardView mTaskCard;
            public String mId;
            public boolean mDone;
            public int mPriority;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTaskCompleted = (CheckBox) view.findViewById(R.id.task_list_completed);
                mTaskContainer = (LinearLayout) view.findViewById(R.id.task_list_container);
                mTaskName = (TextView) view.findViewById(R.id.task_list_name);
                mDueDate = (TextView) view.findViewById(R.id.task_list_due_date);
                mTaskCard = (CardView) view.findViewById(R.id.task_card);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTaskName.getText() + "'";
            }
        }
    }

    public String formattedDateString(String rawDate) {
        final SimpleDateFormat rawFormat = new SimpleDateFormat("yyyyMMdd");
        final SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MMM-dd");
        try {
            Date date = rawFormat.parse(rawDate);
            return outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
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
}
