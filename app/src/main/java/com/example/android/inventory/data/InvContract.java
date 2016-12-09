package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by tiger on 2016-12-06.
 */

public class InvContract {

    //initialize self.
    private InvContract() {
    }

    public static final class InvEntry implements BaseColumns {

        public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
        public static final String PATH_INV = "inventory";
        private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INV);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INV;


        public final static String TABLE_NAME = "Inventory";
        public final static String COLUMN_INV_PRODUCT_NAME = "product_name";
        public final static String COLUMN_INV_PRODUCT_QUANTITY = "product_quantity";
        public final static String COLUMN_INV_PRODUCT_CATEGORY = "product_category";
        public final static String COLUMN_INV_PRODUCT_COMPANY = "product_company";
        public final static String COLUMN_INV_PRODUCT_COMPANY_EMAIL = "product_company_email";
        public final static String COLUMN_INV_PRODUCT_PRICE = "product_price";
        public final static String COLUMN_INV_PRODUCT_DESCRIPTION = "product_description";
        public final static String COLUMN_INV_PRODUCT_SIZE = "product_size";
        public final static String COLUMN_INV_PRODUCT_WEIGHT = "product_weight";
        public final static String COLUMN_INV_PRODUCT_DATE = "product_date";

        //category
        public static final int CATEGORY_UNKNOWN = 0;
        public static final int CATEGORY_BOOK_AND_AUDIBLE = 1;
        public static final int CATEGORY_MOVIES_MUSIC_AND_GAME = 2;
        public static final int CATEGORY_ELECTRONICS_AND_COMPUTERS = 3;
        public static final int CATEGORY_HOME_GARDEN_AND_TOOLS = 4;
        public static final int CATEGORY_BEAUTY_HEALTH_AND_GROCERY = 5;
        public static final int CATEGORY_TOYS_KIDS_AND_BABY = 6;
        public static final int CATEGORY_CLOTHING_SHOES_AND_JEWELRY = 7;
        public static final int CATEGORY_HANDMADE = 8;
        public static final int CATEGORY_SPORTS_AND_OUTDOORS = 9;
        public static final int CATEGORY_AUTOMOTIVE_AND_INDUSTRIAL = 10;

        public static boolean isValidCategory(int category){
            if (category == CATEGORY_UNKNOWN || category == CATEGORY_BOOK_AND_AUDIBLE || category == CATEGORY_MOVIES_MUSIC_AND_GAME || category == CATEGORY_ELECTRONICS_AND_COMPUTERS || category == CATEGORY_HOME_GARDEN_AND_TOOLS || category == CATEGORY_BEAUTY_HEALTH_AND_GROCERY || category == CATEGORY_TOYS_KIDS_AND_BABY
                     || category == CATEGORY_CLOTHING_SHOES_AND_JEWELRY || category == CATEGORY_HANDMADE || category == CATEGORY_SPORTS_AND_OUTDOORS || category == CATEGORY_AUTOMOTIVE_AND_INDUSTRIAL){
                return true;
            }
            return false;
        }
        public static boolean isValidEmail(String cEmail){
            return !TextUtils.isEmpty(cEmail) && Patterns.EMAIL_ADDRESS.matcher(cEmail).matches();
        }
    }
}
