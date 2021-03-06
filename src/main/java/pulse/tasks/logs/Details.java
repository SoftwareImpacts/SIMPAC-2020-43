package pulse.tasks.logs;

/**
 * An enum which lists different possible problems wit the {@code SearchTask}.
 *
 */

public enum Details {

	NONE,

	/**
	 * The {@code Problem} has not been specified by the user.
	 */

	MISSING_PROBLEM_STATEMENT,

	/**
	 * The {@code DifferenceScheme} for solving the {@code Problem} has not been
	 * specified by the user.
	 */

	MISSING_DIFFERENCE_SCHEME,

	/**
	 * A heating curve has not been set up for the {@code DifferenceScheme}.
	 */

	MISSING_HEATING_CURVE,

	/**
	 * No information can be found about the selected path solver.
	 */

	MISSING_LINEAR_SOLVER,

	/**
	 * There is no information about the selected path solver.
	 */

	MISSING_PATH_SOLVER,

	/**
	 * The buffer has not been created.
	 */

	MISSING_BUFFER,

	/**
	 * Some data is missing in the problem statement. Probably, the interpolation
	 * datasets have been set up incorrectly or the specific heat and density data
	 * have not been loaded.
	 */

	INSUFFICIENT_DATA_IN_PROBLEM_STATEMENT,

	SIGNIFICANT_CORRELATION_BETWEEN_PARAMETERS,

	PARAMETER_VALUES_NOT_SENSIBLE,

	ABNORMAL_DISTRIBUTION_OF_RESIDUALS;

	@Override
	public String toString() {
		return Status.parse(super.toString());
	}

}