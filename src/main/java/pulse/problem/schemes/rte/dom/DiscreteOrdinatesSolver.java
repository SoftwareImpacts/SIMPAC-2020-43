package pulse.problem.schemes.rte.dom;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import pulse.problem.schemes.Grid;
import pulse.problem.schemes.rte.EmissionFunction;
import pulse.problem.schemes.rte.MathUtils;
import pulse.problem.schemes.rte.RadiativeTransferSolver;
import pulse.problem.statements.ParticipatingMedium;
import pulse.properties.NumericProperty;
import pulse.properties.NumericPropertyKeyword;
import pulse.properties.Property;

public class DiscreteOrdinatesSolver extends RadiativeTransferSolver {

	public static void main(String[] args) {

		var problem = new ParticipatingMedium();
		problem.setOpticalThickness(NumericProperty.derive(NumericPropertyKeyword.OPTICAL_THICKNESS, 10.0));
		problem.setEmissivity(NumericProperty.derive(NumericPropertyKeyword.EMISSIVITY, 0.85));

		File f = null;
		try {
			f = new File(DiscreteOrdinatesSolver.class.getResource("/test/TestSolution.dat").toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		var data = new ArrayList<Double>();

		try (Scanner scanner = new Scanner(f)) {
			while (scanner.hasNextLine())
				data.add(Double.parseDouble(scanner.nextLine()));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] U = data.stream().mapToDouble(x -> x).toArray();
		int N = U.length - 1;

		var density = NumericProperty.derive(NumericPropertyKeyword.GRID_DENSITY, N);
		var tauFactor = NumericProperty.derive(NumericPropertyKeyword.TAU_FACTOR, 0.0);
		Grid grid = new Grid(density, tauFactor);

		double tFactor = 10.0 / 800.0;

		var rte = new DiscreteOrdinatesSolver(problem, grid);
		rte.integrator.emissionFunction.setReductionFactor(tFactor);
		rte.integrator.setAlbedo(0.4);
		rte.integrator.ipf.setAnisotropyFactor(0.9);
		rte.compute(U);

		for (int i = 0; i < rte.discrete.n; i++)
			System.out.println(rte.discrete.I[rte.getExternalGridDensity()][i]);

		System.out.printf("%n%2.4f %4.5f %4.5f", rte.discrete.grid.getNode(0), rte.getFlux(0),
				rte.getFluxDerivativeFront());

		for (int i = 1; i < U.length - 1; i++)
			System.out.printf("%n%2.4f %4.5f %4.5f", i * (1.0 / (U.length - 1) * rte.getOpticalThickness()),
					rte.getFlux(i), rte.getFluxDerivative(i));

		System.out.printf("%n%2.4f %4.5f %4.5f", (U.length - 1) * (1.0 / (U.length - 1) * rte.getOpticalThickness()),
				rte.getFlux((U.length - 1)), rte.getFluxDerivativeRear());

	}

	private double iterationError;
	private UnivariateInterpolator interpolator;
	private PhaseFunction ipf;
	private NumericIntegrator integrator;

	private DiscreteIntensities discrete;
	private int nT;

	/**
	 * Constructs a discrete ordinates solver using the parameters (emissivity,
	 * scattering albedo and optical thickness) declared by the {@code problem}
	 * object. The nodes and weights of the quadrature are initialised using an
	 * instance of the {@code LegendrePoly class}.
	 * 
	 * @see pulse.problem.schemes.rte.dom.GaussianQuadrature
	 * @param problem statement
	 * @param n       even number of direction pairs for DOM passed to the
	 *                {@Code LegendrePoly constructor}
	 * @param N       is the number of equal-length grid segments
	 * @throws IllegalArgumentException if n is odd or less than two
	 */

	public DiscreteOrdinatesSolver(ParticipatingMedium problem, Grid grid) {
		super(problem, grid);

		iterationError = (double) NumericProperty.theDefault(NumericPropertyKeyword.DOM_ITERATION_ERROR).getValue();

		var emissionFunction = new EmissionFunction(problem, grid);

		discrete = new DiscreteIntensities((double) problem.getOpticalThickness().getValue());
		ipf = new HenyeyGreensteinIPF(discrete);
		integrator = new EmbeddedRK(discrete, emissionFunction, ipf);

		interpolator = new SplineInterpolator();

		init(problem, grid);
	}

//	private double[] arrayWithExtraPoint(final double[] tempArray) {
//		double[] uExtended = new double[tempArray.length + 1]; // create an array with an extra element to avoid
//																// conditional statements in the source(...) method
//		System.arraycopy(tempArray, 0, uExtended, 0, tempArray.length); // copy all elements
//		uExtended[tempArray.length] = uExtended[tempArray.length - 1]; // copy the last element
//		return uExtended;
//	}

	private void temperatureInterpolation(double[] tempArray) {
		nT = tempArray.length - 1;

		double[] xArray = new double[nT + 1];
		double h = 1.0 / nT;
		double tau0 = discrete.grid.getDimension();

		for (int i = 0; i < xArray.length; i++)
			xArray[i] = tau0 * i * h;

		var interpolator = new SplineInterpolator();
		UnivariateFunction function = interpolator.interpolate(xArray, tempArray);
		integrator.emissionFunction.setInterpolation(function);
	}

	@Override
	public void compute(double[] tempArray) {

		temperatureInterpolation(tempArray);
		discrete.clear();

		for (double iterationErrorSq = iterationError * iterationError, ql = 1, qr = 1; (MathUtils
				.fastPowLoop(discrete.getQLeft() - ql, 2)
				* (MathUtils.fastPowLoop(discrete.getQRight() - qr, 2)) > iterationErrorSq);) {
			ql = discrete.getQLeft();
			qr = discrete.getQRight();
			integrator.integrate();
		}

		this.fluxes();
		discrete.fluxDerivatives();
	}

	/**
	 * Interpolates local fluxes on external grid points.
	 */

	public void fluxes() {
		discrete.fluxes();

		int N = discrete.grid.getDensity();
		double hx = integrator.emissionFunction.getGridStep();

		setFlux(0, discrete.localFlux[0]);

		if (discrete.grid.getDensity() > nT) {
			// try linear interpolation (finite-difference) if the number of points is
			// sufficient
			for (int i = 1; i < nT; i++)
				setFlux(i, discrete.grid.interpolateUniform(discrete.localFlux, hx, i));
		} else {
			UnivariateFunction function = interpolator.interpolate(discrete.grid.getNodes(), discrete.localFlux);
			double hxd = hx * this.getOpticalThickness();
			for (int i = 1; i < nT; i++)
				setFlux(i, function.value(hxd * i));
		}

		setFlux(nT, discrete.localFlux[N]);
	}

	@Override
	public String getDescriptor() {
		return "Discrete Ordinates Method (DOM)";
	}

	@Override
	public int getExternalGridDensity() {
		return discrete.grid.getDensity();
	}

	@Override
	public double getFluxDerivative(int u) {
		double hx = integrator.emissionFunction.getGridStep();
		return discrete.grid.interpolateUniform(discrete.localFluxDerivative, hx, u);
	}

	@Override
	public double getFluxDerivativeFront() {
		return discrete.localFluxDerivative[0];
	}

	@Override
	public double getFluxDerivativeRear() {
		return discrete.localFluxDerivative[this.getExternalGridDensity()];
	}

	@Override
	public double getFluxMeanDerivative(int u) {
		double hx = integrator.emissionFunction.getGridStep();
		return 0.5 * (discrete.grid.interpolateUniform(discrete.localFluxDerivative, hx, u)
				+ discrete.grid.interpolateUniform(discrete.storedFD, hx, u));
	}

	@Override
	public double getFluxMeanDerivativeFront() {
		return 0.5 * (discrete.localFluxDerivative[0] + discrete.storedFD[0]);
	}

	@Override
	public double getFluxMeanDerivativeRear() {
		int N = discrete.grid.getDensity();
		return 0.5 * (discrete.localFluxDerivative[N] + discrete.storedFD[N]);
	}

	public NumericProperty getIterationErrorTolerance() {
		return NumericProperty.derive(NumericPropertyKeyword.DOM_ITERATION_ERROR, this.iterationError);
	}

	@Override
	public void init(ParticipatingMedium problem, Grid grid) {
		super.init(problem, grid);

		ipf.setAnisotropyFactor((double) problem.getScatteringAnisostropy().getValue());
		discrete.setEmissivity(problem.getEmissivity());
		integrator.init(problem, grid);

		super.reinitArrays((int) grid.getGridDensity().getValue());
		discrete.grid.setDimension((double) problem.getOpticalThickness().getValue());
		discrete.grid.generateUniform(StretchedGrid.DEFAULT_GRID_DENSITY, true);

	}

	@Override
	public List<Property> listedTypes() {
		List<Property> list = super.listedTypes();
		list.add(NumericProperty.def(NumericPropertyKeyword.DOM_DIRECTIONS));
		list.add(NumericProperty.def(NumericPropertyKeyword.DOM_ITERATION_ERROR));
		return list;
	}

	@Override
	public void set(NumericPropertyKeyword type, NumericProperty property) {
		switch (type) {
		case DOM_ITERATION_ERROR:
			setIterationErrorTolerance(property);
			break;
		default:
			return;
		}

		notifyListeners(this, property);

	}

	public void setIterationErrorTolerance(NumericProperty e) {
		if (e.getType() != NumericPropertyKeyword.DOM_ITERATION_ERROR)
			throw new IllegalArgumentException("Illegal type: " + e.getType());
		this.iterationError = (double) e.getValue();
	}

	@Override
	public void store() {
		super.store();
		discrete.store();
	}

}