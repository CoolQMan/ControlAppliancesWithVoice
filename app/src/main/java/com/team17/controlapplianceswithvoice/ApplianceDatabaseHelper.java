package com.team17.controlapplianceswithvoice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ApplianceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "appliances.db";
    private static final int DATABASE_VERSION = 1;

    // Table and Column Names
    private static final String TABLE_APPLIANCES = "appliances";
    private static final String COLUMN_ID = "appliance_id";
    private static final String COLUMN_NAME = "appliance_name";
    private static final String COLUMN_STATUS = "status";

    public ApplianceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_APPLIANCES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_STATUS + " INTEGER DEFAULT 0);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLIANCES);
        onCreate(db);
    }

    // 1. Add an Appliance
    public long addAppliance(ApplianceModel appliance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, appliance.getApplianceName());
        values.put(COLUMN_STATUS, appliance.getStatus() ? 1 : 0);
        long result = db.insert(TABLE_APPLIANCES, null, values);
        db.close();
        return result;
    }

    // 2. Remove an Appliance by ID
    public int removeAppliance(int applianceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_APPLIANCES, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)});
        db.close();
        return rowsDeleted;
    }

    // 3. Edit Appliance Name by ID
    public int editApplianceName(int applianceId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        int rowsUpdated = db.update(TABLE_APPLIANCES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)});
        db.close();
        return rowsUpdated;
    }

    // 4. Toggle Appliance Status
    public int toggleApplianceStatus(int applianceId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Fetch current status
        Cursor cursor = db.query(TABLE_APPLIANCES, new String[]{COLUMN_STATUS}, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)}, null, null, null);
        if (cursor != null && ((android.database.Cursor) cursor).moveToFirst()) {
            int currentStatus = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            int newStatus = currentStatus == 1 ? 0 : 1; // Toggle the status

            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, newStatus);

            int rowsUpdated = db.update(TABLE_APPLIANCES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)});
            cursor.close();
            db.close();
            return rowsUpdated;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return 0; // No rows updated
    }

    // 5. Fetch All Appliances
    public ArrayList<ApplianceModel> getAllAppliances() {
        ArrayList<ApplianceModel> applianceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_APPLIANCES, null, null, null, null, null, COLUMN_ID + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                boolean status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)) == 1;

                ApplianceModel appliance = new ApplianceModel(id, name, status);
                applianceList.add(appliance);
            }
            cursor.close();
        }

        db.close();
        return applianceList;
    }
}
