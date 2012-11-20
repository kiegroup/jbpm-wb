
        <div class="form-content">
            
            <input type="hidden" name="taskId" value="${task.id}"/>
            <table>
                <#if inputs?size != 0>
                    <tr><td colspan="2"><br/><br/>Inputs</td></tr>
                </#if>    
                <#list inputs?keys as key>
                    <#assign value = inputs[key]>
                    
                     
                        <tr><td>${key} : </td><td> <input type="text" name="${key}" value="${value}"/> </td></tr>
                    
                </#list>
                <#if outputs?size != 0>
                    <tr><td colspan="2"><br/><br/>Outputs</td></tr>
                </#if>
                
                <#list outputs?keys as key>
                    <#assign value = outputs[key]>
                    <#if task.taskData.status = 'Reserved'>
                        <tr><td>${value} : </td><td>  </td></tr>
                    </#if>
                    <#if task.taskData.status = 'InProgress'>
                        <tr><td>${value} : </td><td> <input type="text" name="${value}" value=""/> </td></tr>
                    </#if>
                </#list>

            </table>
            
           
              
        </div>

        <div class="form-row submit clearfix">
            <#if task.taskData.status = 'Reserved'>
                <input type="button" class="button main" name="btn_Start" value="Start" onClick="startTask(getFormValues(form));"/>
            </#if>
            <#if task.taskData.status = 'InProgress'>
              <input type="button" class="button main" name="btn_Complete" value="Complete" onClick="completeTask(getFormValues(form));"/>
              <input type="button" class="button main" name="btn_Save" value="Save" onClick="saveTaskState(getFormValues(form));"/>
            </#if>  
        </div>
      
    