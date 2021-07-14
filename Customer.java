package components;
/**
<h1>Customer Class!</h1>
*the customers creating the packges they want to send,
* the main office  handles them
* the customer has a read write lock to maintain proper file handle
* customer has an address that he sends all his packages from
* <p>
*
* 
* 

*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
	private Reader_Writer_Lock lock;
	private Address C_add;
	private static long StaticID=getRandomNumber(200000000,400000000);
	final private long C_Id;
	protected ArrayList <Package> unsafeListPackages = new ArrayList<Package>();
	protected List<Package> listPackages = unsafeListPackages; //Collections.synchronizedList(unsafeListPackages);	
	boolean IsFinished=false;
	protected boolean threadSuspend = false;

	
	public synchronized void setSuspend() {
	   	threadSuspend = true;
	}

	public synchronized void setResume() {
	   	threadSuspend = false;
	   	notify();
	}
	
	
	public Customer(Reader_Writer_Lock lock ) {
		Random r = new Random();
		C_Id=StaticID+getRandomNumber(999999,100000);
		C_add=new Address(r.nextInt(MainOffice.getHub().getBranches().size()), r.nextInt(999999)+100000);
		this.lock=lock;
	}
	@Override
	public void run() {
		Random r = new Random();
		
		for(int i=0;i<5;i++) {
			synchronized(this) {
	            while (threadSuspend)
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
			AddPackage();
			try {
				Thread.sleep(r.nextInt(2000)+5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
		
	}
	/**
	<h1>ReadTracking</h1>
	*method for reading the trcking file
	* <p>
	*
	* 
	* 

	*/

	public boolean ReadTracking() {
		String line = null;
        ArrayList<String> fileContents = new ArrayList<>();
         int counter=0;
        try {
            FileReader fReader = new FileReader(MainOffice.f1);
            BufferedReader fileBuff = new BufferedReader(fReader);
            while ((line = fileBuff.readLine()) != null) {
                fileContents.add(line);
            }
            fileBuff.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for(String s : fileContents)
        if(s.contains(""+this.C_Id)) {
        	if(s.contains("status=DELIVERED"))
		         counter++;
        }
        if(counter==5)
			this.IsFinished=true;
        
		return this.IsFinished;
	}
	
	/**
	<h1>ReadTracking</h1>
	*method for writing on the tracking file
	* <p>
	*
	* 
	* 

	*/
	
	public void WriteTracking(String tr) {
		try {
			
			FileWriter F_W=new FileWriter(MainOffice.f1.getName(),true);
			BufferedWriter B_W= new BufferedWriter(F_W);
		      
			B_W.write(tr);
			B_W.newLine(); 
		      
		      
			B_W.close();
			F_W.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
		
		
	}
	
	
	public void AddPackage()   {
		Random r = new Random();
		Branch br;
		
		Priority priority=Priority.values()[r.nextInt(3)];
		Package p;Tracking tr;
		Address dest = new Address(r.nextInt(MainOffice.getHub().getBranches().size()), r.nextInt(999999)+100000);
		switch (r.nextInt(3)){
		case 0:
			p = new SmallPackage(priority,  C_add, dest, r.nextBoolean(),this.C_Id);
			 tr=new Tracking( MainOffice.getClock(), null, p.getStatus(),this.C_Id);
			p.addTracking(tr);
			WriteTracking("Package_ID :"+p.getPackageID()+"-"+tr.toString());
			br = MainOffice.getHub().getBranches().get(C_add.zip);
			br.addPackage(p);
			p.setBranch(br); 
			break;
		case 1:
			p = new StandardPackage(priority,  C_add, dest, r.nextFloat()+(r.nextInt(9)+1),this.C_Id);
			 tr=new Tracking( MainOffice.getClock(), null, p.getStatus(),this.C_Id);
				p.addTracking(tr);
				WriteTracking("Package_ID :"+p.getPackageID()+"-"+tr.toString());
			br = MainOffice.getHub().getBranches().get(C_add.zip); 
			br.addPackage(p);
			p.setBranch(br); 
			break;
		case 2:
			p=new NonStandardPackage(priority,  C_add, dest,  r.nextInt(400), r.nextInt(200), r.nextInt(350),this.C_Id);
			 tr=new Tracking( MainOffice.getClock(), null, p.getStatus(),this.C_Id);
				p.addTracking(tr);
				WriteTracking("Package_ID :"+p.getPackageID()+"-"+tr.toString());
			MainOffice.getHub().addPackage(p);
			break;
		default:
			p=null;
			return;
		}
		
		MainOffice.getPackages().add(p);
		this.listPackages.add(p);
		System.out.println("\r\n**************Customer Num:"+this.C_Id+" Created A new Package: " + p.getPackageID() + "\r\n From: "+this.C_add.toString()+" To: "+dest.toString()  );
		
			
		
		
		
		
	}
	
	public static long getRandomNumber(long min, long max) {
	    return (long) ((Math.random() * (max - min)) + min);
	    }
	public long getC_ID() {
		// TODO Auto-generated method stub
		return this.C_Id;
	}
	
}
