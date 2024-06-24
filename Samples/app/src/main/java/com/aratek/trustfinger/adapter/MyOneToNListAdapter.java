//package com.aratek.trustfinger.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.aratek.trustfinger.R;
//import com.aratek.trustfinger.bean.User;
//
//import java.util.List;
//
///**
// * Created by hecl on 2018/9/22.
// */
//
//public class MyOneToNListAdapter extends BaseAdapter {
//    private Context context;
//    private List<User> mUserList;
//
//    public MyOneToNListAdapter(Context context, List<User> userList) {
//        super();
//        this.context = context;
//        mUserList = userList;
//    }
//
//    @Override
//    public int getCount() {
//        return mUserList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mUserList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        if (null == convertView) {
//            convertView = (LinearLayout) LayoutInflater.from(context).inflate(
//                    R.layout.one_to_n_user_list_item, null);
//            holder = new ViewHolder();
//            holder.mTextView_rank = (TextView) convertView
//                    .findViewById(R.id.tv_rank);
//            holder.mTextView_finger_id = (TextView) convertView
//                    .findViewById(R.id.tv_finger_id);
//            holder.mTextView_similarity = (TextView) convertView
//                    .findViewById(R.id.tv_similarity);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        User user = mUserList.get(position);
//        int rank = -1;
//        String id = null;
//        int score = -1;
//
//        if (user != null) {
//            rank = user.getRank();
//            id = user.getId();
//            score = user.getSimilarity();
//            if (rank != -1) {
//                holder.mTextView_rank.setText("" + rank);
//            }
//            if (id != null) {
//                holder.mTextView_finger_id.setText(id);
//            }
//            if (score != -1) {
//                holder.mTextView_similarity.setText("" + score);
//            }
//        }
//
//        return convertView;
//    }
//
//    class ViewHolder {
//        TextView mTextView_rank;
//        TextView mTextView_finger_id;
//        TextView mTextView_similarity;
//    }
//}
