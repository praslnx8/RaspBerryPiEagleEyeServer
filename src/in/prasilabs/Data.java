package in.prasilabs;

public class Data extends Thread implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;

	private static String remoteip;
	private static Boolean LockStatus = false;
	private static Client cl;
	private static AudioClient acl;
	private static int clport = 2000;
	private static boolean login = false;
	private static boolean light = true;

	public String getRemoteip()
	{
		return remoteip;
	}

	public void setRemoteip(String ip)
	{
		remoteip = ip;
	}

	public void setLockStatus(boolean b)
	{
		LockStatus = b;
	}
	
	public void setLight(boolean b)
	{
		light = b;
	}

	public Boolean getLockStatus()
	{
		return LockStatus;
	}

	public void setClientObject(Client cle) 
	{
		cl= cle;	
	}

	public void setLoginStatus(boolean lgn)
	{
		System.out.println("login set to "+lgn);
		login = lgn;
	}
	
	public void setAudioClient(AudioClient adcl)
	{
		acl = adcl;
	}

	public Client getClientObject()
	{
		return cl;
	}

	public int getPort() 
	{
		return clport;	
	}

	public boolean getLoginStatus()
	{
		return login;
	}
	
	public AudioClient getAudioClient()
	{
		return acl;
	}
	
	public boolean getLight()
	{
		return light;
	}

}