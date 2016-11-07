package com.yuddi.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Mauricio on 11/7/2016.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "inventory.db";
    
    private static final int DATABASE_VERSION = 1;
    
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + "("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_INVENTORY_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_INVENTORY_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_INVENTORY_PICTURE + " BLOB, "
                + InventoryEntry.COLUMN_INVENTORY_DESCRIPTION + " TEXT, "
                + InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE + " INTEGER, "
                + InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL + " TEXT);";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so the database won't be upgraded.
    }
}
