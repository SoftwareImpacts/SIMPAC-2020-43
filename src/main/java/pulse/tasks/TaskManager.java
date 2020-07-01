package pulse.tasks;

import static java.lang.System.gc;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_WEEK_DATE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static pulse.tasks.Status.DONE;
import static pulse.tasks.Status.IN_PROGRESS;
import static pulse.tasks.Status.QUEUED;
import static pulse.tasks.Status.READY;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.SHUTDOWN;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.TASK_ADDED;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.TASK_FINISHED;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.TASK_REMOVED;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.TASK_RESET;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.TASK_SUBMITTED;
import static pulse.ui.Launcher.threadsAvailable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import pulse.input.ExperimentalData;
import pulse.io.readers.ReaderManager;
import pulse.search.direction.PathOptimiser;
import pulse.tasks.listeners.TaskRepositoryEvent;
import pulse.tasks.listeners.TaskRepositoryListener;
import pulse.tasks.listeners.TaskSelectionEvent;
import pulse.tasks.listeners.TaskSelectionListener;
import pulse.util.UpwardsNavigable;

/**
 * <p>
 * The {@code TaskManager} is a high-level class for dealing with operations of
 * creation, removal, storage, and execution of {@code SearchTask}s, as well as
 * with the associated {@code Result}s and {@code InterpolationDataset}s. Note
 * that {@code TaskManager} adopts a {@code PathSolver}.
 * </p>
 *
 */

public class TaskManager extends UpwardsNavigable {

	private static TaskManager instance = new TaskManager();

	private static List<SearchTask> tasks = new LinkedList<SearchTask>();
	private static SearchTask selectedTask;
	private static Map<SearchTask, Result> results = new HashMap<SearchTask, Result>();

	private static final int THREADS_AVAILABLE = threadsAvailable();
	private static ForkJoinPool taskPool = new ForkJoinPool(THREADS_AVAILABLE - 1);

	private static List<TaskSelectionListener> selectionListeners = new CopyOnWriteArrayList<TaskSelectionListener>();
	private static List<TaskRepositoryListener> taskRepositoryListeners = new CopyOnWriteArrayList<TaskRepositoryListener>();

	private static final String DEFAULT_NAME = "Project 1 - "
			+ now().format(ISO_WEEK_DATE);

	private TaskManager() {

	}

	/**
	 * This class uses a singleton pattern, meaning there is only instance of this
	 * class.
	 * 
	 * @return the single (static) instance of this class
	 */

	public static TaskManager getInstance() {
		return instance;
	}

	/**
	 * <t>Executes {@code t} asynchronously using a {@code CompletableFuture}. When
	 * done, creates a {@code Result} and puts it into the
	 * {@code Map(SearchTask,Result)} in this {@code TaskManager}.</t>
	 * 
	 * @param t a {@code SearchTask} that will be executed
	 */

	public static void execute(SearchTask t) {
		removeResult(t); // remove old result
		t.setStatus(QUEUED); // notify listeners computation is about to start

		// notify listeners
		notifyListeners(new TaskRepositoryEvent(TASK_SUBMITTED, t.getIdentifier()));

		// run task t -- after task completed, write result and trigger listeners

		CompletableFuture.runAsync(t).thenRun(() -> {
            if (t.getStatus() == DONE) {
                results.put(t, new Result(t, ResultFormat.getInstance()));
            }
            var e = new TaskRepositoryEvent(TASK_FINISHED, t.getIdentifier());
            notifyListeners(e);
        });
		
	}

	/**
	 * Notifies the {@code TaskRepositoryListener}s of the {@code e}
	 * 
	 * @param e an event
	 */

	public static void notifyListeners(TaskRepositoryEvent e) {
		taskRepositoryListeners.stream().forEach(l -> l.onTaskListChanged(e));
	}

	/**
	 * <p>
	 * Creates a queue of {@code SearchTask}s based on their readiness and feeds
	 * that queue to a {@code ForkJoinPool} using a parallel stream. The size of the
	 * pool is usually limited by hardware, e.g. for a 4 core system with 2
	 * independent threads on each core, the limitation will be <math>4*2 - 1 =
	 * 7</math>, etc.
	 */

	public static void executeAll() {

		var queue = tasks.stream().filter(t -> {
			switch (t.getStatus()) {
			case DONE:
			case IN_PROGRESS:
			case EXECUTION_ERROR:
				return false;
			default:
				return true;
			}
		}).collect(toList());

		try {
			taskPool.submit(() -> queue.parallelStream().forEach(t -> execute(t))).get();
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Execution exception while running multiple tasks");
			e.printStackTrace();
		}

		gc();

	}

	/**
	 * Checks if any of the tasks that this {@code TaskManager} manages is either
	 * {@code QUEUED} or {@code IN_PROGRESS}.
	 * 
	 * @return {@code false} if the status of the {@code SearchTask} is any of the
	 *         above; {@code false} otherwise.
	 */

	public static boolean isTaskQueueEmpty() {
		return !tasks.stream().anyMatch(t -> t.getStatus() == QUEUED || t.getStatus() == IN_PROGRESS);
	}

