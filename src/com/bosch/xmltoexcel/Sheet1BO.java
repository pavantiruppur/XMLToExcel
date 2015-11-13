package com.bosch.xmltoexcel;

public class Sheet1BO {

	private String className = "";
	private String classType;
	private String ownerFc;
	private Integer ownedFcCount;
	private String ownerBc;
	private String parentOwnerBc;
	private String suParentOwnerBc;
	private Integer classVarible;
	private Integer classParameter;
	private Integer classService;
	private Integer nestedClass;
	private String importedFc;
	private String parentImportedFc;
	private String suParentImportedFc;
	private Integer importedFcCount;
	private String clsInstFileWithCount;
	private String parentClsInst;
	private String suParentClsInst;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getClassType() {
		return classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}
	public String getOwnerFc() {
		return ownerFc;
	}
	public void setOwnerFc(String ownerFc) {
		this.ownerFc = ownerFc;
	}
	public String getOwnerBc() {
		return ownerBc;
	}
	public void setOwnerBc(String ownerBc) {
		this.ownerBc = ownerBc;
	}
	public Integer getClassVarible() {
		return classVarible;
	}
	public void setClassVarible(Integer classVarible) {
		this.classVarible = classVarible;
	}
	public Integer getClassParameter() {
		return classParameter;
	}
	public void setClassParameter(Integer classParameter) {
		this.classParameter = classParameter;
	}
	public Integer getClassService() {
		return classService;
	}
	public void setClassService(Integer classService) {
		this.classService = classService;
	}
	public Integer getNestedClass() {
		return nestedClass;
	}
	public void setNestedClass(Integer nestedClass) {
		this.nestedClass = nestedClass;
	}
	public String getImportedFc() {
		return importedFc;
	}
	public void setImportedFc(String importedFc) {
		this.importedFc = importedFc;
	}
	public Integer getOwnedFcCount() {
		return ownedFcCount;
	}
	public void setOwnedFcCount(Integer ownedFcCount) {
		this.ownedFcCount = ownedFcCount;
	}
	public Integer getImportedFcCount() {
		return importedFcCount;
	}
	public void setImportedFcCount(Integer importedFcCount) {
		this.importedFcCount = importedFcCount;
	}
	public String getParentOwnerBc() {
		return parentOwnerBc;
	}
	public void setParentOwnerBc(String parentOwnerBc) {
		this.parentOwnerBc = parentOwnerBc;
	}
	public String getSuParentOwnerBc() {
		return suParentOwnerBc;
	}
	public void setSuParentOwnerBc(String suParentOwnerBc) {
		this.suParentOwnerBc = suParentOwnerBc;
	}
	public String getParentImportedFc() {
		return parentImportedFc;
	}
	public void setParentImportedFc(String parentImportedFc) {
		this.parentImportedFc = parentImportedFc;
	}
	public String getSuParentImportedFc() {
		return suParentImportedFc;
	}
	public void setSuParentImportedFc(String suParentImportedFc) {
		this.suParentImportedFc = suParentImportedFc;
	}
	public String getClsInstFileWithCount() {
		return clsInstFileWithCount;
	}
	public void setClsInstFileWithCount(String clsInstFileWithCount) {
		this.clsInstFileWithCount = clsInstFileWithCount;
	}
	public String getParentClsInst() {
		return parentClsInst;
	}
	public void setParentClsInst(String parentClsInst) {
		this.parentClsInst = parentClsInst;
	}
	public String getSuParentClsInst() {
		return suParentClsInst;
	}
	public void setSuParentClsInst(String suParentClsInst) {
		this.suParentClsInst = suParentClsInst;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Sheet1BO)){
			return false;
		}
		Sheet1BO sheet = (Sheet1BO) obj;
		return sheet.getClassName().equals(this.getClassName());
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
