package pulse.ui.components;

import java.awt.Font;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import pulse.properties.NumericProperty;
import pulse.tasks.AbstractResult;
import pulse.tasks.AverageResult;
import pulse.tasks.Identifier;
import pulse.tasks.Result;
import pulse.tasks.ResultFormat;
import pulse.tasks.SearchTask;
import pulse.tasks.TaskManager;
import pulse.tasks.listeners.TaskRepositoryEvent;
import pulse.tasks.listeners.TaskRepositoryListener;
import pulse.tasks.listeners.TaskSelectionEvent;
import pulse.tasks.listeners.TaskSelectionListener;
import pulse.util.Saveable;

public class ResultTable extends JTable implements Saveable  {		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3448187918228094149L;
	private ResultFormat fmt;
	private final static Font font = new Font(Messages.getString("ResultTable.FontName"), Font.PLAIN, 12);  //$NON-NLS-1$
	
	private TableRowSorter<TableModel> sorter;
	private NumericPropertyRenderer renderer;
	
	public ResultTable(ResultFormat fmt) {
		super();
		renderer = new NumericPropertyRenderer();
		this.fmt = fmt;
		
		this.setModel(new ResultTableModel(fmt, 0));
		
		this.setRowHeight(30);
		setShowHorizontalLines(false);
		setFillsViewportHeight(true);
		
		getTableHeader().setFont(font);
		
		sorter = new TableRowSorter<TableModel>(getModel()); 
	    ArrayList<RowSorter.SortKey> list	= new ArrayList<SortKey>();
	    for(int i = 0; i < this.getModel().getColumnCount(); i++) {
		    list.add( new RowSorter.SortKey(i, SortOrder.ASCENDING) );
	    	sorter.setComparator(i, new NumericPropertyComparator());
	    }
	    
	    sorter.setSortKeys(list);

	    this.setRowSorter(sorter);
	    sorter.sort();
	    
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    setRowSelectionAllowed(false);
	    setColumnSelectionAllowed(true);
	    
	    TaskManager.addSelectionListener(new TaskSelectionListener() {

			@Override
			public void onSelectionChanged(TaskSelectionEvent e) {
				
				Identifier id = e.getSelection().getIdentifier();
				getSelectionModel().clearSelection();
				
				List<AbstractResult> results = ((ResultTableModel)getModel()).getResults();
				int jj = 0;
				
				for(AbstractResult r : results) {
					
					if(r instanceof AverageResult) 
						selectMatchingResult( (AverageResult) r, id);
				
					else {
					
						if(! ((Result)r).getIdentifier().equals(id) )
							continue;
						
						jj = convertRowIndexToView(results.indexOf(r));
						
						if(jj < -1)
							break;
						
						getSelectionModel().setSelectionInterval(jj, jj);
						scrollToSelection(jj);	
						return;
					
					}
					
				}
								
			}
	    	
	    });
	    
		TaskManager.addTaskRepositoryListener(new TaskRepositoryListener() {

			@Override
			public void onTaskListChanged(TaskRepositoryEvent e) {
					
				//task added
				
				switch(e.getState()) {
				
					case TASK_FINISHED :
					
						SearchTask t = TaskManager.getTask(e.getId());
						Result r = TaskManager.getResult(t);
						
						SwingUtilities.invokeLater( new Runnable() {
	
							@Override
							public void run() {
								((ResultTableModel)getModel()).addRow( r );
								List<AbstractResult> results = ((ResultTableModel)getModel()).getResults();
								scrollToSelection(
										convertColumnIndexToView(results.indexOf(r)) 
										);
							}
								
						});
						
					break;
					case TASK_REMOVED :
					case TASK_RESET :
						((ResultTableModel)getModel()).removeRow( e.getId() );
						getSelectionModel().clearSelection();						
					break;				
				}				
				
				}

			
		});
	    
	}
	
	private void selectMatchingResult(AverageResult ar, Identifier id) {
		List<AbstractResult> ir = ar.getIndividualResults();
		List<AbstractResult> results = ( (ResultTableModel)this.getModel() ).getResults();
		Result rr;
		int jj;
		
		for(AbstractResult aar : ir){
			if(aar instanceof AverageResult) {
				selectMatchingResult((AverageResult)aar, id);
				return;
			}
			
			rr = (Result)aar;
			
			if(! (rr.getIdentifier()).equals(id) )
				continue;
			
			AbstractResult selection = ar.getParent() != null ? ar.getParent() : ar;
			jj = convertRowIndexToView( results.indexOf( selection) ); 
		
			getSelectionModel().setSelectionInterval(jj, jj);
			scrollToSelection(jj);	
			return;
			
		}
	}
	
