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
<%@ page import="org.jbpm.formModeler.service.LocaleManager"%>
<%@ page import="org.jbpm.formModeler.core.processing.FormProcessor"%>
<%@ page import="org.jbpm.formModeler.core.processing.formRendering.FormRenderingFormatter" %>
<%@ taglib uri="factory.tld" prefix="factory" %>
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<i18n:bundle baseName="org.jbpm.formModeler.core.processing.fieldHandlers.messages" locale="<%=LocaleManager.currentLocale()%>"/>

function ismaxlength(obj) {
    var mlength = obj.getAttribute? parseInt(obj.getAttribute("maxlength")) : ""
    if (obj.getAttribute && obj.value.length > mlength)
        obj.value = obj.value.substring(0, mlength)
}

var lastFormChangedElement;
var lastFormChangeTimeout;

function initialFormCalculations(input) {
    if (input && input.id) {
        if (IE && document.readyState != 'complete') {
            setTimeout("initialFormCalculations(document.getElementById('" + input.id + "'))", 100);
            return;
        }
        processFormInputChange(input);
        input=null;
    }
}

function processFormInputChange(element) {
    if(!element || !element.id) return;
    if (lastFormChangedElement){
        var lastId = lastFormChangedElement.id;
        var thisId = element.id;
        lastId = lastId.substring(0, lastId.lastIndexOf('<%=FormProcessor.NAMESPACE_SEPARATOR%>'));
        thisId = thisId.substring(0, thisId.lastIndexOf('<%=FormProcessor.NAMESPACE_SEPARATOR%>'));
        if( lastId != thisId){
            doprocessFormInputChange(lastFormChangedElement);
        }
    }


    lastFormChangedElement = element;
    if ( lastFormChangeTimeout ) {
        clearTimeout(lastFormChangeTimeout);
    }
    lastFormChangeTimeout = setTimeout('checkChangeDDMTrigger()', 1);
}

function checkChangeDDMTrigger() {
    if (IE && document.readyState != 'complete') {
        setTimeout("checkChangeDDMTrigger()",100);
    } else {
        doprocessFormInputChange(lastFormChangedElement);
        lastFormChangeTimeout = null;
        lastFormChangedElement = null;
    }
}

function clearChangeDDMTrigger() {
    if ( lastFormChangeTimeout ) {
        clearTimeout(lastFormChangeTimeout);
    }
    lastFormChangeTimeout = null;
    lastFormChangedElement = null;
    return true;
}

function doprocessFormInputChange(element) {
    if( !element || !element.id || !document.getElementById(element.id) || !element.form || !element.name ){
        return;
    }

    if (IE && document.readyState != 'complete') {
        setTimeout("doprocessFormInputChange(document.getElementById('" + element.id + "'))", 100);
        return;
    }

    var form = element.form;
    var elementName = element.name;

    if (elementName.indexOf('.editingFormFieldId.') != -1) {
        submitAjaxForm(form);
        return;
    }

    var _backup_bean = getFormInputValue(form, '<%=FactoryURL.PARAMETER_BEAN%>');
    var _backup_prop = getFormInputValue(form, '<%=FactoryURL.PARAMETER_PROPERTY%>');
    var _backup_pAction = getFormInputValue(form, 'pAction');
    /*var _backup_FormAction = form.action; */

    prepareFormForHandler(form, 'org.jbpm.formModeler.core.processing.formProcessing.FormChangeHandler', 'process');
    setFormInputValue( form, 'modifiedFieldName', elementName );
    var formBody = getFormBody(form, false);

    setFormInputValue(form, '<%=FactoryURL.PARAMETER_BEAN%>', _backup_bean);
    setFormInputValue(form, '<%=FactoryURL.PARAMETER_PROPERTY%>', _backup_prop);
    setFormInputValue(form, 'pAction', _backup_pAction);
    /*form.action = _backup_FormAction; */

    var url = "Controller";
    var formProcessor = new Object();
    formProcessor.onresponse = function() {
    var readyState, status;
    try{
        readyState = formProcessor.formRequest.readyState;
        if (readyState == 4)
            status = formProcessor.formRequest.status;
    }
    catch(e){
    }
    if (readyState == 4) {
        // only if "OK"
        if (status == 200) {
                var xmlResponse = formProcessor.formRequest.responseXML;
                //alert("Received response "+formProcessor.formRequest.responseText);
                if (xmlResponse != null) {
                    //alert("Received XML response "+xmlResponse);
                    var setValues = xmlResponse.documentElement.getElementsByTagName('setvalue');
                    //alert("setValues="+setValues+" with size "+ (setValues?setValues.length:0) );
                    for (i = 0; i < setValues.length; i++) {
                        var fieldId = setValues[i].getAttribute("name");
                        var fieldValue = setValues[i].getAttribute("value");
                        /*alert("Putting field "+fieldId+" = "+fieldValue);*/
                        if (form.elements[fieldId]){
                            form.elements[fieldId].value = fieldValue;
                        }
                        var elms = document.getElementsByName(fieldId+'_showContainer') ;
                        if ( elms ) {
                            for ( j = 0; j < elms.length; j++ ) {
                                elms[j].innerHTML = fieldValue;
                            }
                        }
                    }
                    var setListValues = xmlResponse.documentElement.getElementsByTagName('setListValues');
                    //alert("setListValues ="+setListValues+" with size "+ (setListValues?setListValues.length:0) );
                    for (i = 0; i < setListValues.length; i++) {
                        var fieldId = setListValues[i].getAttribute("name");
                        var formField =  form.elements[fieldId];
                        if ( formField && formField.nodeName ) {
                            if ( formField && formField.nodeName.toLowerCase() == 'select') {
                                <%--var selectedValue = formField.options[formField.selectedIndex].value;--%>
                                var optionsToAdd = setListValues[i].getElementsByTagName("option");
                                if(newOptionsImplyChange(optionsToAdd, formField, formField.multiple)){
                                    <%--Leave first null element--%>
                                    if (formField.multiple) formField.options.length=0;
                                    else formField.options.length=1;
                                    if(optionsToAdd.length>0){
                                        formField.disabled = false;
                                        for(j = 0; j<optionsToAdd.length; j++){
                                            var optionToAdd = optionsToAdd[j];
                                            var option = new Option(optionToAdd.getAttribute("text"), optionToAdd.getAttribute("value") );
                                            if ( "true" == optionToAdd.getAttribute("selected") ) option.selected = true;
                                            formField.options[formField.options.length]=option;
                                            <%--if ( selectedValue ==  optionToAdd.getAttribute("value"))
                                                formField.selectedIndex = j+1;
                                            if ( "true" == optionToAdd.getAttribute("selected") )
                                                formField.selectedIndex = j+1; --%>
                                        }
                                    } else {
                                        if (formField.multiple) formField.options.length=0;
                                        else formField.options.length=1;
                                        formField.selectedIndex = 0;
                                        formField.disabled = true;
                                    }
                                }
                            }
                        }
                    }

                    var setFieldStyleValues = xmlResponse.documentElement.getElementsByTagName('setFieldStyle');
                    processElementsStyle(setFieldStyleValues);
                    var setFieldStyleValues = xmlResponse.documentElement.getElementsByTagName('setLabelStyle');
                    processElementsStyle(setFieldStyleValues);
                }
            }
        }
    }

    if (window.XMLHttpRequest) {
        formProcessor.formRequest = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
        formProcessor.formRequest = new ActiveXObject('Microsoft.XMLHTTP');
    }
    if (formProcessor.formRequest != null) {
        formProcessor.formRequest.onreadystatechange = formProcessor.onresponse;
        if (formBody) {
            formProcessor.formRequest.open('POST', url, true);
            formProcessor.formRequest.setRequestHeader("Content-Type", "multipart/form-data; charset=UTF-8; boundary=" + boundary);
        } else {
            formProcessor.formRequest.open('GET', url, true);
        }
        if (ajaxAlertsEnabled){
            alert("Sending to: "+url);
            alert("Sending body:\n" + formBody);
        }
        formProcessor.formRequest.send(formBody);
        return false;
    }
}


