// Removes leading whitespaces
function LTrim( value ) {
    var re = /\s*((\S+\s*)*)/;
    return value.replace(re, "$1");
}

// Removes ending whitespaces
function RTrim( value ) {
    var re = /((\s*\S+)*)\s*/;
    return value.replace(re, "$1");
}

// Removes leading and ending whitespaces
function trim( value ) {
    return LTrim(RTrim(value));
    
}

function checkCheckboxes(prefix, numbers) {    
    if (trim(numbers) != "") {
        numberArray = numbers.split(",");
        
        for (i = 0; i < numberArray.length; i++) {
            elementId = numberArray[i];
            checkCheckbox(prefix + trim(elementId));
        }
    }
}

function checkCheckbox(id) {
    var element = document.getElementById(id);
    
    if (element != null) {
        element.checked = 'true';
    }
}

function importStyleSheet(url) {    
    if(document.createStyleSheet) {
        document.createStyleSheet(url);
    }
    else {
        var styles = "@import url('" + url + "');";
        
        var newSS=document.createElement('link');
        newSS.rel='stylesheet';
        //newSS.href='data:text/css,'+escape(styles);
        newSS.href=url;
        document.getElementsByTagName("head")[0].appendChild(newSS);
    }
}
