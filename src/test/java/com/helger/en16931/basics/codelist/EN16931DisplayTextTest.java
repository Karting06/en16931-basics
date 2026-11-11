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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

import com.helger.text.display.IHasDisplayText;

/**
 * Test class for the localized names of the code list enums.
 *
 * @author Philip Helger
 */
public final class EN16931DisplayTextTest
{
  private static final Locale SPANISH = Locale.forLanguageTag ("es");

  @Test
  public void testGermanNames ()
  {
    assertEquals ("Regelsteuersatz", EEN16931TaxCategoryCode.S.getDisplayText (Locale.GERMAN));
    assertEquals ("Regelsteuersatz", EEN16931TaxCategoryCode.S.getDisplayText (Locale.GERMANY));
    assertEquals ("SEPA-\u00dcberweisung", EEN16931PaymentMeansCode._58.getDisplayText (Locale.GERMAN));
  }

  @Test
  public void testInvoiceTypeRole ()
  {
    assertEquals ("Rechnung", EEN16931InvoiceTypeCodeRole.INVOICE.getDisplayText (Locale.GERMAN));
    assertEquals ("Gutschrift", EEN16931InvoiceTypeCodeRole.CREDIT_NOTE.getDisplayText (Locale.GERMAN));
    assertEquals ("Credit Note", EEN16931InvoiceTypeCodeRole.CREDIT_NOTE.getDisplayText (Locale.ENGLISH));
    assertEquals ("Avoir", EEN16931InvoiceTypeCodeRole.CREDIT_NOTE.getDisplayText (Locale.FRENCH));
    assertEquals ("Nota de cr\u00e9dito", EEN16931InvoiceTypeCodeRole.CREDIT_NOTE.getDisplayText (SPANISH));
  }

  @Test
  public void testInvoiceTypeCodes ()
  {
    assertEquals ("Handelsrechnung", EEN16931InvoiceTypeCode._380.getDisplayText (Locale.GERMAN));
    assertEquals ("Gutschrift", EEN16931InvoiceTypeCode._381.getDisplayText (Locale.GERMANY));
    assertEquals ("Commercial invoice", EEN16931InvoiceTypeCode._380.getDisplayText (Locale.ENGLISH));
    assertEquals ("Facture commerciale", EEN16931InvoiceTypeCode._380.getDisplayText (Locale.FRENCH));
    assertEquals ("Facture commerciale", EEN16931InvoiceTypeCode._380.getDisplayText (Locale.CANADA_FRENCH));
    assertEquals ("Factura comercial", EEN16931InvoiceTypeCode._380.getDisplayText (SPANISH));
    for (final EEN16931InvoiceTypeCode e : EEN16931InvoiceTypeCode.values ())
    {
      assertNotEquals (e.getID (), e.getDisplayName (), e.getDisplayText (Locale.GERMAN));
      assertNotEquals (e.getID (), e.getDisplayName (), e.getDisplayText (Locale.FRENCH));
      assertNotEquals (e.getID (), e.getDisplayName (), e.getDisplayText (SPANISH));
    }
  }

  @Test
  public void testSpanishAndFrenchNames ()
  {
    assertEquals ("Tipo general", EEN16931TaxCategoryCode.S.getDisplayText (SPANISH));
    assertEquals ("Tipo general", EEN16931TaxCategoryCode.S.getDisplayText (Locale.forLanguageTag ("es-ES")));
    assertEquals ("Taux normal", EEN16931TaxCategoryCode.S.getDisplayText (Locale.FRENCH));
    assertEquals ("Taux normal", EEN16931TaxCategoryCode.S.getDisplayText (Locale.FRANCE));
    assertEquals ("Transferencia SEPA", EEN16931PaymentMeansCode._58.getDisplayText (SPANISH));
    assertEquals ("Virement SEPA", EEN16931PaymentMeansCode._58.getDisplayText (Locale.FRENCH));
    assertEquals ("Exon\u00e9r\u00e9 de TVA pour livraison intracommunautaire de biens et de services dans l'EEE",
                  EEN16931TaxCategoryCode.K.getDisplayText (Locale.FRENCH));
    // German is not affected
    assertEquals ("Regelsteuersatz", EEN16931TaxCategoryCode.S.getDisplayText (Locale.GERMAN));
  }

  @Test
  public void testEnglishIsTheFallBack ()
  {
    for (final Locale aLocale : new Locale [] { Locale.ENGLISH, Locale.US, Locale.ITALIAN, Locale.ROOT })
    {
      assertEquals ("Standard rate", EEN16931TaxCategoryCode.S.getDisplayText (aLocale));
      assertEquals ("SEPA credit transfer", EEN16931PaymentMeansCode._58.getDisplayText (aLocale));
    }
    assertEquals ("Commercial invoice", EEN16931InvoiceTypeCode._380.getDisplayText (Locale.ITALIAN));
    // Code lists without a translation
    assertEquals (EEN16931AllowanceReasonCode._100.getDisplayName (),
                  EEN16931AllowanceReasonCode._100.getDisplayText (Locale.GERMAN));
    assertEquals (EEN16931AllowanceReasonCode._100.getDisplayName (),
                  EEN16931AllowanceReasonCode._100.getDisplayText (Locale.FRENCH));
    assertEquals (EEN16931AllowanceReasonCode._100.getDisplayName (),
                  EEN16931AllowanceReasonCode._100.getDisplayText (SPANISH));
    // Payment means without a translation
    assertEquals (EEN16931PaymentMeansCode._1.getDisplayName (),
                  EEN16931PaymentMeansCode._1.getDisplayText (Locale.FRENCH));
  }

  @Test
  public void testCountryAndCurrencyFromTheJdk ()
  {
    assertEquals ("Deutschland", EEN16931CountryCode.DE.getDisplayText (Locale.GERMAN));
    assertEquals ("Germany", EEN16931CountryCode.DE.getDisplayText (Locale.ENGLISH));
    assertEquals ("Euro", EEN16931CurrencyCode.EUR.getDisplayText (Locale.GERMAN));
    // Codes of EN 16931 that are no ISO 3166-1 codes
    assertNotNull (EEN16931CountryCode.getFromIDOrNull ("XI"));
    assertEquals (EEN16931CountryCode.getFromIDOrNull ("XI").getDisplayName (),
                  EEN16931CountryCode.getFromIDOrNull ("XI").getDisplayText (Locale.GERMAN));
    assertEquals (EEN16931CountryCode.getFromIDOrNull ("1A").getDisplayName (),
                  EEN16931CountryCode.getFromIDOrNull ("1A").getDisplayText (Locale.GERMAN));
  }

  @Test
  public void testEnumsAreDisplayTexts ()
  {
    final IHasDisplayText aText = EEN16931TaxCategoryCode.S;
    assertEquals ("Regelsteuersatz", aText.getDisplayText (Locale.GERMAN));
  }
}
