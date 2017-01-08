package com.szh.myphoto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by szh on 2017/1/8.
 */
public class BaseFragment extends Fragment {

    private PhotoView photoView;

    private int position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photoview, null);
        photoView = (PhotoView) view.findViewById(R.id.photoview);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    ((FullscreenActivity)getActivity()).toggle();
                }
            });
        photoView.setMaximumScale(20);
        photoView.setMediumScale(2);
        photoView.setMinimumScale(1);
        Glide.with(this).load(ListsInfo.list.get(position)).into(new ImageViewTarget<GlideDrawable>(photoView) {

            @Override
            protected void setResource(GlideDrawable resource) {
                photoView.setImageDrawable(resource);
            }
        });
        return view;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