	/**
	 * This will terminate all tasks in this {@code TaskManager} and trigger a
	 * {@code SHUTDOWN} {@code TaskRepositoryEvent}.
	 * 
	 * @see pulse.tasks.Task.terminate()
	 */

	public static void cancelAllTasks() {

		tasks.stream().forEach(t -> t.terminate());

		var e = new TaskRepositoryEvent(SHUTDOWN, null);

		notifyListeners(e);

	}

	/**
	 * Checks whether the acquisition time recorded by the experimental setup has
	 * been chosen appropriately.
	 * 
	 * @return {@code false} if the acquisition time seems sensible for the
	 *         {@code ExperimentalData} in each of the tasks; {@code true}
	 *         otherwise.
	 * @see pulse.input.ExperimentalData.isAcquisitionTimeSensible()
	 */

	public static boolean dataNeedsTruncation() {

		return tasks.stream().anyMatch(t ->

		!t.getExperimentalCurve().isAcquisitionTimeSensible()

		);

	}

	/**
	 * Calls {@code truncate()} on {@code ExperimentalData} for each
	 * {@code SearchTask}.
	 * 
	 * @see pulse.input.ExperimentalData.truncate()
	 */

	public static void truncateData() {
		tasks.stream().forEach(t -> t.getExperimentalCurve().truncate());
	}

	/**
	 * <p>
	 * Selects the first non-{@code null} task that is within the reach of this
	 * {@code TaskManager}. If all tasks are null, will do nothing.
	 * </p>
	 */

	public static void selectFirstTask() {
		if (tasks.size() > 0) {
			var task = tasks.get(0);
			if (task != null && selectedTask != task) {
				selectedTask = task;
				fireTaskSelected(getInstance());
			}
		}
	}

	private static void fireTaskSelected(Object source) {
		var e = new TaskSelectionEvent(source);
		for (var l : selectionListeners) {
                    l.onSelectionChanged(e);
                }
	}

	/**
	 * <p>
	 * Purges all tasks from this {@code TaskManager}. Generates a
	 * {@code TASK_REMOVED} {@code TaskRepositoryEvent} for each of the removed
	 * tasks. Clears task selection.
	 * </p>
	 */

	public static void clear() {
		tasks.stream().sorted((t1, t2) -> -t1.getIdentifier().compareTo(t2.getIdentifier())).forEach(task -> {
			var e = new TaskRepositoryEvent(TASK_REMOVED,
					task.getIdentifier());

			notifyListeners(e);
		});

		tasks.clear();
		selectTask(null, null);
	}

	/**
	 * Uses the first non-{@code null} {@code SearchTask} to retrieve the sample
	 * name from the {@code Metadata} associated with its {@code ExperimentalData}.
	 * 
	 * @return a {@code String} with the sample name, or {@code null} if no suitable
	 *         task can be found.
	 */

	public static String getSampleName() {
		if (tasks.size() < 1)
			return null;

		var optional = tasks.stream().filter(t -> t != null).findFirst();

		if (!optional.isPresent())
			return null;

		return optional.get().getExperimentalCurve().getMetadata().getSampleName();
	}

	/**
	 * <p>
	 * Clears any progress for all the tasks and resets everything. Triggers a
	 * {@code TASK_RESET} event.
	 * </p>
	 */

	public static void reset() {
		if (tasks.size() < 1)
			return;

		for (var task : tasks) {
			var e = new TaskRepositoryEvent(TASK_RESET, task.getIdentifier());

			task.clear();

			notifyListeners(e);
		}

		PathOptimiser.reset();

	}

	/**
	 * Finds a {@code SearchTask} whose {@code Identifier} matches {@code id}.
	 * 
	 * @param id the {@code Identifier} of the task.
	 * @return the {@code SearchTask} associated with this {@code Identifier}.
	 */

	public static SearchTask getTask(Identifier id) {
		return tasks.stream().filter(t -> t.getIdentifier().equals(id)).findFirst().get();
	}

	/**
	 * <p>
	 * Generates a {@code SearchTask} assuming that the {@code ExperimentalData} is
	 * stored in the {@code file}. This will make the {@code ReaderManager} attempt
	 * to read that {@code file}. If successful, invokes {@code addTask(...)} on the
	 * created {@code SearchTask}.
	 * </p>
	 * 
	 * @param file
	 * @see addTask(SearchTask)
	 * @see pulse.io.readers.ReaderManager.extract(File)
	 */

	public static void generateTask(File file) {
		List<ExperimentalData> curves = null;
		try {
			curves = ReaderManager.extract(file);
		} catch (IOException e) {
			System.err.println("Error loading experimental data");
			e.printStackTrace();
		}
		curves.stream().forEach(curve -> addTask(new SearchTask(curve)));
		selectFirstTask();
	}

	/**
	 * Generates multiple tasks from multiple {@code files}.
	 * 
	 * @param files a list of {@code File}s that can be parsed down to
	 *              {@code ExperimentalData}.
	 */

