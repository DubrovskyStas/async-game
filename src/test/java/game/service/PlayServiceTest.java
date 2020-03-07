package game.service;

import game.domain.Player;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.Assert.*;

public class PlayServiceTest {

    @Test
    public void canStartNewGame_should_be_true() {
        //given
        Player first = stubPlayer();
        Player second = stubPlayer();

        //when
        PlayService playService = new PlayService(first, second);

        //then
        assertTrue(playService.canStartNewGame());
    }

    @Test
    public void canStartNewGame_should_be_false() {
        //given
        Player first = stubPlayer();
        Player second = stubPlayer();

        //when
        PlayService playService = new PlayService(first, second);
        first.setFree(false);

        //then
        assertFalse(playService.canStartNewGame());
    }

    @Test
    public void sendMessage() {
        //given
        Player first = stubPlayer();
        Player second = stubPlayer();
        PlayService playService = new PlayService(first, second);

        //when
        playService.sendMessage("play");

        //then
        String expected = "play";
        String actual = first.getOutgoingQueue().poll();

        assertEquals(1, first.getSentAtomicCounter().get());
        assertEquals(expected, actual);
        assertTrue(first.getOutgoingQueue().isEmpty());
    }

    @Test
    public void startGame() throws InterruptedException {
        //given
        Queue<String> directChannel = new ArrayBlockingQueue<>(10);
        Queue<String> backwardChannel = new ArrayBlockingQueue<>(10);

        Player first = new Player(backwardChannel, directChannel, "First");
        Player second = new Player(directChannel, backwardChannel, "Second");
        PlayService playService = new PlayService(first, second);

        //when
        playService.startGame();
        playService.sendMessage("test");

        Thread.sleep(500);

        //then
        List<String> expectedFirst = Arrays.asList(
                "test1",
                "test112",
                "test11223",
                "test1122334",
                "test112233445",
                "test11223344556",
                "test1122334455667",
                "test112233445566778",
                "test11223344556677889",
                "test11223344556677889910"
        );

        List<String> expectedSecond = Arrays.asList(
                "test",
                "test11",
                "test1122",
                "test112233",
                "test11223344",
                "test1122334455",
                "test112233445566",
                "test11223344556677",
                "test1122334455667788",
                "test112233445566778899"
        );

        assertTrue("Service become extremely slow", playService.canStartNewGame());

        assertEquals(expectedFirst, first.getReceivedMessages());
        assertTrue(first.getOutgoingQueue().isEmpty());
        assertTrue(first.getIncomingQueue().isEmpty());
        assertEquals(0, first.getReceivedAtomicCounter().get());
        assertEquals(0, first.getSentAtomicCounter().get());

        assertEquals(expectedSecond, second.getReceivedMessages());
        assertTrue(second.getOutgoingQueue().isEmpty());
        assertTrue(second.getIncomingQueue().isEmpty());
        assertEquals(0, second.getReceivedAtomicCounter().get());
        assertEquals(0, second.getSentAtomicCounter().get());
    }

    private Player stubPlayer() {
        return new Player(new ArrayDeque<>(), new ArrayDeque<>(), "");
    }
}
