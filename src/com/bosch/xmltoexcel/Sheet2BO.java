package com.bosch.xmltoexcel;

import java.util.Map;


public class Sheet2BO {

	private String exportClasses;
	private String importClasses;
	private String ownedClasses;
	private String fcName;
	private String parentFcName;
	private String suParentFcName;
	private Map<String, Integer> clsInstCount;
	
	public String getExportClasses() {
		return exportClasses;
	}
	public void setExportClasses(String exportClasses) {
		this.exportClasses = exportClasses;
	}
	public String getImportClasses() {
		return importClasses;
	}
	public void setImportClasses(String importClasses) {
		this.importClasses = importClasses;
	}
	public String getOwnedClasses() {
		return ownedClasses;
	}
	public void setOwnedClasses(String ownedClasses) {
		this.ownedClasses = ownedClasses;
	}
	public String getFcName() {
		return fcName;
	}
	public void setFcName(String fcName) {
		this.fcName = fcName;
	}
	public String getParentFcName() {
		return parentFcName;
	}
	public void setParentFcName(String parentFcName) {
		this.parentFcName = parentFcName;
	}
	public String getSuParentFcName() {
		return suParentFcName;
	}
	public void setSuParentFcName(String suParentFcName) {
		this.suParentFcName = suParentFcName;
	}
	public Map<String, Integer> getClsInstCount() {
		return clsInstCount;
	}
	public void setClsInstCount(Map<String, Integer> clsInstCount) {
		this.clsInstCount = clsInstCount;
	}
}
