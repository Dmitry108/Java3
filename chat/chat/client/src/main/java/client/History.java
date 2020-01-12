package client;

import java.io.*;

public class History {
    private File logFile;
    private Writer out;
    private Reader in;

    public History(String login){
        System.out.println("login is " + login);
        logFile = new File("history\\history_" + login + ".txt");
        try {
            out = new BufferedWriter(new FileWriter(logFile, true));
            in = new BufferedReader(new FileReader(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //простой вариант получения всех записей
    public StringBuffer read(){
        int x;
        StringBuffer log = new StringBuffer();
        try {
            while ((x = in.read()) != -1) {
                log.append((char)x);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return log;
    }
    //вариант получения произвольного количества записей, считыванием всего файла и последующей с ним работы
    public StringBuffer read(int n) {
        int x;
        StringBuffer log = new StringBuffer();
        try {
            while ((x = in.read()) != -1) {
                log.append((char) x);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = log.length();
        int k = 0;
        while (k <= n && i != -1) {
            i = log.lastIndexOf("\n", i - 1);
            k++;
        }
        if (i != -1) log = log.delete(0, i);
        return log;
    }
    /*
    вариант получения произвольного количества последних записей при помощи RandomAccessFile
    работает медленнее
    public StringBuffer readread(int n){
        StringBuffer sb=null;
        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
            //цикл проходится по файлу с конца, пока не прочитает 100 строчек
            //k - счетчик встретившихся символов новой строки
            byte c[] = new byte[(int)logFile.length()];
            for(int i=(int)raf.length()-2, k=0; i>=0; i--){
                raf.seek(i);
                if ((c[i]=(byte)raf.read())=='\n') if (++k==n) break;
            }
            sb = new StringBuffer(new String(c, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }
    */
    public void write(String s) {
        try {
            out.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}