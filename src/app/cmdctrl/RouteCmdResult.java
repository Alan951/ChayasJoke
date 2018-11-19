package app.cmdctrl;

public class RouteCmdResult {

	public Integer resultCode;
	public String result;
	
	public RouteCmdResult(Integer resultCode) {
		this.resultCode = resultCode;
	}
	
	public RouteCmdResult(String result) {
		this.result = result;
	}
	
	public RouteCmdResult(Integer resultCode, String result) {
		this.result = result;
		this.resultCode = resultCode;
	}
	
}
