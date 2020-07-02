package pulse.problem.schemes;

import static pulse.properties.NumericProperty.derive;
import static pulse.properties.NumericPropertyKeyword.GRID_DENSITY;
import static pulse.properties.NumericPropertyKeyword.TAU_FACTOR;
import static pulse.ui.Messages.getString;

import pulse.properties.NumericProperty;

/**
 * This class implements the fully implicit finite-difference scheme for solving
 * the one-dimensional heat conduction problem.
 * <p>
 * The fully implicit scheme uses a standard 4-point template on a
 * one-dimensional grid that utilises the following grid-function values on each
 * step: <math><i>&Theta;(x<sub>i</sub>,t<sub>m</sub>),
 * &Theta;(x<sub>i</sub>,t<sub>m+1</sub>),
 * &Theta;(x<sub>i-1</sub>,t<sub>m+1</sub>),
 * &Theta;(x<sub>i+1</sub>,t<sub>m+1</sub>)</i></math>. Because no
 * <i>explicit</i> formula can be used for calculating the grid-function at
 * timestep <math><i>m</i>+1</math>, a sweep method is implemented instead. The
 * boundary conditions are approximated with a Taylor expansion up to the third
 * term, hence the scheme has an increased order of approximation.
 * </p>
 * <p>
 * The fully implicit scheme is unconditionally stable and has an order of
 * approximation of at least <math><i>O(&tau; + h<sup>2</sup>)</i></math> for
 * both the heat equation and the boundary conditions.
 * </p>
 * 
 * @see pulse.problem.statements.LinearisedProblem
 * @see pulse.problem.statements.NonlinearProblem
 */

public abstract class ImplicitScheme extends DifferenceScheme {

	/**
	 * Constructs a default fully-implicit scheme using the default values of
	 * {@code GRID_DENSITY} and {@code TAU_FACTOR}.
	 */

	public ImplicitScheme() {
		this(derive(GRID_DENSITY, 30), derive(TAU_FACTOR, 0.25) );
	}

	/**
	 * Constructs a fully-implicit scheme on a one-dimensional grid that is
	 * specified by the values {@code N} and {@code timeFactor}.
	 * 
	 * @see pulse.problem.schemes.DifferenceScheme
	 * @param N          the {@code NumericProperty} with the type
	 *                   {@code GRID_DENSITY}
	 * @param timeFactor the {@code NumericProperty} with the type
	 *                   {@code TAU_FACTOR}
	 */

	public ImplicitScheme(NumericProperty N, NumericProperty timeFactor) {
		super();
		initGrid(N, timeFactor);
	}

	/**
	 * <p>
	 * Constructs a fully-implicit scheme on a one-dimensional grid that is
	 * specified by the values {@code N} and {@code timeFactor}. Sets the time limit
	 * of this scheme to {@code timeLimit}
	 * 
	 * @param N          the {@code NumericProperty} with the type
	 *                   {@code GRID_DENSITY}
	 * @param timeFactor the {@code NumericProperty} with the type
	 *                   {@code TAU_FACTOR}
	 * @param timeLimit  the {@code NumericProperty} with the type
	 *                   {@code TIME_LIMIT}
	 * @see pulse.problem.schemes.DifferenceScheme
	 */

	public ImplicitScheme(NumericProperty N, NumericProperty timeFactor, NumericProperty timeLimit) {
		super(timeLimit);
		initGrid(N, timeFactor);
	}

	@Override
	public String toString() {
		return getString("ImplicitScheme.4");
	}

}