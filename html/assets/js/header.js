function safe_id(rawid) {
    var regex = /[.,\s]+/g;
    var id = String(rawid).replace(regex, "");

    return id;
}

function headerLinks(id) {
    var act = window.location;
    var link1 = document.getElementById('back1');

    if (act.toString().match('kb_details.jsp') || act.toString().match('histogram.jsp')) {
	link1.style.visibility = 'visible';
	link1.childNodes[1].href = 'kbuild.jsp?id=' + safe_id(id);
	link1.childNodes[1].textContent = 'Build ID ' + safe_id(id);
    }
}
