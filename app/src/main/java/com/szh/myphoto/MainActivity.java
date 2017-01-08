package com.szh.myphoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ = 1;
    private GridView gridView;
    private TextView number;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                            intent.putExtra("Position", position);
                            MainActivity.this.startActivity(intent);
                        }
                    });
                    number.setText(String.valueOf(msg.arg1));
                    number.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridview);
        number = (TextView) findViewById(R.id.number);
        readPermission();
    }

    @SuppressLint("InlinedApi")
    public void readPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ);
        } else
        {
            getPicture();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getPicture();
            } else
            {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPicture() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Uri uri = MediaStore.Files.getContentUri("external");
        Cursor cursor = null;
        try {
            cursor = this.getContentResolver().query(uri, null, null, null, null);
            ListsInfo.list = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
//            String width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
//            String height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
//            if (mime != null && width != null && height != null && mime.matches("^image.*")
//                    && Integer.parseInt(width) > 100 && Integer.parseInt(height) > 100) {
                ListsInfo.list.add(path);
//            }
            }
        }finally {
            cursor.close();
        }

        gridView.setAdapter(new GridAdapter(this, ListsInfo.list));
        Message message = Message.obtain();
        message.what = 0;
        message.arg1 = ListsInfo.list.size();
        handler.sendMessage(message);
    }

    public static class GridAdapter extends BaseAdapter {
        private Context context;
        private List<String> list;
        private LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            this.context = context;
            inflater = LayoutInflater.from(context);
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.grid_item, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            Glide.with(context).load(list.get(position)).skipMemoryCache(false).into(holder.imageView);
            return convertView;
        }

        private static class ViewHolder {
            ImageView imageView;
        }
    }
}
