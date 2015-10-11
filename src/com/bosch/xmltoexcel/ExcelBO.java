package com.bosch.xmltoexcel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcelBO {

	private Set<Sheet1BO> sheet1 = new HashSet<Sheet1BO>();
	private List<Sheet2BO> sheet2 = new ArrayList<Sheet2BO>();
	
	public Set<Sheet1BO> getSheet1() {
		return sheet1;
	}
	public void setSheet1(Set<Sheet1BO> sheet1) {
		this.sheet1 = sheet1;
	}
	public List<Sheet2BO> getSheet2() {
		return sheet2;
	}
	public void setSheet2(List<Sheet2BO> sheet2) {
		this.sheet2 = sheet2;
	}
}
