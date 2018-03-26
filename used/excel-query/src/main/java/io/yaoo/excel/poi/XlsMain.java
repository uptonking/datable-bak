package io.yaoo.excel.poi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsMain {

//	public static void main(String[] args) throws IOException {
//		XlsDto xls = null;
//		String path = "/root/Documents/repo/hello-dataplayer/src/main/resources/xlsx/B.xlsx";
//		List<XlsDto> list = XlsMain.readXls(path);// 读取Excel文件
//		for (int i = 0; i < list.size(); i++) {
//			xls = (XlsDto) list.get(i);
//			System.out.println(xls.toString());
//		}
//	}

	/**
	 * 读取xls文件内容
	 *
	 * @return List<XlsDto>对象
	 * @throws IOException
	 *             输入/输出(i/o)异常
	 */
	public static List<XlsDto> readXls(String path) throws IOException {
		List<XlsDto> list = new LinkedList<XlsDto>();
		XSSFWorkbook xssfWorkbook = null;
		try {
			InputStream is = new FileInputStream(path);
			xssfWorkbook = new XSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xssfWorkbook != null) {
			for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
				XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
				if (xssfSheet == null) {
					continue;
				}
				// Read the Row
				for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
					XSSFRow xssfRow = xssfSheet.getRow(rowNum);
					if (xssfRow != null) {
						XlsDto dto = new XlsDto();
						XSSFCell timeColumn = xssfRow.getCell(0);
						XSSFCell columnA = xssfRow.getCell(1);
						XSSFCell columnB = xssfRow.getCell(2);
						dto.setTimeColumn(getValue(timeColumn));
						dto.setColumnA(getValue(columnA));
						dto.setColumnB(getValue(columnB));
						list.add(dto);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 得到Excel表中的值
	 *
	 * @param hssfCell
	 *            Excel中的每一个格子
	 * @return Excel中每一个格子中的值
	 */
	@SuppressWarnings("static-access")
	public static String getValue(XSSFCell hssfCell) {
	 String strCell = ""; 
		// 2017/8/5 17:24:00
		if (XSSFDateUtil.isCellDateFormatted(hssfCell)) {
			// 如果是date类型则 ，获取该cell的date值
			strCell = new SimpleDateFormat("yyyyMMddhhmmss").format(XSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue()));
			return strCell;
		}
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			// 返回字符串类型的值
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}
}
