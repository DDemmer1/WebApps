package de.uni_koeln.info.webapp.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;
import de.uni_koeln.spinfo.textengineering.ir.util.IRUtils;

@Controller
@RequestMapping(value = "article/")
public class ArticleController {

	@Autowired
	private Set<IRDocument> tmpResults;
	
	@Autowired
	private RankedRetrieval index;

	/**
	 * Wird bei einem Klick auf einen Titel (article/detail?uri=...)aufgerufen. Gibt den Inhalt, den
	 * Titel und die URI zur Stammseite an das Template weiter.
	 * 
	 * @param request
	 * @param model
	 * @return text/html
	 */
	@GetMapping (value="detail")
	public String showArticle(HttpServletRequest request, Model model) {
		String uri = request.getParameter("uri");

		IRDocument ird = getIRDocumentFromURI(uri);

		model.addAttribute("text", ird.getContent());
		model.addAttribute("title", ird.getTitle());
		model.addAttribute("uri", ird.getURI());

		// zu tmpResults hinzugefügt, damit getIRDocumentFromUri den Artikel
		// findet
		List<IRDocument> similar = IRUtils.getMostSimilar(ird, index, 4);
		tmpResults.addAll(similar);
		model.addAttribute("similar", similar);

		return "article";
	}
	
	
	/**
	 * Durchsucht die Ergebnisse nach und vergleicht die URIs.
	 * 
	 * @param uriString
	 * @return das IRDocument mit der gleichen URI
	 */
	private IRDocument getIRDocumentFromURI(String uriString) {
		try {
			URI uri = new URI(uriString);
			for (IRDocument ird : tmpResults) {
				if (uri.equals(ird.getURI())) {
					return ird;
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
