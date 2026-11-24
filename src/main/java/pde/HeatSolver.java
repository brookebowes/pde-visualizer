package pde;

import java.util.function.DoubleUnaryOperator;

public class HeatSolver {
    private final double k;    // diffusion constant
    private final double L;    // rod length
    private final int nx;      // number of grid points
    private final double dx;   // spacing between points
    private final double dt;   // time step
    private final double r;    // k * dt / dx^2

    private double[] u;        // current temperatures
    private double time;       // current time

    public HeatSolver(double k, double L, int nx, double dt) {
        this(k, L, nx, dt, true);
    }

    public HeatSolver(double k, double L, int nx, double dt, boolean enforceStability) {
        this.k = k;
        this.L = L;
        this.nx = nx;
        this.dt = dt;
        this.dx = L / (nx - 1);
        this.r = k * dt / (dx * dx);
        this.u = new double[nx];
        this.time = 0.0;

        if (enforceStability && r > 0.5) {
            throw new IllegalArgumentException(
                    "Unstable: r = k*dt/dx^2 = " + r + " must be <= 0.5");
        }
    }

    /** Fill the grid using a function f(x). Endpoints are forced to 0. */
    public void setInitialCondition(DoubleUnaryOperator f) {
        for (int i = 0; i < nx; i++) {
            u[i] = f.applyAsDouble(i * dx);
        }
        u[0] = 0.0;
        u[nx - 1] = 0.0;
        time = 0.0;
    }

    /** Advance the solution by one time step dt. */
    public void step() {
        double[] next = new double[nx];
        for (int i = 1; i < nx - 1; i++) {
            next[i] = u[i] + r * (u[i + 1] - 2 * u[i] + u[i - 1]);
        }
        u = next;
        time += dt;
    }

    public double[] getU() { return u.clone(); }
    public double getTime() { return time; }
    public double getK() { return k; }
    public double getL() { return L; }
    public int getNx() { return nx; }
    public double getDx() { return dx; }
    public double getDt() { return dt; }
    public double getR() { return r; }
}