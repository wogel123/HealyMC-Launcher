package fr.wogel123.healymc.bootstrap;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;

public class HealymcBootstrap {

	private static SplashScreen splash;
	private static SColoredBar bar;
	private static Thread barThread;

	private static final LauncherInfos HMC_B_INFOS = new LauncherInfos("HealyMC", "fr.wogel123.healymc.launcher.LauncherFrame");
	private static final File HMC_DIR = GameDir.createGameDir("HealyMC");
	private static final LauncherClasspath HMC_B_CP = new LauncherClasspath(new File(HMC_DIR, "Launcher/launcher.jar"), new File(HMC_DIR, "Launcher/Libs/"));
	
	private static ErrorUtil errorUtil = new ErrorUtil(new File(HMC_DIR, "Launcher/crashes/"));
	
	public static void main(String[] args) {
		Swinger.setResourcePath("/fr/wogel123/healymc/bootstrap/resources/");
		displaySplash();
		try {
			doUpdate();
		} catch (Exception e) {
			errorUtil.catchError(e, "Impossible de mettre a jour le launcher");
			barThread.interrupt();
		}
		
		try {
			launchLauncher();
		} catch (IOException e) {
			errorUtil.catchError(e, "Impossible de lancer le launcher");
		}
	}
	
	private static void displaySplash() {
		splash = new SplashScreen("HealyMC", Swinger.getResource("splash.png"));
		splash.setLayout(null);
		
		splash.setBackground(Swinger.TRANSPARENT);
		splash.getContentPane().setBackground(Swinger.TRANSPARENT);
		
		bar = new SColoredBar(new Color(61, 57, 62), new Color(255, 87, 87));
		bar.setBounds(0, 250, 300, 10);
		splash.add(bar);
		
		splash.setVisible(true);
	}
	
	private static void doUpdate() throws Exception {
		SUpdate su = new SUpdate("https://download.healymc.fr/bootstrap/", new File(HMC_DIR, "Launcher"));
		su.getServerRequester().setRewriteEnabled(true);
		
		barThread = new Thread() {
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000));
					bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000));
				}
			}
		};
		barThread.start();
		
		su.start();
		barThread.interrupt();
	}
	
	private static void launchLauncher() throws IOException {
		Bootstrap bootstrap = new Bootstrap(HMC_B_CP, HMC_B_INFOS);
		Process p = bootstrap.launch();
		splash.setVisible(false);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			
		}
		System.exit(0);
	}
}
