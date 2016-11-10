var dataUrl;
var detailUrl;
var table;
var overview_data;

function embedCharts() {
    dataUrl = "Data?table=";
    detailUrl = "kbuild.jsp?id=";
    
    google.charts.load('current', {'packages':['corechart', 'gauge', 'table', 'controls']});
    google.charts.setOnLoadCallback(chartsLoaded);
}

function chartsLoaded() {
    overview();
    donut();
}

function overview() {
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("overview"), opts);

    query.setQuery('select kernelbuild, branch, name, "timestamp", pass order by kernelbuild desc');
    query.send(drawOverview);
}

function drawOverview(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    dash = new google.visualization.Dashboard(document.getElementById('dash_builds_div'));

    var con_branch = new google.visualization.ControlWrapper({
	'controlType': 'CategoryFilter',
	'containerId': 'con_branch_div',
	'options': {
	    'filterColumnIndex': 1,
	    'ui': {
		'label': 'Branch Selection:',
		'allowTyping': false,
		'allowMultiple': true
	    }
	}
    });

    var con_tag = new google.visualization.ControlWrapper({
	'controlType': 'CategoryFilter',
	'containerId': 'con_tag_div',
	'options': {
	    'filterColumnIndex': 2,
	    'ui': {
		'label': 'Tag Selection:',
		'allowTyping': false,
		'allowMultiple': true
	    }
	}
    });
    
    var con_time = new google.visualization.ControlWrapper({
	'controlType': 'DateRangeFilter',
	'containerId': 'con_time_div',
	'options': {
	    'filterColumnIndex': 3,
	    'ui': {
		'label': 'Time Selection:'
	    }
	}
    });

    
    table = new google.visualization.ChartWrapper({
	'chartType': 'Table',
	'containerId': 'table_div',
	'options': {
	    page: 'enable',
	    width: '100%',
	    height: '100%'
	}
    });

    overview_data = response.getDataTable();
    
    overview_data.setColumnLabel(0, 'Build ID');
    overview_data.setColumnLabel(1, 'Branch');
    overview_data.setColumnLabel(2, 'Tag');
    overview_data.setColumnLabel(3, 'Timestamp (UTC)');
    overview_data.setColumnLabel(4, 'Success');

    google.visualization.events.addListener(table, 'select', overviewHandler);

    dash.bind([con_branch, con_tag, con_time], [table]);
    dash.draw(overview_data);
}

function overviewHandler(e) {
    var selection = table.getChart().getSelection();
    var item = selection[0];

    var str = overview_data.getFormattedValue(item.row, 0);

    window.location.assign(detailUrl.concat(str));

    table.getChart().setSelection();
}

