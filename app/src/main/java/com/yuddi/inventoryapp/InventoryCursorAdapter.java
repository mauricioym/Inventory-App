package com.yuddi.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.name.setText("");
        holder.quantity.setText("");
        holder.price.setText("");
    }

    private static class ViewHolder {
        TextView name;
        TextView quantity;
        TextView price;
        Button sale;
    }

}
