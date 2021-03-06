package com.almondtools.stringbench.incubation;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.byteseek.io.reader.StringReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.ForwardSearchIterator;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.Searcher;

import com.almondtools.stringbench.SinglePatternMatcherBenchmark;

public abstract class ByteSeekBenchmark extends SinglePatternMatcherBenchmark {

	private Map<String, Searcher<SequenceMatcher>> algorithm;

	@Override
	public void prepare(Set<String> patterns) {
		this.algorithm = patterns.stream()
			.collect(toMap(pattern -> pattern, pattern -> create(pattern)));
	}

	public abstract Searcher<SequenceMatcher> create(String pattern);

	@Override
	public List<Integer> find(String pattern, String text) {
		Searcher<SequenceMatcher> searcher = algorithm.get(pattern);
		WindowReader reader = new StringReader(text);
		ForwardSearchIterator<SequenceMatcher> searchIterator = new ForwardSearchIterator<SequenceMatcher>(searcher, reader, 0);

		List<Integer> indexes = new ArrayList<>();

		while (searchIterator.hasNext()) {
			List<SearchResult<SequenceMatcher>> results = searchIterator.next();
			for (SearchResult<SequenceMatcher> result : results) {
				indexes.add((int) result.getMatchPosition());
			}
		}
		return indexes;
	}

}
