package in.prasilabs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;

import javax.swing.JFrame;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.exception.GpioPinExistsException;

public class GpioListener extends Thread
{

	private Client cl;
	//private LogDatabase ldb = new LogDatabase();
	Data dt = new Data();

	public GpioListener()
	{

	}
	public void run() 
	{
		System.out.println("<--Pi4J--> GPIO Listen started.");

		boolean debug = false;
		if(debug)
		{
			JFrame frm = new JFrame("Debug with key listener");
			frm.setSize(400, 400);
			frm.setVisible(true);
			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frm.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent arg0) 
				{
					
				}

				@Override
				public void keyReleased(KeyEvent arg0) 
				{
					System.out.println("Pressed n released");
					if(dt.getLoginStatus())
					{
						System.out.println("Pressed");
						cl = dt.getClientObject();
						cl.sendMessage("call");
					}
					else
					{
						System.out.println("Pressed | Not connected");
					}
					//ldb.log();
				}

				@Override
				public void keyPressed(KeyEvent arg0) 
				{
				
				}
			});
		}
		else
		{
			final GpioController gpio = GpioFactory.getInstance();

			GpioPinDigitalInput myButton = null;
			try
			{
				myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

			}
			catch(GpioPinExistsException e)
			{
				System.out.println("Unable to create GPIO Listener Button");
			}

			try
			{
				myButton.addListener(new GpioPinListenerDigital() 
				{
					Calendar cal = Calendar.getInstance();
					long prevtime = cal.getTimeInMillis();

					public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
					{
						if(event.getState().toString().equalsIgnoreCase("HIGH"))
						{
							cal = Calendar.getInstance();
							long curtime = cal.getTimeInMillis();
							if((curtime-prevtime) < 10000)
							{
								System.out.println("False Deflection");
							}
							else
							{
								System.out.println("Pressed");
								if(dt.getLoginStatus())
								{
									cl = dt.getClientObject();
									cl.sendMessage("call");
								}
								else
								{
									System.out.println("Sorry No eagles :(");
								}
								
								//ldb.log();
							}
							prevtime = curtime;
						}
					}

				});
			}
			catch(NullPointerException e2)
			{
				System.out.println("Exception at GPIO.. Exiting");
				gpio.shutdown();
				System.exit(0);
			}
			/*----------End of GPIO -----------*/
		}
	}
}
