package com.yuddi.inventoryapp;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentProductUri;

    private static final int EXISTING_PRODUCT_LOADER = 1;

    private Button mOrderButton;
    private String mPhone;
    private String mEmail;
    private String mProductName;
    private int mQuantity;

    private TextView mQuantityTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mCurrentProductUri = getIntent().getData();

        getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        mQuantityTextView = (TextView) findViewById(R.id.detail_quantity_textview);

        mOrderButton = (Button) findViewById(R.id.detail_order_button);
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderButtonClicked();
            }
        });

        Button saleButton = (Button) findViewById(R.id.detail_sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaleAlertDialog();
            }
        });

        Button shipmentButton = (Button) findViewById(R.id.detail_shipment_button);
        shipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShipmentAlertDialog();
            }
        });

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
            mProductName = cursor.getString(nameColumnIndex);
            mQuantity = cursor.getInt(quantityColumnIndex);
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
            nameTextView.setText(mProductName);
            quantityTextView.setText(String.valueOf(mQuantity));
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
                mPhone = phone;
            }
            if (email == null || email.isEmpty()) {
                emailLabel.setVisibility(View.GONE);
                emailTextView.setVisibility(View.GONE);
            } else {
                emailTextView.setText(email);
                mOrderButton.setEnabled(true);
                mEmail = email;
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ImageView pictureImageView = (ImageView) findViewById(R.id.detail_picture_imageview);
        TextView nameTextView = (TextView) findViewById(R.id.detail_name_textview);
        TextView priceTextView = (TextView) findViewById(R.id.detail_price_textview);
        TextView descriptionTextView = (TextView) findViewById(R.id.detail_description_textview);
        TextView phoneTextView = (TextView) findViewById(R.id.detail_phone_textview);
        TextView emailTextView = (TextView) findViewById(R.id.detail_email_textview);

        pictureImageView.setImageBitmap(null);
        nameTextView.setText("");
        mQuantityTextView.setText("");
        priceTextView.setText("");
        descriptionTextView.setText("");
        phoneTextView.setText("");
        emailTextView.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_message);
        builder.setPositiveButton(R.string.dialog_delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
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

    private void showSaleAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle(R.string.dialog_sale_title)
                .setView(getLayoutInflater().inflate(R.layout.sale_shipment_dialog, null))
                .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText quantityEditText = (EditText)((AlertDialog) dialogInterface).findViewById(R.id.dialog_quantity_edittext);
                        String quantityText = quantityEditText.getText().toString();
                        int quantityForSale = quantityText.isEmpty() ? 0 : Integer.parseInt(quantityText);
                        
                        if (quantityForSale <= 0) {
                            Toast.makeText(DetailActivity.this, R.string.toast_no_sale_made, Toast.LENGTH_SHORT).show();
                        } else if (quantityForSale <= mQuantity) {
                            int newQuantity = mQuantity - quantityForSale;

                            ContentValues values = new ContentValues();
                            values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
                            getContentResolver().update(mCurrentProductUri, values, null, null);

                            mQuantityTextView.setText(String.valueOf(newQuantity));

                            Toast.makeText(DetailActivity.this, R.string.toast_sale_was_made, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(DetailActivity.this, R.string.toast_not_enough, Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(DetailActivity.this, R.string.toast_sale_was_cancelled, Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showShipmentAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle(R.string.dialog_shipment_title)
                .setView(getLayoutInflater().inflate(R.layout.sale_shipment_dialog, null))
                .setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText quantityEditText = (EditText)((AlertDialog) dialogInterface).findViewById(R.id.dialog_quantity_edittext);
                        String quantityText = quantityEditText.getText().toString();
                        int quantity = quantityText.isEmpty() ? 0 : Integer.parseInt(quantityText);

                        if (quantity > 0) {
                            int newQuantity = mQuantity + quantity;

                            ContentValues values = new ContentValues();
                            values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
                            getContentResolver().update(mCurrentProductUri, values, null, null);

                            mQuantityTextView.setText(String.valueOf(newQuantity));

                            Toast.makeText(DetailActivity.this, R.string.toast_shipment_received, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(DetailActivity.this, R.string.toast_no_shipment_received, Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(DetailActivity.this, R.string.toast_shipment_cancelled, Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void orderButtonClicked() {
        final Map<String, Intent> intents = new HashMap<>();
        final String PHONE_KEY = "Phone";
        final String EMAIL_KEY = "Email";

        if (mPhone != null) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.fromParts("tel", mPhone, null));

            intents.put(PHONE_KEY, phoneIntent);
        }
        if (mEmail != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.fromParts("mailto", mEmail, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, mProductName);

            intents.put(EMAIL_KEY, emailIntent);
        }

        if (intents.size() == 1) {
            Intent intent;
            if (intents.containsKey(PHONE_KEY)) {
                intent = intents.get(PHONE_KEY);
            } else {
                intent = intents.get(EMAIL_KEY);
            }

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(DetailActivity.this, R.string.error_ordering, Toast.LENGTH_SHORT).show();
            }

        } else if (intents.size() == 2) {

            final String[] keys = new String[]{PHONE_KEY, EMAIL_KEY};

            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setTitle(R.string.order_by)
                    .setItems(keys, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = intents.get(keys[i]);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(DetailActivity.this, R.string.error_ordering, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

}
