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
    String[][] queueArray;
    Boolean playing = false;

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
            playing = !playing;
            window.updatePlayPauseButton(playing);
            window.enableScrubberArea();

        };


        window = new PlayerWindow(btnPlayNow, btnRemove, btnAddSong,
                null, null, null, null,
                null, null, null, null,
                "Nome janela", null);

        window.start();
    }
}

