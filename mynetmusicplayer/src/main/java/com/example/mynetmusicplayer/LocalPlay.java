package com.example.mynetmusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mynetmusicplayer.service.PlayerService;
import com.example.mynetmusicplayer.utils.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LocalPlay extends AppCompatActivity {

    private static final int PLAY_MSG = 0;
    MediaPlayer mMediaPlayer;
    SimpleAdapter mAdapter;
    ListView mMusicList;
    List<Song> songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);
        mMusicList= (ListView) findViewById(R.id.music_list);
        mMusicList.setOnItemClickListener(new MusicListItemClickListener());


        //运行时申请权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            List<Song> songList = getSongList();
            setListAdapter(songList);
        }

    }

    //获取歌曲
    public List<Song> getSongList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        songList = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Song song = new Song();
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));   //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            if (isMusic !=0 ) {
                song.setSongID(id);
                song.setSongTitle(title);
                song.setSongID(id);
                song.setArtist(artist);
                song.setDuration(duration);
                song.setSize(size);
                song.setUrl(url);
                songList.add(song);

            }
        }
        return songList;
    }

    //设置歌曲到listview
    public void setListAdapter(List<Song> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Song mp3Info = (Song) iterator.next();
            HashMap<String, String> map = new HashMap<>();
            map.put("title", mp3Info.getSongTitle());
            map.put("artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(mp3Info.getDuration()));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        mAdapter = new SimpleAdapter(this, mp3list,
                R.layout.music_list_item, new String[]{"title", "duration","size"},
                new int[]{R.id.title, R.id.duration,R.id.size});
        mMusicList.setAdapter(mAdapter);
    }

    private class MusicListItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mMusicList != null){
                Song song = songList.get(position);
                Intent intent = new Intent(LocalPlay.this,PlayerService.class);
                intent.putExtra("url",song.getUrl());
                intent.putExtra("MSG", PLAY_MSG);
                startService(intent);
            }
        }
    }
}
