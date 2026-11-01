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
package com.helger.en16931.basics.codelist;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.misc.Translatable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.id.IHasID;
import com.helger.base.name.IHasDisplayName;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.text.IMultilingualText;
import com.helger.text.display.IHasDisplayText;
import com.helger.text.resolve.DefaultTextResolver;
import com.helger.text.util.TextHelper;

/**
 * UNTDID 5153 duty or tax or fee type name codes.
 * <p>
 * Used by BT-177 and BT-193, the non-VAT tax codes of the 2026 edition, where the list identifier
 * {@link EN16931CodeLists#NON_VAT_TAX_CODE_LIST_ID} tells them from the UNTDID 7161 codes of
 * {@link EEN16931ChargeReasonCode} in the same element. In BT-193-1 of UBL that identifier may be
 * omitted. Some codes are part of both lists with a different meaning, like <code>AAA</code>.
 * <p>
 * Source of truth: UNTDID 5153 of the D24A directory. This is not a sheet of the EN 16931 code list
 * workbook. The binding names <code>VAT</code> as the one code that BT-177 and BT-193 must not
 * use, and it stays part of this enum because it is part of the code list.
 *
 * @author Philip Helger
 */
@Translatable
@CodingStyleguideUnaware
public enum EEN16931DutyTaxFeeTypeCode implements IHasID <String>, IHasDisplayName, IHasDisplayText
{
  /** <code>AAA</code> - Petroleum tax */
  AAA ("AAA", "Petroleum tax"),
  /** <code>AAB</code> - Provisional countervailing duty cash */
  AAB ("AAB", "Provisional countervailing duty cash"),
  /** <code>AAC</code> - Provisional countervailing duty bond */
  AAC ("AAC", "Provisional countervailing duty bond"),
  /** <code>AAD</code> - Tobacco tax */
  AAD ("AAD", "Tobacco tax"),
  /** <code>AAE</code> - Energy fee */
  AAE ("AAE", "Energy fee"),
  /** <code>AAF</code> - Coffee tax */
  AAF ("AAF", "Coffee tax"),
  /** <code>AAG</code> - Harmonised sales tax, Canadian */
  AAG ("AAG", "Harmonised sales tax, Canadian"),
  /** <code>AAH</code> - Quebec sales tax */
  AAH ("AAH", "Quebec sales tax"),
  /** <code>AAI</code> - Canadian provincial sales tax */
  AAI ("AAI", "Canadian provincial sales tax"),
  /** <code>AAJ</code> - Tax on replacement part */
  AAJ ("AAJ", "Tax on replacement part"),
  /** <code>AAK</code> - Mineral oil tax */
  AAK ("AAK", "Mineral oil tax"),
  /** <code>AAL</code> - Special tax */
  AAL ("AAL", "Special tax"),
  /** <code>AAM</code> - Insurance tax */
  AAM ("AAM", "Insurance tax"),
  /** <code>AAO</code> - Provincial Cannabis Tax */
  AAO ("AAO", "Provincial Cannabis Tax"),
  /** <code>AAP</code> - Outstanding duty interest */
  AAP ("AAP", "Outstanding duty interest"),
  /** <code>ADD</code> - Anti-dumping duty */
  ADD ("ADD", "Anti-dumping duty"),
  /** <code>BOL</code> - Stamp duty (Imposta di Bollo) */
  BOL ("BOL", "Stamp duty (Imposta di Bollo)"),
  /** <code>CAP</code> - Agricultural levy */
  CAP ("CAP", "Agricultural levy"),
  /** <code>CAR</code> - Car tax */
  CAR ("CAR", "Car tax"),
  /** <code>COC</code> - Paper consortium tax (Italy) */
  COC ("COC", "Paper consortium tax (Italy)"),
  /** <code>CST</code> - Commodity specific tax */
  CST ("CST", "Commodity specific tax"),
  /** <code>CUD</code> - Customs duty */
  CUD ("CUD", "Customs duty"),
  /** <code>CVD</code> - Countervailing duty */
  CVD ("CVD", "Countervailing duty"),
  /** <code>ENV</code> - Environmental tax */
  ENV ("ENV", "Environmental tax"),
  /** <code>EXC</code> - Excise duty */
  EXC ("EXC", "Excise duty"),
  /** <code>EXP</code> - Agricultural export rebate */
  EXP ("EXP", "Agricultural export rebate"),
  /** <code>FET</code> - Federal excise tax */
  FET ("FET", "Federal excise tax"),
  /** <code>FRE</code> - Free */
  FRE ("FRE", "Free"),
  /** <code>GCN</code> - General construction tax */
  GCN ("GCN", "General construction tax"),
  /** <code>GST</code> - Goods and services tax */
  GST ("GST", "Goods and services tax"),
  /** <code>ILL</code> - Illuminants tax */
  ILL ("ILL", "Illuminants tax"),
  /** <code>IMP</code> - Import tax */
  IMP ("IMP", "Import tax"),
  /** <code>IND</code> - Individual tax */
  IND ("IND", "Individual tax"),
  /** <code>LAC</code> - Business license fee */
  LAC ("LAC", "Business license fee"),
  /** <code>LCN</code> - Local construction tax */
  LCN ("LCN", "Local construction tax"),
  /** <code>LDP</code> - Light dues payable */
  LDP ("LDP", "Light dues payable"),
  /** <code>LOC</code> - Local sales tax */
  LOC ("LOC", "Local sales tax"),
  /** <code>LST</code> - Lust tax */
  LST ("LST", "Lust tax"),
  /** <code>MCA</code> - Monetary compensatory amount */
  MCA ("MCA", "Monetary compensatory amount"),
  /** <code>MCD</code> - Miscellaneous cash deposit */
  MCD ("MCD", "Miscellaneous cash deposit"),
  /** <code>OTH</code> - Other taxes */
  OTH ("OTH", "Other taxes"),
  /** <code>PDB</code> - Provisional duty bond */
  PDB ("PDB", "Provisional duty bond"),
  /** <code>PDC</code> - Provisional duty cash */
  PDC ("PDC", "Provisional duty cash"),
  /** <code>PRF</code> - Preference duty */
  PRF ("PRF", "Preference duty"),
  /** <code>SCN</code> - Special construction tax */
  SCN ("SCN", "Special construction tax"),
  /** <code>SSS</code> - Shifted social securities */
  SSS ("SSS", "Shifted social securities"),
  /** <code>STT</code> - State/provincial sales tax */
  STT ("STT", "State/provincial sales tax"),
  /** <code>SUP</code> - Suspended duty */
  SUP ("SUP", "Suspended duty"),
  /** <code>SUR</code> - Surtax */
  SUR ("SUR", "Surtax"),
  /** <code>SWT</code> - Shifted wage tax */
  SWT ("SWT", "Shifted wage tax"),
  /** <code>TAC</code> - Alcohol mark tax */
  TAC ("TAC", "Alcohol mark tax"),
  /** <code>TOT</code> - Total */
  TOT ("TOT", "Total"),
  /** <code>TOX</code> - Turnover tax */
  TOX ("TOX", "Turnover tax"),
  /** <code>TTA</code> - Tonnage taxes */
  TTA ("TTA", "Tonnage taxes"),
  /** <code>VAD</code> - Valuation deposit */
  VAD ("VAD", "Valuation deposit"),
  /** <code>VAT</code> - Value added tax */
  VAT ("VAT", "Value added tax");

