package pde;

public class Experiments {

    public static void main(String[] args) {
        convergenceStudy();
        System.out.println();
        stabilityDemo();
    }

    /** Halve dx repeatedly (r stays 0.4) and watch the error fall. */
    private static void convergenceStudy() {
        final double k = 1.0;
        final double L = 1.0;
        final double tFinal = 0.1;

        System.out.println("CONVERGENCE STUDY (r = 0.4, t = 0.1, initial condition sin(pi x / L))");
        System.out.printf("%6s %10s %14s %8s %8s%n", "nx", "dx", "max error", "ratio", "order");

        double previousError = Double.NaN;
        for (int nx : new int[] {11, 21, 41, 81, 161}) {
            double dx = L / (nx - 1);
            double dt = 0.4 * dx * dx / k;
            int steps = (int) Math.round(tFinal / dt);

            HeatSolver solver = new HeatSolver(k, L, nx, dt);
            solver.setInitialCondition(x -> Math.sin(Math.PI * x / L));
            for (int n = 0; n < steps; n++) {
                solver.step();
            }

            double t = solver.getTime();
            double decay = Math.exp(-k * Math.PI * Math.PI * t / (L * L));
            double[] u = solver.getU();
            double maxError = 0.0;
            for (int i = 0; i < nx; i++) {
                double exact = decay * Math.sin(Math.PI * i * dx / L);
                maxError = Math.max(maxError, Math.abs(u[i] - exact));
            }

            double ratio = previousError / maxError;
            double order = Math.log(ratio) / Math.log(2);
            System.out.printf("%6d %10.5f %14.6e %8.3f %8.3f%n", nx, dx, maxError, ratio, order);
            previousError = maxError;
        }
    }

    /** Run r = 0.4, 0.5, 0.6 on a square pulse and print max|u| every 10 steps. */
    private static void stabilityDemo() {
        final double k = 1.0;
        final double L = 1.0;
        final int nx = 51;
        final double dx = L / (nx - 1);

        System.out.println("STABILITY DEMO (square pulse, nx = 51, max|u| every 10 steps, 100 steps)");
        for (double r : new double[] {0.4, 0.5, 0.6}) {
            double dt = r * dx * dx / k;
            HeatSolver solver = new HeatSolver(k, L, nx, dt, false);
            solver.setInitialCondition(x -> (x > 0.4 * L && x < 0.6 * L) ? 1.0 : 0.0);

            StringBuilder line = new StringBuilder(String.format("r = %.1f: ", r));
            for (int n = 0; n <= 100; n++) {
                if (n % 10 == 0) {
                    line.append(String.format("%10.2e", maxAbs(solver.getU())));
                }
                solver.step();
            }
            System.out.println(line);
        }
    }

    private static double maxAbs(double[] a) {
        double m = 0.0;
        for (double v : a) {
            m = Math.max(m, Math.abs(v));
        }
        return m;
    }
}
