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

        window.updateMiniplayer(true, player.playing, false,t, player.currentSongTime, player.currentSong, player.songCount);

        try{
            while (t < player.currentSongTime){
                Thread.sleep(1000);
                if(player.playing){
                    t += 1;
                }
                window.updateMiniplayer(true, player.playing, false,t, player.currentSongTime, player.currentSong, player.songCount);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
