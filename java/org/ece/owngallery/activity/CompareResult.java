package org.ece.owngallery.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.ece.owngallery.ApplicationOwnGallery;
import org.ece.owngallery.R;
import org.ece.owngallery.adapter.BaseFragmentAdapter;
import org.opencv.core.Mat;

import java.util.ArrayList;


public class CompareResult extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private GridView mView;
    private Toolbar toolbar;

    private int itemWidth = 100;
    private ListAdapter listAdapter;
    private int AlbummID=0;

    public ArrayList<AlbumEntry> albumsSorted = new ArrayList<AlbumEntry>();
    public ArrayList<PhotoEntry> photos = new ArrayList<PhotoEntry>();
    public AlbumEntry compareAlbum = null;

    int ret = 0;
    private Mat img1;
    private Mat img2;

    private Button btn3;
    private String path3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparealbum);

        a();
        initializeActionBar();
        initializeView();
    }

    private void a() {
        img1 = new Mat();
        img2 = new Mat();

        Bundle mBundle = getIntent().getExtras();
        String path1 = mBundle.getString("Key_Name");
        loadImage1(path1, img1.getNativeObjAddr());

        String[] projectionPhotos = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor imageCursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projectionPhotos, "", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");

        if (imageCursor != null) {
            int imageIdColumn = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
            int bucketIdColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int bucketNameColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int dataColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int dateColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int orientationColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);

            while (imageCursor.moveToNext()) {
                int imageId = imageCursor.getInt(imageIdColumn);
                int bucketId = imageCursor.getInt(bucketIdColumn);
                String bucketName = imageCursor.getString(bucketNameColumn);
                String path = imageCursor.getString(dataColumn);
                long dateTaken = imageCursor.getLong(dateColumn);
                int orientation = imageCursor.getInt(orientationColumn);
                PhotoEntry photoEntry = new PhotoEntry(bucketId, imageId, dateTaken, path, orientation);

                loadImage2(path, img2.getNativeObjAddr());
                ret = compare(img1.getNativeObjAddr(), img2.getNativeObjAddr());

                if (compareAlbum == null) {
                    compareAlbum = new AlbumEntry(0, "결과", photoEntry);
                    albumsSorted.add(0, compareAlbum);
                }
                if (compareAlbum != null) {
                    if (ret == 1) {
                        compareAlbum.addPhoto(photoEntry);
                    }
                }
                ret = 0;
            }
            imageCursor.close();
        }
    }


    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList<PhotoEntry>();

        public AlbumEntry(int bucketId, String bucketName, PhotoEntry coverPhoto) {
            this.bucketId = bucketId;
            this.bucketName = bucketName;
            this.coverPhoto = coverPhoto;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            photos.add(photoEntry);
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public int imageId;
        public long dateTaken;
        public String path;
        public int orientation;

        public PhotoEntry(int bucketId, int imageId, long dateTaken,String path, int orientation) {
            this.bucketId = bucketId;
            this.imageId = imageId;
            this.dateTaken = dateTaken;
            this.path = path;
            this.orientation = orientation;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeActionBar() {

        photos=albumsSorted.get(AlbummID).photos;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("결과");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initializeView() {
        mView = (GridView) findViewById(R.id.grid_view);
        mView.setAdapter(listAdapter = new ListAdapter(CompareResult.this));
        btn3 = (Button)findViewById(R.id.button2);
        int columnsCount = 2;
        mView.setNumColumns(columnsCount);
        itemWidth = (ApplicationOwnGallery.displaySize.x - ((columnsCount + 1) * ApplicationOwnGallery.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

        listAdapter.notifyDataSetChanged();
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PhotoEntry mPhotoEntry = photos.get(position);
                path3 = mPhotoEntry.path;
                btn3.setText(path3);
            }
        });
    }


    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public ListAdapter(Context context) {
            this.mContext = context;
            this.layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.nophotos)
                    .showImageForEmptyUri(R.drawable.nophotos)
                    .showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
                    .cacheOnDisc(true).considerExifParams(true).build();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public int getCount() {
            return photos != null ? photos.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            viewHolder mHolder;
            if (view == null) {
                mHolder = new viewHolder();
                view = layoutInflater.inflate(R.layout.album_image, viewGroup,false);
                mHolder.imageView = (ImageView) view.findViewById(R.id.album_image);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = itemWidth;
                params.height = itemWidth;
                view.setLayoutParams(params);
                mHolder.imageView.setTag(i);

                view.setTag(mHolder);
            } else {
                mHolder = (viewHolder) view.getTag();
            }
            PhotoEntry mPhotoEntry = photos.get(i);
            String path = mPhotoEntry.path;
            if (path != null && !path.equals("")) {
                ImageLoader.getInstance().displayImage("file://" + path, mHolder.imageView);
            }

            return view;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEmpty() {
            return albumsSorted == null || albumsSorted.isEmpty();
        }

        class viewHolder {
            public ImageView imageView;
        }

    }
    public native void loadImage1(String imageFileName, long img);
    public native void loadImage2(String imageFileName, long img);
    public native int compare(long Image1, long Image2);

}