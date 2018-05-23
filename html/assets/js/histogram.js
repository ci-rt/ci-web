var dataUrl;
var hist_data;
var cur_cyclictest;
var cur_kernelbuild;

var failed = "images/red.png";
var success = "images/green.png";

function embedCharts(cyclictest, kernelbuild) {
    dataUrl = "Data?table=";
    cur_cyclictest = cyclictest;
    cur_kernelbuild = kernelbuild;

    google.charts.load('current', {'packages':['bar', 'corechart', 'table']});
    google.charts.setOnLoadCallback(chartsLoaded);
}

function chartsLoaded() {
    histogram();
    donut();
    headerLinks(cur_kernelbuild);
    embedHeading(cur_kernelbuild);
}

function histogram() {
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("histogram_view"), opts);

    query.setQuery('select cpu, latency, count where cyclictest_id = ' + safe_id(cur_cyclictest));

    query.send(drawHistogram);

    var query_t = new google.visualization.Query(dataUrl.concat ("cyc_target"), opts);

    query_t.setQuery('select * where c_id = ' + safe_id(cur_cyclictest));

    query_t.send(drawArch);

    var query_stat = new google.visualization.Query(dataUrl.concat ("cyclictest_view"), opts);

    query_stat.setQuery('select pass, threshold where id = ' + safe_id(cur_cyclictest));

    query_stat.send(drawStat);
}

function drawStat(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    var data = response.getDataTable();

    var cyc_status = document.getElementById('hist_status');

    var pass = data.getFormattedValue(0, 0);
    if (pass == 'true') {
	cyc_status.src = success;
	cyc_status.alt = "Test succeeded";
    } else {
	cyc_status.src = failed;
	cyc_status.alt = "Test aborted";
	var threshold = data.getFormattedValue(0, 1);
	document.getElementById('hist_descr').innerHTML = "Cyclictest was aborted because the latency exceeded the defined threshold value of "+threshold+" us.";
    }
}

function drawArch(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    var data = response.getDataTable();

    var arch = document.getElementById('hist_arch');
    arch.innerHTML = data.getFormattedValue(0, 0);
}

function drawHistogram(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    var data = response.getDataTable();

    var cpus = data.getColumnRange(0).max + 1;
    var lmin = data.getColumnRange(1).min;
    var lmax = data.getColumnRange(1).max;

    /*
     * column structure of hist_data:
     * Latency, CPU_0, CPU_1, ..., CPU_N-1, CPU_N
     */
    hist_data = new google.visualization.DataTable();
    hist_data.addColumn('number', 'Latency');

    for (i = 0; i < cpus; i++)
	hist_data.addColumn('number', 'CPU ' + i);

    rows = [];
    /* Add rows with Latency up to max occuring latency */
    for (i = 0; i <= (lmax + 1); i++) {
	var row = Array(cpus + 1);
	row.fill(0, 1);
	row[0] = i;
	rows.push(row);
    }

    /* Fill CPU_X Columns with latency counter */
    for (i = 0; i < data.getNumberOfRows(); i++) {
	row = rows [data.getValue(i, 1)];
	row [data.getValue(i, 0) + 1] = data.getValue(i, 2);
    }

    hist_data.addRows(rows);

    var chart = new google.visualization.SteppedAreaChart(document.getElementById('histogram'));

    var options = {
	title: 'Latency Histogram',
	legend: { position: 'bottom' },
	height: 500,
	areaOpacity: 0,
	hAxis: {title: "Latency in microseconds"},
	vAxis: {title: "Count", scaleType: 'mirrorLog', minValue: 0, baseline: 0},
	
    };

    chart.draw(hist_data, options);
}

function downloadScript(buildid) {
    var downloadUrl = "Testscript?test=cyclic&id=";
    window.location.assign(downloadUrl.concat(safe_id(buildid)));
}
