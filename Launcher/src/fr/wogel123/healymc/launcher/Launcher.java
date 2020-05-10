package fr.wogel123.healymc.launcher;

import java.io.File;

import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;
import re.alwyn974.mineweb.auth.mineweb.AuthMineweb;
import re.alwyn974.mineweb.auth.mineweb.mc.MinewebGameType;
import re.alwyn974.mineweb.auth.mineweb.utils.Get.getSession;
import re.alwyn974.mineweb.auth.mineweb.utils.TypeConnection;
import re.alwyn974.openlauncherlib.LaunchException;
import re.alwyn974.openlauncherlib.internal.InternalLaunchProfile;
import re.alwyn974.openlauncherlib.internal.InternalLauncher;
import re.alwyn974.openlauncherlib.minecraft.AuthInfos;
import re.alwyn974.openlauncherlib.minecraft.GameFolder;
import re.alwyn974.openlauncherlib.minecraft.GameInfos;
import re.alwyn974.openlauncherlib.minecraft.GameTweak;
import re.alwyn974.openlauncherlib.minecraft.GameVersion;
import re.alwyn974.openlauncherlib.minecraft.MinecraftLauncher;
import re.alwyn974.openlauncherlib.util.CrashReporter;

@SuppressWarnings("deprecation")
public class Launcher {

	public static final GameVersion HMC_VERSION = new GameVersion("1.8.8", MinewebGameType.V1_8_HIGHER);
	public static final GameInfos HMC_INFOS = new GameInfos("HealyMC", HMC_VERSION, new GameTweak[] {GameTweak.OPTIFINE});
	public static final File HMC_DIR = HMC_INFOS.getGameDir();
	public static final File HMC_CRASHES_DIR = new File(HMC_DIR, "crashes");
	
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	private static CrashReporter crashReporter = new CrashReporter("HealyMC Launcher", HMC_CRASHES_DIR);
	
	public static void auth(String username, String password) {
		authInfos = new AuthInfos(getSession.getUsername(), getSession.getAccessToken(), getSession.getUuid());
		
		AuthMineweb.setTypeConnection(TypeConnection.launcher);
		AuthMineweb.setUrlRoot("https://dev.healymc.fr");
		AuthMineweb.setUsername(username);
		AuthMineweb.setPassword(password);
		
			
		}
	

	public static void update() throws Exception {
		SUpdate su = new SUpdate("https://download.healymc.fr/", HMC_DIR);
		su.addApplication(new FileDeleter());
		
		updateThread = new Thread() {
			private int val;
			private int max;
			
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					if(BarAPI.getNumberOfFileToDownload() == 0) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers");
						continue;
					}
					
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
					max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);
					
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Telechargement des fichiers " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(val, max) + "%");
				
				}
			}
		};
		updateThread.start();
		
		su.start();
		updateThread.interrupt();
	}
	
	public static void launch() throws LaunchException {
		
		InternalLaunchProfile profil = MinecraftLauncher.createInternalProfile(HMC_INFOS, GameFolder.BASIC, authInfos);
		InternalLauncher launcher = new InternalLauncher(profil);
		
		LauncherFrame.getInstance().setVisible(false);

		launcher.launch();	
		
		System.exit(0);
	}
	
	public static void interruptThread() {
		updateThread.interrupt();
	}
	
	public static CrashReporter getCrashReporter() {
		return crashReporter;
	}
}
