/* StorageService.java

	Purpose:
		
	Description:
		
	History:
		3:50 PM 2022/8/9, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zksandbox.service;

import org.zkoss.zephyr.zpr.IListitem;
import org.zkoss.zephyrex.state.IListboxController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zksandbox.DemoItem;

/**
 * A simple storage service that stores the data in Desktop scope.
 * @author jumperchen
 */
public class StorageService {
	private final static String SELECTED_CATE_ID = "SELECTED_CATE_ID";
	private final static String ITEM_LIST = "ITEM_LIST";

	/**
	 * Returns the selected category id.
	 */
	public String getSelectedCategory() {
		return (String) Executions.getCurrent().getDesktop().getAttribute(
				SELECTED_CATE_ID);
	}

	/**
	 * Sets the selected category id.
	 * @param cateId The category id.
	 */
	public void setSelectedCategory(String cateId) {
		Executions.getCurrent().getDesktop().setAttribute(SELECTED_CATE_ID, cateId);
	}

	/**
	 * Returns the listbox controller if any.
	 */
	public IListboxController<DemoItem, IListitem> getListboxController() {
		return (IListboxController<DemoItem, IListitem>) Executions.getCurrent().getDesktop().getAttribute(
				ITEM_LIST);
	}

	/**
	 * Sets the listbox controller.
	 * @param controller The listbox controller to control model and renderer.
	 */
	public void setListboxController(IListboxController<DemoItem, IListitem> controller) {
		Executions.getCurrent().getDesktop().setAttribute(
				ITEM_LIST, controller);
	}
}
