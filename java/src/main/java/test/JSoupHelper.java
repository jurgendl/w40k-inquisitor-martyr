package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public interface JSoupHelper {
	public static enum Soup {
		SELECT, TAG;
	}


	default String soupTag(String html, String tags, Predicate<Element> filter, String attribute) {
		List<Object> soup = soup(false, html, Soup.TAG, tags, filter, attribute, null);
		return soup.isEmpty() ? null : soup.get(0).toString();
	}

	default List<String> soup(boolean multiple, String html, String soup, String attribute) {
		return soup(multiple, html, null, soup, null, attribute, null);
	}

	default List<String> soup(boolean multiple, String html, String soup, Predicate<Element> filter, String attribute) {
		return soup(multiple, html, null, soup, filter, attribute, null);
	}

	default <T> List<T> soup(boolean multiple, String html, String soup, Predicate<Element> filter, Function<Element, T> mapper) {
		return soup(multiple, html, null, soup, filter, (String) null, mapper);
	}

	default <T> List<T> soup(boolean multiple, String html, Soup type, String soup, Predicate<Element> filter, Function<Element, T> mapper) {
		return soup(multiple, html, type, soup, filter, null, mapper);
	}

	default <T> List<T> soup(boolean multiple, String html, String soup, Function<Element, T> mapper) {
		return soup(multiple, html, null, soup, null, null, mapper);
	}

	@SuppressWarnings("unchecked")
	default <T> List<T> soup(boolean multiple, String html, Soup type, String soup, Predicate<Element> filter, String attribute, Function<Element, T> mapper) {
		Set<T> list = new LinkedHashSet<>();
		Document parsed = Jsoup.parse(html);
		Elements elements;
		if (type == null) type = Soup.SELECT;
		switch (type) {
			case SELECT:
				elements = parsed.select(soup);
				break;
			case TAG:
				elements = parsed.getElementsByTag(soup);
				break;
			default:
				throw new IllegalArgumentException();
		}
		Iterator<Element> it = elements.iterator();
		while (it.hasNext()) {
			Element tag = it.next();
			if (filter == null || filter.test(tag)) {
				if (attribute != null) {
					list.add((T) tag.attr(attribute));
				} else if (mapper != null) {
					list.add(mapper.apply(tag));
				} else {
					list.add((T) tag.toString());
				}
				if (!multiple) {
					break;
				}
			}
		}
		return new ArrayList<>(list);
	}

	default List<Element> soupElements(boolean multiple, String html, String soup, Predicate<Element> filter) {
		Set<Element> list = new LinkedHashSet<>();
		Document parsed = Jsoup.parse(html);
		Elements elements = parsed.select(soup);
		Iterator<Element> it = elements.iterator();
		while (it.hasNext()) {
			Element tag = it.next();
			if (filter == null || filter.test(tag)) {
				list.add(tag);
				if (!multiple) {
					break;
				}
			}
		}
		return new ArrayList<>(list);
	}
}
