package cn.auto.imoocsqlite.utils;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import cn.auto.imoocsqlite.bean.Person;
public class DbManager {
    /*
    一般情况下，不会直接使用Activity直接调用数据库的底层代码，而是通过创建一个用于管理数据库的中间层的类DbManager，
目的是增加程序的耦合度和可读性；
同常设计成单例模式，保证对象的唯一性，不会多次创建，从而造成内存紧张；
     */
    private static MySqlitHelper helper;

    public static MySqlitHelper getInstance(Context context) {
        if (helper == null) {
            helper = new MySqlitHelper(context);
        }
        return helper;
    }

    public static void execSql(SQLiteDatabase db, String sql) {
        if (db != null) {
            if (sql != null && !"".equals(sql)) {
                db.execSQL(sql);
            }
        }
    }

    /**
     * 根据sql语句在数据库中执行查询获得cursor对象
     *
     * @param db            数据库对象
     * @param sql           sql语句
     * @param selectionArgs 查询条件
     * @return 查询结果
     */
    public static Cursor selectDataBySql(SQLiteDatabase db, String sql, String[] selectionArgs) {
        Cursor cursor = null;
        if (db != null) {
            if (sql != null && !"".equals(sql)) {
                cursor = db.rawQuery(sql, selectionArgs);
            }
        }
        return cursor;
    }

    /**
     * 把cursor查询的数据转换到list中
     *
     * @param cursor
     * @return
     */
    public static List<Person> cursorToList(Cursor cursor) {
        List<Person> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int _id = cursor.getInt(cursor.getColumnIndex(Constant._ID));
                String name = cursor.getString(cursor.getColumnIndex(Constant.NAME));
                int age = cursor.getInt(cursor.getColumnIndex(Constant.AGE));
                Person p = new Person(_id, name, age);
                list.add(p);
            }
        }
        return list;
    }

    /**
     * 根据数据库以及数据表名称获取表中数据总条目
     *
     * @param db        数据库对象
     * @param tableName 数据表名称
     * @return 数据总条目
     */
    public static int getDataCount(SQLiteDatabase db, String tableName) {
        int count = 0;
        if (db != null) {
            Cursor cursor = db.rawQuery("select * from " + tableName, null);
            count = cursor.getCount();
        }
        return count;
    }

    /**
     * 根据当前页面查询获取该页码对应的集合数据
     *
     * @param db          数据库对象
     * @param tableName   数据表名称
     * @param currentPage 当前页码
     * @return 当前页对应的集合
     * select * from person limit ?,?
     * 0,20    1
     * 20,20   2
     * 40,20   3
     */
    public static List<Person> getListByCurrentPage(SQLiteDatabase db, String tableName,
                                                    int currentPage, int pageSize) {
        int index = (currentPage - 1) * pageSize;
        Cursor cursor = null;
        if (db != null) {
            String sql = "select * from " + tableName + " limit ?,?";
            cursor = db.rawQuery(sql, new String[]{index + "", pageSize + ""});
        }
        return cursorToList(cursor);
    }
}
