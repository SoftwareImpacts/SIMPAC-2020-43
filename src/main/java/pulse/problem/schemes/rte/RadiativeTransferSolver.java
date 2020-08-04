package pulse.problem.schemes.rte;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;

import pulse.problem.schemes.Grid;
import pulse.problem.statements.ParticipatingMedium;
import pulse.util.Descriptive;
import pulse.util.PropertyHolder;
import pulse.util.Reflexive;

/**
 * Manages processes to solve the radiative transfer equation and generate the
 * input needed by the heat problem, i.e. fluxes and their derivatives. Uses a
 * {@code SplineInterpolator} to generate a smooth spatial temperature profile.
 * Provides means of probing the calculation health and tracking calculation
 * steps with listeners.
 *
 */

public abstract class RadiativeTransferSolver extends PropertyHolder implements Reflexive, Descriptive {

	private Fluxes fluxes;
	private List<RTECalculationListener> rteListeners;

	/**
	 * Dummy constructor.
	 * 
	 */

	public RadiativeTransferSolver() {
		rteListeners = new ArrayList<>();
	}

	/**
	 * Launches a calculation of the radiative transfer equation.
	 * 
	 * @param temperatureArray the input temperature profile
	 * @return the status of calculation
	 */

	public abstract RTECalculationStatus compute(double[] temperatureArray);

	/**
	 * Retrieves the parameters from {@code p} and {@code grid} needed to run the
	 * calculations. Resets the flux arrays.
	 * 
	 * @param p    the problem statement
	 * @param grid the grid
	 */

	public void init(ParticipatingMedium p, Grid grid) {
		if (fluxes != null) {
			fluxes.setDensity(grid.getGridDensity());
			fluxes.setOpticalThickness(p.getOpticalThickness());
		}
	}

	/**
	 * Performs interpolation with natural cubic splines using the input arguments.
	 * 
	 * @param tempArray an array of data defined on a previously initialised grid.
	 * @return a {@code UnivariateFunction} generated with a
	 *         {@code SplineInterpolator}
	 */

	public UnivariateFunction interpolateTemperatureProfile(final double[] tempArray) {
		var xArray = new double[tempArray.length + 1];

		for (int i = -1; i < xArray.length - 1; i++)
			xArray[i + 1] = opticalCoordinateAt(i);
		
		var tarray = new double[tempArray.length + 1];
		System.arraycopy(tempArray, 0, tarray, 1, tempArray.length);
		final double alpha = (xArray[0] - xArray[1])/(xArray[2] - xArray[1]);
		tarray[0] = tarray[1] * alpha + (1.0 - alpha) * tarray[2];
		
		return (new SplineInterpolator()).interpolate(xArray, tarray);
	}

	/**
	 * Retrieves the optical coordinate corresponding to the grid index {@code i}
	 * 
	 * @param i the external grid index
	 * @return <math>&tau;<sub>0</sub>/<i>N</i> <i>i</i> </math>
	 */

	public double opticalCoordinateAt(final int i) {
		return fluxes.getOpticalGridStep() * i;
	}

	@Override
	public boolean ignoreSiblings() {
		return true;
	}

	@Override
	public String getPrefix() {
		return "Radiative Transfer Solver";
	}

	public List<RTECalculationListener> getRTEListeners() {
		return rteListeners;
	}

	/**
	 * Adds a listener that can listen to status updates.
	 * 
	 * @param listener a listener to track the calculation progress
	 */

	public void addRTEListener(RTECalculationListener listener) {
		rteListeners.add(listener);
	}

	public void fireStatusUpdate(RTECalculationStatus status) {
		for (RTECalculationListener l : getRTEListeners())
			l.onStatusUpdate(status);
	}

	public Fluxes getFluxes() {
		return fluxes;
	}

	public void setFluxes(Fluxes fluxes) {
		this.fluxes = fluxes;
	}

}