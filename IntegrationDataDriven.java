package com.testing.runner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;

import com.testing.app.DataDrivenOfApp;
import com.testing.autocommon.AutoLogger;
import com.testing.autocommon.ExcelReader;
import com.testing.autocommon.ExcelWriter;
import com.testing.autocommon.Report;
import com.testing.inter.DataDrivenOfInter;
import com.testing.inter.DataDrivenOfSoap;
import com.testing.web.DataDrivenOfWeb;

public class IntegrationDataDriven {
	public static DataDrivenOfWeb web;
	public static DataDrivenOfInter inter;
	public static DataDrivenOfSoap soap;
	public static DataDrivenOfApp app;
	public static String filepath;
	public static ExcelReader excelCase;
	public static ExcelWriter excelResult;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		 String type="web";
		 String type = args[0];
		// 获取项目的绝对路径
		filepath = System.getProperty("user.dir");
		// 拼接当前执行目录的路径
		String caseFile = filepath+"/Cases/";
		String resultFile = filepath+"/Cases/result-";
		//
		try {
			AutoLogger.log.info("当前运行路径为："+filepath); 
			String startTime = createDate("yyyyMMdd+HH-mm-ss");
			//通过输入的参数，决定执行的自动化类型，从而完成用例文件和结果文件的拼接,以及关键字对象的实例化
			switch (type) {
			case "web":
				caseFile += "WebCases.xlsx";
				resultFile += "WebCases" + startTime + ".xlsx";
				excelCase = new ExcelReader(caseFile);
				excelResult = new ExcelWriter(caseFile, resultFile);
				web =new DataDrivenOfWeb(excelResult);
				break;
			case "http":
				caseFile += "HTTPLogin.xlsx";
				resultFile += "HTTPLogin" + startTime + ".xlsx";
				excelCase = new ExcelReader(caseFile);
				excelResult = new ExcelWriter(caseFile, resultFile);
				inter = new DataDrivenOfInter(excelResult);
				break;
			case "app":
				caseFile += "APPCases.xlsx";
				resultFile += "APPCases" + startTime + ".xlsx";
				excelCase = new ExcelReader(caseFile);
				excelResult = new ExcelWriter(caseFile, resultFile);
				app = new DataDrivenOfApp(excelResult);
				break;
			case "soap":
				caseFile += "SOAPLogin.xlsx";
				resultFile += "SOAPLogin" + startTime + ".xlsx";
				excelCase = new ExcelReader(caseFile);
				excelResult = new ExcelWriter(caseFile, resultFile);
				soap = new DataDrivenOfSoap(excelResult);
				break;
			default:
				AutoLogger.log.error("自动化执行类型错误！请输入web、app、http或soap");
				break;
			}
			AutoLogger.log.info("用例文件路径：" + caseFile);
			//根据执行的类型来执行对应的自动化测试
			exeCase(type);
			//执行完成，发送邮件
			Report report=new Report();
			report.sendreport(resultFile, startTime);
		} catch (Exception e) {
			AutoLogger.log.error("获取文件位置失败，请检查。");
			e.printStackTrace();
		}
		
		AutoLogger.log.info(type+"类型自动化测试执行结束");

//		System.out.print("输入回车，退出...");
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	private static void exeCase(String type) {
		List<String> list = null;
		// 根据传入的用例和结果文件路径，实例化关键字类的成员变量
		for (int sheetNo = 0; sheetNo < 1; sheetNo++) {
			excelCase.useSheetByIndex(sheetNo);
			excelResult.useSheetByIndex(sheetNo);
			for (int line = 0; line < excelCase.rows; line++) {
				list = excelCase.readLine(line);
				AutoLogger.log.info(list);
				if ((list.get(0) != null || list.get(1) != null)
						&& (!list.get(0).equals("null") || !list.get(1).equals("null"))
						&& (list.get(0).length() > 0 || list.get(1).length() > 0)) {
					;
				} else {
					//根据不同的类型，完成关键字对象的实例化，以及关键字操作的执行。
					switch (type) {
					case "web":
						web.line = line;
						runWeb(list);
						break;
					case "http":
						inter.line = line;
						runHttp(list);
						break;
					case "soap":
						// 确定执行的行数
						soap.line = line;
						runSoap(list);
						break;
					case "app":
						app.line = line;
						invokeAppKey(list);
						break;
					default:
						break;
					}
				}
			}
		}
		excelCase.close();
		excelResult.save();
	}

