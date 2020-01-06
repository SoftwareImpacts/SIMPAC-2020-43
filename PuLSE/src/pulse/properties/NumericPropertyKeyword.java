package pulse.properties;

public enum NumericPropertyKeyword {
	
	/**
	 * The thermal diffusivity of the sample.
	 */
	
	DIFFUSIVITY, 
	
	/**
	 * Sample thickness.
	 */
	
	THICKNESS, 
	
	/**
	 * Sample diameter.
	 */
	
	DIAMETER, 
	
	/**
	 * The maximum temperature, or the signal height -- 
	 * if a relative temperature scale
	 * is used.
	 */
	
	MAXTEMP, 
	
	/**
	 * The number of points in the {@code HeatingCurve}.
	 */
	
	NUMPOINTS, 
	
	/**
	 * The precision parameter used to solve nonlinear problems.
	 */
	
	NONLINEAR_PRECISION, 
	
	/**
	 * Pulse width (time).
	 */
	
	PULSE_WIDTH, 
	
	/**
	 * Laser spot diameter.
	 */
	
	SPOT_DIAMETER, 
	
	/**
	 * Calculation time limit.
	 */
	
	TIME_LIMIT, 
	
	/**
	 * Grid (space partitioning) density.
	 */
	
	GRID_DENSITY, 
	
	/**
	 * Specific heat (<math><i>C</i><sub>p</sub></math>).
	 */
	
	SPECIFIC_HEAT, 
	
	/**
	 * Thermal conductivity.
	 */
	
	CONDUCTIVITY, 
	
	/**
	 * Emissivity of the sample (nonlinear problems).
	 */
	
	EMISSIVITY,
	
	/**
	 * Density of the sample.
	 */
	
	DENSITY, 
	
	/**
	 * Absorbed energy (nonlinear problems).
	 */
	
	LASER_ENERGY,
	
	/**
	 * Test temperature, at which the laser was fired at the sample.
	 */
	
	TEST_TEMPERATURE, 
	
	/**
	 * The resolution of linear search.
	 */
	
	LINEAR_RESOLUTION, 
	
	/**
	 * The accuracy of gradient calculation.
	 */
	
	GRADIENT_RESOLUTION,
	
	/**
	 * The buffer size that is used to establish whether results are converging.
	 */
	
	BUFFER_SIZE, 
	
	/**
	 * The total error tolerance.
	 */
	
	ERROR_TOLERANCE, 
	
	/**
	 * The diameter of the pyrometer detection area.
	 */
	
	PYROMETER_SPOT,
	
	/**
	 * The baseline slope (for linear baselines).
	 */
	
	BASELINE_SLOPE, 
	
	/**
	 * The baseline intercept value.
	 */
	
	BASELINE_INTERCEPT,
	
	/**
	 * The factor used to convert squared grid spacing to time step.
	 */
	
	TAU_FACTOR, 
	
	/**
	 * The detector gain (amplification).
	 */
	
	DETECTOR_GAIN, 
	
	/**
	 * The detector iris (aperture).
	 */
	
	DETECTOR_IRIS, 
	
	/**
	 * The coefficient of heat losses from the front (laser-facing) surface of the sample.
	 */
	
	HEAT_LOSS_FRONT, 
	
	/**
	 * The coefficient of heat losses from the side surface of the sample (two-dimensional problems).
	 */
	
	HEAT_LOSS_SIDE, 
	
	/**
	 * The coefficient of heat losses from the rear (pyrometer-facing) surface of the sample.
	 */
	
	HEAT_LOSS_REAR,
	
	/**
	 * A general keyword for the coefficient of heat losses.
	 */
	
	HEAT_LOSS, 
	
	/**
	 * The <math><i>R</i><sup>2</sup></math> (coefficient of determination).
	 */
	
	RSQUARED, 
	
	/**
	 * The sum of squared residuals.
	 */
	
	SUM_OF_SQUARES, 
	
	/**
	 * Search iteration.
	 */
	
	ITERATION, 
	
	/**
	 * Task identifier.
	 */
	
	IDENTIFIER, 
	
	/**
	 * Iteration limit for reverse problem solution.
	 */
	
	ITERATION_LIMIT,
	
	/**
	 * Dimensionless coefficient of laser energy absorption (&gamma;<sub>0</sub>).
	 */
	
	ABSORPTIVITY,
	
	/**
	 * Reflectance of the sample (0 < R <= 1).
	 */
	
	REFLECTANCE,
	
	/**
	 * A dimensionless coefficient in the radiation flux expression for the radiative heat transfer between the front and the rear (coated) surfaces.
	 * Used by the <code>DiathermicMaterialProblem</code>. 
	 */
	
	DIATHERMIC_COEFFICIENT;
	
}