import { Component, Injector<#--, OnInit--> } from '@angular/core';
import { select } from '@angular-redux/store';
<#--import FunctionsBean, {isEmpty} from '${relativePathToRoot}/../app/widgets/actions/FunctionsBean.js';-->
import { BaseForm } from '${relativePathToRoot}/widgets/form/base-form';
<#list imports as imp>
import ${imp.form?cap_first} from '${relativePathToRoot}${imp.path}/${imp.form}';
</#list>
@Component({
selector: 'phi-${formName?lower_case}',
templateUrl: './${formName}.html'
})
export class ${formNameCamelCase?cap_first} extends BaseForm <#--implements OnInit--> {
<#if variables?has_content>
<#list variables as v><#if v != "$event">  @select(['conversation', '${v}']) ${v}$;
  ${v};
</#if></#list>
</#if>
<#--let functions = FunctionsBean;-->
<#--let empty = isEmpty;-->
<#--let Param = this.props.param;-->

  constructor(injector: Injector) {
    super(injector);
<#if variables?has_content>
<#list variables as v><#if v != "$event">    this.${v}$.subscribe(res => this.${v} = res);
</#if></#list>
</#if>
  }
<#if methods?has_content>
<#list methods as m>    ${m}() {

    }

</#list>
</#if>
}
