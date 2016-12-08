package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static com.example.android.inventory.data.InvContract.InvEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int EDITOR_LOADER = 101;
    private Uri mCurrentUri;
    private ProductHolder mHolder;
    private boolean mProductChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductChanged = true;
            return false;
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.product_item_save:
                saveProduct();
                return true;
            case R.id.product_item_delete:
                deleteProductConfirm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {

        String pName = mHolder.productNameView.getText().toString().trim();
        String pCompany = mHolder.productCompanyView.getText().toString().trim();
        int pCategory = mHolder.productCategoryView.getSelectedItemPosition();
        String pPriceString = mHolder.productPriceView.getText().toString().trim();
        float pPrice = 0;
        if (pPriceString.contains("$")) {
            NumberFormat format = NumberFormat.getCurrencyInstance();
            try {
                pPrice = format.parse(pPriceString).floatValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (TextUtils.isEmpty(pPriceString) && pPriceString.equals("") ) {
            pPriceString = "0";
            pPrice = Float.parseFloat(pPriceString);
        }



        String pDescription = mHolder.productDescriptionView.getText().toString().trim();

        String sizeX = mHolder.productSizeXView.getText().toString().trim();
        String sizeY = mHolder.productSizeYView.getText().toString().trim();
        String sizeZ = mHolder.productSizeZView.getText().toString().trim();

        if(TextUtils.isEmpty(sizeX) && sizeX.equals("")){
            sizeX = "0";
        }
        if(TextUtils.isEmpty(sizeY) && sizeY.equals("")){
            sizeY = "0";
        }
        if(TextUtils.isEmpty(sizeZ) && sizeZ.equals("")){
            sizeZ = "0";
        }


        String[] pSize = new String[]{
                sizeX,
                sizeY,
                sizeZ
        };
        String pWeightString = mHolder.productWeightView.getText().toString().trim();
        if(TextUtils.isEmpty(pWeightString) && pWeightString.equals("")){
            pWeightString = "0";
        }
        float pWeight = Float.valueOf(pWeightString);
        String pStockDate;
        if (mCurrentUri != null) {
            pStockDate = mHolder.productStockDateView.getText().toString().trim();
        } else {
            pStockDate = new Date().toString();
        }
        String pQuantityString = mHolder.productQuantityView.getText().toString().trim();
        if (TextUtils.isEmpty(pQuantityString) && pQuantityString.equals("")){
            pQuantityString = "0";
        }
        int pQuantity = Integer.valueOf(pQuantityString);

        ContentValues values = new ContentValues();

        values.put(InvEntry.COLUMN_INV_PRODUCT_NAME, pName);
        values.put(InvEntry.COLUMN_INV_PRODUCT_CATEGORY, pCategory);
        values.put(InvEntry.COLUMN_INV_PRODUCT_COMPANY, pCompany);
        values.put(InvEntry.COLUMN_INV_PRODUCT_PRICE, pPrice);
        values.put(InvEntry.COLUMN_INV_PRODUCT_DESCRIPTION, pDescription);
        values.put(InvEntry.COLUMN_INV_PRODUCT_SIZE, Arrays.toString(pSize));
        values.put(InvEntry.COLUMN_INV_PRODUCT_WEIGHT, pWeight);
        values.put(InvEntry.COLUMN_INV_PRODUCT_DATE, pStockDate);
        values.put(InvEntry.COLUMN_INV_PRODUCT_QUANTITY, pQuantity);

        if (mCurrentUri == null) {
            Uri uri = getContentResolver().insert(InvEntry.CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(this, R.string.product_insert_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_insert_success, Toast.LENGTH_SHORT).show();
                finish();

            }
        } else {
            int updateRow = getContentResolver().update(mCurrentUri, values, null, null);

            if (updateRow == 0) {
                Toast.makeText(this, R.string.product_update_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_update_success, Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }

    private void deleteProductConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Delete this Product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void deleteProduct() {
        if (mCurrentUri != null) {
            int rowDelete = getContentResolver().delete(mCurrentUri, null, null);
            if (rowDelete == 0) {
                Toast.makeText(this, R.string.product_delete_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_delete_success, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_item_menu, menu);

        return true;
    }

    private void setCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.product_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.productCategoryView.setAdapter(adapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mCurrentUri == null) {
            MenuItem item = menu.findItem(R.id.product_item_delete);
            item.setVisible(false);
        }
        MenuItem saveMenuItem = menu.findItem(R.id.product_item_save);
        saveMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mProductChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnSavedChangesDialog(discardButton);

    }

    private void showUnSavedChangesDialog(DialogInterface.OnClickListener discardButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Product is not saved are you sure finish?");
        builder.setPositiveButton("Discard", discardButton);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mCurrentUri = getIntent().getData();


        if (mCurrentUri == null) {
            setTitle("Add a Product");
        } else {
            setTitle("Edit a Product");
        }
        mHolder = new ProductHolder();

        mHolder.productNameView = (EditText) findViewById(R.id.edit_product_name);
        mHolder.productCompanyView = (EditText) findViewById(R.id.edit_product_company);
        mHolder.productQuantityView = (EditText) findViewById(R.id.edit_product_quantity);
        mHolder.productPriceView = (EditText) findViewById(R.id.edit_product_price);
        mHolder.productDescriptionView = (EditText) findViewById(R.id.edit_product_description);
        mHolder.productCategoryView = (Spinner) findViewById(R.id.edit_product_spinner_category);
        mHolder.productSizeXView = (EditText) findViewById(R.id.edit_product_size_x);
        mHolder.productSizeYView = (EditText) findViewById(R.id.edit_product_size_y);
        mHolder.productSizeZView = (EditText) findViewById(R.id.edit_product_size_z);
        mHolder.productWeightView = (EditText) findViewById(R.id.edit_product_weight);
        mHolder.productStockDateView = (TextView) findViewById(R.id.edit_product_date);

        mHolder.productOrderButton = (Button) findViewById(R.id.order_button);
        if (mCurrentUri != null) {
            mHolder.productOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Date date = new Date();
                    int pQuantity = Integer.parseInt(mHolder.productQuantityView.getText().toString().trim());
                    String dateString = date.toString();
                    pQuantity += 10;
                    String pQuantityString = String.valueOf(pQuantity);
                    mHolder.productStockDateView.setText(dateString);
                    mHolder.productQuantityView.setText(pQuantityString);
                    Toast.makeText(EditorActivity.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mHolder.productOrderButton.setVisibility(View.GONE);
            mHolder.productStockDateView.setVisibility(View.GONE);
        }

        setCategorySpinner();

        //set TouchListener
        mHolder.productNameView.setOnTouchListener(mTouchListener);
        mHolder.productCategoryView.setOnTouchListener(mTouchListener);
        mHolder.productCompanyView.setOnTouchListener(mTouchListener);
        mHolder.productPriceView.setOnTouchListener(mTouchListener);
        mHolder.productDescriptionView.setOnTouchListener(mTouchListener);
        mHolder.productSizeXView.setOnTouchListener(mTouchListener);
        mHolder.productSizeYView.setOnTouchListener(mTouchListener);
        mHolder.productSizeZView.setOnTouchListener(mTouchListener);
        mHolder.productWeightView.setOnTouchListener(mTouchListener);
        mHolder.productStockDateView.setOnTouchListener(mTouchListener);


        if (mCurrentUri != null) {
            getLoaderManager().initLoader(EDITOR_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{
                InvEntry._ID,
                InvEntry.COLUMN_INV_PRODUCT_NAME,
                InvEntry.COLUMN_INV_PRODUCT_CATEGORY,
                InvEntry.COLUMN_INV_PRODUCT_COMPANY,
                InvEntry.COLUMN_INV_PRODUCT_PRICE,
                InvEntry.COLUMN_INV_PRODUCT_DESCRIPTION,
                InvEntry.COLUMN_INV_PRODUCT_SIZE,
                InvEntry.COLUMN_INV_PRODUCT_WEIGHT,
                InvEntry.COLUMN_INV_PRODUCT_DATE,
                InvEntry.COLUMN_INV_PRODUCT_QUANTITY
        };
        return new CursorLoader(this, mCurrentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int pNameCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_NAME);
        int pCompanyCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_COMPANY);
        int pCategoryCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_CATEGORY);
        int pPriceCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_PRICE);
        int pDescriptionCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_DESCRIPTION);
        int pSizeCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_SIZE);
        int pWeightCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_WEIGHT);
        int pDateCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_DATE);
        int pQuantityCI = cursor.getColumnIndex(InvEntry.COLUMN_INV_PRODUCT_QUANTITY);


        if (cursor.moveToFirst()) {
            String pNameString = cursor.getString(pNameCI);
            String pCompanyString = cursor.getString(pCompanyCI);
            int pCategoryInt = cursor.getInt(pCategoryCI);
            float pPriceFloat = cursor.getFloat(pPriceCI);
            String pPriceString = formatPrice(pPriceFloat);
            String pDescriptionString = cursor.getString(pDescriptionCI);
            String pSizeString = cursor.getString(pSizeCI);

            String[] pSizeArray = new String[]{};
            try {
                JSONArray jsonArray = new JSONArray(pSizeString);
                pSizeArray = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    pSizeArray[i] = jsonArray.getString(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String sizeX = pSizeArray[0];
            String sizeY = pSizeArray[1];
            String sizeZ = pSizeArray[2];
            String pWeightFloat = String.valueOf(cursor.getFloat(pWeightCI));

            String pStockDate = cursor.getString(pDateCI);
            int pQuantityInt = cursor.getInt(pQuantityCI);
            String pQuantityString = String.valueOf(pQuantityInt);

            mHolder.productNameView.setText(pNameString);
            mHolder.productCompanyView.setText(pCompanyString);
            mHolder.productCategoryView.setSelection(pCategoryInt);
            mHolder.productPriceView.setText(pPriceString);
            mHolder.productDescriptionView.setText(pDescriptionString);
            mHolder.productSizeXView.setText(sizeX);
            mHolder.productSizeYView.setText(sizeY);
            mHolder.productSizeZView.setText(sizeZ);
            mHolder.productWeightView.setText(pWeightFloat);
            mHolder.productStockDateView.setText(pStockDate);
            mHolder.productQuantityView.setText(pQuantityString);
        }


    }

    private String formatPrice(float pPriceFloat) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(pPriceFloat);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class ProductHolder {
        public EditText productNameView;
        public Spinner productCategoryView;
        public EditText productCompanyView;
        public EditText productPriceView;
        public EditText productDescriptionView;
        public EditText productSizeXView;
        public EditText productSizeYView;
        public EditText productSizeZView;
        public EditText productWeightView;
        public TextView productStockDateView;
        public EditText productQuantityView;
        public Button productOrderButton;
    }
}
