package com.aratek.trustfinger.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aratek.trustfinger.R;
import com.aratek.trustfinger.interfaces.FileSelectCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenCaptureFileDialog {

    private ListView listview;
    private List<File> folder = new ArrayList<>();
    private File baseFolder;
    private String path;
    private Context context;
    private Dialog dialog;
    private View view_content;
    private ImageView iv_back;
    private ListAdapter adapter;
    private FileSelectCallBack callback;

    public OpenCaptureFileDialog(final Context context,
                                 final FileSelectCallBack callback, final String path) {
        this.context = context;
        this.callback = callback;
        this.path = path;
        view_content = LayoutInflater.from(context).inflate(
                R.layout.layout_fileselect2, null);
        iv_back = (ImageView) view_content.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (folder.size() > 0 && folder.get(0).getParent().equals(path)) {
                    return;
                }
                if (null == folder || folder.size() <= 0) {
                    baseFolder = baseFolder.getParentFile();
                    folder = filterFile(baseFolder.listFiles());
                }
                else {
                    baseFolder = baseFolder.getParentFile();
                    folder = filterFile(baseFolder.listFiles());
                }
                adapter.notifyDataSetChanged();
            }
        });
        listview = (ListView) view_content.findViewById(R.id.lv_listview);
        baseFolder = new File(path);
        folder = filterFile(baseFolder.listFiles());
        adapter = new ListAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                baseFolder = folder.get(position);
                folder = filterFile(baseFolder.listFiles());
                adapter.notifyDataSetChanged();
            }
        });
    }

    public Dialog getDialog() {
        dialog = new Dialog(context, R.style.cus_dialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams attr = window.getAttributes();
        attr.height = ViewGroup.LayoutParams.MATCH_PARENT;
        attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attr.gravity = Gravity.CENTER;
        dialog.setCancelable(false);
        // dialog.getWindow().setLayout(300, 400);
        dialog.setContentView(view_content);
        Button bt_cancle = (Button) view_content.findViewById(R.id.bt_cancle);
        Button bt_confirm = (Button) view_content.findViewById(R.id.bt_confirm);
        bt_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                callback.backFilePath("");
            }
        });
        bt_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.backFilePath(baseFolder.getAbsolutePath());
            }
        });
        return dialog;
    }

    private List<File> filterFile(File[] files) {
        List<File> filterFiles = new ArrayList<>();
        if (files != null && files.length > 0){
            for (File file : files) {
                if (file.isDirectory()) {
                    filterFiles.add(file);
                }
            }
        }
        return filterFiles;
    }

    class ListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return folder.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return folder.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(
                        R.layout.layout_item_fileselect, null);
                holder = new ViewHolder();
                holder.iv_image = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                convertView = view;
                view.setTag(holder);
            }
            else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            File file = folder.get(position);
            if (file.isDirectory()) {
                holder.iv_image.setBackgroundResource(R.drawable.folder4);
            }
            else {
                holder.iv_image.setBackgroundResource(R.drawable.file4);
            }
            holder.tv_name.setText(file.getName());
            return view;
        }

        class ViewHolder {
            ImageView iv_image;
            TextView tv_name;
        }

    }

}