	public double[][][] data() {
		double[][][] data = new double[getColumnCount()][2][getRowCount()];
		NumericProperty property = null;
		
		for(int i = 0; i < data.length; i++) 
			for(int j = 0; j < data[0][0].length; j++) {
				property = (NumericProperty)getValueAt(j, i);
				data[i][0][j] = (double) property.getValue() * (double) property.getDimensionFactor();
				if(property.getError() != null)
					data[i][1][j] = (double) property.getError() * (double) property.getDimensionFactor();
				else
					data[i][1][j] = 0;
			}	
		return data;
	}
	
	public String[] getColumnNames() {
		String[] names = new String[getColumnCount()];
		for(int i = 0; i < getColumnCount(); i++)
			names[i] = getColumnName(i);
		return names;
	}
	
	private void scrollToSelection(int rowIndex) {
		scrollRectToVisible(getCellRect(rowIndex,0, true));
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		   Object value = getValueAt(row, column);
		   
		   if(value != null) 			   			   			   
			   if(value instanceof NumericProperty)
				   return renderer;	   
		   
		   return super.getCellRenderer(row, column);
		   
	}

	public ResultFormat getFormat() {
		return fmt;
	}

	public void setFmt(ResultFormat fmt) {
		this.fmt = fmt;
	}
	
	@Override
	public void printData(FileOutputStream fos) {
		PrintStream stream = new PrintStream(fos);
		
		stream.print("<table>"); //$NON-NLS-1$
		stream.print("<tr>"); //$NON-NLS-1$
		
        for (int col = 0; col < getColumnCount(); col++) {
        	stream.print("<td>"); //$NON-NLS-1$
            stream.print(getColumnName(col) + "\t"); //$NON-NLS-1$
            stream.print("</td>"); //$NON-NLS-1$
        }
        
        stream.print("</tr>"); //$NON-NLS-1$

        stream.println(""); //$NON-NLS-1$

        NumericProperty tmp;

        for (int i = 0; i < getRowCount(); i++) {
        	stream.print("<tr>"); //$NON-NLS-1$
            for (int j = 0; j < getColumnCount(); j++) {
            	stream.print("<td>"); //$NON-NLS-1$
            	tmp = (NumericProperty) getValueAt(i,j);
                stream.print(tmp.formattedValue());
                stream.print("</td>"); //$NON-NLS-1$
            }
            stream.println("</tr>"); //$NON-NLS-1$
        }
        
        stream.print("</table>"); //$NON-NLS-1$
        
        List<AbstractResult> results = ( (ResultTableModel)getModel() ).getResults();
        
        boolean printMore = false;
        
        for(AbstractResult ar : results) {
        	if(ar instanceof AverageResult)
        		printMore = true;
        }
        
        if(! printMore) {
        	stream.close();
        	return;
        }
        
        stream.print(Messages.getString("ResultTable.IndividualResults")); //$NON-NLS-1$
        stream.print("<table>"); //$NON-NLS-1$
        stream.print("<tr>"); //$NON-NLS-1$
        
        for (int col = 0; col < getColumnCount(); col++) {
        	stream.print("<td>"); //$NON-NLS-1$
            stream.print(getColumnName(col) + "\t"); //$NON-NLS-1$
            stream.print("</td>"); //$NON-NLS-1$
        }
        
        stream.print("</tr>"); //$NON-NLS-1$

        stream.println(""); //$NON-NLS-1$

        List<AbstractResult> ir;
        AverageResult ar;
        Result rr;
        NumericProperty[] props;
        
        for(AbstractResult r : results) {
        	if(r instanceof AverageResult) {
        		ar = (AverageResult) r;
        		ir = ar.getIndividualResults();
        		
        		for(AbstractResult aar : ir) {		
        			stream.print("<tr>"); //$NON-NLS-1$
        			rr = (Result)aar;
        			props = rr.properties();
        			
        			for (int j = 0; j < getColumnCount(); j++) {
                    	stream.print("<td>"); //$NON-NLS-1$
                    	tmp = (NumericProperty) props[j];
                        stream.print(tmp.formattedValue());
                        stream.print("</td>"); //$NON-NLS-1$
                    }
        			
        			stream.print("</tr>"); //$NON-NLS-1$
        		}
        		
        	}
        }
                
        stream.print("</table>"); //$NON-NLS-1$
        
        
        stream.close();
	}
	
