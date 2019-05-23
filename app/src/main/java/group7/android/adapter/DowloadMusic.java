package group7.android.adapter;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


import group7.android.mediaplayerg7.MainActivity;
import group7.android.mediaplayerg7.StreamingAudioActivity;

public class DowloadMusic extends AsyncTask<String,Integer,Integer> {
    private ProgressBar pgbDowload = StreamingAudioActivity.pgbDowload;
    private int max = 0;
    public static String musicName;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pgbDowload.setProgress(0);
        pgbDowload.setMax(0);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (pgbDowload.getProgress() == max) ;
        {
            StreamingAudioActivity.NotDowload = false;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (pgbDowload.getMax() == 0) {
            pgbDowload.setMax(max);
        }
        pgbDowload.setProgress(pgbDowload.getProgress() + values[0]);
        MainActivity.pgbpercent=pgbDowload.getProgress();

    }

    @Override
    protected Integer doInBackground(String... strings) {
        String LinkUrl = strings[0];
        String[] str = LinkUrl.split("[/]");
        musicName=str[str.length - 1];
        // duong dan luu
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+musicName);


        try {
            URL url = new URL(LinkUrl);
            URLConnection connec = url.openConnection();
            connec.connect();
            max = connec.getContentLength();

            InputStream inputStream = connec.getInputStream();

            OutputStream outputStream = new FileOutputStream(file);
            byte data[]=new byte[1024];
            int total=0;
            while ((total = inputStream.read(data)) != -1) {
                outputStream.write(data, 0,total);
                publishProgress(total);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}

