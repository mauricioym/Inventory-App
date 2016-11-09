package com.yuddi.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
        holder.nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        holder.quantityTextView = (TextView) view.findViewById(R.id.list_item_quantity);
        holder.priceTextView = (TextView) view.findViewById(R.id.list_item_price);
        holder.saleButton = (Button) view.findViewById(R.id.list_item_sale);
        holder.saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View listItem = (View) view.getParent();
                final ViewHolder holder = (ViewHolder) listItem.getTag();

                if (holder.quantity > 0) {

                    holder.quantity--;
                    updateQuantity(context, holder);

                    final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.getRootView().findViewById(R.id.coordinator_layout);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.toast_sale_was_made, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    holder.quantity++;
                                    updateQuantity(context, holder);

                                    Snackbar snackbar1 = Snackbar.make(coordinatorLayout, R.string.toast_sale_was_cancelled, Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });
                    snackbar.show();
                }
            }
        });

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        int idColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_NAME);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_INVENTORY_PRICE);

        holder.id = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        holder.quantity = cursor.getInt(quantityColumnIndex);
        float price = cursor.getInt(priceColumnIndex) / 100f;

        holder.nameTextView.setText(name);
        holder.quantityTextView.setText(context.getString(R.string.stock_quantity, holder.quantity));
        holder.priceTextView.setText(NumberFormat.getCurrencyInstance().format(price));
    }

    private void updateQuantity(Context context, ViewHolder holder) {

        holder.quantityTextView.setText(context.getString(R.string.stock_quantity, holder.quantity));

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, holder.quantity);

        Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, holder.id);

        context.getContentResolver().update(uri, values, null, null);
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        Button saleButton;

        int id;
        int quantity;
    }

}
