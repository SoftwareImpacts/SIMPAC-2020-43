package pulse.ui;

import static java.awt.EventQueue.invokeLater;
import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.SplashScreen.getSplashScreen;
import static java.lang.Integer.valueOf;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.err;
import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static javax.management.ObjectName.getInstance;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;
import static pulse.ui.frames.TaskControlFrame.getInstance;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.swing.ImageIcon;

/**
 * <p>
 * This is the main class used to launch {@code PULsE} and start the GUI. In
 * addition to providing the launcher methods, it also provides some
 * functionality for accessing the System CPU and memory usage, as well as the
 * number of available threads that can be used in calculation.
 * </p>
 *
 */

public class Launcher {

	private Launcher() {
		// intentionally blank
	}

	/**
	 * Launches the application and creates a GUI.
	 */

	public static void main(String[] args) {
		splashScreen();

		/* Set the Nimbus Look and feel setting code.
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (var info : getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException ex) {
			getLogger(Launcher.class.getName()).log(SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		invokeLater(() -> {
			getInstance().setLocationRelativeTo(null);
			getInstance().setVisible(true);
		});
	}

	private static void splashScreen() {
		var splash = getSplashScreen();
		if (splash == null)
			err.println("SplashScreen.getSplashScreen() returned null");
		else {
			var g = splash.createGraphics();
			requireNonNull(g, "splash.createGraphics() returned null");
		}
	}

	/**
	 * <p>
	 * This will calculate the ratio {@code totalMemory/maxMemory} using the
	 * standard {@code Runtime}. Note this memory usage depends on heap allocation
	 * for the JVM.
	 * </p>
	 * 
	 * @return a value depicting the memory usage
	 */

	public static double getMemoryUsage() {
		double totalMemory = getRuntime().totalMemory();
		double maxMemory = getRuntime().maxMemory();
		return (totalMemory / maxMemory * 100);
	}

	/**
	 * <p>
	 * This will calculate the CPU load for the machine running {@code PULsE}. Note
	 * this is rather code-intensive, so it is recommende to use only at certain
	 * time intervals.
	 * </p>
	 * 
	 * @return a value depicting the CPU usage
	 */

	public static double cpuUsage() {

		var mbs = getPlatformMBeanServer();
		ObjectName name = null;
		try {
			name = getInstance("java.lang:type=OperatingSystem");
		} catch (MalformedObjectNameException | NullPointerException e1) {
			err.println("Error while calculating CPU usage:");
			e1.printStackTrace();
		}

		AttributeList list = null;
		try {
			list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });
		} catch (InstanceNotFoundException | ReflectionException e) {
			err.println("Error while calculating CPU usage:");
			e.printStackTrace();
		}

		if (list.isEmpty())
			return valueOf(null);

		var att = (Attribute) list.get(0);
		var value = (double) att.getValue();

		if (value < 0)
			return valueOf(null);

		return (value * 100);
	}

	/**
	 * Finds the number of threads available for calculation. This will be used by
	 * the {@code TaskManager} when allocating the {@code ForkJoinPool} for running
	 * several tasks in parallel.
	 * 
	 * @return the number of threads, which is greater or equal to the number of
	 *         cores
	 * @see pulse.tasks.TaskManager
	 */

	public static int threadsAvailable() {
		var number = getRuntime().availableProcessors();
		return number > 1 ? (number - 1) : 1;
	}

	public static ImageIcon loadIcon(String path, int iconSize) {
		var imageIcon = new ImageIcon(Launcher.class.getResource("/images/" + path)); // load the image to a
																						// imageIcon
		var image = imageIcon.getImage(); // transform it
		var newimg = image.getScaledInstance(iconSize, iconSize, SCALE_SMOOTH); // scale it the smooth way
		return new ImageIcon(newimg); // transform it back
	}

}