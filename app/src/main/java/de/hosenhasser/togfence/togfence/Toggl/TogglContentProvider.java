package de.hosenhasser.togfence.togfence.Toggl;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.hardware.camera2.TotalCaptureResult;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hosenhasser.togfence.togfence.GeofenceElement;

public class TogglContentProvider extends ContentProvider {
    static final public String PROVIDER_NAME = "de.hosenhasser.togfence.togfence.Toggl.toggl_content_provider";
    static final public String URL_PROJECTS = "content://" + PROVIDER_NAME + "/projects";
    static final public String URL_TAGS = "content://" + PROVIDER_NAME + "/tags";

    static final public Uri CONTENT_URI_PROJECTS = Uri.parse(URL_PROJECTS);
    static final public Uri CONTENT_URI_TAGS = Uri.parse(URL_TAGS);
    static final public String _ID = "_id";
    static final public String ID = "id";
    static final public String NAME = "name";
    static final public String AT = "at";

    static final int PROJECTS = 1;
    static final int PROJECT_ID = 2;
    static final int TAGS = 3;
    static final int TAG_ID = 4;

    private static HashMap<String, String> PROJECTS_PROJECTION_MAP;
    private static HashMap<String, String> TAGS_PROJECTION_MAP;


    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "projects", PROJECTS);
        uriMatcher.addURI(PROVIDER_NAME, "projects/#", PROJECT_ID);
        uriMatcher.addURI(PROVIDER_NAME, "tags", TAGS);
        uriMatcher.addURI(PROVIDER_NAME, "tags/#", TAG_ID);
    }

    static final String DATABASE_NAME = "toggl";
    static final String PROJECTS_TABLE_NAME = "projects";
    static final String TAGS_TABLE_NAME = "tags";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE_PROJECTS =
            " CREATE TABLE " + PROJECTS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " id INTEGER, " +
                    " name TEXT NOT NULL, " +
                    " at TEXT " +
                    " );";

    static final String CREATE_DB_TABLE_TAGS =
            " CREATE TABLE " + TAGS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " id INTEGER, " +
                    " name TEXT NOT NULL, " +
                    " at TEXT " +
                    " );";


    private static class TogglDatabaseHelper extends SQLiteOpenHelper {
        TogglDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE_PROJECTS);
            db.execSQL(CREATE_DB_TABLE_TAGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static volatile TogglContentProvider sTogglContentProviderSingletonInstance;
    private Context context;
    private TogglDatabaseHelper mOpenHelper;

    public static TogglContentProvider getInstance() {
        if (sTogglContentProviderSingletonInstance == null) {
            synchronized (TogglContentProvider.class) {
                if (sTogglContentProviderSingletonInstance == null) sTogglContentProviderSingletonInstance = new TogglContentProvider();
            }
        }

        return sTogglContentProviderSingletonInstance;
    }

    protected TogglContentProvider readResolve() {
        return getInstance();
    }

    public TogglContentProvider() {

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TogglDatabaseHelper(getContext());
        return mOpenHelper != null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)){
            case PROJECTS:
                count = db.delete(PROJECTS_TABLE_NAME, selection, selectionArgs);
                break;

            case PROJECT_ID:
                String projid = uri.getPathSegments().get(1);
                count = db.delete(PROJECTS_TABLE_NAME, _ID +  " = " + projid +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""), selectionArgs);
                break;
            case TAGS:
                count = db.delete(TAGS_TABLE_NAME, selection, selectionArgs);
                break;

            case TAG_ID:
                String tagid = uri.getPathSegments().get(1);
                count = db.delete(TAGS_TABLE_NAME, _ID +  " = " + tagid +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case PROJECTS:
                return "vnd.android.cursor.dir/vnd.togfence.Toggl.projects";
            case PROJECT_ID:
                return "vnd.android.cursor.item/vnd.togfence.Toggl.projects";
            case TAGS:
                return "vnd.android.cursor.dir/vnd.togfence.Toggl.tags";
            case TAG_ID:
                return "vnd.android.cursor.item/vnd.togfence.Toggl.tags";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private String tablenameFromUri(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PROJECTS:
            case PROJECT_ID:
                return PROJECTS_TABLE_NAME;
            case TAGS:
            case TAG_ID:
                return TAGS_TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tablename = tablenameFromUri(uri);
        long rowID = db.insert(	tablename, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    public Uri insert(TogglProject project) {
        ContentValues cv = new ContentValues();
        cv.put(TogglContentProvider.ID, project.id);
        cv.put(TogglContentProvider.NAME, project.name);
        cv.put(TogglContentProvider.AT, project.at);
        return insert(TogglContentProvider.CONTENT_URI_PROJECTS, cv);
    }

    public Uri insert(TogglTag tag) {
        ContentValues cv = new ContentValues();
        cv.put(TogglContentProvider.ID, tag.id);
        cv.put(TogglContentProvider.NAME, tag.name);
        cv.put(TogglContentProvider.AT, tag.at);
        return insert(TogglContentProvider.CONTENT_URI_TAGS, cv);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case PROJECTS:
                qb.setTables(PROJECTS_TABLE_NAME);
                qb.setProjectionMap(PROJECTS_PROJECTION_MAP);
                break;

            case PROJECT_ID:
                qb.setTables(PROJECTS_TABLE_NAME);
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            case TAGS:
                qb.setTables(TAGS_TABLE_NAME);
                qb.setProjectionMap(TAGS_PROJECTION_MAP);
                break;

            case TAG_ID:
                qb.setTables(TAGS_TABLE_NAME);
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
        }

        if (sortOrder == null || sortOrder == ""){
            sortOrder = NAME;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case PROJECTS:
                count = db.update(PROJECTS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case PROJECT_ID:
                count = db.update(PROJECTS_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""), selectionArgs);
                break;
            case TAGS:
                count = db.update(TAGS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case TAG_ID:
                count = db.update(TAGS_TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public static Cursor getAllProjectsCursor(ContentResolver contentResolver) {
        return getAllCursor(contentResolver, TogglContentProvider.CONTENT_URI_PROJECTS);
    }

    public static Cursor getAllTagsCursor(ContentResolver contentResolver) {
        return getAllCursor(contentResolver, TogglContentProvider.CONTENT_URI_TAGS);
    }

    private static Cursor getAllCursor(ContentResolver contentResolver, Uri uri) {
        String[] mProjection = {
                TogglContentProvider._ID,
                TogglContentProvider.ID,
                TogglContentProvider.NAME,
                TogglContentProvider.AT
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = TogglContentProvider.NAME;
        Cursor mCursor = contentResolver.query(
                uri,
                mProjection,
                mSelectionClause,
                null,
                mSortOrder
        );
        if (mCursor != null) {
            return mCursor;
        }
        return null;
    }

    private static Cursor getElementCursor(ContentResolver contentResolver, Uri uri,
                                           String selectionClause, String[] selectionArgs) {
        String[] mProjection = {
                TogglContentProvider._ID,
                TogglContentProvider.ID,
                TogglContentProvider.NAME,
                TogglContentProvider.AT
        };
        String mSelectionClause = selectionClause;
        String[] mSelectionArgs = selectionArgs;
        String mSortOrder = TogglContentProvider.NAME;
        Cursor mCursor = contentResolver.query(
                uri,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                mSortOrder
        );
        if (mCursor != null) {
            return mCursor;
        }
        return null;
    }

    public static List<TogglProject> getAllProjectsList(ContentResolver contentResolver) {
        Cursor mCursor = getAllProjectsCursor(contentResolver);
        ArrayList<TogglProject> mElements = new ArrayList<>();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                Integer mId = mCursor.getInt(mCursor.getColumnIndex(TogglContentProvider.ID));
                String mName = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.NAME));
                String mAt = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.AT));

                TogglProject mProject = new TogglProject();
                mProject.id = mId;
                mProject.name = mName;
                mProject.at = mAt;
                mElements.add(mProject);
            }
        }
        return mElements;
    }

    public static List<TogglTag> getAllTagsList(ContentResolver contentResolver) {
        Cursor mCursor = getAllTagsCursor(contentResolver);
        ArrayList<TogglTag> mElements = new ArrayList<>();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                Integer mId = mCursor.getInt(mCursor.getColumnIndex(TogglContentProvider.ID));
                String mName = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.NAME));
                String mAt = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.AT));

                TogglTag mTag = new TogglTag();
                mTag.id = mId;
                mTag.name = mName;
                mTag.at = mAt;
                mElements.add(mTag);
            }
        }
        return mElements;
    }

    public static TogglProject getProjectElement(ContentResolver contentResolver, int id) {
        TogglProject mProject = null;
        String[] mSelectionArgs = {Integer.toString(id)};
        Cursor mCursor = getElementCursor(contentResolver,
                TogglContentProvider.CONTENT_URI_PROJECTS,
                TogglContentProvider.ID + " = ?",
                mSelectionArgs);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                Integer mId = mCursor.getInt(mCursor.getColumnIndex(TogglContentProvider.ID));
                String mName = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.NAME));
                String mAt = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.AT));

                mProject = new TogglProject();
                mProject.id = mId;
                mProject.name = mName;
                mProject.at = mAt;
            }
        }
        return mProject;
    }

    public static TogglTag getTagElement(ContentResolver contentResolver, int id) {
        TogglTag mTag = null;
        String[] mSelectionArgs = {Integer.toString(id)};
        Cursor mCursor = getElementCursor(contentResolver,
                TogglContentProvider.CONTENT_URI_TAGS,
                TogglContentProvider.ID + " = ?",
                mSelectionArgs);
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                Integer mId = mCursor.getInt(mCursor.getColumnIndex(TogglContentProvider.ID));
                String mName = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.NAME));
                String mAt = mCursor.getString(mCursor.getColumnIndex(TogglContentProvider.AT));

                mTag = new TogglTag();
                mTag.id = mId;
                mTag.name = mName;
                mTag.at = mAt;
            }
        }
        return mTag;
    }

    public static void insertProjectElement(ContentResolver contentResolver, TogglProject project) {
        ContentValues cv = new ContentValues();
        cv.put(TogglContentProvider.ID, project.id);
        cv.put(TogglContentProvider.NAME, project.name);
        cv.put(TogglContentProvider.AT, project.at);

        contentResolver.insert(
                TogglContentProvider.CONTENT_URI_PROJECTS, cv
        );
    }

    public static void insertTagElement(ContentResolver contentResolver, TogglTag tag) {
        ContentValues cv = new ContentValues();
        cv.put(TogglContentProvider.ID, tag.id);
        cv.put(TogglContentProvider.NAME, tag.name);
        cv.put(TogglContentProvider.AT, tag.at);

        contentResolver.insert(
                TogglContentProvider.CONTENT_URI_TAGS, cv
        );
    }

    public static void updateOrInsertProjectElement(ContentResolver contentResolver, TogglProject project) {
        ContentValues cv = new ContentValues();
        cv.put(TogglContentProvider.NAME, project.name);
        cv.put(TogglContentProvider.AT, project.at);

        String selection = TogglContentProvider.ID + " = ?";
        String[] selectionArgs = {Integer.toString(project.id)};

        if (getProjectElement(contentResolver, project.id) != null) {
            contentResolver.update(
                    TogglContentProvider.CONTENT_URI_PROJECTS,
                    cv,
                    selection,
                    selectionArgs
            );
        } else {
            insertProjectElement(contentResolver, project);
        }
    }

    public static void updateOrInsertTagElement(ContentResolver contentResolver, TogglTag tag) {
        ContentValues cv = new ContentValues();
        cv.put(TogglContentProvider.NAME, tag.name);
        cv.put(TogglContentProvider.AT, tag.at);

        String selection = TogglContentProvider.ID + " = ?";
        String[] selectionArgs = {Integer.toString(tag.id)};

        if (getTagElement(contentResolver, tag.id) != null) {
            contentResolver.update(
                    TogglContentProvider.CONTENT_URI_TAGS,
                    cv,
                    selection,
                    selectionArgs
            );
        } else {
            insertTagElement(contentResolver, tag);
        }
    }

    public static void deleteProjectElement(ContentResolver contentResolver, int id) {
        String selection = TogglContentProvider.ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        contentResolver.delete(
                TogglContentProvider.CONTENT_URI_PROJECTS,
                selection,
                selectionArgs
        );
    }

    public static void deleteTagElement(ContentResolver contentResolver, int id) {
        String selection = TogglContentProvider.ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        contentResolver.delete(
                TogglContentProvider.CONTENT_URI_TAGS,
                selection,
                selectionArgs
        );
    }
}
