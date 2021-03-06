package com.wm.remusic.fragment;


import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wm.remusic.R;

import java.lang.ref.WeakReference;


/**
 * Created by wm on 2016/3/11.
 */
public class RoundFragment extends Fragment {

    private WeakReference<ObjectAnimator> animatorWeakReference;
    private SimpleDraweeView sdv;
    private long musicId = -1;
    private ObjectAnimator animator;
    private String albumPath;

    public static RoundFragment newInstance(String albumpath) {
        RoundFragment fragment = new RoundFragment();
        Bundle bundle = new Bundle();
        bundle.putString("album", albumpath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_roundimage, container, false);
        ((ViewGroup) rootView).setAnimationCacheEnabled(false);
        if (getArguments() != null) {
            albumPath = getArguments().getString("album");
        }
        //  CircleImageView  circleImageView = (CircleImageView) rootView.findViewById(R.id.circle);

        sdv = (SimpleDraweeView) rootView.findViewById(R.id.sdv);


        //?????????????????????????????????
        RoundingParams rp = new RoundingParams();
        //???????????????????????????
        rp.setRoundAsCircle(true);
        //??????????????????
        //rp.setCornersRadius(20);
        //????????????????????????????????????????????????????????????????????????
        //rp.setCornersRadii(20,25,30,35);
        //??????????????????2??????????????????(3???4)????????????(5???6)????????????(7???8)????????????????????????
        //rp.setCornersRadii(new float[]{20,25,30,35,40,45,50,55});
        //??????????????????????????????
        rp.setBorder(Color.BLACK, 6);

        //??????GenericDraweeHierarchy??????
        GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(getResources())
                //????????????????????????
                .setRoundingParams(rp)
                //??????????????????
                //.setRoundingParams(RoundingParams.fromCornersRadius(20))
                //????????????????????????????????????????????????????????????????????????
                //.setRoundingParams(RoundingParams.fromCornersRadii(20,25,30,35))
                //??????????????????2??????????????????(3???4)????????????(5???6)????????????(7???8)????????????????????????
                //.setRoundingParams(RoundingParams.fromCornersRadii(new float[]{20,25,30,35,40,45,50,55}))
                //???????????????????????????RoundingParams.asCircle()???????????????????????????
                //.setRoundingParams(RoundingParams.asCircle())
                //????????????????????????????????????(???????????????ms)
                .setFadeDuration(300)
                //??????
                .build();


        //??????Hierarchy
        sdv.setHierarchy(hierarchy);
        //Log.e("music id",musicId + "");
//        String uri = MusicUtils.getAlbumdata(getContext().getApplicationContext(), musicId);
//
//        if (musicId != -1 && uri != null) {
//            //circleImageView.setImageBitmap(bitmap);
//            //circleImageView.setImageURI(Uri.parse(uri));
//            Uri ur = MusicUtils.getAlbumUri(getContext().getApplicationContext(), musicId);
//            sdv.setImageURI(ur);
//        } else {
//
//            // circleImageView.setImageResource(R.drawable.placeholder_disk_play_song);
//            Uri urr = Uri.parse("res:/" + R.drawable.placeholder_disk_play_song);
//            sdv.setImageURI(urr);
//        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                //FLog.d("Intermediate image received");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                sdv.setImageURI(Uri.parse("res:/" + R.drawable.placeholder_disk_play_song));
            }
        };
        if (albumPath == null) {
            sdv.setImageURI(Uri.parse("res:/" + R.drawable.placeholder_disk_play_song));
        } else {
            try {

                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(albumPath)).build();

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(sdv.getController())
                        .setImageRequest(request)
                        .setControllerListener(controllerListener)
                        .build();

                sdv.setController(controller);
                //  sdv.setImageBitmap(BitmapFactory.decodeStream(HttpUtil.getFromCache(getActivity(),getAlbumPath())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        animatorWeakReference = new  WeakReference<ObjectAnimator>(new ObjectAnimator());
//        animator = animatorWeakReference.get();
        animatorWeakReference = new WeakReference(ObjectAnimator.ofFloat(getView(), "rotation", new float[]{0.0F, 360.0F}));
        animator = animatorWeakReference.get();
        //animator = ObjectAnimator.ofFloat(getView(), "rotation", new float[]{0.0F, 360.0F});
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator.setDuration(25000L);
        animator.setInterpolator(new LinearInterpolator());

        if (getView() != null)
            getView().setTag(R.id.tag_animator, this.animator);
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //?????????Fragment???onResume

        } else {
            //?????????Fragment???onPause

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("roundfragment"," id = " + hashCode());
        if (animator != null) {
            animator = null;
            Log.e("roundfragment"," id = " + hashCode() + "  , animator destroy");
        }
//        RefWatcher refWatcher = MainApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }


}
