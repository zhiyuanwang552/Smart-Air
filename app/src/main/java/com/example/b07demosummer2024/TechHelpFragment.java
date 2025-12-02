package com.example.b07demosummer2024;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.Nullable;

public class TechHelpFragment extends Fragment {

    // 1. 将成员变量改为 VideoView
    private VideoView vvTechHelp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tech_help, container, false);

        vvTechHelp = view.findViewById(R.id.vvTechHelp);

        try {
            Uri videoUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.tech_help_vedio);
            vvTechHelp.setVideoURI(videoUri);

            MediaController mediaController = new MediaController(getContext());
            vvTechHelp.setMediaController(mediaController);
            mediaController.setAnchorView(vvTechHelp);

            vvTechHelp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    vvTechHelp.start();
                }
            });

            vvTechHelp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(getContext(), "Error playing video", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error setting up video player.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (vvTechHelp != null && vvTechHelp.isPlaying()) {
            vvTechHelp.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (vvTechHelp != null) {
            vvTechHelp.stopPlayback();
        }
    }
}
