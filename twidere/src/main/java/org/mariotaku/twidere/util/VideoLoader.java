/*
 * Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.util;

import android.content.Context;

import com.squareup.otto.Bus;

import org.mariotaku.twidere.app.TwidereApplication;
import org.mariotaku.twidere.model.SingleResponse;
import org.mariotaku.twidere.task.ManagedAsyncTask;
import org.mariotaku.twidere.util.message.VideoLoadFinishedEvent;

import java.io.File;


/**
 * Created by mariotaku on 14-8-13.
 */
public class VideoLoader {

    private final Context mContext;
    private final AsyncTaskManager mTaskManager;
    private final Bus mBus;

    public VideoLoader(Context context) {
        final TwidereApplication app = TwidereApplication.getInstance(context);
        mContext = context;
        mTaskManager = app.getAsyncTaskManager();
        mBus = app.getMessageBus();
    }

    public File getCachedVideoFile(final String url, boolean loadIfNotFound) {
        if (url == null) return null;
//        final File cache = mDiskCache.get(url);
//        if (cache.exists())
//            return cache;
//        else if (loadIfNotFound) {
//            loadVideo(url, null);
//        }
        return null;
    }

    public boolean isLoading(String url) {
        return mTaskManager.hasRunningTasksForTag(url);
    }


    public int loadVideo(String uri, VideoLoadingListener listener) {
        if (mTaskManager.hasRunningTasksForTag(uri)) {
            return 0;
        }
        return mTaskManager.add(new PreLoadVideoTask(mContext, this, listener, uri), true);
    }

    private void notifyTaskFinish(String uri, boolean succeeded) {
        mBus.post(new VideoLoadFinishedEvent());
    }

    public interface VideoLoadingListener {

        void onVideoLoadingCancelled(String uri, VideoLoadingListener listener);

        void onVideoLoadingComplete(String uri, VideoLoadingListener listener, File file);

        void onVideoLoadingFailed(String uri, VideoLoadingListener listener, Exception e);

        void onVideoLoadingProgressUpdate(String uri, VideoLoadingListener listener, int current, int total);

        void onVideoLoadingStarted(String uri, VideoLoadingListener listener);
    }

    private static class PreLoadVideoTask extends ManagedAsyncTask<Object, Integer, SingleResponse<File>> {

        private final VideoLoader mPreLoader;
        private final VideoLoadingListener mListener;
        private final String mUri;

        private PreLoadVideoTask(final Context context, final VideoLoader preLoader, VideoLoadingListener listener, final String uri) {
            super(context, preLoader.mTaskManager, uri);
            mPreLoader = preLoader;
            mListener = listener;
            mUri = uri;
        }

        public boolean onBytesCopied(int current, int total) {
            if (isCancelled()) return false;
            publishProgress(current, total);
            return true;
        }

        @Override
        protected SingleResponse<File> doInBackground(Object... params) {
//            final File file = mPreLoader.mDiskCache.get(mUri);
//            if (file.isFile() && file.length() > 0) return SingleResponse.getInstance(file);
//            try {
//                final InputStream is = mPreLoader.mImageDownloader.getStream(mUri, null);
//                mPreLoader.mDiskCache.save(mUri, is, this);
//                IoUtils.closeSilently(is);
//            } catch (IOException e) {
//                mPreLoader.mDiskCache.remove(mUri);
//                Log.w(LOGTAG, e);
//                return SingleResponse.getInstance(e);
//            }
            return SingleResponse.getInstance();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mListener != null) {
                mListener.onVideoLoadingProgressUpdate(mUri, mListener, values[0], values[1]);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mListener != null) {
                mListener.onVideoLoadingStarted(mUri, mListener);
            }
        }

        @Override
        protected void onPostExecute(SingleResponse<File> result) {
            super.onPostExecute(result);
            if (mListener != null) {
                if (result.hasData()) {
                    mListener.onVideoLoadingComplete(mUri, mListener, result.getData());
                } else {
                    mListener.onVideoLoadingFailed(mUri, mListener, result.getException());
                }
            }
            mPreLoader.notifyTaskFinish(mUri, result.hasData());
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mListener != null) {
                mListener.onVideoLoadingCancelled(mUri, mListener);
            }
        }
    }

}
