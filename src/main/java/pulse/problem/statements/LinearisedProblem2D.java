package pulse.problem.statements;

import pulse.problem.schemes.ADIScheme;
import pulse.problem.schemes.DifferenceScheme;
import pulse.ui.Messages;

/**
 * The complete problem statement for a fully two-dimensional problem, which includes
 * side heat losses, a variable field of view and variable pulse-to-diameter ratio.  
 *
 */

public class LinearisedProblem2D extends Problem2D {

	public LinearisedProblem2D() {
		super();
	}

	public LinearisedProblem2D(Problem lp2) {
		super(lp2);
	}

	public LinearisedProblem2D(Problem2D lp2) {
		super(lp2);
	}

	@Override
	public Class<? extends DifferenceScheme> defaultScheme() {
		return ADIScheme.class;
	}

	@Override
	public String toString() {
		return Messages.getString("LinearizedProblem2D.Descriptor"); //$NON-NLS-1$
	}

}