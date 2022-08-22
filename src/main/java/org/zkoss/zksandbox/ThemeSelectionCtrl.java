/* ThemeSelectionCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 30, 2010 12:08:29 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zksandbox;

import org.zkoss.lang.Strings;
import org.zkoss.zephyr.action.data.SelectData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.ui.BuildContext;
import org.zkoss.zephyr.ui.StatelessComposer;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.IListbox;
import org.zkoss.zephyr.zpr.IListitem;
import org.zkoss.zephyrex.state.IListboxController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.theme.Themes;

/**
 * Handles theme selection work. Upon selecting a new theme, it will write
 * zktheme=[theme name] in the cookie, and refresh the current page.
 * @author simon
 */
public class ThemeSelectionCtrl implements StatelessComposer<IDiv<IAnyGroup>> {
	IListboxController<String, IListitem> controller;
	@Action(from = "#themeSelectListbox", type = Events.ON_SELECT)
	public void doSelect$themeSelectListbox(SelectData data){
		// get current theme
		String currentTheme = Themes.getCurrentTheme();
		
		// get selected theme from listbox
		String selectedTheme = controller.getSelectedObject();
		if (selectedTheme == null)
			return;
		
		if(selectedTheme.equals(currentTheme))
			return;
		
		// write cookie, redirect
		Themes.setTheme(Executions.getCurrent(), selectedTheme);
		Executions.sendRedirect(null);
	}

	public IDiv<IAnyGroup> build(BuildContext<IDiv<IAnyGroup>> ctx) {
		Double version = Executions.getCurrent().getBrowser("mobile");
		if (version != null && version > 1) {
			return ctx.getOwner().withVisible(false);
		}
		controller = IListboxController.of(
				(IListbox) ctx.getOwner().getChildren().get(0),
				new ListModelList<>(Themes.getThemes()),
				(name, index) -> IListitem.of(name).withWidgetClass("zul.sel.Option").withMold("select")); // for select mold
			String name = Themes.getCurrentTheme();
		for (int i = 0, j = controller.getModel().getSize(); i < j; i++) {
			if (name.equals(controller.getModel().getElementAt(i))) {
				controller.setSelectedIndex(i);
				break;
			}
		}
		return ctx.getOwner().withVisible(!Strings.isEmpty(name)).withChildren(controller.build());
	}
}
