package com.yuddi.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yuddi.inventoryapp.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;

/**
 * Created by Mauricio on 11/7/2016.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(R.id.list_item_name);
        holder.quantity = (TextView) view.findViewById(R.id.list_item_quantity);
        holder.price = (TextView) view.findViewById(R.id.list_item_price);
        holder.sale = (Button) view.findViewById(R.id.list_item_sale);
        holder.sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.nameColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_NAME);
        holder.quantityColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        holder.priceColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_PRICE);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        String productName = cursor.getString(holder.nameColumnIndex);
        int quantity = cursor.getInt(holder.quantityColumnIndex);
        float price = cursor.getInt(holder.priceColumnIndex) / 100f;

        holder.name.setText(productName);
        holder.quantity.setText(context.getString(R.string.stock_quantity, quantity));
        holder.price.setText(NumberFormat.getCurrencyInstance().format(price));
    }

    private static class ViewHolder {
        TextView name;
        TextView quantity;
        TextView price;
        Button sale;

        int nameColumnIndex;
        int quantityColumnIndex;
        int priceColumnIndex;
    }

}
