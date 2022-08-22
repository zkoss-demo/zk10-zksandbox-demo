/* MainLayoutRichlet.java

	Purpose:
		
	Description:
		
	History:
		4:32 PM 2022/8/8, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zksandbox.ui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.ServletRequest;

import org.zkoss.web.fn.ServletFns;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zephyr.action.ActionTarget;
import org.zkoss.zephyr.action.data.BookmarkData;
import org.zkoss.zephyr.action.data.InputData;
import org.zkoss.zephyr.action.data.KeyData;
import org.zkoss.zephyr.action.data.SelectData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zephyr.zpr.IA;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IBorderlayout;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.ICenter;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.IImage;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ILayoutRegion;
import org.zkoss.zephyr.zpr.IListbox;
import org.zkoss.zephyr.zpr.IListcell;
import org.zkoss.zephyr.zpr.IListitem;
import org.zkoss.zephyr.zpr.INorth;
import org.zkoss.zephyr.zpr.IPanel;
import org.zkoss.zephyr.zpr.IPanelchildren;
import org.zkoss.zephyr.zpr.IScript;
import org.zkoss.zephyr.zpr.IStyle;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.IToolbar;
import org.zkoss.zephyr.zpr.IWest;
import org.zkoss.zephyrex.state.IListboxController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zksandbox.Category;
import org.zkoss.zksandbox.DemoItem;
import org.zkoss.zksandbox.DemoWebAppInit;
import org.zkoss.zksandbox.service.StorageService;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ext.Selectable;

/**
 * The main layout richlet to handle the layout stuff in a stateless manner for zk sandbox demo.
 * @author jumperchen
 */
@RichletMapping("/")
public class MainLayoutRichlet implements StatelessRichlet {
	private final static IDiv<IAnyGroup> CATEGORY_BAR = IDiv.DEFAULT.withWidgetClass("zksandbox.Categorybar");

	private final static IButton CATEGORY = IButton.DEFAULT.withWidgetClass("zksandbox.Category");

	private final StorageService service = new StorageService();
	private static DemoItem[] ITEMS;

	public void service(Page page) throws Exception {
		StatelessRichlet.super.service(page);
		// init event queue
		EventQueues.lookup("mainLayoutRichlet").subscribe((event -> {
			UiAgent.getCurrent().replaceChildren(Locator.ofId("xcontents"), Immutables.createComponents(service.getListboxController().getSelectedObject().getFile(), null));
		}));
	}

	private Map<String, Category> getCategoryMap() {
		ServletRequest request = ServletFns.getCurrentRequest();
		return Servlets.getBrowser(request, "mobile") == null ?
				DemoWebAppInit.getCateMap()	: DemoWebAppInit.getMobilCateMap();
	}

	private void initHeaderInfo(Page page) {
		page.setTitle("ZK Sandbox");
		PageCtrl pc = (PageCtrl) page;
		pc.addBeforeHeadTags("<meta name=\"keywords\" content=\"ZK, Ajax, Mobile, Framework, Ajax framekwork, RIA, Direct RIA, Java, JSP, JSF, Open Source, Comet\">");

		// after ZK JS files
		pc.addAfterHeadTags("<link rel=\"stylesheet\" type=\"text/css\" href=\"macros/zksandbox.css.dsp?v=" + page.getDesktop().getWebApp().getBuild() + "\">");
		pc.addAfterHeadTags("<script type=\"text/javascript\" src=\"macros/zksandbox.js.dsp?v=" + page.getDesktop().getWebApp().getBuild() + "\"></script>");
	}

	@RichletMapping("")
	public List index() {
		initHeaderInfo(((ExecutionCtrl)Executions.getCurrent()).getCurrentPage());
		Double number = Executions.getCurrent().getBrowser("mobile");
		return Arrays.asList(
				IStyle.of(".z-html {display: block;}"),
				number != null ? IStyle.ofSrc("~./style/index.css.dsp") : IStyle.DEFAULT,
				initMainLayout(),
				"www.zkoss.org".equals(Executions.getCurrent().getServerName()) ||
						"www.potix.com".equals(Executions.getCurrent().getServerName()) ?
						IScript.ofSrc("macros/ga.js") : IScript.ofSrc("")
		);
	}

