package podstawowy;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;


/**
 Klasa odpowiedzialna za dzialanie klienta
 */
public class TicTaToeClient {
    /**
     Glowne okno pragramu
     */
    private JFrame frame = new JFrame("KOLKO KRZYZYK");
    /**
     Przechowuje obecne informacje o rozgrywce
     */
    private JLabel messageLabel = new JLabel("");
    /**
    Przechowuje nazwe gracza
     */
    private static String nick;
    /**
     Przechowuje ikone gracza
     */
    private ImageIcon icon;
    /**
     Przechowuje ikone przeciwnika
     */
    private ImageIcon opponentIcon;
    /**
     Przechowuje plansze rozgrywki
     */
    private Plansza[] board = new Plansza[9];
    /**
    Obecna plansza rozgrywki
     */
    private Plansza currentPlansza;
    /**
     Polaczenie miedzy klientem a serwerem
     */
    private Socket socket;
    /**
     Bufor wejsciowy
     */
    private BufferedReader in;
    /**
     Bufor wyjsciowy
     */
    private PrintWriter out;
    /**
     Tworzy socket i bufory potrzebne do obslugi rozgrywki, tworzy graficzna reprezentacje tablicy rozgrywki
     */
    private TicTaToeClient(String serverAddress) throws Exception {

        socket = new Socket(serverAddress, 8890);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "North");

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));

        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Plansza();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentPlansza = board[j];
                    out.println("MOVE " + j);
                }
            });
            boardPanel.add(board[i]);
        }

        frame.getContentPane().add(boardPanel, "Center");
    }


    /**
     Odpowiada za odebranie komunikatow i przetworzenie ich na konkretne instrukcje oraz zamkniecie socketu
     */
    private void play() throws Exception {
        String odpowiedz;
        try {
            odpowiedz = in.readLine();
            if (odpowiedz.startsWith("WITAJ")) {
                char mark = odpowiedz.charAt(6);
                icon = new ImageIcon(mark == 'X' ? "./res/x.png" : "./res/o.png");
                opponentIcon = new ImageIcon(mark == 'X' ? "./res/o.png" : "./res/x.png");
                frame.setTitle("KOLKO I KRZYZYK - "+nick+" "+mark);
            }
            while (true) {
                odpowiedz = in.readLine();
                if (odpowiedz.startsWith("VALID_MOVE")) {
                    messageLabel.setText("Ruch dobry, czekaj na przeciwnika");
                    currentPlansza.ustawIkone(icon);
                    currentPlansza.repaint();
                } else if (odpowiedz.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(odpowiedz.substring(15));
                    board[loc].ustawIkone(opponentIcon);
                    board[loc].repaint();
                    messageLabel.setText("Twoj ruch");
                } else if (odpowiedz.startsWith("VICTORY")) {
                    messageLabel.setText("Wygrana");
                    break;
                } else if (odpowiedz.startsWith("DEFEAT")) {
                    messageLabel.setText("Porazka");
                    break;
                } else if (odpowiedz.startsWith("TIE")) {
                    messageLabel.setText("Remis");
                    break;
                } else if (odpowiedz.startsWith("MESSAGE")) {
                    messageLabel.setText(odpowiedz.substring(8));
                }
            }
            out.println("KONIEC"+nick);

        } finally {
            socket.close();
        }
    }
    /**
     Sprawdza czy gracz chce powtorzyc rozgrywke
     */
    private boolean chcePonowic() {
        int odpowiedz = JOptionPane.showConfirmDialog(frame, "Wybierz czy zagrac ponownie", "Koniec gry!", JOptionPane.YES_NO_OPTION);
        frame.dispose();

        return odpowiedz == JOptionPane.YES_OPTION;
    }
    /**
     Odpowiada za wyglad ekranu rozgrywki
     */
    static class Plansza extends JPanel {
        /**
         Odpowiada za wyswietlenie ikony gracza
         */
        JLabel label = new JLabel((Icon) null);


        /**
         Odpowiada za ustawienie tla ekranu rozgrywki
         */
        Plansza() {
            setBackground(Color.white);
            add(label);
        }
        /**
         Odpowiada za ustawienie ikony gracza
         */
        void ustawIkone(Icon icon) {
            label.setIcon(icon);
        }
    }

    /**
     Tworzy polaczenie z serwerem na podstawie adresu, tworzy klienta i jego GUI
     */
    public static void main(String[] args)   {




        while (true) {
            String serverAddress;
            if (args.length == 0) {
                serverAddress = "localhost";
            } else {
                serverAddress = args[0];
            }
            nick=args[1];
            JFrame ramka = new JFrame();
            JOptionPane.showMessageDialog(ramka, "Twoj nick to: "+nick);

            TicTaToeClient client = null;
            try {
                client = new TicTaToeClient(serverAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
            client.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            client.frame.setSize(650, 650);
            client.frame.setVisible(true);
            client.frame.setResizable(false);
            try {
                client.play();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (!client.chcePonowic()) {

                break;
            }
        }
    }
}
