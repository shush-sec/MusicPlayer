package com.example.mynetmusicplayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by shush on 2017/5/25.
 */

public class MyLog    //静态类
{
    public static void getLog() {
        try {
            ArrayList<String> cmdLine = new ArrayList<>();   //设置命令   logcat -d 读取日志
            cmdLine.add("logcat");
            cmdLine.add("-d");

            ArrayList<String> clearLog = new ArrayList<>();  //设置命令  logcat -c 清除日志
            clearLog.add("logcat");
            clearLog.add("-c");


            Process process = Runtime.getRuntime().exec(new String[]{"logcat","-s","MediaPlayer:W"});   //捕获日志
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));    //将捕获内容转换为BufferedReader


//               Runtime.runFinalizersOnExit(true);
            String str = null;
            while ((str = bufferedReader.readLine()) != null)    //开始读取日志，每次读取一行
            {
                System.out.println("logcat"+str);    //输出，在logcat中查看效果，也可以是其他操作，比如发送给服务器..
                //Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));  //清理日志....这里至关重要，不清理的话，任何操作都将产生新的日志，代码进入死循环，直到bufferreader满
            }
            if (str == null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}