	private Category[] getCategories() {
		return getCategoryMap().values()
				.toArray(new Category[] {});
	}

	private DemoItem[] getItems() {
		if (ITEMS == null) {
			LinkedList<DemoItem> items = new LinkedList<>();
			Arrays.stream(getCategories()).map(Category::getItems).forEach(items::addAll);
			ITEMS = items.toArray(new DemoItem[0]);
		}
		return ITEMS;
	}

	private ListModel<DemoItem> getSelectedModel() {
		String selectedCategory = service.getSelectedCategory();
		Category cate = selectedCategory != null ?
				getCategory(selectedCategory) :
				getCategories()[0];
		return new ListModelList<>(cate.getItems(), true);
	}

	private Category getCategory(String cateId) {
		return getCategoryMap().get(cateId);
	}
	@Action(from = "#main", type = Events.ON_BOOKMARK_CHANGE)
	public void doBookmarkChange$main(UiAgent uiAgent, BookmarkData data) {
		String id = data.getBookmark();
		if (id.length() > 0) {
			final DemoItem[] items = getItems();
			for (int i = 0; i < items.length; i++) {
				DemoItem demoItem = items[i];
				if (demoItem.getId().equals(id)) {
					service.setSelectedCategory(demoItem.getCateId());
					IListboxController<DemoItem, IListitem> listboxController = service.getListboxController();
					listboxController.setModel(getSelectedModel());
					listboxController.setSelectedObject(demoItem);
					uiAgent.replaceWith(Locator.ofId("itemList"), listboxController.build());
					uiAgent.smartUpdate(Locator.ofId(id), new IListitem.Updater().selected(true).focus(true));
					setSelectedCategory(demoItem);
					uiAgent.replaceChildren(Locator.ofId("xcontents"), Immutables.createComponents(
							demoItem.getFile(), null));
					return;
				}
			}
		}
	}

	@Action(type= Events.ON_CLICK)
	public void doCategorySelect(@ActionVariable(targetId = ActionTarget.SELF, field = "id") String cateId) {
		DemoItem selectedObject = null;
		IListboxController<DemoItem, IListitem> listboxController = service.getListboxController();
		if (Objects.equals(service.getSelectedCategory(), cateId)) {
			selectedObject = listboxController.getSelectedObject();
		} else {
			service.setSelectedCategory(cateId);
		}
		listboxController.setModel(getSelectedModel());
		if (selectedObject != null) {
			listboxController.setSelectedObject(selectedObject);
		}
		UiAgent.getCurrent().replaceWith(Locator.ofId("itemList"), listboxController.build());
	}

	@Action(type=Events.ON_SELECT)
	public void doDemoSelect(SelectData data) {
		DemoItem demoItem = service.getListboxController().getModel()
				.getElementAt(data.getSelectedItems().get(0));
		setSelectedCategory(demoItem);
		UiAgent.getCurrent().replaceChildren(Locator.ofId("xcontents"), Immutables.createComponents(demoItem.getFile(), null));
	}

	@Action(type = Events.ON_CTRL_KEY)
	public void doCtrlKeySearchBox(KeyData keyData) {
		int keyCode = keyData.getKeyCode();
		IListboxController<DemoItem, IListitem> listboxController = service.getListboxController();
		ListModel<DemoItem> items = listboxController.getModel();
		if (items.getSize() == 0) return;
		DemoItem item = null;
		switch (keyCode) {
		case 38: // UP
			item = items.getElementAt(items.getSize() -1);
			listboxController.setSelectedObject(item);
			break;
		case 40: // DOWN
			item = items.getElementAt(0);
			listboxController.setSelectedObject(item);
			break;
		}
		if (item != null) {
			setSelectedCategory(item);
			UiAgent.getCurrent().replaceChildren(Locator.ofId("xcontents"),
							Immutables.createComponents(item.getFile(), null))
					.smartUpdate(Locator.ofId(item.getId()), new IListitem.Updater().focus(true));
		}
	}

