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

            Boolean skip = false;

            for (int i = 0; i < queueTemp.length;i++){
                if(songIDs[i] == remove){
                    skip = true;
                }
                if(!skip){
                    queueTemp[i] = queueArray[i];
                    songIDsTemp[i] = songIDs[i];
                }
                else{
                    queueTemp[i] = queueArray[i+1];
                    songIDsTemp[i] = songIDs[i+1];
                }
            }
            window.updateQueueList(queueTemp);
            queueArray=queueTemp;
            songIDs=songIDsTemp;
            songCount--;
        };

        ActionListener btnAddSong = e ->{
            addSongWindow = new AddSongWindow(Integer.toString(songID), btnAddSongOK, window.getAddSongWindowListener());
            addSongWindow.start();
        };

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

            if(selected > -1){
                currentSong = selected;
                currentSongTime = Integer.parseInt(queueArray[selected][5]);
                window.updatePlayingSongInfo(
                        queueArray[selected][0], queueArray[selected][1], queueArray[selected][2]);

                scrubber = new Scrubber(window,this);
                scrubber.run();
            }


        };

        ActionListener btnPlayPause = e -> {
            playing = !playing;
            window.updatePlayPauseButton(playing);
        };


        window = new PlayerWindow(btnPlayNow, btnRemove, btnAddSong,
                btnPlayPause, null, null, null,
                null, null, null, null,
                "Nome janela", null);

        window.start();
    }
}


