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

        wind.updateMiniplayer(true, play.playing, play.repeat,t, play.currentSongTime, play.currentSong, play.songCount);

        try{
            //tempo da musica correndo
            while (t < play.currentSongTime || !play.playing){
                Thread.sleep(1000);

                meuLock.lock();
                try{
                    if(play.playing){
                        t += 1;
                    }
                    wind.updateMiniplayer(true, play.playing, play.repeat,t, play.currentSongTime, play.currentSong, play.songCount);

                } finally {

                    meuLock.unlock();
                }
            }
            Thread.sleep(1000);

            // inicia a thread que vai checar o que fazer agora que a música terminou
            SongFinished songFinished = new SongFinished(play);
            songFinished.start();

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
