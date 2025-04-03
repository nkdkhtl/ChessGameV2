package utils;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    public static boolean isTurnOn = true;
    
    public static void setSoundSate(String state) {
    	if (state == "On") {
    		isTurnOn = true;
    	} else {
    		isTurnOn = false;
    	}
    }
    
    public static boolean getSoundSate() {
    	return isTurnOn;
    }
    public static void playSound(String soundFileName) {
        if (!isTurnOn) {
            return; // Do not play sound if sound effects are disabled
        }
        
        try {
            File soundFile = new File("res/sounds/" + soundFileName + ".wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}