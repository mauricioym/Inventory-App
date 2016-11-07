package com.yuddi.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mauricio on 11/7/2016.
 */

public class InventoryContract {

    private static final String CONTENT_AUTHORITY = "com.yuddi.inventoryapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_INVENTORY = "inventory";

    private InventoryContract() {
        throw new AssertionError("No InventoryContract instances for you!");
    }

    public static abstract class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String TABLE_NAME = PATH_INVENTORY;

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_INVENTORY_NAME = "name";
        public static final String COLUMN_INVENTORY_QUANTITY = "quantity";
        public static final String COLUMN_INVENTORY_PRICE = "price";
        public static final String COLUMN_INVENTORY_PICTURE = "picture";
        public static final String COLUMN_INVENTORY_DESCRIPTION = "description";
        public static final String COLUMN_INVENTORY_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_INVENTORY_SUPPLIER_EMAIL = "supplier_email";

    }

}
