package shutwid;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import marytts.signalproc.effects.JetPilotEffect;
import marytts.signalproc.effects.LpcWhisperiserEffect;
import marytts.signalproc.effects.RobotiserEffect;
import marytts.signalproc.effects.StadiumEffect;
import marytts.signalproc.effects.VocalTractLinearScalerEffect;
import marytts.signalproc.effects.VolumeEffect;

public class Main {

	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());

	// Variables
	private String result;

	// Threads
	Thread	speechThread;
	Thread	resourcesThread;

	// LiveRecognizer
	private LiveSpeechRecognizer recognizer;

	/**
	 * Constructor
	 */
	public Main() {

		// Loading Message
		logger.log(Level.INFO, "Loading..\n");

		// Configuration
		Configuration configuration = new Configuration();

		// Load model from the jar
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

		// if you want to use LanguageModelPath disable the 3 lines after which
		// are setting a custom grammar->

		// configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")

		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);
                
               
		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

		// Start recognition process pruning previously cached data.
		recognizer.startRecognition(true);

		// Start the Thread
		startSpeechThread();
		startResourcesThread();
	}

	 /*
	    main Thread of speech recognition
	 */
	protected void startSpeechThread() {
                        //======================================================================================//
        //Create TextToSpeech
		TextToSpeech tts1 = new TextToSpeech();
		
		//=========================================================================
		//======================= Print available AUDIO EFFECTS ====================
		//=========================================================================
		
		//Print all the available audio effects
		tts1.getAudioEffects().stream().forEach(audioEffect -> {
			System.out.println("-----Name-----");
			System.out.println(audioEffect.getName());
			System.out.println("-----Examples-----");
			System.out.println(audioEffect.getExampleParameters());
			System.out.println("-----Help Text------");
			System.out.println(audioEffect.getHelpText() + "\n\n");
			
		});
		
		//=========================================================================
		//========================= Print available voices =========================
		//=========================================================================
		
		//Print all the available voices
		tts1.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
		
		// Setting the Current Voice
		tts1.setVoice("cmu-rms-hsmm");
		
		//=========================================================================
		//========================= Let's try different effects=====================
		//=========================================================================
		
		//----- Hey you !-> check the help that is being printed on the console
		//----- You will understand how to use the effects better :)
		
		//VocalTractLinearScalerEffect
		VocalTractLinearScalerEffect vocalTractLSE = new VocalTractLinearScalerEffect(); //russian drunk effect
		vocalTractLSE.setParams("amount:70");
		
		//JetPilotEffect
		JetPilotEffect jetPilotEffect = new JetPilotEffect(); //epic fun!!!
		jetPilotEffect.setParams("amount:100");
		
		//RobotiserEffect
		RobotiserEffect robotiserEffect = new RobotiserEffect();
		robotiserEffect.setParams("amount:50");
		
		//StadiumEffect
		StadiumEffect stadiumEffect = new StadiumEffect();
		stadiumEffect.setParams("amount:150");
		
		//LpcWhisperiserEffect
		LpcWhisperiserEffect lpcWhisperiserEffect = new LpcWhisperiserEffect(); //creepy
		lpcWhisperiserEffect.setParams("amount:70");
		
		//VolumeEffect
		VolumeEffect volumeEffect = new VolumeEffect(); //be careful with this i almost got heart attack
		volumeEffect.setParams("amount:0");
		
		//Apply the effects
		//----You can add multiple effects by using the method `getFullEffectAsString()` and + symbol to connect with the other effect that you want
		//----check the example below
		tts1.getMarytts().setAudioEffects(stadiumEffect.getFullEffectAsString());// + "+" + stadiumEffect.getFullEffectAsString());
		
		//=========================================================================
		//===================== Now let's troll user ==============================
		//=========================================================================
	//      List<String> arrayList = Arrays.asList("gunza khabo bro", "I am jarvis", " how may i help you master", "life is good sometimes");
		
		//Loop infinitely
	//      for (int i = 0; i < 1.000; i++)
	//          arrayList.forEach(word -> tts.speak(word, 2.0f, false, true));
                
         //       tutorial_1_2_3_FromYoutube();
		
	
        //======================================================================================//
        

		// alive?
		if (speechThread != null && speechThread.isAlive())
			return;

		// initialise
		speechThread = new Thread(() -> {
			logger.log(Level.INFO, "You can start to speak...\n");
                        TextToSpeech ttsB = new TextToSpeech();
                        ttsB.speak("i am jarvis a program to shut down and restart your P C", 2.0f, false, true);
			try {
				while (true) {
					/*
					 * This method will return when the end of speech is
					 * reached. Note that the end pointer will determine the end
					 * of speech.
					 */
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {

						result = speechResult.getHypothesis();
						System.out.println("You said: [" + result + "]\n");
						makeDecision(result);
						// logger.log(Level.INFO, "You said: " + result + "\n")

					} else
						logger.log(Level.INFO, "I can't understand what you said.\n");

				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}

	/**
	 * Starting a Thread that checks if the resources needed to the
	 * SpeechRecognition library are available
	 */
	protected void startResourcesThread() {

		// alive?
		if (resourcesThread != null && resourcesThread.isAlive())
			return;

		resourcesThread = new Thread(() -> {
			try {

				// Detect if the microphone is available
				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						// logger.log(Level.INFO, "Microphone is available.\n")
					} else {
						// logger.log(Level.INFO, "Microphone is not
						// available.\n")

					}

					// Sleep some period
					Thread.sleep(150); //350
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		// Start
		resourcesThread.start();
	}

	/**
	 * Takes a decision based on the given result
	 */
	public void makeDecision(String speech) {


        String s1 =speech;
        TextToSpeech tts = new TextToSpeech();
        //Print all the available voices
	tts.getAvailableVoices().stream().forEach(voice -> System.out.println("Voice: " + voice));
        tts.setVoice("cmu-rms-hsmm");
        
        // tts.speak("i am jarvis a program to shut down and restart your P C", 2.0f, false, true);
        
        
        //=========================================================================================
                //Arduino
                ArduinoSerial arduino = new ArduinoSerial("COM4");
                
           //
                //=========================================================================================

         
         
        if (s1.matches(".*hello.*")){    //compareToIgnoreCase("hello jarvis")==0
            
            System.out.println("hello sir!!!  :D");
            tts.speak("hello master armun", 2.0f, false, true);
                       
        }
            else if (s1.matches(".*how are you.*")){    //compareToIgnoreCase("hello jarvis")==0
            
                 System.out.println("fine!!!  :D");
                 tts.speak("i am fine ", 2.0f, false, true);
                 tts.speak("how about you ", 2.0f, false, true);
                       
            }
            else if (s1.matches(".*am fine.*")){    //compareToIgnoreCase("hello jarvis")==0
            
                 System.out.println("how may i help you!!!  :D");
                 tts.speak("how may i help you ", 2.0f, false, true);
          
            }
            
            else if (s1.compareToIgnoreCase("okay")==0){    //compareToIgnoreCase("hello jarvis")==0
            
                 System.out.println("okay!!!  :D");
                 tts.speak("okay", 2.0f, false, true);
          
            }
            
            else if(s1.compareToIgnoreCase("thank you")==0){
                System.out.println("happy to help you... :D");
                tts.speak("my pleasure master ", 2.0f, true, true);
                tts.speak("happy to help you ", 2.0f, true, true);
            }
            
            else if(s1.compareToIgnoreCase("system log off")==0){
                System.exit(0);
            }
            
             
            else if (s1.compareToIgnoreCase("lights on")==0){    //compareToIgnoreCase("hello jarvis")==0
            
                 System.out.println("lights turned on");
                 arduino.initialize();
                 arduino.send("n");
                 tts.speak("lights turned on", 2.0f, false, true);
          
            }
            else if (s1.compareToIgnoreCase("lights off")==0){    //compareToIgnoreCase("hello jarvis")==0
            
                 System.out.println("lights turned off");
                 arduino.initialize();
                 arduino.send("f");
                 tts.speak("lights turned off", 2.0f, false, true);
          
            }
            
        
                else if(s1.compareToIgnoreCase("shut down")==0){
                    String comExec = "shutdown -s -t 120";
                    try{
                        Runtime.getRuntime().exec(comExec);
                        tts.speak("windows will shut down in t minus 1 20 second", 2.0f, false, true);
                    }
                    catch(IOException e){
        
                    }
                }
        
                    else if (s1.compareToIgnoreCase("restart")==0){
                        String  comExec = "shutdown -r -t 120";
                        try{
                            Runtime.getRuntime().exec(comExec);
                            tts.speak("windows will restart in t minus 1 20 second", 2.0f, false, true);
                        }
                        catch(IOException e){
        
                        }
                    }
                        else if (s1.compareToIgnoreCase("cancel")==0){
                            String comExec = "shutdown /a";
                            try{
                                Runtime.getRuntime().exec(comExec);
                                tts.speak("command cancelled", 2.0f, false, true);
                            }
                            catch(IOException e){
        
                            }
                        }
                        
                        
        
        
                            else System.out.println("could you repeat that again please...");;

	}

	/**
	 * Java Main Application Method
	 * 
	 * @param args
	 */
//	public static void main(String[] args) {

		// // Be sure that the user can't start this application by not giving
		// the
		// // correct entry string
		// if (args.length == 1 && "SPEECH".equalsIgnoreCase(args[0]))
//		new Main();
		// else
		// Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Give me
		// the correct entry string..");

//	}

}