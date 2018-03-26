package io.yaoo.excel;

import io.yaoo.excel.poi.XlsDto;
import io.yaoo.excel.poi.XlsMain;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class QueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private List<XlsDto> listA = new LinkedList<XlsDto>();
    private List<XlsDto> listB = new LinkedList<XlsDto>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String startTime = req.getParameter("startTime");
        String endTime = req.getParameter("endTime");
        String querySign = req.getParameter("querySign");

        if ("a".equals(querySign)) {
            String realPath = req.getServletContext().getRealPath("/") + "resources/xlsx/A.xlsx";
            listA = XlsMain.readXls(realPath);// 读取Excel文件
        } else if ("b".equals(querySign)) {
            String realPathb = req.getServletContext().getRealPath("/") + "resources/xlsx/B.xlsx";
            listB = XlsMain.readXls(realPathb);// 读取Excel文件
        } else {
            String realPath_a = req.getServletContext().getRealPath("/") + "resources/xlsx/A.xlsx";
            listA = XlsMain.readXls(realPath_a);// 读取Excel文件
            String realPath_b = req.getServletContext().getRealPath("/") + "resources/xlsx/B.xlsx";
            listB = XlsMain.readXls(realPath_b);// 读取Excel文件
        }


        String str = "";
        if ("a".equals(querySign)) {
            Integer[] arr_A = sum(listA, startTime, endTime);
            str = "[{'sumColTwo':'" + arr_A[0] + "','sumColThree':'" + arr_A[1] + "'},{'sumColTwo':'0','sumColThree':'0'},{'sumColTwo':'0','sumColThree':'0'}]";
        } else if ("b".equals(querySign)) {
            Integer[] arr_B = sum(listB, startTime, endTime);
            str = "[{'sumColTwo':'0','sumColThree':'0'},{'sumColTwo':'" + arr_B[0] + "','sumColThree':'" + arr_B[1] + "'},{'sumColTwo':'0','sumColThree':'0'}]";
        } else {
            Integer[] arrA = sum(listA, startTime, endTime);
            Integer[] arrB = sum(listB, startTime, endTime);
            int absumColTwo = arrA[0] + arrB[0];
            int absumColThree = arrA[1] + arrB[1];
            str = "[{'sumColTwo':'" + arrA[0] + "','sumColThree':'" + arrA[1] + "'},{'sumColTwo':'" + arrB[0] + "','sumColThree':'" + arrB[1] + "'},{'sumColTwo':'" + absumColTwo + "','sumColThree':'" + absumColThree + "'}]";
        }
//        resp.setContentType("application/json");
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();

        out.println(str);
        out.flush();
        out.close();
    }

    public static Integer[] sum(List<XlsDto> list, String startTime, String endTime) {
        Integer[] arr = new Integer[2];
        Integer sumColTwo = 0;
        Integer sumColThree = 0;
        for (int i = 0; i < list.size(); i++) {
            XlsDto xls = (XlsDto) list.get(i);
            String date_CurStr = xls.getTimeColumn();
            Date date_Cur;
            Date startDate;
            Date endDate;
            try {
                date_Cur = DateUtil.parseDate(date_CurStr, "yyyyMMddhhmmss");
                startDate = DateUtil.parseDate(startTime, "yyyyMMddhhmmss");
                endDate = DateUtil.parseDate(endTime, "yyyyMMddhhmmss");
                if (DateUtil.isBetweenTime(date_Cur, startDate, endDate)) {
                    String a = xls.getColumnA();
                    String b = xls.getColumnB();
                    if (a.contains(".0")) {
                        a = a.substring(0, a.length() - 2);
                    }
                    if (b.contains(".0")) {
                        b = b.substring(0, b.length() - 2);
                    }
                    sumColTwo = sumColTwo + Integer.valueOf(a);
                    sumColThree = sumColThree + Integer.valueOf(b);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        arr[0] = sumColTwo;
        arr[1] = sumColThree;
        return arr;
    }
}