function newOptionsImplyChange(optionsToAdd, formField, multiple) {
    var offset = multiple ? 0 : 1;
    if (formField.options.length < optionsToAdd.length) return true;
    if (formField.options.length - offset > optionsToAdd.length) return true;
    if (formField.disabled) return true;
    for(j = 0; j<optionsToAdd.length; j++) {
        var optionToAdd = optionsToAdd[j];
        if ( !formField.options[j+offset] ) {
            return true;
        }
        if(formField.options[j+offset].value != optionToAdd.getAttribute("value")) {
            //alert("Form field value="+formField.options[j+offset].value+", instruction.value="+optionToAdd.getAttribute("value"));
            return true;
        }
        if(formField.options[j+offset].text != optionToAdd.getAttribute("text")) {
            //alert("Form field text="+formField.options[j+offset].text+", instruction.text="+optionToAdd.getAttribute("text"));
            return true;
        }
        var optionToAddSelected = ("true" == optionToAdd.getAttribute("selected") );
        if(optionToAddSelected != formField.options[j+offset].selected ) return true;
    }
    //alert("No need to modify this select... received options are the same.");
}

function processElementsStyle(setFieldStyleValues) {
    //alert("setFieldStyleValues="+setFieldStyleValues+" with size "+ (setFieldStyleValues?setFieldStyleValues.length:0) );
    for (var i = 0; i < setFieldStyleValues.length; i++) {
        var fieldId = setFieldStyleValues[i].getAttribute("name");
        var fieldValue = setFieldStyleValues[i].getAttribute("value");
        //alert("Putting field style for  "+fieldId+" = "+fieldValue);

        var container = document.getElementById(fieldId+"_container");
        if (container) {
            container.style.cssText = fieldValue;
            if ( container.style.display=='none' ) {
               container.style.cssText = 'display:none';
            } else {
               container.style.cssText = '<%=FormRenderingFormatter.FIELD_CONTAINER_STYLE%>';
            }
            propagateStyleToChildElements(container, fieldValue);
        } else {
            //alert("No container for "+fieldId);
        }
    }
}

function propagateStyleToChildElements(container, style) {
    //alert ("Propagating "+style+" to childElements in "+container);
    var allElements = container.childNodes;
    for (var j=0 ; j < allElements.length; j++) {
        var elm = allElements[j];
        if (elm && elm.className && elm.className.indexOf('dynInputStyle')==0){
            elm.style.cssText = style;
            //alert("Style applied to "+elm);
        }
        if (!elm.id || (elm.id && elm.id.indexOf("_container")==-1)) {
            propagateStyleToChildElements(elm, style);
        }
    }
}

function getDefinedCssClasses(){
    var result = new Array();
    if (document.styleSheets) {
        for (var i=0; i<document.styleSheets.length; i++) {
          var styleSheet=document.styleSheets[i];
            var rules;
            if (styleSheet.cssRules) {
              rules = styleSheet.cssRules;
            } else {
              rules = styleSheet.rules;
            }
           for(j=0; j<rules.length; j++){
               cssRule = rules[j];
               if(cssRule.selectorText.indexOf('.')==0 && cssRule.selectorText.indexOf(':')==-1 )
                  result.push(cssRule.selectorText.substring(1));
           }
        }
    }
    return result;
}
