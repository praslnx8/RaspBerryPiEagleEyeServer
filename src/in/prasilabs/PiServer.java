package in.prasilabs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PiServer extends Thread
{
	private int port;
	private int nos = 3;
	private ServerSocket serv;
	private Socket sck;
	private DataOutputStream os;
	private DataInputStream is;
	public String msg = null;
	public String remoteip;
	private String myremoteip;


	Client cl;
	AudioClient acl;

	GpioListener gpl;
	GpioController gpc;

	String filename = "userlist";
	File f = new File(".");
	BufferedReader br;
	BufferedWriter bw;

	Data dt = new Data();

	public PiServer(int port)
	{
		gpl = new GpioListener();
		gpl.start();
		try
		{
			Runtime.getRuntime().exec("sudo python CloseDoor.py");
			dt.setLockStatus(false);
		}
		catch (IOException e) 
		{
			System.out.println("Problem with python module....(Warning)");
			//e.printStackTrace();
		} 

		this.port = port;
	}

	public void run() 
	{
		try {

			ClientHandler clh = new ClientHandler();
			gpc = new GpioController();
			
			System.out.println("Waiting for Eagle to connect");
			serv = new ServerSocket(port,nos);

			while(true)
			{
				sck = serv.accept();

				try
				{
					clh.interrupt();
				}catch(Exception e)
				{

				}

				if(sck.isConnected())
				{
					System.out.println("Creating Streams");

					os = new DataOutputStream(sck.getOutputStream());
					is = new DataInputStream(sck.getInputStream());

					remoteip = sck.getInetAddress().toString().substring(1);	

					clh = new ClientHandler();

					clh.start();

				}

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		acl = dt.getAudioClient();

	}

	public class ClientHandler extends Thread
	{
		public void run()
		{
			System.out.println("Waiting for User Authenication:");
			recieveUserInfo(0);
			boolean connect = true;
			while(connect)
			{
				try 
				{
					System.out.println("Waiting for message from Eagle");
					msg = is.readUTF();
					messageHandler(msg);
					System.out.println("From EAGLE:"+msg);

				} catch (IOException e) {
					System.out.println("Disconnected");
					connect = false;
				}

				if(msg.equals("logn"))
				{
					recieveUserInfo(1);
				}
				else
				{
					if(dt.getLoginStatus())
					{
						messageHandler(msg);
					}
				}
			}

		}
		public void recieveUserInfo(int mode)
		{

			System.out.println("Waiting to get auth info");
			final String username;
			final String password;
			if(sck.isConnected())
			{
				try
				{
					br = new BufferedReader(new FileReader(f.getCanonicalPath()+"/"+filename));
					String datum,datum2;
					String data[][] = new String[10][10];
					int i = 1;

					while((datum=br.readLine())!=null && (datum2 = br.readLine()) != null)
					{
						data[i][1] = datum.trim();
						data[i][2] = datum2.trim();
						i++;
						//System.out.println("FILE::"+datum+"\n"+datum2);
					}
					br.close();

					if(mode == 0)
					{
						System.out.println("Waiting for message");
						if(is.readUTF().equals("logn"))
						{
							username = is.readUTF();
							System.out.println("Signing as "+username);
							password = is.readUTF();
							System.out.println("Authenticating");

							int login = 0;
							int j =1;

							if(data[j][1] != null)
							{
								//System.out.println(data[j][1]);
								if(data[j][1].equals(username))
								{
									if(data[j][2].equals(password))
									{
										System.out.println("Success");
										os.writeUTF("success");
										dt.setLoginStatus(true);
										dt.setRemoteip(remoteip);
										try
										{
											cl.interrupt();
										}catch(NullPointerException e)
										{

										}
										cl = new Client(remoteip, 1);
										dt.setClientObject(cl);
										login = 1;
									}
									else
									{
										os.writeUTF("error");
										login = 0;
									}
								}
								else
								{
									os.writeUTF("error");
									login = 0;
								}
								j++;
							}
							if(login == 1)
							{
								acl = dt.getAudioClient();
								try
								{
									acl.stopAudio();
									System.out.println("audio closed");
								}catch(Exception e)
								{

								}
								acl = new AudioClient();
								dt.setAudioClient(acl);
								acl.start();
								dt.setLoginStatus(true);
							}
							else
							{
								dt.setLoginStatus(false);
								System.out.println("Unauthorised access exiting:Username:"+username);
								Thread.interrupted();
							}
						}
					}

					else
					{
						username = is.readUTF();
						System.out.println("Signing as "+username);
						password = is.readUTF();
						System.out.println("Authenticating");

						int login = 0;
						int j =1;

						if(data[j][1] != null)
						{
							//System.out.println(data[j][1]);
							if(data[j][1].equals(username))
							{
								if(data[j][2].equals(password))
								{
									dt.setLoginStatus(true);
									os.writeUTF("success");
									System.out.println("Success");
									dt.setRemoteip(remoteip);

									myremoteip = remoteip;
									cl = new Client(myremoteip, 1);
									cl.sendMessage("success");
									dt.setClientObject(cl);
									login = 1;
								}
								else
								{
									os.writeUTF("error");
									dt.setLoginStatus(false);
									login = 0;
								}
							}
							else
							{
								os.writeUTF("error");
								dt.setLoginStatus(false);
								login = 0;
							}
							j++;
						}

						if(login == 1)
						{
							dt.setLoginStatus(true);
							acl = dt.getAudioClient();
							try
							{
								acl.interrupt();
								System.out.println("audio closed");
							}catch(Exception e)
							{

							}
							acl.start();
						}
						else
						{
							dt.setLoginStatus(false);
							System.out.println("Unauthorised access exiting::username"+username);
							Thread.interrupted();
						}
					}
				}


				catch(IOException e)
				{
					dt.setLoginStatus(false);
					try
					{
						acl.interrupt();
						System.out.println("audio closed");
					}catch(Exception e2)
					{

					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					dt.setLoginStatus(false);
					System.out.println("Problem at server");
					try
					{
						acl.interrupt();
						System.out.println("audio closed");
					}catch(Exception e2)
					{

					}
				}
			}
		}


		public void changePassword()
		{
			String oldusername;
			String username;
			String password;

			try {
				br = new BufferedReader(new FileReader(f.getCanonicalPath()+"/"+filename));
				bw = new BufferedWriter(new FileWriter(f.getCanonicalPath()+"/"+filename));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {

				oldusername = is.readUTF();
				username = is.readUTF();
				password = is.readUTF();

				String datum;
				while((datum = br.readLine()) != null)
				{
					if(oldusername.equals(datum))
					{
						oldusername.replace(oldusername, username);
						bw.write(password);
					}
				}


			} catch (IOException e) {
				try
				{
					acl.interrupt();
					System.out.println("audio closed");
				}catch(Exception e2)
				{

				}

			}
		}


		private void messageHandler(String msg)
		{
			if(msg.equals("open"))
			{
				gpc.Door(true);
			}
			else if(msg.equals("close"))
			{
				gpc.Door(false);
			}
			else if(msg.equals("lighton"))
			{
				gpc.Light(true);
			}
			else if(msg.equals("lightoff"))
			{
				gpc.Light(false);
			}
			else if(msg.equals("changepassword"))
			{

			}
			else if(msg == null || msg.equals(""))
			{

			}
			else if(msg.equalsIgnoreCase("refresh"))
			{
				gpl.interrupt();
				gpl = new GpioListener();
				gpl.start();
			}
			else if(msg.equalsIgnoreCase("status"))
			{
				Boolean LockStatus = dt.getLockStatus();
				if(LockStatus)
				{
					cl.sendMessage("dopen");
				}
				else
				{
					cl.sendMessage("dclose");
				}
				
				Boolean LightStatus = dt.getLight();
				if(LightStatus)
				{
					cl.sendMessage("lon");
				}
				else
				{
					cl.sendMessage("loff");
				}
				
			}

			else if(msg.equalsIgnoreCase("logn"))
			{
				recieveUserInfo(1);
			}
			else if(msg.equalsIgnoreCase("reboot"))
			{
				String mycommand = "sudo reboot";
				try {
					Runtime.getRuntime().exec(mycommand);
					System.out.println("Going to Reboot");
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			else if(msg.equalsIgnoreCase("poweroff"))
			{
				String mycommand = "sudo poweroff";
				try {
					Runtime.getRuntime().exec(mycommand);
					System.out.println("Going to shutdown");
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			else
			{

			}
		}
	}

}

