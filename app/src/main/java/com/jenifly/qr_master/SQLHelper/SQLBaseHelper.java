package com.jenifly.qr_master.SQLHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jenifly.qr_master.helper.Item;

import java.util.ArrayList;

/**
 * Created by Jenifly on 2017/10/9.
 */

public class SQLBaseHelper {

    private SQLiteDatabase db;

    public SQLBaseHelper(Context context){
        DataBaseHelper helper = new DataBaseHelper(context);
        db = helper.getReadableDatabase();
    }

    public ArrayList getDataList(){
        ArrayList<Item> list = new ArrayList();
        Cursor cursor = db.rawQuery("select * from qr_management", null);
        while (cursor.moveToNext()) {
            list.add(new Item(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return list;
    }

    public long insertQR(Item item){
        ContentValues values = new ContentValues();
        values.put("name", item.getItemName());
        values.put("content", item.getItemContent());
        return db.insert("qr_management", null, values);
    }

    public int deleteById(int id){
        return db.delete("qr_management", "qrid=?", new String[]{String.valueOf(id)});
    }
}
