package com.aratek.trustfinger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aratek.trustfinger.R;
import com.aratek.trustfinger.bean.User;

import java.util.List;

/**
 * Created by hecl on 2018/9/22.
 */

public class MyRankListAdapter extends BaseAdapter {
    private Context context;
    private List<User> mUserList;

    public MyRankListAdapter(Context context, List<User> userList) {
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
                    R.layout.rank_user_list_item, null);
            holder = new ViewHolder();
            holder.mTextView_rank = (TextView) convertView
                    .findViewById(R.id.tv_rank);
            holder.mTextView_user_id = (TextView) convertView
                    .findViewById(R.id.tv_user_id);
            holder.mTextView_user_firstName = (TextView) convertView
                    .findViewById(R.id.tv_user_first_name);
            holder.mTextView_user_lastName = (TextView) convertView
                    .findViewById(R.id.tv_user_last_name);
            holder.mTextView_similarity = (TextView) convertView
                    .findViewById(R.id.tv_similarity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = mUserList.get(position);
        int rank = -1;
        String id = null;
        String firstName = null;
        String lastName = null;
        int score = -1;

        if (user != null) {
            rank = user.getRank();
            id = user.getId();
            firstName = user.getFirstName();
            lastName = user.getLastName();
            score = user.getSimilarity();
            if (rank != -1) {
                holder.mTextView_rank.setText("" + rank);
            }
            if (id != null) {
                holder.mTextView_user_id.setText(id);
            }
            if (firstName != null) {
                holder.mTextView_user_firstName.setText(firstName);
            }
            if (lastName != null) {
                holder.mTextView_user_lastName.setText(lastName);
            }
            if (score != -1) {
                holder.mTextView_similarity.setText("" + score);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTextView_rank;
        TextView mTextView_user_id;
        TextView mTextView_user_firstName;
        TextView mTextView_user_lastName;
        TextView mTextView_similarity;
    }
}
