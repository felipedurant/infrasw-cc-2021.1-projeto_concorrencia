import ui.AddSongWindow;
import ui.PlayerWindow;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Player {

    AddSongWindow addSongWindow;
    PlayerWindow window;

    // contador que serve para o songID
    int songID=0;

    // contador de quantas musicas estão na lista
    int songCount = 0;
    int[] songIDs;
    // array das musicas
    public String[][] queueArray;
    Boolean playing = false;
    Scrubber scrubber;
    public int currentSong = -1;
    public int currentSongTime = -1;

    public Player() {

        // botao que adiciona as musicas
        ActionListener btnAddSongOK = e ->{
            // cria um array temp com uma posicao a mais e copia o antigo
            String[][] queueTemp = new String[songCount+1][];
            int[] songIDsTemp = new int[songCount + 1];

            for (int i = 0; i < songCount;i++){
                queueTemp[i] = queueArray[i];
                songIDsTemp[i] = songIDs[i];
            }
            // adiciona a nova musica no array
            queueTemp[songCount] = addSongWindow.getSong();
            songIDsTemp[songCount] = songID;
            // guarda no queueArray a lista atualizada
            queueArray = queueTemp;
            songIDs = songIDsTemp;
            // atualiza a janela com a nova musica
            window.updateQueueList(queueTemp);

            songID++;
            songCount++;
        };

        ActionListener btnRemove = e -> {
            // cria um array temp com uma posicao a mais e copia o antigo
            int remove = window.getSelectedSongID();
            String[][] queueTemp = new String[songCount - 1][];
            int[] songIDsTemp = new int[songCount - 1];


            //Ajuda na varredura da lista de músicas
            Boolean skip = false;

            //Verifica se a música removida está sendo, também, reproduzida
            if(currentSong > -1 && songIDs[currentSong] == remove){
                if(scrubber.isAlive())
                    scrubber.interrupt();
                window.resetMiniPlayer();
            }

            //Varredura da lista de músicas para achar a música que será removida
            for (int i = 0; i < queueTemp.length;i++){
                if(songIDs[i] == remove){
                    skip = true;
                }
                if(!skip){
                    queueTemp[i] = queueArray[i];
                    songIDsTemp[i] = songIDs[i];
                }
                else{
                    if(currentSong == i+1){
                        System.out.println(currentSong);
                        currentSong -=1;
                        System.out.println(currentSong);
                    }
                    queueTemp[i] = queueArray[i+1];
                    songIDsTemp[i] = songIDs[i+1];
                }
            }
            window.updateQueueList(queueTemp);
            queueArray=queueTemp;
            songIDs=songIDsTemp;
            songCount--;
        };

        //Comando de adicionar música
        ActionListener btnAddSong = e ->{
            addSongWindow = new AddSongWindow(Integer.toString(songID), btnAddSongOK, window.getAddSongWindowListener());
            addSongWindow.start();
        };

        //Comando de Play Now
        ActionListener btnPlayNow = e -> {
            playing = true;
            window.updatePlayPauseButton(playing);
            window.enableScrubberArea();

            int selected = -1;
            for (int i = 0; i < songIDs.length;i++){
                if(songIDs[i] == window.getSelectedSongID()){
                    selected = i;
                    break;
                }
            }

            //Inicia a reprodução da música e a thread
            if(selected > -1){
                currentSong = selected;
                currentSongTime = Integer.parseInt(queueArray[selected][5]);
                window.updatePlayingSongInfo(
                        queueArray[selected][0], queueArray[selected][1], queueArray[selected][2]);

                if(scrubber!=null && scrubber.isAlive()){
                    scrubber.interrupt();
                }
                scrubber = new Scrubber(window,this);
                scrubber.start();
            }


        };

        //Alterna entre play e pause
        ActionListener btnPlayPause = e -> {
            playing = !playing;
            window.updatePlayPauseButton(playing);
        };


        window = new PlayerWindow(btnPlayNow, btnRemove, btnAddSong,
                btnPlayPause, null, null, null,
                null, null, null, null,
                "Tocador de musicas", null);

        window.start();
    }
}


