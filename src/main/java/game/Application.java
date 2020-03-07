package game;

import game.domain.Player;
import game.service.PlayService;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class Application {

    private static final Scanner INPUT = new Scanner(System.in);

    private static final String EXIT_COMMAND = "exit";

    public static void main(String[] args) throws InterruptedException {
        Queue<String> directChannel = new ArrayBlockingQueue<>(10);
        Queue<String> backwardChannel = new ArrayBlockingQueue<>(10);

        Player first = new Player(backwardChannel, directChannel, "First");
        Player second = new Player(directChannel, backwardChannel, "Second");
        PlayService playService = new PlayService(first, second);

        while (true) {
            System.out.println("--------------------------------------------------------------");
            System.out.println("The game is started. Send the message (Type 'exit' to exit it)");
            System.out.println("--------------------------------------------------------------");
            String message = INPUT.nextLine();
            if (message.equalsIgnoreCase(EXIT_COMMAND)) {
                System.exit(0);
            }
            playService.startGame();
            playService.sendMessage(message);

            while (!playService.canStartNewGame()) {
                Thread.sleep(100);
            }
        }
    }
}
