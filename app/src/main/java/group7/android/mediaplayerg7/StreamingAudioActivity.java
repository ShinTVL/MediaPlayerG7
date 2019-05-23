package group7.android.mediaplayerg7;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import group7.android.adapter.DowloadMusic;
import group7.android.model.Music;

import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PAUSE;
import static group7.android.mediaplayerg7.MusicPlayer.PLAYER_PLAY;

public class StreamingAudioActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.OnCompletionListener
{

    public EditText edtUrl;
    private  String musicName="";
    private  Boolean EndMusic=false;

    private  Button btnPlay,btnDowload;
    public static ProgressBar pgbDowload;
    public static String LinkURL = "";

    public static Boolean Start = true, NotDowload = true;
    private int timeCurrent, UPDATE_TIME = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_audio);
        AddControls();
        AddEvents();
        TiepTucBaiHat();
    }

    private  void TiepTucBaiHat()
    {
        if (MainActivity.musicPlayer.getState()==PLAYER_PLAY || MainActivity.musicPlayer.getState()==PLAYER_PAUSE)
        {
            if(MainActivity.TEN_BAI_HAT!=null) {
                MainActivity.tvArtist.setText(MainActivity.TEN_CA_SI);
            }
            if(MainActivity.TEN_BAI_HAT!=null) {
                MainActivity.tvTitle.setText(MainActivity.TEN_BAI_HAT);
            }
            MainActivity.tvTimeTotal.setText(MainActivity.TOTAL_TIME);
            MainActivity.isRunning = true;
            if (MainActivity.musicPlayer.getState()==PLAYER_PLAY)
                MainActivity.ivPlay.setImageResource(R.drawable.pause);
            else
                MainActivity.ivPlay.setImageResource(R.drawable.play);
        }
        if(MainActivity.PlayingOnline==true)
            btnPlay.setEnabled(false);
        if(MainActivity.DowloadOnline==true)
            btnDowload.setEnabled(false);
        //if(MainActivity.pgbpercent!=0)
            //pgbDowload.setProgress(MainActivity.pgbpercent);
    }

    public void AddControls() {

        edtUrl = (EditText) findViewById(R.id.edturl);
        edtUrl.setText("https://data.chiasenhac.com/dataxx/00/downloads/1851/4/1850232-c2033074/128/Ghe%20Qua%20-%20Dick_%20Tofu_%20PC.mp3");
        LinkURL = edtUrl.getText().toString();

        btnPlay=(Button)findViewById(R.id.btnplay);
        btnDowload=(Button) findViewById(R.id.btndowload);
        pgbDowload=(ProgressBar) findViewById((R.id.pgbdowload));

        MainActivity.ivPlay = (ImageView) findViewById(R.id.iv_play);
        MainActivity.ivNext = (ImageView) findViewById(R.id.iv_next);
        MainActivity.ivPrevious = (ImageView) findViewById(R.id.iv_previous);
        MainActivity.sbProcess = (SeekBar) findViewById((R.id.sb_process));
        MainActivity.tvTitle = (TextView) findViewById(R.id.tv_title);
        MainActivity.tvTimeTotal = (TextView) findViewById(R.id.tv_time_total);
        MainActivity.tvArtist = (TextView) findViewById(R.id.tv_artist);
        MainActivity.tvTimeProcess = (TextView) findViewById(R.id.tv_time_process);
        MainActivity.ivRepeat = (ImageView) findViewById(R.id.iv_repeat);
        MainActivity.ivShuffle = (ImageView) findViewById(R.id.iv_shuffle);

    }

    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MainActivity.UPDATE_TIME) {
                MainActivity.timeCurrent = MainActivity.musicPlayer.getTimeCurrent();
                MainActivity.tvTimeProcess.setText(getTimeFormat(MainActivity.timeCurrent));
                Double percentage = (double) 0;
                long currentSeconds = (int) (MainActivity.timeCurrent);
                long totalSeconds = (int) (MainActivity.musicPlayer.getTimeTotal());
                percentage =(((double)currentSeconds)/totalSeconds)*100;
                System.out.println(percentage);
                MainActivity.sbProcess.setProgress(percentage.intValue());
            }
        }
    };



    public  void AddEvents()
    {
        String[] str = LinkURL.split("[/]");
        musicName=str[str.length - 1];

        // click Buuton Play
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //bai hát trong link chưa dc chơi
                if(Start==true) {
                        MainActivity.PlayingOnline=true;
                        ChoiBaiHatOnlineLanDau();
                }

            }
        });


        //click ImageView ivPlay
        MainActivity.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Start==false) {
                    if(EndMusic==true&&MainActivity.musicPlayer.getState() != PLAYER_PLAY)
                    {
                        ChoiLaiBaiHatOnline();
                    }
                    //nếu đang chơi nhạc online
                    if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
                        MainActivity.ivPlay.setImageResource(R.drawable.play);
                        MainActivity.musicPlayer.pause();
                    }
                    // dang tạm dừng nhạc đã chơi online
                    else {
                        MainActivity.ivPlay.setImageResource(R.drawable.pause);
                        MainActivity.musicPlayer.play();
                    }
                }
                else {
                    //nếu đang chơi nhạc
                    if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
                        MainActivity.ivPlay.setImageResource(R.drawable.play);
                        MainActivity.musicPlayer.pause();
                    }
                    // dang tạm dừng nhạc đã chơi
                    else {
                        MainActivity.ivPlay.setImageResource(R.drawable.pause);
                        MainActivity.musicPlayer.play();
                    }

                }
            }
        });

        //click Button Dowload
        btnDowload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.DowloadOnline=true;
                String[] str = LinkURL.split("[/]");
                String musicName=str[str.length - 1];

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/"+musicName);
             //  if(!file.exists()) {
                     Toast.makeText(StreamingAudioActivity.this, file.getPath(), Toast.LENGTH_SHORT).show();
                     new DowloadMusic().execute(LinkURL);
            }
        });
    }


    private  void ChoiBaiHatOnlineLanDau()
    {
        // dừng bài đang chơi lại nếu có
        if (MainActivity.musicPlayer.getState() == PLAYER_PLAY) {
            MainActivity.musicPlayer.stop();
        }
        MainActivity.tvArtist.setText("");
        MainActivity.tvTitle.setText(musicName);
        MainActivity.TEN_BAI_HAT=musicName;
        MainActivity.TEN_CA_SI="";
        MainActivity.musicPlayer.setup(LinkURL);
        MainActivity.sbProcess.setProgress(0);
        MainActivity.musicPlayer.play();
        MainActivity.ivPlay.setImageResource(R.drawable.pause);
        Start = false;
        MainActivity.isRunning = true;
        btnPlay.setEnabled(false);
        MainActivity.totaltime = MainActivity.musicPlayer.getTimeTotal();
        MainActivity.tvTimeTotal.setText(getTimeFormat(MainActivity.musicPlayer.getTimeTotal()));
        MainActivity.TOTAL_TIME = MainActivity.tvTimeTotal.getText().toString();
        // process time // set up seekbar
        MainActivity.sbProcess.setMax(MainActivity.musicPlayer.getTimeTotal());
        //cập nhật seekbar
        new Thread(new Runnable() {
            @Override
            public void run() {
                //khi còn chơi nhạc
                while (MainActivity.isRunning) {
                    Message message = new Message();
                    message.what =  MainActivity.UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    private  void ChoiLaiBaiHatOnline()
    {
        MainActivity.musicPlayer.setup(LinkURL);
        MainActivity.sbProcess.setProgress(0);
        MainActivity.musicPlayer.play();
        MainActivity.ivPlay.setImageResource(R.drawable.pause);
        Start = false;
        MainActivity.isRunning = true;
        btnPlay.setEnabled(false);
        MainActivity.totaltime = MainActivity.musicPlayer.getTimeTotal();
        MainActivity.tvTimeTotal.setText(getTimeFormat(MainActivity.musicPlayer.getTimeTotal()));
        MainActivity.TOTAL_TIME = MainActivity.tvTimeTotal.getText().toString();
        // process time // set up seekbar
        MainActivity.sbProcess.setMax(MainActivity.musicPlayer.getTimeTotal());
        //cập nhật seekbar
        new Thread(new Runnable() {
            @Override
            public void run() {
                //khi còn chơi nhạc
                while (MainActivity.isRunning) {
                    Message message = new Message();
                    message.what =  MainActivity.UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }
    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (MainActivity.musicPlayer.getTimeCurrent());
        long totalSeconds = (int) (MainActivity.musicPlayer.getTimeTotal());
        percentage =(((double)currentSeconds)/totalSeconds)*100;
        System.out.print("percentage="+percentage+" progress="+progress);
        if (percentage.intValue() != progress && MainActivity.timeCurrent != 0) {
            int currentDuration = 0;
            int totalDuration = MainActivity.musicPlayer.getTimeTotal();
            currentDuration = (int) ((((double)progress) / 100) * totalDuration);
            MainActivity.musicPlayer.seek(currentDuration * 1000);
        }
        if (MainActivity.sbProcess.getProgress()==MainActivity.sbProcess.getMax())
        {
            EndMusic=true;
            MainActivity.musicPlayer.stop();
            MainActivity.sbProcess.setProgress(0);
            MainActivity.ivPlay.setImageResource(R.drawable.play);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void OnEndMusic() {

    }



}
