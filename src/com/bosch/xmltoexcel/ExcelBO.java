package com.bosch.xmltoexcel;

import java.util.ArrayList;
import java.util.List;

public class ExcelBO {

	private List<Sheet1BO> sheet1 = new ArrayList<Sheet1BO>();
	private List<Sheet2BO> sheet2 = new ArrayList<Sheet2BO>();
	
	public List<Sheet1BO> getSheet1() {
		return sheet1;
	}
	public void setSheet1(List<Sheet1BO> sheet1) {
		this.sheet1 = sheet1;
	}
	public List<Sheet2BO> getSheet2() {
		return sheet2;
	}
	public void setSheet2(List<Sheet2BO> sheet2) {
		this.sheet2 = sheet2;
	}
}
