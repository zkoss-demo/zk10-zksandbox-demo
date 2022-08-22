/* DemoWindowComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10, 2022 3:36:23 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2022 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zksandbox;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.ui.BuildContext;
import org.zkoss.zephyr.ui.ISelectors;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessComposer;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ITab;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.IWindow;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@SuppressWarnings("serial")
public class DemoWindowComposer
		implements StatelessComposer<IWindow<IAnyGroup>> {

	@Action(from = "#reloadBtn", type = Events.ON_CLICK)
	public void onClick$reloadBtn() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("demoView"),
				new ITab.Updater().selected(true));
		EventQueues.lookup("mainLayoutRichlet").publish(new Event("reloadBtn"));
	}

	@Action(from = "#tryBtn", type = Events.ON_CLICK)
	public void onClick$tryBtn(
			@ActionVariable(targetId = "codeView") String code,
			@ActionVariable(targetId = "tryBtn", field = "visible") boolean isVisible) {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("demoView"),
				new ITab.Updater().selected(true));
		execute(code, isVisible);
	}

	public void execute(String code, boolean isVisible) {
		try {
			if (isVisible) {
				UiAgent.getCurrent().replaceChildren(Locator.ofId("view"),
						Immutables.createComponentsDirectly(code, "zul", null));
			} else {
				UiAgent.getCurrent().replaceChildren(Locator.ofId("view"),
						Immutables.createComponents("/macros/warning.zul",
								null));
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public IWindow build(BuildContext<IWindow<IAnyGroup>> ctx) {
		final String code = ISelectors.<ITextbox>findById(ctx.getOwner(),
				"codeView").getValue();

		// post a dummy event to execute the code above later.
		Component dummy = Locator.of("dummy")
				.toComponent((evt, scope) -> execute(code, true));
		Events.postEvent(dummy, new Event("onAfterBuild"));

		List newChildren = new ArrayList(ctx.getOwner().getChildren());
		newChildren.add(0, IDiv.DEFAULT.withStyle("float:right")
				.withChildren(Immutables.createComponents("/bar.zul", null)));

		return ctx.getOwner().withContentSclass("demo-main-cnt")
				.withSclass("demo-main").withChildren(newChildren);
	}
}
