package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * All-DU-pairs coverage for DataFlowTargets.gcd (line numbers = lines of DataFlowTargets.java).
 *
 * Pair notation: var@defLine->useLine, c = c-use, p(T)/p(F) = p-use, true/false edge.
 * The full 13-row table (with def-clear paths) is in README.md, section A.
 */
class GcdTest {

    @Test
    @DisplayName("G1 gcd(7,0)=7: loop body skipped. Covers x@13->20c, y@13->15p(F)")
    void g1_loopSkipped_returnsFirstArgument() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("G2 gcd(12,8)=4: loop runs 2 times. Covers x@13->16c, x@17->16c, x@17->20c, "
            + "y@13->15p(T), y@13->16c, y@13->17c, y@18->15p(T), y@18->15p(F), y@18->16c, "
            + "y@18->17c, tmp@16->18c")
    void g2_twoIterations_returnsGcd() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }
}
