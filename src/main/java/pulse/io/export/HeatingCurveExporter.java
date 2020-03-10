package pulse.io.export;

import java.io.FileOutputStream;
import java.io.PrintStream;

import pulse.HeatingCurve;
import pulse.ui.Messages;

public class HeatingCurveExporter implements Exporter<HeatingCurve> {

	private static HeatingCurveExporter instance = new HeatingCurveExporter();
	
	private HeatingCurveExporter() {
		// Intentionally blank
	}
	
	@Override
	public void printToStream(HeatingCurve hc, FileOutputStream fos, Extension extension) {		
		if(hc.arraySize() < 1)
			return;
			
		switch(extension) {
			case HTML : printHTML(hc, fos); break;
			case CSV : printCSV(hc, fos); break;
			default : 
				throw new IllegalArgumentException("Format not recognised: " + extension);
		}		
	}
	
	/**
	 * The supported extensions for exporting the data contained in this object. Currently include {@code .html} and {@code .csv}.
	 */
	
	@Override
	public Extension[] getSupportedExtensions() {
		return new Extension[] {Extension.HTML, Extension.CSV};
	}
	
	private void printHTML(HeatingCurve hc, FileOutputStream fos) {
		PrintStream stream = new PrintStream(fos);
				
		stream.print("<table>"); 
		stream.print("<tr>"); 
	
		final String TIME_LABEL = Messages.getString("HeatingCurve.6"); 
		final String TEMPERATURE_LABEL = Messages.getString("HeatingCurve.7");

		stream.print("<td>" + TIME_LABEL + "\t</td>");
       	stream.print("<td>" + TEMPERATURE_LABEL + "\t</td>");
       	       	
        stream.print("</tr>");

        stream.println("");
        
        double t;
        double T;

        int size = hc.actualDataPoints();
        int nominalSize = (int)hc.getNumPoints().getValue();        
        int finalSize = size < nominalSize ? size : nominalSize;
        
        for (int i = 0; i < finalSize; i++) {
        	stream.print("<tr>"); 
            
        		stream.print("<td>");
            	t = hc.timeAt(i);
                stream.printf("%.6f %n", t);
                stream.print("\t</td><td>"); 
                T = hc.temperatureAt(i);
                stream.printf("%.6f %n</td>", T); 
                
            stream.println("</tr>");
        }  
        
        stream.print("</table>");
        stream.close();
        
	}
	
	private void printCSV(HeatingCurve hc, FileOutputStream fos) {
		PrintStream stream = new PrintStream(fos);
				
		final String TIME_LABEL = Messages.getString("HeatingCurve.6");
		final String TEMPERATURE_LABEL = hc.getPrefix();
		stream.print(TIME_LABEL + "\t" + TEMPERATURE_LABEL + "\t");
		
		double t;
		double T;

        int size = hc.actualDataPoints();
        int nominalSize = (int)hc.getNumPoints().getValue();        
        int finalSize = size < nominalSize ? size : nominalSize;   
        
        for (int i = 0; i < finalSize; i++) {        	
	        t = hc.timeAt(i);
	        stream.printf("%n%3.4f", t); 
	        T = hc.temperatureAt(i);
	        stream.printf("\t%3.4f", T);        	            
        }
        
        stream.close();
        
	}

	public static HeatingCurveExporter getInstance() {
		return instance;
	}

	@Override
	public Class<HeatingCurve> target() {
		return HeatingCurve.class;
	}	

}