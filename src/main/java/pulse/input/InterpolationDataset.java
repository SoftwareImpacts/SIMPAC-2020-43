package pulse.input;

import java.util.ArrayList;
import java.util.List;

import pulse.util.ImmutableDataEntry;

/**
 * An {@code InterpolationDataset} stores data in a {@code List} of {@code DataEntry<Double,Double>} objects (each containing a 'key' and a 'value')
 * and provides means to interpolate between the 'values' using the 'keys'. This is used mainly to interpolate between
 * available data for thermal properties loaded in tabular representation, e.g. the density and specific heat tables. 
 */

public class InterpolationDataset {

	private List<ImmutableDataEntry<Double,Double>> dataset;
	private static InterpolationDataset specificHeatData;
	private static InterpolationDataset densityData;
	
	/**
	 * Creates an empty {@code InterpolationDataset}. 
	 */
	
	public InterpolationDataset() {				
		dataset = new ArrayList<ImmutableDataEntry<Double,Double>>();
	}
	
	/**
	 * Iterates over the {@code List} of {@code DataEntry} objects to find one 
	 * that has the closest {@code getKey() < key} value to the argument {@code key}.
	 * @param key the key, which is the upper bound for the search.
	 * @return a {@code DataEntry} object, satisfying the conditions above.
	 */
	
	public ImmutableDataEntry<Double,Double> previousTo(double key) {
		return dataset.stream().filter(element -> element.getKey() < key).reduce( (a,b) -> b).get();
	}
	
	/**
	 * Provides an interpolated value at {@code key} based on the available data in the {@code DataEntry List}.
	 * <p> The interpolation is linear, i.e. {@code result = k*key + b}, where {@code k} and {@code b} are the 
	 * parameters of the linear function calculated based on the value of the adjacent {@code DataEntry} objects.
	 * The adjacent {@code DataEntries} are calculated using the {@code previousTo} method. When found,
	 * a simple set of equations (<math>2x2</math>) is solved to calculate the {@code k} and {@code b} values,
	 * which are then substituted in the linear equation.</p>  
	 * @param key the argument, at which interpolation needs to be done (e.g. temperature)
	 * @return a double, representing the interpolated value
	 * @see previousTo
	 */
	
	public double interpolateAt(double key) {
		var entry	= previousTo(key);
		var next	= dataset.get(dataset.indexOf(entry)+1);
		
		double k = ( next.getValue() - entry.getValue() ) /
				   ( next.getKey() - entry.getKey() );
		
		double b = ( entry.getValue()*next.getKey() - next.getValue()*entry.getKey() ) /
				   ( next.getKey() - entry.getKey() );
		
		return k*key + b;
		
	}
	
	/**
	 * Adds {@code entry} to this {@code InterpolationDataset}.
	 * @param entry the entry to be added
	 */

	public void add(ImmutableDataEntry<Double,Double> entry) {
		dataset.add(entry);
	}
	
	/**
	 * Extracts all data available in this {@code InterpolationDataset}.
	 * @return the {@code List} of data.
	 */
	
	public List<ImmutableDataEntry<Double,Double>> getData() {
		return dataset;			
	}
	
	public static InterpolationDataset getSpecificHeatData() {
		return specificHeatData;
	}

	public static void setSpecificHeatData(InterpolationDataset specificHeatData) {
		InterpolationDataset.specificHeatData = specificHeatData;
	}

	public static InterpolationDataset getDensityData() {
		return densityData;
	}

	public static void setDensityData(InterpolationDataset densityData) {
		InterpolationDataset.densityData = densityData;
	}

	public enum Type {
		SPECIFIC_HEAT, DENSITY;
	}
	
}