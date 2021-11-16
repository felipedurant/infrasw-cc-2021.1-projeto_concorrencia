import ui.PlayerWindow;


// thread para cuidar do reloginho da musica
public class Scrubber extends Thread {

    PlayerWindow window;
    Player player;

    long lastTime =-1;
    long currTime = 0;
    public Scrubber(PlayerWindow playerWindow, Player pl){
        window = playerWindow;
        player = pl;
    }

    @Override
    public void run() {

        int t=0;
        if(lastTime>-1){
            long time = System.nanoTime();
            currTime += (lastTime-time);
            lastTime = time;
        }

        t = (int)(currTime * 1000000);

        window.updateMiniplayer(true, player.playing, false,t, player.currentSongTime, player.currentSong, player.songCount);

    }
}
