<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
/* mobile device only, let arrow button bigger for better UX */
.breeze .demo-categorybar-left-scroll,
.breeze .demo-categorybar-right-scroll,
.silvertail .demo-categorybar-left-scroll,
.silvertail .demo-categorybar-right-scroll {
	border: 0;
	width: 23px;
	height: 38px;
	top: 36px;
}
.breeze .demo-categorybar-body-scroll,
.silvertail .demo-categorybar-body-scroll {
	margin-left: 30px;
	margin-right: 30px;
}
.breeze .demo-categorybar-left-scroll,
.silvertail .demo-categorybar-left-scroll {
	background-image: url(${c:encodeURL('/img/tablet-arrow-left.png')});
background-position: 0 0;
}
.breeze .demo-categorybar-left-scroll:hover,
.silvertail .demo-categorybar-left-scroll:hover {
	background-position: -24px 0;
}
.breeze .demo-categorybar-right-scroll,
.silvertail .demo-categorybar-right-scroll {
	background-image: url(${c:encodeURL('/img/tablet-arrow-right.png')});
background-position: right 0;
}
.breeze .demo-categorybar-right-scroll:hover,
.silvertail .demo-categorybar-right-scroll:hover {
	background-position: 0 0;
}