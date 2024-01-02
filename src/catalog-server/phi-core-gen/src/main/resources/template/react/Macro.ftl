<#macro renderChildren list>
    <#list list as child>
        <#if child.class.simpleName == "Layout">
            <@L.Layout l=child/>
        <#elseif child.class.simpleName == "DataGrid">
            <@D.DataGrid d=child/>
        <#elseif child.class.simpleName == "Widget">
            <@W.Widget w=child/>
        <#elseif child.class.simpleName == "TabbedPanel">
            <@T.TabbedPanel t=child/>
        <#elseif child.class.simpleName == "VirtualPage">
            <@V.VirtualPage v=child/>
        </#if>
</#list>
</#macro>