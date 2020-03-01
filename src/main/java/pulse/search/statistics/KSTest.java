package pulse.search.statistics;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.inference.TestUtils;

import pulse.tasks.SearchTask;

public class KSTest extends NormalityTest {
	
	public boolean test(SearchTask task) {
		double[] residuals = transformResiduals(task);		
		NormalDistribution nd = new NormalDistribution(0.0, sd); //null hypothesis: normal distribution with zero mean and empirical standard dev
		statistic = TestUtils.kolmogorovSmirnovStatistic(nd, residuals);
		probability = TestUtils.kolmogorovSmirnovTest(nd, residuals);
		return probability > significance;
		
	}
	
}