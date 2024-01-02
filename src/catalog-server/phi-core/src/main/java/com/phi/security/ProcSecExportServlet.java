package com.phi.security;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.ProcSecurityAction;

public class ProcSecExportServlet extends HttpServlet {
	
	public void init(ServletConfig config) throws ServletException 	{
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
		this.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		this.service(req, resp);
	}

	protected void service(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		new ContextualHttpServletRequest(req) {

			@Override
			public void process() throws Exception {
				doWork(req,resp);
			}
		}.run();

	}

	private void doWork( HttpServletRequest req, HttpServletResponse resp ) throws IOException, PhiException 	{
		
		ProcSecurityAction procSecAction = ProcSecurityAction.instance();
		StringBuffer outputCsv = procSecAction.exportAllCsv();
		byte[] byteResponse = String.valueOf(outputCsv).getBytes();
		
		resp.setContentType("text/csv" ); 
		resp.setContentLength(byteResponse.length);
		
		String date = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date());
		resp.setHeader("Content-Disposition", "attachment; filename=\"SecurityRoles_"+date+".csv\"");
		
		ServletOutputStream op = resp.getOutputStream();
		op.write(byteResponse);
		op.flush();
		op.close();
		
	}
	
}