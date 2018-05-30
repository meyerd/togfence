package de.hosenhasser.togfence.togfence;

import android.content.ContentProvider;
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

import java.util.HashMap;

public class GeofencesContentProvider extends ContentProvider {
    static final public String PROVIDER_NAME = "geofences_content_provider"
    static final public String URL = "content://" + PROVIDER_NAME + "/geofences";

    static final public Uri CONTENT_URI = Uri.parse(URL);
    static final public String _ID = "_id";
    static final public String NAME = "name";
    static final public String LAT = "lat";
    static final public String LON = "lon";
    static final public String RADIUS = "radius";
    static final public String ACTIVE = "active";
    static final public String TOGGL_PROJECT = "toggl_project";
    static final public String TOGGL_TAGS = "toggl_tags";

    static final int GEOFENCES = 1;
    static final int GEOFENCE_ID = 2;

    private static HashMap<String, String> GEOFENCES_PROJECTION_MAP;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "geofences", GEOFENCES);
        uriMatcher.addURI(PROVIDER_NAME, "geofences/#", GEOFENCE_ID);
    }

    private SQLiteDatabase db;
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
                    " toggl_project TEXT NOT NULL," +
                    " toggl_tags TEXT" +
                    ");";

    private static class GeofencesDatabaseHelper extends SQLiteOpenHelper {
        GeofencesDatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public GeofencesContentProvider() {

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        GeofencesDatabaseHelper dbHelper = new GeofencesDatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
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
}
