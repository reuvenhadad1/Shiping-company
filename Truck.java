package components;

import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public  abstract class Truck implements Node,Runnable {
	private static int countID=2000;
	final private int truckID;
	private String licensePlate;
	private String truckModel;
	private boolean available=true;
	private int timeLeft=0;
	private ArrayList<Package> packages=new ArrayList<Package>();
	protected int initTime;
	protected boolean threadSuspend = false;
	 WriteReeLock lock ;	
	
	 
	//default random constructor
	public Truck(WriteReeLock l) {
		truckID=countID++;
		Random r= new Random();
		licensePlate=(r.nextInt(900)+100)+"-"+(r.nextInt(90)+10)+"-"+(r.nextInt(900)+100);
		truckModel="M"+r.nextInt(5);
		lock=l;
	}

	public Truck(String licensePlate,String truckModel) {
		truckID=countID++;
		this.licensePlate=licensePlate;
		this.truckModel=truckModel;
	}
	public Truck(String licensePlate,String truckModel,WriteReeLock l) {
		truckID=countID++;
		this.licensePlate=licensePlate;
		this.truckModel=truckModel;
	}
	
	
	public String getLicensePlate() {
		return licensePlate;
	}

	public String getTruckModel() {
		return truckModel;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public void setTruckModel(String truckModel) {
		this.truckModel = truckModel;
	}

	public ArrayList<Package> getPackages() {
		return packages;
	}


	public int getTimeLeft() {
		return timeLeft;
	}

	
	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}


	@Override
	public String toString() {
		return "truckID=" + truckID + ", licensePlate=" + licensePlate + ", truckModel=" + truckModel + ", available= " + available ;
	}
   

	@Override
	public synchronized void collectPackage(Package p) {
		setAvailable(false);
		int time=(p.getSenderAddress().street%10+1);
		this.setTimeLeft(time);
		this.initTime = time;
		this.packages.add(p);
		p.setStatus(Status.COLLECTION);
		Tracking tr=new Tracking(MainOffice.getClock(), this, p.getStatus(),p.getSender_ID());
		p.addTracking(tr);
		try {
	           lock.lockWrite();
	           WriteTracking("Package_ID :"+p.getPackageID()+"-"+tr.toString());
	           

	        } catch (InterruptedException e) {
	        	
	        }
	        finally{ try {
				lock.unlockWrite();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} }
		
		System.out.println(getName() + " is collecting package " + p.getPackageID() + ", time to arrive: "+ getTimeLeft()  );
	}


	@Override
	public synchronized void deliverPackage(Package p) throws InterruptedException {}


	public boolean isAvailable() {
		return available;
	}
	
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
	
	public int getTruckID() {
		return truckID;
	}

	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	
	public String getName() {
		return this.getClass().getSimpleName()+" "+ truckID;
	}
	
	public synchronized void setSuspend() {
	   	threadSuspend = true;
	}

	public synchronized void setResume() {
	   	threadSuspend = false;
	   	notify();
	}
	
	public abstract void paintComponent(Graphics g);
	
}
