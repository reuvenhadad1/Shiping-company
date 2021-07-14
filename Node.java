package components;

public interface Node {
	public void collectPackage(Package p);
	public void deliverPackage(Package p) throws InterruptedException;
	public void work();
	public String getName();
    public abstract Node copy();

}
