package pulse.ui.components;

import static java.awt.Font.PLAIN;
import static java.lang.System.err;
import static java.lang.System.lineSeparator;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.getWindowAncestor;
import static pulse.tasks.listeners.TaskRepositoryEvent.State.TASK_FINISHED;
import static pulse.tasks.logs.Details.MISSING_HEATING_CURVE;
import static pulse.tasks.logs.Details.NONE;
import static pulse.tasks.logs.Status.DONE;
import static pulse.tasks.logs.Status.READY;
import static pulse.tasks.processing.ResultFormat.getInstance;
import static pulse.ui.Launcher.loadIcon;
import static pulse.ui.Messages.getString;
import static pulse.ui.frames.MainGraphFrame.getChart;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import pulse.problem.schemes.solvers.Solver;
import pulse.problem.schemes.solvers.SolverException;
import pulse.tasks.TaskManager;
import pulse.tasks.listeners.TaskRepositoryEvent;
import pulse.tasks.processing.Result;

@SuppressWarnings("serial")
public class TaskPopupMenu extends JPopupMenu {

	private final static Font f = new Font(getString("TaskTable.FontName"), PLAIN, 16); //$NON-NLS-1$

	private final static int ICON_SIZE = 24;

	private static ImageIcon ICON_GRAPH = loadIcon("graph.png", ICON_SIZE);
	private static ImageIcon ICON_METADATA = loadIcon("metadata.png", ICON_SIZE);
	private static ImageIcon ICON_MISSING = loadIcon("missing.png", ICON_SIZE);
	private static ImageIcon ICON_RUN = loadIcon("execute_single.png", ICON_SIZE);
	private static ImageIcon ICON_RESET = loadIcon("reset.png", ICON_SIZE);
	private static ImageIcon ICON_RESULT = loadIcon("result.png", ICON_SIZE);

	public TaskPopupMenu() {
		var referenceWindow = getWindowAncestor(this);

		var itemChart = new JMenuItem(getString("TaskTablePopupMenu.ShowHeatingCurve"), ICON_GRAPH); //$NON-NLS-1$
		itemChart.addActionListener(e -> plot(false));
		itemChart.setFont(f);

		var itemExtendedChart = new JMenuItem(getString("TaskTablePopupMenu.ShowExtendedHeatingCurve"), //$NON-NLS-1$
				ICON_GRAPH);
		itemExtendedChart.addActionListener(e -> plot(true));
		itemExtendedChart.setFont(f);

		var instance = TaskManager.getManagerInstance();

		var itemShowMeta = new JMenuItem("Show metadata", ICON_METADATA);
		itemShowMeta.addActionListener((ActionEvent e) -> {
			var t = instance.getSelectedTask();
			if (t == null) {
				showMessageDialog(getWindowAncestor((Component) e.getSource()),
						getString("TaskTablePopupMenu.EmptySelection2"), //$NON-NLS-1$
						getString("TaskTablePopupMenu.11"), ERROR_MESSAGE); //$NON-NLS-1$
			} else
				showMessageDialog(getWindowAncestor((Component) e.getSource()),
						t.getExperimentalCurve().getMetadata().toString(), "Metadata", PLAIN_MESSAGE);
		});

		itemShowMeta.setFont(f);

		var itemShowStatus = new JMenuItem("What is missing?", ICON_MISSING);

		instance.addSelectionListener(event -> {
			var details = instance.getSelectedTask().checkProblems(false).getDetails();
			if ((details == null) || (details == NONE))
				itemShowStatus.setEnabled(false);
			else
				itemShowStatus.setEnabled(true);
		});

		itemShowStatus.addActionListener((ActionEvent e) -> {
			var t = instance.getSelectedTask();
			if (t != null) {
				var d = t.getStatus().getDetails();
				showMessageDialog(getWindowAncestor((Component) e.getSource()),
						"<html>This is due to " + d.toString() + "</html>", "Problems with " + t, INFORMATION_MESSAGE);
			}
		});

		itemShowStatus.setFont(f);

		var itemExecute = new JMenuItem(getString("TaskTablePopupMenu.Execute"), ICON_RUN); //$NON-NLS-1$
		itemExecute.addActionListener((ActionEvent e) -> {
			var t = instance.getSelectedTask();
			if (t == null) {
				showMessageDialog(getWindowAncestor((Component) e.getSource()),
						getString("TaskTablePopupMenu.EmptySelection"), //$NON-NLS-1$
						getString("TaskTablePopupMenu.ErrorTitle"), ERROR_MESSAGE); //$NON-NLS-1$
			} else if (t.checkProblems() == DONE) {
				var dialogButton = YES_NO_OPTION;
				var dialogResult = showConfirmDialog(referenceWindow,
						getString("TaskTablePopupMenu.TaskCompletedWarning") + lineSeparator()
								+ getString("TaskTablePopupMenu.AskToDelete"),
						getString("TaskTablePopupMenu.DeleteTitle"), dialogButton);
				if (dialogResult == 0) {
					instance.removeResult(t);
					// t.storeCurrentSolution();
					instance.execute(instance.getSelectedTask());
				}
			} else if (t.checkProblems() != READY) {
				showMessageDialog(getWindowAncestor((Component) e.getSource()),
						t.toString() + " is " + t.getStatus().getMessage(), //$NON-NLS-1$
						getString("TaskTablePopupMenu.TaskNotReady"), //$NON-NLS-1$
						ERROR_MESSAGE);
			} else
				instance.execute(instance.getSelectedTask());
		});

		itemExecute.setFont(f);

		var itemReset = new JMenuItem(getString("TaskTablePopupMenu.Reset"), ICON_RESET);
		itemReset.setFont(f);

		itemReset.addActionListener((ActionEvent arg0) -> instance.getSelectedTask().clear());

		var itemGenerateResult = new JMenuItem(getString("TaskTablePopupMenu.GenerateResult"), ICON_RESULT);
		itemGenerateResult.setFont(f);

		itemGenerateResult.addActionListener((ActionEvent arg0) -> {
			var t = instance.getSelectedTask();
			if (t == null)
				return;
			if (t.getProblem() != null) {
				var r = new Result(t, getInstance());
				instance.useResult(t, r);
				var e = new TaskRepositoryEvent(TASK_FINISHED, t.getIdentifier());
				instance.notifyListeners(e);
			}
		});

		add(itemShowMeta);
		add(itemShowStatus);
		add(new JSeparator());
		add(itemChart);
		add(itemExtendedChart);
		add(new JSeparator());
		add(itemReset);
		add(itemGenerateResult);
		add(new JSeparator());
		add(itemExecute);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void plot(boolean extended) {
		var t = TaskManager.getManagerInstance().getSelectedTask();

		if (t == null) {
			showMessageDialog(getWindowAncestor(this), getString("TaskTablePopupMenu.EmptySelection2"), //$NON-NLS-1$
					getString("TaskTablePopupMenu.11"), ERROR_MESSAGE); //$NON-NLS-1$
		} else {

			var statusDetails = t.getStatus().getDetails();

			if (statusDetails == MISSING_HEATING_CURVE) {

				showMessageDialog(getWindowAncestor(this), getString("TaskTablePopupMenu.12"), //$NON-NLS-1$
						getString("TaskTablePopupMenu.13"), //$NON-NLS-1$
						ERROR_MESSAGE);

			} else {

				var scheme = (Solver) t.getScheme();
				if (scheme != null) {
					try {
						scheme.solve(t.getProblem());
					} catch (SolverException e) {
						err.println("Solver error for " + t + "Details: ");
						e.printStackTrace();
					}
				}

				getChart().plot(t, extended);

			}

		}

	}

}