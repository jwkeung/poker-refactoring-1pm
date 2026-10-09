package lab.poker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
class BonusPolicyTest {
    @ParameterizedTest
    @CsvSource({
        "2H 3H 4H 5H 6H, true",
        "7C 7D 7H 9S 9C, true",
        "2C 3D 4H 5S 6C, false",
        "2H 5H 8H JH KH, false",
        "2C 5D 8H JS KC, false"
    })
    void bonusExamples(String cards, boolean expected) {
        assertEquals(expected, new BonusPolicy().qualifies(Hands.of(cards)));
    }
}
