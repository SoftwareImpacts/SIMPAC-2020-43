package pulse.problem.statements;

import static pulse.properties.NumericPropertyKeyword.LASER_ENERGY;
import static pulse.properties.NumericPropertyKeyword.PULSE_WIDTH;
import static pulse.properties.NumericPropertyKeyword.SPOT_DIAMETER;

import java.util.ArrayList;
import java.util.List;

import pulse.properties.EnumProperty;
import pulse.properties.NumericProperty;
import pulse.properties.NumericPropertyKeyword;
import pulse.properties.Property;
import pulse.ui.Messages;
import pulse.util.PropertyHolder;

/**
 * A {@code Pulse} stores the parameters of the laser pulse,
 * but does not provide the calculation facilities.
 * @see pulse.problem.schemes.DiscretePulse 
 * 
 */

public class Pulse extends PropertyHolder {
	
	private TemporalShape pulseShape;
	protected double pulseWidth, spotDiameter;
	private double laserEnergy;
	
	/**
	 * Creates a default {@code Pulse} with a {@code RECTANGULAR} shape.
	 */
	
	public Pulse() {
		this(TemporalShape.RECTANGULAR);
	}
	
	/**
	 * Creates a {@code Pulse} with default values of pulse width and laser spot diameter (as per XML specification),
	 * but with custom {@code PulseShape}.
	 * @param pform the pulse shape
	 */
	
	public Pulse(TemporalShape pform) {
		this.pulseShape = pform;
		pulseWidth		= (double) NumericProperty.def(PULSE_WIDTH).getValue();
		spotDiameter	= (double) NumericProperty.def(SPOT_DIAMETER).getValue();
		laserEnergy		= (double) NumericProperty.def(LASER_ENERGY).getValue();
	}
	
	/**
	 * Copy constructor
	 * @param p the pulse, parameters of which will be copied.
	 */

	public Pulse(Pulse p) {
		this.pulseShape		= p.getPulseShape();
		this.spotDiameter	= p.spotDiameter;
		this.pulseWidth		= p.pulseWidth;
		this.laserEnergy	= p.laserEnergy;
	}
	
	/**
	 * Retrieves the {code PulseShape} enum constant.
	 * @return the {@code} PulseShape
	 */
	
	public TemporalShape getPulseShape() {
		return pulseShape;
	}

	public void setPulseShape(TemporalShape pulseShape) {
		this.pulseShape = pulseShape;
	}

	public NumericProperty getPulseWidth() {
		return NumericProperty.derive(PULSE_WIDTH, pulseWidth);
	}

	public void setPulseWidth(NumericProperty pulseWidth) {
		this.pulseWidth = (double)pulseWidth.getValue();
	}

	public NumericProperty getSpotDiameter() {
		return NumericProperty.derive(SPOT_DIAMETER, spotDiameter);
	}

	public void setSpotDiameter(NumericProperty spotDiameter) {
		this.spotDiameter = (double)spotDiameter.getValue();
		notifyListeners(this, spotDiameter);
	}
	
	public NumericProperty getLaserEnergy() {
		return NumericProperty.derive(LASER_ENERGY, laserEnergy);
	}
	
	public void setLaserEnergy(NumericProperty laserEnergy) {
		this.laserEnergy = (double)laserEnergy.getValue();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getPulseShape());
		sb.append(" ");
		sb.append(String.format("%3.2f", pulseWidth*1E3));
		sb.append(Messages.getString("Pulse.2"));
		sb.append(String.format("%3.2f", spotDiameter*1E3));
		sb.append(Messages.getString("Pulse.3"));
		sb.append(String.format("%3.2f", laserEnergy));
		sb.append(Messages.getString("Pulse.4"));
		return sb.toString();
	}
	
	/**
	 * The listed parameters for {@code Pulse} are: <code>PulseShape, PULSE_WIDTH, SPOT_DIAMETER</code>.
	 */
	
	@Override
	public List<Property> listedTypes() {
		List<Property> list = new ArrayList<Property>();
		list.add(TemporalShape.RECTANGULAR);
		list.add(NumericProperty.def(PULSE_WIDTH));
		list.add(NumericProperty.def(SPOT_DIAMETER));
		list.add(NumericProperty.def(LASER_ENERGY));
		return list;				
	}

	@Override
	public void set(NumericPropertyKeyword type, NumericProperty property) {
		switch(type) {
		case PULSE_WIDTH	: setPulseWidth(property); break;
		case SPOT_DIAMETER	: setSpotDiameter(property); break;
		case LASER_ENERGY	: setLaserEnergy(property); break;
		default:
			break;
		}
		
		notifyListeners(this, property);
		
	}
	
	/**
	 * The {@code PulseShape}, an instance of {@code EnumProperty}, defines 
	 * a few simple pulse shapes usually encountered in a laser flash experiment.  
	 *
	 */
	
	public enum TemporalShape implements EnumProperty {	
		
		/**
		 * Currently not supported (redirects to {@code RECTANGULAR})
		 */
		
		TRAPEZOIDAL,
		
		/**
		 * The simplest pulse shape defined as 
		 * <math>0.5*(1 + sgn(<i>t</i><sub>pulse</sub> - <i>t</i>))</math>,
		 * where <math>sgn(...)</math> is the signum function, 
		 * <sub>pulse</sub> is the pulse width.
		 * @see java.lang.Math.signum(double) 
		 */
		
		RECTANGULAR, 
		
		/**
		 * A pulse shape defined as an isosceles triangle. 
		 */
		
		TRIANGULAR,
		
		/**
		 * A bell-shaped laser pulse centered at {@code pulseWidth}/2.0. 
		 */
		
		GAUSSIAN;

		@Override
		public Object getValue() {
			return this;
		}	

		@Override
		public String getDescriptor(boolean addHtmlTags) {
			return "Pulse temporal shape";
		}

		@Override
		public EnumProperty evaluate(String string) {
			return valueOf(string);
		}

	}

}