package edu.tsinghua.spice.Task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.http.RestHttpClient;
import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.mime.FileTypedData;
import org.mariotaku.restfu.http.mime.MultipartTypedBody;
import org.mariotaku.twidere.BuildConfig;
import org.mariotaku.twidere.util.TwitterAPIFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.tsinghua.spice.Utilies.SpiceProfilingUtil;

import static org.mariotaku.twidere.TwidereConstants.LOGTAG;
import static org.mariotaku.twidere.util.Utils.copyStream;

/**
 * Created by Denny C. Ng on 2/20/15.
 */

public class SpiceAsyUploadTask extends AsyncTask<Object, Object, Object> {

    private static final String PROFILE_SERVER_URL = "http://twidere-spice.mariotaku.org:18080/spice/usage";

    private static final String LAST_UPLOAD_DATE = "last_upload_time";
    private static final double MILLSECS_HALF_DAY = 1000 * 60 * 60 * 12;

    private final Context context;
    private final RestHttpClient client;


    public SpiceAsyUploadTask(final Context context) {
        this.context = context;
        this.client = TwitterAPIFactory.getDefaultHttpClient(context);
    }


    public void uploadMultipart(final File file) {

        final String app_root = file.getParent();
        final File tmp_dir = new File(app_root + "/spice");
        if (!tmp_dir.exists()) {
            if (!tmp_dir.mkdirs()) {
                SpiceProfilingUtil.log("cannot create folder spice, do nothing.");
                return;
            }
        }

        final File tmp = new File(tmp_dir, file.getName());
        file.renameTo(tmp);

        try {
            final RestHttpRequest.Builder builder = new RestHttpRequest.Builder();
            builder.url(PROFILE_SERVER_URL);
            builder.method(POST.METHOD);
            final MultipartTypedBody body = new MultipartTypedBody();
            body.add("file", new FileTypedData(tmp));
            builder.body(body);
            client.execute(builder.build());
            SpiceProfilingUtil.log("server has already received file " + tmp.getName());
            tmp.delete();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.w(LOGTAG, e);
                SpiceProfilingUtil.log("server does not receive file " + tmp.getName());
            }
            putBackProfile(context, tmp, file);
        }


    }

    @Override
    protected Object doInBackground(final Object... params) {

        final SharedPreferences prefs = context.getSharedPreferences("spice_data_profiling", Context.MODE_PRIVATE);

        if (prefs.contains(LAST_UPLOAD_DATE)) {
            final long lastUpload = prefs.getLong(LAST_UPLOAD_DATE, System.currentTimeMillis());
            final double deltaDays = (System.currentTimeMillis() - lastUpload) / (MILLSECS_HALF_DAY * 2);
            if (deltaDays < 1) {
                SpiceProfilingUtil.log("Last uploaded was conducted in 1 day ago.");
                return null;
            }
        }

        final File root = context.getFilesDir();
        final File[] spiceFiles = root.listFiles(new SpiceFileFilter());
        uploadToServer(spiceFiles);
        prefs.edit().putLong(LAST_UPLOAD_DATE, System.currentTimeMillis()).apply();
        return null;
    }


    private boolean uploadToServer(final File... files) {
        for (final File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            final String url = PROFILE_SERVER_URL;
            SpiceProfilingUtil.log(url);
            uploadMultipart(file);
        }
        return false;
    }

    public static void putBackProfile(final Context context, final File tmp, final File profile) {
        boolean success;
        if (profile.exists()) {
            try {
                final FileOutputStream os = new FileOutputStream(tmp, true);
                final FileInputStream is = new FileInputStream(profile);
                copyStream(is, os);
                is.close();
                os.close();
                success = true;
            } catch (final IOException e) {
                e.printStackTrace();
                success = false;
            }

            if (success && tmp.renameTo(profile) && tmp.delete()) {
                SpiceProfilingUtil.log("put profile back success");
            } else {
                SpiceProfilingUtil.log("put profile back failed");
            }
        } else {
            if (tmp.renameTo(profile)) {
                SpiceProfilingUtil.log("put profile back success");
            } else {
                SpiceProfilingUtil.log("put profile back failed");
            }
        }
    }
}
