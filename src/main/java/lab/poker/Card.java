package lab.poker;

/** A card has a rank from 2 to 14 (Ace) and a suit. */
public record Card(int rank, Suit suit) {
    private static final int MIN_RANK = 2;
    private static final int MAX_RANK = 14;

    public Card {
        if (rank < MIN_RANK || rank > MAX_RANK) {
            throw new IllegalArgumentException("rank must be between 2 and 14 (Ace): " + rank);
        }
    }

    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
}
