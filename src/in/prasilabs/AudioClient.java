package in.prasilabs;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class AudioClient extends Thread
{

	public void run()
	{
		System.out.println("Audio Client started");
		captureAudio();
	}
	/**
	 * 
	 */

	boolean stopaudioCapture = false;
	ByteArrayOutputStream byteOutputStream;
	AudioFormat adFormat;
	TargetDataLine targetDataLine;
	AudioInputStream InputStream;
	SourceDataLine sourceLine;
	Data dt = new Data();
		
	public void stopAudio()
	{
		targetDataLine.close();
	}

	private void captureAudio() {
		try {
			
			
			
			adFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(adFormat);
			targetDataLine.start();

			CaptureThread captureThread = new CaptureThread();
			captureThread.run();
			targetDataLine.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			targetDataLine.close();
			
			
		}
	}


	private AudioFormat getAudioFormat() 
	{
		float sampleRate = 16000.0F;
		int sampleInbits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
	}

	class CaptureThread
	{

		byte tempBuffer[] = new byte[512];

		public void run() 
		{

			DatagramSocket clientSocket;
			byteOutputStream = new ByteArrayOutputStream();
			stopaudioCapture = false;
			try {
				String ip = dt.getRemoteip();
				clientSocket = new DatagramSocket();
//				InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
				InetAddress IPAddress = InetAddress.getByName(ip);
				
				while (!stopaudioCapture) {
					int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
					if (cnt > 0) {
						DatagramPacket sendPacket = new DatagramPacket(tempBuffer, tempBuffer.length, IPAddress, 50005);
						clientSocket.send(sendPacket);
					}
				}
				clientSocket.close();
			} catch (Exception e) {
				System.out.println("CaptureThread::run()" + e);
				targetDataLine.close();
			}
		}
	}
}