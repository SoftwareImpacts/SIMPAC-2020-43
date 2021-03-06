package pulse.problem.statements.penetration;

public class BeerLambertAbsorption extends AbsorptionModel {

	public BeerLambertAbsorption() {
		super();
	}

	@Override
	public double absorption(SpectralRange range, double y) {
		double a = (double) (this.getAbsorptivity(range).getValue());
		return a * Math.exp(-a * y);
	}

}