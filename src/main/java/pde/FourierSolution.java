package pde;

import java.util.function.DoubleUnaryOperator;

/** Exact series solution on [0, L] with u = 0 at both ends. */
public class FourierSolution {

    public enum Type { HEAT, WAVE }

    private static final int INTEGRATION_INTERVALS = 4000;

    private final Type type;
    private final double L;
    private final int maxTerms;
    private final double[] b;   // b[n] for n = 1..maxTerms (b[0] unused)
    private double parameter;   // k for HEAT, c for WAVE

    public FourierSolution(Type type, double parameter, double L,
                           int maxTerms, DoubleUnaryOperator f) {
        this.type = type;
        this.parameter = parameter;
        this.L = L;
        this.maxTerms = maxTerms;
        this.b = new double[maxTerms + 1];

        double h = L / INTEGRATION_INTERVALS;
        double[] fx = new double[INTEGRATION_INTERVALS + 1];
        for (int j = 0; j <= INTEGRATION_INTERVALS; j++) {
            fx[j] = f.applyAsDouble(j * h);
        }
        for (int n = 1; n <= maxTerms; n++) {
            double sum = 0.5 * (fx[0] * phi(n, 0.0) + fx[INTEGRATION_INTERVALS] * phi(n, L));
            for (int j = 1; j < INTEGRATION_INTERVALS; j++) {
                sum += fx[j] * phi(n, j * h);
            }
            b[n] = (2.0 / L) * h * sum;
        }
    }

    public void setParameter(double parameter) { this.parameter = parameter; }

    public double lambda(int n) {
        double a = n * Math.PI / L;
        return a * a;
    }

    public double phi(int n, double x) { return Math.sin(n * Math.PI * x / L); }

    public double getCoefficient(int n) { return b[n]; }

    public int getMaxTerms() { return maxTerms; }

    private double timeFactor(int n, double t) {
        if (type == Type.HEAT) {
            return Math.exp(-parameter * lambda(n) * t);
        }
        return Math.cos(parameter * Math.sqrt(lambda(n)) * t);
    }

    /** Sum of the first `terms` terms of the series at position x and time t. */
    public double evaluate(double x, double t, int terms) {
        int count = Math.min(terms, maxTerms);
        double sum = 0.0;
        for (int n = 1; n <= count; n++) {
            sum += b[n] * timeFactor(n, t) * phi(n, x);
        }
        return sum;
    }

    public double evaluate(double x, double t) { return evaluate(x, t, maxTerms); }
}