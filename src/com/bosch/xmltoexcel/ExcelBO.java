package com.bosch.xmltoexcel;

public class ExcelBO {

	private String className;
	private String classType;
	private String ownerFc;
	private String ownerBc;
	private Integer classVarible;
	private Integer classParameter;
	private Integer classService;
	private Integer nestedClass;
	
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
}
