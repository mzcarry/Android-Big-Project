package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.adapter.ModeAdapter;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends Activity{
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private RecyclerView mModeListView;
    private List<String> mList = new ArrayList<String>();
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private boolean isRecording = false;
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private int mode = MODE_PIC;

    private ImageView btn_shutter;
    private ImageView btn_mode;
    private ImageView btn_sticker;

    private ObjectAnimator animator;

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder.build((MagicCameraView)findViewById(R.id.glsurfaceview_camera));


        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        System.out.println("width: ");
        System.out.println(width);

        //mode_select  recyclerView
        mModeListView = (RecyclerView)findViewById(R.id.mode_select_listView);
        mList.add("");
        mList.add("拍照");
        mList.add("长视频");
        mList.add("");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        //设置为横向滑动
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mModeListView.setLayoutManager(manager);
        ModeAdapter modeAdapter = new ModeAdapter(mList);
        mModeListView.setAdapter(modeAdapter);
        initView();

    }



    private void initView(){
        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);

        btn_shutter = (ImageView)findViewById(R.id.btn_camera_shutter);
        btn_mode = (ImageView)findViewById(R.id.btn_camera_mode);
        btn_sticker = (ImageView)findViewById(R.id.btn_camera_sticker);

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        //findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_mode).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_sticker).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_filter1).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_lip).setOnClickListener(btn_listener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        animator = ObjectAnimator.ofFloat(btn_shutter,"rotation",0,360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicCameraView cameraView = (MagicCameraView)findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.y;//x* 4 / 3;
        cameraView.setLayoutParams(params);

    }

    /////开始？？？
    public boolean onTouchEvent(MotionEvent event)  {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {//MotionEvent.ACTION_MOVE
            return false;
        }

        hideFilters();
        findViewById(R.id.mode_select_listView).setVisibility(View.VISIBLE);
        return true;
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(mode == MODE_PIC)
                takePhoto();
            else
                takeVideo();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_camera_mode:
                    switchMode();
                    break;
                case R.id.btn_camera_shutter:
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                v.getId());
                    } else {
                        if(mode == MODE_PIC)
                            takePhoto();
                        else
                            takeVideo();
                    }
                    break;
                case R.id.btn_camera_filter:
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small_pressed);
                    findViewById(R.id.mode_select_listView).setVisibility(View.GONE);
                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    showFilters();
                    break;
                case R.id.btn_camera_filter1:
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small_pressed);
                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip);
                    //showFilters();
                    break;
                case R.id.btn_camera_switch:
                    magicEngine.switchCamera();
                    break;
                case R.id.btn_camera_beauty:
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);
                    new AlertDialog.Builder(CameraActivity.this)
                            .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        magicEngine.setBeautyLevel(which);
                                        dialog.dismiss();
                                    }
                                })
                            .setNegativeButton("取消", null)
                            .show();
                    break;
                case R.id.btn_camera_sticker:
                    break;
                case R.id.btn_camera_lip:
                    findViewById(R.id.btn_camera_lip).setBackgroundResource(R.drawable.btn_camera_lip_pressed);
                    findViewById(R.id.btn_camera_filter1).setBackgroundResource(R.drawable.btn_camera_filter_small);
                    findViewById(R.id.btn_camera_beauty).setBackgroundResource(R.drawable.btn_camera_beauty);
                    break;
            }
        }
    };

    private void switchMode(){
        if(mode == MODE_PIC){
            mode = MODE_VIDEO;
            btn_mode.setImageResource(R.drawable.icon_camera);
        }else{
            mode = MODE_PIC;
            btn_mode.setImageResource(R.drawable.icon_video);
        }
    }

    private void takePhoto(){
        magicEngine.savePicture(getOutputMediaFile(),null);
    }

    private void takeVideo(){
        if(isRecording) {
            animator.end();
            magicEngine.stopRecord();
        }else {
            animator.start();
            magicEngine.startRecord();
        }
        isRecording = !isRecording;
    }

    private void showFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight()+1000, 1280);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
               // findViewById(R.id.btn_camera_shutter).setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_camera_sticker).setVisibility(View.GONE);
                findViewById(R.id.btn_camera_filter).setVisibility(View.GONE);
                ObjectAnimator animator_shutter_scalex = ObjectAnimator.ofFloat(btn_shutter,"scaleX", 1.0f, 0.7f);
                ObjectAnimator animator_shutter_scaley = ObjectAnimator.ofFloat(btn_shutter,"scaleY", 1.0f, 0.7f);
                animator_shutter_scaley.setDuration(200);
                animator_shutter_scalex.setDuration(200);
                animator_shutter_scalex.start();
                animator_shutter_scaley.start();
                ObjectAnimator animator_shutter_trany = ObjectAnimator.ofFloat(btn_shutter, "translationY", 150, 100);
                animator_shutter_trany.setDuration(200);
                animator_shutter_trany.start();

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private void hideFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 1280 ,  mFilterLayout.getHeight()+1000);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
                findViewById(R.id.btn_camera_sticker).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_camera_filter).setVisibility(View.VISIBLE);
                ObjectAnimator animator_shutter_scalex = ObjectAnimator.ofFloat(btn_shutter,"scaleX", 0.7f, 1.0f);
                ObjectAnimator animator_shutter_scaley = ObjectAnimator.ofFloat(btn_shutter,"scaleY", 0.7f, 1.0f);
                animator_shutter_scaley.setDuration(200);
                animator_shutter_scalex.setDuration(200);
                animator_shutter_scalex.start();
                animator_shutter_scaley.start();
                ObjectAnimator animator_shutter_trany = ObjectAnimator.ofFloat(btn_shutter, "translationY", 80, 0);
                animator_shutter_trany.setDuration(200);
                animator_shutter_trany.start();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }
        });
        animator.start();
    }

    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MagicCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
