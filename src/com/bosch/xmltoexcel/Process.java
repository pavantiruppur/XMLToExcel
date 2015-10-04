package com.bosch.xmltoexcel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		process.writeStudentsListToExcel(excelBO, XL_OUTOUT_LOCATION);
		JOptionPane.showMessageDialog(null, "Excel created successfully in "+ XL_OUTOUT_LOCATION);
	}

	public ExcelBO searchFileAndConvertToExcelList(File file, ExcelBO excelBO) {
		if (file.isDirectory() && file.listFiles() != null) {
			for (File childFiles : file.listFiles()) {
				searchFileAndConvertToExcelList(childFiles, excelBO);
			}
		} else if(file.getName().contains("_pavast.xml")){
			convertXmlToExcelList(file, excelBO);
		}
		return excelBO;
	}

	@SuppressWarnings("unchecked")
	public ExcelBO convertXmlToExcelList(File file, ExcelBO excelBO) {
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
				excelBoList = createExcelList(doc.getElementsByTagName("SW-CLASS"), file);
				excelBO.getSheet1().addAll(excelBoList);
				
				Sheet2BO sheet2 = new Sheet2BO();
				String fileName = file.getName().split("_pavast.xml")[0];
				sheet2.setFcName(fileName);
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

	public List<Sheet1BO> createExcelList(NodeList nodeList, File file) {
		List<Sheet1BO> excelBoList = new ArrayList<Sheet1BO>();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node swClassNode = nodeList.item(count);
			if (swClassNode.getNodeType() == Node.ELEMENT_NODE && swClassNode.getParentNode().getNodeName().equals("SW-COMPONENTS")) {
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
				excelBo.setClassName(shortName.getTextContent());
				excelBo.setClassType(swImplPolicy.getTextContent());
				excelBo.setClassVarible(variables.getLength());
				excelBo.setClassParameter(prameters.getLength());
				excelBo.setClassService(service.getLength());
				excelBo.setNestedClass(innerSwClass.getLength());
				excelBo.setOwnerFc(file.getName());
				File parentFile = file.getParentFile().getParentFile();
				excelBo.setOwnerBc(parentFile != null ? parentFile.getName() : null);
				excelBoList.add(excelBo);
			}
		}
		return excelBoList;
	}

	public void writeStudentsListToExcel(ExcelBO excelBO,
			String outputLocation) {
		List<Sheet1BO> excelBoList = excelBO.getSheet1();
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet1 = workbook.createSheet("Sheet1");
		int rowIndex = 0;
		for (Sheet1BO excelBo : excelBoList) {
			Row row = sheet1.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(excelBo.getClassName());
			row.createCell(cellIndex++).setCellValue(excelBo.getClassType());
			row.createCell(cellIndex++).setCellValue(excelBo.getOwnerFc());
			row.createCell(cellIndex++).setCellValue(excelBo.getOwnerBc());
			row.createCell(cellIndex++).setCellValue(excelBo.getClassVarible());
			row.createCell(cellIndex++).setCellValue(
					excelBo.getClassParameter());
			row.createCell(cellIndex++).setCellValue(excelBo.getClassService());
			row.createCell(cellIndex++).setCellValue(excelBo.getNestedClass());
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
