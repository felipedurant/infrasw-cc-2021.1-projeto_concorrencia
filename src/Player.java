import org.jetbrains.annotations.NotNull;
import ui.AddSongWindow;
import ui.PlayerWindow;

import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class Player {

    public ReentrantLock lock = new ReentrantLock();

    AddSongWindow addSongWindow;
    PlayerWindow window;

    // contador que serve para o songID
    int songID=0;
    boolean repeat = false;
    boolean shuffle = false;
    boolean mudarMusicaEmRepeat = false;
    int contador = 0;

    // contador de quantas musicas estão na lista
    int songCount = 0;
    int[] songIDs;
    // array das musicas
    public String[][] queueArray;
    boolean playing = false;
    Scrubber scrubber;
    public int currentSong = -1;
    public int selected = -1;
    public int currentSongTime = -1;
    Random random = new Random();
    public int[] queueRandom = new int[300];

    public Player() {

        //Comportamento normal de um player


        // botao que adiciona as musicas
        ActionListener btnAddSongOK = e ->{
            AddSongOK addSongOK = new AddSongOK(this);
            addSongOK.start();
        };

        ActionListener btnRemove = e -> {
            RemoveSong removeSong = new RemoveSong(this);
            removeSong.start();
        };

        //Comando de adicionar música
        ActionListener btnAddSong = e ->{
            addSongWindow = new AddSongWindow(Integer.toString(songID), btnAddSongOK, window.getAddSongWindowListener());
            addSongWindow.start();
        };

        //Comando de Play Now
        ActionListener btnPlayNow = e -> {
            PlayNow playnow = new PlayNow(this);
            playnow.start();
        };

        //Alterna entre play e pause
        ActionListener btnPlayPause = e -> {
            PauseSong pauseSong = new PauseSong(this);
            pauseSong.start();
        };

        ActionListener btnNext = e -> {
            mudarMusicaEmRepeat = true;
            NextSong nextSong = new NextSong(this);
            nextSong.start();
        };

        ActionListener btnPrevious = e -> {
            PrevSong prevSong = new PrevSong(this);
            prevSong.start();

        };

        ActionListener btnStop = e -> {
            StopSong stopSong = new StopSong(this);
            stopSong.start();
        };

        //Consegue mudar o tempo do relógio quando arrasta
        MouseMotionListener scrubberMotion = new MouseMotionListener() {
            //Relativo a arrastar o slider
            @Override
            public void mouseDragged(MouseEvent e) {
                scrubber.meuLock.lock();
                try {
                    window.updateMiniplayer(true,playing,repeat,window.getScrubberValue(),currentSongTime,currentSong,songCount);
                    scrubber.t = window.getScrubberValue();
                } finally {
                    scrubber.meuLock.unlock();
                }

            }
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
        //Modifica tempo do relógio ao clicar na barra e quando soltamos depois de arrastar
        MouseListener mouseClick = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scrubber.meuLock.lock();
                try {
                    playing = true;
                    window.updatePlayPauseButton(playing);
                    window.updateMiniplayer(true,playing,repeat,window.getScrubberValue(),currentSongTime,currentSong,songCount);
                    scrubber.t = window.getScrubberValue();
                } finally {
                    scrubber.meuLock.unlock();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                scrubber.meuLock.lock();
                try {
                    playing = false;
                    window.updatePlayPauseButton(playing);
                } finally {
                    scrubber.meuLock.unlock();
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                scrubber.meuLock.lock();
                try {
                    playing = true;
                    window.updatePlayPauseButton(playing);
                } finally {
                    scrubber.meuLock.unlock();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        ActionListener btnRepeat = e -> {

            repeat = !repeat;
            window.updateMiniplayer(true,playing,!repeat,window.getScrubberValue(),currentSongTime,currentSong,songCount);

        };

        ActionListener btnShuffle = e -> {

            shuffle = !shuffle;

            window.updateMiniplayer(true,playing,repeat,window.getScrubberValue(),currentSongTime,currentSong,songCount);

        };

        window = new PlayerWindow(btnPlayNow, btnRemove, btnAddSong,btnPlayPause, btnStop, btnNext, btnPrevious,btnShuffle,btnRepeat , mouseClick ,scrubberMotion,"Tocador de musicas", null);

        window.start();
    }
}

// Threads das ações dos botões

class PrevSong extends Thread{
    Player player;
    public  PrevSong(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        player.lock.lock();
        try{
            //Adicionar comando para seguir a lista aleatória quando voltar

            player.selected -= 1;

            player.currentSong = player.selected;
            //encerrando música que foi passada
            if(player.scrubber!=null && player.scrubber.isAlive()){
                player.scrubber.interrupt();
            }
            //iniciar próxima música
            player.currentSongTime = Integer.parseInt(player.queueArray[player.selected][5]);
            player.window.updatePlayingSongInfo(
                    player.queueArray[player.selected][0], player.queueArray[player.selected][1], player.queueArray[player.selected][2]);

            player.scrubber = new Scrubber(player.window,player);
            player.scrubber.start();
            player.playing = true;

        }
        finally {
            player.lock.unlock();
        }
    }
}

class PauseSong extends Thread{
    Player player;
    public PauseSong(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        try{
            player.playing = !player.playing;
            player.window.updatePlayPauseButton(player.playing);
        }
        finally {
            player.lock.unlock();
        }
    }
}
class StopSong extends Thread{
    Player player;
    public StopSong(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        player.lock.lock();
        try{
            if(player.scrubber!=null && player.scrubber.isAlive()){
                player.scrubber.interrupt();
            }
            player.window.resetMiniPlayer();

        }
        finally {
            player.lock.unlock();
        }

    }
}

class NextSong extends Thread{
    Player player;
    public NextSong(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        player.lock.lock();
        try{
            if (player.songCount > player.selected + 1){
                if (!player.repeat || player.mudarMusicaEmRepeat) {
                    player.selected += 1;
                }
                if (player.shuffle) {
                    player.selected = player.random.nextInt(player.songCount);
                    //tentativa de uma implementação melhor de shuffle
//                    do {
//                        player.selected = player.random.nextInt(player.songCount);
//                    } while (Arrays.asList(player.queueRandom).contains(player.selected) == true); {
//                        player.selected = player.random.nextInt(player.songCount);
//                    }
//                    player.queueRandom[player.contador] = player.selected;
//                    player.contador ++;
                }

                    //execução normal sem shuffle
                    player.currentSong = player.selected;
                    //encerrando música que foi passada
                    if(player.scrubber!=null && player.scrubber.isAlive()){
                        player.scrubber.interrupt();
                    }
                    //iniciar próxima música

                    //Atualizar o tempo da nova música
                    player.currentSongTime = Integer.parseInt(player.queueArray[player.selected][5]);
                    //Atualizar informações da nova música
                    player.window.updatePlayingSongInfo(
                            player.queueArray[player.selected][0], player.queueArray[player.selected][1], player.queueArray[player.selected][2]);

                    player.scrubber = new Scrubber(player.window,player);
                    player.scrubber.start();
                    player.playing = true;
                    player.mudarMusicaEmRepeat = false;
                }



        }
        finally {
            player.lock.unlock();
        }
    }
}

class AddSongOK extends Thread{
    Player player;
    public AddSongOK(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        player.lock.lock();
        try{
            // cria um array temp com uma posicao a mais e copia o antigo
            String[][] queueTemp = new String[player.songCount+1][];
            int[] songIDsTemp = new int[player.songCount + 1];

            for (int i = 0; i < player.songCount;i++){
                queueTemp[i] = player.queueArray[i];
                songIDsTemp[i] = player.songIDs[i];
            }
            // adiciona a nova musica no array
            queueTemp[player.songCount] = player.addSongWindow.getSong();
            songIDsTemp[player.songCount] = player.songID;
            // guarda no queueArray a lista atualizada
            player.queueArray = queueTemp;
            player.songIDs = songIDsTemp;
            // atualiza a janela com a nova musica
            player.window.updateQueueList(queueTemp);

            player.songID++;
            player.songCount++;

        }finally {
            player.lock.unlock();
        }
    }
}


class RemoveSong extends Thread{

    Player player;
    public RemoveSong(Player player){
        this.player=player;
    }

    @Override
    public void run() {
        player.lock.lock();

        try{
            int remove = player.window.getSelectedSongID();
            String[][] queueTemp = new String[player.songCount - 1][];
            int[] songIDsTemp = new int[player.songCount - 1];


            //Ajuda na varredura da lista de músicas
            boolean skip = false;

            //Verifica se a música removida está sendo, também, reproduzida
            if(player.currentSong > -1 && player.songIDs[player.currentSong] == remove){
                if(player.scrubber.isAlive())
                    player.scrubber.interrupt();
                player.window.resetMiniPlayer();
            }

            //Varredura da lista de músicas para achar a música que será removida
            for (int i = 0; i < queueTemp.length;i++){
                if(player.songIDs[i] == remove){
                    skip = true;
                }
                if(!skip){
                    queueTemp[i] = player.queueArray[i];
                    songIDsTemp[i] = player.songIDs[i];
                }
                else{
                    if(player.currentSong == i+1){
                        System.out.println(player.currentSong);
                        player.currentSong -=1;
                        System.out.println(player.currentSong);
                    }
                    queueTemp[i] = player.queueArray[i+1];
                    songIDsTemp[i] = player.songIDs[i+1];
                }
            }
            player.window.updateQueueList(queueTemp);
            player.queueArray=queueTemp;
            player.songIDs=songIDsTemp;
            player.songCount--;

        }
        finally {
            player.lock.unlock();
        }
    }
}

class PlayNow extends Thread {
    Player player;
    public PlayNow (Player player) {
        this.player= player;
    }

    @Override
    public void run(){
        player.lock.lock();

        try {
            player.playing = true;
            player.window.updatePlayPauseButton(player.playing);
            player.window.enableScrubberArea();

            for (int i = 0; i < player.songIDs.length;i++){
                if(player.songIDs[i] == player.window.getSelectedSongID()){
                    player.selected = i;
                    break;
                }
            }

            //Inicia a reprodução da música e a thread
            if(player.selected > -1){
                player.currentSong = player.selected;
                player.currentSongTime = Integer.parseInt(player.queueArray[player.selected][5]);
                player.window.updatePlayingSongInfo(
                        player.queueArray[player.selected][0], player.queueArray[player.selected][1], player.queueArray[player.selected][2]);

                if(player.scrubber!=null && player.scrubber.isAlive()){
                    player.scrubber.interrupt();
                }
                player.scrubber = new Scrubber(player.window,player);
                player.scrubber.start();
            }
        } finally {
            player.lock.unlock();
        }
    }

}

