package com.communication.volley.toolbox;

import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/*
 2    * ====================================================================
 3    * Licensed to the Apache Software Foundation (ASF) under one
 4    * or more contributor license agreements.  See the NOTICE file
 5    * distributed with this work for additional information
 6    * regarding copyright ownership.  The ASF licenses this file
 7    * to you under the Apache License, Version 2.0 (the
 8    * "License"); you may not use this file except in compliance
 9    * with the License.  You may obtain BaseApplication copy of the License at
 10    *
 11    *   http://www.apache.org/licenses/LICENSE-2.0
 12    *
 13    * Unless required by applicable law or agreed to in writing,
 14    * software distributed under the License is distributed on an
 15    * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 16    * KIND, either express or implied.  See the License for the
 17    * specific language governing permissions and limitations
 18    * under the License.
 19    * ====================================================================
 20    *
 21    * This software consists of voluntary contributions made by many
 22    * individuals on behalf of the Apache Software Foundation.  For more
 23    * information on the Apache Software Foundation, please see
 24    * <http://www.apache.org/>.
 25    *
 26    */
public class DateUtils {
	public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
	public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

	private static final String[] DEFAULT_PATTERNS = new String[] {
			PATTERN_RFC1036, PATTERN_RFC1123, PATTERN_ASCTIME };
	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
	static {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(GMT);
		calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
	}

	private DateUtils() {
	}

	public static Date parseDate(String dateValue) throws DateParseException {
		String[] dateFormats = null;
		Date startDate = null;
		if (dateValue == null) {
			throw new IllegalArgumentException("dateValue is null");
		}
		if (dateFormats == null) {
			dateFormats = DEFAULT_PATTERNS;
		}
		if (startDate == null) {
			startDate = DEFAULT_TWO_DIGIT_YEAR_START;
		}
		if (dateValue.length() > 1 && dateValue.startsWith("'")
				&& dateValue.endsWith("'")) {
			dateValue = dateValue.substring(1, dateValue.length() - 1);
		}

		for (String dateFormat : dateFormats) {
			SimpleDateFormat dateParser = DateFormatHolder
					.formatFor(dateFormat);
			dateParser.set2DigitYearStart(startDate);

			try {
				return dateParser.parse(dateValue);
			} catch (ParseException pe) {
				// ignore this exception, we will try the next format
			}
		}

		// we were unable to parse the date
		throw new DateParseException("Unable to parse the date " + dateValue);
	}

	public static String formatDate(Date date) {
		return formatDate(date, PATTERN_RFC1123);
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null)
			throw new IllegalArgumentException("date is null");
		if (pattern == null)
			throw new IllegalArgumentException("pattern is null");

		SimpleDateFormat formatter = DateFormatHolder.formatFor(pattern);
		return formatter.format(date);
	}

	final static class DateFormatHolder {

		private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>() {

			@Override
			protected SoftReference<Map<String, SimpleDateFormat>> initialValue() {
				return new SoftReference<Map<String, SimpleDateFormat>>(
						new HashMap<String, SimpleDateFormat>());
			}

		};

		public static SimpleDateFormat formatFor(String pattern) {
			SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS
					.get();
			Map<String, SimpleDateFormat> formats = ref.get();
			if (formats == null) {
				formats = new HashMap<String, SimpleDateFormat>();
				THREADLOCAL_FORMATS
						.set(new SoftReference<Map<String, SimpleDateFormat>>(
								formats));
			}

			SimpleDateFormat format = formats.get(pattern);
			if (format == null) {
				format = new SimpleDateFormat(pattern, Locale.US);
				format.setTimeZone(TimeZone.getTimeZone("GMT"));
				formats.put(pattern, format);
			}

			return format;
		}

	}

}
