package pulse.problem.schemes.solvers;

import static java.lang.Math.pow;

import pulse.problem.schemes.DifferenceScheme;
import pulse.problem.schemes.ExplicitScheme;
import pulse.problem.statements.LinearisedProblem;
import pulse.problem.statements.Problem;
import pulse.properties.NumericProperty;

/**
 * Performs a fully-dimensionless calculation for the {@code LinearisedProblem}.
 * <p>
 * Relies on using the heat equation to
 * calculate the value of the grid-function at the next timestep. Fills the
 * {@code grid} completely at each specified spatial point. The heating curve is
 * updated with the rear-side temperature
 * <math><i>&Theta;(x<sub>N</sub>,t<sub>i</sub></i></math>) (here
 * <math><i>N</i></math> is the grid density) at the end of {@code timeLimit}
 * intervals, which comprise of {@code timeLimit/tau} time steps. The
 * {@code HeatingCurve} is scaled (re-normalised) by a factor of
 * {@code maxTemp/maxVal}, where {@code maxVal} is the absolute maximum of the
 * calculated solution (with respect to time), and {@code maxTemp} is the
 * {@code maximumTemperature} {@code NumericProperty} of {@code problem}.
 * </p>
 * <p>
 * The explicit scheme uses a standard 4-point template on a one-dimensional
 * grid that utilises the following grid-function values on each step:
 * <math><i>&Theta;(x<sub>i</sub>,t<sub>m</sub>),
 * &Theta;(x<sub>i</sub>,t<sub>m+1</sub>),
 * &Theta;(x<sub>i-1</sub>,t<sub>m</sub>),
 * &Theta;(x<sub>i+1</sub>,t<sub>m</sub>)</i></math>. Hence, the calculation of
 * the grid-function at the timestep <math><i>m</i>+1</math> can be done
 * <i>explicitly</i>. The derivative in the boundary conditions is approximated
 * using a simple forward difference.
 * </p>
 * <p>
 * The explicit scheme is stable only if <math><i>&tau; &le;
 * h<sup>2</sup></i></math> and has an order of approximation of
 * <math><i>O(&tau; + h)</i></math>. Note that this scheme is only used for
 * validating more complex schemes and does not give accurate results due to the
 * lower order of approximation. When calculations using this scheme are
 * performed, the <code>gridDensity</code> is chosen to be at least 80, which
 * ensures that the error is not too high (typically a {@code 1.5E-2} relative
 * error).
 * </p>
 * 
 * @see super.solve(Problem)
 */

public class ExplicitLinearisedSolver extends ExplicitScheme implements Solver<LinearisedProblem> {

	private double[] U;
	private double[] V;

	private int N;
	private double hx;

	private double a;

	private final static double EPS = 1e-7; // a small value ensuring numeric stability

	public ExplicitLinearisedSolver() {
		super();
	}

	public ExplicitLinearisedSolver(NumericProperty N, NumericProperty timeFactor) {
		super(N, timeFactor);
	}

	public ExplicitLinearisedSolver(NumericProperty N, NumericProperty timeFactor, NumericProperty timeLimit) {
		super(N, timeFactor, timeLimit);
	}

	@Override
	public void prepare(Problem problem) {
		super.prepare(problem);

		N = (int) getGrid().getGridDensity().getValue();
		hx = getGrid().getXStep();

		U = new double[N + 1];
		V = new double[N + 1];

		double Bi1 = (double) problem.getHeatLoss().getValue();

		a = 1. / (1. + Bi1 * hx);
	}

	@Override
	public void solve(LinearisedProblem problem) {

		prepare(problem);
		var curve = problem.getHeatingCurve();
		final double maxTemp = (double) problem.getMaximumTemperature().getValue();
		final double tau = getGrid().getTimeStep();
		
		double maxVal = 0;
		double TAU_HH = tau / pow(hx, 2);

		final var discretePulse = getDiscretePulse();

		/*
		 * The outer cycle iterates over the number of points of the HeatingCurve
		 */

		for (int w = 1, counts = (int) curve.getNumPoints().getValue(); w < counts; w++) {

			/*
			 * Two adjacent points of the heating curves are separated by timeInterval on
			 * the time grid. Thus, to calculate the next point on the heating curve,
			 * timeInterval/tau time steps have to be made first.
			 */

			for (int m = (w - 1) * getTimeInterval() + 1; m < w * getTimeInterval() + 1; m++) {

				/*
				 * Uses the heat equation explicitly to calculate the grid-function everywhere
				 * except the boundaries
				 */

				for (int i = 1; i < N; i++) {
					V[i] = U[i] + TAU_HH * (U[i + 1] - 2. * U[i] + U[i - 1]);
				}

				/*
				 * Calculates boundary values
				 */

				double pls = discretePulse.laserPowerAt((m - EPS) * tau);
				V[0] = (V[1] + hx * pls) * a;
				V[N] = V[N - 1] * a;

				System.arraycopy(V, 0, U, 0, N + 1);

			}

			maxVal = Math.max(maxVal, V[N]);
			curve.addPoint((w * getTimeInterval()) * tau * problem.timeFactor(), V[N]);

		}

		curve.scale(maxTemp / maxVal);

	}

	@Override
	public DifferenceScheme copy() {
		var grid = getGrid();
		return new ExplicitLinearisedSolver(grid.getGridDensity(), grid.getTimeFactor(), getTimeLimit());
	}

	@Override
	public Class<? extends Problem> domain() {
		return LinearisedProblem.class;
	}

}