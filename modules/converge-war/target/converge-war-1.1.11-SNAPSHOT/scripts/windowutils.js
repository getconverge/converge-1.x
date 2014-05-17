function openDialogue(url, windowName, width, height){
    var top=((screen.height-(screen.height/1.618))-(height/2));
    var left=((screen.width-width)/2);
    whatever = window.open(url, windowName, "width=" + width + ",height=" + height + ",left="+left+",top="+top+",status=0, menubar=no, toolbar=no, resizable=yes, scrollbars=yes");
    whatever.focus();
}

function openInOpener(url) {
    window.opener.document.location.href=url;
    window.close();
}

function openLocalInOpener(url) {
    window.opener.document.location.href='/ecms/' + url;
    window.close();
}

function promptUser(saveField, question, defaultValue) {
    var field = document.getElementById(saveField);
    var promptValue  = prompt(question,defaultValue); 
    
    if (promptValue != null) {
        field.value = promptValue;
    }
    else {
        return false;
    }
}
