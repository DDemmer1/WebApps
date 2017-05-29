package de.uni_koeln.info.webapp.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;
import de.uni_koeln.spinfo.textengineering.ir.model.newspaper.NewsCorpus;
import de.uni_koeln.spinfo.textengineering.ir.ranked.InvIndex;
import de.uni_koeln.spinfo.textengineering.ir.ranked.RankedRetrieval;
import de.uni_koeln.spinfo.textengineering.ir.util.IRUtils;

@Controller
@RequestMapping(value = "index/")
public class IndexController {

	@Autowired
	private Searcher searcher;

	@Autowired
	private List<IRDocument> tmpResults;

	@Autowired
	private RankedRetrieval index;
	/**
	 * Calling the URL:
	 * <a href= "http://localhost:8080/index/size">http://localhost:8080/index/
	 * size</a> Returns the index size as plain text. The attribute specified
	 * within the {@link RequestMapping} annotation
	 * <code>produces="text/plain"</code> determines the mime type which is the
	 * format/representation of the requested resource. See also
	 * <a href= "http://wiki.selfhtml.org/wiki/MIME-Type/%C3%9Cbersicht">MIME-
	 * Type</a>
	 * 
	 * @return text/plain
	 */
	@RequestMapping(value = "size", method = RequestMethod.GET, produces = "text/plain")
	public @ResponseBody String indexSize() {
		return String.valueOf(searcher.indexSize());
	}

	@GetMapping(value = "search")
	public String search(HttpServletRequest request, Model model) {

		try {
			String query = request.getParameter("query");
			if (query == "" || query == null) {
				return "home";
			}

			tmpResults.clear();
			tmpResults.addAll(searcher.search(query + "~", 10));

			if (!tmpResults.isEmpty()) {
				model.addAttribute("results", tmpResults);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "results";
	}

	/**
	 * 
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/article")
	public String articleHandler(HttpServletRequest request, Model model) {
		String uri = request.getParameter("uri");

		IRDocument ird = getIRDocumentFromURI(uri);

		model.addAttribute("text", ird.getContent());
		model.addAttribute("title", ird.getTitle());
		model.addAttribute("uri", ird.getURI());

		
		model.addAttribute("similar", IRUtils.getMostSimilar(ird, index , 4));

		return "article";
	}

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

	/**
	 * Search a certain document field by calling the URL: <code><a href=
	 * "http://localhost:8080/index/search/title/köln">http://localhost:8080/index/search/{fieldName}/{yourSearchPhrase}</a></code>
	 * 
	 * <p>
	 * The results are from type {@link IRDocument}. They are returned as a
	 * collection of JSON objects.
	 * </p>
	 * 
	 * @param searchPhrase
	 * @return Collection+JSON
	 * @throws IOException
	 * @throws ParseException
	 */
	@RequestMapping(value = "search/{field}/{searchPhrase}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<IRDocument> searchField(@PathVariable("field") String fieldName,
			@PathVariable("searchPhrase") String searchPhrase) throws IOException, ParseException {
		return searcher.search(searchPhrase, fieldName, 10);
	}

}
