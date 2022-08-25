/* MainLayoutRichletTest.java

	Purpose:
		
	Description:
		
	History:
		11:31 AM 2022/8/25, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zksandbox.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * A simple test case for MainLayoutRichlet.
 * @author jumperchen
 */
public class MainLayoutRichletTest extends WebDriverTestCase {
	@Test
	public void testMainLayout() {
		connect("");

		Widget $itemList = jq("$itemList").toWidget();
		assertEquals(8, $itemList.nChildren());

		assertEquals("Hello World", $itemList.firstChild().get("label"));

		assertEquals("Demo", jq("$demoView").text());
		assertEquals("View Source", jq("$srcView").text());
		assertEquals("Pure Java", jq("$javaView").text());
		assertEquals("My First Window\nHello, World!\n", jq("$view").text());

		assertTrue(jq("$view").isVisible());

		click(jq("$srcView"));
		waitResponse();
		assertFalse(jq("$view").isVisible());
	}

	@Test
	public void testBookmarkChange() {
		connect("/#l6");

		waitResponse();

		Widget $itemList = jq("$itemList").toWidget();
		assertEquals(12, $itemList.nChildren());

		assertEquals("Window", jq("$itemList .z-listitem.z-listitem-selected").text().trim());

		assertNoZKError();

		assertEquals("Demo", jq("$demoView").text());
		assertEquals("View Source", jq("$srcView").text());
		assertFalse(jq("$javaView").exists());

		click($itemList.firstChild());
		waitResponse();

		assertEquals("Border Layout", jq("$itemList .z-listitem.z-listitem-selected").text().trim());

		click(jq("$srcView"));
		waitResponse();
		assertFalse(jq("$demoView").toWidget().is("selected"));
		assertTrue(jq("$tryBtn").isVisible());

		type(jq("$codeView"), "<div>Dummy</div>");
		click(jq("$tryBtn"));
		waitResponse();
		assertTrue(jq("$demoView").toWidget().is("selected"));
		assertEquals("Dummy", jq("$view").text());

		click(jq("$srcView"));
		waitResponse();
		assertTrue(jq("$reloadBtn").isVisible());
		click(jq("$reloadBtn"));
		waitResponse();
		assertTrue(jq("$demoView").toWidget().is("selected"));
		assertNotEquals("Dummy", jq("$view").text());

		getWebDriver().navigate().back();
		waitResponse();

		assertEquals("Window", jq("$itemList .z-listitem.z-listitem-selected").text().trim());
		assertNoZKError();

		click(jq("$c3"));
		waitResponse();
		assertEquals(12, $itemList.nChildren());
		click(jq("$itemList .z-listcell-content:contains(Master)"));
		waitResponse();

		assertNoZKError();
	}
}
