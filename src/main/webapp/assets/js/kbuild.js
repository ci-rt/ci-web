/* SPDX-License-Identifier: MIT */
/* Copyright (c) 2016-2019 Linutronix GmbH */

var dataUrl;
var kbdetailUrl;
var detail;
var detail_data;
var cur_kernelbuild;

function embedCharts(kernelbuild) {
    dataUrl = "Data?table=";
    kbdetailUrl = "kb_details.jsp?id=";
    
    cur_kernelbuild = kernelbuild;
    
    google.charts.load('current', {'packages':['corechart', 'gauge', 'table']});
    google.charts.setOnLoadCallback(overview);
}

function overview() {
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("kbuild"), opts);

    query.setQuery('select * where kernelbuild=' + safe_id(cur_kernelbuild));
    query.send(drawOverview);

    donut();
    embedHeading(cur_kernelbuild);
 
}

function drawOverview(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    detail_data = response.getDataTable();
    var view = new google.visualization.DataView(detail_data);
    view.hideColumns([0,7,8,9,10]);

    detail_data.setColumnLabel(1, 'Architecture');
    detail_data.setColumnLabel(2, 'Kernel Configuration');
    detail_data.setColumnLabel(3, 'with Overlay');
    detail_data.setColumnLabel(4, 'buildtest');
    detail_data.setColumnLabel(5, 'boottest');
    detail_data.setColumnLabel(6, 'cyclictest');
    detail_data.setColumnLabel(11, 'Target Type');

    var table = new google.visualization.Table(document.getElementById('detail'));
    
    detail = table;
    google.visualization.events.addListener(detail, 'select', detailHandler);

    table.draw(view, {page: 'enable', width: '100%', height: '100%'});

}

function detailHandler(e) {
    var selection = detail.getSelection();
    var item = selection[0];
    var type;

    if(!item)
	return;

    var formatter = new google.visualization.NumberFormat(
	{fractionDigits: 0, groupingSymbol: ''});
    formatter.format(detail_data, 7);
    formatter.format(detail_data, 8);
    
    var buildid = detail_data.getFormattedValue(item.row, 7);
    var bootid = detail_data.getFormattedValue(item.row, 8);

    if (!bootid)
	bootid = 0;

    window.location.assign(kbdetailUrl.concat(cur_kernelbuild,
					      '&b_id=', safe_id(buildid),
					      '&bt_id=', safe_id(bootid)));
}

