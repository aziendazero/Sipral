<#macro Widget w>
${""?left_pad(w.depth*2)}<${w.type}<#if w.binding??><#if w.dgBodyEntityName??> binding={${w.reduxFormBinding}}<#else> binding="${w.binding}"</#if></#if><#if w.bindingHigh??> bindingHigh="${w.bindingHigh}"</#if><#if w.id??> id="${w.id}"</#if><#if w.mnemonicName??> mnemonicName="${w.mnemonicName}"</#if><#if w.widgetLabel??> widgetLabel="${w.widgetLabel}"</#if><#if w.x??> x="${w.x?c}"</#if><#if w.y??> y="${w.y?c}"</#if><#if w.width??> width="${w.width?c}"</#if><#if w.height??> height="${w.height?c}"</#if><#if w.styleClass??> styleClass=<#if w.styleClass?contains("'")>{${w.styleClass}}<#else/>"${w.styleClass}"</#if></#if><#if w.tooltip??> tooltip="${w.tooltip}"</#if><#if w.domain??> domain="${w.domain}"</#if><#if w.listElementsExpression??> listElementsExpression="${w.listElementsExpression}"</#if><#if w.customCode??> customCode={() => {${w.customCode}}}</#if><#if w.render??> render="${w.render}"</#if><#if w.renderedEl??> renderedEl={() => (${w.renderedEl})}</#if><#if w.dateTimePatternLength??> dateTimePatternLength="${w.dateTimePatternLength}"</#if><#if w.type=="Link" || w.type?starts_with("Button")><#if w.dgBodyEntityName??> inject={${w.dgBodyEntityName}}</#if> handleSubmit={this.props.handleSubmit}</#if><#if !w.value?has_content>/</#if>><#if w.value?has_content>${w.value}</${w.type}></#if>
</#macro>