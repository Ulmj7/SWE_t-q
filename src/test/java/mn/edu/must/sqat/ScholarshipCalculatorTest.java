package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ScholarshipCalculatorTest {

    private final ScholarshipCalculator calc = new ScholarshipCalculator();

    // ---- Part A: happy path ----

    @Test
    void letterGrade_95_isA() {
        assertEquals("A", calc.letterGrade(95));
    }

    @Test
    void letterGrade_85_isB() {
        assertEquals("B", calc.letterGrade(85));
    }

    @Test
    void gpa_95and85_is3_5() {
        assertEquals(3.5, calc.gpa(new double[] {95, 85}), 1e-9);
    }

    @Test
    void scholarship_highGpaEnoughCredits_is100() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
    }

    @Test
    void scholarship_gpa3_1_is50() {
        assertEquals(50, calc.scholarship(3.1, 10, false));
    }

    // ---- Part B: reach 100% branch / line coverage ----

    // letterGrade(): remaining grade branches and the range check
    @Test
    void letterGrade_75_isC() {
        assertEquals("C", calc.letterGrade(75));
    }

    @Test
    void letterGrade_65_isD() {
        assertEquals("D", calc.letterGrade(65));
    }

    @Test
    void letterGrade_30_isF() {
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_negative_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-0.01));
    }

    @Test
    void letterGrade_above100_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(100.01));
    }

    // gpa(): invalid input and every case of the switch (A, B, C, D, default=F)
    @Test
    void gpa_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
    }

    @Test
    void gpa_empty_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[0]));
    }

    @Test
    void gpa_allGradeKinds_is2_0() {
        // A=4, B=3, C=2, D=1, F=0 -> 10 / 5
        assertEquals(2.0, calc.gpa(new double[] {95, 85, 75, 65, 30}), 1e-9);
    }

    // scholarship(): each condition of the input check, then each decision
    @Test
    void scholarship_negativeGpa_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.1, 16, false));
    }

    @Test
    void scholarship_gpaAbove4_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 16, false));
    }

    @Test
    void scholarship_negativeCredits_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.9, -1, false));
    }

    @Test
    void scholarship_violation_is0() {
        assertEquals(0, calc.scholarship(3.9, 16, true));
    }

    @Test
    void scholarship_highGpaLowCredits_is70() {
        // gpa >= 3.8 but credits < 15 -> falls through to the 70% rule
        assertEquals(70, calc.scholarship(3.9, 14, false));
    }

    @Test
    void scholarship_gpa3_5Credits12_is70() {
        assertEquals(70, calc.scholarship(3.5, 12, false));
    }

    @Test
    void scholarship_gpa3_5Credits10_is50() {
        // gpa >= 3.4 but credits < 12 -> falls through to the 50% rule
        assertEquals(50, calc.scholarship(3.5, 10, false));
    }

    @Test
    void scholarship_lowGpa_is0() {
        assertEquals(0, calc.scholarship(2.5, 20, false));
    }

    // ---- Part C: loop boundary tests for the loop in gpa() (0 / 1 / 2+ iterations) ----
    // 0 iterations: gpa_empty_throws and gpa_null_throws (Part B) - the loop is never entered.

    @Test
    void gpa_oneScore_is4_0() {
        assertEquals(4.0, calc.gpa(new double[] {95}), 1e-9);
    }

    @Test
    void gpa_manyScores_is2_5() {
        // A=4, B=3, C=2, D=1 -> 10 / 4
        assertEquals(2.5, calc.gpa(new double[] {95, 85, 75, 65}), 1e-9);
    }
}
