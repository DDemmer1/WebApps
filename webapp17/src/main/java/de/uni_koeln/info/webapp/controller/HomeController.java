package de.uni_koeln.info.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.qos.logback.core.Context;
import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

@Controller
public class HomeController {
	
	//Liste für die Ergebnisse der Suche
	List<IRDocument> results;

	
	
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
	public String defaultHandlerMethod(Model model) {
		return "home";
	}

	
	
	@PostMapping(value = "/search")
	public String getSearchResult(HttpServletRequest request, Model model) {

		try {
			Searcher searcher = new Searcher("luceneIndex");
			
			String query = request.getParameter("query");

			results = new ArrayList<>();
			System.out.println(query);
			
			
			results = searcher.search(query+"~",10);

			model.addAttribute("results", results);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "home";
	}
	
	@GetMapping(value = "/article")
	public String articleHandler(HttpServletRequest request, Model model) {
		String uri = request.getParameter("article");
		
		
		String text = getTextFromURI(uri);
		
		System.out.println(text);
		
		model.addAttribute("text",text);
		
		return "article";
	}



	private String getTextFromURI(String uri) {
		
		System.out.println("Size: "+results.size());
		for(IRDocument ird : results){
				if(uri.equals(ird.getURI())){
				return ird.getContent();
			}
		}
		return null;
	}
	

}
