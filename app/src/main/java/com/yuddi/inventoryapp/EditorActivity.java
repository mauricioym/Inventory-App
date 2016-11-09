package com.yuddi.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView mPictureImageView;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mDescriptionEditText;
    private EditText mPhoneEditText;
    private EditText mEmailEditText;

    private boolean imageChanged = false;
    private String oldName = "";
    private int oldQuantity = 0;
    private int oldPrice = 0;
    private String oldDescription = "";
    private String oldPhone = "";
    private String oldEmail = "";

    private Bitmap picture;
    private String newName;
    private int newQuantity;
    private int newPrice;
    private String newDescription;
    private String newPhone;
    private String newEmail;

    private Uri mCurrentProductUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int EXISTING_PRODUCT_LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentProductUri = getIntent().getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.title_add_product));
        } else {
            setTitle(getString(R.string.edit_product_label));
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mPictureImageView = (ImageView) findViewById(R.id.editor_picture_imageview);
        mNameEditText = (EditText) findViewById(R.id.editor_name_edittext);
        mQuantityEditText = (EditText) findViewById(R.id.editor_quantity_edittext);
        mPriceEditText = (EditText) findViewById(R.id.editor_price_edittext);
        mDescriptionEditText = (EditText) findViewById(R.id.editor_description_edittext);
        mPhoneEditText = (EditText) findViewById(R.id.editor_phone_edittext);
        mEmailEditText = (EditText) findViewById(R.id.editor_email_edittext);

        mPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a picture from"), PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                picture = scaleBitmap(bitmap);
                mPictureImageView.setImageBitmap(picture);
                mPictureImageView.setBackgroundColor(Color.TRANSPARENT);
                imageChanged = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int height = mPictureImageView.getHeight();
        int width = height * bitmap.getWidth() / bitmap.getHeight();

        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                if (mCurrentProductUri != null) {
                    goToDetail();
                }
                finish();
                return true;
            case R.id.action_cancel:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToDetail() {
        Intent intent = new Intent(EditorActivity.this, DetailActivity.class);
        intent.setData(mCurrentProductUri);
        startActivity(intent);
    }

    private boolean productHasChanged() {

        newName = mNameEditText.getText().toString().trim();

        String quantityText = mQuantityEditText.getText().toString();
        newQuantity = quantityText.isEmpty() ? 0 : Integer.parseInt(quantityText);

        String priceText = mPriceEditText.getText().toString();
        newPrice = priceText.isEmpty() ? 0 : (int)(Float.parseFloat(priceText) * 100);

        newDescription = mDescriptionEditText.getText().toString();

        newPhone = mPhoneEditText.getText().toString();

        newEmail = mEmailEditText.getText().toString().trim();

        return (!newName.equals(oldName)) || (newQuantity != oldQuantity)
                || (newPrice != oldPrice) || (!newDescription.equals(oldDescription))
                || (!newPhone.equals(oldPhone)) || (!newEmail.equals(oldEmail))
                || imageChanged;

    }

    private void saveProduct() {

        if (!productHasChanged()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_PICTURE, convertToByteArray(picture));
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, newName);
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, newPrice);
        values.put(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION, newDescription);
        values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE, newPhone);
        values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL, newEmail);

        if (mCurrentProductUri == null){
            // Add product
            try {
                mCurrentProductUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
                if (mCurrentProductUri == null) {
                    Toast.makeText(this, R.string.editor_add_product_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.editor_add_product_successful, Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, getString(R.string.failed, e.getMessage()), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Edit Product
            try {
                int rowsUpdated = getContentResolver().update(mCurrentProductUri, values, null, null);
                if (rowsUpdated == 0) {
                    Toast.makeText(this, R.string.editor_edit_product_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.editor_edit_product_successful, Toast.LENGTH_SHORT).show();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, getString(R.string.failed, e.getMessage()), Toast.LENGTH_SHORT).show();
            }
        }

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
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL);
            int pictureColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PICTURE);

            oldName = cursor.getString(nameColumnIndex);
            oldQuantity = cursor.getInt(quantityColumnIndex);
            oldPrice = cursor.getInt(priceColumnIndex);
            oldDescription = cursor.getString(descriptionColumnIndex);
            oldPhone = cursor.getString(phoneColumnIndex);
            oldEmail = cursor.getString(emailColumnIndex);
            picture = convertToBitmap(cursor.getBlob(pictureColumnIndex));

            mNameEditText.setText(oldName);
            mQuantityEditText.setText(String.valueOf(oldQuantity));
            mPriceEditText.setText(String.valueOf(oldPrice / 100f));
            mDescriptionEditText.setText(oldDescription);
            mPhoneEditText.setText(oldPhone);
            mEmailEditText.setText(oldEmail);
            if(picture != null) {
                mPictureImageView.setImageBitmap(picture);
                mPictureImageView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mDescriptionEditText.setText("");
        mPhoneEditText.setText("");
        mEmailEditText.setText("");
        mPictureImageView.setImageBitmap(null);
    }

    private byte[] convertToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private Bitmap convertToBitmap(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
