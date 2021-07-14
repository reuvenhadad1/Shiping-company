package components;

public class SmallPackage extends Package {
	private boolean acknowledge;
	
	
	public SmallPackage(Priority priority, Address senderAddress,Address destinationAdress, boolean acknowledge,long Sid){
		super(priority, senderAddress,destinationAdress, Sid);
		this.acknowledge=acknowledge;
		System.out.println("Creating " + this);

	}
	
	
	public boolean isAcknowledge() {
		return acknowledge;
	}
	
	
	public void setAcknowledge(boolean acknowledge) {
		this.acknowledge = acknowledge;
	}
	
	
	@Override
	public String toString() {
		return "SmallPackage ["+ super.toString() +", acknowledge=" + acknowledge + "]";
	}
	
		
}
