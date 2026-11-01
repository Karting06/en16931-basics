/*
 * Copyright (C) 2026 Philip Helger
 * http://www.helger.com
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.en16931.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

import org.junit.Test;

import com.helger.datetime.helper.PDTFactory;
import com.helger.datetime.xml.XMLOffsetDateTime;

/**
 * Test class for class {@link EEN16931DateFormatCode}.
 *
 * @author Philip Helger
 */
public final class EEN16931DateFormatCodeTest
{
  @Test
  public void testBasic ()
  {
    for (final EEN16931DateFormatCode e : EEN16931DateFormatCode.values ())
    {
      assertTrue (e.getID ().length () > 0);
      assertTrue (e.getJavaPattern ().length () > 0);
      assertSame (e, EEN16931DateFormatCode.getFromIDOrNull (e.getID ()));
      assertSame (e, EEN16931DateFormatCode.getFromIDOrDefault (e.getID ()));
    }

    // The qualifiers as they appear in the CII "format" attribute
    assertEquals ("102", EEN16931DateFormatCode.CCYYMMDD.getID ());
    assertEquals ("208", EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.getID ());
    assertSame (EEN16931DateFormatCode.CCYYMMDD, EEN16931DateFormatCode.DEFAULT);

    // Only 208 carries a time
    for (final EEN16931DateFormatCode e : EEN16931DateFormatCode.values ())
      if (e == EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM)
        assertTrue (e.hasTime ());
      else
        assertFalse (e.hasTime ());

    assertNull (EEN16931DateFormatCode.getFromIDOrNull ("999"));
    assertNull (EEN16931DateFormatCode.getFromIDOrNull (""));
    assertNull (EEN16931DateFormatCode.getFromIDOrNull (null));
  }

  @Test
  public void testGetFromIDOrDefault ()
  {
    // An absent qualifier means the EN 16931 default
    assertSame (EEN16931DateFormatCode.DEFAULT, EEN16931DateFormatCode.getFromIDOrDefault (null));
    assertSame (EEN16931DateFormatCode.DEFAULT, EEN16931DateFormatCode.getFromIDOrDefault (""));

    // A qualifier that is present but unknown must not silently become the default
    assertNull (EEN16931DateFormatCode.getFromIDOrDefault ("999"));
  }

  @Test
  public void testParseLocalDate ()
  {
    final LocalDate aExpected = PDTFactory.createLocalDate (2026, Month.MAY, 15);

    assertEquals (aExpected, EEN16931DateFormatCode.CCYYMMDD.parseLocalDate ("20260515"));
    assertEquals (aExpected, EEN16931DateFormatCode.YYMMDD.parseLocalDate ("260515"));
    assertEquals (aExpected, EEN16931DateFormatCode.DDMMCCYY.parseLocalDate ("15052026"));
    assertEquals (aExpected, EEN16931DateFormatCode.DDMMYY.parseLocalDate ("150526"));
    assertEquals (aExpected, EEN16931DateFormatCode.MMDDYY.parseLocalDate ("051526"));
    assertEquals (aExpected, EEN16931DateFormatCode.YYDDD.parseLocalDate ("26135"));

    // A format with a time parses as well - the time is dropped
    assertEquals (aExpected, EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.parseLocalDate ("20260515102030+0200"));

    // Not matching the format
    assertNull (EEN16931DateFormatCode.CCYYMMDD.parseLocalDate ("2026-05-15"));
    assertNull (EEN16931DateFormatCode.CCYYMMDD.parseLocalDate ("bogus"));
    assertNull (EEN16931DateFormatCode.CCYYMMDD.parseLocalDate (""));
    assertNull (EEN16931DateFormatCode.CCYYMMDD.parseLocalDate (null));
  }

  @Test
  public void testWeekDate ()
  {
    final EEN16931DateFormatCode e = EEN16931DateFormatCode.YYWWD;

    // A Friday in ISO week 20
    final LocalDate aDate = PDTFactory.createLocalDate (2026, Month.MAY, 15);
    assertEquals ("26205", e.getAsString (aDate));
    assertEquals (aDate, e.parseLocalDate ("26205"));

    // Day 1 is the Monday
    assertEquals (PDTFactory.createLocalDate (2026, Month.MAY, 11), e.parseLocalDate ("26201"));

    // The week belongs to the week based year, not to the calendar year
    assertEquals (PDTFactory.createLocalDate (2027, Month.JANUARY, 1), e.parseLocalDate ("26535"));
    assertEquals ("26535", e.getAsString (PDTFactory.createLocalDate (2027, Month.JANUARY, 1)));
    assertEquals (PDTFactory.createLocalDate (2025, Month.DECEMBER, 29), e.parseLocalDate ("26011"));

    assertNull (e.parseLocalDate ("262005"));
    assertNull (e.parseLocalDate ("26208"));
    assertNull (e.parseLocalDate ("26545"));
  }

  @Test
  public void testWeekDateDoesNotDependOnTheLocale ()
  {
    final Locale aDefault = Locale.getDefault ();
    try
    {
      for (final Locale aLocale : new Locale [] { Locale.US, Locale.GERMANY, Locale.ROOT })
      {
        Locale.setDefault (aLocale);
        assertEquals (PDTFactory.createLocalDate (2026, Month.MAY, 15),
                      EEN16931DateFormatCode.YYWWD.parseLocalDate ("26205"));
      }
    }
    finally
    {
      Locale.setDefault (aDefault);
    }
  }

  @Test
  public void testParseDateTime ()
  {
    final XMLOffsetDateTime aDateTime = EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.parseDateTime ("20260515102030+0200");
    assertNotNull (aDateTime);
    assertEquals (PDTFactory.createLocalDate (2026, Month.MAY, 15), aDateTime.toLocalDate ());
    assertEquals (10, aDateTime.toLocalTime ().getHour ());
    assertEquals (20, aDateTime.toLocalTime ().getMinute ());
    assertEquals (30, aDateTime.toLocalTime ().getSecond ());

    // A date only format has no date and time to offer
    assertNull (EEN16931DateFormatCode.CCYYMMDD.parseDateTime ("20260515"));

    assertNull (EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.parseDateTime ("20260515"));
    assertNull (EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.parseDateTime (""));
    assertNull (EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.parseDateTime (null));
  }

  @Test
  public void testGetAsString ()
  {
    final LocalDate aDate = PDTFactory.createLocalDate (2026, Month.MAY, 15);

    assertEquals ("20260515", EEN16931DateFormatCode.CCYYMMDD.getAsString (aDate));
    assertEquals ("260515", EEN16931DateFormatCode.YYMMDD.getAsString (aDate));
    assertEquals ("15052026", EEN16931DateFormatCode.DDMMCCYY.getAsString (aDate));

    // A date alone cannot fill a format that carries a time
    assertNull (EEN16931DateFormatCode.CCYYMMDDHHMMSSZHHMM.getAsString (aDate));

    assertNull (EEN16931DateFormatCode.CCYYMMDD.getAsString (null));
  }

  @Test
  public void testWriteAndReadBack ()
  {
    final LocalDate aDate = PDTFactory.createLocalDate (2026, Month.MAY, 15);
    for (final EEN16931DateFormatCode e : EEN16931DateFormatCode.values ())
    {
      final String sValue = e.getAsString (aDate);
      if (e.hasTime ())
        assertNull (sValue);
      else
      {
        assertNotNull ("No value for " + e.getID (), sValue);
        assertEquals ("Round trip failed for " + e.getID (), aDate, e.parseLocalDate (sValue));
      }
    }
  }
}
