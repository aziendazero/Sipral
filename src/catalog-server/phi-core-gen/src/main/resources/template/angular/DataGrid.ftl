<#import "DataGridColumn.ftl" as C>

<#macro DataGrid d>
<#if d.label??>${""?left_pad(d.depth*2)}<label>{{'${d.id}' | translate}}</label>
</#if>${""?left_pad(d.depth*2)}<div id="${d.id}"<#if d.binding??> [phi-datagrid]="${d.binding}"</#if><#if d.label??> label="${d.label}"</#if><#if d.caption??> caption="${d.caption}"</#if><#if d.styleClass??> class="${d.styleClass}"</#if><#if d.style??> style="${d.style}"</#if><#if d.renderedEl??> *ngIf="${d.renderedEl}"</#if>>
${""?left_pad(d.depth*2)}  <thead class="tableHeader">
${""?left_pad(d.depth*2)}    <tr>
<#list d.sortedChildren as child>
${""?left_pad(d.depth*2)}      <th<#if child.renderedEl??> *ngIf="${child.renderedEl}"</#if><#if child.style??> style="${child.style}"</#if><#if child.styleClass??> class="${child.styleClass}"</#if><#if child.sortable??> (click)="${child.sortable}"</#if>><#if child.label??>{{'${child.id}' | translate}}<#if child.sortable??> ${child.sortArrow}</#if></#if></th>
</#list>
${""?left_pad(d.depth*2)}    </tr>
${""?left_pad(d.depth*2)}  </thead>
${""?left_pad(d.depth*2)}  <tbody<#if d.selectableRow == true> class="selectable"</#if>>
${""?left_pad(d.depth*2)}    <tr<#if d.binding??> *ngFor="let ${d.entityName} of ${d.binding}<#if d.binding?ends_with("List")>?.entities</#if>; let i = index"</#if><#if d.selectableRow == true> (click)="ie(${d.entityName}, '${d.entityName!}')" [class.selRow]="selected(${d.entityName}, '${d.entityName!}')"</#if>>
<#list d.sortedChildren as child>
<@C.DataGridColumn c=child/>
</#list>
${""?left_pad(d.depth*2)}  </tr>
${""?left_pad(d.depth*2)}  </tbody>
${""?left_pad(d.depth*2)}</div>
</#macro>