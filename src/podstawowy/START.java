package podstawowy;

import javax.swing.*;

/**
 Klasa odpowiedzialna za menu glowne
 */
public class START extends JDialog {
    /**
     Panel glowny
     */
    private JPanel contentPane;
    /**
     Przycisk uruchamiajacy serwer
     */
    private JButton buttonSER;
    /**
     Przycisk uruchamiajacy klienta
     */
    private JButton buttonKLI;
    /**
     Przycisk uruchamiajacy gre lokalna
     */
    private JButton ButtonLOKAL;

    /**
     Tworzy GUI wraz z obsluga zdarzen
     */
    private START() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSER);

        buttonSER.addActionListener(e -> onSER());

        buttonKLI.addActionListener(e -> onKLI());

        ButtonLOKAL.addActionListener(e-> onLOK());

    }
    /**
     Odpowiada za wlaczenie serwera, po wcisnieciu odpowiedniego buttona, tworzy nowy watek
     */
    private void onSER() {


        new Thread(() -> {
            try {
                TicTacToeServer.main(new String[]{"1"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
dispose();
    }
    /**
     Odpowiada za wlaczenie klienta, po wcisnieciu odpowiedniego buttona, tworzy nowy watek
     */
    private void onKLI() {


        new Thread(() -> {
            try {
                LOGIN.main(new String[] {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        dispose();
    }

    /**
     Odpowiada za wlaczenie serwera lokalnie i 2x klienta, po wcisnieciu odpowiedniego buttona, tworzy nowe watki
     */
    private void onLOK() {

        new Thread(() -> {
            try {
                TicTacToeServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                TicTaToeClient.main(new String[] {"localhost","LOKALNIE"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            try {
                TicTaToeClient.main(new String[] {"localhost","LOKALNIE"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        dispose();
    }
    /**
     Odpowiada za wyswietlenie GUI
     */
    public static void main(String[] args) {
        START dialog = new START();
        dialog.pack();
        dialog.setVisible(true);
        dialog.dispose();
    }
}
