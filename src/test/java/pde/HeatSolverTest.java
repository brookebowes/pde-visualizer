package pde;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeatSolverTest {

    private static final double K = 1.0;
    private static final double L = 1.0;
    private static final DoubleUnaryOperator SINE = x -> Math.sin(Math.PI * x / L);

    /** Largest |numerical - exact| at time tFinal, using r = 0.4. */
    private static double maxError(int nx, double tFinal, DoubleUnaryOperator f, int terms) {
        double dx = L / (nx - 1);
        double dt = 0.4 * dx * dx / K;
        int steps = (int) Math.round(tFinal / dt);

        HeatSolver solver = new HeatSolver(K, L, nx, dt);
        solver.setInitialCondition(f);
        for (int n = 0; n < steps; n++) {
            solver.step();
        }

        FourierSolution exact = new FourierSolution(FourierSolution.Type.HEAT, K, L, terms, f);
        double[] u = solver.getU();
        double max = 0.0;
        for (int i = 0; i < nx; i++) {
            double diff = Math.abs(u[i] - exact.evaluate(i * dx, solver.getTime()));
            max = Math.max(max, diff);
        }
        return max;
    }

    @Test
    void sineMatchesExactSolution() {
        assertTrue(maxError(21, 0.1, SINE, 10) < 0.002);
    }

    @Test
    void errorShrinksByFactorOfFourWhenDxIsHalved() {
        double e1 = maxError(11, 0.1, SINE, 10);
        double e2 = maxError(21, 0.1, SINE, 10);
        double e3 = maxError(41, 0.1, SINE, 10);
        assertEquals(4.0, e1 / e2, 0.5);
        assertEquals(4.0, e2 / e3, 0.5);
    }

    @Test
    void parabolaMatchesFourierSeries() {
        assertTrue(maxError(41, 0.1, x -> x * (L - x), 50) < 1e-3);
    }

    @Test
    void unstableTimeStepIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new HeatSolver(1.0, 1.0, 11, 0.01));
    }

    @Test
    void endpointsStayZero() {
        HeatSolver solver = new HeatSolver(1.0, 1.0, 11, 0.004);
        solver.setInitialCondition(SINE);
        for (int n = 0; n < 10; n++) {
            solver.step();
        }
        double[] u = solver.getU();
        assertEquals(0.0, u[0]);
        assertEquals(0.0, u[u.length - 1]);
    }
}