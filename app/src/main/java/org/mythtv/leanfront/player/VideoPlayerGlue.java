/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mythtv.leanfront.player;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.media.PlaybackTransportControlGlue;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.PlaybackControlsRow;

import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter;

import org.mythtv.leanfront.R;
import org.mythtv.leanfront.model.Settings;
import org.mythtv.leanfront.model.Video;

import java.util.concurrent.TimeUnit;

/**
 * Manages customizing the actions in the {@link PlaybackControlsRow}. Adds and manages the
 * following actions to the primary and secondary controls:
 *
 * <ul>
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.RepeatAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.ThumbsDownAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.ThumbsUpAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.SkipPreviousAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.SkipNextAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.FastForwardAction}
 *   <li>{@link androidx.leanback.widget.PlaybackControlsRow.RewindAction}
 * </ul>
 *
 * Note that the superclass, {@link PlaybackTransportControlGlue}, manages the playback controls
 * row.
 */
public class VideoPlayerGlue extends PlaybackTransportControlGlue<LeanbackPlayerAdapter> {

    /** Listens for when skip to next and previous actions have been dispatched. */
    public interface OnActionClickedListener {
        /** Skip to the previous item in the queue. */
        void onPrevious();
        /** Skip to the next item in the queue. */
        void onNext();
        void onPlayCompleted();
        void onZoom();
        void onAspect();
        void onCaption();
        void onPivot();
        void onUpdateProgress();
    }

    private final OnActionClickedListener mActionListener;

    private PlaybackControlsRow.SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow.SkipNextAction mSkipNextAction;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction;
    private PlaybackControlsRow.PictureInPictureAction mPivotAction;
    private ZoomAction mZoomAction;
    private AspectAction mAspectAction;
    private int mSkipFwd;
    private int mSkipBack;
    private int mJump;
    private boolean mActionsVisible;
    private long mOffsetMillis = 0;

