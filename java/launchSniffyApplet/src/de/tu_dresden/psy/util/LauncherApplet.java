package de.tu_dresden.psy.util;

import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;


import javax.swing.JFrame;

public class LauncherApplet extends Applet {

	
	private static final long serialVersionUID = -2572712616439336285L;

	
	
	
	private boolean launched;
	static final String sniffyPath = "Z:\\win32\\Sniffy\\Sniffy.exe";

	/**
	 * applet main entry point
	 */

	public LauncherApplet() {
		launched = false;

		// JButton btnSniffy = new JButton("Sniffy");
		// btnSniffy.setAction(launchSniffy);
		// add(btnSniffy);

	}

	/**
	 * launch applet in a frame
	 * 
	 * @param args
	 */

	public static void main(String[] args) {
		LauncherApplet applet = new LauncherApplet();

		JFrame frame = new JFrame("LauncherApplet frame");
		frame.getContentPane().add(applet);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	
	/**
	 * a little test routine, call it from within JavaScript like this:
	 * 
	 * document.getElementById("applet_id").testJavaScriptJavaInteraction("test"
	 * )
	 * 
	 * -> "received test"
	 * 
	 * @param input
	 * @return
	 */
	public String testJavaScriptJavaInteraction(String input) {
		return "received " + input;
	}

	/**
	 * call this routine from within JavaScript in order to launch the sniffy
	 * application
	 * 
	 * @return error message or empty string (no errors then)
	 */
	private String launchSniffy() {
	
		try {
			Runtime.getRuntime().exec(sniffyPath);
			launched = true;
			return "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	/**
	 * call this routine from within JavaScript in order to launch the sniffy
	 * application
	 * 
	 * @return error message or empty string (no errors then)
	 */
	public String doLaunchSniffy() {
		/**
		 * we need this to escape from the JavaScript security context denying file permissions!
		 */
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				return launchSniffy();
			}
		});		
	}
	
	

	/**
	 * 
	 * @return true, if sniffy was launched
	 */
	public boolean hasLaunchedSniffy() {
		return launched;
	}
	
	
}
