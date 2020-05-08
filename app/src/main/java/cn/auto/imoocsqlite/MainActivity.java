package cn.auto.imoocsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cn.auto.imoocsqlite.adpters.MyBaseAdapter;
import cn.auto.imoocsqlite.adpters.MyCursorAdapter;
import cn.auto.imoocsqlite.bean.Person;
import cn.auto.imoocsqlite.utils.Constant;
import cn.auto.imoocsqlite.utils.DbManager;
import cn.auto.imoocsqlite.utils.MySqlitHelper;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnCreateDB, btnInsert, btnDelete, btnUpdate, btnInsertApi, btnDeleteApi, btnUpdateApi, btnQuery, btnQueryApi;
    private MySqlitHelper helper;
    private ListView mLv;
    private SQLiteDatabase sdCardDB;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    private MyCursorAdapter mCursorAdapter;
    private int totalNum;//当前控件加载的总条目
    private int pageSize = 20;//指定每页显式的条数
    private int pageNum;//表示总页数
    private int currentPage = 1;//当前页码
    private List<Person> totalList;
    private MyBaseAdapter mBaseAdapter;
    private boolean isDivPage;//是否分页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        helper = DbManager.getInstance(this);
        //申请WRITE_EXTERNAL_STORAGE权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.WRITE_EXTERNAL_STORAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == Constant.WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限允许
                Toast.makeText(this, "允许", Toast.LENGTH_SHORT).show();
                getDataFromSdCard();
            } else {
                //权限不允许
                Toast.makeText(this, "不允许", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDataFromSdCard() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "info.db";
        Log.e("tag", path);
        /**
         * openDatabase(String path, CursorFactory factory, int flags) 打开指定路径下的数据库
         * String path 数据库存放路径
         * CursorFactory factory 游标工厂 指定为null即可
         * int flags 打开数据库操作模式
         */
        sdCardDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select * from " + Constant.TABLE_NAME;
        Cursor c = sdCardDB.rawQuery(sql, null);
        /**
         * SimpleCursorAdapter(Context context, int layout,Cursor c, String[] from, int[] to)
         * Context context 上下文对象
         *  int layout 表示适配器控件中每项item的布局id
         *  String[] from 表示cursor中数据表字段的数组
         *  int[] to 表示字段对应值的控件资源id
         *  int flag 设置适配器的标记   SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER 设置为观察者模式
         *  注意：在使用SimpleCursorAdapter时，要求数据库中必须有名字叫_id的主键列
         */
        mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_listview, c,
                new String[]{Constant._ID, Constant.NAME, Constant.AGE}, new int[]{R.id.tvId, R.id.tvName, R.id.tvAge},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mCursorAdapter = new MyCursorAdapter(this, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
//    mLv.setAdapter(mSimpleCursorAdapter);
        mLv.setAdapter(mCursorAdapter);
//分页加载:1、获取数据表中的总条目
        totalNum = DbManager.getDataCount(sdCardDB, Constant.TABLE_NAME);
        if (currentPage == 1) {
            totalList = DbManager.getListByCurrentPage(sdCardDB, Constant.TABLE_NAME, currentPage, pageSize);
        }
//        2、计算总页数
        pageNum = (int) Math.ceil(totalNum / (double) pageSize);
        mBaseAdapter = new MyBaseAdapter(this, totalList);
//    mLv.setAdapter(mBaseAdapter);
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (isDivPage && AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                    if (currentPage < pageNum) {
                        currentPage++;
                        totalList.addAll(DbManager.getListByCurrentPage(sdCardDB, Constant.TABLE_NAME,
                                currentPage, pageSize));
                        mBaseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totleItemCount) {
                isDivPage = ((firstVisibleItem + visibleItemCount) == totleItemCount);
            }
        });
    }

    private void initViews() {
        btnCreateDB = findViewById(R.id.btnCreateDB);
        btnInsert = findViewById(R.id.btnInsert);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnInsertApi = findViewById(R.id.btnInsertApi);
        btnUpdateApi = findViewById(R.id.btnUpdateApi);
        btnQuery = findViewById(R.id.btnQuery);
        btnQueryApi = findViewById(R.id.btnQueryApi);
        mLv = findViewById(R.id.lv);
    }

    public void createDB(View view) {
        /*获取SQLiteOpenHelper实例，通过调用getReadableDatabase() 或getWritableDatabase() 创建或者打开数据库。
         如果数据库不存在则创建数据库，如果数据库存在直接打开数据库；
         默认情况下两个函数都表示打开或创建可读可写的数据库对象，如果磁盘已满或者是数据库本身权限等情况下，
         getReadableDatabase() 打开的是只读数据库
         */
        switch (view.getId()) {
            case R.id.btnCreateDB:
                SQLiteDatabase db = helper.getReadableDatabase();
                for (int j = 50; j < 80; j++) {
                    String sql = "insert into " + Constant.TABLE_NAME + " values(" + j + ",'zhangsanfeng" + j + "',50)";
                    db.execSQL(sql);
                }
                db.close();
                break;

            case R.id.btnInsertTransaction:
                //使用事务批量插入数据
                SQLiteDatabase db1 = helper.getWritableDatabase();
                //1.数据库显式开启事务
                db1.beginTransaction();
                for (int m = 1; m < 300; m++) {
                    ContentValues values = new ContentValues();
                    values.put(Constant._ID + "", m + "");
                    values.put(Constant.NAME, "洪七公" + m);
                    values.put(Constant.AGE + "", 70 + m);
                    db1.insert(Constant.TABLE_NAME, null, values);
                }
                //2.提交当前事务
                db1.setTransactionSuccessful();
                //3.关闭事务
                db1.endTransaction();
                db1.close();
                Toast.makeText(this, "批量插入成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDeleteTransaction:
                //使用事务批量删除数据
                SQLiteDatabase db2 = helper.getWritableDatabase();
                db2.beginTransaction();
                String deleteSql = "delete from " + Constant.TABLE_NAME + " where " + Constant._ID + "<301";
                DbManager.execSql(db2, deleteSql);

                db2.setTransactionSuccessful();
                db2.endTransaction();
                db2.close();
                Toast.makeText(this, "批量删除成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnInsert:
                SQLiteDatabase db1 = helper.getWritableDatabase();
                String sql1 = "insert into " + Constant.TABLE_NAME + " values(111,'zhangsan',20)";
                DbManager.execSql(db1, sql1);
                String sql2 = "insert into " + Constant.TABLE_NAME + " values(211,'lisi',21)";
                DbManager.execSql(db1, sql2);
                db1.close();
                Toast.makeText(this, "插入成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnUpdate:
                db1 = helper.getWritableDatabase();
                String updateSql = "update " + Constant.TABLE_NAME + " set " + Constant.NAME + "='xiaoming' where " + Constant._ID + "=211";
                DbManager.execSql(db1, updateSql);
                Toast.makeText(this, "Update data finish", Toast.LENGTH_SHORT).show();
                db1.close();
                break;
            case R.id.btnDelete:
                db1 = helper.getWritableDatabase();
                String deleteSql = "delete from person where _id=111";
                DbManager.execSql(db1, deleteSql);
                db1.close();
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void onClick(View view) {
        switch (view.getId()) {
            /**
             * String table 数据库表名
             * ContentValues  values 键为String的Hashmap集合
             * String whereClause  where 子句，修改的条件
             * String whereArgs 修改条件的占位符
             * 返回值 int 修改的条数
             */
            case R.id.btnInsertApi:
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues value = new ContentValues();
                value.put(Constant._ID, 30);
                value.put(Constant.NAME, "daming");
                value.put(Constant.AGE, 34);
                long count = db.insert(Constant.TABLE_NAME, null, value);
                if (count > 0) {
                    Toast.makeText(MainActivity.this, "插入数据成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "插入数据失败", Toast.LENGTH_SHORT).show();
                }
                db.close();
                break;
            /**
             * String table 数据库表名
             * ContentValues  values 键为String的Hashmap集合
             * String whereClause  where 子句，修改的条件
             * String whereArgs 修改条件的占位符
             * 返回值 int 修改的条数
             */
            case R.id.btnUpdateApi:
                db = helper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(Constant.NAME, "郭靖");
                int result = db.update(Constant.TABLE_NAME, cv, Constant._ID + "=?", new String[]{"3"});
                if (result > 0) {
                    Toast.makeText(MainActivity.this, "修改数据成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "修改数据失败", Toast.LENGTH_SHORT).show();
                }
                db.close();
                break;
            /**
             * String table 数据库表名
             * String whereClause  where 子句，修改的条件
             * String whereArgs 修改条件的占位符
             * 返回值 int 修改的条数
             */
            case R.id.btnDeleteApi:
                db = helper.getWritableDatabase();
                int count1 = 0;
                //int result1=db.update(Constant.TABLE_NAME, value, Constant._ID+ "=3", null);
                for (int m = 50; m <= 80; m++) {
                    db.delete(Constant.TABLE_NAME, Constant._ID + "=?", new String[]{"+m+"});
                    count = m;
                }
                //int count1=db.delete(Constant.TABLE_NAME,Constant._ID+"=?",new String[]{"30"});
                if (count1 > 0) {
                    Toast.makeText(MainActivity.this, "删除数据成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "删除数据失败", Toast.LENGTH_SHORT).show();
                }
                db.close();
                break;
        }
    }
    public void onQueryClick(View view) {
        switch (view.getId()) {
            case R.id.btnQuery:
                SQLiteDatabase db = helper.getWritableDatabase();
                String querySql = "select * from " + Constant.TABLE_NAME;
                Cursor cursor = DbManager.selectDataBySql(db, querySql, null);
                List<Person> list = DbManager.cursorToList(cursor);
                for (Person person : list) {
                    Log.e("tag", person.toString());
                }
                db.close();
                break;
            /**
             * String table 表示查询的表名 String[] columns 表示查询表中的字段名称 null表示查询所有
             * String selection 表示查询条件 where子句 String[] selectionArgs
             * 表示查询条件占位符的取值 String groupBy 表示分组条件 group by子句 String having
             * 表示筛选条件 having子句 String orderBy 表示排序条件 order by子句
             */
            case R.id.btnQueryApi:
                db = helper.getWritableDatabase();
                Cursor cursor1 = db.query(Constant.TABLE_NAME, null, Constant._ID + ">?", new String[]{"3"}, null, null, Constant._ID + " desc");
                List<Person> list1 = DbManager.cursorToList(cursor1);
                for (Person person : list1) {
                    Log.e("tag", person.toString());
                }
                db.close();
                break;
        }
    }
}
