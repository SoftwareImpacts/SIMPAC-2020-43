package pulse.problem.schemes.rte.exact;

import java.util.List;

import pulse.math.FunctionWithInterpolation;
import pulse.math.Segment;
import pulse.problem.schemes.Grid;
import pulse.problem.schemes.rte.BlackbodySpectrum;
import pulse.problem.schemes.rte.RTECalculationStatus;
import pulse.problem.schemes.rte.RadiativeTransferSolver;
import pulse.problem.statements.ParticipatingMedium;
import pulse.properties.NumericProperty;
import pulse.properties.NumericPropertyKeyword;
import pulse.properties.Property;
import pulse.util.InstanceDescriptor;

public abstract class NonscatteringRadiativeTransfer extends RadiativeTransferSolver {

	private static FunctionWithInterpolation ei3 = ExponentialIntegrals.get(3);

	private double emissivity, doubleReflectivity;
	private double tau0;

	private BlackbodySpectrum emissionFunction;
	private Convolution convolution;

	private double radiosityFront;
	private double radiosityRear;

	private static InstanceDescriptor<Convolution> instanceDescriptor = new InstanceDescriptor<Convolution>(
			"Quadrature Selector", Convolution.class);

	static {
		instanceDescriptor.setSelectedDescriptor(ChandrasekharsQuadrature.class.getSimpleName());
	}

	protected NonscatteringRadiativeTransfer(ParticipatingMedium problem, Grid grid) {
		super(problem, grid);
		init(problem, grid);
		emissionFunction = new BlackbodySpectrum(problem);
		initQuadrature();
		instanceDescriptor.addListener(() -> initQuadrature());
	}

	@Override
	public void init(ParticipatingMedium p, Grid grid) {
		super.init(p, grid);
		emissivity = p.getEmissivity();
		doubleReflectivity = 2.0 * (1.0 - emissivity);
		tau0 = (double) p.getOpticalThickness().getValue();
	}

	@Override
	public RTECalculationStatus compute(double[] array) {
		emissionFunction.setInterpolation(interpolateTemperatureProfile(array));
		radiosities();
		return RTECalculationStatus.NORMAL;
	}

	public void fluxes() {
		for (int i = 1, N = this.getExternalGridDensity(); i < N; i++) {
			flux(i);
		}
		boundaryFluxes();
	}

	/*
	 * Assumes radiosities have already been calculated using radiosities() F*(1) =
	 * -R_2 + 2R_1 E_3(\tau_0) + 2 int
	 */

	private double fluxRear() {
		int N = this.getExternalGridDensity();
		this.setFlux(N,
				-radiosityRear + 2.0 * radiosityFront * ei3.valueAt(tau0) + 2.0 * integrateSecondOrder(tau0, -1.0));
		return getFlux(N);
	}

	public void boundaryFluxes() {
		fluxFront();
		fluxRear();
	}

	public double fluxFront() {
		this.setFlux(0,
				radiosityFront - 2.0 * radiosityRear * ei3.valueAt(tau0) - 2.0 * integrateSecondOrder(0.0, 1.0));
		return getFlux(0);
	}

	protected double flux(int uIndex) {
		convolution.setOrder(2);
		double t = getOpticalGridStep() * uIndex;

		convolution.setBounds(new Segment(0, t));
		convolution.setCoefficients(t, -1.0);
		double I_1 = convolution.integrate();

		convolution.setBounds(new Segment(t, tau0));
		convolution.setCoefficients(-t, 1.0);
		double I_2 = convolution.integrate();

		double result = radiosityFront * ei3.valueAt(t) - radiosityRear * ei3.valueAt(tau0 - t) + I_1 - I_2;

		setFlux(uIndex, result * 2.0);

		return getFlux(uIndex);

	}

	/*
	 * Radiosities of front and rear surfaces respectively in the assumption of
	 * diffuse and opaque boundaries
	 */

	public void radiosities() {
		final double b = b();
		final double sq = 1.0 - b * b;
		final double a1 = a1();
		final double a2 = a2();
		radiosityFront = (a1 + b * a2) / sq;
		radiosityRear = (a2 + b * a1) / sq;
	}

	public Convolution getQuadrature() {
		return convolution;
	}

	public void setQuadrature(Convolution specialIntegrator) {
		this.convolution = specialIntegrator;
		convolution.setParent(this);
		convolution.setEmissionFunction(emissionFunction);
	}

	public BlackbodySpectrum getEmissionFunction() {
		return emissionFunction;
	}

