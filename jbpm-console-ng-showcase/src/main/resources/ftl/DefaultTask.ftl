
<h2>${task.names[0].text}</h2>
<hr>
<#if task.descriptions[0]??>
Description: ${task.descriptions[0].text}
</#if>

<form>
<input type="hidden" name="taskId" value="${task.id}"/>
<table>
    
<#list content?keys as key>
    <#assign value = content[key]>
    
    
    <tr><td>${key} : </td><td> <input type="text" name="${key}" value="${value}"/> </td></tr>
</#list>
<tr><td colspan="2" align="center">
<input type="button" name="btn_Complete" value="Complete" onClick="completeForm(getFormValues(form));"/>
</td></tr>
</table>
</form>
