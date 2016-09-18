

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Servlet implementation class OpenStackServletMeetings
 */
public class OpenStackServletMeetings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean started = false;
	private static ArrayList<String> vistedUrls = new ArrayList<String>();
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter printer = response.getWriter();
		printVisitedURL(response);
		//If session parameter is passed in only that parameter will be processed. All other parameters will be ignored.
		String session = request.getParameter("session");
		if(session != null){
			if (session.equalsIgnoreCase("start")) {
				started = true;
				vistedUrls.add(request.getRequestURL().toString() + "?" + request.getQueryString());
				printer.println("\nSession has started.");
				printer.println("Enter <project> and <year> parameters to search meetings.\n");
				return;
			}
			else if(session.equalsIgnoreCase("end")) {
				printer.println("\nSession has ended");
				started = false;
				session = "";
				vistedUrls.clear();
				return;
			}
			else {
				printer.println("Entered invalid session parameter.");
				return;
			}
		}
		
		//Handling project and year parameters
		String project = request.getParameter("project");
		String year = request.getParameter("year");
		if (project != null) {
			project = project.toLowerCase();
		}
		else {
			printer.println("Required parameter <project> needed");
		}
		//if year parameter is null then program will exit and give response
		if (year == null) {
			printer.println("Required parameter <year> needed");
			return;
		}

		if(project.length() > 0 && year.length() > 0){
			String source = "http://eavesdrop.openstack.org/meetings";
			Document doc;
			
			//checking is project is valid
			try {
				source = source + "/" + project;
				doc = Jsoup.connect(source).get();
			}
			catch (Exception exp) {
				printer.println("\nProject with name " + project + " is not a valid project");
				exp.printStackTrace();
				return;
			} 
			//checking if year is valid
			try {
				source = source + "/" + year;
				doc = Jsoup.connect(source).get();
			}
			catch (Exception exp) {
				printer.println("\nInvalid year " + year + " for Project named " + project);
				exp.printStackTrace();
				return;
			} 
			if(started) {
				vistedUrls.add(request.getRequestURL().toString() + "?" + request.getQueryString());
			}
			try {	
				Elements links = doc.select("a");
			    if(doc != null) {
			    	ListIterator<Element> iter = links.listIterator();
			    	while(iter.hasNext()) {
			    		Element e = (Element) iter.next();
			    		String s = e.html();
			    		if ( s != null && s.contains(project)) {
			    			printer.println(s);
			    		}
			    	}	
			    }  
			} catch (Exception exp) {
				exp.printStackTrace();
			} 
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private void printVisitedURL(HttpServletResponse response) throws IOException {
		PrintWriter printer = response.getWriter();
		
		printer.println("Visited URLs");
		for (int i = 0; i < vistedUrls.size(); i++) {
			printer.println(vistedUrls.get(i));
		}
		printer.println();
		printer.println("URL Data");
	}


}