    public VideoPlayerGlue(
            Context context,
            LeanbackPlayerAdapter playerAdapter,
            OnActionClickedListener actionListener) {
        super(context, playerAdapter);

        mActionListener = actionListener;

        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(context);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(context);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(context);
        mRewindAction = new PlaybackControlsRow.RewindAction(context);
        mZoomAction = new ZoomAction(context);
        mAspectAction = new AspectAction(context);
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(context);
        mPivotAction = new PlaybackControlsRow.PictureInPictureAction(context);
        mSkipFwd = 1000 * Settings.getInt("pref_skip_fwd");
        mSkipBack = 1000 * Settings.getInt("pref_skip_back");
        mJump = 60000 * Settings.getInt("pref_jump");
    }

    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter adapter) {
        // Order matters, super.onCreatePrimaryActions() will create the play / pause action.
        // Will display as follows:
        // play/pause, previous, rewind, fast forward, next
        //   > /||      |<        <<        >>         >|
        super.onCreatePrimaryActions(adapter);
        adapter.add(mSkipPreviousAction);
        adapter.add(mRewindAction);
        adapter.add(mFastForwardAction);
        adapter.add(mSkipNextAction);
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
        super.onCreateSecondaryActions(adapter);
        adapter.add(mClosedCaptioningAction);
        adapter.add(mZoomAction);
        adapter.add(mAspectAction);
        adapter.add(mPivotAction);
        mActionsVisible = true;
    }

    public void setActions(boolean showActions) {
        if (showActions) {
            if (mActionsVisible)
                return;
            PlaybackControlsRow row =  getControlsRow();
            ArrayObjectAdapter adapter = (ArrayObjectAdapter) row.getPrimaryActionsAdapter();
            adapter.clear();
            onCreatePrimaryActions(adapter);
            adapter.notifyArrayItemRangeChanged(0,adapter.size());
            adapter = (ArrayObjectAdapter) row.getSecondaryActionsAdapter();
            adapter.clear();
            onCreateSecondaryActions(adapter);
            adapter.notifyArrayItemRangeChanged(0,adapter.size());
            mActionsVisible = true;
            onPlayStateChanged();
        }
        else {
            if (!mActionsVisible)
                return;
            PlaybackControlsRow row =  getControlsRow();
            ArrayObjectAdapter adapter = (ArrayObjectAdapter) row.getPrimaryActionsAdapter();
            adapter.clear();
            adapter.notifyArrayItemRangeChanged(0,0);
            adapter = (ArrayObjectAdapter) row.getSecondaryActionsAdapter();
            adapter.clear();
            adapter.notifyArrayItemRangeChanged(0,0);
            mActionsVisible = false;
        }
    }

    @Override
    public void onActionClicked(Action action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action);
            return;
        }
        // Super class handles play/pause and delegates to abstract methods next()/previous().
        super.onActionClicked(action);
    }

    // Should dispatch actions that the super class does not supply callbacks for.
    private boolean shouldDispatchAction(Action action) {
        return action == mRewindAction
                || action == mFastForwardAction
                || action == mClosedCaptioningAction
                || action == mZoomAction
                || action == mAspectAction
                || action == mPivotAction;
    }

    private void dispatchAction(Action action) {
        // Primary actions are handled manually.
        if (action == mRewindAction) {
            rewind();
        } else if (action == mFastForwardAction) {
            fastForward();
        } else if (action == mZoomAction) {
            mActionListener.onZoom();
        } else if (action == mAspectAction) {
            mActionListener.onAspect();
        } else if (action == mClosedCaptioningAction) {
            mActionListener.onCaption();
        } else if (action == mPivotAction) {
            mActionListener.onPivot();
        } else if (action instanceof PlaybackControlsRow.MultiAction) {
            PlaybackControlsRow.MultiAction multiAction = (PlaybackControlsRow.MultiAction) action;
            multiAction.nextIndex();
            // Notify adapter of action changes to handle secondary actions, such as, thumbs up/down
            // and repeat.
            notifyActionChanged(
                    multiAction,
                    (ArrayObjectAdapter) getControlsRow().getSecondaryActionsAdapter());
        }
    }

    private void notifyActionChanged(
            PlaybackControlsRow.MultiAction action, ArrayObjectAdapter adapter) {
        if (adapter != null) {
            int index = adapter.indexOf(action);
            if (index >= 0) {
                adapter.notifyArrayItemRangeChanged(index, 1);
            }
        }
    }

    @Override
    public void next() {
        mActionListener.onNext();
    }

    @Override
    public void previous() {
        mActionListener.onPrevious();
    }

    /** Skips backwards 10 seconds. */
    public void rewind() {
        long newPosition = getCurrentPosition() - mSkipBack;
        newPosition = (newPosition < 0) ? 0 : newPosition;
        getPlayerAdapter().seekTo(newPosition);
    }

    /** Skips forward 10 seconds. */
    public void fastForward() {
        if (myGetDuration() > -1) {
            long newPosition = getCurrentPosition() + mSkipFwd;
            newPosition = (newPosition > myGetDuration()) ? myGetDuration() : newPosition;
            getPlayerAdapter().seekTo(newPosition);
        }
    }

    /** Jumps backwards 5 min. */
    public void jumpBack() {
        long newPosition = getCurrentPosition() - mJump;
        newPosition = (newPosition < 0) ? 0 : newPosition;
        getPlayerAdapter().seekTo(newPosition);
    }

    /** Jumps forward 5 min. */
    public void jumpForward() {
        if (myGetDuration() > -1) {
            long newPosition = getCurrentPosition() + mJump;
            newPosition = (newPosition > myGetDuration()) ? myGetDuration() : newPosition;
            getPlayerAdapter().seekTo(newPosition);
        }
    }

    @Override
    protected void onPlayCompleted() {
        mActionListener.onPlayCompleted();
        super.onPlayCompleted();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return super.onKey(v, keyCode, event);
    }

    @Override
    protected void onUpdateProgress() {
        mActionListener.onUpdateProgress();
        super.onUpdateProgress();
    }

    static int getIconHighlightColor(Context context) {
        TypedValue outValue = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.playbackControlsIconHighlightColor,
                outValue, true)) {
            return outValue.data;
        }
        return context.getResources().getColor(R.color.lb_playback_icon_highlight_no_theme);
    }

    @Override
    public long getCurrentPosition() {
        return super.getCurrentPosition() + mOffsetMillis;
    }

    @Override
    // This method was copied from PlaybackBaseControlGlue
    // so we can modify the duration
    protected void onUpdateDuration() {
        PlaybackControlsRow controlsRow = getControlsRow();
        LeanbackPlayerAdapter adapter = getPlayerAdapter();
        if (controlsRow != null) {
            controlsRow.setDuration(
                    adapter.isPrepared() ? myGetDuration() : -1);
        }
        else
            // This is to satisfy the @CallSuper annotation
            super.onUpdateDuration();
    }

    public long myGetDuration() {
        long duration = getDuration();
        if (duration >= 0)
            duration += mOffsetMillis;
        return duration;
    }

    public long getOffsetMillis() {
        return mOffsetMillis;
    }

    public void setOffsetMillis(long offsetMillis) {
        this.mOffsetMillis = offsetMillis;
    }

    /**
     * An action for displaying a Zoom icon.
     */
    public static class ZoomAction extends PlaybackControlsRow.MultiAction {

        /**
         * Constructor
         * @param context Context used for loading resources.
         */
        public ZoomAction(Context context) {
            this(context, getIconHighlightColor(context));
        }

        /**
         * Constructor
         * @param context Context used for loading resources.
         * @param highlightColor Color for the highlighted icon state.
         */
        public ZoomAction(Context context, int highlightColor) {
            super(Video.ACTION_ZOOM);
            Resources res = context.getResources();
            Drawable[] drawables = new Drawable[1];
            drawables[0] = ResourcesCompat.getDrawable(res, R.drawable.ic_zoom_button, null);
            setDrawables(drawables);
        }
    }

    /**
     * An action for displaying an Aspect icon.
     */
    public static class AspectAction extends PlaybackControlsRow.MultiAction {

        /**
         * Constructor
         * @param context Context used for loading resources.
         */
        public AspectAction(Context context) {
            this(context, getIconHighlightColor(context));
        }

        /**
         * Constructor
         * @param context Context used for loading resources.
         * @param highlightColor Color for the highlighted icon state.
         */
        public AspectAction(Context context, int highlightColor) {
            super(Video.ACTION_ASPECT);
            Resources res = context.getResources();
            Drawable[] drawables = new Drawable[1];
            drawables[0] = ResourcesCompat.getDrawable(res, R.drawable.ic_aspect_button, null);
            setDrawables(drawables);
        }
    }


}
