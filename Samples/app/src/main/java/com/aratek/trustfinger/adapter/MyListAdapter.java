package com.aratek.trustfinger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aratek.trustfinger.R;
import com.aratek.trustfinger.bean.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by hecl on 2018/9/22.
 */

public class MyListAdapter extends BaseAdapter {
    private Context context;
    private List<User> mUserList;

    public MyListAdapter(Context context, List<User> userList) {
        super();
        this.context = context;
        mUserList = userList;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.user_list_item, null);
            holder = new ViewHolder();
            holder.mTextView_user_id = (TextView) convertView
                    .findViewById(R.id.tv_user_id);
            holder.mTextView_user_firstName = (TextView) convertView
                    .findViewById(R.id.tv_user_first_name);
            holder.mTextView_user_lastName = (TextView) convertView
                    .findViewById(R.id.tv_user_last_name);
            holder.mTextView_user_fingerIndex = (TextView) convertView
                    .findViewById(R.id.tv_user_finger_position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = mUserList.get(position);
        String id = null;
        String firstName = null;
        String lastName = null;
        Set<String> fingerIndexs = null;

        if (user != null) {
            id = user.getId();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            fingerIndexs = user.getFingerData().getFingerPositions();
            if (id != null) {
                holder.mTextView_user_id.setText(id);
            }
            if (firstName != null) {
                holder.mTextView_user_firstName.setText(firstName);
            }
            if (lastName != null) {
                holder.mTextView_user_lastName.setText(lastName);
            }
            if (fingerIndexs != null) {
                StringBuffer index_buf = new StringBuffer();
                for (String index : fingerIndexs) {
                    if (index_buf.length() == 15) {
                        index_buf.append("\n");
                    }
                    index_buf.append(index + " ");
                }
                holder.mTextView_user_fingerIndex.setText(index_buf.toString());
            }
        }
        if (position == selectItem) {
            convertView.setBackgroundColor(Color.parseColor("#1D9F9A"));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;

    class ViewHolder {
        TextView mTextView_user_id;
        TextView mTextView_user_firstName;
        TextView mTextView_user_lastName;
        TextView mTextView_user_fingerIndex;
    }
}
