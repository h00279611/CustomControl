package com.example.slidemenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slidemenu.menu.SlideItemLayout;
import com.example.slidemenu.menu.SlideListener;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private final List<MyBean> myBeans;
    private final Context context;

    public MyAdapter(Context context, List<MyBean> myBeans) {
        this.myBeans = myBeans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return myBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return myBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(context,R.layout.item_main, null);
            viewHolder = new ViewHolder();
            viewHolder.item_content = convertView.findViewById(R.id.item_content);
            viewHolder.item_menu = convertView.findViewById(R.id.item_menu);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final MyBean myBean = myBeans.get(position);
        viewHolder.item_content.setText(myBean.getName());

        viewHolder.item_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, myBean.getName(), Toast.LENGTH_SHORT).show();
            }
        });


        viewHolder.item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先关闭menuView菜单，再刷新list
                SlideItemLayout itemLayout = (SlideItemLayout) v.getParent();
                itemLayout.closeMenu();

                myBeans.remove(myBean);
                notifyDataSetChanged();
            }
        });


        ((SlideItemLayout)convertView).setSlideListener(new MySlideListener());
        return convertView;
    }



    private SlideItemLayout preSlideItemLayout;

    class MySlideListener implements SlideListener{

        @Override
        public void onDown(SlideItemLayout itemLayout) {
            if (preSlideItemLayout != null && preSlideItemLayout != itemLayout) {
                preSlideItemLayout.closeMenu();
            }
        }

        @Override
        public void onOpen(SlideItemLayout itemLayout) {
            preSlideItemLayout = itemLayout;
        }

        @Override
        public void onClose(SlideItemLayout itemLayout) {
            if (preSlideItemLayout == itemLayout) {
                preSlideItemLayout = null;
            }
        }
    }


    static class ViewHolder{
        TextView item_content;
        TextView item_menu;
    }
}
