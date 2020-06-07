package pulse.problem.schemes.rte.dom;

public class HenyeyGreensteinIPF extends PhaseFunction {

	private double a1;
	private double a2;
	private double b1;

	public HenyeyGreensteinIPF(DiscreteIntensities intensities) {
		super(intensities);
		init();
	}

	public void init() {
		b1 = 2.0 * getAnisotropyFactor();
		a1 = 1.0 - getAnisotropyFactor() * getAnisotropyFactor();
		a2 = 1.0 + getAnisotropyFactor() * getAnisotropyFactor();
	}

	public double function(int i, int k) {
		double theta = intensities.mu[k] * intensities.mu[i];
		double f = a2 - b1 * theta;
		return a1 / (f * Math.sqrt(f));
	}

	@Override
	public void setAnisotropyFactor(double A1) {
		super.setAnisotropyFactor(A1);
		init();
	}

}