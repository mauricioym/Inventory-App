package com.yuddi.inventoryapp;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

        View emptyView = findViewById(R.id.empty_view);
        productList.setEmptyView(emptyView);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

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
            case R.id.action_add_product:
                addProduct();
                return true;
            case R.id.action_delete_data:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addProduct() {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_all_message);
        builder.setPositiveButton(R.string.dialog_delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteData();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
