/* SPDX-License-Identifier: MIT */
/* Copyright (c) 2016-2019 Linutronix GmbH */

var dataUrl;
var cur_kernelbuild;

var heading_data;
var heading;
var head;
var commit;

function embedHeading(kernelbuild) {
    cur_kernelbuild = kernelbuild;

    var opts = {sendMethod: 'auto'};
    var query = new google.visualization.Query(dataUrl.concat ("overview"), opts);

    query.setQuery('select * where kernelbuild=' + safe_id(cur_kernelbuild));
    query.send(drawHeading);
}

function drawHeading(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    heading_data = response.getDataTable();
    
    var time = heading_data.getFormattedValue(0, 2);
    var branch = heading_data.getFormattedValue(0, 1);
    commit = heading_data.getFormattedValue(0, 3);
    var name = heading_data.getFormattedValue(0, 4);
    var git_id = heading_data.getFormattedValue(0, 7);
    heading = document.getElementById('p_header');

    if (heading_data.getFormattedValue(0, 9) == 'true') {
	heading.innerHTML = time+ ' <br> Branch: ' +branch+ ' <br> Tag: ' +name;
	var opts = {sendMethod: 'auto'};
	var query_git = new google.visualization.Query(dataUrl.concat ("git_view"), opts);

	query_git.setQuery('select httprepo, path where id=' + safe_id(git_id));
	query_git.send(makeGitLink);
    } else {
	head = commit.substring(0,12);
	heading.innerHTML = time+ ' <br> Branch: ' +branch+ ' <br> Tag: ' +name+' <br> Head: '+head;
    }
}

function makeGitLink(response) {
    if (response.isError()) {
	alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
	return;
    }

    var git_data = response.getDataTable();

    var httppath = git_data.getFormattedValue(0, 0);
    var gitpath = git_data.getFormattedValue(0, 1);

    head = '<a target= "_blank" href="'+httppath+'/commit/?id='+commit+'">' +commit.substring(0,12)+'</a> <br> Git: <a target= "_blank" href="'+gitpath+'"> '+gitpath+' </a>';
    heading.innerHTML = heading.innerHTML+'<br> Head: '+head;
}
