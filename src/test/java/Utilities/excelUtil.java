package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class excelUtil {
	
	public static FileInputStream fi;
	public static FileOutputStream fo;
	public static XSSFWorkbook wb;
	public static XSSFSheet ws;
	public static XSSFRow row;
	public static XSSFCell cell;
		
	public static int getRowCount(String xlfile, String xlsheet) throws IOException
	
	{
		fi= new FileInputStream(xlfile);
		wb = new XSSFWorkbook(fi);
		ws = wb.getSheet(xlsheet);
		int rowcount = ws.getLastRowNum();
		wb.close();
		fi.close();
		return rowcount;
	}

	public static int getCellCount(String xlfile, String xlsheet, int rownum) throws IOException
	
	{
		fi= new FileInputStream(xlfile);
		wb = new XSSFWorkbook(fi);
		ws = wb.getSheet(xlsheet);
		row = ws.getRow(rownum);
		int cellcount = row.getLastCellNum();
		wb.close();
		fi.close();
		return cellcount;
	}

	public static String getCellData(String xlfile, String xlsheet, int rownum, int colnum) throws IOException
	
	{
		fi= new FileInputStream(xlfile);
		wb = new XSSFWorkbook(fi);
		ws = wb.getSheet(xlsheet);
		row = ws.getRow(rownum);
		cell = row.getCell(colnum);
		
		DataFormatter formatter = new DataFormatter();
		String data;
		try {
			data = formatter.formatCellValue(cell);
		}
		catch(Exception e) {
			data = "";
		}
		wb.close();
		fi.close();
		return data;
	}
	
	public static int setCellData(String xlfile, String xlsheet, int rownum, int colnum, int id) throws IOException
	
	{
		
		fi= new FileInputStream(xlfile);
		wb = new XSSFWorkbook(fi);
		ws = wb.getSheet(xlsheet);
		//System.out.println("the sheet is:" + xlsheet);
		row = ws.getRow(rownum);
	//	System.out.println("the row is:" + rownum);
		cell = row.getCell(colnum);
		//System.out.println("the cell is:" + colnum);
		cell.setCellValue(id);
		fo= new FileOutputStream(xlfile);
		wb.write(fo);
		wb.close();
		fi.close();
		fo.close();
		return id;
		
		
	}
	
	public void WriteData_Excel(String dataPath,String sheetname,Object writethis,int row,int col,CellType type) throws IOException{
		  
		  File excelFile = new File(dataPath);
		  FileInputStream file = new FileInputStream(excelFile);
		  XSSFWorkbook workbook = new XSSFWorkbook(file);
		  //XSSFSheet sheet = workbook.getSheetAt(0);
		  XSSFSheet sheet = workbook.getSheet(sheetname);
		  //CellType.STRING
		//  XSSFRow row1 = sheet.createRow(row);
		 // Cell cell= row1.createCell(col);
		 // cell.setCellValue(writethis.toString());
		  sheet.getRow(row).createCell(col).setCellType(type);
		  //sheet.getRow(1).createCell(4).setCellType(CellType.NUMERIC);
		  //sheet.getRow(1).createCell(5).setCellType(CellType.STRING);
		  sheet.getRow(row).getCell(col).setCellValue(writethis.toString());
		  //sheet.getRow(1).getCell(4).setCellValue(resp.statusCode());
		  //sheet.getRow(1).getCell(5).setCellValue(resp.asString());
		  
		  FileOutputStream outFile = new FileOutputStream(excelFile);
		  workbook.write(outFile);
		  workbook.close();
		  outFile.close();
		  file.close();
		 }

}


