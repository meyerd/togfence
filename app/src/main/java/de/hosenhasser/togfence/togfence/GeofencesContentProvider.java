package de.hosenhasser.togfence.togfence;

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
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import de.hosenhasser.togfence.togfence.TogfenceApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeofencesContentProvider extends ContentProvider {
    static final public String PROVIDER_NAME = "de.hosenhasser.togfence.togfence.geofences_content_provider";
    static final public String URL = "content://" + PROVIDER_NAME + "/geofences";

    static final public Uri CONTENT_URI = Uri.parse(URL);
    static final public String _ID = "_id";
    static final public String NAME = "name";
    static final public String LAT = "lat";
    static final public String LON = "lon";
    static final public String RADIUS = "radius";
    static final public String ACTIVE = "active";
    static final public String TOGGL_PROJECT = "toggl_project";
    static final public String TOGGL_PROJECT_TEXT = "toggl_project_text";
    static final public String TOGGL_TAGS = "toggl_tags";
    static final public String TOGGL_TAGS_TEXT = "toggl_tags_text";
    static final public String RUNNING_ENTRY_ID = "running_entry_id";

    static final int GEOFENCES = 1;
    static final int GEOFENCE_ID = 2;

    private static HashMap<String, String> GEOFENCES_PROJECTION_MAP;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "geofences", GEOFENCES);
        uriMatcher.addURI(PROVIDER_NAME, "geofences/#", GEOFENCE_ID);
    }

    static final String DATABASE_NAME = "geofences";
    static final String GEOFENCES_TABLE_NAME = "geofences";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + GEOFENCES_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " name TEXT NOT NULL, " +
                    " lat REAL NOT NULL," +
                    " lon REAL NOT NULL," +
                    " radius NOT NULL," +
                    " active BOOLEAN NOT NULL," +
                    " toggl_project INTEGER," +
                    " toggl_project_text TEXT," +
                    " toggl_tags INTEGER," +
                    " toggl_tags_text TEXT," +
                    " running_entry_id INTEGER" +
                    ");";

    static final String FILL_INITIAL_DATA =
            "INSERT INTO " + GEOFENCES_TABLE_NAME +
                "(_id, name, lat, lon, radius, active, toggl_project, toggl_project_text, toggl_tags, toggl_tags_text, running_entry_id)" +
                "VALUES (0, \"test\", 48.7648, 9.27025, 100, 1, 0, \"test-project\", 0, \"test-tag\", -1)," +
                "(1, \"test-deact\", 50.00, 10.00, 100, 0, 0, \"test-project\", 0, \"test-tag\", -1);";

    private static class GeofencesDatabaseHelper extends SQLiteOpenHelper {
        GeofencesDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
            db.execSQL(FILL_INITIAL_DATA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static volatile GeofencesContentProvider sGeofencesContentProviderSingletonInstance;
    private Context context;
    private GeofencesDatabaseHelper mOpenHelper;

    public static GeofencesContentProvider getInstance() {
        if (sGeofencesContentProviderSingletonInstance == null) {
            synchronized (GeofencesContentProvider.class) {
                if (sGeofencesContentProviderSingletonInstance == null) sGeofencesContentProviderSingletonInstance = new GeofencesContentProvider();
            }
        }

        return sGeofencesContentProviderSingletonInstance;
    }

    protected GeofencesContentProvider readResolve() {
        return getInstance();
    }

    public GeofencesContentProvider() {

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new GeofencesDatabaseHelper(getContext());
        return mOpenHelper != null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)){
            case GEOFENCES:
                count = db.delete(GEOFENCES_TABLE_NAME, selection, selectionArgs);
                break;

            case GEOFENCE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( GEOFENCES_TABLE_NAME, _ID +  " = " + id +
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
            case GEOFENCES:
                return "vnd.android.cursor.dir/vnd.togfence.geofences";
            case GEOFENCE_ID:
                return "vnd.android.cursor.item/vnd.togfence.geofences";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID = db.insert(	GEOFENCES_TABLE_NAME, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(GEOFENCES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case GEOFENCES:
                qb.setProjectionMap(GEOFENCES_PROJECTION_MAP);
                break;

            case GEOFENCE_ID:
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
            case GEOFENCES:
                count = db.update(GEOFENCES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case GEOFENCE_ID:
                count = db.update(GEOFENCES_TABLE_NAME, values,
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

    public static Cursor getAllGeofenceElementsCursor(ContentResolver contentResolver) {
        ArrayList<GeofenceElement> mGeofenceElements = new ArrayList<>();
        String[] mProjection = {
                GeofencesContentProvider._ID,
                GeofencesContentProvider.NAME,
                GeofencesContentProvider.LAT,
                GeofencesContentProvider.LON,
                GeofencesContentProvider.RADIUS,
                GeofencesContentProvider.ACTIVE,
                GeofencesContentProvider.TOGGL_PROJECT,
                GeofencesContentProvider.TOGGL_PROJECT_TEXT,
                GeofencesContentProvider.TOGGL_TAGS,
                GeofencesContentProvider.TOGGL_TAGS_TEXT,
                GeofencesContentProvider.RUNNING_ENTRY_ID
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = GeofencesContentProvider.NAME;
        Cursor mCursor = contentResolver.query(
                GeofencesContentProvider.CONTENT_URI,
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

    // TODO: refactor code duplication
    public static List<GeofenceElement> getAllGeofenceElementsList(ContentResolver contentResolver) {
        ArrayList<GeofenceElement> mGeofenceElements = new ArrayList<>();
        String[] mProjection = {
                GeofencesContentProvider._ID,
                GeofencesContentProvider.NAME,
                GeofencesContentProvider.LAT,
                GeofencesContentProvider.LON,
                GeofencesContentProvider.RADIUS,
                GeofencesContentProvider.ACTIVE,
                GeofencesContentProvider.TOGGL_PROJECT,
                GeofencesContentProvider.TOGGL_PROJECT_TEXT,
                GeofencesContentProvider.TOGGL_TAGS,
                GeofencesContentProvider.TOGGL_TAGS_TEXT,
                GeofencesContentProvider.RUNNING_ENTRY_ID
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = GeofencesContentProvider.NAME;
        Cursor mCursor = contentResolver.query(
                GeofencesContentProvider.CONTENT_URI,
                mProjection,
                mSelectionClause,
                null,
                mSortOrder
        );
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                Integer mId = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider._ID)
                );
                String mName = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.NAME)
                );
                Float mLat = mCursor.getFloat(
                        mCursor.getColumnIndex(GeofencesContentProvider.LAT)
                );
                Float mLon = mCursor.getFloat(
                        mCursor.getColumnIndex(GeofencesContentProvider.LON)
                );
                LatLng mPosition = new LatLng(mLat, mLon);
                int mRadius = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.RADIUS)
                );
                int mTogglProject = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT)
                );
                String mTogglProjectText = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT_TEXT)
                );
                int mTogglTags = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS)
                );
                String mTogglTagsText = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS_TEXT)
                );
                int mRunningEntryId = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.RUNNING_ENTRY_ID)
                );
                boolean mActive =
                        (mCursor.getInt(
                                mCursor.getColumnIndex(GeofencesContentProvider.ACTIVE)
                        ) == 1);
                mGeofenceElements.add(
                        new GeofenceElement(
                                mId, mName, mPosition, mRadius, mTogglProject, mTogglProjectText,
                                mTogglTags, mTogglTagsText, mRunningEntryId, mActive
                        )
                );
            }
        }
        return mGeofenceElements;
    }

    public static GeofenceElement getGeofenceElement(ContentResolver contentResolver, int id) {
        GeofenceElement mGeofenceElement = null;
        String[] mProjection = {
                GeofencesContentProvider._ID,
                GeofencesContentProvider.NAME,
                GeofencesContentProvider.LAT,
                GeofencesContentProvider.LON,
                GeofencesContentProvider.RADIUS,
                GeofencesContentProvider.ACTIVE,
                GeofencesContentProvider.TOGGL_PROJECT,
                GeofencesContentProvider.TOGGL_PROJECT_TEXT,
                GeofencesContentProvider.TOGGL_TAGS,
                GeofencesContentProvider.TOGGL_TAGS_TEXT,
                GeofencesContentProvider.RUNNING_ENTRY_ID
        };
        String mSelectionClause = GeofencesContentProvider._ID + " = ?";
        String[] mSelectionArgs = {Integer.toString(id)};
        String mSortOrder = GeofencesContentProvider.NAME;
        Cursor mCursor = contentResolver.query(
                GeofencesContentProvider.CONTENT_URI,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                mSortOrder
        );
        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                Integer mId = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider._ID)
                );
                String mName = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.NAME)
                );
                Float mLat = mCursor.getFloat(
                        mCursor.getColumnIndex(GeofencesContentProvider.LAT)
                );
                Float mLon = mCursor.getFloat(
                        mCursor.getColumnIndex(GeofencesContentProvider.LON)
                );
                LatLng mPosition = new LatLng(mLat, mLon);
                int mRadius = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.RADIUS)
                );
                int mTogglProject = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT)
                );
                String mTogglProjectText = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT_TEXT)
                );
                int mTogglTags = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS)
                );
                String mTogglTagsText = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS_TEXT)
                );
                int mRunningEntryId = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.RUNNING_ENTRY_ID)
                );
                boolean mActive =
                        (mCursor.getInt(
                                mCursor.getColumnIndex(GeofencesContentProvider.ACTIVE)
                        ) == 1);
                mGeofenceElement = new GeofenceElement(
                        mId, mName, mPosition, mRadius, mTogglProject, mTogglProjectText,
                        mTogglTags, mTogglTagsText, mRunningEntryId, mActive);
            }
        }
        return mGeofenceElement;
    }

    public static void insertGeofenceElement(ContentResolver contentResolver, GeofenceElement ge) {
        ContentValues cv = new ContentValues();
        cv.put(GeofencesContentProvider.NAME, ge.name);
        cv.put(GeofencesContentProvider.LAT, ge.position.latitude);
        cv.put(GeofencesContentProvider.LON, ge.position.longitude);
        cv.put(GeofencesContentProvider.RADIUS, ge.radius);
        cv.put(GeofencesContentProvider.ACTIVE, ge.active);
        cv.put(GeofencesContentProvider.TOGGL_PROJECT, ge.toggl_project);
        cv.put(GeofencesContentProvider.TOGGL_PROJECT_TEXT, ge.toggl_project_text);
        cv.put(GeofencesContentProvider.TOGGL_TAGS, ge.toggl_tag);
        cv.put(GeofencesContentProvider.TOGGL_TAGS_TEXT, ge.toggl_tag_text);
        cv.put(GeofencesContentProvider.RUNNING_ENTRY_ID, ge.running_entry_id);

        contentResolver.insert(
                GeofencesContentProvider.CONTENT_URI, cv
        );
    }

    public static void updateGeofenceElement(ContentResolver contentResolver, GeofenceElement ge) {
        ContentValues cv = new ContentValues();
        cv.put(GeofencesContentProvider.NAME, ge.name);
        cv.put(GeofencesContentProvider.LAT, ge.position.latitude);
        cv.put(GeofencesContentProvider.LON, ge.position.longitude);
        cv.put(GeofencesContentProvider.RADIUS, ge.radius);
        cv.put(GeofencesContentProvider.ACTIVE, ge.active);
        cv.put(GeofencesContentProvider.TOGGL_PROJECT, ge.toggl_project);
        cv.put(GeofencesContentProvider.TOGGL_PROJECT_TEXT, ge.toggl_project_text);
        cv.put(GeofencesContentProvider.TOGGL_TAGS, ge.toggl_tag);
        cv.put(GeofencesContentProvider.TOGGL_TAGS_TEXT, ge.toggl_tag_text);
        cv.put(GeofencesContentProvider.RUNNING_ENTRY_ID, ge.running_entry_id);

        String selection = GeofencesContentProvider._ID + " = ?";
        String[] selectionArgs = {Integer.toString(ge._id)};

        contentResolver.update(
                GeofencesContentProvider.CONTENT_URI,
                cv,
                selection,
                selectionArgs
        );
    }

    public static void deleteGeofenceElement(ContentResolver contentResolver, int id) {
        String selection = GeofencesContentProvider._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        contentResolver.delete(
                GeofencesContentProvider.CONTENT_URI,
                selection,
                selectionArgs
        );
    }
}
