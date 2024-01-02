<#macro DataGridColumn c>
${""?left_pad(c.depth*2)}<td<#if c.renderedEl??> *ngIf="${c.renderedEl}"</#if>>
    <#list c.sortedChildren as child>
        <@W.Widget w=child/>
    </#list>
${""?left_pad(c.depth*2)}</td>
</#macro>