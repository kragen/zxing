/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.result;

import android.app.Activity;
import com.google.zxing.client.android.R;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.ParsedResult;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class CalendarResultHandler extends ResultHandler {

  private static final int[] mButtons = {
      R.string.button_add_calendar
  };

  public CalendarResultHandler(Activity activity, ParsedResult result) {
    super(activity, result);
  }

  public int getButtonCount() {
    return mButtons.length;
  }

  public int getButtonText(int index) {
    return mButtons[index];
  }

  public void handleButtonPress(int index) {
    CalendarParsedResult calendarResult = (CalendarParsedResult) mResult;
    switch (index) {
      case 0:
        addCalendarEvent(calendarResult.getSummary(), calendarResult.getStart(),
            calendarResult.getEnd());
        break;
    }
  }

  @Override
  public CharSequence getDisplayContents() {
    CalendarParsedResult calResult = (CalendarParsedResult) mResult;
    StringBuffer result = new StringBuffer();
    ParsedResult.maybeAppend(calResult.getSummary(), result);
    appendTime(calResult.getStart(), result);

    // The end can be null if the event has no duration, so use the start time.
    String endString = calResult.getEnd();
    if (endString == null) endString = calResult.getStart();
    appendTime(endString, result);

    ParsedResult.maybeAppend(calResult.getLocation(), result);
    ParsedResult.maybeAppend(calResult.getAttendee(), result);
    ParsedResult.maybeAppend(calResult.getTitle(), result);
    return result.toString();
  }

  private void appendTime(String when, StringBuffer result) {
    if (when.length() == 8) {
      // Show only year/month/day
      DateFormat format = new SimpleDateFormat("yyyyMMdd");
      Date date = format.parse(when, new ParsePosition(0));
      ParsedResult.maybeAppend(DateFormat.getDateInstance().format(date.getTime()), result);
    } else {
      // The when string can be local time, or UTC if it ends with a Z
      DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
      Date date = format.parse(when.substring(0, 15), new ParsePosition(0));
      long milliseconds = date.getTime();
      if (when.length() == 16 && when.charAt(15) == 'Z') {
        Calendar calendar = new GregorianCalendar();
        int offset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET));
        milliseconds += offset;
      }
      ParsedResult.maybeAppend(DateFormat.getDateTimeInstance().format(milliseconds), result);
    }
  }

  public int getDisplayTitle() {
    return R.string.result_calendar;
  }

}
