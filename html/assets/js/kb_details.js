var dataUrl;
var histoUrl;
var cur_kernelbuild;

var build_data;
var cur_buildid;

var boot_data;
var cur_bootid;

var cyclic;
var cyclic_data;

var failed = "images/red.png";
var success = "images/green.png";

function embedCharts(kernelbuild, buildid, bootid) {
    headerLinks(kernelbuild);
    dataUrl = "Data?table=";
    histoUrl = "histogram.jsp?cyclictest=";

    cur_buildid = buildid;
    cur_bootid = bootid;
    cur_kernelbuild = kernelbuild;
    
    google.charts.load('current', {'packages':['corechart', 'gauge', 'table']});
    google.charts.setOnLoadCallback(overview);
}

function overview() {
    var opts = {sendMethod: 'auto'};
    var query_b = new google.visualization.Query(dataUrl.concat ("build"), opts);

    query_b.setQuery('select overlay, pass, config where id=' + safe_id(cur_buildid));
    query_b.send(drawBuild);

    if (cur_bootid != 0) {
	var query_bt = new google.visualization.Query(dataUrl.concat ("boottest_view"), opts);

	query_bt.setQuery('select target_id, cmdline, pass where id=' + safe_id(cur_bootid));
	query_bt.send(Boot);
	document.getElementById('boot_div').style.visibility = 'visible';
    }

    donut();
    embedHeading(cur_kernelbuild);
}

function drawBuild(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    build_data = response.getDataTable();

    var overlay = build_data.getFormattedValue(0, 0);
    var pass = build_data.getFormattedValue(0, 1);
    var config = build_data.getFormattedValue(0, 2);
    var status = "failed";
    var build = document.getElementById('build');
    
    build.innerHTML = config+ ' with ' +overlay+ ' overlay';

    var b_status = document.getElementById('b_status');

    if (pass == 'true') {
	b_status.src = success;
	b_status.alt = "Test succeeded";
    } else {
	b_status.src = failed;
	b_status.alt = "Test failed";
    }
    
}

function Boot(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    boot_data = response.getDataTable();
    var formatter = new google.visualization.NumberFormat(
	{fractionDigits: 0, groupingSymbol: ''});
    formatter.format(boot_data, 0);
    var target_id = boot_data.getFormattedValue(0,0);

    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("targets"), opts);

    query.setQuery('select descr where t_id=' + safe_id(target_id));
    query.send(drawBoot);
}

function drawBoot(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    var target_data = response.getDataTable();
    
    var cmdline = boot_data.getFormattedValue(0,1);
    var descr = target_data.getFormattedValue(0,0);
    var pass = boot_data.getFormattedValue(0,2);

    var boot = document.getElementById('boot');
    boot.innerHTML = 'Used Kernel Command Line: \"' +cmdline+ '\" <br><br> Target information: \"' +descr+ '\"';

    var bt_status = document.getElementById('bt_status');

    if (pass == 'true') {
	bt_status.src = success;
	bt_status.alt = "Test succeeded";
	var opts = {sendMethod: 'auto'};
	var query_cyc = new google.visualization.Query(dataUrl.concat ("cyclictest_view"), opts);

	query_cyc.setQuery('select id, load, duration, interval, min, avg, max, pass, threshold where boottest_id=' + safe_id(cur_bootid));
	query_cyc.send(drawCyclic);
    } else {
	bt_status.src = failed;
	bt_status.alt = "Test failed";
    }
}

function drawCyclic(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    cyclic_data = response.getDataTable();
    var cyc_cnt = cyclic_data.getNumberOfRows();

    if (!cyc_cnt)
	return;

    document.getElementById('cyclic_div').style.visibility = 'visible';
    
    var cyc_status = document.getElementById('cyc_status');

    var pass = true;
    for (i = 0; i < cyc_cnt; i++) {
	pass = pass && cyclic_data.getFormattedValue(i,7);
    }

    if (pass == 'true') {
	cyc_status.src = success;
	cyc_status.alt = "Test succeeded";
    } else {
	cyc_status.src = failed;
	cyc_status.alt = "Test aborted";
    }
    
    var view = new google.visualization.DataView(cyclic_data);
    view.hideColumns([0,8]);

    cyclic_data.setColumnLabel(1, 'Specified Load');
    cyclic_data.setColumnLabel(2, 'Duration [s]');
    cyclic_data.setColumnLabel(3, 'Interval [us]');
    cyclic_data.setColumnLabel(4, 'Min. Value [us]');
    cyclic_data.setColumnLabel(5, 'Avg. Value [us]');
    cyclic_data.setColumnLabel(6, 'Max. Value [us]');
    cyclic_data.setColumnLabel(7, 'Success');

    var table = new google.visualization.Table(document.getElementById('cyclic_table'));
    
    cyclic = table;
    google.visualization.events.addListener(cyclic, 'select', cyclicHandler);

    table.draw(view, {page: 'enable', width: '100%', height: '100%'});
}

function cyclicHandler(e) {
    var selection = cyclic.getSelection();
    var item = selection[0];
    var type;

    if(!item)
	return;
    
    var str = cyclic_data.getFormattedValue(item.row, 0)
    window.location.assign(histoUrl.concat(str, '&id=', safe_id(cur_kernelbuild)));
}

function downloadConfig(buildid) {
    var downloadUrl = "Kernelconfig?id=";
    window.location.assign(downloadUrl.concat(safe_id(buildid)));
}

function downloadScript(buildid) {
    var downloadUrl = "Testscript?test=build&id=";
    window.location.assign(downloadUrl.concat(safe_id(buildid)));
}
