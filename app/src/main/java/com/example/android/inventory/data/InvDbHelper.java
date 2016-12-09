package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;
import static com.example.android.inventory.data.InvContract.*;

/**
 * Created by tiger on 2016-12-06.
 */

public class InvDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATABASE = "CREATE TABLE " + InvEntry.TABLE_NAME + "("
                + InvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InvEntry.COLUMN_INV_PRODUCT_NAME + " TEXT NOT NULL, "
                + InvEntry.COLUMN_INV_PRODUCT_COMPANY + " TEXT, "
                + InvEntry.COLUMN_INV_PRODUCT_COMPANY_EMAIL + " TEXT NOT NULL, "
                + InvEntry.COLUMN_INV_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InvEntry.COLUMN_INV_PRODUCT_PRICE + " REAL NOT NULL, "
                + InvEntry.COLUMN_INV_PRODUCT_DESCRIPTION + " TEXT, "
                + InvEntry.COLUMN_INV_PRODUCT_SIZE + " TEXT, "
                + InvEntry.COLUMN_INV_PRODUCT_WEIGHT + " REAL, "
                + InvEntry.COLUMN_INV_PRODUCT_DATE + " TEXT, "
                + InvEntry.COLUMN_INV_PRODUCT_CATEGORY + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(CREATE_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
