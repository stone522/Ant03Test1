package com.testing.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginOut
 */
@WebServlet("/LoginOut")
public class LoginOut extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginOut() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/html;charset=UTF-8");
		// 请求参数的编码
		request.setCharacterEncoding("UTF-8");
		//通过让session失效，达到注销的目的。
		if(request.getSession().getAttribute("loginName") !=null) {
			request.getSession().invalidate();
			response.getWriter().append("{\"status\":0,\"msg\":\"注销成功\"}");			
			
		}else {
			request.getSession().invalidate();
			response.getWriter().append("{\"status\":0,\"msg\":\"您还没有登录！\"}");
		}
		
//		通过sendRedirect进行重定向。
//		response.sendRedirect("index.html");
		
	}

}
