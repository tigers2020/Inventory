package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static android.R.attr.content;
import static android.R.attr.id;
import static com.example.android.inventory.data.InvContract.InvEntry;

/**
 * Created by tiger on 2016-12-06.
 */

public class InvCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = InvCursorAdapter.class.getSimpleName();
    private Context mContext;
    private ProductHolder mHolder;


    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.inv_list_item, parent, false);
        mHolder = new ProductHolder();

        mHolder.productNameView = (TextView) view.findViewById(R.id.list_item_product_name);
        mHolder.productCategoryView = (TextView) view.findViewById(R.id.list_item_product_category);
        mHolder.productQuantityView = (TextView) view.findViewById(R.id.list_item_product_quantity);
        mHolder.productPriceView = (TextView) view.findViewById(R.id.list_item_product_price);
        mHolder.productCompanyView = (TextView) view.findViewById(R.id.list_item_product_company);
        mHolder.productFrameView = view.findViewById(R.id.list_item_frame);
        mHolder.productSaleButton = (Button) view.findViewById(R.id.list_item_product_sale_button);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        int pIdCI = cursor.getColumnIndex(InvEntry._ID);
        int pNameCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_NAME);
        int pCategoryCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_CATEGORY);
        int pQuantityCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_QUANTITY);
        int pCompanyCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_COMPANY);
        int pPriceCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_PRICE);

        final int pIdInt = cursor.getInt(pIdCI);
        String pNameString = cursor.getString(pNameCI);
        String pCompanyString = cursor.getString(pCompanyCI);
        int pCategoryInt = cursor.getInt(pCategoryCI);
        String pCategoryString = getCategory(pCategoryInt);
        final int pQuantityInteger = cursor.getInt(pQuantityCI);
        boolean pInStock;
        if (pQuantityInteger == 0) {
            pInStock = false;
        } else {
            pInStock = true;
        }
        String pQuantityString = String.valueOf(pQuantityInteger);
        float pPriceInt = cursor.getFloat(pPriceCI);

        String pPriceFormatted = priceFormat(pPriceInt);

        mHolder.productNameView.setText(pNameString);
        mHolder.productCompanyView.setText(pCompanyString);
        mHolder.productCategoryView.setText(pCategoryString);
        mHolder.productPriceView.setText(pPriceFormatted);
        mHolder.productQuantityView.setText(pQuantityString);
        if (pInStock) {
            mHolder.productFrameView.setBackgroundResource(android.R.color.holo_green_dark);
        } else {
            mHolder.productFrameView.setBackgroundResource(android.R.color.holo_red_dark);
        }
        mHolder.productSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private String priceFormat(float pPriceInt) {
        float priceFloat = pPriceInt;
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String priceString = format.format(priceFloat);
        return priceString;
    }

    private String getCategory(int pCategorySelected) {
        String[] categoryArray;
        categoryArray = mContext.getResources().getStringArray(R.array.product_category);
        String categoryString;
        for (int i = 0; i < categoryArray.length; i++) {
        }
        switch (pCategorySelected) {
            case InvEntry.CATEGORY_BOOK_AND_AUDIBLE:
                categoryString = categoryArray[1];
                break;
            case InvEntry.CATEGORY_MOVIES_MUSIC_AND_GAME:
                categoryString = categoryArray[2];
                break;
            case InvEntry.CATEGORY_ELECTRONICS_AND_COMPUTERS:
                categoryString = categoryArray[3];
                break;
            case InvEntry.CATEGORY_HOME_GARDEN_AND_TOOLS:
                categoryString = categoryArray[4];
                break;
            case InvEntry.CATEGORY_BEAUTY_HEALTH_AND_GROCERY:
                categoryString = categoryArray[5];
                break;
            case InvEntry.CATEGORY_TOYS_KIDS_AND_BABY:
                categoryString = categoryArray[6];
                break;
            case InvEntry.CATEGORY_CLOTHING_SHOES_AND_JEWELRY:
                categoryString = categoryArray[7];
                break;
            case InvEntry.CATEGORY_HANDMADE:
                categoryString = categoryArray[8];
                break;
            case InvEntry.CATEGORY_SPORTS_AND_OUTDOORS:
                categoryString = categoryArray[9];
                break;
            case InvEntry.CATEGORY_AUTOMOTIVE_AND_INDUSTRIAL:
                categoryString = categoryArray[10];
                break;
            default:
                categoryString = categoryArray[0];
                break;
        }
        return categoryString;


    }
    private class ProductHolder {
        public TextView productNameView;
        public TextView productCategoryView;
        public TextView productQuantityView;
        public TextView productPriceView;
        public TextView productCompanyView;
        public View productFrameView;
        public Button productSaleButton;
    }

}
