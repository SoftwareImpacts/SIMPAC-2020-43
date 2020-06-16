package pulse.search.direction;

import pulse.math.Matrix;
import pulse.tasks.SearchTask;

/**
 * <p>
 * A more complex version of {@code Path}, which in addition to other variables
 * stores the Hessian matrix at the current step. Note the {@code reset} method
 * is overriden.
 * </p>
 *
 */

public class ComplexPath extends Path {

	private Matrix hessian;

	protected ComplexPath(SearchTask task) {
		super(task);
	}

	/**
	 * In addition to the superclass method, resets the Hessian to an Identity
	 * matrix.
	 */

	@Override
	public void reset(SearchTask task) {
		setGradient(PathOptimiser.gradient(task));
		hessian = new Matrix(PathOptimiser.activeParameters().size(), 1.0);
		setDirection(PathOptimiser.getSelectedPathOptimiser().direction(this));
	}

	public Matrix getHessian() {
		return hessian;
	}

	public void setHessian(Matrix hes) {
		this.hessian = hes;
	}

}