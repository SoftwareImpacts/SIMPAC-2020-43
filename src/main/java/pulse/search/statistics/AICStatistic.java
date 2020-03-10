package pulse.search.statistics;

import pulse.properties.Flag;
import pulse.search.direction.PathOptimiser;
import pulse.tasks.SearchTask;

/**
 * AIC algorithm: Banks, H. T., & Joyner, M. L. (2017). Applied Mathematics Letters, 74, 33–45. doi:10.1016/j.aml.2017.05.005 
 * 
 */

public class AICStatistic extends SumOfSquares {
	
	private int kq;
	private final static double PENALISATION_FACTOR = Math.log(2.0*Math.PI) + 1.0;
	
	@Override
	public void evaluate(SearchTask t) {
		kq = Flag.convert(PathOptimiser.getSearchFlags()).size();
		super.evaluate(t);
		double n = getResiduals().size();
		this.statistic = n*Math.log(statistic) + 2.0*(kq + 1) + n*PENALISATION_FACTOR;
	}
	
	@Override
	public String getDescriptor() {
		return "Akaike Information Criterion (AIC)";
	}
	
	public int getNumVariables() {
		return kq;
	}

}