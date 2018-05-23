var dataUrl;
var cur_target;

var heading_data;

function embedHeading(t_id) {
    cur_target = t_id;
    
    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("targets"), opts);

    query.setQuery('select * where t_id=' + safe_id(cur_target));
    query.send(drawHeading);
}

function drawHeading(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    heading_data = response.getDataTable();

    var str = heading_data.getFormattedValue(0, 1);
    
    var element = document.getElementById("t_header");
    element.innerHTML = str;
}
