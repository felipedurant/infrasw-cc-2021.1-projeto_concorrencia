import org.jetbrains.annotations.NotNull;
import ui.PlayerWindow;

import java.awt.event.MouseMotionListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// thread para cuidar do reloginho da musica
public class Scrubber extends Thread {

    public PlayerWindow wind;
    public Player play;
    long lastTime =-1;
    long currTime = 0;
    public Scrubber(PlayerWindow playerWindow, Player pl){
        wind = playerWindow;
        play = pl;
    }
    public int t;
    ReentrantLock meuLock = new ReentrantLock();
    @Override
    public void run() {

        t=0;

        wind.updateMiniplayer(true, play.playing, false,t, play.currentSongTime, play.currentSong, play.songCount);

        try{
            while (t < play.currentSongTime){
                Thread.sleep(1000);

                meuLock.lock();
                try{
                    if(play.playing){
                        t += 1;
                    }
                    wind.updateMiniplayer(true, play.playing, false,t, play.currentSongTime, play.currentSong, play.songCount);

                } finally {
                    meuLock.unlock();
                }
            }

        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
