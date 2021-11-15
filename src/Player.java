import ui.AddSongWindow;
import ui.PlayerWindow;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Player {

    AddSongWindow addSongWindow;
    PlayerWindow window;

    public Player() {

        ActionListener btnAddSongOK = e ->{

        };

        ActionListener btnAddSong = e ->{
            addSongWindow = new AddSongWindow(null, btnAddSongOK, window.getAddSongWindowListener());
        };

        window = new PlayerWindow(null, null, btnAddSong,
                null, null, null, null,
                null, null, null, null,
                "Nome janela", null);

        window.start();
    }
}

