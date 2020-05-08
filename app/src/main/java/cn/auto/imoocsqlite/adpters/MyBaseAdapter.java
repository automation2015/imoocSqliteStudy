package cn.auto.imoocsqlite.adpters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import cn.auto.imoocsqlite.R;
import cn.auto.imoocsqlite.bean.Person;
public class MyBaseAdapter extends BaseAdapter {
    private Context context;
    private List<Person> list;

    public MyBaseAdapter(Context context, List<Person> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<Person> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.tvId = view.findViewById(R.id.tvId);
            viewHolder.tvAge = view.findViewById(R.id.tvAge);
            viewHolder.tvName = view.findViewById(R.id.tvName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvId.setText(list.get(position).get_id() + "");
        viewHolder.tvName.setText(list.get(position).getName());
        viewHolder.tvAge.setText(list.get(position).getAge() + "");
        return view;
    }

    static class ViewHolder {
        TextView tvId, tvAge, tvName;
    }
}
