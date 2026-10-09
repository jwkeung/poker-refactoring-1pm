package lab.poker;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static lab.poker.HandType.*;
import static org.junit.jupiter.api.Assertions.*;

class PokerHandEvaluatorTest {
    private final PokerHandEvaluator evaluator = new PokerHandEvaluator();
    @ParameterizedTest
    @CsvSource({
        "2H 3H 4H 5H 6H, STRAIGHT_FLUSH",
        "7C 7D 7H 7S 9C, FOUR_OF_A_KIND",
        "7C 7D 7H 9S 9C, FULL_HOUSE",
        "2H 5H 8H JH KH, FLUSH",
        "6C 2D 5H 3S 4C, STRAIGHT",
        "7C 7D 7H 9S KC, THREE_OF_A_KIND",
        "7C 7D 9H 9S KC, TWO_PAIR",
        "7C 7D 9H JS KC, ONE_PAIR",
        "2C 5D 8H JS KC, HIGH_CARD"
    })
    void categoryExamples(String cards, HandType expected) {
        assertEquals(expected, evaluator.classify(Hands.of(cards)));
    }
    @Test void aceLowStraight() {
        assertEquals(STRAIGHT, evaluator.classify(Hands.of("AH 2C 3D 4S 5H")));
    }
    @Test void aceLowStraightFlush() {
        assertEquals(STRAIGHT_FLUSH, evaluator.classify(Hands.of("AH 2H 3H 4H 5H")));
    }
    @Test void aceHighStraight() {
        assertEquals(STRAIGHT, evaluator.classify(Hands.of("AC JD QH KS 10C")));
    }
    @Test void wrappedRanksAreNotStraight() {
        assertFalse(evaluator.isStraight(Hands.of("QC KD AH 2S 3C")));
    }
    @Test void repeatedRanksAreNotStraight() {
        assertFalse(evaluator.isStraight(Hands.of("2C 3D 4H 5S 5C")));
    }
    @Test void doesNotReorderCallerList() {
        var hand = new ArrayList<>(Hands.of("6C 2D 5H 3S 4C"));
        var before = new ArrayList<>(hand);
        assertEquals(STRAIGHT, evaluator.classify(hand));
        assertEquals(before, hand);
    }
    @Test void readOnlyListAccepted() {
        var hand = List.copyOf(Hands.of("6C 2D 5H 3S 4C"));
        assertEquals(STRAIGHT, evaluator.classify(hand));
    }
    @Test void publicFullHouseHelperStillWorks() {
        assertTrue(evaluator.isFullHouse(Hands.of("7C 7D 7H 9S 9C")));
        assertFalse(evaluator.isFullHouse(Hands.of("7C 7D 7H 9S KC")));
    }
}
