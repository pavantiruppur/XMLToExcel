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
		List<ExcelBO> excelBoList = new ArrayList<>();
		final JFileChooser  fileDialog = new JFileChooser();
		fileDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		File file = null;
		if(fileDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ){
			file = fileDialog.getCurrentDirectory();
		}
		if(file == null){
			JOptionPane.showMessageDialog(null, "Program failed - Invalid path");
			return;
		}
		excelBoList = process.searchFileAndConvertToExcelList(file, excelBoList);
		process.writeStudentsListToExcel(excelBoList, XL_OUTOUT_LOCATION);
		JOptionPane.showMessageDialog(null, "Excel created successfully in "+ XL_OUTOUT_LOCATION);
	}

	public List<ExcelBO> searchFileAndConvertToExcelList(File file,
			List<ExcelBO> excelBoList) {
		if (file.isDirectory() && file.listFiles() != null) {
			for (File childFiles : file.listFiles()) {
				searchFileAndConvertToExcelList(childFiles, excelBoList);
			}
		} else if(file.getName().contains("_pavast.xml")){
			excelBoList.addAll(convertXmlToExcelList(file));
		}
		return excelBoList;
	}

	@SuppressWarnings("unchecked")
	public List<ExcelBO> convertXmlToExcelList(File file) {
		List<ExcelBO> excelBoList = Collections.EMPTY_LIST;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			if (doc.hasChildNodes()) {
				excelBoList = createExcelList(doc
						.getElementsByTagName("SW-CLASS"), file);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return excelBoList;

	}

	public List<ExcelBO> createExcelList(NodeList nodeList, File file) {
		List<ExcelBO> excelBoList = new ArrayList<ExcelBO>();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node swClassNode = nodeList.item(count);
			if (swClassNode.getNodeType() == Node.ELEMENT_NODE) {
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
				ExcelBO excelBo = new ExcelBO();
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

	public void writeStudentsListToExcel(List<ExcelBO> excelBoList,
			String outputLocation) {
		Workbook workbook = new XSSFWorkbook();
		Sheet studentsSheet = workbook.createSheet("Students");
		int rowIndex = 0;
		for (ExcelBO excelBo : excelBoList) {
			Row row = studentsSheet.createRow(rowIndex++);
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
}
