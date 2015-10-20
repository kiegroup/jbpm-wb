<%--

    Copyright (C) 2012 JBoss Inc

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="org.jbpm.formModeler.service.bb.mvc.components.FactoryURL"%>
<%@ page import="org.jbpm.formModeler.service.LocaleManager" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n"%>
<i18n:bundle baseName="org.jbpm.formModeler.service.mvc.messages" locale="<%= LocaleManager.currentLocale() %>"/>

// JBoss Inc. All rights reserved.

// Boundary for multipart forms.  DO NOT CHANGE IT !!!
var boundary = "JBOSS_AJAX_Boundary_" + new Date().getMilliseconds() * new Date().getMilliseconds() * new Date().getMilliseconds();
var ajaxAlertsEnabled = false;
var ajaxRequestNumber = 0;
var ajaxMaxRequestNumber = <%=request.getParameter("maxAjaxRequestAllowed") != null ? request.getParameter("maxAjaxRequestAllowed") : "50"%>;

var IE = false;
var NS = false;
var FX = false;
var OP = false;
var CH = false;
var DHTML_support = false;
var navigatorVersion = 0;
checkBrowser();

function checkBrowser() {
    var userAgent = navigator.userAgent;
    if (userAgent.indexOf('Netscape') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('Netscape') + 9, userAgent.length));
        NS = true;
    } else if (userAgent.indexOf('MSIE') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('MSIE') + 4, userAgent.length));
        IE = true;
        DHTML_support = navigatorVersion >= 5;
    } else if (userAgent.indexOf('Firefox') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('Firefox') + 1, userAgent.length));
        FX = true;
    } else if (userAgent.indexOf('Opera') != -1) {
        navigatorVersion = parseFloat(userAgent.substring(userAgent.indexOf('Opera') + 1, userAgent.length));
        OP = true;
    } else if (userAgent.indexOf('Chrome') != -1) {
        var startPos = userAgent.indexOf('Chrome') + 1;
        var endPos = userAgent.indexOf(" ", startPos);
        navigatorVersion = parseFloat(userAgent.substring(startPos, endPos));
        CH = true;
    }
};

/**
* Loads a given url into element with id tagId. If a body is specified, it uses multipart content-type
* to POST to the url.
*/

var value;
function ajaxRequest(url, body, tagId) {
    return ajaxRequest(url, body, tagId, null, null);
};

