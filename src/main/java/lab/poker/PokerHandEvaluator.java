package lab.poker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static lab.poker.HandType.*;

/** Evaluate five distinct cards and rank them by poker hand type. */
public class PokerHandEvaluator {
    private static final int FOUR = 4;
    private static final int THREE = 3;
    private static final int TWO = 2;
    private static final int FIVE = 5;
    private static final int ACE = 14;

    public HandType classify(List<Card> hand) {
        Map<Integer, Integer> counts = rankCounts(validate(hand));
        boolean straight = isStraight(hand);
        boolean flush = isFlush(hand);
        if (straight && flush) return STRAIGHT_FLUSH;
        if (counts.containsValue(FOUR)) return FOUR_OF_A_KIND;
        if (isFullHouse(counts)) return FULL_HOUSE;
        if (flush) return FLUSH;
        if (straight) return STRAIGHT;
        if (counts.containsValue(THREE)) return THREE_OF_A_KIND;
        long pairCount = counts.values().stream().filter(n -> n == TWO).count();
        if (pairCount == TWO) return TWO_PAIR;
        if (pairCount == 1) return ONE_PAIR;
        return HIGH_CARD;
    }

    public boolean isStraight(List<Card> hand) {
        int[] ranks = validate(hand).stream().mapToInt(Card::rank).sorted().toArray();
        if (isAceLowStraight(ranks)) return true;
        return isConsecutive(ranks);
    }

    public boolean isFlush(List<Card> hand) {
        List<Card> cards = validate(hand);
        Card.Suit suit = cards.get(0).suit();
        for (int i = 1; i < cards.size(); i++) {
            if (cards.get(i).suit() != suit) return false;
        }
        return true;
    }

    public boolean isFullHouse(List<Card> hand) {
        return isFullHouse(rankCounts(validate(hand)));
    }

    private boolean isFullHouse(Map<Integer, Integer> counts) {
        return counts.containsValue(THREE) && counts.containsValue(TWO);
    }

    private boolean isAceLowStraight(int[] ranks) {
        return ranks[0] == TWO && ranks[1] == THREE && ranks[2] == FOUR
                && ranks[3] == FIVE && ranks[4] == ACE;
    }

    private boolean isConsecutive(int[] ranks) {
        for (int i = 1; i < ranks.length; i++) {
            if (ranks[i] != ranks[i - 1] + 1) return false;
        }
        return true;
    }

    private List<Card> validate(List<Card> hand) {
        if (hand == null) {
            throw new IllegalArgumentException("hand must not be null");
        }
        if (hand.size() != 5) {
            throw new IllegalArgumentException("hand must contain exactly 5 cards, got " + hand.size());
        }
        if (new HashSet<>(hand).size() != hand.size()) {
            throw new IllegalArgumentException("hand must contain 5 distinct cards");
        }
        return hand;
    }

    private Map<Integer, Integer> rankCounts(List<Card> hand) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Card card : hand) {
            counts.merge(card.rank(), 1, Integer::sum);
        }
        return counts;
    }
}
