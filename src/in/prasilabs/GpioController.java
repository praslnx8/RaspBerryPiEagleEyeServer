package in.prasilabs;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class GpioController 
{
	final com.pi4j.io.gpio.GpioController gpio;
	final GpioPinDigitalOutput pin1;
	final GpioPinDigitalOutput pin2;
	Data dt;
	
	public GpioController()
	{
		gpio = GpioFactory.getInstance();
		pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED", PinState.LOW);
		pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
		dt = new Data();
	}
	
	public void Door(boolean b)
	{
		if(b)
		{
			pin1.high();
			dt.setLockStatus(true);
			System.out.println("opened");
		}
		else
		{
			pin1.low();
			dt.setLockStatus(false);
			System.out.println("closed");
		}
	}
	
	public void Light(boolean b)
	{
		if(b)
		{
			pin2.high();
			dt.setLight(true);
			System.out.println("white");
		}
		else
		{
			pin2.low();
			dt.setLight(false);
			System.out.println("dark");
		}
	}
	
	public void close()
	{
		gpio.shutdown();
	}
	
	
}
