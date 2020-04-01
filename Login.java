package com.testing.login;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.testing.class11.LoginSample;
import com.testing.mysql.ConnectMysql;
import com.testing.mysql.UseMysql;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	// 构造方法
	public Login() {
		// 调用父类构造方法
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// 设置返回内容的编码格式
		response.setContentType("text/html;charset=UTF-8");
		// 请求参数的编码
		request.setCharacterEncoding("UTF-8");

		System.out.println("走get方法");
		// 从请求中获取loginName和passWord参数。
		String user = request.getParameter("loginName");
		String pwd = request.getParameter("passWord");

		// 调用数据库检查数据
		ConnectMysql csql = new ConnectMysql();
		UseMysql mySql = new UseMysql(csql.conn);
		boolean loginResult = mySql.Login(user, pwd);

		String responseResult = "{";
//		boolean loginResult = mySql.PLogin(user, pwd);
		if (loginResult) {
			responseResult += "\"status\":200,\"msg\":\"恭喜您，登录成功!11\"}";
		} else {
			responseResult += "\"status\":201,\"msg\":\"登录失败，用户名密码错误！\"}";
		}

		System.out.println(responseResult);
		response.getWriter().append(responseResult);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		// 请求参数的编码
		request.setCharacterEncoding("UTF-8");
		System.out.println("走post方法");
		// 获取sessionid
		String sessionId = request.getSession().getId();
		System.out.println("sessionId是" + sessionId);
		//设置seesion的有效时间
		//没有设置cookeie的有效时间，所以浏览器的有效时间还是默认的，关闭浏览器后有效时间立即失效
		request.getSession().setMaxInactiveInterval(18000);
		// 从请求中获取loginName和passWord参数。
		String user = request.getParameter("loginName");
		String pwd = request.getParameter("passWord");
		String regexU = "[^a-zA-Z0-9]";
		// 创建模板进行匹配
		Pattern p = Pattern.compile(regexU);
		// 使用匹配器匹配目标和ipResult中的内容
		Matcher mu = p.matcher(user);
		Matcher mp = p.matcher(pwd);

//		//调用login的方法进行登录验证
//		LoginSample loginClass=new LoginSample();
//		boolean loginResult=loginClass.login(user, pwd);
		String responseResult = "{";
		if (user != null && pwd != null && !user.equals("") && !pwd.equals("")) {
			// 限制用户名和密码长度3-8位
			if (user.length() > 2 && user.length() < 9 && pwd.length() > 2 && pwd.length() < 9) {
				// 判断是否含有特殊字符
				if (!mu.find() && !mp.find()) {
					// 如果session中没有记录loginName，则说明没有登录，可以进行正常的登录
					if (request.getSession().getAttribute("loginName") == null) {
						// 调用数据库检查数据
						ConnectMysql csql = new ConnectMysql();
						UseMysql mySql = new UseMysql(csql.conn);
						boolean loginResult = mySql.Login(user, pwd);
						System.out.println("loginResult" + loginResult);

//		boolean loginResult = mySql.PLogin(user, pwd);
						if (loginResult) {
							responseResult += "\"status\":200,\"msg\":\"恭喜您，登录成功!\"}";
							// 如果登录成功通过了校验，就在服务器记录登录的用户名
							request.getSession().setAttribute("loginName", user);
							//返回给客户端一个cookie，记录的是本次的sessionID（也就是房间号）,
							//名称和servlet默认返回的cookie的名字一致
							Cookie ssID=new Cookie("JSESSIONID", sessionId);
							//设置cookie的超时时长，一般和session的超时时长一致
							ssID.setMaxAge(18000);							
							//返回cookie给客户端
							response.addCookie(ssID);
							
						} else {
							responseResult += "\"status\":201,\"msg\":\"登录失败，用户名密码错误！\"}";
						} // 调用数据库查询结束
							// 如果session有记录，分为两种情况，第一种：同一个用户第二次登录，第二种：不同用户登录
					} else {
						if (request.getSession().getAttribute("loginName").equals(user)) {
							responseResult += "\"status\":205,\"msg\":\"已经登录，不能再次登录\"}";
						} else {
							responseResult += "\"status\":206,\"msg\":\"已经有其他用户登录，不能再次登录\"}";
						}
					}
				} // 判断特殊字符结束
				else {
					responseResult += "\"status\":204,\"msg\":\"登录失败，用户名密码包含特殊字符\"}";
				}
			} else {
				responseResult += "\"status\":203,\"msg\":\"登录失败，用户名密码长度须在3-8位之间！\"}";
			}
		} else {
			responseResult += "\"status\":202,\"msg\":\"输入有误，用户名密码不能为空\"}";

		}

		System.out.println(responseResult);
		response.getWriter().append(responseResult);
	}

}
