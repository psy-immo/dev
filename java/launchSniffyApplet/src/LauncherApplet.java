import java.applet.Applet;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.Action;


public class LauncherApplet extends Applet {
	private final Action launchSniffy = new SniffyAction();
	
	static final String sniffyPath ="";
	
	/**
	 * applet main entry point
	 */
	
	public LauncherApplet() {
		
		JButton btnSniffy = new JButton("Sniffy");
		btnSniffy.setAction(launchSniffy);
		add(btnSniffy);
		
	}
	
	/**
	 * launch applet in a frame
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

	private class SniffyAction extends AbstractAction {
		public SniffyAction() {
			putValue(NAME, "Sniffy");
			putValue(SHORT_DESCRIPTION, "Launch Sniffy the virtual rat Pro.");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				Runtime.getRuntime().exec(sniffyPath);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}
	}
}
