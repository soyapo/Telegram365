package _365.telegram;

public class SharedCSH {
    private static ClientSocketHandler CSH = new ClientSocketHandler();

    public static ClientSocketHandler getClientSocketHandler() {
        return CSH;
    }
    public static void setClientSocketHandler(ClientSocketHandler handler) {
        CSH = handler;
    }
}