	public void setEmissionFunction(BlackbodySpectrum emissionFunction) {
		this.emissionFunction = emissionFunction;
		convolution.setEmissionFunction(emissionFunction);
	}

	@Override
	public void set(NumericPropertyKeyword type, NumericProperty property) {
		// intentionally left blank
	}

	@Override
	public List<Property> listedTypes() {
		List<Property> list = super.listedTypes();
		list.add(instanceDescriptor);
		return list;
	}

	@Override
	public String toString() {
		return "( " + this.getSimpleName() + " )";
	}

	public static InstanceDescriptor<Convolution> getInstanceDescriptor() {
		return instanceDescriptor;
	}

	@Override
	public String getDescriptor() {
		return "Non-scattering Radiative Transfer";
	}

	public double getRadiosityFront() {
		return radiosityFront;
	}

	public double getRadiosityRear() {
		return radiosityRear;
	}

	/*
	 * Coefficient b
	 */

	private double b() {
		return doubleReflectivity * ei3.valueAt(tau0);
	}

	/*
	 * Coefficient a1
	 *
	 * a1 = \varepsilon*J*(0) + integral = int_0^1 { J*(t) E_2(\tau_0 t) dt }
	 */

	private double a1() {
		return emissivity * emissionFunction.powerAt(0.0) + doubleReflectivity * integrateSecondOrder(0.0, 1.0);
	}

	/*
	 * Coefficient a2
	 *
	 * a2 = \varepsilon*J*(0) + ... integral = int_0^1 { J*(t) E_2(\tau_0 t) dt }
	 */

	private double a2() {
		return emissivity * emissionFunction.powerAt(tau0) + doubleReflectivity * integrateSecondOrder(tau0, -1.0);
	}

	/*
	 * Source function J*(t) = (1 + @U[i]*tFactor)^4, where i = t/hx tFactor =
	 * (tMax/t0)
	 */

	private double integrateSecondOrder(double a, double b) {
		convolution.setBounds(new Segment(0, tau0));
		convolution.setOrder(2);
		convolution.setCoefficients(a, b);
		return convolution.integrate();
	}

	private void initQuadrature() {
		setQuadrature(instanceDescriptor.newInstance(Convolution.class));
	}
/*
	public static void main(String[] args) {
		var problem = new ParticipatingMedium();
		problem.setSpecificHeat(NumericProperty.derive(NumericPropertyKeyword.SPECIFIC_HEAT, 540.0));
		problem.setDensity(NumericProperty.derive(NumericPropertyKeyword.DENSITY, 10000.0));
		problem.setTestTemperature(NumericProperty.derive(NumericPropertyKeyword.TEST_TEMPERATURE, 800.0));
		problem.setOpticalThickness(NumericProperty.derive(NumericPropertyKeyword.OPTICAL_THICKNESS, 0.1));
		problem.setScatteringAlbedo(NumericProperty.derive(NumericPropertyKeyword.SCATTERING_ALBEDO, 0.0));
		System.out.println("Maximum heating: " + problem.maximumHeating() + " at " + problem.getTestTemperature());
		var scheme = new ImplicitCoupledSolver();

		File test = null;
		try {
			test = new File(NonscatteringRadiativeTransfer.class.getResource("/test/TestSolution.dat").toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scanner scanner = null;
		try {
			scanner = new Scanner(test);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Double> doubleList = new ArrayList<Double>();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			var numbersStrings = line.split(" ");
			doubleList.add(Double.valueOf(numbersStrings[0]));
		}

		int size = doubleList.size();

		scheme.getGrid().setGridDensity(NumericProperty.derive(NumericPropertyKeyword.GRID_DENSITY, size - 1));
		var rad = new NonscatteringAnalyticalDerivatives(problem, scheme.getGrid());
		//rad.setQuadrature(new NewtonCotesQuadrature());
		rad.setQuadrature(new ChandrasekharsQuadrature());
		var rad2 = new DiscreteOrdinatesSolver(problem, scheme.getGrid());

		rad.compute(doubleList.stream().mapToDouble(d -> d).toArray());
		rad2.compute(doubleList.stream().mapToDouble(d -> d).toArray());

		for (int i = 1; i < size - 1; i++) {
			System.out.printf("%n%3.2f \t %2.4e \t %2.4e \t %2.4e \t %2.4e", rad.opticalCoordinateAt(i), rad.getFlux(i),
					rad2.getFlux(i), rad.fluxDerivative(i), +rad2.fluxDerivative(i));
		}
		
	}
*/
}