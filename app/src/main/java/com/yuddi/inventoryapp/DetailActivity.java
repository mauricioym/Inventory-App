package com.yuddi.inventoryapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentProductUri;

    private static final int EXISTING_PRODUCT_LOADER = 1;

    private Button mOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCurrentProductUri = getIntent().getData();

        getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        mOrderButton = (Button) findViewById(R.id.detail_order_button);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                goToEditor();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToEditor() {
        Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
        intent.setData(mCurrentProductUri);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case EXISTING_PRODUCT_LOADER:
                return new CursorLoader(
                        this,
                        mCurrentProductUri,
                        null,
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

        if (cursor.moveToFirst()) {
            int pictureColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PICTURE);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL);

            Bitmap picture = convertToBitmap(cursor.getBlob(pictureColumnIndex));
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String email = cursor.getString(emailColumnIndex);

            ImageView pictureImageView = (ImageView) findViewById(R.id.detail_picture_imageview);
            TextView nameTextView = (TextView) findViewById(R.id.detail_name_textview);
            TextView quantityTextView = (TextView) findViewById(R.id.detail_quantity_textview);
            TextView priceTextView = (TextView) findViewById(R.id.detail_price_textview);
            TextView descriptionLabel = (TextView) findViewById(R.id.detail_description_label);
            TextView descriptionTextView = (TextView) findViewById(R.id.detail_description_textview);
            TextView phoneLabel = (TextView) findViewById(R.id.detail_phone_label);
            TextView phoneTextView = (TextView) findViewById(R.id.detail_phone_textview);
            TextView emailLabel = (TextView) findViewById(R.id.detail_email_label);
            TextView emailTextView = (TextView) findViewById(R.id.detail_email_textview);

            if (picture != null) pictureImageView.setImageBitmap(picture);
            nameTextView.setText(name);
            quantityTextView.setText(String.valueOf(quantity));
            priceTextView.setText(NumberFormat.getCurrencyInstance().format(price / 100f));
            if (description == null || description.isEmpty()) {
                descriptionLabel.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
            } else {
                descriptionTextView.setText(description);
            }
            if (phone == null || phone.isEmpty()) {
                phoneLabel.setVisibility(View.GONE);
                phoneTextView.setVisibility(View.GONE);
            } else {
                phoneTextView.setText(String.valueOf(phone));
                mOrderButton.setEnabled(true);
            }
            if (email == null || email.isEmpty()) {
                emailLabel.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
            } else {
                emailTextView.setText(email);
                mOrderButton.setEnabled(true);
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ImageView pictureImageView = (ImageView) findViewById(R.id.detail_picture_imageview);
        TextView nameTextView = (TextView) findViewById(R.id.detail_name_textview);
        TextView quantityTextView = (TextView) findViewById(R.id.detail_quantity_textview);
        TextView priceTextView = (TextView) findViewById(R.id.detail_price_textview);
        TextView descriptionTextView = (TextView) findViewById(R.id.detail_description_textview);
        TextView phoneTextView = (TextView) findViewById(R.id.detail_phone_textview);
        TextView emailTextView = (TextView) findViewById(R.id.detail_email_textview);

        pictureImageView.setImageBitmap(null);
        nameTextView.setText("");
        quantityTextView.setText("");
        priceTextView.setText("");
        descriptionTextView.setText("");
        phoneTextView.setText("");
        emailTextView.setText("");
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.editor_delete_product_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_delete_product_successful, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private Bitmap convertToBitmap(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