function ajaxRequest(url, body, tagId, onAjaxRequestScript, onAjaxResponseScript) {
    var ajaxHandler = new Object();
    url = url.replace(/&amp;/g,'&');
    ajaxHandler.ajaxRequestScript = onAjaxRequestScript;
    ajaxHandler.ajaxResponseScript = onAjaxResponseScript;
    if (ajaxAlertsEnabled) alert("ajax request: " + url + "\nbody:\n" + body + "\n\non " + tagId);
    beforeAjaxRequest();
    ajaxHandler.ajaxTarget = tagId;

    // Execute the ajaxRequestScript specified by client.
    if (onAjaxRequestScript != null) eval(onAjaxRequestScript);

    if (window.XMLHttpRequest) {
        ajaxHandler.ajaxReq = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
        ajaxHandler.ajaxReq = new ActiveXObject('Microsoft.XMLHTTP');
    }

    ajaxHandler.ajaxResponse = function(){
        // Only if req shows "complete"
        var readyState, status;
        try{
            readyState = ajaxHandler.ajaxReq.readyState;
            if (readyState == 4){
                status = ajaxHandler.ajaxReq.status;}
        }
        catch(e){
        }
        if (readyState == 4) {
            // only if "OK"
            if (status == 200) {
                var targetElementId;
                if (ajaxHandler.ajaxReq.responseText.indexOf("<html>")!=-1){
                    //alert("Workspace response contains an html tag. Doing my best with "+ajaxHandler.ajaxReq.responseText);
                    //document.location.href = '<%=request.getRequestURI()%>';
                    value = ajaxHandler.ajaxReq.responseText;
                    setTimeout("document.write(value);document.close()",1);
                } else {
                    var element = document.getElementById(ajaxHandler.ajaxTarget);

                    targetElementId = element.id;
                    var newElement = document.createElement(element.tagName);
                    newElement.id = element.id;
                    //alert("Setting "+ ajaxHandler.ajaxReq.responseText);
                    newElement.innerHTML = ajaxHandler.ajaxReq.responseText;
                    if (ajaxAlertsEnabled) alert("Set " + newElement.outerHTML);

                    // remove embedded objects from the old content to avoid js errors caused by flash
                    var objs = element.getElementsByTagName("object");
                    if (objs) {
                        for(var i=0; i < objs.length; i++){
                            objs[i].parentNode.removeChild(objs[i]);
                        }
                    }

                    element.parentNode.replaceChild(newElement, element);
                    // Execute the ajaxResponseScript specified by client.
                    if (ajaxHandler.ajaxResponseScript != null) eval(ajaxHandler.ajaxResponseScript);
                    try{
                        if ( tt_Init )
                            tt_Init(); /*Evaluate tooltips*/
                    } catch(e){/*Ignore errors on tooltip evaluation*/}
                    element = null;
                    newElement=null;

                    // Parsea Script elements y los coloca en el HEAD para evitar problema de Firefox 6/7 / Chrome
                    if (FX || CH) {
                        var ob = document.getElementById(targetElementId).getElementsByTagName("script");
                        var head = document.getElementsByTagName("head")[0];
                        // pasamos los elementos SCRIPT al HEAD
                        for(var i=0; i < ob.length; i++){
                                script = document.createElement('script');
                                script.type = 'text/javascript';
                                if(ob[i].src != "" && ob[i].src != null){
                                    script.src = ob[i].src;
                                }else{
                                    script.text = ob[i].text;
                                }
                                head.appendChild(script);
                        }
                        // borramos los elementos SCRIPT del target original
                        for(var i=0; i < ob.length; i++){
                            ob[i].parentNode.removeChild(ob[i]);
                        }
                    }
                }
            }

            afterAjaxRequest();
            ajaxHandler.ajaxTarget = '';
            ajaxRequestNumber++;
        }
    }

    var ajaxLoadingDivTimeout;
    function beforeAjaxRequest(){
        ajaxLoadingDivTimeout = setTimeout('if(document.body)document.body.style.cursor = "wait";if(document.getElementById(\'ajaxLoadingDiv\')); document.getElementById(\'ajaxLoadingDiv\').style.display=\'block\'',300);
    }

    function afterAjaxRequest(){
        if(document.body)document.body.style.cursor = 'default';
        if ( ajaxLoadingDivTimeout ) clearTimeout(ajaxLoadingDivTimeout);
        if(document.getElementById('ajaxLoadingDiv')) document.getElementById('ajaxLoadingDiv').style.display='none';
    }

    var ajaxReq = ajaxHandler.ajaxReq;
    if (ajaxReq != null) {
        ajaxReq.onreadystatechange = ajaxHandler.ajaxResponse;
        if (body) {
            ajaxReq.open('POST', url, true);
            //XMLHttpRequest handles only UTF-8
            ajaxReq.setRequestHeader("Content-Type", "multipart/form-data; charset=UTF-8; boundary=" + boundary);
        } else {
            ajaxReq.open('GET', url, true);
        }
        if (ajaxAlertsEnabled)
            alert("Sending body:\n" + body);
        ajaxReq.send(body);
        if (ajaxAlertsEnabled)
            alert("Loading " + url + " into " + tagId);
        return false;
    }
    return true;
};

function getBody(element) {
    var body = '';
    if (element && element.name) {
        body += '--' + boundary + '\r\n';
        body += 'Content-Disposition: form-data; name="' + element.name + '"' + '\r\n\r\n';
        body += element.value + '\r\n';
    }
    return body;
};

/**
* Returns the body multipart representation for a form, adding an ajaxAction parameter.
*/
function getFormBody(form) {
    return getFormBody(form, true);
};

