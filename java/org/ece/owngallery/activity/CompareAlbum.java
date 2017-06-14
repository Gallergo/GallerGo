package org.ece.owngallery.activity;

import java.util.ArrayList;

import org.ece.owngallery.ApplicationOwnGallery;
import org.ece.owngallery.R;
import org.ece.owngallery.adapter.BaseFragmentAdapter;
import org.ece.owngallery.component.PhoneMediaControl;
import org.ece.owngallery.component.PhoneMediaControl.PhotoEntry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CompareAlbum extends ActionBarActivity {

    public static final String PACKAGE = "org.ece.owngallery";
    private Toolbar toolbar;
    private GridView mView;
    private Context mContext;

    public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
    public static ArrayList<PhotoEntry> photos = new ArrayList<PhotoEntry>();

    private int itemWidth = 100;
    private ListAdapter listAdapter;
    private int AlbummID = 0;
    private Button btn;
    private String path1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparealbum);


        mContext = CompareAlbum.this;
        initializeActionBar();
        initializeView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.confirm:
                Bundle mBundle=new Bundle();
                Intent resultmIntent = new Intent(mContext, CompareResult.class);
                mBundle.putString("Key_Name", path1+"");
                resultmIntent.putExtras(mBundle);
                mContext.startActivity(resultmIntent);
                break;
            case R.id.cancel:
                Intent mIntent = new Intent(mContext, ActivityHome.class);
                mContext.startActivity(mIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeActionBar() {

        Bundle mBundle=getIntent().getExtras();
        String nameAlbum = mBundle.getString("Key_Name");
        AlbummID =Integer.parseInt(mBundle.getString("Key_ID")) ;
        albumsSorted=CompareGalleryFragment.albumsSorted;

        photos=albumsSorted.get(AlbummID).photos;

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("사진을 선택해주세요.");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeView(){
        mView=(GridView)findViewById(R.id.grid_view2);
        mView.setAdapter(listAdapter = new ListAdapter(CompareAlbum.this));
        btn = (Button)findViewById(R.id.button2);

        int position = mView.getFirstVisiblePosition();
        int columnsCount = 2;
        mView.setNumColumns(columnsCount);
        itemWidth = (ApplicationOwnGallery.displaySize.x - ((columnsCount + 1) * ApplicationOwnGallery.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

        listAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PhotoEntry mPhotoEntry = photos.get(position);
                path1 = mPhotoEntry.path;
                btn.setText(path1);
            }
        });
        LoadAllAlbum();
    }

    private void LoadAllAlbum(){
        if (mView != null && mView.getEmptyView() == null) {
            mView.setEmptyView(null);
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
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

}
