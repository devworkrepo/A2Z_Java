package com.a2zsuvidhaa.in.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    private static String DB_NAME ="a2zsuvidha_db";
    private static int DB_VERSION= 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS "+DBContract.TABLE_RECHARGE+"("+
                DBContract.RECHARGE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                DBContract.RECHARGE_TYPE+" TEXT,"+
                DBContract.RECHARGE_AMOUNT+" TEXT,"+
                DBContract.RECHARGE_TIME+" DATE,"+
                DBContract.RECHARGE_OPERATOR+" TEXT,"+
                DBContract.RECHARGE_NUMBER+" TEXT)" +
                "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean saveTransaction(String rechargeNumber,
                                   String operator, String rechargeType, String rechargeTime, String strAmount){

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.RECHARGE_NUMBER,rechargeNumber);
        cv.put(DBContract.RECHARGE_TYPE,rechargeType);
        cv.put(DBContract.RECHARGE_OPERATOR,operator);
        cv.put(DBContract.RECHARGE_TIME,rechargeTime);
        cv.put(DBContract.RECHARGE_AMOUNT,strAmount);

        return db.insert(DBContract.TABLE_RECHARGE,null,cv)>0;
    }


    public boolean updateTransaction(String rechargeNumber, String rechargeTime, String amount){

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBContract.RECHARGE_TIME,rechargeTime);
        cv.put(DBContract.RECHARGE_AMOUNT,amount);
        return db.update(DBContract.TABLE_RECHARGE,cv,
                DBContract.RECHARGE_NUMBER+" = ?", new String[]{rechargeNumber})>0;
    }

    public boolean isNumberExist(String number,String amount){
        boolean isExist = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+DBContract.RECHARGE_NUMBER +" FROM "+DBContract.TABLE_RECHARGE
                +" WHERE "+DBContract.RECHARGE_NUMBER+" = '"+number
                +"' AND "+DBContract.RECHARGE_AMOUNT+" = '"+amount+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    public boolean canTransaction(String strMobile, Date time) {
        boolean canRecharge= false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+DBContract.RECHARGE_TIME+" FROM "+DBContract.TABLE_RECHARGE
                +" WHERE "+DBContract.RECHARGE_NUMBER+" = '"+strMobile+"'";

        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            Date preRechargeTime = new Date(cursor.getString(0));
            if(time.after(preRechargeTime)){
                canRecharge = true;
            }
        }

        cursor.close();
        return canRecharge;
    }



    public boolean deleteData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(DBContract.TABLE_RECHARGE,null,null)>0;

    }

}