/**
* Returns the body multipart representation for a form, adding an ajaxAction parameter, depending on second parameter.
*/
function getFormBody(form, addAjaxParameter) {
    var body = '';
    for (var i = 0; i < form.length; i++) {
        field = form[i];
        if (!field.name || field.name=='')
            continue;
        if (field.type == 'checkbox' || field.type == 'radio') {
            if (field.checked)
                body += getBody(field);
        }
        else if ((field.type == 'select-one' || field.type == 'select-multiple')) {
            for (var j = 0; j < field.length; j++) {
                if (field[j].selected) {
                    value = field[j].value;
                    if (value == '') value = field[j].text;
                    body += '--' + boundary + '\r\n';
                    body += 'Content-Disposition: form-data; name="' + field.name + '"' + '\r\n\r\n';
                    body += value + '\r\n';
                }
            }
        }
        else {
            body += getBody(field);
        }
    }
    if(addAjaxParameter){
        if(ajaxAlertsEnabled)
            alert("Adding ajax parameter to form to be sent");
        body += '--' + boundary + '\r\n';
        body += 'Content-Disposition: form-data; name="ajaxAction"' + '\r\n\r\n';
        body += 'true\r\n';
    }
    else{
        if(ajaxAlertsEnabled)
            alert("Getting form body without ajax parameter: "+addAjaxParameter);
    }
    body += "--" + boundary;
    form=null;
    return body + "--";
};

var ajaxPreviousHandlers = new Object();

function setAjaxTarget(element, targetId) {
    return setAjaxTarget(element, targetId,  null,  null);
};

function submitAjaxForm(form) {
    if (form) {
        if (form.onsubmit && (ajaxRequestNumber < ajaxMaxRequestNumber)) {
            var wasAjaxed = false;
            var formClass = form.styleClass;
            if (formClass) {
                 wasAjaxed = formClass.indexOf('ajaxedElement') != -1;
            }
            if(wasAjaxed) {
                form.onsubmit();
            }
            else if (form.onsubmit()) {
                submitForm(form);
            }
        }
        else {
            submitForm(form);
        }
    }
}

function submitForm(form) {
    if (form) {
        // Double click control.
        form.onsubmit = function() {
            processDoubleClick();
            return false;
        };
        // Submit the form.
        form.submit();
    }
}

function processDoubleClick() {
	var message = "<i18n:message key="ajax.doubleClickWarn"/>";
    alert(message);
}

function sendFormToHandler(form, component, property){
    prepareFormForHandler(form, component, property);
    submitAjaxForm(form);
};

function prepareFormForHandler(form, component, property){
    setFormInputValue(form, '<%=FactoryURL.PARAMETER_BEAN%>', component );
    setFormInputValue(form, '<%=FactoryURL.PARAMETER_PROPERTY%>', property );
    setFormInputValue(form, '<%=FactoryURL.DISPATCH_ACTION%>', "_factory" );
};

function getFormInputValue( form, name ){
    for (var i = 0; i < form.length; i++) {
        var field = form[i];
        if (!field.name) continue;
        if(field.name == name){
            return field.value;
        }
    }
    return null;
};

function setFormInputValue( form, name, value ){
    for (var i = 0; i < form.length; i++) {
        var field = form[i];
        if (!field.name) continue;
        if(field.name == name){
            field.value = value;
            return;
        }
    }
    var theHidden = document.createElement('input');
    theHidden.type = 'hidden';
    theHidden.name = name;
    theHidden.value = value;
    form.appendChild(theHidden);
    form=null;
};

