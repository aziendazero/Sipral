<#macro VirtualPage v>
<#list v.pages as key, value>
${""?left_pad(v.depth*2)}<phi-${key?lower_case}<#if value??> *ngIf="${value}"</#if><#if v.jollyParameter??> ${v.jollyParameter}</#if>></phi-${key?lower_case}>
</#list>
</#macro>