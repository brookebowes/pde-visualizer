package pde;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FourierSolutionTest {

    private static final double L = 1.0;

    @Test
    void sineInitialConditionHasOnlyModeOne() {
        FourierSolution s = new FourierSolution(FourierSolution.Type.HEAT, 1.0, L, 10,
                x -> Math.sin(Math.PI * x / L));
        assertEquals(1.0, s.getCoefficient(1), 1e-6);
        for (int n = 2; n <= 10; n++) {
            assertEquals(0.0, s.getCoefficient(n), 1e-6);
        }
    }

    @Test
    void parabolaCoefficientsMatchClosedForm() {
        FourierSolution s = new FourierSolution(FourierSolution.Type.HEAT, 1.0, L, 10,
                x -> x * (L - x));
        for (int n = 1; n <= 10; n++) {
            double expected = 4 * L * L * (1 - Math.cos(n * Math.PI)) / Math.pow(n * Math.PI, 3);
            assertEquals(expected, s.getCoefficient(n), 1e-6);
        }
    }

    @Test
    void seriesAtTimeZeroRecoversInitialCondition() {
        FourierSolution s = new FourierSolution(FourierSolution.Type.HEAT, 1.0, L, 200,
                x -> x * (L - x));
        for (double x : new double[] {0.1, 0.3, 0.5, 0.77}) {
            assertEquals(x * (L - x), s.evaluate(x, 0.0), 1e-4);
        }
    }

    @Test
    void heatModeOneDecaysAtExpectedRate() {
        FourierSolution s = new FourierSolution(FourierSolution.Type.HEAT, 1.0, L, 10,
                x -> Math.sin(Math.PI * x / L));
        assertEquals(Math.exp(-Math.PI * Math.PI * 0.1), s.evaluate(0.5, 0.1), 1e-6);
    }

    @Test
    void waveModeOneOscillatesAtExpectedRate() {
        FourierSolution s = new FourierSolution(FourierSolution.Type.WAVE, 2.0, L, 10,
                x -> Math.sin(Math.PI * x / L));
        assertEquals(Math.cos(2.0 * Math.PI * 0.1), s.evaluate(0.5, 0.1), 1e-6);
    }
}