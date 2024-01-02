<#macro TabbedPanel t>
${""?left_pad(t.depth*2)}<phi-tabbedpanel #${t.id}<#if t.binding??> [binding]="${t.binding}?.entities" conversationName="${t.entityName}"</#if><#if t.jollyParameter??> ${t.jollyParameter}</#if>>
<#if !t.binding??>${""?left_pad(t.depth*2)}  <ul class="tab-nav"><#list t.sortedChildren as child>
${""?left_pad(t.depth*2)}    <li<#if child.tabRenderCondition??> *ngIf="${child.tabRenderCondition}"</#if>>
${""?left_pad(t.depth*2)}      <a class="button" [class.activetab]="${t.id}.selectedTabIndex == ${child?index}" (click)="${t.id}.ie(entity, ${child?index})">{{'${child.id}' | translate}}</a>
${""?left_pad(t.depth*2)}    </li></#list>
${""?left_pad(t.depth*2)}  </ul>
</#if>${""?left_pad(t.depth*2)}  <div class="tab-content">
<#list t.sortedChildren as child>
    <@L.Layout l=child/>
</#list>
${""?left_pad(t.depth*2)}  </div>
${""?left_pad(t.depth*2)}</phi-tabbedpanel>
</#macro>