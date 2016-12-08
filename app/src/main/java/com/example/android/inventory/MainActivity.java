package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Date;

import static com.example.android.inventory.data.InvContract.InvEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int LIST_LOADER = 1;
    private InvCursorAdapter mAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy:
                insertProduct();
                return true;
            case R.id.action_delete_all_data:
                deleteProducts();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteProducts() {
        int deletedRow = getContentResolver().delete(InvEntry.CONTENT_URI, null, null);
        if (deletedRow == -1) {
            Toast.makeText(this, "product detection failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, deletedRow + " rows products delete success", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertProduct() {
        ContentValues values = new ContentValues();

        values.put(InvEntry.COLUMN_INV_PRODUCT_NAME, "Amazon Echo - Black");
        values.put(InvEntry.COLUMN_INV_PRODUCT_COMPANY, "Amazon");
        values.put(InvEntry.COLUMN_INV_PRODUCT_QUANTITY, 10);
        values.put(InvEntry.COLUMN_INV_PRODUCT_CATEGORY, 2);
        values.put(InvEntry.COLUMN_INV_PRODUCT_PRICE, 179.99);
        values.put(InvEntry.COLUMN_INV_PRODUCT_DESCRIPTION, "description about echo");
        String[] size = new String[]{
                "9.3",
                "3.3",
                "3.3"
        };
        values.put(InvEntry.COLUMN_INV_PRODUCT_SIZE, Arrays.toString(size));
        values.put(InvEntry.COLUMN_INV_PRODUCT_WEIGHT, 37.5);
        Date date = new Date();
        values.put(InvEntry.COLUMN_INV_PRODUCT_DATE, date.toString());

        Uri newUri = getContentResolver().insert(InvEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, R.string.product_insert_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.product_insert_failed, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_insert);
        fab.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.inv_list);
        View emptyView = findViewById(R.id.list_empty_view);
        listView.setEmptyView(emptyView);
        mAdapter = new InvCursorAdapter(this, null);
        listView.setAdapter(mAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
//                Uri currentUri = ContentUris.withAppendedId(InvEntry.CONTENT_URI, id);
//                intent.setData(currentUri);
//                startActivity(intent);
//            }
//        });


        getLoaderManager().initLoader(LIST_LOADER, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                InvEntry._ID,
                InvEntry.COLUMN_INV_PRODUCT_NAME,
                InvEntry.COLUMN_INV_PRODUCT_COMPANY,
                InvEntry.COLUMN_INV_PRODUCT_CATEGORY,
                InvEntry.COLUMN_INV_PRODUCT_PRICE,
                InvEntry.COLUMN_INV_PRODUCT_QUANTITY
        };

        return new CursorLoader(this, InvEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
