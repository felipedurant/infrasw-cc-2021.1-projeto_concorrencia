import ui.PlayerWindow;

import java.awt.event.MouseMotionListener;


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

    @Override
    public void run() {

        int t=0;

        wind.updateMiniplayer(true, play.playing, false,t, play.currentSongTime, play.currentSong, play.songCount);

        try{
            while (t < play.currentSongTime){
                Thread.sleep(1000);
                if(play.playing){
                    t += 1;
                }
                wind.updateMiniplayer(true, play.playing, false,t, play.currentSongTime, play.currentSong, play.songCount);
            }

        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
