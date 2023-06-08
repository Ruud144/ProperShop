package ru.propershop.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.propershop.Model.Products;

public class CartDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cart.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "cart_items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE = "image";

    public CartDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," + COLUMN_PRICE + " INTEGER," + COLUMN_DESCRIPTION + " TEXT," + COLUMN_IMAGE + " TEXT" + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public void addItem(Products product) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getPname());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_IMAGE, product.getImage());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void removeItem(Products product) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(product.getPid())});
        db.close();
    }

    public void deleteAllItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public boolean isProductExists(Products product) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{product.getPid()});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0;
    }

    @SuppressLint("Range")
    public Products getProductById(String productId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{productId}, null, null, null);
        Products product = null;
        if (cursor.moveToFirst()) {
            product = new Products();
            product.setPid(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            product.setPname(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            product.setPrice(cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)));
            product.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            product.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
        }
        cursor.close();
        db.close();
        return product;
    }

    @SuppressLint("Range")
    public List<Products> getCartItems() {
        List<Products> cartItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Products product = new Products();
                product.setPid(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
                product.setPname(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                product.setPrice(cursor.getInt(cursor.getColumnIndex(COLUMN_PRICE)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                product.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                cartItems.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cartItems;
    }
}