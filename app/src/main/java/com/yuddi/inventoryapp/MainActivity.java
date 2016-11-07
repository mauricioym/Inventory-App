package com.yuddi.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView productList = (ListView) findViewById(R.id.product_list);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        productList.setAdapter(mCursorAdapter);

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
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertNewProduct() {
        
    }

}
