package com.yuddi.inventoryapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

public class DetailActivity extends AppCompatActivity {

    private ImageView mPictureImageView;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mDescriptionEditText;
    private EditText mPhoneEditText;
    private EditText mEmailEditText;

    private int oldImage = 0;
    private String oldName = "";
    private int oldQuantity = 0;
    private int oldPrice = 0;
    private String oldDescription = "";
    private int oldPhone = 0;
    private String oldEmail = "";

    private int newImage;
    private String newName;
    private int newQuantity;
    private int newPrice;
    private String newDescription;
    private int newPhone;
    private String newEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle(getString(R.string.add_product));

        mPictureImageView = (ImageView) findViewById(R.id.detail_picture_imageview);
        mNameEditText = (EditText) findViewById(R.id.detail_name_edittext);
        mQuantityEditText = (EditText) findViewById(R.id.detail_quantity_edittext);
        mPriceEditText = (EditText) findViewById(R.id.detail_price_edittext);
        mDescriptionEditText = (EditText) findViewById(R.id.detail_description_edittext);
        mPhoneEditText = (EditText) findViewById(R.id.detail_phone_edittext);
        mEmailEditText = (EditText) findViewById(R.id.detail_email_edittext);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean productHasChanged() {

        newName = mNameEditText.getText().toString().trim();

        String quantityText = mQuantityEditText.getText().toString();
        quantityText = quantityText.isEmpty() ? "0" : quantityText;
        newQuantity = Integer.parseInt(quantityText);

        String priceText = mPriceEditText.getText().toString();
        priceText = priceText.isEmpty() ? "0" : priceText;
        newPrice = (int)(Float.parseFloat(priceText) * 100);

        newDescription = mDescriptionEditText.getText().toString();

        String phoneText = mPhoneEditText.getText().toString();
        phoneText = phoneText.isEmpty() ? "0" : phoneText;
        newPhone = Integer.parseInt(phoneText);

        newEmail = mEmailEditText.getText().toString().trim();

        return (!newName.equals(oldName)) || (newQuantity != oldQuantity)
                || (newPrice != oldPrice) || (!newDescription.equals(oldDescription))
                || (newPhone != oldPhone) || (!newEmail.equals(oldEmail));

    }

    private void saveProduct() {

        if (!productHasChanged()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_NAME, newName);
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
        values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, newPrice);
        values.put(InventoryEntry.COLUMN_INVENTORY_DESCRIPTION, newDescription);
        values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_PHONE, newPhone);
        values.put(InventoryEntry.COLUMN_INVENTORY_SUPPLIER_EMAIL, newEmail);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, R.string.detail_add_product_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.detail_add_product_successful, Toast.LENGTH_SHORT).show();
        }
    }
}