	private static void runWeb(List<String> list) {
		try {
			switch (list.get(3)) {
			case "打开浏览器":
				web.openBrowser(list.get(4));
				break;
			case "visitWeb":
				web.visitWeb(list.get(4));
				break;
			case "input":
				web.input(list.get(4), list.get(5));
				break;
			case "click":
				web.click(list.get(4));
				break;
			case "intoIframe":
				web.intoIframe(list.get(4));
				break;
			case "selectByValue":
				web.selectByValue(list.get(4), list.get(5));
				break;
			case "closeOldWin":
				web.closeOldWin();
				break;
			case "halt":
				web.halt(list.get(4));
				break;
			case "assertcontains":
				web.assertTitleContains(list.get(4));
				break;
			case "closeBrowser":
				web.closeBrowser();
				break;
			case "login":
				web.login(list.get(4), list.get(5));
				break;
			}
		} catch (Exception e) {
			AutoLogger.log.error("执行web关键字时出错");
		}

	}

	private static void invokeWebkey(List<String> line) {
		//基于方法名获取参数为空的方法
		try {
			//获取对象对应的类中所有的方法。
			Method appMethod = web.getClass().getDeclaredMethod(line.get(3).toString());
			// invoke语法，需要输入类名以及相应的方法用到的参数
			appMethod.invoke(web);
			return;
		} catch (Exception e) {
		}
		//基于方法名获取带有一个参数的方法
		try {
			Method uis = web.getClass().getDeclaredMethod(line.get(3).toString(), String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(web, line.get(4));
			return;
		} catch (Exception e) {
		}
		//基于方法名获取带有两个参数的方法
		try {
			Method uis = web.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(web, line.get(4), line.get(5));
			return;
		} catch (Exception e) {
		}
		//基于方法名获取带有三个参数的方法
		try {
			Method uis = web.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class,
					String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(web, line.get(4), line.get(5), line.get(6));
			return;
		} catch (Exception e) {
		}
	}

	// 执行接口用例
	private static void runHttp(List<String> list) {
		// TODO Auto-generated method stub
		// 执行关键字相应操作
		try {
			// 通过Excel表中填写的关键字判断调用哪个方法执行
			switch (list.get(3)) {
			case "post":
				inter.testPost(list.get(4), list.get(5));
				break;
			case "get":
				inter.testGet(list.get(4), list.get(5));
				break;
			case "testPostRest":
				inter.testPostRest(list.get(4), list.get(5));
				break;
			case "savecookie":
				inter.saveCookie();
				break;
			case "clearcookie":
				inter.clearCookie();
				break;
			case "addHeader":
				inter.addHeader(list.get(4));
				break;
			case "saveParam":
				inter.saveParam(list.get(4), list.get(5));
				break;
			}
			// 通过excel表中填写的校验方法确定
			switch (list.get(7)) {
			case "equal":
				inter.assertSame(list.get(8), list.get(9));
				break;
			case "contain":
				inter.assertContains(list.get(8), list.get(9));
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void runSoap(List<String> list) {
		try {
			// 通过Excel表中填写的关键字判断调用哪个方法执行
			switch (list.get(3)) {
			case "createCon":
				soap.createCon(list.get(4));
				break;
			case "testSoap":
				soap.testSoap(list.get(4), list.get(5), list.get(6));
				break;
			case "addHeader":
				soap.addHeader(list.get(4));
				break;
			case "saveParam":
				soap.saveParam(list.get(4), list.get(5));
				break;
			}
			// 通过excel表中填写的校验方法确定
			switch (list.get(7)) {
			case "equal":
				soap.assertSame(list.get(8), list.get(9));
				break;
			case "contain":
				soap.assertContains(list.get(8), list.get(9));
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void invokeAppKey(List<String> line) {
		try {
			Method appMethod = app.getClass().getDeclaredMethod(line.get(3).toString());
			// invoke语法，需要输入类名以及相应的方法用到的参数
			appMethod.invoke(app);
			return;
		} catch (Exception e) {
		}
		try {
			Method uis = app.getClass().getDeclaredMethod(line.get(3).toString(), String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(app, line.get(4));
			return;
		} catch (Exception e) {
		}
		try {
			Method uis = app.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(app, line.get(4), line.get(5));
			return;
		} catch (Exception e) {
		}
		try {
			Method uis = app.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class,
					String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(app, line.get(4), line.get(5), line.get(6));
			return;
		} catch (Exception e) {
		}
		try {
			Method uis = app.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class,
					String.class, String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(app, line.get(4), line.get(5), line.get(6), line.get(7));
			return;
		} catch (Exception e) {
		}
		try {
			Method uis = app.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class,
					String.class, String.class, String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(app, line.get(4), line.get(5), line.get(6), line.get(7), line.get(8));
			return;
		} catch (Exception e) {
		}
		try {
			Method uis = app.getClass().getDeclaredMethod(line.get(3).toString(), String.class, String.class,
					String.class, String.class, String.class, String.class);
			// invoke语法，需要输入类名以及相应的方法用到的参数
			uis.invoke(app, line.get(4), line.get(5), line.get(6), line.get(7), line.get(8), line.get(9));
			return;
		} catch (Exception e) {
		}
	}

	private static String createDate(String dateFormat) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String createdate = sdf.format(date);
		return createdate;
	}
}
