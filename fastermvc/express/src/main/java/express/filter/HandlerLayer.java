package express.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import express.http.Request;
import express.http.Response;

/**
 * @author Simon Reinisch
 *         <p>
 *         Controller class for FilterLayer.
 */
public class HandlerLayer<T extends DefaultHandler> {

	public final List<T> filters = Collections.synchronizedList(new ArrayList<>());

//	public void add(T expressFilter) {
//		this.filter.add(expressFilter);
//	}

//	public void add(int index, T expressFilter) {
//		this.filter.add(index, expressFilter);
//	}

//	public void addAll(List<T> expressFilters) {
//		this.filter.addAll(expressFilters);
//	}

//	public List<T> getFilter() {
//		return filter;
//	}

//	public void filter(Request req, Response res) {
//		ListIterator<T> iter = this.filter.listIterator();
//
//		while (!res.isClosed() && iter.hasNext()) {
//			iter.next().handle(req, res);
//		}
//	}
}
