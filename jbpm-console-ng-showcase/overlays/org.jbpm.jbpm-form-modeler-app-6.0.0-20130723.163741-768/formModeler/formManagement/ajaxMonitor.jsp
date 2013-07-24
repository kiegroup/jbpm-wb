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


function disableFormChecks(form) {
var i;
for (i=0; i<form.elements.length; i++) {
if (form.elements[i].type == "checkbox") form.elements[i].disabled="disabled";
}
}

function removeUploadedFile(name, fieldName) {
try {
document.getElementById("delete_" + name).value='true';
document.getElementById("tr_delete_" + name).style.display='none';

increaseCounter(fieldName);
} catch(err){alert(err);}
return false;
}

function removeNotUploadedFile(fieldName, id) {
var tdContainer = document.getElementById("td_" + fieldName + "_container");
var input = document.getElementById(fieldName + "_" + id);
tdContainer.removeChild(input);
var showTable = document.getElementById(fieldName+"_showTable");
var i;
for (i=0; i<showTable.rows.length; i++) {
if (showTable.rows[i].id == "tr_"+fieldName+"_"+id) {
showTable.deleteRow(i);
break;
}
}

increaseCounter(fieldName);

return false;
}

function increaseCounter(fieldName) {
var counter = document.getElementById(fieldName + "_counter");

counter.value ++;

if (counter.value == 1) {
createNewFile(fieldName);
if (supportsAltText(fieldName)) showAndHideAltRelated(fieldName, "block");
}
}

function supportsAltText(fieldName) {
return document.getElementById(fieldName+"_accepts_alt_text") && document.getElementById(fieldName+"_accepts_alt_text").value == "true"
}

function setVisible(fieldName, input) {
var fileName = input.value;
var index = fileName.lastIndexOf("/");
if (index == -1) index = fileName.lastIndexOf("\\");
fileName = fileName.substring(index+1, fileName.length);
var docs = document.getElementById(fieldName + "_totalDox").value;
createShowNode(fieldName, fileName, docs);
input.style.display="none";
var addBtn = document.getElementById(fieldName+"_add");
if (addBtn != null) addBtn.disabled=true;

var counter = document.getElementById(fieldName + "_counter");

counter.value--;

if (supportsAltText(fieldName)) {
document.getElementById("tr_"+fieldName+"_alt_title").style.display="none";
var langSelector = document.getElementById(fieldName+"_lang_selector");
for (i=0; langSelector.options.length; i++) {
if (langSelector.options[i].value == '<%=LocaleManager.currentLang()%>') {
langSelector.selectedIndex = i;
break;
}
}

var langs = document.getElementById(fieldName+"_lang_selector").options;
var i;
for (i = 0; i < langs.length; i++) {
var identifier = fieldName + '_' + docs + '_alt_' + langs[i].value;
document.getElementById(identifier).style.display = "none";
}

if (counter.value == 0) {
showAndHideAltRelated(fieldName, "none");
}
}

if (counter.value != 0) createNewFile(fieldName);
}

function showAndHideAltRelated(fieldName, display) {
document.getElementById("tr_" + fieldName + "_chooseImage").style.display = display;
document.getElementById("tr_" + fieldName + "_alt_title").style.display = display;
document.getElementById("tr_" + fieldName + "_alt_selector").style.display = display;
document.getElementById("tr_" + fieldName + "_button").style.display = display;
}

