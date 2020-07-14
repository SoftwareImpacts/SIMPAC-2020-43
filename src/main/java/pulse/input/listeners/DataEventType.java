package pulse.input.listeners;

/**
 * <p>
 * This is an enum type that is used to store some information about the type of
 * the {@code DataEvent}s occurring with a {@code HeatingCurve} object. This is 
 * currently limited to two types of events.
 * </p>
 *
 */

public enum DataEventType {
	/**
	 * <p>
	 * The {@code TRUNCATED} {@code DataEventType} indicates the range of the
	 * {@code ExperimentalData} has been truncated. Note this means that only
	 * the range is affected and not the data itself.
	 *  
	 * @see pulse.input.ExperimentalData.truncate()
	 */

	TRUNCATED,
	
	/**
	 * <p>Signal a time shift between the time sequences of a {@code HeatingCurve}
	 * and its linked {@code ExperimentalData}. Triggered either when manually changing
	 * the time origin of the solution (i.e., shifting it relative to the experimental
	 * data points) or by the search procedure.
	 */

	CHANGE_OF_ORIGIN;

}