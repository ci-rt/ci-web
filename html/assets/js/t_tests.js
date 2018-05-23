var dataUrl;
var histoUrl;
var t_tests;
var data;
var data_max;

var chart;
var target_id;
var c_id;

var view;

function embedCharts(id) {
    dataUrl = 'Data?table=';
    histoUrl = 'histogram.jsp?cyclictest=';

    target_id = id;
    cnt = 0;
    
    google.charts.load('current', {'packages':['corechart', 'gauge', 'table']});
    google.charts.setOnLoadCallback(chartsLoaded);
}

function chartsLoaded() {
	embedHeading(target_id);
    targetOverview();
    donut();
}

function targetOverview() {
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("target_tests"), opts);

    query.setQuery('select * where t_id=' + safe_id(target_id));
    query.send(drawOverview);
}

function drawOverview(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    data = response.getDataTable();
    
    data.insertColumn(0, 'string', '<input type="checkbox" id="addall" onclick="choseAll()" checked/><label for="addall">&nbsp;</label>');
    data.addColumn('boolean', 'select');

    for (var nr=0; nr<data.getNumberOfRows(); nr++){
	data.setValue(nr , 0, '<input type="checkbox" id="add'+nr+'" onclick="chosen('+nr+')" /><label for="add'+nr+'">&nbsp</label>');

	data.setValue(nr, 4, data.getFormattedValue(nr, 4).substring(0,12));
	data.setValue(nr, 14, false);
    }
    
    view = new google.visualization.DataView(data);
    view.hideColumns([8,9,10,11,12,14]);
    
    //overview_data.setCell(0, 3, '<input type="checkbox" value="false">');
    //overview_data.setCell(0, 3, true);
    
    data.setColumnLabel(1, 'Cyclictest<br>ID');
    data.setColumnLabel(2, 'Branch');
    data.setColumnLabel(3, 'Tag');
    data.setColumnLabel(4, 'Commit');    
    data.setColumnLabel(5, 'Timestamp');
    data.setColumnLabel(6, 'Configuration');
    data.setColumnLabel(7, 'with Overlay');
    data.setColumnLabel(13, 'Success');
    var table = new google.visualization.Table(document.getElementById('t_tests'));

    t_tests = table;
    google.visualization.events.addListener(table, 'select', unselectRow);

    table.draw(view, {page: 'enable', width: '100%', height: '100%', 'allowHtml': true});
}

function unselectRow(e) {
    t_tests.setSelection();
}

function chosen(nr){
    var stat =  data.getFormattedValue(nr, 14);

    if (stat == 'false') {
	data.setValue(nr, 14, true);
	data.setValue(nr , 0, '<input type="checkbox" id="add'+nr+'" onclick="chosen('+nr+')" checked/><label for="add'+nr+'">&nbsp</label>');
    } else {
	data.setValue(nr, 14, false);
	data.setValue(nr , 0, '<input type="checkbox" id="add'+nr+'" onclick="chosen('+nr+')" /><label for="add'+nr+'">&nbsp</label>');
    }
}

var toggel = 0;

function choseAll(){
    if (toggel == 0) {
	for (var nr=0; nr<data.getNumberOfRows(); nr++){
	    data.setValue(nr , 0, '<input type="checkbox" id="add'+nr+'" onclick="chosen('+nr+')" checked/><label for="add'+nr+'">&nbsp</label>');
	    data.setValue(nr, 14, true);
	    data.setColumnLabel(0, '<input type="checkbox" id="addall" onclick="choseAll()" /><label for="addall">&nbsp;</label>');
	}
	toggel = 1;
    } else {
	for (var nr=0; nr<data.getNumberOfRows(); nr++){
	    data.setValue(nr , 0, '<input type="checkbox" id="add'+nr+'" onclick="chosen('+nr+')" /><label for="add'+nr+'">&nbsp</label>');
	    data.setValue(nr, 14, false);
	    data.setColumnLabel(0, '<input type="checkbox" id="addall" onclick="choseAll()" checked/><label for="addall">&nbsp;</label>');
	}

	toggel = 0;
    }
    t_tests.draw(view, {width: '100%', height: '100%', 'allowHtml': true});
}

function compMax(){
    document.getElementById('comp_hint').innerHTML = "Click on the Min/Avg/Max values of the desired cyclictest to show the corresponding histogram in a new tab.";
    
    data_max = new google.visualization.DataTable();
    data_max.addColumn('string', 'ID');
    data_max.addColumn('number', 'Min');
    data_max.addColumn('number', 'Avg');
    data_max.addColumn('number', 'Max');

    for (var j=0; j<data.getNumberOfRows(); j++){
	var sel = data.getFormattedValue(j, 14);

	if (sel == 'true') {
	    data_max.addRow([data.getFormattedValue(j, 1), data.getValue(j, 10), data.getValue(j, 11), data.getValue(j, 12)]);
	}
	
    }

    var options = {
	title: 'Comparison of Cyclictest Results',
	width: '100%',
	hAxis: {
	    title: 'Cyclictest ID'
	},
	vAxis: {
	    title: 'Latency [us]'
	}
    }
    
    chart = new google.visualization.ColumnChart(document.getElementById('max'));

    google.visualization.events.addListener(chart, 'select', detailHandler);
    
    chart.draw(data_max, options);
}

function detailHandler(e) {
    var selection = chart.getSelection();
    var item = selection[0];

    if(!item)
	return;

    var formatter = new google.visualization.NumberFormat(
	{fractionDigits: 0, groupingSymbol: ''});
    formatter.format(data_max, 0);
    c_id = data_max.getFormattedValue(item.row, 0);
    
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("target_tests"), opts);

    query.setQuery('select kernelbuild where c_id=' + safe_id(c_id));
    query.send(callHisto);
    chart.setSelection();
}

function callHisto(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    kbuild = response.getDataTable();

    var formatter = new google.visualization.NumberFormat(
	{fractionDigits: 0, groupingSymbol: ''});
    formatter.format(kbuild, 0);
    k_id = kbuild.getFormattedValue(0, 0);
    
    var win = window.open(histoUrl.concat(c_id, '&id=', k_id), '_blank');
    win.focus();

}
