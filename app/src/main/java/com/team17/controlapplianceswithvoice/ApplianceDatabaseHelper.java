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
                COLUMN_ID + " INTEGER PRIMARY KEY , " +
                COLUMN_NAME + " TEXT NOT NULL UNIQUE, " + // Ensure unique appliance names
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

        // Check if appliance name or ID already exists
        if (applianceExists(appliance.getApplianceName()) || applianceExists(appliance.getApplianceId())) {
            return -1; // Error code indicating duplicate name or ID
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, appliance.getApplianceId()); // Adding ID
        values.put(COLUMN_NAME, appliance.getApplianceName());
        values.put(COLUMN_STATUS, appliance.getStatus() ? 1 : 0);

        return db.insert(TABLE_APPLIANCES, null, values);
    }


    // 2. Remove an Appliance by ID
    public int removeAppliance(int applianceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_APPLIANCES, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)});
    }

    // 3. Edit Appliance Name by ID
    public int editApplianceName(int applianceId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if new name already exists
        if (applianceExists(newName)) {
            return -1; // Error code indicating duplicate name
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);

        return db.update(TABLE_APPLIANCES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)});
    }

    // 4. Toggle Appliance Status
    public void toggleApplianceStatus(int applianceId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_APPLIANCES, new String[]{COLUMN_STATUS}, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int currentStatus = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            int newStatus = currentStatus == 1 ? 0 : 1;

            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, newStatus);

            int rowsUpdated = db.update(TABLE_APPLIANCES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)});
            cursor.close();
            return;
        }

        if (cursor != null) {
            cursor.close();
        }
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

                applianceList.add(new ApplianceModel(id, name, status));
            }
            cursor.close();
        }
        return applianceList;
    }

    // 6. Check if an appliance name already exists
    public boolean applianceExists(String applianceName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPLIANCES, new String[]{COLUMN_ID}, COLUMN_NAME + " = ?", new String[]{applianceName}, null, null, null);

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    public boolean applianceExists(int applianceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_APPLIANCES, new String[]{COLUMN_ID}, COLUMN_ID + " = ?", new String[]{String.valueOf(applianceId)}, null, null, null);

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // 7. Delete all appliances
    public int deleteAllAppliances() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_APPLIANCES, null, null);
    }
}
