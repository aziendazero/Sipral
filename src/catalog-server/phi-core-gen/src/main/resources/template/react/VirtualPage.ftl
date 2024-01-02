<#macro VirtualPage v>
${""?left_pad(v.depth*2)}<VirtualPage id="${v.id}" pages={(empty, functions) => {
<#list v.pages as key, value>
    <#if value??>
${""?left_pad(v.depth*2)}  try{if (${value}) {
${""?left_pad(v.depth*2)}    return <${key?cap_first}/>
${""?left_pad(v.depth*2)}  }} catch(e){}
    <#else>
${""?left_pad(v.depth*2)}${key?cap_first}
    </#if>
</#list>
${""?left_pad(v.depth*2)}}}/>
</#macro>