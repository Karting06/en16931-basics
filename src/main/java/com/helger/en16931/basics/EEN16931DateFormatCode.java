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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;
import com.helger.base.string.StringHelper;
import com.helger.datetime.format.PDTFromString;
import com.helger.datetime.xml.XMLOffsetDateTime;

/**
 * The date format qualifiers of UNTDID 2379 that the CII syntax binding uses. CII spells every date
 * as a string plus a <code>format</code> attribute naming the layout of that string, whereas UBL
 * uses the XML Schema types and needs no qualifier - which is why this enum only ever shows up on
 * the CII side of a conversion.
 * <p>
 * EN 16931 itself only ever writes {@link #CCYYMMDD}, and since the 2026 edition also
 * {@link #CCYYMMDDHHMMSSZHHMM} for BT-166. The other entries exist because documents in the wild
 * use them and reading them is harmless.
 * <p>
 * Source of truth: <a href="https://service.unece.org/trade/untdid/d16b/tred/tred2379.htm">UNTDID
 * 2379</a>. This is not part of the EN 16931 code list workbook, which is why this enum lives
 * outside the <code>codelist</code> package.
 *
 * @author Philip Helger
 */
public enum EEN16931DateFormatCode implements IHasID <String>
{
  /** DDMMYY - day, month, year of the century */
  DDMMYY ("2", "ddMMuu", false),
  /** MMDDYY - month, day, year of the century */
  MMDDYY ("3", "MMdduu", false),
  /** DDMMCCYY - day, month, full year */
  DDMMCCYY ("4", "ddMMuuuu", false),
  /** YYMMDD - year of the century, month, day */
  YYMMDD ("101", "uuMMdd", false),
  /** CCYYMMDD - full year, month, day. The only date format EN 16931 writes. */
  CCYYMMDD ("102", "uuuuMMdd", false),
  /**
   * YYWWD - year of the century, ISO 8601 week, day of the week with 1 for Monday. Unlike the Java
   * pattern, the formatter of this format does not depend on the locale.
   */
  YYWWD ("103", "YYwwe", _createWeekDateFormatter (), false),
  /** YYDDD - year of the century, day of the year */
  YYDDD ("105", "uuDDD", false),
  /** CCYYMMDDHHMMSSZHHMM - full year, month, day, time and UTC offset */
  CCYYMMDDHHMMSSZHHMM ("208", "uuuuMMddHHmmssZ", true);

  /**
   * The format EN 16931 prescribes for a plain date, and the one to assume if the
   * <code>format</code> attribute is absent.
   */
  public static final EEN16931DateFormatCode DEFAULT = CCYYMMDD;

  private final String m_sID;
  private final String m_sJavaPattern;
  private final boolean m_bHasTime;
  private final DateTimeFormatter m_aFormatter;

  EEN16931DateFormatCode (@NonNull @Nonempty final String sID,
                          @NonNull @Nonempty final String sJavaPattern,
                          final boolean bHasTime)
  {
    this (sID, sJavaPattern, DateTimeFormatter.ofPattern (sJavaPattern), bHasTime);
  }

  EEN16931DateFormatCode (@NonNull @Nonempty final String sID,
                          @NonNull @Nonempty final String sJavaPattern,
                          @NonNull final DateTimeFormatter aFormatter,
                          final boolean bHasTime)
  {
    m_sID = sID;
    m_sJavaPattern = sJavaPattern;
    m_bHasTime = bHasTime;
    m_aFormatter = aFormatter;
  }

  /**
   * The letters of a week based {@link DateTimeFormatter} pattern follow the locale: the first day
   * of the week and the first week of the year differ between countries. UNTDID 2379 fixes both, so
   * the fields are named explicitly.
   */
  @NonNull
  private static DateTimeFormatter _createWeekDateFormatter ()
  {
    return new DateTimeFormatterBuilder ().appendValueReduced (IsoFields.WEEK_BASED_YEAR, 2, 2, 2000)
                                          .appendValue (IsoFields.WEEK_OF_WEEK_BASED_YEAR, 2)
                                          .appendValue (ChronoField.DAY_OF_WEEK, 1)
                                          .toFormatter (Locale.ROOT);
  }

  /**
   * @return The UNTDID 2379 qualifier, e.g. <code>102</code>. This is the value of the CII
   *         <code>format</code> attribute. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The {@link DateTimeFormatter} pattern matching this qualifier, e.g.
   *         <code>uuuuMMdd</code>. The pattern of {@link #YYWWD} is only informative, because its
   *         letters depend on the locale. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getJavaPattern ()
  {
    return m_sJavaPattern;
  }

  /**
   * @return <code>true</code> if this format carries a time of day and not only a date.
   */
  public boolean hasTime ()
  {
    return m_bHasTime;
  }

  /**
   * Parse a value written in this format to a date. For a format that {@link #hasTime()} the time
   * is parsed as well and then dropped.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @return <code>null</code> if the value is empty or does not match this format.
   */
  @Nullable
  public LocalDate parseLocalDate (@Nullable final String sValue)
  {
    if (StringHelper.isEmpty (sValue))
      return null;

    if (m_bHasTime)
    {
      final XMLOffsetDateTime aDateTime = parseDateTime (sValue);
      return aDateTime == null ? null : aDateTime.toLocalDate ();
    }

    return PDTFromString.getLocalDateFromString (sValue, m_aFormatter);
  }

  /**
   * Parse a value written in this format to a date and time.
   *
   * @param sValue
   *        The value to parse. May be <code>null</code>.
   * @return <code>null</code> if the value is empty, if this format does not {@link #hasTime()}, or
   *         if the value does not match this format.
   */
  @Nullable
  public XMLOffsetDateTime parseDateTime (@Nullable final String sValue)
  {
    if (StringHelper.isEmpty (sValue) || !m_bHasTime)
      return null;

    return PDTFromString.getXMLOffsetDateTimeFromString (sValue, m_aFormatter);
  }

  /**
   * Write a date in this format.
   *
   * @param aDate
   *        The date to write. May be <code>null</code>.
   * @return <code>null</code> if the date is <code>null</code> or if this format
   *         {@link #hasTime()}, because a date alone cannot fill it.
   */
  @Nullable
  public String getAsString (@Nullable final LocalDate aDate)
  {
    if (aDate == null || m_bHasTime)
      return null;

    return m_aFormatter.format (aDate);
  }

  /**
   * Find the format with the provided UNTDID 2379 qualifier.
   *
   * @param sID
   *        The qualifier to search, e.g. the value of the CII <code>format</code> attribute. May be
   *        <code>null</code>.
   * @return <code>null</code> if the qualifier is unknown.
   */
  @Nullable
  public static EEN16931DateFormatCode getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EEN16931DateFormatCode.class, sID);
  }

  /**
   * Find the format with the provided UNTDID 2379 qualifier, defaulting to {@link #DEFAULT} if none
   * is given. An unknown qualifier still yields <code>null</code>, because silently treating it as
   * {@link #DEFAULT} would misread the value.
   *
   * @param sID
   *        The qualifier to search. May be <code>null</code> or empty.
   * @return <code>null</code> if a qualifier is present but unknown.
   */
  @Nullable
  public static EEN16931DateFormatCode getFromIDOrDefault (@Nullable final String sID)
  {
    return StringHelper.isEmpty (sID) ? DEFAULT : getFromIDOrNull (sID);
  }
}
