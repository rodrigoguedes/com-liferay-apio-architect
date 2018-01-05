/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.message.json.ld.internal;

import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonArrayThat;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonBoolean;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonInt;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonObjectWhere;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonObjectWith;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonElement;

import com.liferay.apio.architect.test.json.Conditions;
import com.liferay.apio.architect.test.json.Conditions.Builder;
import com.liferay.apio.architect.test.json.JsonMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

/**
 * Provides utility functions for testing JSON Plain message mappers.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class JSONLDTestUtil {

	/**
	 * A {@link Matcher} that checks if a field is a {@code {"@type" = " ID"}}
	 * JSON Object.
	 */
	public static final Matcher<JsonElement> IS_A_TYPE_ID_JSON_OBJECT = is(
		aJsonObjectWhere("@type", is(aJsonString(equalTo("@id")))));

	/**
	 * Returns a {@link Matcher} that checks if the field is the JSON Object of
	 * a {@code RootElement} with the provided ID.
	 *
	 * @param  id the ID of the {@code RootElement}
	 * @return a matcher for a JSON Object of a {@code RootElement} with the
	 *         provided ID
	 * @review
	 */
	public static Matcher<JsonElement> aRootElementJsonObjectWithId(String id) {
		Builder builder = new Builder();

		Conditions contextConditions = builder.where(
			"@vocab", is(aJsonString(equalTo("http://schema.org")))
		).where(
			"embedded2", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"linked1", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"linked2", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"relatedCollection1", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"relatedCollection2", IS_A_TYPE_ID_JSON_OBJECT
		).build();

		Matcher<JsonElement> isAJsonObjectWithTheContext = is(
			aJsonObjectWith(contextConditions));

		Conditions conditions = builder.where(
			"@context", isAJsonObjectWithTheContext
		).where(
			"@id", isALinkTo("localhost/p/model/" + id)
		).where(
			"@type", containsTheTypes("Type 1", "Type 2")
		).where(
			"binary1", isALinkTo("localhost/b/model/" + id + "/binary1")
		).where(
			"binary2", isALinkTo("localhost/b/model/" + id + "/binary2")
		).where(
			"boolean1", is(aJsonBoolean(true))
		).where(
			"boolean2", is(aJsonBoolean(false))
		).where(
			"date1", is(aJsonString(equalTo("2016-06-15T09:00Z")))
		).where(
			"date2", is(aJsonString(equalTo("2017-04-03T18:36Z")))
		).where(
			"embedded1", isAJsonObjectWithTheFirstEmbedded()
		).where(
			"embedded2", isALinkTo("localhost/p/first-inner-model/second")
		).where(
			"link1", isALinkTo("www.liferay.com")
		).where(
			"link2", isALinkTo("community.liferay.com")
		).where(
			"linked1", isALinkTo("localhost/p/first-inner-model/third")
		).where(
			"linked2", isALinkTo("localhost/p/first-inner-model/fourth")
		).where(
			"localizedString1", is(aJsonString(equalTo("Translated 1")))
		).where(
			"localizedString2", is(aJsonString(equalTo("Translated 2")))
		).where(
			"number1", is(aJsonInt(equalTo(2017)))
		).where(
			"number2", is(aJsonInt(equalTo(42)))
		).where(
			"relatedCollection1",
			isALinkTo("localhost/p/model/" + id + "/models")
		).where(
			"relatedCollection2",
			isALinkTo("localhost/p/model/" + id + "/models")
		).where(
			"string1", is(aJsonString(equalTo("Live long and prosper")))
		).where(
			"string2", is(aJsonString(equalTo("Hypermedia")))
		).build();

		return aJsonObjectWith(conditions);
	}

	/**
	 * Returns a {@link Matcher} that checks if the field contains the provided
	 * types as a JSON Array.
	 *
	 * @param  types the types to match
	 * @return a matcher for a type JSON Array
	 * @review
	 */
	public static Matcher<? extends JsonElement> containsTheTypes(
		String... types) {

		Stream<String> stream = Arrays.stream(types);

		List<Matcher<? super JsonElement>> matchers = stream.map(
			CoreMatchers::equalTo
		).map(
			JsonMatchers::aJsonString
		).collect(
			Collectors.toList()
		);

		return is(aJsonArrayThat(contains(matchers)));
	}

	/**
	 * Returns a {@link Matcher} that checks if the field is a JSON Object of
	 * the first embedded.
	 *
	 * @return a matcher for a JSON Object of the first embedded
	 * @review
	 */
	public static Matcher<JsonElement> isAJsonObjectWithTheFirstEmbedded() {
		Builder builder = new Builder();

		Conditions firstEmbeddedContextConditions = builder.where(
			"linked", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"relatedCollection", IS_A_TYPE_ID_JSON_OBJECT
		).build();

		Conditions firstEmbeddedConditions = builder.where(
			"@context", is(aJsonObjectWith(firstEmbeddedContextConditions))
		).where(
			"@id", isALinkTo("localhost/p/first-inner-model/first")
		).where(
			"@type", containsTheTypes("Type")
		).where(
			"binary", isALinkTo("localhost/b/first-inner-model/first/binary")
		).where(
			"boolean", is(aJsonBoolean(true))
		).where(
			"link", isALinkTo("www.liferay.com")
		).where(
			"localizedString", is(aJsonString(equalTo("Translated")))
		).where(
			"number", is(aJsonInt(equalTo(42)))
		).where(
			"embedded", isAJsonObjectWithTheSecondEmbedded()
		).where(
			"linked", isALinkTo("localhost/p/second-inner-model/second")
		).where(
			"relatedCollection",
			isALinkTo("localhost/p/first-inner-model/first/models")
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).build();

		return is(aJsonObjectWith(firstEmbeddedConditions));
	}

	/**
	 * Returns a {@link Matcher} that checks if the field is a JSON Object of
	 * the second embedded.
	 *
	 * @return a matcher for a JSON Object of the second embedded
	 * @review
	 */
	public static Matcher<JsonElement> isAJsonObjectWithTheSecondEmbedded() {
		Conditions.Builder builder = new Conditions.Builder();

		Conditions secondEmbeddedContextConditions = builder.where(
			"embedded", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"linked", IS_A_TYPE_ID_JSON_OBJECT
		).where(
			"relatedCollection", IS_A_TYPE_ID_JSON_OBJECT
		).build();

		Conditions secondEmbeddedConditions = builder.where(
			"@context", is(aJsonObjectWith(secondEmbeddedContextConditions))
		).where(
			"@id", isALinkTo("localhost/p/second-inner-model/first")
		).where(
			"@type", containsTheTypes("Type")
		).where(
			"boolean", is(aJsonBoolean(false))
		).where(
			"binary", isALinkTo("localhost/b/second-inner-model/first/binary")
		).where(
			"embedded", isALinkTo("localhost/p/third-inner-model/first")
		).where(
			"link", isALinkTo("community.liferay.com")
		).where(
			"number", is(aJsonInt(equalTo(2017)))
		).where(
			"relatedCollection",
			isALinkTo("localhost/p/second-inner-model/first/models")
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).where(
			"linked", isALinkTo("localhost/p/third-inner-model/second")
		).build();

		return is(aJsonObjectWith(secondEmbeddedConditions));
	}

	/**
	 * Returns a {@link Matcher} that checks if the field is a link to the
	 * provided URL.
	 *
	 * @param  url the URL to match
	 * @return a matcher for URL fields
	 * @review
	 */
	public static Matcher<? extends JsonElement> isALinkTo(String url) {
		return is(aJsonString(equalTo(url)));
	}

}