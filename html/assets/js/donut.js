var dataUrl;
var detailUrl;

function donut() {
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("donut"), opts);
    query.setQuery('select *');
    query.send(drawDonut);
}

function drawDonut(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    donut_data = response.getDataTable();
    donut_data.setColumnLabel(0, "Tests");
    var options = {
	title: '',
	hAxis: {title: 'Kernelbuild',  titleTextStyle: {color: '#333'}, format:'#'},
	vAxis: {minValue: 0},
	colors: ['red', 'green'],
	areaOpacity: 1,
	legend: {position: 'top', textStyle: {color: '#333', fontSize: 10}}
    };

    donut_data.setColumnLabel(1, '# Build Configurations');
    donut_data.setColumnLabel(2, 'Build Pass');
    
    var chart = new google.visualization.AreaChart(document.getElementById('donut'));
    //var chart = new google.visualization.SteppedAreaChart(document.getElementById('donut'));
    chart.draw(donut_data, options);
}