	@Action(type=Events.ON_CHANGING)
	public void doChangingSearchBox(InputData data) {
		String key = data.getValue();
		LinkedList<DemoItem> item = new LinkedList<>();
		DemoItem[] items = getItems();

		IListboxController<DemoItem, IListitem> listboxController = service.getListboxController();
		if (key.trim().length() != 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getLabel().toLowerCase(java.util.Locale.ENGLISH)
						.indexOf(key.toLowerCase(java.util.Locale.ENGLISH)) != -1)
					item.add(items[i]);
			}
			listboxController.setModel(new ListModelList<>(item, true));
		} else
			listboxController.setModel(new ListModelList<>(items));
		UiAgent.getCurrent().replaceWith(Locator.ofId("itemList"), listboxController.build());
		service.setSelectedCategory(null);
	}
	private void setSelectedCategory(DemoItem item) {
		service.setSelectedCategory(item.getCateId());
		String deselect = "jq('$"+ item.getCateId() +
				"').addClass('demo-seld').siblings().removeClass('demo-seld');";
		Clients.evalJavaScript(deselect);
		Executions.getCurrent().getDesktop().setBookmark(item.getId());
	}
	private IBorderlayout initMainLayout() {
		final String id = Executions.getCurrent().getParameter("id");
		IListboxController<DemoItem, IListitem> iListboxController = initListboxController();
		DemoItem selectedDemo = null;
		if (id != null) {
			final LinkedList<DemoItem> list = new LinkedList<>();
			final DemoItem[] items = getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].getId().equals(id))
					list.add(items[i]);
			}
			if (!list.isEmpty()) {
				iListboxController.setModel(new ListModelList<>(list, true));
				setSelectedCategory(selectedDemo = list.getFirst());
			}
		}

		if (selectedDemo == null) {
			selectedDemo = iListboxController.getModel().getElementAt(0);
			setSelectedCategory(selectedDemo);
			((Selectable)iListboxController.getModel()).addToSelection(selectedDemo);
		}
		String currentTheme = org.zkoss.zul.theme.Themes.getCurrentTheme();
		return IBorderlayout.ofId("main").withSclass(currentTheme).withNorth(
				INorth.DEFAULT.withBorder(ILayoutRegion.Border.NONE)
						.withSize("100px").withSclass("demo-header")
						.withCollapsible(true).withChild(
								IDiv.of(IDiv.DEFAULT.withSclass("demo-logo pointer")
										.withChildren(IA.DEFAULT.withHref(
												"https://www.zkoss.org").withChildren(
												IImage.ofSize("90px", "80px")
														.withSrc("/img/ZK-Logo.gif"))),
										CATEGORY_BAR.withZclass("demo-categorybar").withId("header").withChildren(
												Arrays.stream(getCategories()).map(
														category -> CATEGORY.withZclass("demo-category").withId(
																category.getId()).withLabel(category.getLabel())
																.withImage(category.getIcon()).withAction(this::doCategorySelect)
												).collect(Collectors.toList())
										)
								)
		)).withWest(IWest.DEFAULT.withTitle("ZK " + WebApps.getCurrent().getVersion()).withSize("250px")
				.withSplittable(true).withMinsize(210).withMaxsize(500).withCollapsible(true)
				.withChild(IPanel.ofVflex("1").withTopToolbar(
						IToolbar.of(
							ILabel.of("Search:"),
							ITextbox.ofId("searchBox").withCtrlKeys("#down#up")
									.withSclass("demo-search-inp").withFocus(true)
									.withActions(ActionHandler.of(this::doChangingSearchBox),
											ActionHandler.of(this::doCtrlKeySearchBox))
						))
						.withPanelchildren(
							IPanelchildren.of(
									iListboxController.build()
							)
		)))
		.withCenter(ICenter.of(IDiv.ofId("xcontents").withStyle("padding:3px").withVflex("1").withChildren(
				Immutables.createComponents(selectedDemo.getFile(), null)
		)));
	}

	private IListboxController initListboxController() {
		IListboxController<DemoItem, IListitem> itemList = IListboxController.of(
				IListbox.ofId("itemList").withOddRowSclass("non-odd")
						.withSclass("demo-items").withAction(this::doDemoSelect),
				getSelectedModel(),
				(DemoItem di, Integer index) -> IListitem.ofId(di.getId())
						.withChildren(IListcell.of(di.getLabel(), di.getIcon())
								.withHeight("30px")));
		service.setListboxController(itemList);
		return itemList;
	}
}
