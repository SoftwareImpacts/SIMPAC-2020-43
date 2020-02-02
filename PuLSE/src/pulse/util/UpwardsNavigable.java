package pulse.util;

import java.util.ArrayList;
import java.util.List;

import pulse.tasks.Identifier;
import pulse.tasks.SearchTask;

/**
 * <p>An {@code UpwardsNavigable} provides a two-way connection with the other {@code Describable} in an 
 * asymmetric (upwards-oriented) manner (hence its name). The {@code UpwardsNavigable} stores information about its parent,
 * which stands higher in hierarchy than this object. The {@code parent} is always informed if any changes happen 
 * with its child properties.</p>
 *
 */

public abstract class UpwardsNavigable implements Describable {
	
	private UpwardsNavigable parent;
	private List<HierarchyListener> listeners = new ArrayList<HierarchyListener>();
	
	public void removeHierarchyListeners() {
		this.listeners.clear();
	}
	
	public void addHierarchyListener(HierarchyListener l) {
		this.listeners.add(l);
	}
	
	public List<HierarchyListener> getHierarchyListeners() {
		return listeners;
	}
	
	/**
	 * Recursively informs the parent, the parent of its parent, etc. of this {@code UpwardsNavigable}
	 * that an action has been taken on its child's properties specified by {@e}. 
	 * @param e the property event
	 */
	
	public void tellParent(PropertyEvent e) {
		if(parent == null)
			return;
		
		parent.listeners.forEach(l -> l.onChildPropertyChanged(e));
		parent.tellParent(e);		
	}

	/**
	 * Return the parent of this {@code UpwardsNavigable} -- if is has been previously explicitly set.
	 * @return the parent (which is also an {@code UpwardsNavigable}).
	 */

	public UpwardsNavigable getParent() {
		return parent;
	}
	
	/**
	 * Finds an ancestor that looks similar to {@code aClass} by recursively calling {@code getParent()}.
	 * @param aClass a class which should be similar to an ancestor of this {@code UpwardsNavigable}
	 * @return the ancestor, which is a parent, or grand-parent, or grand-grand-parent, etc. of this {@code UpwardsNavigable}. 
	 */
	
	public UpwardsNavigable specificAncestor(Class<? extends UpwardsNavigable> aClass) {
		UpwardsNavigable aParent = null;
		for(UpwardsNavigable navigable = this; navigable != null ; navigable = navigable.getParent()) {
			aParent = navigable.getParent();
			if(aParent != null)
				if(aParent.getClass().equals(aClass))
					return aParent;
		}
		return null;
	}
	
	/**
	 * Explicitly sets the parent of this {@code UpwardsNavigable}. 
	 * @param parent the new parent that will adopt this {@code UpwardsNavigable}.
	 */

	public void setParent(UpwardsNavigable parent) {
		this.parent = parent;
	}
	
	public Identifier identify() {
		UpwardsNavigable un = specificAncestor(SearchTask.class);
		if(un != null)
			return ((SearchTask)un).getIdentifier();
		return null;
	}
	
	@Override
	public String describe() {
		Identifier id = identify();
		String name = getClass().getSimpleName(); 
		if(id == null)
			return name;
		
		return name + "_" + id.getValue();
	}
	
}