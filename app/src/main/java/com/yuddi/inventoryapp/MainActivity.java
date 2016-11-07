package com.yuddi.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InventoryCursorAdapter mCursorAdapter;

    private static final int INVENTORY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView productList = (ListView) findViewById(R.id.product_list);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        productList.setAdapter(mCursorAdapter);

        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_new_product:
                insertNewProduct();
                return true;
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_data:
                deleteData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertNewProduct() {

    }

    private void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, "Earphone");
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, 5);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, 1000);

        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    private void deleteData() {
        getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case INVENTORY_LOADER:

                String[] projection = {
                        InventoryEntry._ID,
                        InventoryEntry.COLUMN_INVENTORY_NAME,
                        InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                        InventoryEntry.COLUMN_INVENTORY_PRICE
                };

                return new CursorLoader(
                        this,
                        InventoryEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
