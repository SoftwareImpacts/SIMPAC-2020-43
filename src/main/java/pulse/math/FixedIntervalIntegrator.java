package pulse.math;

import static pulse.properties.NumericProperty.def;
import static pulse.properties.NumericProperty.derive;
import static pulse.properties.NumericProperty.requireType;
import static pulse.properties.NumericProperty.theDefault;
import static pulse.properties.NumericPropertyKeyword.INTEGRATION_SEGMENTS;

import java.util.ArrayList;
import java.util.List;

import pulse.properties.NumericProperty;
import pulse.properties.NumericPropertyKeyword;
import pulse.properties.Property;

/**
 * A fixed-interval integrator implements a numerical scheme in which the domain
 * within the integration bounds is divided into equal intervals of a sufficiently
 * small (and fixed) length. The integral is then approximately equal to the sum of
 * the integrand function values at the nodes resulting from such partitioning times the 
 * integration weights.
 *
 */

public abstract class FixedIntervalIntegrator extends AbstractIntegrator {

	private double dx;
	private int integrationSegments;
	
	/**
	 * Creates a {@code FixedIntervalIntegrator} with the specified integration bounds
	 * and a default number of integration segments.
	 * @param bounds the integration bounds
	 */
	
	public FixedIntervalIntegrator(Segment bounds) {
		this(bounds, theDefault(INTEGRATION_SEGMENTS));
	}
	
	/**
	 * Creates a {@code FixedIntervalIntegrator} with the specified integration bounds
	 * number of integration segments.
	 * @param bounds the integration bounds
	 * @param segments number of integration segments
	 */

	public FixedIntervalIntegrator(Segment bounds, NumericProperty segments) {
		super(bounds);
		setIntegrationSegments(segments);
	}
	
	/**
	 * Retrieves the number of integration segments.
	 * @return the number of integration segments.
	 */
	
	public NumericProperty getIntegrationSegments() {
		return derive(INTEGRATION_SEGMENTS, integrationSegments);
	}
	
	/**
	 * Sets the number of integration segments and re-evaluates the integration step size.
	 * @param integrationSegments a property of the {@code INTEGRATION_SEGMENTS} type
	 */

	public void setIntegrationSegments(NumericProperty integrationSegments) {
		requireType(integrationSegments, INTEGRATION_SEGMENTS);
		this.integrationSegments = (int) integrationSegments.getValue();
		evaluateStepSize();
	}
	
	/**
	 * Sets the bounds to the argument and re-evaluates the integration step size.
	 * @param bounds the integration bounds
	 */
	
	@Override
	public void setBounds(Segment bounds) {
		super.setBounds(bounds);
		evaluateStepSize();
	}
	
	private void evaluateStepSize() {
		dx = getBounds().length()/(double)this.integrationSegments;
	}
	
	@Override
	public void set(NumericPropertyKeyword type, NumericProperty property) {
		if (type == INTEGRATION_SEGMENTS) {
			setIntegrationSegments(property);
			firePropertyChanged(this, property);
		}
	}

	/**
	 * The listed property is {@code INTEGRATION_SEGMENTS}.
	 */

	@Override
	public List<Property> listedTypes() {
		List<Property> list = new ArrayList<>();
		list.add(def(INTEGRATION_SEGMENTS));
		return list;
	}
	
	/**
	 * Retrieves the step size equal to the integration range length divided by the number of integration segments.
	 * @return the integration step size.
	 */

	public double getStepSize() {
		return dx;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " ; " + getIntegrationSegments();
	}
	
}