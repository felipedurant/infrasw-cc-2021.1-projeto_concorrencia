import ui.PlayerWindow;

public class Main {
    public static void main(String[] args) {
        Thread t = new PlayerWindow(null, null, null,
                null, null, null, null,
                null, null, null, null,
                "Nome janela", null);

        t.start();
    }
}
