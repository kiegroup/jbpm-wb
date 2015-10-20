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
<%@ taglib uri="mvc_taglib.tld" prefix="mvc" %>
<mvc:fragment name="outputStart">
    <table border="0" cellspacing="0" cellpadding="0"  width="<mvc:fragmentValue name="width"/>">
</mvc:fragment>
<mvc:fragment name="formHeader">
</mvc:fragment>
<mvc:fragment name="groupStart"><tr></mvc:fragment>

<mvc:fragment name="beforeInputElement">
    <td colspan="<mvc:fragmentValue name="colspan"/>" width="<mvc:fragmentValue name="width"/>%" align="left"
                    style="height:100%; " valign="top">
    <table border="0" cellspacing="0" cellpadding="0"><tr>
</mvc:fragment>

<mvc:fragment name="beforeLabel"><td valign="top" nowrap="nowrap" width="1%"></mvc:fragment>
<mvc:fragment name="afterLabel"></td></mvc:fragment>

<mvc:fragment name="lineBetweenLabelAndField"></tr><tr></mvc:fragment>

<mvc:fragment name="beforeField"><td valign="top"></mvc:fragment>
<mvc:fragment name="afterField"></td></mvc:fragment>

<mvc:fragment name="afterInputElement">
    </tr>
    </table>
    </td>
</mvc:fragment>

<mvc:fragment name="groupEnd"></tr></mvc:fragment>
<mvc:fragment name="formFooter">
    <tr style="display:none"><td>
        <input disabled type="hidden" name="<mvc:fragmentValue name="name"/>" id="<mvc:fragmentValue name="uid"/>">
        <script defer>
            setTimeout('initialFormCalculations(document.getElementById("<mvc:fragmentValue name="uid"/>"))',1);
        </script>
    </td></tr>
</mvc:fragment>
<mvc:fragment name="outputEnd">
    </table>
</mvc:fragment>