	public static void generateTasks(List<File> files) {
		requireNonNull(files, "Null list of files passed to generatesTasks(...)");

		if (files.size() == 1)
			generateTask(files.get(0));
		else {
			var pool = Executors.newSingleThreadExecutor();
			files.stream().forEach(f -> pool.submit(() -> generateTask(f)));
		}
	}

	/**
	 * <p>
	 * If a task {@code equal} to {@code t} has already been previously loaded, does
	 * nothing. Otherwise, adds this {@code t} to the task repository and triggers a
	 * {@code TASK_ADDED} event.
	 * </p>
	 * 
	 * @param t the {@code SearchTask} that needs to be added to the internal
	 *          repository
	 * @return {@code null} if a task like {@code t} has already been added
	 *         previously, {@code t} otherwise.
	 * @see pulse.tasks.SearchTask.equals(SearchTask)
	 */

	public static SearchTask addTask(SearchTask t) {

		if (tasks.stream().filter(task -> task.equals(t)).count() > 0)
			return null;

		tasks.add(t);

		var e = new TaskRepositoryEvent(TASK_ADDED, t.getIdentifier());
		t.setParent(getInstance());
		notifyListeners(e);

		return t;
	}

	/**
	 * If {@code t} is found in the local repository, removes it and triggers a
	 * {@code TASK_REMOVED} event.
	 * 
	 * @param t a {@code SearchTask} that has been previously loaded to this
	 *          repository.
	 * @return {@code true} if the operation is successful, {@code false} otherwise.
	 */

	public static boolean removeTask(SearchTask t) {
		if (tasks.stream().filter(task -> task.equals(t)).count() < 1)
			return false;

		tasks.remove(t);

		var e = new TaskRepositoryEvent(TASK_REMOVED, t.getIdentifier());

		notifyListeners(e);
		selectedTask = null;

		return true;
	}

	/**
	 * Gets the current number of tasks in the repository.
	 * 
	 * @return the number of available tasks.
	 */

	public static int numberOfTasks() {
		return tasks.size();
	}

	/**
	 * <p>
	 * Selects a {@code SearchTask} within this repository with the specified
	 * {@code id} (if present). Informs the listeners this selection has been
	 * triggered by {@code src}.
	 * </p>
	 * 
	 * @param id  the {@code Identifier} of a task within this repository.
	 * @param src the source of the selection.
	 */

	public static void selectTask(Identifier id, Object src) {
		tasks.stream().filter(t -> t.getIdentifier().equals(id)).filter(t -> t != selectedTask).findAny()
				.ifPresent(t -> {
					selectedTask = t;
					fireTaskSelected(src);
				});

	}

	public static void addSelectionListener(TaskSelectionListener listener) {
		selectionListeners.add(listener);
	}

	public static void addTaskRepositoryListener(TaskRepositoryListener listener) {
		taskRepositoryListeners.add(listener);
	}

	public static TaskSelectionListener[] getSelectionListeners() {
		return (TaskSelectionListener[]) selectionListeners.toArray();
	}

	public static void removeSelectionListeners() {
		selectionListeners.clear();
	}

	public static void removeTaskRepositoryListener(TaskRepositoryListener trl) {
		taskRepositoryListeners.remove(trl);
	}

	public static int indexOfTask(SearchTask t) {
		return tasks.indexOf(t);
	}

	public static List<SearchTask> getTaskList() {
		return tasks;
	}

	public static SearchTask getSelectedTask() {
		return selectedTask;
	}

	public static List<TaskRepositoryListener> getTaskRepositoryListeners() {
		return taskRepositoryListeners;
	}

	/**
	 * This {@code TaskManager} will be described by the sample name for the
	 * experiment.
	 */

	@Override
	public String describe() {
		return tasks.size() > 0 ? getSampleName() : DEFAULT_NAME;
	}

	public static Result getResult(SearchTask t) {
		return results.get(t);
	}

	/**
	 * Assigns {@code r} as the {@code Result} for {@code t}.
	 * 
	 * @param t the {@code Result}
	 * @param r the {@code SearchTask}.
	 */

	public static void useResult(SearchTask t, Result r) {
		results.put(t, r);
	}

	/**
	 * Searches for a {@code Result} for a {@code SearchTask} with a specific
	 * {@code id}.
	 * 
	 * @param id the {@code Identifier} of a {@code SearchTask}
	 * @return {@code null} if such {@code Result} cannot be found. Otherwise,
	 *         returns the found {@code Result}.
	 */

	public static Result getResult(Identifier id) {
		var optional = tasks.stream().filter(t -> t.getIdentifier().equals(id)).findFirst();

		if (!optional.isPresent())
			return null;

		return results.get(optional.get());
	}

	/**
	 * Removes the results of the task {@code t} and sets its status to
	 * {@code READY}.
	 * 
	 * @param t a {@code SearchTask} contained in the repository
	 */

	public static void removeResult(SearchTask t) {
		if (!results.containsKey(t))
			return;
		results.remove(t);
		t.setStatus(READY);
	}

	public static void evaluate() {
		tasks.stream().forEach(t -> t.calculateThermalProperties());
	}

}