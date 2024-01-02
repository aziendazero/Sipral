<#macro Layout l>
${""?left_pad(l.depth*2)}<#if l.type == "Form"><form #form="ngForm" id="${l.id}"<#if l.clazz??> class="${l.clazz}"</#if><#if l.style??> style={${l.style}}</#if>><#if l.label??>
${""?left_pad(l.depth*2)}  <h1>{{'${l.id}' | translate}}</h1></#if>
</#if><#if l.type != "Form"><div id="${l.id}"<#if l.clazz??> class="${l.clazz}<#if l.asGroupBox??> fieldset</#if>"</#if><#if l.style??> style={${l.style}}</#if><#if l.binding??> *ngFor="let ${l.entityName} of ${l.binding}<#if l.binding?ends_with("List")>?.entities</#if>"</#if><#if l.render??> render="${l.render}"</#if><#if l.renderedEl??> *ngIf="${l.renderedEl}"</#if>>
<#if l.asGroupBox?? && l.label??>${""?left_pad(l.depth*2)}<label class="fldstLabel">{{'${l.id}' | translate}}</label>
</#if>
</#if>
    <@M.renderChildren list=l.sortedChildren/>
${""?left_pad(l.depth*2)}<#if l.type == "Form"></form><#else></div></#if>
</#macro>