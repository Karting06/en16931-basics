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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.function.Function;

import org.junit.Test;

import com.helger.base.id.IHasID;
import com.helger.base.name.IHasDisplayName;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;

/**
 * Test class for the code list enums generated from the EN 16931 code list workbook.
 * <p>
 * The counts asserted below are the row counts of the respective sheet of the workbook named in
 * {@link EN16931CodeLists#CODE_LIST_VERSION}. If one of them changes, a new version of the workbook
 * was transcribed and {@link EN16931CodeLists#CODE_LIST_VERSION} has to change with it. The one
 * exception is {@link EEN16931DutyTaxFeeTypeCode}, which is not part of the workbook.
 *
 * @author Philip Helger
 */
public final class EN16931CodeListEnumsTest
{
  /**
   * Assert that the code list is complete, that no code appears twice and that every code can be
   * looked up again.
   *
   * @param nExpectedCount
   *        The number of data rows of the sheet.
   * @param aValues
   *        All enum values.
   * @param aLookup
   *        The <code>getFromIDOrNull</code> of the enum.
   */
  private static <T extends IHasID <String>> void _assertCodeList (final int nExpectedCount,
                                                                   final T [] aValues,
                                                                   final Function <String, T> aLookup)
  {
    assertEquals (nExpectedCount, aValues.length);

    final ICommonsSet <String> aCodes = new CommonsHashSet <> ();
    for (final T aValue : aValues)
    {
      final String sID = aValue.getID ();
      assertTrue ("Code must not be empty", sID.length () > 0);
      assertTrue ("Code '" + sID + "' is contained twice", aCodes.add (sID));
      assertSame ("Code '" + sID + "' cannot be looked up", aValue, aLookup.apply (sID));

      if (aValue instanceof final IHasDisplayName aDN)
        assertTrue ("Code '" + sID + "' has no name", aDN.getDisplayName ().length () > 0);
    }

    assertNull (aLookup.apply ("this-is-surely-not-a-code"));
    assertNull (aLookup.apply (""));
    assertNull (aLookup.apply (null));
  }

  @Test
  public void testAllCodeListsAreComplete ()
  {
    _assertCodeList (251, EEN16931CountryCode.values (), EEN16931CountryCode::getFromIDOrNull);
    _assertCodeList (178, EEN16931CurrencyCode.values (), EEN16931CurrencyCode::getFromIDOrNull);
    _assertCodeList (246, EEN16931ICDCode.values (), EEN16931ICDCode::getFromIDOrNull);
    _assertCodeList (62, EEN16931InvoiceTypeCode.values (), EEN16931InvoiceTypeCode::getFromIDOrNull);
    _assertCodeList (818, EEN16931ReferenceQualifierCode.values (), EEN16931ReferenceQualifierCode::getFromIDOrNull);
    _assertCodeList (401, EEN16931TextSubjectCode.values (), EEN16931TextSubjectCode::getFromIDOrNull);
    _assertCodeList (84, EEN16931PaymentMeansCode.values (), EEN16931PaymentMeansCode::getFromIDOrNull);
    _assertCodeList (9, EEN16931TaxCategoryCode.values (), EEN16931TaxCategoryCode::getFromIDOrNull);
    _assertCodeList (19, EEN16931AllowanceReasonCode.values (), EEN16931AllowanceReasonCode::getFromIDOrNull);
    _assertCodeList (185, EEN16931ItemTypeCode.values (), EEN16931ItemTypeCode::getFromIDOrNull);
    _assertCodeList (178, EEN16931ChargeReasonCode.values (), EEN16931ChargeReasonCode::getFromIDOrNull);
    _assertCodeList (56, EEN16931DutyTaxFeeTypeCode.values (), EEN16931DutyTaxFeeTypeCode::getFromIDOrNull);
    _assertCodeList (6, EEN16931MimeCode.values (), EEN16931MimeCode::getFromIDOrNull);
    _assertCodeList (103, EEN16931EASCode.values (), EEN16931EASCode::getFromIDOrNull);
    _assertCodeList (106, EEN16931VATEXCode.values (), EEN16931VATEXCode::getFromIDOrNull);
    _assertCodeList (1, EEN16931TaxRegistrationCode.values (), EEN16931TaxRegistrationCode::getFromIDOrNull);
    // The unit codes of BT-130 and BT-150 are deliberately not an enum - see EN16931CodeLists
  }

  @Test
  public void testCountry ()
  {
    assertEquals ("Germany", EEN16931CountryCode.DE.getDisplayName ());
    // The list is extended beyond ISO 3166-1
    assertNotNull (EEN16931CountryCode._1A);
    assertNotNull (EEN16931CountryCode.XI);
  }

