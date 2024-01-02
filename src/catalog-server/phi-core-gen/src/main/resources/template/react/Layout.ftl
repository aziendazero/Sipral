<#macro Layout l>
${""?left_pad(l.depth*2)}<${l.type} id="${l.id}"<#if l.label??> label="${l.label}"</#if><#if l.binding??> binding="${l.binding}"</#if><#if l.orientation??> orientation="${l.orientation}"</#if><#if l.asGroupBox??> asGroupBox="${l.asGroupBox}"</#if><#if l.style??> style={${l.style}}</#if><#if l.styleClass??> styleClass="${l.styleClass}"</#if><#if l.alignment??> alignment="${l.alignment}"</#if><#if l.render??> render="${l.render}"</#if><#if l.renderedEl??> renderedEl={() => (${l.renderedEl})}</#if>>
    <@M.renderChildren list=l.sortedChildren/>
${""?left_pad(l.depth*2)}</${l.type}>
</#macro>