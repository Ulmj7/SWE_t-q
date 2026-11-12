package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("B3 empty array, zero threshold: present@35->50p(T), score@51->56c")
    void b3_emptyThresholdZero_returnsFive() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {}, 0));
    }

    @Test
    @DisplayName("B4 six-day streak below threshold: present@40->50p(F), score@49->54c")
    void b4_sixDayStreakBelowThreshold_returnsThree() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 1}, 7));
    }

    @Test
    @DisplayName("B5 null array: attendance@31->32ap(T), exception before threshold check")
    void b5_nullAttendance_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 0));
    }

    @Test
    @DisplayName("B6 negative threshold: threshold@31->32bp(T), exception with non-null array")
    void b6_negativeThreshold_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[] {}, -1));
    }

    @Test
    @DisplayName("B7 short broken streak at threshold: best@43->53p(F), streak@46->41c")
    void b7_shortStreakAtThreshold_returnsFive() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {0, 1, 1, 0, 1}, 3));
    }

    @Test
    @DisplayName("B8 {1,1,0,1,1,1}, threshold 0 -> 5; kills M2 (present vs streak)")
    void b8_absenceBeforeLongerRun_returnsFive() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 0, 1, 1, 1}, 0));
    }
}
