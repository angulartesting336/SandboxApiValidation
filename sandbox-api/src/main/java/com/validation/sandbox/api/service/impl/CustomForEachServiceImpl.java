package com.validation.sandbox.api.service.impl;

import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class CustomForEachServiceImpl {

	public static class Break {
		private boolean doBreak = false;

		public void stop() {
			doBreak = true;
		}

		boolean get() {
			return doBreak;
		}
	}

	public static <T> void forEach(Stream<T> stream, BiConsumer<T, Break> consumer) {
		Spliterator<T> spliterator = stream.spliterator();
		boolean hadNext = true;
		Break breaker = new Break();

		while (hadNext && !breaker.get()) {
			hadNext = spliterator.tryAdvance(elem -> {
				consumer.accept(elem, breaker);
			});
		}
	}

}
