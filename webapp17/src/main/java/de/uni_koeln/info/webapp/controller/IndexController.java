package de.uni_koeln.info.webapp.controller;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.uni_koeln.spinfo.textengineering.ir.lucene.Searcher;
import de.uni_koeln.spinfo.textengineering.ir.model.IRDocument;

@Controller
@RequestMapping(value = "index/")
public class IndexController {

//	@Autowired
	private Searcher searcher;

	/**
	 * Calling the URL: <a href=
	 * "http://localhost:8080/index/size">http://localhost:8080/index/size</a>
	 * Returns the index size as plain text. The attribute specified within the
	 * {@link RequestMapping} annotation <code>produces="text/plain"</code>
	 * determines the mime type which is the format/representation of the
	 * requested resource. See also <a href=
	 * "http://wiki.selfhtml.org/wiki/MIME-Type/%C3%9Cbersicht">MIME-Type</a>
	 * 
	 * @return text/plain
	 */
	@RequestMapping(value = "size", method = RequestMethod.GET, produces = "text/plain")
	public @ResponseBody String indexSize() {
		return String.valueOf(searcher.indexSize());
	}

	/**
	 * <p>
	 * Search the index by calling the URL: <code><a href=
	 * "http://localhost:8080/index/search/köln">http://localhost:8080/index/search/{yourSearchPhrase}</a></code>
	 * </p>
	 * <p>
	 * The search phrase is handled to the controller as part of the URL. The
	 * brackets <code>{...}</code> declare a placeholder for a given request
	 * parameter, who's type is determined by the type declared within the
	 * method's signature (auto-conversion). The assignment is handled by
	 * Springs {@link PathVariable} annotation. The linking is managed by a
	 * reference name (e.g. <i>searchPhrase</i>).
	 * </p>
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
	@RequestMapping(value = "search/{searchPhrase}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<IRDocument> search(@PathVariable("searchPhrase") String param)
			throws IOException, ParseException {
		return searcher.search(param, 10);
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
