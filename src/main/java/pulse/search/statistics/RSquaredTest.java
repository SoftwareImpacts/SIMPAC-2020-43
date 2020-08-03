package pulse.search.statistics;

import pulse.input.ExperimentalData;
import pulse.tasks.SearchTask;

/**
 *The coefficient of determination represents the goodness of fit that a {@code HeatingCurve}
 *provides for the {@code ExperimentalData}
 *
 */

public class RSquaredTest extends NormalityTest {

	private SumOfSquares sos;
	public final static double SUCCESS_CUTOFF = 0.2;

	public RSquaredTest() {
		super();
	}

	public RSquaredTest(SumOfSquares sos) {
		this();
		this.sos = sos;
	}

	@Override
	public boolean test(SearchTask task) {
		evaluate(task);
		return statistic > SUCCESS_CUTOFF;
	}

	/**
	 * Calculates the coefficient of determination, or simply the
	 * <math><msup><mi>R</mi><mn>2</mn></msup></math> value.
	 * <p>
	 * First, the mean temperature of the {@code data} is calculated. Then, the
	 * {@code TSS} (total sum of squares) is calculated as proportional to the
	 * variance of data. The residual sum of squares ({@code RSS}) is calculated by
	 * calling {@code this.deviationSquares(curve)}. Finally, these values are
	 * combined together as: {@code 1 - RSS/TSS}.
	 * </p>
	 * 
	 * @param t the task containing the reference data
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Coefficient_of_determination">Wikipedia
	 *      page</a>
	 */

	@Override
	public void evaluate(SearchTask t) {

		ExperimentalData reference = t.getExperimentalCurve();

		double mean = 0;

		int start = reference.getIndexRange().getLowerBound();
		int end = reference.getIndexRange().getUpperBound();

		for (int i = start; i < end; i++) {
			mean += reference.signalAt(i);
		}

		mean /= (end - start);

		double TSS = 0;

		for (int i = start; i < end; i++) {
			TSS += Math.pow(reference.signalAt(i) - mean, 2);
		}

		TSS /= (end - start);

		this.statistic = (1. - sos.statistic / TSS);
	}

	public SumOfSquares getSumOfSquares() {
		return sos;
	}

	public void setSumOfSquares(SumOfSquares sos) {
		this.sos = sos;
	}

	@Override
	public String getDescriptor() {
		return "<html><i>R</i><sup>2</sup> test";
	}

}