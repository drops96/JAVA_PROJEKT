package podstawowy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;


/**
 Klasa odpowiedzialna za mechanizm rozgrywki
 */
public class Game {
    /**
     Przechowuje czas rozpoczecia rozgrywki
     */
   private Instant start;

    /**
     Przechowuje nazwe gracza
     */
    private String nick1,nick2;
    /**
     Przechowuje tablice z przebiegiem rogrywki wykorzystywana w zapytaniu SQL
     */
   private String[] tablica = new String[9];
    /**
     Opisuje plansze gry, czyli tablice 3x3, zapelnia ja zerami na poczatek rozgrywki
     */
    private Gracz[] plansza = {
            null, null, null,
            null, null, null,
            null, null, null};
    /**
     Przechowuje zapytanie SQL do bazy
     */
    public String sql;
    /**
     Obiekt przechowujacy obecnego gracza
     */
    Gracz obecnyGracz;





    /**
     Sprawdza na postawie pol tablicy czy sa 3 identyczne znaki w linii prostej.
     */
    private boolean isWinner() {
        return
                (plansza[0] != null && plansza[0] == plansza[1] && plansza[0] == plansza[2])
                        || (plansza[3] != null && plansza[3] == plansza[4] && plansza[3] == plansza[5])
                        || (plansza[6] != null && plansza[6] == plansza[7] && plansza[6] == plansza[8])
                        || (plansza[0] != null && plansza[0] == plansza[3] && plansza[0] == plansza[6])
                        || (plansza[1] != null && plansza[1] == plansza[4] && plansza[1] == plansza[7])
                        || (plansza[2] != null && plansza[2] == plansza[5] && plansza[2] == plansza[8])
                        || (plansza[0] != null && plansza[0] == plansza[4] && plansza[0] == plansza[8])
                        || (plansza[2] != null && plansza[2] == plansza[4] && plansza[2] == plansza[6]);
    }

    /**
     Sprawdza czy plansza jest pelna.
     */
    private boolean planszaPelna() {
        for (Gracz aplansza : plansza) {
            if (aplansza == null) {
                return false;
            }
        }
        return true;
    }

    /**
     Zasob o dostepie synchronizowanym, sluzy do synchronizacji ruchu graczy, okresla kto w chwili obecnej moze sie ruszyc
     */
    private synchronized boolean dobryRuch(int location, Gracz Gracz) {
        if (Gracz == obecnyGracz && plansza[location] == null) {
            plansza[location] = obecnyGracz;
            if(obecnyGracz.ktory == 'X')
            {
                tablica[location]="X";
            }
            else  if(obecnyGracz.ktory == 'O')
            {
                tablica[location]="O";
            }
            obecnyGracz = obecnyGracz.przeciwnik;
            obecnyGracz.ruchPrzeciwnika(location);

            return true;
        }
        return false;
    }
    /**
     Klasa Gracz, ktora tworzy nowy watek z wymaganymi komponentami(metoda run(), bufory itp.)
     */
    class Gracz extends Thread {
        char ktory;
        Gracz przeciwnik;
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        /**
         Tworzy bufor do obslugi wymiany danych miedzy graczami
         */
        Gracz(Socket socket, char ktory) {
            this.socket = socket;
            this.ktory = ktory;
            try {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WITAJ " + ktory);
                output.println("MESSAGE Oczekiwanie na przeciwnika");
            } catch (IOException e) {
                System.out.println("Rozłaczony: " + e);
            }
        }

        /**
         Zmienia przeciwnika danego gracza
         */
        public void ustawPrzeciwnika(Gracz przeciwnik) {
            this.przeciwnik = przeciwnik;
        }

        /**
         Okresla ruch przeciwnika
         */
        void ruchPrzeciwnika(int location) {
            output.println("OPPONENT_MOVED " + location);
            output.println(
                    isWinner() ? "DEFEAT" : planszaPelna() ? "TIE" : "");
        }
        /**
         Odpowiada za obsluge zdarzen, jak ruchy graczy, koniec rozgrywki itp.
         */
        public void run() {

 start= Instant.now();
            try {
                if (ktory == 'X') {
                    output.println("MESSAGE Gracze polaczeni.Twoj ruch");
                }
                if (ktory == 'O') {
                    output.println("MESSAGE Gracze polaczeni.Ruch przeciwnika");
                }
                while (true) {
                    String command = input.readLine();
                    if (command.startsWith("MOVE")) {
                        int location = Integer.parseInt(command.substring(5));
                        if (dobryRuch(location, this)) {
                            output.println("VALID_MOVE");
                            output.println(isWinner() ? "VICTORY"
                                    : planszaPelna() ? "TIE"
                                    : "");
                        } else {
                            output.println("MESSAGE Co Ty robisz?!");
                        }
                    } else if (command.startsWith("KONIEC")) {
                        if (ktory == 'X') {

                            nick1=command.substring(6);

                        }
                        if (ktory == 'O') {

                            nick2=command.substring(6);
                        }
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Rozłączony: " + e);
            }
            finally {
                try {
                    Instant end = Instant.now();
                    Duration czas = Duration.between(start, end);


                    if (!nick1.equals("LOKALNIE"))
                             {
                                 DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
                                 Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "hr", "hr");
                                 Statement stat=connect.createStatement();


                                 if(ktory=='X')    {

                                     sql="   insert into TEST values('"+nick1+"','"+nick2+"','"+czas.toSeconds()+"','"+ Arrays.toString(tablica) +"')";
                                     stat.execute(sql);
                                 }







                                 stat.close();

                    }



                    output.println("KONIEC");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
