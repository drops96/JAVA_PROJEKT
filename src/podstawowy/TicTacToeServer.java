package podstawowy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

/**
 Klasa odpowiedzialna za mechanizm serwera
 */
public class TicTacToeServer {
    /**
     Zmienna przechowujaca ile czasu pracuje serwer
     */
    private static int minuty = 0;
    /**
     Zmienna przechowujaca ile czasu pracuje serwer
     */
    private static int godziny = 0;
    /**
     Zmienna przechowujaca ile czasu pracuje serwer
     */
    private static int sekundy = 0;






    /**
     Tworzy GUI, timery do odswiezania czasu dzialania i ilosci graczy
     */
    public static void main(String args[]) throws Exception {
        JFrame okno = new JFrame("KOLKO I KRZYZYK - SERWER");
        JLabel czas = new JLabel();

        JLabel data = new JLabel();
        data.setText("CZAS STARTU SERWERA: " + Calendar.getInstance().getTime().toString().substring(0, 16));
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

        new Timer(1000, e -> {
            String ile = String.valueOf(mxBean.getUptime());
            sekundy = (Integer.parseInt(ile) / 1000 % 60);

            if (sekundy == 59) {
                minuty += 1;
                sekundy = 0;
            }
            if (minuty == 59) {
                godziny++;
                minuty = 0;
            }
            czas.setText("CZAS PRACY: " + godziny + "h " + minuty + "m " + sekundy + "s");
        }).start();

        JButton przycisk1 = new JButton("ZAKONCZ");
        ActionListener al = e -> {

            okno.dispose();
            System.exit(0);
        };

        przycisk1.addActionListener(al);
        przycisk1.setVisible(true);

        JLabel informacja = new JLabel("  SERWER DZIALA ", SwingConstants.CENTER);

        panel.add(informacja);
        panel.add(czas);
        panel.add(data);
        panel.add(przycisk1);

        okno.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        okno.setSize(400, 150);
        okno.add(panel);
        okno.setVisible(true);
        okno.setResizable(false);
        /**
        Tworzy nowy socket a następnie grę i ustawia graczy
         */
        try (ServerSocket listener = new ServerSocket(8890)) {
            while (true) {

                Game game = new Game();

                SwingUtilities.updateComponentTreeUI(okno);
                Game.Gracz playerX = game.new Gracz(listener.accept(), 'X');
                Game.Gracz playerO = game.new Gracz(listener.accept(), 'O');
                playerX.ustawPrzeciwnika(playerO);
                playerO.ustawPrzeciwnika(playerX);
                game.obecnyGracz = playerX;
                playerX.start();
                playerO.start();



            }
        }
    }
}

