package de.uni_koeln.info.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

@Controller
public class HomeController {

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
	public String defaultHandlerMethod(Model model) {
		return "home";
	}

	@PostMapping(value = "/search")
	public String getSearchResult(HttpServletRequest request, Model model) {

		try {
			Searcher searcher = new Searcher("luceneIndex");
			
			String query = request.getParameter("query");

			List<IRDocument> results = new ArrayList<>();
			System.out.println(query);
			
			
			results = searcher.search(query+"~",50);

			model.addAttribute("results", results);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "home";
	}
	
	@RequestMapping(value = "/article", method = RequestMethod.GET, produces = "text/html")
	public String articleHandler(HttpServletRequest request, Model model) {
		
		String ird = request.getParameter("article");
		
		
		
		
//		model.addAttribute("text",text);
		
		return "article";
	}
	

}
