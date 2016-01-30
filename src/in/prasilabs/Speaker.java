package in.prasilabs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Speaker extends Thread{

	AudioInputStream audioInputStream;
	static AudioInputStream ais;
	static AudioFormat format;
	static boolean status = true;
	static int port = 8000;
	static int sampleRate = 44100;

	static DataLine.Info dataLineInfo;
	static SourceDataLine sourceDataLine;

	public void run()
	{
		System.out.println("Server started at port:"+port);
		
		DatagramSocket serverSocket;
		try {
			serverSocket = new DatagramSocket(port);
		

		/**
		 * Formula for lag = (byte_size/sample_rate)*2
		 * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
		 * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
		 * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
		 */

		byte[] receiveData = new byte[4096];

		format = new AudioFormat(sampleRate, 16, 1, true, false);
		dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		sourceDataLine.open(format);
		sourceDataLine.start();

		//FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
		//volumeControl.setValue(1.00f);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		ByteArrayInputStream baiss = new ByteArrayInputStream(receivePacket.getData());
		
		while (status == true) 
		{
			serverSocket.receive(receivePacket);
			ais = new AudioInputStream(baiss, format, receivePacket.getLength());
			toSpeaker(receivePacket.getData());
		}
		
		sourceDataLine.drain();
		sourceDataLine.close();
		
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
		} catch (LineUnavailableException e) {
			// TODO: handle exception
		}
	}

	public static void toSpeaker(byte soundbytes[]) {
		try 
		{
			sourceDataLine.write(soundbytes, 0, soundbytes.length);
		} catch (Exception e) {
			System.out.println("Not working in speakers...");
			e.printStackTrace();
		}
	}
}
