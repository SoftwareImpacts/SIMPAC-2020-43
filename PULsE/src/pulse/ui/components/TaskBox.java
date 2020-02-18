package pulse.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import pulse.tasks.Identifier;
import pulse.tasks.SearchTask;
import pulse.tasks.TaskManager;
import pulse.tasks.listeners.TaskSelectionEvent;
import pulse.tasks.listeners.TaskSelectionListener;
import pulse.ui.Messages;
import pulse.ui.components.models.TaskBoxModel;

@SuppressWarnings("serial")
public class TaskBox extends JComboBox<SearchTask> {

	private final static int FONT_SIZE = 12;

	public TaskBox() {
		super();
		
		init();
		this.setModel(new TaskBoxModel());
		
		TaskBox reference 	= this;
		
		addItemListener(new ItemListener() {

			@Override
		    public void itemStateChanged(ItemEvent event) {
				
		        if (event.getStateChange() == ItemEvent.SELECTED) {
		        	Identifier id = ((SearchTask)((TaskBoxModel)reference.getModel()).getSelectedItem()).getIdentifier();
					
		        	/*
		        	 * if task already selected, just ignore this event and return
		        	 */
		        	
		        	if( (TaskManager.getSelectedTask() == TaskManager.getTask(id)) )
		        		return;
		        	
		        	TaskManager.selectTask(id, reference);
		        	
		        }
			}
			
		});
		
		TaskManager.addSelectionListener(new TaskSelectionListener() {

			@Override
			public void onSelectionChanged(TaskSelectionEvent e) {
				//simply ignore if source of event is taskBox
				if(e.getSource() == reference)
					return;
				
				getModel().setSelectedItem( e.getSelection() );
				
				
			}
						
		});
	}
	
	public void init() {
		setMaximumSize(new Dimension(32767, 24));
		setFont(getFont().deriveFont(FONT_SIZE));
		setMinimumSize(new Dimension(250, 20));		
		setToolTipText(Messages.getString("TaskBox.DefaultText")); //$NON-NLS-1$
		setBackground(Color.WHITE);
	}
	
}