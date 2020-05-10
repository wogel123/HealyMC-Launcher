package fr.wogel123.healymc.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;
import re.alwyn974.mineweb.auth.exception.DataEmptyException;
import re.alwyn974.mineweb.auth.exception.DataWrongException;
import re.alwyn974.mineweb.auth.exception.ServerNotFoundException;
import re.alwyn974.mineweb.auth.mineweb.AuthMineweb;
import re.alwyn974.openlauncherlib.LaunchException;
import re.alwyn974.openlauncherlib.util.Saver;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {

	private Image background = Swinger.getResource("background.png");
	
	private Saver saver = new Saver(new File(Launcher.HMC_DIR, "launcher.properties"));
	
	private JTextField usernameField = new JTextField(saver.get("username"));
	private JPasswordField passwordField = new JPasswordField(saver.get("password"));

	private STexturedButton playButton = new STexturedButton(Swinger.getResource("play.png"));
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quit.png"));
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hide.png"));
	private STexturedButton settingButton = new STexturedButton(Swinger.getResource("setting.png"));
	
	private SColoredBar progressBar = new SColoredBar(new Color(61, 57, 62), new Color(255, 87, 87));
	private JLabel infoLabel = new JLabel(" ", SwingConstants.CENTER);
	
	
	public LauncherPanel() {
		this.setLayout(null);
		
		usernameField.setForeground(Color.WHITE);
		usernameField.setFont(usernameField.getFont().deriveFont(19F));
		usernameField.setCaretPosition(usernameField.getText().length());
		usernameField.setCaretColor(Color.WHITE);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setBounds(769, 269, 300, 40);
		this.add(usernameField);
		
		passwordField.setForeground(Color.WHITE);
		passwordField.setFont(passwordField.getFont().deriveFont(19F));
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(769, 356, 300, 40);
		this.add(passwordField);
		
		playButton.setBounds(719, 495);
		playButton.addEventListener(this);
		this.add(playButton);
		
		settingButton.setBounds(60, 55);
		settingButton.addEventListener(this);
		this.add(settingButton);
		
		hideButton.setBounds(1221, 55);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		quitButton.setBounds(1275, 55);
		quitButton.addEventListener(this);
		this.add(quitButton);
		
		progressBar.setBounds(163, 672, 1048, 32);
		this.add(progressBar);
		
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setBounds(163, 640, 1048, 32);
		this.add(infoLabel);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEvent(SwingerEvent e) {
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur : veuillez entrer un pseudo et un mot de passe valides", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
			Thread t = new Thread() {
				@Override
				public void run() {
					Launcher.auth(usernameField.getText(), passwordField.getText());
					
					try {
						AuthMineweb.start();
					} catch (DataWrongException e) {
						JOptionPane.showMessageDialog(LauncherFrame.getInstance(), "Erreur : Pseudo ou mot de passe invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						throw null;
					} catch (DataEmptyException e) {
						JOptionPane.showMessageDialog(LauncherFrame.getInstance(), "Erreur : Aucun pseudo ou mot de passe", "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						throw null;	
					} catch (ServerNotFoundException e) {
						JOptionPane.showMessageDialog(LauncherFrame.getInstance(), "Erreur : Problème de liaison avec le site", "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						throw null;
					} catch (IOException e) {
						JOptionPane.showMessageDialog(LauncherFrame.getInstance(), "Erreur : Impossible de récupérer vos informations sur le site", "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldsEnabled(true);
						throw null;	
					} if(AuthMineweb.isConnected());
					
					
					saver.set("username", usernameField.getText());
					saver.set("password", passwordField.getText());
					
					try {
						Launcher.update();
					} catch (Exception e) {
						Launcher.interruptThread();
						Launcher.getCrashReporter().catchError(e, "Impossible de mettre le jeu a jour");
						return;
					}
					
					try {
						Launcher.launch();
					} catch (LaunchException e) {
						Launcher.getCrashReporter().catchError(e, "Impossible de lancer le jeu");					
					}
				}
			};
			t.start();
		} else if(e.getSource() == quitButton) {
			Animator.fadeOutFrame(LauncherFrame.getInstance(), 2, new Runnable() {
				
				@Override
				public void run() {
					System.exit(0);
					
				}
			});
		} else if(e.getSource() == hideButton) {
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	public void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {
		return progressBar;
	}
	
	public void setInfoText(String text) {
		infoLabel.setText(text);
	}
}
