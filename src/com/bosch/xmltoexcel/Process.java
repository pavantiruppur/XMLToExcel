package com.bosch.xmltoexcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Process {

	private static final String XL_OUTOUT_LOCATION = "D:/testOutPut.xlsx";

	public static void main(String[] args) {
		Process process = new Process();
		final JFileChooser  fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		File file = null;
		if(fileDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ){
			System.out.println(fileDialog.getSelectedFile().getAbsolutePath());
			file = fileDialog.getSelectedFile();
		}
		if(file == null){
			JOptionPane.showMessageDialog(null, "Program failed - Invalid path");
			return;
		}
		ExcelBO excelBO = new ExcelBO();
		excelBO = process.searchFileAndConvertToExcelList(file, excelBO);
		excelBO = process.findOwnedAndImport(excelBO);
		process.writeStudentsListToExcel(excelBO, XL_OUTOUT_LOCATION);
		JOptionPane.showMessageDialog(null, "Excel created successfully in "+ XL_OUTOUT_LOCATION);
	}
	
	public ExcelBO findOwnedAndImport(ExcelBO excelBO){
		Map<String, Map<String, Integer>> ownedModifierMap = new HashMap<String, Map<String,Integer>>();
		Map<String, Map<String, Integer>> impModifierMap = new HashMap<String, Map<String,Integer>>();
		for(Sheet1BO sheet1 : excelBO.getSheet1()){
			String ownedFc = "";
			String parentOwnedFc = "";
			String suParentOwnedFc = "";
			String importedFc = "";
			String parentImportedFc = "";
			String suParentImportedFc = "";
			int ownedFcCount = 0;
			int importFcCount = 0;
			for(Sheet2BO sheet2 : excelBO.getSheet2()){
				if(sheet2.getOwnedClasses() != null && sheet2.getOwnedClasses().contains(sheet1.getClassName() + "\n")){
					ownedFc += sheet2.getFcName() + "\n";
					parentOwnedFc += sheet2.getParentFcName() + "\n";
					suParentOwnedFc += sheet2.getSuParentFcName() + "\n";
					ownedFcCount++;
				}
				if(sheet2.getImportClasses() != null && sheet2.getImportClasses().contains(sheet1.getClassName() + "\n")){
					importedFc += sheet2.getFcName() + "\n";
					parentImportedFc += sheet2.getParentFcName() + "\n";
					suParentImportedFc += sheet2.getSuParentFcName() + "\n";
					importFcCount++;
				}
			}
			if(ownedFcCount > 0){
				sheet1.setOwnerFc(ownedFc);
				sheet1.setParentOwnerBc(parentOwnedFc);
				sheet1.setSuParentOwnerBc(suParentOwnedFc);
			}
			sheet1.setImportedFc(importedFc);
			sheet1.setParentImportedFc(parentImportedFc);
			sheet1.setSuParentImportedFc(suParentImportedFc);
			sheet1.setOwnedFcCount(ownedFcCount);
			sheet1.setImportedFcCount(importFcCount);
			
			Map<String, Integer> ownedCountMap = ownedModifierMap.get(suParentOwnedFc);
			if(ownedCountMap == null){
				ownedCountMap = new HashMap<String, Integer>();
				ownedModifierMap.put(suParentOwnedFc, ownedCountMap);
			}
			
			Integer modifierCount = ownedCountMap.get(sheet1.getClassType());
			if(modifierCount == null){
				modifierCount = 0;
			}
			
			modifierCount++;
			ownedCountMap.put(sheet1.getClassType(), modifierCount);
			
			String[] impArr = suParentImportedFc.split("\n");
			for(String imp : impArr){
				Map<String, Integer> impCountMap = impModifierMap.get(imp);
				if(impCountMap == null){
					impCountMap = new HashMap<String, Integer>();
					impModifierMap.put(imp, impCountMap);
				}
				
				Integer impModifierCount = impCountMap.get(sheet1.getClassType());
				if(impModifierCount == null){
					impModifierCount = 0;
				}
				
				impModifierCount++;
				impCountMap.put(sheet1.getClassType(), impModifierCount);
			}
		}
		excelBO.setOwnedModifierMap(ownedModifierMap);
		excelBO.setImpModifierMap(impModifierMap);
		return excelBO;
	}

	public ExcelBO searchFileAndConvertToExcelList(File file, ExcelBO excelBO) {
		if (file.isDirectory() && file.listFiles() != null) {
			for (File childFiles : file.listFiles()) {
				searchFileAndConvertToExcelList(childFiles, excelBO);
			}
		} else if(file.getName().contains("_pavast.xml") || file.getName().contains("_auto_pavast.xml")){
			convertXmlToExcelList(file, excelBO);
		}
		return excelBO;
	}

	@SuppressWarnings("unchecked")
	public ExcelBO convertXmlToExcelList(File file, ExcelBO excelBO) {
		Set<Sheet1BO> excelBoList = Collections.EMPTY_SET;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			if (doc.hasChildNodes()) {
				excelBoList = createExcelList(doc.getElementsByTagName("SW-CLASS"), file);
				excelBO.getSheet1().addAll(excelBoList);
				
				Sheet2BO sheet2 = new Sheet2BO();
				String fileName = "";
				if(file.getName().contains("_auto_pavast.xml")){
					fileName = file.getName().split("_auto_pavast.xml")[0];
				} else {
					fileName = file.getName().split("_pavast.xml")[0];
				}
				String parentFile = file.getParentFile().getParentFile() != null ? file.getParentFile().getParentFile().getName() : "";
				sheet2.setFcName(fileName);
				sheet2.setParentFcName(file.getParentFile().getName());
				sheet2.setSuParentFcName(parentFile);
				NodeList interfaceNodes = doc.getElementsByTagName("SW-FEATURE-INTERFACE");
				for(int i = 0; i < interfaceNodes.getLength(); i++){
					Element temp = (Element) interfaceNodes.item(i);
					NodeList tempNodeList = temp.getElementsByTagName("SHORT-NAME");
					String shortName = tempNodeList.getLength() > 0 ? tempNodeList.item(0).getTextContent() : null;
					if(shortName.equalsIgnoreCase(fileName + "_Ex")){
						sheet2.setExportClasses(getListOfElementValue(temp, "SW-CLASS-REF"));
					} else if(shortName.equalsIgnoreCase(fileName + "_Im")){
						sheet2.setImportClasses(getListOfElementValue(temp, "SW-CLASS-REF"));
					}
				}
				
				NodeList feartureNodes = doc.getElementsByTagName("SW-FEATURE");
				for(int i = 0; i < feartureNodes.getLength(); i++){
					Element temp = (Element) feartureNodes.item(i);
					NodeList tempNodeList = temp.getElementsByTagName("CATEGORY");
					String shortName = tempNodeList.getLength() > 0 ? tempNodeList.item(0).getTextContent() : null;
					if(shortName.equalsIgnoreCase("FCT")){
						temp = (Element)temp.getElementsByTagName("SW-FEATURE-OWNED-ELEMENTS").item(0);
						sheet2.setOwnedClasses(getListOfElementValue(temp, "SW-CLASS-REF"));
					}
				}
				excelBO.getSheet2().add(sheet2);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return excelBO;
	}
	
	@SuppressWarnings("unchecked")
	public List<Sheet1BO> convertXmlToExcelListSheet2(File file) {
		List<Sheet1BO> excelBoList = Collections.EMPTY_LIST;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			if (doc.hasChildNodes()) {
				excelBoList = createExcelListSheet2(doc
						.getElementsByTagName("SW-FEATURE-INTERFACE"), file);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return excelBoList;

	}

	public List<Sheet1BO> createExcelListSheet2(NodeList nodeList, File file) {
		List<Sheet1BO> excelBoList = new ArrayList<Sheet1BO>();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node swClassNode = nodeList.item(count);
			if (swClassNode.getNodeType() == Node.ELEMENT_NODE) {
				Element swClass = (Element) swClassNode;
				Element shortName;
				NodeList tempNodeList = swClass
						.getElementsByTagName("SHORT-NAME");
				shortName = (Element) (tempNodeList.getLength() > 0 ? tempNodeList
						.item(0) : null);
				
				System.out.println(shortName.getTextContent());
			}
		}
		return excelBoList;
	}

	public Set<Sheet1BO> createExcelList(NodeList nodeList, File file) {
		Set<Sheet1BO> excelBoList = new HashSet<Sheet1BO>();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node swClassNode = nodeList.item(count);
			if (swClassNode.getNodeType() == Node.ELEMENT_NODE && (swClassNode.getParentNode().getNodeName().equals("SW-COMPONENTS")
					|| swClassNode.getParentNode().getNodeName().equals("SW-CLASSES"))) {
				Element swClass = (Element) swClassNode;
				NodeList tempNodeList = swClass
						.getElementsByTagName("SHORT-NAME");
				Element shortName = (Element) (tempNodeList.getLength() > 0 ? tempNodeList
						.item(0) : null);
				tempNodeList = swClass.getElementsByTagName("SW-IMPL-POLICY");
				Element swImplPolicy = (Element) (tempNodeList.getLength() > 0 ? tempNodeList
						.item(0) : null);
				NodeList variables = swClass
						.getElementsByTagName("SW-VARIABLE-PROTOTYPE");
				NodeList prameters = swClass
						.getElementsByTagName("SW-CALPRM-PROTOTYPE");
				NodeList service = swClass
						.getElementsByTagName("SW-SERVICE-PROTOTYPE");
				NodeList innerSwClass = swClass
						.getElementsByTagName("SW-CLASS");
				Sheet1BO excelBo = new Sheet1BO();
				excelBo.setClassName(shortName == null ? null : shortName.getTextContent());
				excelBo.setClassType(swImplPolicy == null ? null : swImplPolicy.getTextContent());
				excelBo.setClassVarible(variables == null ? null : variables.getLength());
				excelBo.setClassParameter(prameters == null ? null : prameters.getLength());
				excelBo.setClassService(service == null ? null : service.getLength());
				excelBo.setNestedClass(innerSwClass == null ? null : innerSwClass.getLength());
				excelBo.setOwnerFc(file == null ? null : file.getName());
				File parentFile = file.getParentFile().getParentFile();
				excelBo.setOwnerBc(parentFile != null ? parentFile.getName() : null);
				excelBo.setParentOwnerBc(file.getParentFile().getName());
				excelBo.setSuParentOwnerBc(parentFile != null ? parentFile.getName() : null);;
				excelBoList.add(excelBo);
			}
		}
		return excelBoList;
	}

	public void writeStudentsListToExcel(ExcelBO excelBO,
			String outputLocation) {
		Set<Sheet1BO> excelBoList = excelBO.getSheet1();
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet1 = workbook.createSheet("Sheet1");
		int rowIndex = 0;
		for (Sheet1BO excelBo : excelBoList) {
			Row row = sheet1.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(excelBo.getClassName());
			row.createCell(cellIndex++).setCellValue(excelBo.getClassType());
			row.createCell(cellIndex++).setCellValue(excelBo.getSuParentOwnerBc());
			row.createCell(cellIndex++).setCellValue(excelBo.getParentOwnerBc());
			row.createCell(cellIndex++).setCellValue(excelBo.getOwnerFc());
			row.createCell(cellIndex++).setCellValue(excelBo.getOwnedFcCount());
			row.createCell(cellIndex++).setCellValue(excelBo.getOwnerBc());
			row.createCell(cellIndex++).setCellValue(excelBo.getClassVarible());
			row.createCell(cellIndex++).setCellValue(
					excelBo.getClassParameter());
			row.createCell(cellIndex++).setCellValue(excelBo.getClassService());
			row.createCell(cellIndex++).setCellValue(excelBo.getNestedClass());
			row.createCell(cellIndex++).setCellValue(excelBo.getImportedFcCount() > 0 ? excelBo.getSuParentImportedFc() : "Nill");
			row.createCell(cellIndex++).setCellValue(excelBo.getImportedFcCount() > 0 ? excelBo.getParentImportedFc() : "Nill");
			row.createCell(cellIndex++).setCellValue(excelBo.getImportedFcCount() > 0 ? excelBo.getImportedFc() : "Nill");
			row.createCell(cellIndex++).setCellValue(excelBo.getImportedFcCount());
		}
		
		List<Sheet2BO> sheet2BOlist = excelBO.getSheet2();
		Sheet sheet2 = workbook.createSheet("Sheet2");
		rowIndex = 0;
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		for (Sheet2BO sheet2BO : sheet2BOlist) {
			Row row = sheet2.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(sheet2BO.getFcName());
			Cell cell = row.createCell(cellIndex++);
			cell.setCellStyle(cs);
			cell.setCellValue(sheet2BO.getExportClasses());
			cell = row.createCell(cellIndex++);
			cell.setCellStyle(cs);
			cell.setCellValue(sheet2BO.getImportClasses());
			cell = row.createCell(cellIndex++);
			cell.setCellStyle(cs);
			cell.setCellValue(sheet2BO.getOwnedClasses());
		}
		
		Sheet sheet3 = workbook.createSheet("Sheet3");
		Map<String, Map<String, Integer>> ownedCountMap = excelBO.getOwnedModifierMap();
		rowIndex = 0;
		for (Entry<String, Map<String, Integer>> ownedMap : ownedCountMap.entrySet()) {
			String value = "";
			for(Entry<String, Integer> countMap : ownedMap.getValue().entrySet()){
				value += countMap.getKey() + " - " + countMap.getValue() +"\n";
			}
			Row row = sheet3.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(ownedMap.getKey());
			row.createCell(cellIndex++).setCellValue(value);
		}
		
		Sheet sheet4 = workbook.createSheet("Sheet4");
		Map<String, Map<String, Integer>> impCountMap = excelBO.getImpModifierMap();
		rowIndex = 0;
		for (Entry<String, Map<String, Integer>> ownedMap : impCountMap.entrySet()) {
			String value = "";
			for(Entry<String, Integer> countMap : ownedMap.getValue().entrySet()){
				value += countMap.getKey() + " - " + countMap.getValue() +"\n";
			}
			Row row = sheet4.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(ownedMap.getKey());
			row.createCell(cellIndex++).setCellValue(value);
		}
		try {
			FileOutputStream fos = new FileOutputStream(outputLocation);
			workbook.write(fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getListOfElementValue(Element element, String tagName){
		String elementValues = new String();
		if(element == null){
			return elementValues;
		}
		NodeList nodeList = element.getElementsByTagName(tagName);
		for (int count = 0; count < nodeList.getLength(); count++) {
			elementValues += nodeList.item(count).getTextContent().trim() +"\n";
		}
		return elementValues;
	}
}
