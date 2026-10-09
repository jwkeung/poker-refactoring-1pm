package lab.poker;

import org.junit.jupiter.api.Test;
import static lab.poker.Card.Suit.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test void minRankAccepted() {
        assertEquals(2, new Card(2, CLUBS).rank());
    }
    @Test void maxRankAccepted() {
        assertEquals(14, new Card(14, SPADES).rank());
    }
    @Test void rankBelowMinimumRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Card(1, CLUBS));
        assertThrows(IllegalArgumentException.class, () -> new Card(0, CLUBS));
    }
    @Test void rankAboveMaximumRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Card(15, CLUBS));
    }
}