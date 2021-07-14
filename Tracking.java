package components;

public class Tracking {
	public final int time;
	public final Node node;
	public final Status status;
	private static int countID=0;
	final private int trackingID;
	private long SenderID;
	public Tracking(int time, Node node, Status status) {
		super();
		this.trackingID=countID++;
		this.time = time;
		this.node = node;
		this.status = status;
	}
	public Tracking(int time, Node node, Status status,long s) {
		super();
		this.trackingID=++countID;
		this.time = time;
		this.node = node;
		this.status = status;
		SenderID=s;
	}

	
	@Override
	public String toString() {
		String name = (node==null)? "Customer" : node.getName();
		return " -Position: " + name + ", status=" + status+" ,Senders ID :"+SenderID +" , #Record#"+trackingID;
	}

	
	
}