function setAjaxTarget(element, targetId, onAjaxRequestScript, onAjaxResponseScript) {
    var elementClass = element.styleClass;
    if ( elementClass ){
        var wasAjaxed = elementClass.indexOf('ajaxedElement') != -1;
        if ( wasAjaxed ) return;
        element.styleClass += ' ajaxedElement';
    }
    if (element.nodeName.toLowerCase() == "a") {
        var destination = element.href;
        if (element.onclick) eval('ajaxPreviousHandlers[\'' + element.id + '\']=element.onclick;');
        element.onclick = function() {

            // Double click control.
            if (element) {
                element.onclick = function() {
                    processDoubleClick();
                    return false;
                };
            }
            // Evaluate first the user defined 'onclick' function (if any).
            var clickReturn = true;
            if ( ajaxPreviousHandlers[this.id] ){
                clickReturn = ajaxPreviousHandlers[this.id]();
                //alert("There is a previous handler "+ajaxPreviousHandlers[this.id]+" that says " + clickReturn);
            }
            // Process the link.
            if (clickReturn != false) {
                // Check max consecutive ajax request.
                if (ajaxRequestNumber >= ajaxMaxRequestNumber) return true;
                eval("ret = ajaxRequest('" + destination + "&ajaxAction=true', null, '" + targetId + "', '" + onAjaxRequestScript + "', '" + onAjaxResponseScript + "')");
                element=null;
                return ret;
            } else {
                element=null;
                return false;
            }
        };
        element = null;
    }
    else if (element.nodeName.toLowerCase() == "form") {
        var containsFileInputs = false;
        if (element.elements) {
            for( elementIndex=0; elementIndex < element.elements.length; elementIndex++){
                var inputElement = element.elements[elementIndex];
                if( inputElement.type == 'file' ){
                    containsFileInputs = true;
                    break;
                }
            }
        }
        if (containsFileInputs && !isFileUploadSupported()) {
            if(ajaxAlertsEnabled)alert('Form containing file inputs cannot be set to use Ajax');
            return false;
        }
        else {
            if(ajaxAlertsEnabled)alert('Form not containing file inputs can be set to use Ajax');
        }
        if (element.onsubmit) {
            eval('ajaxPreviousHandlers[\'' + element.id + '\']=element.onsubmit;');
        }
        //alert("Putting new onsubmit for "+element.id);
        element.onsubmit = function() {

            // Double click control.
            if (element) {
                element.onsubmit = function() {
                    processDoubleClick();
                    return false;
                };
            }
            // Evaluate first the user defined 'onsubmit' function (if any).
            var clickReturn = true;
            if ( ajaxPreviousHandlers[this.id] ){
                clickReturn = ajaxPreviousHandlers[this.id]();
                //alert("There is a previous handler "+ajaxPreviousHandlers[this.id]+" that says " + clickReturn);
            }
            // Submit the form.
            if (clickReturn != false) {
                var ret = false;
                // Check max consecutive ajax request.
                if (ajaxRequestNumber >= ajaxMaxRequestNumber) return true;
                eval("ret = ajaxRequest(this.action?this.action:'Controller', getFormBody(this, true), '" + targetId + "', '" + onAjaxRequestScript + "', '" + onAjaxResponseScript + "');");
                return ret;
            } else {
                element=null;
                return false;
            }
        }
    } else {
        if (ajaxAlertsEnabled)
            alert("Unsupported element nodeName " + element.nodeName);
    }
    return true;
};

/**
* Modifies an item (form or anchor).
*/
function doSetAjax(elementId) {
    return doSetAjax(elementId, null, null);
};
/**
* Modifies an item (form or anchor),
*/
function doSetAjax(elementId, onAjaxRequestScript, onAjaxResponseScript) {
    if (window.XMLHttpRequest || window.ActiveXObject) {
        if (ajaxAlertsEnabled) alert("Looking for area enclosing " + elementId)
        var element = document.getElementById(elementId);
        if (!element) {
            if (ajaxAlertsEnabled) alert("No item with id " + elementId + " found.");
            return;
        }
        var parentElement = element.parentNode;
        while (parentElement) {
            if (parentElement && parentElement.id && (parentElement.id.indexOf("<%=HTTPSettings.AJAX_AREA_PREFFIX%>") == 0)) {
                if (ajaxAlertsEnabled) alert("Found " + parentElement.id);
                var retValue = setAjaxTarget(element, parentElement.id, onAjaxRequestScript, onAjaxResponseScript);
                element = null;
                parentElement = null;
                return retValue;
            }
            parentElement = parentElement.parentNode;
        }
        if (ajaxAlertsEnabled) {
            alert("Cannot find area envolving item with id " + elementId);
        }
        element = null;
        parentElement = null;
    }
};

function setAjax(elementId) {
    return setAjax(elementId, null, null);
};

function setAjax(elementId, onAjaxRequestScript, onAjaxResponseScript) {
    if (ajaxRequestNumber > ajaxMaxRequestNumber) return false;
    if (NS || IE || OP || FX || CH) setTimeout("doSetAjax('" + elementId + "', '" + onAjaxRequestScript + "', '" + onAjaxResponseScript + "')", 1);
};

function isFileUploadSupported() {
    return false;
};