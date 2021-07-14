package components;
/**
* 
*
* @authors  Omer Ben David:316344449,Reuven Hadad:312264781

*/
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;

/**
<h1>Main Office Class!</h1>
*the class that handles all office components
* <p>
*
* 
* 

*/
public class MainOffice implements Runnable{
	public static JPanel getPanel() {
		return panel;
	}


	public static void setPanel(JPanel panel) {
		MainOffice.panel = panel;
	}

	private static volatile MainOffice single_instance = null;
	private static int clock=0;
	public static void setClock(int clock) {
		MainOffice.clock = clock;
	}

	private static Hub hub;
	private static ArrayList<Package> packages=new ArrayList<Package>();
	private static ArrayList<Customer> Customers=new ArrayList<Customer>();
	private static  JPanel panel;
	private int maxPackages;
	private boolean threadSuspend = false;
	static  File f1=new File("tracking.txt");
	static Reader_Writer_Lock customerLock=new Reader_Writer_Lock();
	static WriteReeLock lock = new WriteReeLock();
	private static int FinishNum=0; 
	 private static Hashtable<String, Node> BranchMap  = new Hashtable<String, Node>();
	 
	 
	 /**
	 <h1>CloneBranch!</h1>
	 *Method for adding a new branch using clones
	 * <p>
	 *
	 * 
	 * 

	 */
	 
	public static void CloneBranch(String BranchNum) {
		Node b=getBranch(BranchNum);
		hub.add_branch((Branch) b);
		BranchMap.put(""+(hub.getBranches().size()-1),b) ;
		
		
	}
	
	
	 /**
	 <h1>RestoreBranch!</h1>
	 *Method for restoring the system to the original branches status
	 * <p>
	 *
	 * 
	 * 

	 */
	public static void RestoreBranch() {
		Hub H1=(Hub)BranchMap.get("0");
		
		if(BranchMap.size()==0) {
			System.out.println("Cannot restore before a Clone has been added");
		}
			
		
		
		else {
//			for(int i=0;i<hub.getBranches().size();i++) 
				hub.getBranches().removeAll(hub.getBranches());
				
			
			
			for (int i=0;i<H1.getBranches().size();i++) 
				hub.getBranches().add(H1.getBranches().get(i));
				
			
			
			

				
			}
			
			 

			
		}
	
	
	/**
	 <h1>loadCache!</h1>
	 *Method saving the original hub and the branches on hash map
	 * <p>
	 *
	 * 
	 * 

	 */
	
	 public static void loadCache() {
		 BranchMap.put("0", (hub.copy()));
		 for(int i=1;i<hub.getBranches().size()+1;i++) {
			 BranchMap.put(""+i,hub.getBranches().get(i-1)) ;
		 } 
	 }
	 
	 public static Hashtable<String, Node> getBranchMap() {
		return BranchMap;
	}
	public static void setBranchMap(Hashtable<String, Node> branchMap) {
		BranchMap = branchMap;
	}
	public static Node getBranch(String BranchNum) {
		 Node cachedBranch = BranchMap.get(BranchNum);
	      return (Branch) cachedBranch.copy();
	   }
	 
	private  MainOffice(int branches, int trucksForBranch, JPanel panel, int maxPack) {
		this.panel = panel;
		this.setMaxPackages(50);
		addHub(trucksForBranch);
		addBranches(branches, trucksForBranch);
		for(int i=0;i<10;i++) {
			Customer c1=new Customer(customerLock);
			Customers.add(c1);
			}
		loadCache();
		
		System.out.println("\n\n========================== START ==========================");
	}
	/**
	 <h1>double checked singleton!</h1>
	 
	 * 
	 *
	 * 
	 * 

	 */
	
	public static MainOffice getInstance(int branches, int trucksForBranch, JPanel panel, int maxPack)
    {
        if (single_instance == null)//double checked singleton
        	  synchronized (MainOffice .class) {
                  if (single_instance == null) {
                	  single_instance = new MainOffice(branches, trucksForBranch, panel, maxPack);
                  }
              }
          
          return single_instance;
    }
	public static Hub getHub() {
		return hub;
	}