  @Test
  public void testCurrency ()
  {
    assertEquals ("Euro", EEN16931CurrencyCode.EUR.getDisplayName ());
    // Added in v17
    assertNotNull (EEN16931CurrencyCode.getFromIDOrNull ("XCG"));
    // Removed in v17, and ANG was only really removed in v17b
    assertNull (EEN16931CurrencyCode.getFromIDOrNull ("ANG"));
    assertNull (EEN16931CurrencyCode.getFromIDOrNull ("BGN"));
  }

  @Test
  public void testICDAndEAS ()
  {
    // Added in v17
    for (final String sCode : new String [] { "0245", "0246", "0247", "0248" })
      assertNotNull (sCode, EEN16931ICDCode.getFromIDOrNull (sCode));
    for (final String sCode : new String [] { "0242", "0245", "0246", "0248" })
      assertNotNull (sCode, EEN16931EASCode.getFromIDOrNull (sCode));

    // Added in v18
    for (final String sCode : new String [] { "0249", "0250", "0251" })
      assertNotNull (sCode, EEN16931ICDCode.getFromIDOrNull (sCode));
    assertNotNull (EEN16931EASCode.getFromIDOrNull ("9960"));

    // The EAS list is a subset of the ICD list plus a few non numeric schemes
    assertNotNull (EEN16931EASCode.AS);
    assertNull (EEN16931ICDCode.getFromIDOrNull ("AS"));
  }

  @Test
  public void testInvoiceTypeCodeMatchesEN16931CodeLists ()
  {
    int nInvoice = 0;
    int nCreditNote = 0;
    for (final EEN16931InvoiceTypeCode e : EEN16931InvoiceTypeCode.values ())
      if (e.getRole () == EEN16931InvoiceTypeCodeRole.INVOICE)
      {
        nInvoice++;
        assertTrue (e.getID (), EN16931CodeLists.isInvoiceTypeCode (e.getID ()));
        assertFalse (e.getID (), EN16931CodeLists.isCreditNoteTypeCode (e.getID ()));
      }
      else
      {
        nCreditNote++;
        assertTrue (e.getID (), EN16931CodeLists.isCreditNoteTypeCode (e.getID ()));
        assertFalse (e.getID (), EN16931CodeLists.isInvoiceTypeCode (e.getID ()));
      }
    assertEquals (49, nInvoice);
    assertEquals (13, nCreditNote);

    assertSame (EEN16931InvoiceTypeCodeRole.INVOICE, EEN16931InvoiceTypeCode._380.getRole ());
    assertSame (EEN16931InvoiceTypeCodeRole.CREDIT_NOTE, EEN16931InvoiceTypeCode._381.getRole ());
    // "81" is a Credit Note in every version of the code list
    assertSame (EEN16931InvoiceTypeCodeRole.CREDIT_NOTE, EEN16931InvoiceTypeCode._81.getRole ());

    // BT-17 uses "50", which is not part of the BT-3 subset
    assertNull (EEN16931InvoiceTypeCode.getFromIDOrNull (EN16931CodeLists.DOCUMENT_TYPE_CODE_ORIGINATOR_DOCUMENT));
  }

  @Test
  public void testDueDateTypeCodeMatchesEN16931CodeLists ()
  {
    assertEquals (3, EEN16931DueDateTypeCode.values ().length);
    for (final EEN16931DueDateTypeCode e : EEN16931DueDateTypeCode.values ())
    {
      assertEquals (e.getUBLCode (), EN16931CodeLists.mapDueDateTypeCodeCIIToUBL (e.getCIICode ()));
      assertEquals (e.getCIICode (), EN16931CodeLists.mapDueDateTypeCodeUBLToCII (e.getUBLCode ()));
      assertSame (e, EEN16931DueDateTypeCode.getFromUBLCodeOrNull (e.getUBLCode ()));
      assertSame (e, EEN16931DueDateTypeCode.getFromCIICodeOrNull (e.getCIICode ()));
    }
    assertNull (EEN16931DueDateTypeCode.getFromUBLCodeOrNull ("5"));
    assertNull (EEN16931DueDateTypeCode.getFromCIICodeOrNull ("3"));
    assertNull (EEN16931DueDateTypeCode.getFromUBLCodeOrNull (null));
    assertNull (EEN16931DueDateTypeCode.getFromCIICodeOrNull (null));
  }

