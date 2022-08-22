var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));

var gafn = function () {
	if (window._gat) {
		gafn = null;
		try {
			var pageTracker = _gat._getTracker("UA-121377-3");
			pageTracker._setDomainName("zkoss.org");
			pageTracker._initData();
			pageTracker._trackPageview();

			zk.override(zAu, "beforeSend", function (uri, req) {
				try {
					var t = req.target;
					if (t && t.id && (!req.opts || !req.opts.ignorable)) {
						var data = req.data||{},
							value = data.items && data.items[0]?data.items[0].id:data.value;
						pageTracker._trackPageview(uri +"/" + t.id + "/" + req.name + (value?"/"+value:""));
					}
				} catch (e) {
				}
				return zAu.$beforeSend(uri, req);
			});
		} catch (e) {
		}
	} else
		setTimeout(gafn, 1000);
};
gafn();