	public static int getClock() {
		return clock;
	}
   public static boolean IsAllFinished() {
    	    if(FinishNum<10)
    	    	return false;
    	
    	return true;
    	
    }
	@Override
	public void run() {
		for(int i=0;i<10;i++) {
			Thread CustomerThread = new Thread(Customers.get(i));
			CustomerThread.start();
		}
		Thread hubThrad = new Thread(hub);
		hubThrad.start();
		for (Truck t : hub.listTrucks) {
			Thread trackThread = new Thread(t);
			trackThread.start();
		}
		for (Branch b: hub.getBranches()) {
			Thread branch = new Thread(b);
			for (Truck t : b.listTrucks) {
				Thread trackThread = new Thread(t);
				trackThread.start();
			}
			branch.start();
		}
		while(true) {
		    synchronized(this) {
                while (threadSuspend)
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
			tick();
		}
	}
	
	public void printReport() {
		for (Package p: packages) {
			System.out.println("\nTRACKING " +p);
			for (Tracking t: p.getTracking())
				System.out.println(t);
		}
	}
	
	
	public String clockString() {
		String s="";
		int minutes=clock/60;
		int seconds=clock%60;
		s+=(minutes<10) ? "0" + minutes : minutes;
		s+=":";
		s+=(seconds<10) ? "0" + seconds : seconds;
		return s;
	}
	
	
	public void tick() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(clockString());
		clock++;
		if (MainOffice.getClock()%10==0) {
			int counter=0;
			for(int i=0;i<10;i++) {
				if(this.Customers.get(i).ReadTracking()) {
		        	System.out.println(this.Customers.get(i).getC_ID()+" All Packages Delivered");
//		        	MainOffice.setFinishNum(MainOffice.getFinishNum()+1);
		        	counter+=1;
				}
				if(counter==10)
					MainOffice.setFinishNum(10);
			}
				
			}
			

			if(IsAllFinished()) {
	        	System.out.println("All Packages Delivered");
	        	this.setSuspend();
	        }
		
		
	
		panel.repaint();
	}
	
	
	
	public void branchWork(Branch b) {
		for (Truck t : b.listTrucks) {
			t.work();
		}
		b.work();
	}
	
	
	public void addHub(int trucksForBranch) {
		hub=new Hub();
		for (int i=0; i<trucksForBranch; i++) {
			Truck t = new StandardTruck(lock);
			hub.addTruck(t);
		}
		Truck t=new NonStandardTruck(lock);
		hub.addTruck(t);
	}
	
	
	public void addBranches(int branches, int trucks) {
		for (int i=0; i<branches; i++) {
			Branch branch=new Branch();
			for (int j=0; j<trucks; j++) {
				branch.addTruck(new Van(lock));
			}
			hub.add_branch(branch);		
		}
	}
	
	
	public static ArrayList<Package> getPackages(){
		return packages;
	}
	
//	public void addPackage() {
//		Random r = new Random();
//		Package p;
//		Branch br;
//		Priority priority=Priority.values()[r.nextInt(3)];
//		Address sender = new Address(r.nextInt(hub.getBranches().size()), r.nextInt(999999)+100000);
//		Address dest = new Address(r.nextInt(hub.getBranches().size()), r.nextInt(999999)+100000);
//
//		switch (r.nextInt(3)){
//		case 0:
//			p = new SmallPackage(priority,  sender, dest, r.nextBoolean() );
//			br = hub.getBranches().get(sender.zip);
//			br.addPackage(p);
//			p.setBranch(br); 
//			break;
//		case 1:
//			p = new StandardPackage(priority,  sender, dest, r.nextFloat()+(r.nextInt(9)+1),);
//			br = hub.getBranches().get(sender.zip); 
//			br.addPackage(p);
//			p.setBranch(br); 
//			break;
//		case 2:
//			p=new NonStandardPackage(priority,  sender, dest,  r.nextInt(1000), r.nextInt(500), r.nextInt(400));
//			hub.addPackage(p);
//			break;
//		default:
//			p=null;
//			return;
//		}
//		
//		this.packages.add(p);
//		
//	}
	
	
	public synchronized void setSuspend() {
	   	threadSuspend = true;
		for(Customer c : this.Customers)
	   		c.setSuspend();
		
		for (Truck t : hub.listTrucks) {
			t.setSuspend();
		}
		for (Branch b: hub.getBranches()) {
			for (Truck t : b.listTrucks) {
				t.setSuspend();
			}
			b.setSuspend();
		}
		hub.setSuspend();
	}

	
	
	public synchronized void setResume() {
	   	threadSuspend = false;
	   	notify();
	   	hub.setResume();
	   	for(Customer c : this.Customers)
	   		c.setResume();
	   	
		for (Truck t : hub.listTrucks) {
			t.setResume();
		}
		for (Branch b: hub.getBranches()) {
			b.setResume();
			for (Truck t : b.listTrucks) {
				t.setResume();;
			}
		}
	}

	public int getMaxPackages() {
		return maxPackages;
	}

	public void setMaxPackages(int maxPackages) {
		this.maxPackages = maxPackages;
	}

	public static int getFinishNum() {
		return FinishNum;
	}

	public static void setFinishNum(int finishNum) {
		FinishNum = finishNum;
	}
	



}
