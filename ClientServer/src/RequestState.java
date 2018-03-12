import java.io.Serializable;


public class RequestState implements Serializable{
	
	private int rSeq =-2;
	private int sSeq =-2;
	private int val =-2;
	
	// Getters
	public int getrSeq() {
		return rSeq;
	}
	public int getsSeq() {
		return sSeq;
	}
	public int getVal() {
		return val;
	}
	
	// Setters
	public void setrSeq(int rSeq) {
		this.rSeq = rSeq;
	}
	public void setsSeq(int sSeq) {
		this.sSeq = sSeq;
	}
	public void setVal(int val) {
		this.val = val;
	}
			
}
