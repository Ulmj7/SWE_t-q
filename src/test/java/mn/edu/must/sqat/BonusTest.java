package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Line numbers in the DU table refer to DataFlowTargets.java. */
class BonusTest {

    @Test
    @DisplayName("B1 all-defs: exact threshold, broken five-day streak, score@51->54c and score@54->56c")
    void b1_exactThresholdBrokenStreak_returnsEight() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 0, 1, 1}, 7));
    }

    @Test
    @DisplayName("B2 all-defs: all absent below threshold, score@49->56c")
    void b2_allAbsentBelowThreshold_returnsZero() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {0, 0}, 1));
    }
}
