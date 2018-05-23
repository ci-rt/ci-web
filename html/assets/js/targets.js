var dataUrl;
var detailUrl;
var targets;
var overview_data;

function embedCharts() {
    dataUrl = 'Data?table=';
    t_testsUrl = 't_tests.jsp?id=';
    
    google.charts.load('current', {'packages':['corechart', 'gauge', 'table']});
    google.charts.setOnLoadCallback(chartsLoaded);
}

function chartsLoaded() {
    targetOverview();
    donut();
}

function targetOverview() {
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("targets"), opts);

    query.setQuery('select *');
    query.send(drawOverview);
}

function drawOverview(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    overview_data = response.getDataTable();
    var view = new google.visualization.DataView(overview_data);
    view.hideColumns([2]);


    overview_data.setColumnLabel(0, 'Label');
    overview_data.setColumnLabel(1, 'Target Description');
    var table = new google.visualization.Table(document.getElementById('target_overview'));

    targets = table;
    google.visualization.events.addListener(table, 'select', targetHandler);

    table.draw(view, {page: 'enable', width: '100%', height: '100%'});
}

function targetHandler(e) {
    var selection = targets.getSelection();
    var item = selection[0];

    var str = overview_data.getFormattedValue(item.row, 2);

    window.location.assign(t_testsUrl.concat(safe_id(str)));
}

