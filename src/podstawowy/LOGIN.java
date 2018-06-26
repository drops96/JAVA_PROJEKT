package podstawowy;

import javax.swing.*;
import java.awt.event.*;

/**
 Formularz logowania
 */
public class LOGIN extends JDialog {
    /**
    Panel glowny programu
     */
    private JPanel contentPane;
    /**
     Przycisk akceptujacy dane wpisane przez uztkownika
     */
    private JButton buttonOK;
    /**
    Przechowuje adres serwera
     */
    private JFormattedTextField formattedTextField1;
    /**
     Przechowuje login gracza
     */
    private JFormattedTextField formattedTextField2;
    /**
     Zmienna przechwujaca adres serwera otrzymany od uzytkownika
     */
    private  String adresIP;
    /**
     Zmienna przechwujaca nick uzytkownika
     */
    private  String nick;


    /**
     Konstruktor, tworzy panel kontekstowy wraz z obsluga zdarzen
     */
    private LOGIN() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());


        formattedTextField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                adresIP=formattedTextField1.getText();
                super.focusLost(e);
            }
        });

        formattedTextField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                nick=formattedTextField2.getText();
                super.focusLost(e);
            }
        });

    }
    /**
     Po kliknieciu przycisku wysyla adres IP i nazwe gracza
     */
    private void onOK() {

        new Thread(() -> {
            try {
                TicTaToeClient.main(new String[] {adresIP,nick});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
    /**
     Tworzenie nowego obiektu klasy login
     */
    public static void main(String[] args) {
        LOGIN dialog = new LOGIN();
        dialog.pack();
        dialog.setVisible(true);

    }
}