  private static final ICommonsMap <String, EEN16931DutyTaxFeeTypeCode> MAP_BY_ID = new CommonsHashMap <> ();

  static
  {
    for (final EEN16931DutyTaxFeeTypeCode e : values ())
      MAP_BY_ID.put (e.m_sID, e);
  }

  private final String m_sID;
  private final String m_sDisplayName;
  private final IMultilingualText m_aTP;

  EEN16931DutyTaxFeeTypeCode (@NonNull @Nonempty final String sID, @NonNull @Nonempty final String sDisplayName)
  {
    m_sID = sID;
    m_sDisplayName = sDisplayName;
    m_aTP = TextHelper.create_EN (sDisplayName);
  }

  /**
   * @return The code as defined by the code list. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The name of the code as defined by the code list. Neither <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  /**
   * Get the name of the code in the language of the passed locale. English is the fall back
   * language.
   *
   * @param aContentLocale
   *        The locale to use. May not be <code>null</code>.
   * @return The name in the requested language or the English name. Neither <code>null</code> nor
   *         empty.
   */
  @NonNull
  @Nonempty
  public String getDisplayText (@NonNull final Locale aContentLocale)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");
    final String ret = DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
    return ret != null ? ret : m_sDisplayName;
  }

  /**
   * Find the code list entry with the provided code.
   *
   * @param sID
   *        The code to search. May be <code>null</code>.
   * @return <code>null</code> if the code list contains no such code.
   */
  @Nullable
  public static EEN16931DutyTaxFeeTypeCode getFromIDOrNull (@Nullable final String sID)
  {
    return MAP_BY_ID.get (sID);
  }

  /**
   * Check if the provided code is part of this code list.
   *
   * @param sID
   *        The code to check. May be <code>null</code>.
   * @return <code>true</code> if the code list contains the code.
   */
  public static boolean containsID (@Nullable final String sID)
  {
    return MAP_BY_ID.containsKey (sID);
  }
}