function createShowNode(fieldName, fileName, oldId) {
var showTable = document.getElementById(fieldName + "_showTable");

var rows = showTable.rows.length;
var tr = showTable.insertRow(rows);
tr.id = "tr_" + fieldName + "_" + oldId;

var tdContainer = document.createElement("td");
tdContainer.style.whiteSpace="nowrap";
tdContainer.style.overflow = "hidden";

var tableContainer = document.createElement("table");
tableContainer.setAttribute("className", "skn-table_border");
tableContainer.setAttribute("class", "skn-table_border");
tableContainer.setAttribute("width", "100%");
var tableTr1 = tableContainer.insertRow(0);
var tableTr2 = tableContainer.insertRow(1);

var tdImage = document.createElement("td");
tdImage.setAttribute("width", "32");
tdImage.width = "32";
tdImage.setAttribute("rowspan", "2");
tdImage.rowSpan = "2";
var icon = document.createElement("img");
icon.setAttribute("src","<mvc:context uri="/formModeler/images/general/32x32/image_missing.png"/>");
icon.setAttribute("border","0");
icon.setAttribute("title", "<i18n:message key="toUpload">!!!toUpload</i18n:message>");
tdImage.appendChild(icon);

var tdDelete = document.createElement("td");
var image = document.createElement("img");
image.setAttribute("src","<mvc:context uri="/formModeler/images/actions/delete.png"/>");
image.setAttribute("border","0");
image.setAttribute("title", "<i18n:message key="delete">!!!Borrar</i18n:message>")
var anchor = document.createElement("a")
anchor.href = "#";
anchor.title = "<i18n:message key="delete">!!!Borrar</i18n:message>";
anchor.appendChild(image);
anchor.setAttribute("onclick", "return removeNotUploadedFile('" + fieldName + "'," + oldId + ");");
anchor.onclick = function () {
removeNotUploadedFile(fieldName, oldId);
return false;
}
tdDelete.appendChild(anchor);
tdDelete.setAttribute("width", "16");

var tdName = document.createElement("td");

var divName = document.createElement("div");
divName.style.whiteSpace = "nowrap";
divName.style.fontWeight = "bold";
divName.style.overflow = "hidden";
divName.title = fileName;
divName.appendChild(document.createTextNode(fileName));
tdName.appendChild(divName);

tableTr1.appendChild(tdImage);
tableTr1.setAttribute("valign", "bottom");
tableTr1.valign = "bottom";

if (supportsAltText(fieldName)) {
tdName.setAttribute("colspan", "2");
tableTr1.appendChild(tdName);
var tdAlt = document.createElement("td");
var divAlt = document.createElement("div");
divAlt.style.whiteSpace = "nowrap";
divAlt.style.overflow = "hidden";
divAlt.title = document.getElementById(fieldName + "_" + oldId + "_alt_<%=LocaleManager.currentLang()%>").value;
divAlt.appendChild(document.createTextNode(document.getElementById(fieldName + "_" + oldId + "_alt_<%=LocaleManager.currentLang()%>").value));
tdAlt.appendChild(divAlt);
tableTr2.appendChild(tdAlt);
} else {
tableTr2.setAttribute("valign", "bottom");
tableTr2.appendChild(tdName);
}

tableTr2.appendChild(tdDelete);
tableTr2.setAttribute("valign", "bottom");
tableTr2.valign = "bottom";

tdContainer.appendChild(tableContainer);
tr.appendChild(tdContainer);
}

function createNewFile(fieldName) {
var docs = document.getElementById(fieldName + "_totalDox");
var oldId = docs.value;

var doc = document.getElementById(fieldName + "_" + oldId);
if (doc == null || doc.value != "") {
docs.value++;
var newId = docs.value;
var theTd = document.getElementById("td_" + fieldName + "_container");
var newInput = document.getElementById(fieldName + "_notValid").cloneNode(true);
newInput.id = fieldName + "_" + newId;
newInput.name = fieldName + "_" + newId;
newInput.style.display="block";
theTd.appendChild(newInput);
if (supportsAltText(fieldName)) {
document.getElementById("tr_"+fieldName+"_alt_title").style.display="";
theTd = document.getElementById("td_" + fieldName + "_alt_container");

var langSelector = document.getElementById(fieldName + "_lang_selector");
var langs = langSelector.options;
var selectedLang = langs[langSelector.selectedIndex].value;
var i;
for (i = 0; i < langs.length; i++) {
var identifier = fieldName + '_' + newId + '_alt_' + langs[i].value;
var altInput;
if (document.all) {
altInput = document.createElement('<input type="text" name="' + identifier + '" id="' + identifier + '"/>');
altInput.setAttribute('className', 'skn-input')
} else {
altInput = document.createElement('input');
altInput.setAttribute('name', identifier);
altInput.setAttribute('id', identifier);
altInput.setAttribute('class','skn-input');
}
if (langs[i].value != selectedLang) {
altInput.style.display = 'none';
}
theTd.appendChild(altInput);
}
}
}
}

function showErrorMessages(id, show, start, end) {
if (start && end) {
var displayMode;
var linkShow;
var linkHide;

if (show) {
displayMode = "";
linkShow = "none";
linkHide = "";
} else {
displayMode = "none";
linkShow = "";
linkHide = "none";
}
var i;
for (i=start; i<end; i++) {
document.getElementById("tr_"+id+"_"+i).style.display = displayMode;
}
document.getElementById("link_"+id+"_show").style.display = linkShow;
document.getElementById("link_"+id+"_hide").style.display = linkHide;
}
}
