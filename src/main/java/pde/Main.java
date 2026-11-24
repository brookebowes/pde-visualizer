package pde;

public class Main {
    public static void main(String[] args) {
        double k = 1.0;
        double L = 1.0;
        int nx = 11;
        double dx = L / (nx - 1);          // 0.1
        double dt = 0.4 * dx * dx / k;     // gives r = 0.4

        HeatSolver solver = new HeatSolver(k, L, nx, dt);
        solver.setInitialCondition(x -> Math.sin(Math.PI * x / L));

        System.out.println("r = " + solver.getR());
        System.out.println("t = 0.0000");
        printArray(solver.getU());

        for (int n = 1; n <= 100; n++) {
            solver.step();
            if (n % 25 == 0) {
                System.out.printf("t = %.4f%n", solver.getTime());
                printArray(solver.getU());
            }
        }
    }

    private static void printArray(double[] a) {
        StringBuilder sb = new StringBuilder();
        for (double v : a) {
            sb.append(String.format("%8.4f", v));
        }
        System.out.println(sb);
    }
}
