<#import "DataGridColumn.ftl" as C>

<#macro DataGrid d>
${""?left_pad(d.depth*2)}<${d.type} id="${d.id}"<#if d.label??> label="${d.label}"</#if><#if d.caption??> caption="${d.caption}"</#if>>
${""?left_pad(d.depth*2)}  <thead className="tableHeader"><tr>
<#list d.sortedChildren as child>
${""?left_pad(d.depth*2)}    <DataGridHeader<#if child.label??> label="${child.label}"</#if>/>
</#list>
${""?left_pad(d.depth*2)}  </tr></thead>
${""?left_pad(d.depth*2)}  <DgBody<#if d.binding??> binding="${d.binding}"</#if><#if d.selectableRow == true> selectableRow="true"</#if> handleSubmit={this.props.handleSubmit} createBody={(${d.entityName}, ${d.entityName}Row, rowIndex, ie, injected) => (
${""?left_pad(d.depth*2)}    <tr key={rowIndex} className={(injected ? 'injected' : '')}<#if d.selectableRow == true> onClick={(e) => ie('${d.binding!}', rowIndex, e)}</#if>>
<#list d.sortedChildren as child>
<@C.DataGridColumn c=child/>
</#list>
${""?left_pad(d.depth*2)}    </tr>
${""?left_pad(d.depth*2)}  )}/>
${""?left_pad(d.depth*2)}</${d.type}>
</#macro>