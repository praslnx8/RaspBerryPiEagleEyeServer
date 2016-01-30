package in.prasilabs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Start 
{
	public static void main(String[] args) throws IOException
	{
		
		Speaker spk = new Speaker();
		spk.start();
		
		Data dt = new Data();
		AudioClient acl = new AudioClient();
		dt.setAudioClient(acl);
		
		String filename = "userlist";
		File f = new File(".");
		File file = new File(f.getCanonicalPath()+"/"+filename);
		if(file.exists())
		{
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				BufferedWriter bw;
				try 
				{
					if((br.readLine()) == null || (br.readLine()) == null)
					{
						System.out.println("File empty");
						br.close();
						bw = new BufferedWriter(new FileWriter(file));
						bw.write("admin \n");
						bw.write("admin123 \n");
						bw.close();
					}
					br.close();
				} catch (IOException e) 
				{
					e.printStackTrace();
					System.exit(0);
				}
			} catch (FileNotFoundException e) 
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
		else
		{
			System.out.println("File not found creating a new one with default values");
			if(file.createNewFile())
			{
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				bw.write("admin \n");
				bw.write("admin123 \n");
				bw.close();
			}
			else
			{
				System.out.println("Error creating file | Aborting");
				System.exit(0);
			}
		}
		
		
		
		PiServer srv = new PiServer(1500);
		srv.start();
	}
}
