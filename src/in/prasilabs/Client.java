package in.prasilabs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread
{

	public int ret = 0;
	private String ip;
	private int port;
	private Socket sck;
	private DataOutputStream os;
	private String msg;

	Data dt = new Data();
	public Client(String host, int mode) 
	{
		System.out.println("Client initializing");
		this.port = dt.getPort();
		if(mode == 1)
		{	
			ip = host;
			new Thread(this).start();
		}
		else if(mode == 2)
		{
			if(dt.getRemoteip() != null)
			{
				ip = dt.getRemoteip();
				new Thread(this).start();
				System.out.println("PI: Sent to "+this.ip);
			}
			else
			{
				System.out.println("Cant do... anything.. No Eagles");
			}
		}
	}


	public void run()
	{
		establish();
	}

	public void establish()
	{
		int time_out = 3000;
		try 
		{
			System.out.println("trying to connect with port" + port +" and "+ ip);
			sck = new Socket();
			sck.connect(new InetSocketAddress(ip,port),time_out);
			System.out.println("Established");
			ret = 1;
			this.sendMessage("success");
		}
		catch (UnknownHostException e) 
		{
			ret =0;
			System.out.println("Unknown host exception at establish");
			//e.printStackTrace();
		} 
		catch (IOException e) 
		{
			ret = 0;
			System.out.println("IO exception at establish");
			e.printStackTrace();
		}
	}

	public void sendMessage(String mesg)
	{
		try 
		{
			msg = mesg;
			if(sck.isConnected())
			{
				os = new DataOutputStream(sck.getOutputStream());
				sck.setSoTimeout(2000);
				os.writeUTF(msg);
				System.out.println("@PI :" +msg  + "t0" + ip);
				ret = 1;
			}
			else
			{
				ret = 0;
			}
		}
		catch (IOException e) 
		{
			ret = 0;
			e.printStackTrace();
		}
	}
}
