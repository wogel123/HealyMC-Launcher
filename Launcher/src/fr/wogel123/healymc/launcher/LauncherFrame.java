package fr.wogel123.healymc.launcher;

import javax.swing.JFrame;

import com.sun.awt.AWTUtilities;

import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.util.WindowMover;

@SuppressWarnings("serial")
public class LauncherFrame extends JFrame {

	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	
	public LauncherFrame() {
		this.setTitle("HealyMC Launcher");
		this.setSize(1366, 768);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		this.setIconImage(Swinger.getResource("icon.png"));
		this.setContentPane(launcherPanel = new LauncherPanel());
		AWTUtilities.setWindowOpacity(this, 0.0F);
		
		WindowMover mover = new WindowMover(this);
		this.addMouseListener(mover);
		this.addMouseMotionListener(mover);
		
		this.setVisible(true);
		
		Animator.fadeInFrame(this, Animator.FAST);
	}
	
	
	
	public static void main(String[] args) {
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/wogel123/healymc/launcher/resources/");
		Launcher.HMC_CRASHES_DIR.mkdirs();
		
		instance = new LauncherFrame();
		
	}
	
	public static LauncherFrame getInstance() {
		return instance;
	}
	
	public LauncherPanel getLauncherPanel() {
		return this.launcherPanel;
	}

}
