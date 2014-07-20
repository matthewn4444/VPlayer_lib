/*
 * VPlayerView.java
 * Copyright (c) 2014 Matthew Ng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vplayer;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class VPlayerView extends VPlayerSurfaceView {
    public static final int UNKNOWN_STREAM = VPlayerController.UNKNOWN_STREAM;
    public static final int NO_STREAM = VPlayerController.NO_STREAM;

    private final VPlayerController mPlayer;
    private final Activity mAct;
    private boolean mIsPlaying;
    private boolean mAlreadyFinished;

    public VPlayerView(Activity activity) {
        this(activity, null);
    }

    public VPlayerView(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    public VPlayerView(Activity activity, AttributeSet attrs, int defStyle) {
        super(activity, attrs, defStyle);
        mAct = activity;
        mPlayer = new VPlayerController(this, activity);
        mIsPlaying = false;
        mAlreadyFinished = false;
    }

    public void setWindowFullscreen() {
        mAct.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAct.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mAct.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void clearWindowFullscreen() {
        mAct.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAct.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setDateSource(String path) {
        mPlayer.setDataSource(path);
    }

    public void setDateSource(String path, String fontPath) {
        setDateSource(path, fontPath, null);
    }

    public void setDateSource(String path, int videoStreamIndex, int audioStreamIndex) {
        setDateSource(path, null, null, videoStreamIndex, audioStreamIndex, NO_STREAM);
    }

    public void setDateSource(String path, String fontPath, String encryptionKey) {
        setDateSource(path, fontPath, encryptionKey, UNKNOWN_STREAM, UNKNOWN_STREAM, UNKNOWN_STREAM);
    }

    public void setDateSource(String path, String fontPath, String encryptionKey,
            int videoStreamIndex, int audioStreamIndex, int subtitleStreamIndex) {
        Map<String, String> map = new HashMap<String, String>();
        if (fontPath != null) {
            map.put(VPlayerController.FONT_MAP_KEY, fontPath);
        }
        if (encryptionKey != null) {
            map.put(VPlayerController.ENCRYPTION_KEY, encryptionKey);
        }
        mPlayer.setDataSource(path, map, videoStreamIndex, audioStreamIndex, subtitleStreamIndex);
    }

    public void setVideoListener(VPlayerListener listener) {
        mPlayer.setMpegListener(listener);
    }

    public void onPause() {
        if (mIsPlaying && !mAlreadyFinished) {
            mPlayer.pause();
        }
    }

    public void onResume() {
        if (mIsPlaying && !mAlreadyFinished) {
            mPlayer.resume();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);

        // When coming back to the video from somewhere else as paused,
        // we render the previous frame or else it will show black screen
        if (!mIsPlaying) {
            mPlayer.renderLastNativeFrame();
        }
    }

    @Override
    public void finish() {
        super.finish();
        mPlayer.stop();
        mAlreadyFinished = true;
    }

    public void pause() {
        mIsPlaying = false;
        if (!mAlreadyFinished) {
            mPlayer.pause();
        }
    }

    public void seek(long positionUs) {
        if (!mAlreadyFinished) {
            mPlayer.seek(positionUs);
        }
    }

    public void play() {
        mIsPlaying = true;
        if (!mAlreadyFinished) {
            mPlayer.resume();
        }
    }

    public void stop() {
        pause();
        seek(0);
    }

    public int getVideoWidth() {
        return mPlayer.getVideoWidth();
    }

    public int getVideoHeight() {
        return mPlayer.getVideoHeight();
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }
}
