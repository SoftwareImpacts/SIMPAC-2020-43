package pulse.io.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import pulse.input.Metadata;
import pulse.properties.EnumProperty;
import pulse.properties.NumericProperty;
import pulse.properties.NumericPropertyKeyword;
import pulse.ui.Messages;
import pulse.util.ImmutableDataEntry;

/**
 * An {@code AbstractReader} capable of reading metafiles. 
 * <p>Metafiles are ASCII files storing various experimental parameters for
 * different instances of {@code ExperimentalData}. The {@code Metadata (.met)} file 
 * should be formatted to include a header of arbitrary length, which defines global parameters,
 * and a table where a series of metaproperties is defined for each laser shot.</p>
 * <p>Metadata for each shot should be recorded during the experiment in a tab delimited ASCII format, with a {@code .met} file suffix. Constant data should be recorded in tab-separated pairs at the top of the file, such as {@code Sample_Name}, {@code Thickness} (of the sample, in mm), {@code Diameter} (of the sample, in mm), {@code Spot_Diameter} (diameter of laser spot, in mm), {@code PulseShape} (in capitals; e.g. {@code TRAPEZOIDAL}, {@code RECTANGULAR}) and {@code Detector_Iris}.
 * Two line breaks below, a tab-delimited table with headers for variables should contain variable data for each shot. These variables should include ID (which should relate to the final number of the file name for each shot), Test_Temperature (in deg. C), Pulse_Width (the time width of the laser pulse, in ms), {@code Absorbed_Energy} (the energy transmitted by the laser, in J), and Detector_Gain (gain of the detector). 
 * If any of the “constants” listed above are variable, then they should be included in the variable table, and vice versa.</p>
 * The full list of keywords for the {@code .met} files are listed in the {@code NumericPropertyKeyword} enum.
 * 
 * <p>An example of a valid {@code .met} file is provided below.</p>
 * 
 * <pre><code>
 * Thickness	2.034 						
 * Diameter	9.88 			
 * Spot_Diameter	2 						
 *							
 * ID	Test_Temperature	Pulse_Width	Spot_Diameter	Absorbed_Energy	Detector_Gain	PulseShape	Detector_Iris
 * 200	200	5	2	31.81	50	TRAPEZOIDAL	1
 * 201	196	5	2	31.81	100	TRAPEZOIDAL	1
 * 202	198	5	2	31.81	100	TRAPEZOIDAL	1
 * 203	199	5	2	31.81	50	TRAPEZOIDAL	1
 * 204	199	5	2	31.81	50	TRAPEZOIDAL	1
 * 205	199	5	2	31.81	50	TRAPEZOIDAL	1
 * 206	200	5	2	31.81	50	TRAPEZOIDAL	1
 * 207	200	5	2	31.81	50	TRAPEZOIDAL	1
 * 208	400	5	2	31.81	50	TRAPEZOIDAL	1
 * 209	400	5	2	31.81	20	TRAPEZOIDAL	1
 * 210	400	5	2	31.81	10	TRAPEZOIDAL	1
 * </code></pre>
 * @see pulse.properties.NumericPropertyKeyword
 * @see pulse.problem.statements.Pulse.TemporalShape
 */

public class MetaFileReader implements AbstractReader {	
	
	private static MetaFileReader instance = new MetaFileReader();
	
	private final static double CELSIUS_TO_KELVIN = 273;
	
	private MetaFileReader() {}

	public static MetaFileReader getInstance() {
		return instance;
	}
	
	public void populateMetadata(File file, Metadata met) throws IOException {
			Objects.requireNonNull(file, Messages.getString("MetaFileReader.1")); //$NON-NLS-1$			
			
			BufferedReader reader = new BufferedReader(new FileReader(file));

			Map<Integer,String> metaFormat = new HashMap<Integer,String>();
			metaFormat.put(0, "ID"); //id must always be the first entry of a row
			
			List<String> tokens = new LinkedList<String>();
							
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				
				StringTokenizer st = new StringTokenizer(line);
				
				tokens.clear();
				for( ; st.hasMoreTokens() ; )
					tokens.add(st.nextToken());
				int size = tokens.size();
				
				if(size < 1)
					continue;				
				
				if(size == 2) {
					
					List<ImmutableDataEntry<String,String>> val = new ArrayList<ImmutableDataEntry<String,String>>();
					ImmutableDataEntry<String,String> entry = new ImmutableDataEntry<String,String>( tokens.get(0), tokens.get(1) );
					val.add(entry);
								
					switch( tokens.get(0) ) {					
					case "Sample" : 
						met.setSampleName(tokens.get(1));
						break;
					default :
						try {
							translate(val, met);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							System.err.println("Error changing property in Metadata object. Details below.");
							e.printStackTrace();
						}
					break;
					
					}
					
				} else {
					
					if(tokens.get(0).equalsIgnoreCase( metaFormat.get(0) )) {
						
						for(int i = 1; i < size; i++) 
							metaFormat.put(i, tokens.get(i));
						
					} 
					
					else {						
						
						if( Math.abs( Integer.valueOf(tokens.get(0)) - met.getExternalID() ) > 0.5 ) 							
							continue;												
												
						List<ImmutableDataEntry<String,String>> values = new ArrayList<ImmutableDataEntry<String,String>>(size);						
												
						for(int i = 1; i < size; i++) 						
							values.add(new ImmutableDataEntry<String,String>(metaFormat.get(i), tokens.get(i)));													
						
						try {
							translate(values, met);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							System.err.println("Error changing property in Metadata object. Details below.");
							e.printStackTrace();
						}
						break;
						
					}
					
				}																
				
			}		
				
			reader.close();						
					
	}	
	
	private void translate(List<ImmutableDataEntry<String,String>> data, Metadata met) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		double tmp;		
		
		for(NumericProperty metaEntry : met.numericData()) {

			inner: for(ImmutableDataEntry<String,String> dataEntry : data) {			
				
				if(!metaEntry.getType().toString().
				equalsIgnoreCase(dataEntry.getKey()))
					continue;

				tmp = Double.valueOf( dataEntry.getValue() );
				if(metaEntry.getType() == NumericPropertyKeyword.TEST_TEMPERATURE)
					tmp += CELSIUS_TO_KELVIN;
							
				tmp /= (metaEntry.getDimensionFactor() ).doubleValue();
								
				if( NumericProperty.isValueSensible(metaEntry, tmp)) {																
					met.updateProperty( instance, new NumericProperty(tmp, metaEntry) );
					break inner;												
				}																	
				
			}
			
		}
					
		for(EnumProperty genericEntry : met.enumData())  
		{			
			inner: for(ImmutableDataEntry<String,String> dataEntry : data) {
				
						if(dataEntry.getKey().equals(genericEntry.getClass().getSimpleName())) {		
							met.updateProperty( instance, genericEntry.evaluate(dataEntry.getValue()) );						
							break inner;						
					}
				
				}
		
			}				
								
	}
	
	@Override
	public String getSupportedExtension() {
		return Messages.getString("MetaFileReader.0"); //$NON-NLS-1$
	}

}
