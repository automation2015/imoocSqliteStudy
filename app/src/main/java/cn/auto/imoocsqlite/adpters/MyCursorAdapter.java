package cn.auto.imoocsqlite.adpters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.auto.imoocsqlite.R;
import cn.auto.imoocsqlite.utils.Constant;

public class MyCursorAdapter extends CursorAdapter {
    //private Context context;
    private Cursor c;

    public MyCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        //this.context=context;
        this.c = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_listview, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvId = view.findViewById(R.id.tvId);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvAge = view.findViewById(R.id.tvAge);
        tvId.setText(cursor.getInt(cursor.getColumnIndex(Constant._ID)) + "");
        tvAge.setText(cursor.getInt(cursor.getColumnIndex(Constant.AGE)) + "");
        tvName.setText(cursor.getString(cursor.getColumnIndex(Constant.NAME)));
    }
}
