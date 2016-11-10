
function headerLinks(id) {
    var act = window.location;
    var link1 = document.getElementById('back1');

    if (act.toString().match('kb_details.jsp') || act.toString().match('histogram.jsp')) {
	link1.style.visibility = 'visible';
	link1.childNodes[1].href = 'kbuild.jsp?id='+id;
	link1.childNodes[1].textContent = 'Build ID '+id;
    }
    
}