	public void autoAverage(double precision) {
		
		double temp = 0;
		int[] g, gModel;
		
		List<AbstractResult> newRows 			= new LinkedList<AbstractResult>();
		List<AbstractResult> oldRows			= new LinkedList<AbstractResult>();
		List<AbstractResult> toAverage			= new ArrayList<AbstractResult>();
		
		List<Integer> skipList					= new LinkedList<Integer>();
		
		ResultTableModel model = (ResultTableModel) this.getModel();
		
		for(int i = 0; i < this.getRowCount(); i++) {
			if(skipList.contains(i))
				continue;
				
			temp	= (double) ( (NumericProperty) this.getValueAt(i, 0) ).getValue();
			g		= group(temp, precision); 
			gModel	= new int[g.length];
			
			toAverage.clear();
			
			for(int j = 0; j < g.length; j++) {
				gModel[j] = this.convertRowIndexToModel(g[j]);
				toAverage.add( model.getResults().get( gModel[j] ) );
			}
				
			if(g.length < 2) {
				oldRows.add( model.getResults().get( gModel[0] ) );
				continue;
			}
			
			newRows.add( new AverageResult(toAverage, model.getFormat() ) );
			
			for(int j = 0; j < g.length; j++) 
				skipList.add(g[j]);
			
			
		}
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				model.setRowCount(0);
				model.getResults().clear();
				
				for(AbstractResult row : oldRows) 
					model.addRow( row );
				
				for(AbstractResult row : newRows) 
					model.addRow( row );
			
				scrollToSelection(getModel().getRowCount() - 1);	
				
			}
			
		});
		
	}
	
	public int[] group(double val, double precision) {
		
		List<Integer> selection = new LinkedList<Integer>();
		
		for(int i = 0; i < this.getRowCount(); i++) {
			
			if( Math.abs( 
					(double) ((NumericProperty) this.getValueAt(i, 0)).getValue()
					- val) < precision)
				selection.add(i);
		}
		
		int[] result = new int[selection.size()];
	
		for(int i = 0; i < result.length; i++)
			result[i] = selection.get(i);
		
		return result;
		
	}
	
	public class ResultTableModel extends DefaultTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -6227212091613629835L;
		private ResultFormat fmt;
		private List<AbstractResult> results;
		
		public ResultTableModel(ResultFormat fmt, int rowCount) {
			super(fmt.labels(), rowCount);
			this.fmt = fmt;
			results = new LinkedList<AbstractResult>();
		}		
		
	    @Override
	    public boolean isCellEditable(int row, int column) {
	       //all cells false
	       return false;
	    }
	    
	    public void changeFormat(ResultFormat fmt) {
	    	this.fmt = fmt;
	    	
	    	for(AbstractResult r : results)
	    		r.setFormat(fmt);
	    	
	    	if(this.getRowCount() < 1) {
	    		this.setColumnIdentifiers(fmt.labels());
	    		return;
	    	}
	    	
	    	this.setRowCount(0);
	    	List<AbstractResult> oldResults = new ArrayList<AbstractResult>(results);
    		
	    	this.setColumnIdentifiers(fmt.labels());
	    	results.clear();
	    	
	    	for(AbstractResult r : oldResults)
	    		this.addRow(r);
	    	
	    }
	    
		public void addRow(AbstractResult result) {
			if(result == null)
				return; 
			
			NumericProperty[] propertyList = result.properties();
			
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					addRow(propertyList);
					results.add(result);
				    scrollRectToVisible(getCellRect(getRowCount(), getColumnCount(), true));	    	    
				}
				
			});
	    
		}
		
		public void removeRow(Identifier id) {
			Result res = null;
			for(AbstractResult r : results) {
				if(! (r instanceof Result) )
					continue;
				if( ((Result)r).getIdentifier().equals(id)) {
					res = (Result)r;
					break;
				}
			}
			
			if(res == null)
				return;
			
			this.removeRow(results.indexOf(res));
		}
		
		@Override
		public void removeRow(int index) {
			AbstractResult r = results.get(index);
			super.removeRow(index);
			results.remove(r);
		}
		
		public List<AbstractResult> getResults() {
			return results;
		}
		
		public ResultFormat getFormat() {
			return fmt;
		}				
		
	}
	
}