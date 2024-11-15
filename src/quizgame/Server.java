package quizgame;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static final int PORT = 1235; // 포트를 클라이언트와 맞춤
    private static ArrayList<Question> questions;

    public static void main(String[] args) {
        loadQuestions();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question("Java의 창시자는?", "James Gosling"));
        questions.add(new Question("TCP/IP 모델의 레이어 수는?", "4"));
        questions.add(new Question("HTTP는 무슨 프로토콜인가?", "Hypertext Transfer Protocol"));
    }

    static ArrayList<Question> getQuestions() {
        return questions;
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private int score = 0; // 점수 초기화

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                for (Question question : Server.getQuestions()) {
                    out.println("QUESTION: " + question.getQuestionText());

                    String clientAnswer = in.readLine();
                    if (clientAnswer == null) break;

                    if (clientAnswer.equalsIgnoreCase(question.getAnswer())) {
                        out.println("FEEDBACK: Correct");
                        score++; // 정답일 경우 점수 증가
                    } else {
                        out.println("FEEDBACK: Incorrect");
                    }
                }

                // 최종 점수 전송
                out.println("FINAL_SCORE: " + score);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
