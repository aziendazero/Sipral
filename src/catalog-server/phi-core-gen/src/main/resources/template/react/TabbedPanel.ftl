<#macro TabbedPanel t>
${""?left_pad(t.depth*2)}<${t.type} id="${t.id}">
<#list t.sortedChildren as child>
${""?left_pad(t.depth*2)}  <TabbedPanel.Panel title="${child.label}">
    <@L.Layout l=child/>
${""?left_pad(t.depth*2)}  </TabbedPanel.Panel>
</#list>
${""?left_pad(t.depth*2)}</${t.type}>
</#macro>