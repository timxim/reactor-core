/*
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.util.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class Context1Test {

	Context1 c = new Context1(1, "A");

	@Test
	public void replaceKey1NewContext() throws Exception {
		Context put = c.put(1, "foo");

		assertThat(put)
				.isInstanceOf(Context1.class)
				.isNotSameAs(c);
		assertThat(put.stream().map(Map.Entry::getKey))
				.containsExactly(1);
		assertThat(put.stream().map(Map.Entry::getValue))
				.containsExactly("foo");
	}

	@Test
	public void putDifferentKeyContext2() throws Exception {
		Context put = c.put(2, "Abis");
		assertThat(put)
				.isInstanceOf(Context2.class);
		assertThat(put.stream().map(Map.Entry::getKey))
				.containsExactly(1, 2);
		assertThat(put.stream().map(Map.Entry::getValue))
				.containsExactly("A", "Abis");
	}

	@Test
	public void hasKey() throws Exception {
		assertThat(c.hasKey(1)).as("hasKey(1)").isTrue();
		assertThat(c.hasKey(2)).as("hasKey(2)").isFalse();
	}

	@Test
	public void get() throws Exception {
		assertThat((Object) c.get(1)).isEqualTo("A"); //TODO meh, necessary cast to Object
	}

	@Test
	public void getUnknown() throws Exception {
		assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> c.get(2))
				.withMessage("Context does not contain key: 2");
	}

	@Test
	public void getUnknownWithDefault() throws Exception {
		assertThat(c.getOrDefault(2, "boo")).isEqualTo("boo");
	}

	@Test
	public void stream() throws Exception {
		assertThat(c.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
				.hasSize(1)
				.containsOnlyKeys(1)
				.containsValues("A");
	}

	@Test
	public void getKey() throws Exception {
		assertThat(c.getKey()).isEqualTo(1);
	}

	@Test
	public void getValue() throws Exception {
		assertThat(c.getValue()).isEqualTo("A");
	}

	@Test
	public void setValue() throws Exception {
		assertThatExceptionOfType(UnsupportedOperationException.class)
				.isThrownBy(() -> c.setValue("BOOM"))
				.withMessage("Does not support in-place update");
	}

	@Test
	public void string() throws Exception {
		assertThat(c.toString()).isEqualTo("Context1{1=A}");
	}

	@Test
	public void ofApi() {
		assertThat(Context.of("test", 12))
				.isInstanceOf(Context1.class)
				.hasToString("Context1{test=12}");
	}

	@Test
	public void putAllOf() {
		Context m = Context.of("A", 1, "B", 2, "C", 3);
		Context put = c.putAll(m);

		assertThat(put).isInstanceOf(Context4.class)
		               .hasToString("Context4{1=A, A=1, B=2, C=3}");
	}

	@Test
	public void putAllOfEmpty() {
		Context m = Context.empty();
		Context put = c.putAll(m);

		assertThat(put).isSameAs(c);
	}

}