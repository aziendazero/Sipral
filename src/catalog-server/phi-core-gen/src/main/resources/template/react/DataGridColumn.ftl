<#macro DataGridColumn c>
${""?left_pad(c.depth*2)}<DataGridColumn>
    <#list c.sortedChildren as child>
        <@W.Widget w=child/>
    </#list>
${""?left_pad(c.depth*2)}</DataGridColumn>
</#macro>