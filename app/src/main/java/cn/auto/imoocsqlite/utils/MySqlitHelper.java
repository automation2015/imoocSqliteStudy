package cn.auto.imoocsqlite.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 1、sqliteOpenHelper介绍
 * SQLiteDatabase的帮助类，用于管理数据库的创建和版本更新 * sqliteOpenHelper
 * 1.提供onCreate() onUpgrade()等创建数据库更新数据库的方法
 * 2.提供了获取数据库对象的函数
 */
public class MySqlitHelper extends SQLiteOpenHelper {
    /**
     * 构造函数
     *
     * @param context 上下文对象
     * @param name    表示创建数据库的名称
     * @param factory 游标工厂，一般不使用，设置为null
     * @param version 表示创建数据库的版本   >=1
     */
    public MySqlitHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 通过一个参数的构造函数，可以方便的使用数据库对象，而不用每次都输入数据库名称
     */
    public MySqlitHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    /**
     * 当数据库创建时回调的函数
     *
     * @param db 数据库对象
     *           可以在创建数据库时，直接创建表。数据库创建时，回调onCreat方法，创建表。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + Constant.TABLE_NAME + "(" + Constant._ID + " Integer primary key," + Constant.NAME + " varchar(10)," + Constant.AGE + " Integer)";
        db.execSQL(sql);
        Log.e("tag", "---------onCreate----");
    }

    /**
     * 当数据库版本更新时回调的函数
     *
     * @param db         数据库对象
     * @param oldVersion 数据库的旧版本
     * @param newVersion 数据库的新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 当数据库打开时回调的函数
     *
     * @param db 数据库对象
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.e("tag", "------------onOpen------------");
    }
}
