package quizgame;

import java.io.*;
import java.net.*;

public class Client {
    private static String serverAddress = "localhost";
    private static int port = 1235;

    public static void main(String[] args) {
        try {
            // 서버 연결
            Socket socket = new Socket(serverAddress, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("QUESTION")) {
                    System.out.println(serverMessage);
                    String answer = userInput.readLine();
                    out.println(answer);
                } else if (serverMessage.startsWith("FEEDBACK") || serverMessage.startsWith("FINAL_SCORE")) {
                    System.out.println(serverMessage);
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}