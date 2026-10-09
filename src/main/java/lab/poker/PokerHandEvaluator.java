package lab.poker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static lab.poker.HandType.*;

/** Evaluate five distinct cards and rank them by poker hand type. */
public class PokerHandEvaluator {
    private static final int FOUR = 4;
    private static final int THREE = 3;
    private static final int TWO = 2;

    public HandType classify(List<Card> hand) {
        boolean straight = isStraight(hand);
        boolean flush = isFlush(hand);
        Map<Integer, Integer> counts = rankCounts(hand);
        if (straight && flush) return STRAIGHT_FLUSH;
        if (counts.containsValue(FOUR)) return FOUR_OF_A_KIND;
        if (isFullHouse(hand)) return FULL_HOUSE;
        if (flush) return FLUSH;
        if (straight) return STRAIGHT;
        if (counts.containsValue(THREE)) return THREE_OF_A_KIND;
        long pairCount = counts.values().stream().filter(n -> n == TWO).count();
        if (pairCount == TWO) return TWO_PAIR;
        if (pairCount == 1) return ONE_PAIR;
        return HIGH_CARD;
    }

    public boolean isStraight(List<Card> hand) {
        int[] ranks = hand.stream().mapToInt(Card::rank).sorted().toArray();
        if (isAceLowStraight(ranks)) return true;
        return isConsecutive(ranks);
    }

    public boolean isFlush(List<Card> hand) {
        Card.Suit suit = hand.get(0).suit();
        for (int i = 1; i < hand.size(); i++) {
            if (hand.get(i).suit() != suit) return false;
        }
        return true;
    }

    public boolean isFullHouse(List<Card> hand) {
        Map<Integer, Integer> counts = rankCounts(hand);
        return counts.containsValue(THREE) && counts.containsValue(TWO);
    }

    private boolean isAceLowStraight(int[] ranks) {
        return ranks[0] == TWO && ranks[1] == THREE && ranks[2] == FOUR
                && ranks[3] == 5 && ranks[4] == 14;
    }

    private boolean isConsecutive(int[] ranks) {
        for (int i = 1; i < ranks.length; i++) {
            if (ranks[i] != ranks[i - 1] + 1) return false;
        }
        return true;
    }

    private Map<Integer, Integer> rankCounts(List<Card> hand) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Card card : hand) {
            counts.merge(card.rank(), 1, Integer::sum);
        }
        return counts;
    }
}