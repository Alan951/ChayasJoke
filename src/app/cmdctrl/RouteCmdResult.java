package app.cmdctrl;

import java.io.Serializable;

public class RouteCmdResult implements Serializable {

	public Integer resultCode;
	public String result;
	public Object obj;
	
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

	@Override
	public String toString() {
		return "RouteCmdResult [resultCode=" + resultCode + ", result=" + result + ", obj=" + obj + "]";
	}
	
}
