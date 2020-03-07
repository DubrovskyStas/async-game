package game.service;

import game.domain.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayService {
    private final static String RECEIVED_TEMPLATE = "[%s] Incoming message is '%s' RECEIVED_COUNTER = %d";
    private final static String SENT_TEMPLATE = "[%s] Outgoing message is '%s' SENT_COUNTER = %d";
    private final static String RESTRICTED_TEMPLATE = "[%s] Message '%s' is not sent because the game is over";

    private final static int SLEEP_TIME_MILLIS = 30;
    private final static int STOP_CONDITION = 9;

    private Player first;
    private Player second;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    public PlayService(Player first, Player second) {
        this.first = first;
        this.second = second;
    }

    private void startListening(Player player) {
        executorService.submit(() -> {
            while (!isFinished()) {
                String message = player.getIncomingQueue().poll();
                if (message != null) {
                    receiveMessage(player, message);
                    if (!isFinished()) {
                        sendBack(player, message + player.getReceivedAtomicCounter());
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            cleanAfterFinish(player);
        });
    }

    private void receiveMessage(Player player, String message) {
        System.out.println(
                String.format(
                        RECEIVED_TEMPLATE,
                        player.getName(),
                        message,
                        player.getReceivedAtomicCounter().incrementAndGet()
                )
        );

        player.getReceivedMessages().add(message);
    }

    private void sendBack(Player player, String message) {
        player.getOutgoingQueue().add(message);
        System.out.println(
                String.format(
                        SENT_TEMPLATE,
                        player.getName(),
                        message,
                        player.getSentAtomicCounter().incrementAndGet()
                )
        );
    }

    public void sendMessage(String message) {
        if (!isFinished()) {
            first.getOutgoingQueue().add(message);
            System.out.println(
                    String.format(
                            SENT_TEMPLATE,
                            first.getName(),
                            message,
                            first.getSentAtomicCounter().incrementAndGet()
                    )
            );
        } else {
            System.out.println(String.format(RESTRICTED_TEMPLATE, first.getName(), message));
        }
    }

    private void cleanAfterFinish(Player player) {
        System.out.println(
                String.format(
                        "[%s] Game is finished. Outgoing queue has %d messages. Incoming queue has %d messages.",
                        player.getName(),
                        player.getOutgoingQueue().size(),
                        player.getIncomingQueue().size()
                )
        );

        player.getSentAtomicCounter().set(0);
        player.getReceivedAtomicCounter().set(0);
        player.getIncomingQueue().clear();
        player.getOutgoingQueue().clear();
        player.setFree(true);
    }

    private boolean isFinished() {
        return first.getSentAtomicCounter().get() > STOP_CONDITION
                && first.getReceivedAtomicCounter().get() > STOP_CONDITION;
    }

    public boolean canStartNewGame() {
        return first.isFree() && second.isFree();
    }

    public void startGame() {
        first.setFree(false);
        first.getReceivedMessages().clear();
        startListening(first);

        second.setFree(false);
        second.getReceivedMessages().clear();
        startListening(second);
    }
}