  @Test
  public void testTaxSchemeAndCategoryScheme ()
  {
    assertEquals ("VAT", EEN16931TaxSchemeCode.VAT.getUBLCode ());
    assertEquals ("VA", EEN16931TaxSchemeCode.VAT.getCIICode ());
    assertSame (EEN16931TaxSchemeCode.VAT, EEN16931TaxSchemeCode.getFromUBLCodeOrNull ("VAT"));
    assertSame (EEN16931TaxSchemeCode.VAT, EEN16931TaxSchemeCode.getFromCIICodeOrNull ("VA"));
    assertNull (EEN16931TaxSchemeCode.getFromCIICodeOrNull ("VAT"));

    // The 2017 UBL binding: VAT is BT-31, everything else BT-32
    assertSame (EEN16931TaxSchemeCode.VAT, EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull ("VAT"));
    assertSame (EEN16931TaxSchemeCode.VAT, EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull (" vat "));
    assertSame (EEN16931TaxSchemeCode.LOC, EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull ("LOC"));
    assertSame (EEN16931TaxSchemeCode.LOC, EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull ("CUST"));
    assertSame (EEN16931TaxSchemeCode.LOC, EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull ("TAX"));
    assertNull (EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull (null));
    assertNull (EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull (""));
    assertNull (EEN16931TaxSchemeCode.getFromUBLCodeEN2017OrNull ("  "));

    // Here both syntaxes use the same code
    assertEquals ("VAT", EEN16931TaxCategorySchemeCode.VAT.getUBLCode ());
    assertEquals ("VAT", EEN16931TaxCategorySchemeCode.VAT.getCIICode ());

    assertEquals ("FC", EEN16931TaxRegistrationCode.FC.getID ());
  }

  @Test
  public void testPaymentMeans ()
  {
    // The codes EN16931CodeLists classifies must all be part of the code list
    for (final String sCode : new String [] { "30", "42", "58", "48", "49", "59" })
      assertNotNull (sCode, EEN16931PaymentMeansCode.getFromIDOrNull (sCode));

    assertEquals ("SEPA", EEN16931PaymentMeansCode._58.getUsage ());
    assertEquals ("Instrument not defined", EEN16931PaymentMeansCode._1.getDisplayName ());
    // Most codes carry no usage note
    assertNull (EEN16931PaymentMeansCode._1.getUsage ());
  }

  @Test
  public void testTaxCategory ()
  {
    // The UNTDID name and the EN 16931 semantic model name differ in wording for some codes
    assertEquals ("Standard rate", EEN16931TaxCategoryCode.S.getDisplayName ());
    assertEquals ("Standard rate", EEN16931TaxCategoryCode.S.getSemanticModelName ());
    assertEquals ("Tax for production, services and importation in Ceuta and Melilla",
                  EEN16931TaxCategoryCode.M.getDisplayName ());
    assertEquals ("Liable for IPSI", EEN16931TaxCategoryCode.M.getSemanticModelName ());
  }

  @Test
  public void testMimeAndVATEX ()
  {
    assertEquals ("application/pdf", EEN16931MimeCode.APPLICATION_PDF.getID ());
    assertNull (EEN16931MimeCode.getFromIDOrNull ("application/xml"));

    assertNotNull (EEN16931VATEXCode.getFromIDOrNull ("VATEX-EU-AE"));
    assertNotNull (EEN16931VATEXCode.getFromIDOrNull ("VATEX-EU-D"));
    // Most codes carry no remark
    assertNull (EEN16931VATEXCode.VATEX_EU_132.getRemark ());
  }

  @Test
  public void testVATEXIsCaseInsensitive ()
  {
    // BR-CL-22 of the CEN Schematron compares upper-case(.), the Peppol rules upper-case(cbc:TaxExemptionReasonCode)
    assertSame (EEN16931VATEXCode.VATEX_EU_AE, EEN16931VATEXCode.getFromIDOrNull ("VATEX-EU-AE"));
    assertSame (EEN16931VATEXCode.VATEX_EU_AE, EEN16931VATEXCode.getFromIDOrNull ("vatex-eu-ae"));
    assertSame (EEN16931VATEXCode.VATEX_EU_AE, EEN16931VATEXCode.getFromIDOrNull ("Vatex-Eu-Ae"));
    assertTrue (EEN16931VATEXCode.containsID ("vatex-eu-132-1a"));
    assertFalse (EEN16931VATEXCode.containsID ("VATEX-EU-XX"));
    assertFalse (EEN16931VATEXCode.containsID (null));
    assertNull (EEN16931VATEXCode.getFromIDOrNull (null));
    // The single upper case lookup relies on this
    for (final EEN16931VATEXCode e : EEN16931VATEXCode.values ())
      assertEquals (e.getID (), e.getID ().toUpperCase (Locale.ROOT));
  }
}
