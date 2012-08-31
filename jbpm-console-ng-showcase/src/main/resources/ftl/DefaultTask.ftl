
<h2>${task.names[0].text}</h2>
<hr>
<#if task.descriptions[0]??>
Description: ${task.descriptions[0].text}
</#if>

<form>
<input type="hidden" name="taskId" value="${task.id}"/>
<#list content?keys as key>
    <#assign value = content[key]>
    
    ${key} : <input type="text" name="${key}" value="${value}"/><br/>
</#list>
<input type="button" name="btn_Complete" value="Complete" onClick="completeForm(getFormValues(form));"/>

</form>
