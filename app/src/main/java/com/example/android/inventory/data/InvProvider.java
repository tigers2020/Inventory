package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventory.R;
import com.example.android.inventory.data.InvContract.InvEntry;

/**
 * Created by tiger on 2016-12-06.
 */

public class InvProvider extends ContentProvider {
    private static final String LOG_TAG = InvProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    static {
        sUriMatcher.addURI(InvEntry.CONTENT_AUTHORITY, InvEntry.PATH_INV, INVENTORY);
        sUriMatcher.addURI(InvEntry.CONTENT_AUTHORITY, InvEntry.PATH_INV + "/#", INVENTORY_ID);
    }

    private InvDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InvDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                cursor = db.query(InvEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:

                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InvEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.cannot_query_unknown_uri) + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                return InvEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InvEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Failed to insert production with " + match);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {

        //check valid
        boolean checked = checkValid(contentValues);

        if (!checked) {
            return null;
        } else {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            long id = db.insert(InvEntry.TABLE_NAME, null, contentValues);

            if (id != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return ContentUris.withAppendedId(uri, id);
        }
    }

    private boolean checkValid(ContentValues contentValues) {
        String pName = contentValues.getAsString(InvEntry.COLUMN_INV_PRODUCT_NAME);
        int pQuantity = contentValues.getAsInteger(InvEntry.COLUMN_INV_PRODUCT_QUANTITY);
        int pCategory = contentValues.getAsInteger(InvEntry.COLUMN_INV_PRODUCT_CATEGORY);
        float pPrice = contentValues.getAsFloat(InvEntry.COLUMN_INV_PRODUCT_PRICE);

        if (TextUtils.isEmpty(pName)) {
            Toast.makeText(getContext(), "Product Name must be entered", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pQuantity < 0) {
            Toast.makeText(getContext(), "Quantity must be positive Values", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!InvEntry.isValidCategory(pCategory)) {
            Toast.makeText(getContext(), "Invalid Category", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (pPrice < 0) {
            Toast.makeText(getContext(), "Price must be positive Values", Toast.LENGTH_SHORT).show();
            return false;

        }
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArg) {
        int match = sUriMatcher.match(uri);
        int deleteRow;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (match) {
            case INVENTORY:
                deleteRow = db.delete(InvEntry.TABLE_NAME, selection, selectionArg);
                break;
            case INVENTORY_ID:
                selection = InvEntry._ID + "=?";
                selectionArg = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deleteRow = db.delete(InvEntry.TABLE_NAME, selection, selectionArg);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (deleteRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteRow;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        Log.i(LOG_TAG, "update");
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InvEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
        }
        return 0;

    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        boolean checked = checkValid(contentValues);

        if (!checked) {

            return 0;
        } else {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            int updateRow = db.update(InvEntry.TABLE_NAME, contentValues, selection, selectionArgs);
            if (updateRow != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return updateRow;
        }
    }
}
