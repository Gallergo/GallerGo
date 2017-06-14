package org.ece.owngallery.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


import org.ece.owngallery.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mgarcia on 23-07-2016.
 */
public class ThumbnailGridAdapter extends BaseAdapter {
    public static final String TAG = "ThumbnailGridAdapter";

    private Context mContext;
    private ArrayList<String> imagesFilePaths;
    private LayoutInflater mLayoutInflater;


    public ThumbnailGridAdapter(Context context) {
        this.mContext = context;
        imagesFilePaths = new ArrayList<>(50);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        updateThumbnailGrid();

    }


    public void updateThumbnailGrid() { // Not yet finished
        ContentResolver resolver = mContext.getContentResolver();


        String[] projection = new String[]{BaseColumns._ID, MediaStore.MediaColumns.DATA};

        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String path = cursor.getString(1);
                imagesFilePaths.add(path);
                Log.d(TAG, "Cursor: ID = " + id + ", PATH = " + path);

                // do something meaningful
            } while (cursor.moveToNext());
        }




    }

    @Override
    public int getCount() {
        return imagesFilePaths.size();
    }

    @Override
    public Object getItem(int i) {
        return imagesFilePaths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        if (view == null) {
            //We must create a View:
            view = mLayoutInflater.inflate(R.layout.image_thumbnail, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(mContext)
                .load("file://" + imagesFilePaths.get(i))
                .resize(300,300)
                .centerCrop()
                .error(R.mipmap.ic_error)
                .into(viewHolder.imageThumbnail);

        viewHolder.imageCheckbox.bringToFront();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.imageCheckbox.isChecked())
                    viewHolder.imageCheckbox.setChecked(false);
                else
                    viewHolder.imageCheckbox.setChecked(true);



            }
        });

        Log.d(TAG, "P" + i + " Loaded " + imagesFilePaths.get(i));


        return view;
    }



    static class ViewHolder {
        @BindView(R.id.image_checkbox)
        CheckBox imageCheckbox;
        @BindView(R.id.image_thumbnail)
        ImageView imageThumbnail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
